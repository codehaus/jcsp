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

package org.jcsp.net.cns;

import java.util.*;
import org.jcsp.lang.*;
import org.jcsp.util.*;
import org.jcsp.net.*;

/**
 * <p>
 * This class is the Channel Name Server's main server process class.
 * </p>
 * <p>
 * This class should only be instantiated at Nodes wishing to run
 * a server process.  Although this class does not need to be used
 * by clients wishing to interact with a server, it does
 * provide some convenient static methods for client code to use. There
 * are static versions of the methods provided in <code>CNSService</code>
 * and there are also static factory methods for constructing CNS
 * registered channel objects.
 * </p>
 * <p>
 * <h3>Server Installation</h3>
 * </p>
 * <p>
 * Channel Name Servers may be run either on a dedicated Node or else on
 * the same Node as one of the user Nodes. The former approach is recommended
 * for most sitations but for smaller scale use, the latter approach may
 * suffice. The service implements the
 * <code>org.jcsp.net.Service</code> interface and can be installed
 * in the same way as any other other service by using the service manager
 * ({@link org.jcsp.net.ServiceManager}). Alternatively, the
 * service provides install methods which handle the service manager
 * interaction directly.
 * </p>
 * <p>
 * The following example initializes a Node and installs a Channel Name Server.
 * It then proceeds to install a CNS client service and creates and resolves
 * a channel. The example does not proceed to do anything else but could be
 * used as the framework for an application wishing to host its own
 * Channel Name Server.
 * </p>
 * <pre>
 * import org.jcsp.lang.*;
 * import org.jcsp.net.*;
 * import org.jcsp.net.cns.*;
 * import org.jcsp.net.tcpip.*;
 *
 * import java.io.IOException;
 *
 * public class CNSInSameJVM implements CSProcess {
 *
 *   //main method for running example
 *   public static void main(String[] args) {
 *     CNSInSameJVM proc = new CNSInSameJVM();
 *     proc.run();
 *   }
 *
 *   public void run() {
 *     NodeKey key = null;
 *     NodeID localNodeID = null;
 *     try {
 *       //Initialize a Node that does not have a CNS client
 *       key = Node.getInstance().init(new XMLNodeFactory("nocns.xml"));
 *       localNodeID = Node.getInstance().getNodeID();
 *
 *       //Initialize the CNS Server Process
 *       CNS.install(key);
 *
 *       //Dedicated server code could stop here
 *
 *       //Initialize the CNS client
 *       //use the first address of the local Node as CNS address
 *       NodeAddressID cnsAddress = localNodeID.getAddresses()[0];
 *       CNSService.install(key, cnsAddress);
 *
 *       //creating Channel named "in"
 *       NetChannelInput in = CNS.createNet2One("in");
 *       //resolve the channel
 *       NetChannelOutput out = CNS.createOne2Net("in");
 *
 *       //could now use these channels for something!!
 *       //but this is only a test so will terminate
 *
 *
 *     } catch (NodeInitFailedException e) {
 *       e.printStackTrace();
 *     } catch (IOException e) {
 *       e.printStackTrace();
 *     }
 *     Node.info.log(this, "Done.");
 *   }
 *
 * }
 * </pre>
 * <p>
 * This is the contents of the nocns.xml file:
 * </p>
 * &lt;JCSP-CONFIG&gt;<br />
 *   &lt;PROTOCOLS&gt;<br />
 *     &lt;PROTOCOL id="TCPIP" name="TCP/IP" idclass="org.jcsp.net.tcpip.TCPIPProtocolID"&gt;<br />
 *     &lt;/PROTOCOL&gt;<br />
 *   &lt;/PROTOCOLS&gt;<br />
 *   &lt;ADDRESSES&gt;<br />
 *     &lt;ADDRESS protocolid="TCPIP" value="127.0.0.1:7896" unique="FALSE"&gt;<br />
 *     &lt;/ADDRESS&gt;<br />
 *   &lt;/ADDRESSES&gt;<br />
 * &lt;/JCSP-CONFIG&gt;<br />
 * <p>
 * The above code can be copied into a file named CNSInSameJVM.java and compiled
 * an run in same directory as the nocns.xml file.
 * </p>
 * <p>
 * <h3>Channel Factory Methods</h3>
 * </p>
 * <p>
 * In order to construct a <code>ChannelInput</code> object which can be
 * resolved by other users of a channel name server, a client simply needs
 * to to do this:
 * </p>
 * <pre>
 *   NetChannelInput in = CNS.createNet2One("Fred");
 * </pre>
 * <p>
 * Another process using the same channel name server can create a
 * <code>ChannelOutput</code> that will send objects to this channel
 * by do this:
 * </p>
 * <pre>
 *   NetChannelOutput out = CNS.createOne2Net("Fred");
 * </pre>
 * <p>
 * When these factory methods are called, various resources are used
 * within the JCSP infrastructure. A channel name will be registered
 * and held in the channel name server. These resources are taken for
 * the duration of the JCSP Node's runtime unless the user manually
 * frees the resources. When channel ends are constructed with these
 * factory methods, the <code>destroyChannelEnd(***)</code> methods
 * can be used to free all resources. It is only really necessary to
 * do this if channels are being created for short term use within
 * a long-lived Node.
 * </p>
 * <p>
 * This is an example "Hello World" program which contains two
 * inner classes with main methods, each of which can be run in
 * separate JVMs.
 * </p>
 * <pre>
 *  import org.jcsp.lang.*;
 *  import org.jcsp.net.*;
 *  import org.jcsp.net.cns.*;
 *
 *  public class TestCNS {
 *
 *    public static class Rx {
 *      public static void main(String[] args) {
 *    	  try {
 *    	    Node.getInstance().init();
 *    	    NetChannelInput in = CNS.createNet2One("rx.in");
 *    	    System.out.println(in.read());
 *    	    CNS.destroyChannelEnd(in);
 *    	  } catch (NodeInitFailedException e) {
 *    	    e.printStackTrace();
 *    	  }
 *      }
 *    }
 *
 *    public static class Tx {
 *      public static void main(String[] args) {
 *        try {
 *          Node.getInstance().init();
 *          NetChannelOutput out = CNS.createOne2Net("rx.in");
 *          out.write("Hello World");
 *          CNS.destroyChannelEnd(out);
 *        } catch (NodeInitFailedException e) {
 *          e.printStackTrace();
 *        }
 *      }
 *    }
 *  }
 * </pre>
 * <p>
 * This code can be compiled and then the following run at two
 * command prompts:
 * </p>
 * <p>
 *   java TestCNS$Rx
 * </p>
 * <p>
 *   java TestCNS$Tx
 * </p>
 * <p>
 * The programs will connect to a default channel name server. The
 * Rx program will create a <code>NetChannelInput</code> and wait
 * for a message on the channel. Once it has received the message,
 * it prints it, destroys its channel and then terminates.
 * The Tx program creates a <code>NetChannelOutput</code> that will
 * send to the Rx program's input channel. It sends a "Hello World"
 * message. Once this has been accepted by the Rx process, it
 * destoys its output channel and terminates.
 * </p>
 * </p>
 * <p>
 * <h3>CNS Client Methods</h3>
 * </p>
 * <p>
 * The following code functions the same as the above code but does
 * not use the CNS class' factory methods.  The code uses the CNS client
 * methods and manually registers and resolves the channel with the
 * channel name server.
 *
 * </p>
 * <pre>
 *  import org.jcsp.lang.*;
 *  import org.jcsp.net.*;
 *  import org.jcsp.net.cns.*;
 *
 *  public class TestCNS {
 *
 *    public static class Rx {
 *      public static void main(String[] args) {
 *    	  try {
 *    	    Node.getInstance().init();
 *          NetChannelInput in = NetChannelEnd.createNet2One();
 *          ChannelNameKey key = CNS.register(in, "rx.in");
 *          System.out.println(in.read());
 *          CNS.deregisterChannelName("rx.in", null, key);
 *    	  } catch (NodeInitFailedException e) {
 *    	    e.printStackTrace();
 *    	  }
 *      }
 *    }
 *
 *    public static class Tx {
 *      public static void main(String[] args) {
 *        try {
 *          Node.getInstance().init();
 *          NetChannelLocation loc = CNS.resolve("rx.in");
 *          NetChannelOutput out = NetChannelEnd.createOne2Net(loc);
 *          out.write("Hello World");
 *          out.destroyWriter();
 *        } catch (NodeInitFailedException e) {
 *          e.printStackTrace();
 *        }
 *      }
 *    }
 *  }
 * </pre>
 * <p>
 * The CNS client methods provide the programmer with greater control
 * over how the Channel Name Server is used. Interaction with the
 * server need not be performed at the same time as construction of
 * the channel. A channel can be registered with a Channel Name Server
 * at any time after construction of its input end. A channel can be resolved
 * at any time before construction of an output end. This allows one process
 * to resolve a name into a <code>NetChannelLocation</code> object and
 * then pass this object on for another process to use in constructing the
 * channel.
 *
 * The Channel Name Server will allow a channel to be registered multiple
 * times with different names and/or in different name spaces. Channel
 * implementations that make direct use of the Channel Name Server may
 * forbid this, so the behaviour of channel implemenations should be checked
 * before this is carried out
 * </p>
 *
 * @see org.jcsp.net.cns.CNSService
 * @see org.jcsp.net.ServiceManager
 * @see org.jcsp.net.Node
 *
 * @author Quickstone Technologies Limited
 */
public class CNS implements Service, CSProcess
{
   /** A public constructor which takes a <CODE>NodeKey</CODE> as a parameter which
    *  should be the key for the local Node.
    *
    * @param nodeKey a <CODE>NodeKey</CODE> object that should match
    *                 the local Node's <CODE>NodeKey</CODE>.
    * @throws IllegalStateException if the local Node has not been
    *          initialized.
    * @throws SecurityException if the supplied <CODE>NodeKey</CODE>
    *           object cannot be verified by the local Node.
    */
   public CNS(NodeKey nodeKey) throws IllegalStateException, SecurityException
   {
      if (!Node.getInstance().isInitialized())
         throw new IllegalStateException();
      if (!Node.getInstance().verifyKey(nodeKey))
         throw new SecurityException();
   }
   
   /*---------------Public constants--------------------------------------------*/
   
   public final static String CNS_DEFAULT_SERVICE_NAME = "org.jcsp.net.cns:Channel Name Server";
   
   /*---------------Package level constants-------------------------------------*/
   
   final static String CNS_CHANNEL_LABEL = "org.jcsp.net.cns.CNSLabel";
   
   /*---------------Private fields----------------------------------------------*/
   
   private final int INPUT_BUFFER_SIZE = 30;
   
   private boolean running = false;
   
   private Any2OneChannel stopChan = Channel.any2one();
   
   private Hashtable toClientChans = new Hashtable();
   
   private long channelKeyCount = 0;
   
   private One2OneChannel startedReplyChan = Channel.one2one();
   
   /**
    * The registered channel names and indexes.
    * This is a hashtable where the key is a
    * NameAndLevel object and the value is the ChannelID.
    */
   private Hashtable channels = new Hashtable();
   
   private Hashtable leasedChannelKeys = new Hashtable();
   
   /**
    * This holds NameAndLevel objects as the keys that map to the
    * associated ChannelNameKey objects.
    */
   private Hashtable registeredChannelKeys = new Hashtable();
   
   /**
    * The registered channel names and indexes.
    * This is a hashtable where the key is the
    * channel name and the value is the ChannelID.
    */
   private Hashtable pendingResolves = new Hashtable();
   
   private static CNSChannelEndManager CHAN_FACTORY = new CNSChannelEndManager();
   
   /*---------------Public Methods-from Service Interface------------------------*/
   
   /** This starts the channel name server.
    * @return <CODE>true</CODE> iff the channel name server is able to start.
    */
   public boolean start()
   {
      new ProcessManager(this).start();
      running = Boolean.TRUE.equals(startedReplyChan.in().read());
      return running;
   }
   
   /**
    * Stops the channel name server.
    * @return <CODE>true</CODE> iff the Channel Name Server has accepted the stop request.
    */
   public boolean stop()
   {
      //change this to a connection later
      if (running)
      {
         stopChan.out().write(null);
         running = false;
         return true;
      }
      return false;
   }
   
   /** This method does not need to be called for the Channel Name Server. It returns <CODE>true</CODE>.
    * @param settings A <CODE>ServiceSettings</CODE> object containing settings for the service.
    * @return <CODE>false</CODE>
    */
   public boolean init(ServiceSettings settings)
   {
      return true;
   }
   
   /** Returns whether the service is running.
    * At the moment this always returns false.
    * This will probably change in the future.
    * Early JCSP.NET applications always had the CNS process running in its own JVM.
    * @return <CODE>false</CODE>
    */
   public boolean isRunning()
   {
      return false;
   }
   /**
    * Returns a <code>null</code> reference as this method has no
    * use with this service.
    *
    * @return <code>null</code>.
    */
   public ServiceUserObject getUserObject() throws SecurityException
   {
      return null;
   }
   
   /*---------------Public Methods from CSProcess--------------------------------*/
   
   public void run()
   {
      boolean run = true;
      final int CHAN_STOP = 0;
      final int CHAN_EVENT = 1;
      final int CHAN_FROM_NET = 2;
      
      //get a channel from the Node which can be used to received
      //events notiying that a link to a particular Node has been
      //dropped.
      
      AltingChannelInput linkEvents = Node.getInstance().getLinkLostEventChannel();
      NetAltingChannelInput fromNet = null;
      try
      {
         fromNet = NetChannelEnd.createNet2One(CNS_CHANNEL_LABEL, new Buffer(INPUT_BUFFER_SIZE));
      }
      catch (DuplicateChannelLabelException e)
      {
         Node.err.log(this, "CNS unable to create a labelled channel. Another CNS service might be running.");
         run = false;
         startedReplyChan.out().write(Boolean.FALSE);
         return;
      }
      catch (Exception e)
      {
         Node.err.log(this, "Exception occurred while starting CNS");
         Node.err.log(this, e);
         run = false;
         startedReplyChan.out().write(Boolean.FALSE);
         return;
      }
      Alternative alt = new Alternative(new Guard[] { stopChan.in(), linkEvents, fromNet });
      startedReplyChan.out().write(Boolean.TRUE);
      Node.info.log(this, "CNS server starting");
      
      while (run)
      {
         switch (alt.priSelect())
         {
            case CHAN_STOP :
               run = false;
               break;
            case CHAN_EVENT :
               Object obj = linkEvents.read();
               handleLinkDropped((NodeID) obj);
               Node.info.log(this, "Received link event " + obj);
               break;
            case CHAN_FROM_NET :
               try
               {
                  Object o = fromNet.read();
                  if (o instanceof CNSMessage.LogonMessage)
                     handleLogonMessage((CNSMessage.LogonMessage) o);
                  else if (o instanceof CNSMessage.RegisterRequest)
                     handleRegisterRequest((CNSMessage.RegisterRequest) o);
                  else if (o instanceof CNSMessage.ResolveRequest)
                     handleResolveRequest((CNSMessage.ResolveRequest) o);
                  else if (o instanceof CNSMessage.LeaseRequest)
                     handleLeaseRequest((CNSMessage.LeaseRequest) o);
                  else if (o instanceof CNSMessage.DeregisterRequest)
                     handleDeregisterRequest((CNSMessage.DeregisterRequest) o);
               }
               catch (Exception e)
               {
                  Node.err.log(this, "Error caught and ignored");
                  Node.err.log(this, e);
               }
               break;
         }
      }
      //Destroy the fromNet channel so that all the
      //CNS clients are notified that the CNS has gone down
      fromNet.destroyReader();
      if (stopChan.in().pending())
         stopChan.in().read();
   }
   
   /*---------------Other Public Methods-----------------------------------------*/
   
   /*---------------Public Static Methods----------------------------------------*/
   
   /*-----------Resolving-------------------------*/
   
   public static NetChannelLocation resolve(String name)
   {
      return CNSService.staticServiceRef.resolve(name);
   }
   
   public static NetChannelLocation resolve(String name, NameAccessLevel accessLevel)
   {
      return CNSService.staticServiceRef.resolve(name, accessLevel);
   }
   
   /*-----------Registration----------------------*/
   
   public static ChannelNameKey register(Networked owner, String name)
   {
      return CNSService.staticServiceRef.register(owner, name);
   }
   
   public static ChannelNameKey register(Networked owner, String name, NameAccessLevel accessLevel)
   {
      return CNSService.staticServiceRef.register(owner, name, accessLevel);
   }
   
   public static ChannelNameKey register(Networked owner, String name, ChannelNameKey key)
   {
      return CNSService.staticServiceRef.register(owner, name, key);
   }
   
   public static ChannelNameKey register(Networked owner, String name, NameAccessLevel accessLevel, ChannelNameKey key)
   {
      return CNSService.staticServiceRef.register(owner, name, accessLevel, key);
   }
   
   public static ChannelNameKey register(NetChannelLocation ownerLocation, 
                                         String name, 
                                         NameAccessLevel accessLevel, 
                                         ChannelNameKey key)
   {
      return CNSService.staticServiceRef.register(ownerLocation, name, accessLevel, key);
   }
   
   /*-----------Leasing---------------------------*/
   public static ChannelNameKey leaseChannelName(String name, NameAccessLevel accessLevel, ChannelNameKey channelKey)
   throws ChannelNameException, NameAccessLevelException
   {
      return CNSService.staticServiceRef.leaseChannelName(name, accessLevel, channelKey);
   }
   
   /*-----------Deregistration--------------------*/
   /**
    * This deregisters a Channel name with the CNS. If the CNS Service is not
    * running, this method will block until it is running.
    * A boolean is returned to indicate whether deregistration was successful.
    * This is also returned if the link to the CNS is lost.
    *
    * @param   name    the name of the channel as a String.
    * @param   nameAccessLevel the nameAccessLevel of the channel.
    * @param   channelKey  the ChannelNameKey to use to deregister the
    *                      Channel name.
    *
    * @return a boolean indicating success.
    */
   public static boolean deregisterChannelName(String name, NameAccessLevel accessLevel, ChannelNameKey channelKey)
   {
      return CNSService.staticServiceRef.deregisterChannelName(name, accessLevel, channelKey);
   }
   
   /*---------------Private Methods----------------------------------------------*/
   
   private void handleLogonMessage(CNSMessage.LogonMessage msg)
   {
      NetChannelOutput toClient = NetChannelEnd.createOne2Net(msg.replyLocation);
      
      NodeID clientID = msg.replyLocation.getChannelNodeID();
      CNSMessage.LogonReplyMessage reply = new CNSMessage.LogonReplyMessage();
      if (clientID == null || toClientChans.containsKey(clientID))
         //not going to allow Node to logon - does not have NodeID
         //or a CNS service on that NodeID is already logged in
         reply.success = false;
      else
      {
         toClientChans.put(clientID, toClient);
         reply.success = true;
      }
      toClient.write(reply);
   }
   
   private void handleRegisterRequest(CNSMessage.RegisterRequest msg)
   {
      //get the channel to reply down
      ChannelOutput replyChan = (ChannelOutput)toClientChans.get(msg.replyLocation.getChannelNodeID());
      
      if (replyChan == null || msg.name == null || (msg.channelLocation == null))
         Node.info.log(this, "CNS server process received an invalid register request");
      else
      {
         // set up reply
         CNSMessage.RegisterReply reply = new CNSMessage.RegisterReply();
         reply.RequestIndex = msg.RequestIndex;
         
         //attempt to register the channel - key will be null if
         //registration fails
         reply.key = registerChannel(msg.name, msg.accessLevel, msg.channelLocation, msg.key);
         
         //send the reply to the user
         replyChan.write(reply);
         
         if (reply.key != null)
         {
            Node.info.log(this, msg.name + ", " + msg.accessLevel + " registered to " + msg.channelLocation);
            
            // Notify any waiting processes.
            PendingResolve toPutBack = null;
            PendingResolve ir = (PendingResolve) pendingResolves.remove(msg.name);
            while (ir != null)
            {
               PendingResolve nextIr = ir.next;
               ir.reply.channelLocation = getChannel(ir.channelName, ir.nameAccessLevel);
               if (ir.reply.channelLocation == null)
               {
                  //The new channel has the wrong nameAccessLevel
                  //for this queued request, so put request back
                  ir.next = toPutBack;
                  toPutBack = ir;
               }
               else
               {
                  ir.txReplyChannel.write(ir.reply);
                  Node.info.log(this, "Queued resolve for " + ir.channelName + "," + ir.nameAccessLevel + " was completed");
               }
               ir = nextIr;
            }
            if (toPutBack != null)
               pendingResolves.put(msg.name, toPutBack);
         }
         else
            Node.err.log(msg.name + ", " + msg.accessLevel + " could not be registered to " + 
                         msg.channelLocation + " - already registered.");
      }
      
   }
   
   private void handleResolveRequest(CNSMessage.ResolveRequest msg)
   {
      Node.info.log(this, "ResolveRequest");
      ChannelOutput replyChan = (ChannelOutput)toClientChans.get(msg.replyLocation.getChannelNodeID());
      
      if (replyChan == null || msg.name == null)
         Node.err.log(this, "CNS asked to resolve null name");
      else
      {
         // set up reply
         CNSMessage.ResolveReply reply = new CNSMessage.ResolveReply();
         reply.RequestIndex = msg.RequestIndex;
         reply.channelLocation = getChannel(msg.name, msg.accessLevel);
         reply.name = msg.name;
         reply.accessLevel = msg.accessLevel;
         
         if (reply.channelLocation != null)
         {
            Node.info.log(this, "Channel " + msg.name + " resolved by " + msg.replyLocation + " to " + reply.channelLocation);
            // Send reply
            replyChan.write(reply);
         }
         else
         {
            Node.info.log(this, "Attempted resolve of " + msg.name + " by " + msg.replyLocation + " was queued");
            // Queue request, reply when it registers.
            PendingResolve ir = new PendingResolve(reply, replyChan, msg.name, msg.accessLevel);
            ir.next = (PendingResolve) pendingResolves.put(msg.name, ir);
         }
      }
   }
   
   private void handleLeaseRequest(CNSMessage.LeaseRequest msg)
   {
      ChannelOutput replyChan = (ChannelOutput)toClientChans.get(msg.replyLocation.getChannelNodeID());
      if (replyChan == null)
         Node.err.log(this, "CNS received an invalid lease request.");
      else
      {
         CNSMessage.LeaseReply reply = new CNSMessage.LeaseReply();
         reply.RequestIndex = msg.RequestIndex;
         if (msg.name == null)
            reply.key = null;
         else
         {
            NameAccessLevel nameAccessLevel = msg.accessLevel;
            if (nameAccessLevel == null)
               nameAccessLevel = NameAccessLevel.GLOBAL_ACCESS_LEVEL;
            NameAndLevel nal = new NameAndLevel(msg.name, nameAccessLevel);
            
            //check to see the channel has been registered
            if (deregisterChannel(msg.name, nameAccessLevel, msg.key))
            {
               reply.key = new ChannelNameKey(channelKeyCount++);
               leasedChannelKeys.put(nal, reply.key);
            }
         }
         replyChan.write(reply);
      }
   }
   
   private void handleDeregisterRequest(CNSMessage.DeregisterRequest msg)
   {
      ChannelOutput replyChan = (ChannelOutput)toClientChans.get(msg.replyLocation.getChannelNodeID());
      
      if (replyChan == null)
         Node.err.log(this, "CNS received an invalid deregister request.");
      else
      {
         // set up reply
         CNSMessage.DeregisterReply reply = new CNSMessage.DeregisterReply();
         reply.RequestIndex = msg.RequestIndex;
         
         // do the request
         reply.success = deregisterChannel(msg.name, msg.accessLevel, msg.key);
         
         // Send reply
         replyChan.write(reply);
         
         if (reply.success)
            Node.info.log(this, msg.name + ", " + msg.accessLevel + " was deregistered.");
         else
            Node.info.log(this, msg.name + ", " + msg.accessLevel + " could not be deregistered");
      }
   }
   
   private void handleLinkDropped(NodeID nodeID)
   {
      Object txChannel = toClientChans.get(nodeID);
      
      // Iterating a Hashtable is a **nightmare**
      // But we're going to do it twice!
      //
      // What happens if we change the Hashtable while
      // we're enumerating is undefined, so we have to start
      // all over again.
      
      // Any pending requests?
      boolean changed = true;
      while (changed)
      {
         changed = false;
         Enumeration e = pendingResolves.keys();
         while ((!changed) && e.hasMoreElements())
         {
            String n = (String) e.nextElement();
            PendingResolve ir = (PendingResolve) pendingResolves.get(n);
            
            // Make sure we need to keep the first one.
            while ((ir != null) && (ir.txReplyChannel == txChannel))
            {
               ir = ir.next;
               changed = true; // must start again.
            }
            if (ir == null)
               pendingResolves.remove(n);
            else
            {
               if (changed)
                  pendingResolves.put(n, ir);
               
               // Unlink any others we need to remove.
               // (We can change here without starting again,
               // since this isn't actually in the Hashtable)
               while (ir.next != null)
               {
                  if (ir.next.txReplyChannel == txChannel)
                     // Unlink
                     ir.next = ir.next.next;
                  else
                     // OK, check next.
                     ir = ir.next;
               }
            }
         }
      }
      
      // Any registered channels?
      // Well, unregister them.
      changed = true;
      while (changed)
      {
         changed = false;
         Enumeration e = channels.keys();
         while ((!changed) && e.hasMoreElements())
         {
            NameAndLevel n = (NameAndLevel) e.nextElement();
            NetChannelLocation id = (NetChannelLocation) channels.get(n);
            if (id.getChannelNodeID().equals(nodeID))
            {
               channels.remove(n);
               //      "Broken link, so unregistered.");
               Node.info.log(this, "Broken link, so " + n + " unregistered.");
               changed = true; // must start again.
            }
         }
      }
   }
   
   /**
    * Register a Channel in the database.  Fails if the channel has already
    * been registered to a different location.  (Re-registering to
    * the same ChannelID is allowed) or if it has been leased and the supplied
    * key does not match the leased channel name's key.
    *
    * @param channelName The channel's name.
    * @param channelId   The channel's ChannelID.
    * @return a ChannelNameKey on successful database update, null if the name
    *         is already registered elsewhere.
    */
   private ChannelNameKey registerChannel(String channelName, NameAccessLevel nameAccessLevel, 
                                          NetChannelLocation channelLoc, ChannelNameKey channelKey)
   {
      if (channelName == null || nameAccessLevel == null)
         return null;
      NameAndLevel nal = new NameAndLevel(channelName, nameAccessLevel);
      NetChannelLocation existingID = (NetChannelLocation) channels.get(nal);
      
      //Check to see whether name has been leased, and if it has then
      //the supplied channel key must equal the leased channel name's key,
      //otherwise return null.
      if (existingID == null)
      {
         Node.info.log(this, "Reg: channel not registered - might be leased");
         ChannelNameKey leasedNameKey = (ChannelNameKey)leasedChannelKeys.get(nal);
         if (leasedNameKey != null)
         {
            Node.info.log(this, "Has been leased");
            if (!leasedNameKey.equals(channelKey))
            {
               Node.info.log(this, "Keys not equal - " + leasedNameKey + ", " + channelKey);
               return null;
            }
         }
         //key matches so remove from the leasedChannelKeys set
         leasedChannelKeys.remove(nal);
      }
      
      if ((existingID != null) && (!existingID.equals(channelLoc)))
         return null;
      else
      {
         channels.put(nal, channelLoc);
         ChannelNameKey newChannelKey = new ChannelNameKey(channelKeyCount++);
         registeredChannelKeys.put(nal, newChannelKey);
         Node.info.log(this, "Returning new key");
         return newChannelKey;
      }
   }
   
   /**
    * Get a ChannelID for a channel with a name that matches the supplied name
    * and whose name is within the supplied scope.
    *
    * @param channelName The channel's name.
    * @param nameAccessLevel   the channel's nameAccessLevel
    * @return a ChannelID if one can be found that matches the name and
    *         nameAccessLevel, otherwise returns null.
    */
   private NetChannelLocation getChannel(String channelName, NameAccessLevel nameAccessLevel)
   {
      //find most local matching channel
      NetChannelLocation toReturn = null;
      AbstractID abstractID = nameAccessLevel.getLevelAbstractID();
      while (toReturn == null && abstractID != null)
      {
         toReturn = (NetChannelLocation)channels.get(new NameAndLevel(channelName, nameAccessLevel));
         if (toReturn == null)
         {
            abstractID = abstractID.getParentID();
            nameAccessLevel = new NameAccessLevel(abstractID);
         }
      }
      return toReturn;
   }
   
   /**
    * Deregisters a channel name and nameAccessLevel if a channel matches
    * or removes a matching lease.
    *
    * @param   channelName the channel's name.
    * @param   nameAccessLevel the channel's nameAccessLevel
    * @return  true iff a matching channel is not registered after returning.
    */
   private boolean deregisterChannel(String channelName, NameAccessLevel nameAccessLevel, ChannelNameKey channelKey)
   {
      if (channelName == null || nameAccessLevel == null)
         return true;
      NameAndLevel nal = new NameAndLevel(channelName, nameAccessLevel);
      ChannelNameKey registeredKey = (ChannelNameKey)registeredChannelKeys.get(nal);
      if (registeredKey != null)
      {
         if (registeredKey.equals(channelKey))
         {
            registeredChannelKeys.remove(nal);
            return (channels.remove(nal) != null);
         }
         else
            return false;
      }
      //channel isn't registered - check to see if it has been leased
      //If so, remove the lease
      registeredKey = (ChannelNameKey) leasedChannelKeys.get(nal);
      if (registeredKey != null)
      {
         if (registeredKey.equals(channelKey))
         {
            leasedChannelKeys.remove(nal);
            return true;
         }
         else
            return false;
      }
      return true;
   }
   /*----------------Factory Methods for Channels--------------------------------*/
   
   /**
    * @see org.jcsp.net.cns.NamedChannelEndFactory#createNet2One(String)
    */
   public static NetAltingChannelInput createNet2One(String name)
   {
      return CHAN_FACTORY.createNet2One(name);
   }
   
   /**
    * @see org.jcsp.net.cns.NamedChannelEndFactory#createNet2One(String, NameAccessLevel)
    */
   public static NetAltingChannelInput createNet2One(String name, NameAccessLevel nameAccessLevel)
   {
      return CHAN_FACTORY.createNet2One(name, nameAccessLevel);
   }
   
   /**
    * @see org.jcsp.net.cns.NamedChannelEndFactory#createNet2Any(String)
    */
   public static NetSharedChannelInput createNet2Any(String name)
   {
      return CHAN_FACTORY.createNet2Any(name);
   }
   
   /**
    * @see org.jcsp.net.cns.NamedChannelEndFactory#createNet2Any(String, NameAccessLevel)
    */
   public static NetSharedChannelInput createNet2Any(String name, NameAccessLevel nameAccessLevel)
   {
      return CHAN_FACTORY.createNet2Any(name, nameAccessLevel);
   }
   
   /**
    * @see org.jcsp.net.cns.NamedChannelEndFactory#createOne2Net(String)
    */
   public static NetChannelOutput createOne2Net(String name)
   {
      return CHAN_FACTORY.createOne2Net(name);
   }
   
   /**
    * @see org.jcsp.net.cns.NamedChannelEndFactory#createOne2Net(String, NameAccessLevel)
    */
   public static NetChannelOutput createOne2Net(String name, NameAccessLevel accessLevel)
   {
      return CHAN_FACTORY.createOne2Net(name, accessLevel);
   }
   
   /**
    * @see org.jcsp.net.cns.NamedChannelEndFactory#createAny2Net(String)
    */
   public static NetSharedChannelOutput createAny2Net(String name)
   {
      return CHAN_FACTORY.createAny2Net(name);
   }
   
   /**
    * @see org.jcsp.net.cns.NamedChannelEndFactory#createAny2Net(String, NameAccessLevel)
    */
   public static NetSharedChannelOutput createAny2Net(String name, NameAccessLevel accessLevel)
   {
      return CHAN_FACTORY.createAny2Net(name, accessLevel);
   }
   
   public static void destroyChannelEnd(NetChannelInput chanInEnd)
   {
      CHAN_FACTORY.destroyChannelEnd(chanInEnd);
   }
   
   public static void destroyChannelEnd(NetChannelOutput chanOutEnd)
   {
      CHAN_FACTORY.destroyChannelEnd(chanOutEnd);
   }
   
   /*----------------Static method for installing CNS into current Node----------*/
   
   /**
    * Installs and starts a Channel Name Server on the local Node.
    * The service is installed with the default service name as specified
    * by <code>CNS.CNS_DEFAULT_SERVICE_NAME</code>. This method needs to be
    * supplied with the local Node's <code>NodeKey</code>. This is
    * required in order to obtain access to the Node's service manager.
    *
    * @param key 	the local Node's <code>NodeKey</code>.
    *
    * @throws ServiceInstallationException if installation fails.
    *
    * @return	the <code>CNS</code> object installed.
    */
   public static CNS install(NodeKey key)
   {
      return install(key, CNS_DEFAULT_SERVICE_NAME);
   }
   
   /**
    * Installs and starts a Channel Name Server on the local Node.
    * The service is installed with the specified service name. This
    * method needs to be supplied with the local Node's <code>NodeKey</code>.
    * This is required in order to obtain access to the Node's service
    * manager.
    *
    * @param key 	the local Node's <code>NodeKey</code>.
    * @param name  the name to give the service.
    *
    * @throws ServiceInstallationException if installation fails.
    *
    * @return	the <code>CNS</code> object installed.
    */
   public static CNS install(NodeKey key, String serviceName)
   {
      ServiceManager sm = Node.getInstance().getServiceManager(key);
      
      //Initialize the CNS Server Process
      CNS cns = new CNS(key);
      if (sm.installService(cns, serviceName))
      {
         if (sm.startService(serviceName))
         {
            Node.info.log("org.jcsp.net.cns.CNS", "CNS Started");
            return cns;
         }
         else
         {
            sm.uninstallService(serviceName);
            throw new ServiceInstallationException("Failed to start service.");
         }
      }
      else
      {
         Node.info.log("org.jcsp.net.cns.CNS", "CNS failed to start");
         throw new ServiceInstallationException("Failed to install service.");
      }
   }
   
   /*----------------Inner Classes-----------------------------------------------*/
   
   /**
    * A data structure for holding a channel name and access level.
    * This can be used as a key in a Hashtable.
    */
   static class NameAndLevel
   {
      NameAndLevel(String name, NameAccessLevel nameAccessLevel)
      {
         this.name = name;
         this.nameAccessLevel = nameAccessLevel;
      }
      
      /** Compares this object with another object.
       * @param o An object to compare with this object.
       * @return <CODE>true</CODE> iff o is a non-null <CODE>NameAndLevel</CODE> 
       * object which represents the same name and level as this object.
       */
      public boolean equals(Object o)
      {
         if (o == null || !(o instanceof NameAndLevel))
            return false;
         NameAndLevel other = (NameAndLevel) o;
         //should be able to use == check for nameAccessLevel as only
         //static nameAccessLevel's are accessible
         return name.equals(other.name) && nameAccessLevel.equals(other.nameAccessLevel);
      }
      
      /** Returns a hash code for this object.
       * @return an <CODE>int</CODE> hash code for this object.
       */
      public int hashCode()
      {
         return name.hashCode() + nameAccessLevel.hashCode();
      }
      
      /** Returns a human readable <CODE>String</CODE> representation of this NameAccessLevel object.
       * @return the human readable string as a <CODE>String</CODE> object.
       */
      public String toString()
      {
         return "[" + name + ", " + nameAccessLevel + "]";
      }
      
      private String name;
      private NameAccessLevel nameAccessLevel;
   }
   
   static private class PendingResolve
   {
      CNSMessage.ResolveReply reply;
      ChannelOutput txReplyChannel;
      String channelName;
      NameAccessLevel nameAccessLevel;
      PendingResolve next;
      
      PendingResolve(CNSMessage.ResolveReply reply, ChannelOutput txReplyChannel, 
                     String channelName, NameAccessLevel nameAccessLevel)
      {
         this.channelName = channelName;
         this.nameAccessLevel = nameAccessLevel;
         this.reply = reply;
         this.txReplyChannel = txReplyChannel;
         next = null;
      }
   }
}