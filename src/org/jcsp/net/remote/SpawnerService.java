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

import java.io.*;
import org.jcsp.lang.*;
import org.jcsp.net.*;
import org.jcsp.net.dynamic.*;
import org.jcsp.util.filter.*;

/**
 * <p>Services requests from <code>RemoteProcess</code> proxies to start up child JVMs running the
 * actual processes.</p>
 *
 * <p>If started from the command line, it will use the XML config file specified by the first
 * command parameter. If no file is given it will try and use <code>JCSPNetSpawnerService.xml</code> to
 * initialize the local node. Alternatively it can be started programmatically and the caller must
 * take responsibility for initializing the node.</p>
 *
 * @author Quickstone Technologies Limited
 */
public class SpawnerService implements CSProcess
{
   /** The node key. */
   private final NodeKey nodeKey;
   
   /** Termination channel. */
   private final One2OneChannel terminate = Channel.one2one();
   
   /**
    * Constructs a new service.
    *
    * @param nodeKey the local node key.
    */
   public SpawnerService(NodeKey nodeKey)
   {
      this.nodeKey = nodeKey;
   }
   
   /** Runs the service. */
   public void run()
   {
      // Create an input channel to accept requests on
      AltingChannelInput in = NetChannelEnd.createNet2One("controlSignals");
      
      // Put a dynamic class loader in place
      ServiceManager mgr = Node.getInstance().getServiceManager(nodeKey);
      if (mgr != null)
      {
         Service svc = mgr.getService("dynamic_loading");
         if (svc != null)
         {
            DynamicClassLoader dcl = (DynamicClassLoader)svc;
            in = FilteredChannelEnd.createFiltered(in);
            ((FilteredChannelInput)in).addReadFilter(dcl.getChannelRxFilter());
         }
      }
     
      int unique = 0;
      
      // Service requests
      Alternative alt = new Alternative(new Guard[] { terminate.in(), in });
      while (true)
      {
         try
         {
            if (alt.priSelect() == 0)
            {
               terminate.in().read();
               return;
            }
            else
            {
               SpawnerMessage msg = (SpawnerMessage)in.read();
               if (msg != null)
               {
                  NetChannelOutput out = NetChannelEnd.createOne2Net(msg.caller);
                  new ProcessManager(new ProcessSpawner(this, msg.process, out, msg.factory, msg.applicationID, unique++, msg.classPath)).start();
               }
            }
         }
         catch (Exception e)
         {
            e.printStackTrace();
         }
      }
   }
   
   void stop()
   {
      terminate.out().write(null);
   }
   
   public static SpawnerService construct(String[] args)
   {
      NodeKey nodeKey = null;
      // Initialize the node
      try
      {
         String configFile = "JCSPNetSpawnerService.xml";
         if (args.length > 0) 
            configFile = args[0];
         nodeKey = Node.getInstance().init(new XMLNodeFactory(configFile));
      }
      catch (IOException e)
      {
         System.err.println("Error reading from config file");
         System.exit(1);
      }
      catch (NodeInitFailedException e)
      {
         System.err.println("Unable to initialize node - aborting");
         System.exit(1);
      }
      return new SpawnerService(nodeKey);
   }
   
   /**
    * Program entry point.
    *
    * @param args the command line arguments. The first one may be the name of an XML file for
    *             initializing the local node.
    */
   public static void main(String[] args)
   {
      construct(args).run();
   }
}