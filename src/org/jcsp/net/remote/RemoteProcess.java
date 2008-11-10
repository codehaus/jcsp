//////////////////////////////////////////////////////////////////////
//                                                                  //
//  JCSP ("CSP for Java") Libraries                                 //
//  Copyright (C) 1996-2008 Peter Welch and Paul Austin.            //
//                2001-2004 Quickstone Technologies Limited.        //
//                                                                  //
//  This library is free software; you can redistribute it and/or   //
//  modify it under the terms of the GNU Lesser General Public      //
//  License as published by the Free Software Foundation; either    //
//  version 2.1 of the License, or (at your option) any later       //
//  version.                                                        //
//                                                                  //
//  This library is distributed in the hope that it will be         //
//  useful, but WITHOUT ANY WARRANTY; without even the implied      //
//  warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR         //
//  PURPOSE. See the GNU Lesser General Public License for more     //
//  details.                                                        //
//                                                                  //
//  You should have received a copy of the GNU Lesser General       //
//  Public License along with this library; if not, write to the    //
//  Free Software Foundation, Inc., 59 Temple Place, Suite 330,     //
//  Boston, MA 02111-1307, USA.                                     //
//                                                                  //
//  Author contact: P.H.Welch@kent.ac.uk                             //
//                                                                  //
//                                                                  //
//////////////////////////////////////////////////////////////////////

package org.jcsp.net.remote;

import org.jcsp.lang.*;
import org.jcsp.net.*;

/**
 * <p>A proxy process that runs locally while another process executes on a different node. An instance
 * is created with a <code>CSProcess</code> and an address of a remote node to execute the process on.
 * This proxy is itself a <code>CSProcess</code> so can be used in a PAR construct. When
 * <code>run</code> it will trigger the remote process. The local <code>run</code> will not return until
 * the remote process has terminated.</p>
 *
 * <p>A factory can be specified to control how the remote process initializes its <code>Node</code>.
 * The factory used to initialize the local node should be passed to the remote node so that it will
 * use the same CNS and configure other services in the same manner. For example, to initialize from
 * the same XML file:</p>
 *
 * <pre>
 *   NodeFactory nf = new XMLNodeFactory ("config.xml");
 *   try {
 *     Node.getInstance ().init (nf);
 *   } catch (NodeNotInitializedException e) {
 *     System.exit (1);
 *   }
 *   NodeAddressID slaveNode1 = new TCPIPAddressID ("123.45.6.78", 6000);
 *   NodeAddressID slaveNode2 = new TCPIPAddressID ("123.45.6.79", 6000);
 *   CSProcess proc1 = ...;
 *   CSProcess proc2 = ...;
 *   new Parallel (new CSProcess[] {
 *     new RemoteProcess (proc1, slaveNode1, nf),
 *     new RemoteProcess (proc2, slaveNode2, nf)
 *   }).run ();
 * </pre>
 *
 * <p>If the local node has class files that won't be available at the remote node it can specify a
 * network location for a classpath that the remote node should use to get classes from. Dynamic class
 * loading is not currently implemented for remote spawning in this release.</p>
 *
 * <pre>
 *   NodeAddressID slaveNode1 = new TCPIPAddressID ("123.45.6.78", 6000);
 *   NodeAddressID slaveNode2 = new TCPIPAddressID ("123.45.6.79", 6000);
 *   CSProcess proc1 = ...;
 *   CSProcess proc2 = ...;
 *   new Parallel (new CSProcess[] {
 *     new RemoteProcess (proc1, slaveNode1, "\\\\FileServer\\Packages"),
 *     new RemoteProcess (proc2, slaveNode2, "\\\\FileServer\\Packages")
 *   }).run ();
 * </pre>
 *
 * <p>The remote process will be started in its own JVM and so this approach is not recommended for many
 * small processes at the same node. If a number of remote processes are required it is suggested that
 * a process responsible for creating the others be started remotely. For example:</p>
 *
 * <pre>
 *   class WorkerGroup implements CSProcess, Serializable {
 *
 *     private final int numWorkers;
 *     private final NetChannelLocation farmer, harvester;
 *
 *     public WorkerGroup (int numWorkers, NetChannelLocation farmer,
 *                           NetChannelLocation harvester) {
 *       this.numWorkers = numWorkers;
 *     }
 *
 *     public void run () {
 *       CSProcess workers[] = new CSProcess[numWorkers];
 *       for (int i = 0; i < numWorkers; i++) {
 *         workers[i] = new Worker (farmer, harvester);
 *       }
 *       new Parallel (workers).run ();
 *     }
 *   }
 *
 *   NodeAddressID slaveNode1 = new TCPIPAddressID ("123.45.6.78", 6000);
 *   NodeAddressID slaveNode2 = new TCPIPAddressID ("123.45.6.79", 6000);
 *   NetChannelInput workers2farmer    = NetChannelEnd.createNet2One ();
 *   NetChannelInput workers2harvester = NetChannelEnd.createNet2One ();
 *   One2OneChannel farmer2harvester   = Channel.one2one ();
 *   new Parallel (new CSProcess[] {
 *     new Farmer (farmer2harvester.out (), workers2farmer),
 *     new Harvester (farmer2harvester.in (), workers2harvester),
 *     new RemoteProcess (new WorkerGroup (10, workers2farmer.getChannelLocation (),
 *                                         workers2harvester.getChannelLocation ()),
 *                        slaveNode1),
 *     new RemoteProcess (new WorkerGroup (10, workers2farmer.getChannelLocation (),
 *                                         workers2harvester.getChannelLocation ()),
 *                        slaveNode2),
 *   }).run ();
 * </pre>
 *
 * <p>Using a single <code>RemoteProcess</code> for each remote node is more efficient than using
 * a <code>RemoteProcess</code> for each of the 10 worker processes at each node.</p>
 *
 * <p>If a process needs an application ID it should call the static <code>getApplicationID</code>. If
 * this is the only node running, it will allocate a new ID. If the process running was started
 * remotely then the application ID of the parent node will be returned.</p>
 *
 * @author Quickstone Technologies Limited
 */
public class RemoteProcess implements CSProcess
{
   /** The process to be started at the remote end. */
   private final CSProcess process;
   
   /** The location of the node to start the process at. */
   private final NetChannelLocation remoteNode;
   
   /** The factory to use for initializing the remote node. */
   private final NodeFactory factory;
   
   /** The classpath the remote node should use. */
   private final String classPath;
   
   /** False initialially and then true once this has been run at least once. */
   private boolean hasBeenRun = false;
   
   /** True if the previous run was okay, false if there was an error of some form. */
   private boolean statusOfLastRun = false;
   
   /** The application ID returned by <code>getApplicationID</code>. */
   static ApplicationID applicationID = null;
   
   /**
    * Returns the application ID. If the caller of this was started as a remote process then the
    * parent's application ID will be returned. If this is the first node started then a new
    * application ID is allocated.
    *
    * @return the application ID.
    */
   public static synchronized ApplicationID getApplicationID()
   {
      if (applicationID == null)
         applicationID = Node.getInstance().getNewApplicationID();
      return applicationID;
   }
   
   /**
    * Constructs a new proxy.
    *
    * @param process the process to launch remotely.
    * @param remoteNode the node to launch the process on.
    */
   public RemoteProcess(CSProcess process, NodeAddressID remoteNode)
   {
      this.process = process;
      this.remoteNode = new NetChannelLocation(remoteNode, "controlSignals");
      this.factory = null;
      this.classPath = null;
   }
   
   /**
    * Constructs a new proxy.
    *
    * @param process the process to launch remotely.
    * @param remoteNode the node to launch the process on.
    * @param nodeFactory the factory to use for initializing the remote node.
    */
   public RemoteProcess(CSProcess process, NodeAddressID remoteNode, NodeFactory factory)
   {
      this.process = process;
      this.remoteNode = new NetChannelLocation(remoteNode, "controlSignals");
      this.factory = factory;
      this.classPath = null;
   }
   
   /**
    * Constructs a new proxy.
    *
    * @param process the process to launch remotely.
    * @param remoteNode the node to launch the process on.
    * @param classPath the classpath the remote JVM should use.
    */
   public RemoteProcess(CSProcess process, NodeAddressID remoteNode, String classPath)
   {
      this.process = process;
      this.remoteNode = new NetChannelLocation(remoteNode, "controlSignals");
      this.factory = null;
      this.classPath = classPath;
   }
   
   /**
    * Constructs a new proxy.
    *
    * @param process the process to launch remotely.
    * @param remoteNode the node to launch the process on.
    * @param factory the factory to use for initializing the remote node.
    * @param classPath the classpath the remote JVM should use.
    */
   public RemoteProcess(CSProcess process, NodeAddressID remoteNode, NodeFactory factory, String classPath)
   {
      this.process = process;
      this.remoteNode = new NetChannelLocation(remoteNode, "controlSignals");
      this.factory = factory;
      this.classPath = classPath;
   }
   
   /**
    * <p>The main process body. This will communicate with the remote node and send it the information
    * needed to spawn the remote process. And output from the remote process will be put onto the
    * system output streams. If the remote process throws an exception this will rethrow the
    * same exception to the caller.</p>
    *
    * <p>If there is a problem with the remote process, such as it throwing a checked exception or
    * error, a <code>RemoteSpawnException</code> will be rethrown which contains the actual exception
    * from the process.</p>
    *
    * @throws RemoteSpawnException if there was a serious problem with the remote process.
    * @throws RemoteProcessFailedException if the remote JVM terminated with a non-zero error code.
    */
   public void run()
   {
      NetChannelOutput out = NetChannelEnd.createOne2Net(remoteNode);
      NetAltingChannelInput in = NetChannelEnd.createNet2One();
      
      Node.info.log(this, "Writing spawner message");
      out.write(new SpawnerMessage(process, in.getChannelLocation(), factory, getApplicationID(), classPath));
      out.destroyWriter();
      Node.info.log(this, "Spawner message written");
      
      Throwable eToThrow = null;
      statusOfLastRun = false;
      hasBeenRun = false;
      while (!hasBeenRun)
      {
         Integer msg = (Integer) in.read();
         if (msg == null)
            continue;
         switch (msg.intValue())
         {
            case ProcessSpawner.MSG_STDOUT :
               System.out.println((String) in.read());
               break;
            case ProcessSpawner.MSG_STDERR :
               System.err.println((String) in.read());
               break;
            case ProcessSpawner.MSG_EXCEPTION :
               statusOfLastRun = false;
               hasBeenRun = true;
               break;
            case ProcessSpawner.MSG_FAIL :
               eToThrow = (Throwable) in.read();
               statusOfLastRun = false;
               hasBeenRun = true;
               break;
            case ProcessSpawner.MSG_OK :
               statusOfLastRun = true;
               hasBeenRun = true;
               break;
         }
      }
      if (eToThrow != null)
      {
         if (eToThrow instanceof RuntimeException)
            throw (RuntimeException) eToThrow;
         else
            throw new RemoteSpawnException(eToThrow);
      }
   }
   
   /** Returns false iff the last run generated one or more exceptions. */
   public boolean lastRunStatus()
   {
      return statusOfLastRun;
   }
}