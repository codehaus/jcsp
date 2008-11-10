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
import org.jcsp.net.*;
import org.jcsp.util.Buffer;

/**
 * <p>
 * This class is the Channel Name Server's main client process class.
 * </p>
 * <p>
 * One instance of this class needs to be instantiated at each Node wishing
 * to interact with a Channel Name Server.
 * </p>
 * <p>
 * At present only one instance of this service class should be installed at
 * each Node, however, it is envisaged that it may be desirable to be able to
 * use multiple Channel Name servers. This functionality has been partially
 * implemented and eventually multiple instances of this service will be able
 * to be installed.  Each instance would be given a separate name.
 * </p>
 * <p>
 * A default instance of this class can be held. Interaction with this
 * default instance is facilitated through the static methods of
 * the {@link CNS} class.
 * </p>
 * <p>
 * Once constructed, an instance of the object can be installed into the
 * Node's service manager in the same way as any other <code>Service</code>
 * (see <code>{@link org.jcsp.net.ServiceManager}</code>).
 * Alternatively, the one of the static install methods may be used
 * (see <code>{@link CNSService#install(NodeKey, NodeAddressID)}</code>,
 * <code>{@link CNSService#install(NodeKey, NodeAddressID, String)}</code>).
 * </p>
 *
 * @see CNS
 *
 * @author Quickstone Technologies Limited
 */
public class CNSService implements Service, CNSUser
{
   /*---------------Private fields----------------------------------------------*/
   
   private final int RX_BUFFER_SIZE = 30;
   
   private NetChannelLocation cnsChannelLocation = null;
   
   private NetAltingChannelInput fromCNS = null;
   
   private NetChannelOutput toCNS = null;
   
   private boolean running = false;
   
   private Object processStatusChanging = new Object();
   
   /**
    * Channels used to send requests to the service process
    *
    */
   
   private final Any2OneChannel resolveRequestChan = Channel.any2one();
   private final AltingChannelInput resolveRequestChanIn;
   private final SharedChannelOutput resolveRequestChanOut;
   private final Any2OneChannel registerRequestChan = Channel.any2one();
   private final AltingChannelInput registerRequestChanIn;
   private final SharedChannelOutput registerRequestChanOut;
   private final Any2OneChannel deregisterRequestChan = Channel.any2one();
   private final AltingChannelInput deregisterRequestChanIn;
   private final SharedChannelOutput deregisterRequestChanOut;
   private final Any2OneChannel leaseRequestChan = Channel.any2one();
   private final AltingChannelInput leaseRequestChanIn;
   private final SharedChannelOutput leaseRequestChanOut;
   
   /**
    * Channels used to get replies from the service process
    *
    */
   private final One2OneChannel registerReplyChan = Channel.one2one();
   private final AltingChannelInput registerReplyChanIn;
   private final ChannelOutput registerReplyChanOut;
   private final One2OneChannel deregisterReplyChan = Channel.one2one();
   private final AltingChannelInput deregisterReplyChanIn;
   private final ChannelOutput deregisterReplyChanOut;
   private final One2OneChannel leaseReplyChan = Channel.one2one();
   private final AltingChannelInput leaseReplyChanIn;
   private final ChannelOutput leaseReplyChanOut;
   private final Any2OneChannel stopChan = Channel.any2one();
   
   /**
    * List of all pending resolve requests.
    */
   private Hashtable pendingResolves = new Hashtable();
   
   private boolean initialized = false;
   
   private boolean holdStaticRef = true;
   
   private String cnsServiceName = CNS_DEFAULT_SERVICE_NAME;
   
   private final CNSService outerObject = this;
   
   private final UserObject userProxy = new UserObject();
   
   /*---------------Public constants--------------------------------------------*/
   /**
    * Constant specifying the setting name for CNS address.
    */
   public static final String CNS_SETTING_NAME_ADDRESS = "cns_address";
   
   /**
    * Constant specifying the setting name for setting the service
    * name.
    */
   public static final String CNS_SETTING_NAME_SERVICE_NAME = "cns_service_name";
   
   /**
    * Constant specifying the setting name for server's admin channel.
    */
   public static final String CNS_SETTING_NAME_ADMIN_CHANNEL_LABEL = "admin_chan_label";
   
   /**
    * Constant specifying the default name of this service.
    */
   public static final String CNS_DEFAULT_SERVICE_NAME = "org.jcsp.net.cns.CNSService - The CNS.";
   /*---------------Private/Package static fields-------------------------------*/
   
   /**
    * Reference used by static methods of CNS class.
    *
    */
   static CNSService staticServiceRef = null;
   
   /**
    * Object used for synchronizing around staticServiceRef.
    *
    */
   private static Object staticSync = new Object();
   
   /*---------------Constructors------------------------------------------------*/
   /**
    * <p>
    * Default constructor that does not supply any of the necessary settings
    * for initializing the CNS. The <code>init(ServiceSettings)</code> method
    * must be called if this constructor is used.
    * </p>
    */
   public CNSService()
   {
      holdStaticRef = true;
      
      //Request channels
      resolveRequestChanIn = resolveRequestChan.in();
      resolveRequestChanOut = resolveRequestChan.out();
      registerRequestChanIn = registerRequestChan.in();
      registerRequestChanOut = registerRequestChan.out();
      deregisterRequestChanIn = deregisterRequestChan.in();
      deregisterRequestChanOut = deregisterRequestChan.out();
      leaseRequestChanIn = leaseRequestChan.in();
      leaseRequestChanOut = leaseRequestChan.out();
      
      //leaseRequestChanIn
      registerReplyChanIn = registerReplyChan.in();
      registerReplyChanOut = registerReplyChan.out();
      deregisterReplyChanIn = deregisterReplyChan.in();
      deregisterReplyChanOut = deregisterReplyChan.out();
      leaseReplyChanIn = leaseReplyChan.in();
      leaseReplyChanOut = leaseReplyChan.out();
   }
   
   /**
    * <p>
    * Constructor which takes an address of the Node hosting
    * the Channel Name Server.
    * </p>
    * <p>
    * In order to connect to the Channel Name Server, this service
    * must know the label of the server's admin channel. By default this
    * is given the value of the <code>CNS.CNS_CHANNEL_LABEL</code> constant.
    * This constructor assumes that this label has been used. It is equivalent
    * to calling the constructor which takes
    * <code>(NodeAddressID, String)</code> parameters.
    * </p>
    * @see #CNSService(NodeAddressID,String)
    *
    * @param cnsAddress	the address of channel name server to use.
    */
   public CNSService(NodeAddressID cnsAddress)
   {
      this(cnsAddress, CNS.CNS_CHANNEL_LABEL);
   }
   
   /**
    * <p>
    * Constructor which takes an address of the Node hosting
    * the Channel Name Server as well as a <code>boolean</code>
    * indicating whether or not the service should be started.
    * </p>
    * <p>
    * In order to connect to the Channel Name Server, this service
    * must know the label of the server's admin channel. By default this
    * is given the value of the <code>CNS.CNS_CHANNEL_LABEL</code> constant.
    * This constructor assumes that this label has been used. It is
    * equivalent to calling the constructor which takes
    * <code>(NodeAddressID, String,boolean)</code> parameters.
    * </p>
    *
    * @param cnsAddress the address of channel name server to use.
    * @param start      indicates whether the service should be started.
    *                    Should be <code>true</code> to start service.
    *
    * @see #CNSService(NodeAddressID,String,boolean)
    */
   public CNSService(NodeAddressID cnsAddress, boolean start)
   {
      this(cnsAddress, CNS.CNS_CHANNEL_LABEL, start);
   }
   
   /**
    * <p>
    * Constructor which takes an address of the Node hosting
    * the Channel Name Server as well as the label of the Channel
    * Name Server's admin channel.
    * </p>
    * <p>
    * This is equivalent to using the construtor with
    * <code>(NodeAddressID, String, boolean)</code> parameters with the
    * <code>boolean</code> equal to <code>false</code>.
    * </p>
    *
    * @param cnsAddress	the address of channel name server to use.
    * @param adminChannelLabel the label of server's admin channel.
    *
    * @see #CNSService(NodeAddressID,String,boolean)
    *
    */
   public CNSService(NodeAddressID cnsAddress, String adminChannelLabel)
   {
      this(cnsAddress, adminChannelLabel, false);
   }
   
   /**
    * <p>
    * Constructor which takes an address of the Node hosting
    * the Channel Name Server, the label of the Channel
    * Name Server's admin channel and a <code>boolean</code> indicating
    * whether the service should be started.
    * </p>
    * <p>
    * If the service is started, there is no need to initialize and start
    * the service separately.
    * </p>
    *
    * @param cnsAddress	the address of channel name server to use.
    * @param adminChannelLabel the label of server's admin channel.
    * @param start      indicates whether the service should be started.
    *                    Should be <code>true</code> to start service.
    *
    * @see org.jcsp.net.NodeAddressID
    */
   public CNSService(NodeAddressID cnsAddress, String adminChannelLabel, boolean start)
   {
      cnsChannelLocation = new NetChannelLocation(cnsAddress, adminChannelLabel);
      holdStaticRef = true;
      
      //Request channels
      resolveRequestChanIn = resolveRequestChan.in();
      resolveRequestChanOut = resolveRequestChan.out();
      registerRequestChanIn = registerRequestChan.in();
      registerRequestChanOut = registerRequestChan.out();
      deregisterRequestChanIn = deregisterRequestChan.in();
      deregisterRequestChanOut = deregisterRequestChan.out();
      leaseRequestChanIn = leaseRequestChan.in();
      leaseRequestChanOut = leaseRequestChan.out();
      
      //leaseRequestChanIn
      registerReplyChanIn = registerReplyChan.in();
      registerReplyChanOut = registerReplyChan.out();
      deregisterReplyChanIn = deregisterReplyChan.in();
      deregisterReplyChanOut = deregisterReplyChan.out();
      leaseReplyChanIn = leaseReplyChan.in();
      leaseReplyChanOut = leaseReplyChan.out();
      
      //if want to initialize, then initialize
      if (start)
      {
         if (init(null))
         {
            if (!start())
            {
               cnsChannelLocation = null;
               throw new RuntimeException("Failed to start CNS.");
            }
         }
         else
         {
            cnsChannelLocation = null;
            throw new RuntimeException("Failed to init CNS.");
         }
      }
   }
   
   /*-----------Concrete Implementations of Methods Service---------------------*/
   
   /**
    * <p>
    * Initializes the this instance of the <CODE>CNSService</CODE>.
    * </p>
    * <p>
    * If the default constructor was used to construct the instance of this
    * class, then a name of the instance must be supplied along with at
    * least one address of a channel name server. The admin channel label of
    * the server may also be supplied. The keys to use in the
    * <code>ServiceSettings</code> are held as constants in this class
    * ({@link #CNS_SETTING_NAME_ADDRESS},
    * {@link #CNS_SETTING_NAME_SERVICE_NAME},
    * {@link #CNS_SETTING_NAME_ADMIN_CHANNEL_LABEL}).
    * </p>
    * <p>
    * This need only be called if the no-arg constructor is used.
    * </p>
    *
    * @param settings A <CODE>
    *                  {@link org.jcsp.net.ServiceSettings}
    *                  </CODE> object that holds the
    *                  settings for this service.
    *
    * @return <CODE>true</CODE> if this request to initialize the service
    *                succeeds.
    *
    */
   public boolean init(ServiceSettings settings)
   {
      synchronized (processStatusChanging)
      {
         fromCNS = NetChannelEnd.createNet2One(new Buffer(RX_BUFFER_SIZE));
         CNSMessage.LogonMessage logonMsg = new CNSMessage.LogonMessage();
         logonMsg.replyLocation = fromCNS.getChannelLocation();
         
         String serviceName = (settings != null) ? settings.getSetting(CNS_SETTING_NAME_SERVICE_NAME) : null;
         if (serviceName != null)
            this.cnsServiceName = serviceName;
         
         if (cnsChannelLocation == null)
         {
            //construct from the settings object
            
            //get the channel label of CNS Server's admin channel
            String adminChannelLabel =  settings.getSetting(CNS_SETTING_NAME_ADMIN_CHANNEL_LABEL);
            //if no label supplied, use default
            if (adminChannelLabel == null)
               adminChannelLabel = CNS.CNS_CHANNEL_LABEL;
            
            //Get the list of addresses to try and use to connect to the CNS
            NodeAddressID[] possibleAddresses = settings.getAddresses(CNS_SETTING_NAME_ADDRESS);
            for (int i = 0; i < possibleAddresses.length; i++)
            {
               cnsChannelLocation = new NetChannelLocation(possibleAddresses[i], adminChannelLabel);
               try
               {
                  //Attempt to send message to CNS
                  toCNS = NetChannelEnd.createOne2Net(cnsChannelLocation);
                  toCNS.write(logonMsg);
               }
               catch (Exception e)
               {
                  if (i == possibleAddresses.length - 1)
                  {
                     //no more addresses to try so return false
                     clearFields();
                     return false;
                  }
               }
            }
            if (toCNS == null)
            {
               clearFields();
               return false;
            }
         }
         else
         {
            //Constructor supplied cns channel location information
            try
            {
               toCNS = NetChannelEnd.createOne2Net(cnsChannelLocation);
               toCNS.write(logonMsg);
            }
            catch (Exception e)
            {
               e.printStackTrace();
               clearFields();
               return false;
            }
         }
         try
         {
            //Wait for logon reply message from CNS
            CNSMessage.LogonReplyMessage reply = (CNSMessage.LogonReplyMessage)fromCNS.read();
            if (reply.success)
            {
               synchronized (staticSync)
               {
                  if (holdStaticRef && staticServiceRef == null)
                     staticServiceRef = this;
               }
               initialized = true;
               return true;
            }
            else
            {
               Node.err.log(this, "CNS Logon failure");
               clearFields();
               return false;
            }
         }
         catch (Exception e)
         {
            e.printStackTrace();
            clearFields();
            return false;
         }
      }
   }
   
   private void clearFields()
   {
      if (fromCNS != null)
      {
         fromCNS.destroyReader();
         fromCNS = null;
      }
      cnsChannelLocation = null;
      if (toCNS != null)
      {
         toCNS.destroyWriter();
         toCNS = null;
      }
   }
   
   /**
    * This checks to see if the service is already running, if it is not then
    * it starts it.
    *
    * @return  true iff the service is running once this method has returned.
    */
   public boolean start()
   {
      synchronized (processStatusChanging)
      {
         if (!initialized)
            return false;
         if (running)
            return true;
         One2OneChannel startedChan = Channel.one2one();
         new ProcessManager(new ServiceProcess(startedChan.out())).start();
         return ((Boolean) startedChan.in().read()).booleanValue();
      }
   }
   
   /**
    * This checks to see if the service is running, if it is then it stops it.
    *
    * @return  true if the service is NOT running.
    */
   public boolean stop()
   {
      stopChan.out().write(null);
      return true;
   }
   
   /**
    * This returns whether the service is running.
    * If the service is currently starting or stopping, this method will block
    * until the state change is complete.
    *
    * @return  true iff the service is running.
    */
   public boolean isRunning()
   {
      synchronized (processStatusChanging)
      {
         return running;
      }
   }
   
   /**
    * This returns a <code>ServiceUserObject</code> which implements the
    * <code>Resolver</code> interface. This allows processes to obtain a
    * reference to an installed CNS Service without having admin
    * privileges. A <code>SecurityException</code> may be thrown if the
    * calling process does not have permission to obtain the
    * <code>ServiceUserObject</code>.
    *
    * @return a <code>Resolver</code> which can be used to resolve names
    *          using this <code>CNSService</code>.
    * @throws SecurityException if the calling process does not have
    *              permission to obtain the <code>ServiceUserObject</code>.
    *
    * @see CNSUser
    *
    */
   public ServiceUserObject getUserObject() throws SecurityException
   {
      return userProxy;
   }
   
   /*---------------Public Methods-----------------------------------------------*/
   
   /**
    * <p>
    * Installs and starts a Channel Name Server client service on the
    * local Node. The service is installed with the default service name
    * as specified by <code>CNSService.CNS_DEFAULT_SERVICE_NAME</code>.
    * This method needs to be supplied with the local Node's
    * <code>NodeKey</code>. This is required in order to obtain access
    * to the Node's service manager.
    * </p>
    *
    * @param key 	the local Node's <code>NodeKey</code>.
    * @param cnsAddress	the address of the Channel Name Server to use.
    *
    * @throws ServiceInstallationException if installation fails.
    *
    * @return	the <code>CNS</code> object installed.
    */
   public static CNSService install(NodeKey key, NodeAddressID cnsAddress)
   {
      return install(key, cnsAddress, CNS_DEFAULT_SERVICE_NAME);
   }
   
   /**
    * <p>
    * Installs and starts a Channel Name Server client service on the
    * local Node. The service is installed with the specified service name.
    * This method needs to be supplied with the local Node's
    * <code>NodeKey</code>. This is required in order to obtain access to
    * the Node's service manager.
    * </p>
    *
    * @param key 	the local Node's <code>NodeKey</code>.
    * @param cnsAddress	the address of the Channel Name Server to use.
    * @param name  the name to give the service.
    *
    * @throws ServiceInstallationException if installation fails.
    *
    * @return	the <code>CNS</code> object installed.
    *
    * @see org.jcsp.net.NodeAddressID
    */
   public static CNSService install(NodeKey key, NodeAddressID cnsAddress, String serviceName)
   {
      ServiceManager sm = Node.getInstance().getServiceManager(key);
      //Initialize the CNS Server Process
      CNSService cnsService = new CNSService(cnsAddress, false);
      if (sm.installService(cnsService, serviceName))
      {
         if (cnsService.init(null))
         {
            if (sm.startService(serviceName))
            {
               Node.info.log("org.jcsp.net.cns.CNSService", "CNSService Started");
               return cnsService;
            }
         }
         sm.uninstallService(serviceName);
         throw new ServiceInstallationException("Failed to start service.");
      }
      else
      {
         Node.info.log("org.jcsp.net.cns.CNSService", "CNSService failed to start");
         throw new ServiceInstallationException("Failed to install service.");
      }
   }
   
   /*-----------Resolving-------------------------*/
   
   /**
    * <p>
    * This method resolves a channel name into a
    * <code>NetChannelLocation</code> object. The name is assumed
    * to be in the global name space (see
    * {@link NameAccessLevel#GLOBAL_ACCESS_LEVEL}).
    * </p>
    * <p>
    * This method is safe to be called by concurrent processes.
    * </p>
    * @param name the name of channel to resolve.
    * @return the location to which the name resolved.
    */
   public NetChannelLocation resolve(String name)
   {
      return resolve(name, NameAccessLevel.GLOBAL_ACCESS_LEVEL);
   }
   
   /**
    * <p>
    * This method resolves a channel name into a
    * <code>NetChannelLocation</code> object. The name must
    * exist in the specified name space.
    * </p>
    * <p>
    * The name space is specified by passing in a
    * <code>{@link NameAccessLevel}</code> object. A name space
    * includes itself and any name space higher up in the
    * hierarchy. The global name space is the highest level of
    * name space.
    * </p>
    * <p>
    * This method is safe to be called by concurrent processes.
    * </p>
    * @param name the name of channel to resolve.
    * @param accessLevel  the name space in which to resolve
    *                      the channel name.
    * @return the location to which the name resolved.
    */
   public NetChannelLocation resolve(String name, NameAccessLevel accessLevel)
   {
      if (accessLevel == null)
         return resolve(name);
      if (name == null)
         throw new IllegalArgumentException("name cannot be null.");
      PendingResolve req = new PendingResolve(name, accessLevel);
      resolveRequestChanOut.write(req);
      NetChannelLocation resolved = (NetChannelLocation) req.sync.in().read();
      
      CNSNetChannelLocation toReturn = new CNSNetChannelLocation(resolved, name, accessLevel, this, cnsServiceName);
      return (toReturn);
   }
   
   /*-----------Registration----------------------*/
   
   /**
    * <p>
    * This method allows a channel (or any instance of a class implementing
    * <code>{@link Networked}</code>) to be registered with the Channel
    * Name Server. The name will be registered in the global name space.
    * </p>
    * <p>
    * A name will stay registered with the Channel Name Server until it
    * has been leased, deregistered or the Node that registered the name
    * has shut down.
    * </p>
    * <p>
    * This method is safe to be called by concurrent processes.
    * </p>
    * @param owner the <code>Networked</code> object whose location
    *               should be registered.
    * @param name the name against which to register the channel.
    * @return the <code>ChannelNameKey</code> needed for managing
    *          the name registration or <code>null</code> if registration
    *          failed.
    */
   public ChannelNameKey register(Networked owner, String name)
   {
      return register(owner, name, NameAccessLevel.GLOBAL_ACCESS_LEVEL);
   }
   
   /**
    * <p>
    * This method allows a channel (or any instance of a class implementing
    * <code>{@link Networked}</code>) to be registered with the Channel
    * Name Server.
    * </p>
    * <p>
    * The name will be registered in the specified name space. This is
    * specified by passing in a <code>{@link NameAccessLevel}</code> object.
    * </p>
    * <p>
    * A name will stay registered with the Channel Name Server until it
    * has been leased, deregistered or the Node that registered the name
    * has shut down.
    * </p>
    * <p>
    * This method is safe to be called by concurrent processes.
    * </p>
    * @param owner the <code>Networked</code> object whose location
    *               should be registered.
    * @param name the name against which to register the channel.
    * @param accessLevel the name space in which to register the channel name.
    * @return the <code>ChannelNameKey</code> needed for managing
    *          the name registration or <code>null</code> if registration
    *          failed.
    *
    * @see NameAccessLevel
    */
   public ChannelNameKey register(Networked owner, String name, NameAccessLevel accessLevel)
   {
      return register(owner, name, accessLevel, null);
   }
   
   /**
    * <p>
    * This method allows a channel (or any instance of a class implementing
    * <code>{@link Networked}</code>) that has previously been registered
    * with the to be reregistered with the Channel Name Server. The name
    * should have been leased (see <code>
    * {@link #leaseChannelName(String, NameAccessLevel, ChannelNameKey)}
    * </code>). It is necessaray to supply the key that was obtained when
    * the name was leased. If the channel name has not previously been
    * registered, then the key can be specified as <code>null</code>.
    * </p>
    * <p>
    * The name will be registered in the in the global name space.
    * </p>
    * <p>
    * A name will stay registered with the Channel Name Server until it
    * has been leased, deregistered or the Node that registered the name
    * has shut down.
    * </p>
    * <p>
    * This method is safe to be called by concurrent processes.
    * </p>
    *
    * @param owner the <code>Networked</code> object whose location
    *               should be registered.
    * @param name the name against which to register the channel.
    * @param accessLevel the name space in which to register the channel name.
    * @return the <code>ChannelNameKey</code> needed for managing
    *          the name registration or <code>null</code> if registration
    *          failed.
    */
   public ChannelNameKey register(Networked owner, String name, ChannelNameKey key)
   {
      return register(owner.getChannelLocation(), name, NameAccessLevel.GLOBAL_ACCESS_LEVEL, key);
   }
   
   /**
    * <p>
    * This method allows a channel (or any instance of a class implementing
    * <code>{@link Networked}</code>) that has previously been registered
    * to be reregistered with the Channel Name Server. The name
    * should have been leased (see <code>
    * {@link #leaseChannelName(String, NameAccessLevel, ChannelNameKey)}
    * </code>). It is necessaray to supply the key that was obtained when
    * the name was leased. If the channel name has not previously been
    * registered, then the key can be specified as <code>null</code>.
    * </p>
    * <p>
    * The name will be registered in the specified name space. This is
    * specified by passing in a <code>{@link NameAccessLevel}</code> object.
    * </p>
    * <p>
    * A name will stay registered with the Channel Name Server until it
    * has been leased, deregistered or the Node that registered the name
    * has shut down.
    * </p>
    * <p>
    * This method is safe to be called by concurrent processes.
    * </p>
    *
    * @param owner the <code>Networked</code> object whose location
    *               should be registered.
    * @param name the name against which to register the channel.
    * @param accessLevel the name space in which to register the channel name.
    * @param key the <code>ChannelNameKey</code> returned when the
    *             name was leased.
    * @return the <code>ChannelNameKey</code> needed for managing
    *          the name registration or <code>null</code> if registration
    *          failed.
    * @see NameAccessLevel
    */
   public ChannelNameKey register(Networked owner, String name, NameAccessLevel accessLevel, ChannelNameKey key)
   {
      return register(owner.getChannelLocation(), name, accessLevel, key);
   }
   
   /**
    * <p>
    * This method allows a channel's location to be registered against a
    * name in the Channel Name Server. It differs from the other register
    * methods in that it takes a <code>NetChannelLocation</code> object
    * instead of a <code>Networked</code> object. This can be obtained
    * from a <code>Networked</code> object by invoking its
    * <code>#getChannelLocation()</code> method (see
    * <code>{@link Networked#getChannelLocation()}</code>).
    * </p>
    * <p>
    * The name will be registered in the specified name space. This is
    * specified by passing in a <code>{@link NameAccessLevel}</code> object.
    * </p>
    * This method also allows a leased channel name to be registered against
    * an actual location (see <code>
    * {@link #register(Networked, String, NameAccessLevel, ChannelNameKey)}
    * </code>).
    * </p>
    * <p>
    * A name will stay registered with the Channel Name Server until it
    * has been leased, deregistered or the Node that registered the name
    * has shut down.
    * </p>
    * <p>
    * This method is safe to be called by concurrent processes.
    * </p>
    *
    * @param ownerLocation the location of a channel to be registered
    *                       against the name.
    * @param name the name against which to register the channel.
    * @param accessLevel the name space in which to register the channel name.
    * @param key the <code>ChannelNameKey</code> returned when the
    *             name was leased.
    * @return the <code>ChannelNameKey</code> needed for managing
    *          the name registration or <code>null</code> if registration
    *          failed.
    *
    * @see #register(Networked, String, NameAccessLevel, ChannelNameKey)
    * @see NameAccessLevel
    */
   public ChannelNameKey register(NetChannelLocation ownerLocation, String name, 
                                  NameAccessLevel accessLevel, ChannelNameKey key)
   {
      if (accessLevel == null)
         accessLevel = NameAccessLevel.GLOBAL_ACCESS_LEVEL;
      if (ownerLocation instanceof LocationNotCNSRegisterable)
         throw new IllegalArgumentException("The NetChannelLocation object supplied cannot be registered with the CNS.");
      
      if (name == null || accessLevel == null || ownerLocation == null)
         throw new IllegalArgumentException("One or more parameters is null " + (name == null) + " " + 
                                            (accessLevel == null) + " " + (ownerLocation == null));
      
      CNSMessage.RegisterRequest request = new CNSMessage.RegisterRequest();
      request.name = name;
      request.accessLevel = accessLevel;
      request.key = key;
      request.channelLocation = ownerLocation;
      registerRequestChanOut.write(request);
      
      CNSMessage.RegisterReply reply = (CNSMessage.RegisterReply)registerReplyChanIn.read();
      if (reply != null)
         return reply.key;
      else
         //link to CNS lost
         return null;
   }
   
   /*-----------Leasing---------------------------*/
   
   /**
    * <p>
    * Leases the channel name within the specified name space
    * from the Channel Name Server. Once a name has been leased,
    * a channel cannot be registered with that name without supplying
    * the key returned by the call to this method.
    * </p>
    * <p>
    * Currently, leases are infinite; they never expire. This is likely
    * to change in the future so that leases have to be renewed. Leases
    * can be given up by deregistering the name. Leases will remain even
    * after the obtaining Node has shut down. This allows a channel to move
    * its registered location to another channel on a different Node. There
    * is no danger of another party managing to obtain the name as transfering
    * a registered name into a lease is an atomic action. Although leases
    * are currently infinite, there is no reason why a program cannot renew
    * the lease by recalling this method.
    * </p>
    * <p>
    * A name may be leased without first registering a channel against it.
    * </p>
    * <p>
    * This method is safe to be called by concurrent processes.
    * </p>
    *
    * @param  name  the name to lease.
    * @param  accessLevel the name space in which to lease the name.
    * @param  channelKey   the key to use if the name is currently
    *                       registered or leased.
    *
    * @return a new <code>ChannelNameKey</code> needed for managing the
    *          lease or <code>null</code> if leasing failed.
    *
    * @throws ChannelNameException  if the channel name is invalid.
    * @throws NameAccessLevelException if the specifed name space is
    *                                   invalid.
    *
    */
   public ChannelNameKey leaseChannelName(String name, NameAccessLevel accessLevel, ChannelNameKey channelKey)
   throws ChannelNameException, NameAccessLevelException
   {
      if (name == null)
         throw new IllegalArgumentException("Null parameter");
      if (accessLevel == null)
         accessLevel = NameAccessLevel.GLOBAL_ACCESS_LEVEL;
      
      CNSMessage.LeaseRequest request = new CNSMessage.LeaseRequest();
      request.name = name;
      request.accessLevel = accessLevel;
      request.key = channelKey;
      leaseRequestChanOut.write(request);
      CNSMessage.LeaseReply reply = (CNSMessage.LeaseReply) leaseReplyChanIn.read();
    
      if (reply != null)
         return reply.key;
      else
         return null;
   }
   
   /*-----------Deregistration--------------------*/
   /**
    * <p>
    * This deregisters a Channel name from the Channel Name Server.
    * If the CNS Service is not running, this method will block until it
    * is running.
    * A boolean is returned to indicate whether deregistration was successful.
    * This is also returned if the link to the CNS is lost.
    * </p>
    * <p>
    * This method is safe to be called by concurrent processes.
    * </p>
    *
    * @param   name    the name of the channel as a String.
    * @param   nameAccessLevel the nameAccessLevel of the channel.
    * @param   channelKey  the ChannelNameKey to use to deregister the
    *                      Channel name.
    *
    * @return a boolean indicating success.
    */
   public boolean deregisterChannelName(String name, NameAccessLevel accessLevel, ChannelNameKey channelKey)
   {
      if (name == null || channelKey == null)
         throw new IllegalArgumentException("Null parameter");
      if (accessLevel == null)
         accessLevel = NameAccessLevel.GLOBAL_ACCESS_LEVEL;
      
      CNSMessage.DeregisterRequest request = new CNSMessage.DeregisterRequest();
      request.name = name;
      request.accessLevel = accessLevel;
      request.key = channelKey;
      deregisterRequestChanOut.write(request);
      CNSMessage.DeregisterReply reply = (CNSMessage.DeregisterReply) deregisterReplyChanIn.read();
      if (reply != null)
         return reply.success;
      else
         //link to CNS lost
         return false;
   }
   /*---------------Inner Classes------------------------------------------------*/
   
   /**
    * Data structure for pending resolve requests.
    *
    */
   private static class PendingResolve
   {
      PendingResolve(String name, NameAccessLevel nameAccessLevel)
      {
         channelName = name;
         next = null;
         sync = Channel.one2one();
         this.nameAccessLevel = nameAccessLevel;
      }
      
      final String channelName;
      final One2OneChannel sync;
      final NameAccessLevel nameAccessLevel;
      PendingResolve next;
   }
   
   /**
    * The main process of the service.
    *
    * This handles a single request at a time.
    *
    * Register, lease and deregister requests are sent to the server
    * and then the process waits for a  response.
    *
    * Resolve requests are sent to the server after which the process
    * resumes serving requests. The server will send a response back to
    * this process once a name matching the request has been registered.
    *
    */
   private class ServiceProcess implements CSProcess
   {
      ServiceProcess(ChannelOutput startedChan)
      {
         this.startedChan = startedChan;
      }
      
      ChannelOutput startedChan;
      
      public void run()
      {
         try
         {
            final int STOP_INDEX = 0;
            final int FROM_CNS_INDEX = 1;
            final int RESOLVE_INDEX = 2;
            final int REGISTER_INDEX = 3;
            final int LEASE_INDEX = 4;
            final int DEREGISTER_INDEX = 5;
            
            running = true;
            Alternative alt = new Alternative(new Guard[] 
                                             {
                                                stopChan.in(),
                                                fromCNS,
                                                resolveRequestChanIn,
                                                registerRequestChanIn,
                                                leaseRequestChanIn,
                                                deregisterRequestChanIn 
                                             });
            
            startedChan.write(new Boolean(true));
            while (running)
            {
               switch (alt.priSelect())
               {
                  case STOP_INDEX :
                     stopChan.in().read();
                     running = false;
                     break;
                  case FROM_CNS_INDEX :
                     Object obj = fromCNS.read();
                     if (obj instanceof CNSMessage.ResolveReply)
                     {
                        CNSMessage.ResolveReply rr = (CNSMessage.ResolveReply) obj;
                        PendingResolve pr = 
                                (PendingResolve)pendingResolves.remove(new CNS.NameAndLevel(rr.name, rr.accessLevel));
                        while (pr != null)
                        {
                           pr.sync.out().write(rr.channelLocation);
                           pr = pr.next;
                        }
                     }
                     else
                        Node.err.log(this, "Unexpected message type received: " + obj.getClass());
                     break;
                  case RESOLVE_INDEX :
                  {
                     PendingResolve req = (PendingResolve)resolveRequestChanIn.read();
                     req.next = 
                           (PendingResolve)pendingResolves.put(new CNS.NameAndLevel(req.channelName, req.nameAccessLevel), req);
                     if (req.next == null)
                     {
                        // The request to establish this link had not already
                        // been sent so send request
                        CNSMessage.ResolveRequest request = new CNSMessage.ResolveRequest();
                        request.name = req.channelName;
                        request.accessLevel = req.nameAccessLevel;
                        request.replyLocation = fromCNS.getChannelLocation();
                        request.RequestIndex = 0;
                        toCNS.write(request);
                     }
                     break;
                  }
                  case REGISTER_INDEX :
                  {
                     CNSMessage.RegisterRequest req = (CNSMessage.RegisterRequest)registerRequestChanIn.read();
                     req.RequestIndex = 0;
                     req.replyLocation = fromCNS.getChannelLocation();
                     
                     // Send request to CNS
                     toCNS.write(req);
                     CNSMessage reply = getRegistrationReply();
                     if (reply == null)
                     {
                        Node.err.log(this, "Link to CNS lost");
                        stopCNSService();
                     }
                     else
                     {
                        while (!(reply instanceof CNSMessage.RegisterReply))
                        {
                           Node.err.log(this, "Wrong message type received");
                           reply = getRegistrationReply();
                        }
                     }
                     registerReplyChanOut.write(reply);
                     break;
                  }
                  case LEASE_INDEX :
                  {
                     CNSMessage.LeaseRequest req = (CNSMessage.LeaseRequest)leaseRequestChanIn.read();
                     req.RequestIndex = 0;
                     req.replyLocation = fromCNS.getChannelLocation();
                     
                     // Send request to CNS
                     toCNS.write(req);
                     CNSMessage reply = getRegistrationReply();
                     if (reply == null)
                     {
                        Node.err.log(this, "Link to CNS lost");
                        stopCNSService();
                     }
                     else
                        while (!(reply instanceof CNSMessage.LeaseReply))
                           Node.err.log(this, "Wrong message type received"); reply = getRegistrationReply();
                     leaseReplyChanOut.write(reply);
                     break;
                  }
                  case DEREGISTER_INDEX :
                  {
                     CNSMessage.DeregisterRequest req = (CNSMessage.DeregisterRequest)deregisterRequestChanIn.read();
                     req.RequestIndex = 0;
                     req.replyLocation = fromCNS.getChannelLocation();
                     
                     // Send request to CNS
                     toCNS.write(req);
                     
                     CNSMessage reply = getRegistrationReply();
                     if (reply == null)
                     {
                        Node.err.log(this, "Link to CNS lost");
                        stopCNSService();
                     }
                     else
                     {
                        while (!(reply instanceof CNSMessage.DeregisterReply))
                        {
                           Node.err.log(this, "Wrong message type received");
                           reply = getRegistrationReply();
                        }
                     }
                     deregisterReplyChanOut.write(reply);
                     break;
                  }
               }
            }
         }
         catch (Exception e)
         {
            e.printStackTrace();
         }
         finally
         {
            running = false;
         }
      }
      
      private CNSMessage getRegistrationReply()
      {
         // Get reply from CNS and handle others in the mean time.
         CNSMessage reply = null;
         while (reply == null)
         {
            Object obj = fromCNS.read();
            if (obj instanceof CNSMessage.RegisterReply)
               reply = (CNSMessage.RegisterReply) obj;
            else if (obj instanceof CNSMessage.DeregisterReply)
               reply = (CNSMessage.DeregisterReply) obj;
            else if (obj instanceof CNSMessage.LeaseReply)
               reply = (CNSMessage.LeaseReply) obj;
            else if (obj instanceof CNSMessage.ResolveReply)
            {
               CNSMessage.ResolveReply rr = (CNSMessage.ResolveReply) obj;
               PendingResolve pr = (PendingResolve)pendingResolves.remove(new CNS.NameAndLevel(rr.name, rr.accessLevel));
               while (pr != null)
               {
                  pr.sync.out().write(rr.channelLocation);
                  pr = pr.next;
               }
            }
            else
            {
               Thread.dumpStack();
               Node.err.log(this, "Unexpected message type received.");
            }
         }
         return reply;
      }
      
      /**
       * This stops the CNS Service and removes its Channel from
       * IndexManager. It sets the running indicator to false.
       *
       */
      private void stopCNSService()
      {
      }
      
   }
   /**
    * This class acts as a proxy for the name management
    * methods in its outer class.
    *
    */
   private class UserObject implements ServiceUserObject, CNSUser
   {
      public NetChannelLocation resolve(String name)
      {
         return outerObject.resolve(name);
      }
      
      public NetChannelLocation resolve(String name, NameAccessLevel accessLevel)
      {
         return outerObject.resolve(name, accessLevel);
      }
      
      public ChannelNameKey register(Networked owner, String name)
      {
         return outerObject.register(owner, name);
      }
      
      public ChannelNameKey register(Networked owner, String name, NameAccessLevel accessLevel)
      {
         return outerObject.register(owner, name, accessLevel);
      }
      
      public ChannelNameKey register(Networked owner, String name, ChannelNameKey key)
      {
         return outerObject.register(owner, name, key);
      }
      
      public ChannelNameKey register(Networked owner, String name, NameAccessLevel accessLevel, ChannelNameKey key)
      {
         return outerObject.register(owner, name, accessLevel, key);
      }
      
      public ChannelNameKey register(NetChannelLocation ownerLocation, String name, 
                                     NameAccessLevel accessLevel, ChannelNameKey key)
      {
         return outerObject.register(ownerLocation, name, accessLevel, key);
      }
      
      public ChannelNameKey leaseChannelName(String name, NameAccessLevel accessLevel, ChannelNameKey channelKey)
      throws ChannelNameException, NameAccessLevelException
      {
         return outerObject.leaseChannelName(name, accessLevel, channelKey);
      }
      
      public boolean deregisterChannelName(String name, NameAccessLevel accessLevel, ChannelNameKey channelKey)
      {
         return outerObject.deregisterChannelName(name, accessLevel, channelKey);
      }
   }
}