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

package org.jcsp.net;

import org.jcsp.lang.*;
import java.util.*;

/**
 * <p>
 * This class that is used for initializing and
 * managing the local JCSP.NET Node. A JCSP Node is a Java Virtual Machine
 * that has been initialized to form part of a JCSP.NET network. By this
 * definition, only one instance of this class is ever required on any
 * JVM. Therefore this class is a singleton class.To obtain the instance
 * of this class, use the <code>{@link #getInstance()}</code> method.
 * </p>
 * <p>
 * <h3>Node Initialization</h3>
 * </p>
 * <p>
 * Before any JCSP.NET constructs can be used, the Node must be initialized.
 * There are several <code>init()</code> methods for initializing the Node
 * in different ways.
 * </p>
 * <p>
 * In order for a Node to be initialized, there are various settings that are
 * required and others which are useful to have. A key facility of a Node is to
 * be able to communicate with other Nodes. This requires at least one
 * communications protocol to be installed and also ideally an address on which
 * to listen for incoming connections. As well as protocols, there are various
 * services that a user might want to start when ever the Node is initialized.
 * Technically the Node could be initialized and have its protocols, addresses and
 * services set up manually by the user. This would, however, be rather cumbersome.
 * </p>
 * <p>
 * The JCSP.NET infrastructure is not dependent upon any one network protocol. The
 * <code>org.jcsp.net.tcpip</code> package is provided but could easily
 * be replaced by an alternate protocol implementation. In the real world, TCP/IP is
 * likely to suit many uses and so this class assumes this as a default protocol.
 * With this assumption, it is possible to initialize the Node without any further
 * information. A Node can be initialized which installs TCP/IP as a protocol and
 * listens on all local IP addresses. This is precisely the bahaviour of the
 * <code>{@link #init()}</code> method with no parameters.
 * </p>
 * <p>
 * One of the facilities that most JCSP.NET users are likely to use is the
 * Channel Name Server. This requires a server process to be running on a
 * JCSP.NET Node and client services on all Nodes that wish to use the server.
 * Assuming that a Channel Name Server is running, then at the time of
 * Node initialization, the knowledge of this server's address is required.
 * </p>
 * <p>
 * The <code>{@link org.jcsp.net.tcpip.TCPIPNodeFactory}</code> has
 * knowledge of the Channel Name Server and has code for starting the client
 * service on a Node. It provides a constructor which takes the address of the
 * channel name server. An instance of the class can be created with this
 * constructor and passed to the <code>{@link #init(NodeFactory)}</code> method.
 * If the parameterless <code>{@link #init()}</code> is used, then an instance
 * of the factory is created using its instantiate method which takes no settings.
 * This tries to determine the address of the Channel Name Service through other
 * means (including system properties and the preferences API etc.). See
 * <code>{@link org.jcsp.net.tcpip.TCPIPNodeFactory}</code> for
 * more information on this.
 * </p>
 * <p>
 * Another <code>NodeFactory</code> class provided with JCSP.NET is the
 * <code>{@link XMLNodeFactory}</code>. This reads its settings from an
 * XML-like config file. This is not a full XML file as JCSP.NET provides
 * its own parser. This saves pre Java 1.4 users from obtaining an XML
 * parsing package. See <code>{@link XMLNodeFactory}</code> for full
 * documentation on how to use this.
 * </p>
 * <p>
 * As well as initialization methods which take <code>NodeFactory</code>
 * objects, there are also methods which take protocol settings
 * directly. See the {@link #init(NodeAddressID)}, {@link #init(NodeAddressID[])}
 * and {@link #init(NodeAddressID[], Hashtable[])} methods.
 * </p>
 * <p>
 * All of the Node init methods return a <code>NodeKey</code> object which
 * is required for Node management methods.
 * </p>
 * <p>
 * <h3>Node Management</h3>
 * </p>
 * <p>
 * Once initialized, JCSP Nodes can have their settings altered. There are two
 * manager classes of which each Node has an instance;
 * <code>{@link ServiceManager}</code> and <code>{@link ProtocolManager}</code>.
 * These instances can be obtained from the local <code>Node</code> object by
 * calling their respective accessors (<code>{@link #getServiceManager}</code>
 * and <code>{@link #getProtocolManager}</code>). Each of these accessors takes
 * a <code>{@link NodeKey}</code> object as a parameter. This is for security
 * reasons. Any process can obtain a reference to the local Node object. Without
 * this key parameter, any process could therefore perform Node management
 * operations. This key allows gives control of access to these methods to the
 * process which initialized the Node. This process can decide who it trusts.
 * </p>
 * <p>
 * See <code>{@link ServiceManager}</code> and
 * <code>{@link ProtocolManager}</code> for more information.
 * </p>
 * <p>
 * <h3>Logging</h3>
 * </p>
 * <p>
 * The Node class provides two static <code>{@link Logger}</code> objects for
 * programs to log information. <code>{@link Node#info}</code> provides
 * facilities for logging information messages. <code>{@link Node#err}</code>
 * provides facilities for logging error messages. The output from these
 * logging objects can be enabled or disabled. See <code>{@link Logger}</code>
 * for more information.
 * </p>
 *
 * @author Quickstone Technologies Limited
 */
public class Node
{
   /*-------------------Attributes-----------------------------------------------*/
   
   private NodeID nodeID;
   
   private Specification[] specifications = null;
   
   private UIFactory uiFactory;
   
   private boolean initialized = false;
   
   private ProtocolManager protocolManager = ProtocolManager.getInstance();
   
   private ServiceManager serviceManager = new ServiceManager();
   
   private NodeKey nodeKey = new NodeKey();
   
   private int appIDCounter = 0;
   
   /*-------------------Singleton Class Instance--------------------------------*/
   
   private static Node instance = new Node();
   
   /*-------------------Private Constructor-------------------------------------*/
   
   private Node()
   {
   }
   
   /*-------------------Static Methods------------------------------------------*/
   
   /**
    * Returns the instance of this singleton class.
    *
    * @return the instance of Node.
    */
   public static Node getInstance()
   {
      return instance;
   }
   
   private static NodeFactory factory = org.jcsp.net.tcpip.TCPIPNodeFactory.instantiate();
   
   public static void setNodeFactory(NodeFactory fac)
   {
      factory = fac;
   }
   
   /**
    * A <code>{@link Logger}</code> for logging information messages..
    */
   public static final Logger info = new Logger("info", "stdout");
   
   /**
    * A <code>{@link Logger}</code> for logging error messages.
    */
   public static final Logger err = new Logger("err", "stderr");
   
   /*-------------------Init Methods--------------------------------------------*/
   
   /**
    * <p>
    * This method functions the same as calling
    * <CODE>init(NodeAddressID[])</CODE> with a single element in the
    * array.
    * </p>
    * @param localAddress an address on which to start a <CODE>LinkServer</CODE> process.
    * @throws IllegalStateException if the local Node has already been initialized.
    * @throws IllegalArgumentException if no addresses are supplied.
    * @throws NodeInitFailedException if the Node is not able to initialize.
    * @return the <code>NodeKey</code> required for Node management.
    */
   public NodeKey init(NodeAddressID localAddress) 
   throws IllegalStateException, IllegalArgumentException, NodeInitFailedException
   {
      if (localAddress == null)
         throw new IllegalArgumentException("Null address");
      return init(new NodeAddressID[] { localAddress });
   }
   
   /**
    * <p>
    * This method is functionally equivalent to calling
    * <CODE>init(NodeAddressID[], HashTable)</CODE> with the <CODE>HashTable</CODE>
    * parameter being supplied as <CODE>null</CODE>.
    * </p>
    * @param localAddresses the addresses on which to start <CODE>LinkServer</CODE> processes.
    * @throws IllegalStateException if the local Node has already been initialized.
    * @throws IllegalArgumentException if no addresses are supplied.
    * @throws NodeInitFailedException if the Node is not able to initialize.
    * @return the <code>NodeKey</code> required for Node management.
    */
   public NodeKey init(NodeAddressID[] localAddresses)
   throws IllegalStateException, IllegalArgumentException, NodeInitFailedException
   {
      return init(localAddresses, null);
   }
   
   /**
    * <p>
    * Initializes the local Node and starts <CODE>LinkServer</CODE> listening
    * on each of the supplied <CODE>NodeAddressID</CODE> objects. The
    * protocols that match each address are also installed in the local
    * protocol manager. If protocol settings are supplied, then these are
    * used when the protocols are installed.
    * </p>
    * <p>
    * A key is returned that needs to be supplied to certain management methods.
    * </p>
    *
    * @param localAddresses the addresses on which to start
    *                          <CODE>LinkServer</CODE> processes.
    * @param protocolSettings an array of <CODE>HashTable</CODE> objects each
    *                          containing settings for the protocols to be
    *                          installed.
    *                          Each <CODE>HashTable</CODE> is passed to the
    *                          protocol's <CODE>ProtocolID</CODE> object's
    *                          <CODE>getLinkBuilder(HashTable)</CODE> method.
    *                          No restriction is placed on protocol
    *                          implementations as to how this
    *                          <CODE>HashTable</CODE> object be used. See the
    *                          documentation for whatever protocol
    *                          implementation is being used.
    * @throws IllegalStateException if the local Node has already been initialized.
    * @throws IllegalArgumentException if no addresses are supplied.
    * @throws NodeInitFailedException if the Node is not able to initialize.
    * @return the <code>NodeKey</code> required for Node management.
    */
   public NodeKey init(NodeAddressID[] localAddresses, Hashtable[] protocolSettings)
   throws IllegalStateException, IllegalArgumentException, NodeInitFailedException
   {
      if (localAddresses == null || localAddresses.length == 0)
         throw new IllegalArgumentException("Null argument");
      if (protocolSettings != null && protocolSettings.length != localAddresses.length)
         throw new IllegalArgumentException("Wrong number of protocol settings");
      
      for (int i = 0; i < localAddresses.length; i++)
      {
         if (localAddresses[i] == null)
            throw new IllegalArgumentException("Null address");
      }
      
      // Only call this once or else...
      if (initialized)
         throw new IllegalStateException("Attempted to initialize org.jcsp.net.Node twice!");
      
      //start Link Manager
      LinkManager.getInstance().start();
      
      //Look to see if a System property has been set to set a
      //different UIFactory to be used.
      String uiFactoryClassName = System.getProperty("JCSP.UIFactoryClass");
      if (uiFactoryClassName != null)
      {
         try
         {
            Class uiFactoryClass = Class.forName(uiFactoryClassName);
            uiFactory = (UIFactory) uiFactoryClass.newInstance();
         }
         catch (Exception e)
         {
            Node.err.log(this, "Error trying to load UIFactory: " + uiFactoryClassName);
            throw new NodeInitFailedException("Error trying to load UIFactory: " + uiFactoryClassName);
         }
      }
      else
         uiFactory = new UIFactory();
      nodeID = new NodeID(uiFactory.getUIForThisJVM());
      
      for (int i = 0; i < localAddresses.length; i++)
      {
         if (!protocolManager.installProtocolServer(localAddresses[i], null))
            throw new NodeInitFailedException("Unable to start LinkServer on " + localAddresses[i]);
  
         Hashtable hs = null;
         if (protocolSettings != null)
            hs = protocolSettings[i];
         if (!protocolManager.installProtocolClient(localAddresses[i].getProtocolID(), null, hs))
            throw new NodeInitFailedException("Unable to install protocol " + localAddresses[i].getProtocolID());
      }
      initialized = true;
      return nodeKey;
   }
   
   /**
    * <p>
    * Initializes the Node with the current static <code>NodeFactory</code>.
    * This can be set by calling the
    * <code>{@link #setNodeFactory(NodeFactory)}</code> method.
    * </p>
    * <p>
    * This default factory is currently the
    * <code>{@link org.jcsp.net.tcpip.TCPIPNodeFactory}</code>.
    * This is instantiated with its <code>instantiate()</code> method.
    * </p>
    *
    * @return the <code>NodeKey</code> required for Node management.
    * @throws NodeInitFailedException if the Node is not able to initialize.
    * @see #init(NodeFactory)
    */
   public NodeKey init() throws NodeInitFailedException
   {
      return init(factory);
   }
   
   /**
    * <p>
    * Initializes the Node with the specified
    * <code>{@link NodeFactory}</code> object.
    * </p>
    * <p>
    * This invokes the factory's <code>initNode(Node, Node.Attributes)</code>
    * method to actually perform the initialization.
    * </p>
    * <p>
    * This package provides an <code>{@link XMLNodeFactory}</code> which
    * instantiates the Node from settings read from an XML-like config file.
    * </p>
    * @return the <code>NodeKey</code> required for Node management.
    * @throws NodeInitFailedException if the Node is not able to initialize.
    */
   public NodeKey init(NodeFactory factory) throws NodeInitFailedException
   {
      if (initialized)
         throw new IllegalStateException("Attempted to initialize org.jcsp.net.Node twice!");
      NodeKey nk = factory.initNode(this, new AttributesAccess());
      if (nk == null)
      {
         initialized = false;
         throw new NodeInitFailedException();
      }
      return nk;
   }
   

   /*-------------------Other Public Methods-------------------------------------*/
   /**
    * <p>
    * This method allows a <CODE>NodeID</CODE> to be checked to see whether it
    * represents the local Node. It is provided as a more efficient way of
    * performing this check than obtaining the local node and checking manually.
    * This would result in the local <CODE>NodeID</CODE> object being cloned.
    * </p>
    *
    * @return boolean indicating whether the address supplied is local.
    * @param nodeID a <CODE>NodeID</CODE> to check against the local Node's <CODE>NodeID</CODE>.
    * @throws IllegalStateException if the local Node has not been initialized.
    */
   public boolean isThisNode(NodeID nodeID) throws IllegalStateException
   {
      checkInitialized();
      synchronized (this.nodeID)
      {
         return this.nodeID.equals(nodeID);
      }
   }
   
   /**
    * <p>
    * Checks that the Node has been initialized.  If so, returns normally,
    * otherwise throws an <code>IllegalStateException</code>.
    * </p>
    *
    * @throws IllegalStateException If init() has not been called.
    */
   void checkInitialized() throws IllegalStateException
   {
      if (!initialized)
         throw new IllegalStateException("org.jcsp.net.Node not initialized.");
   }
   
   /**
    * <p>
    * Returns <code>true</code> if the node has already been initialized,
    * <code>false<code> otherwise.
    * </p>
    *
    * @return a <code>true</code> iff Node has been initialized.
    */
   public boolean isInitialized()
   {
      return initialized;
   }
   
   /**
    * <p>
    * Method for obtaining a clone of this Node's <code>NodeID</code>.
    * </p>
    * <p>
    * This method does not expose the actual underlying object as it is not
    * immutable.
    * </p>
    *
    * @return this Node's <CODE>NodeID</CODE>. A clone of the one held by this class.
    * @throws IllegalStateException if this Node has not been initialized.
    */
   public NodeID getNodeID() throws IllegalStateException
   {
      checkInitialized();
      try
      {
         synchronized (this.nodeID)
         {
            return (NodeID) nodeID.clone();
         }
      }
      catch (CloneNotSupportedException e)
      {
         //should never be called
         return null;
      }
   }
   
   synchronized NodeID getActualNode()
   {
      return nodeID;
   }
   
   public synchronized ApplicationID getNewApplicationID()
   {
      return new ApplicationID(this.nodeID, this.appIDCounter++);
   }
   
   /**
    * <p>
    * Accessor for obtaining a reference to the local Node's
    * <CODE>ProtocolManager</CODE> object. The local Node's key must
    * be supplied in order to obtain this reference. This prevents
    * any unauthorized code from managing the local Node's communication protocols.
    * </p>
    * @param nodeKey the local Node's <CODE>NodeKey</CODE>.
    * @return the local Node's <CODE>ProtocolManager</CODE> or <CODE>null></CODE> if
    * an incorrect key is supplied.
    */
   public ProtocolManager getProtocolManager(NodeKey nodeKey)
   {
      if (nodeKey == this.nodeKey)
         return protocolManager;
      return null;
   }
   
   /**
    * <p>
    * Accessor for obtaining a reference to the local Node's
    * <CODE>ServiceManager</CODE> object. The local Node's key must
    * be supplied in order to obtain this reference. This prevents
    * any unauthorized code from managing the local Node's services.
    * </p>
    *
    * @param nodeKey the local Node's <CODE>NodeKey</CODE>.
    * @return the local Node's <CODE>ServiceManager</CODE> or <CODE>null></CODE> if
    * an incorrect key is supplied.
    */
   public ServiceManager getServiceManager(NodeKey nodeKey)
   {
      if (nodeKey == this.nodeKey)
         return getServiceManager();
      return null;
   }
   
   ServiceManager getServiceManager()
   {
      return serviceManager;
   }
   
   /**
    * <p>
    * Obtains a <code>ServiceUserObject</code> from a named Service and
    * returns a reference to it. This method calls
    * <code>getService(String)</code> on a <code>Service</code> object and
    * will therefore obey the rules implemented in the requested Service.
    * </p>
    * <p>
    * Generally, services are expected to check whether the requesting
    * process has permission to access the user object and throw a
    * <code>SecurityException</code> if access is denied.
    * </p>
    * @return a <code>ServiceUserObject</code> object.
    *
    */
   public ServiceUserObject getServiceUserObject(String name)
   {
      return serviceManager.getService(name).getUserObject();
   }
   
   /**
    * <p>
    * Verifies that the supplied key matches the local Node's key.
    * </p>
    * @param nodeKey a <CODE>NodeKey</CODE> to check against that of the local Node's.
    * @return <CODE>true</CODE> iff the supplied key matches the local
    *          Node's key.
    */
   public boolean verifyKey(NodeKey nodeKey)
   {
      return this.nodeKey == nodeKey;
   }
   
   /**
    * <p>
    * Returns an array of <CODE>Specification</CODE> objects to which this
    * Node conforms.
    * </p>
    * @return the set of defined <CODE>Specification</CODE> objects for this Node.
    */
   public Specification[] getNodeSpecifications()
   {
      return (Specification[]) specifications.clone();
   }
   
   /**
    * <p>
    * This method tests whether a link currently exists to
    * a specified remote Node.
    * </p>
    *
    * @param  otherNode  The <code>NodeID</code> of a remote Node to check
    *                     the existance
    * @return <code>true</code> iff a link currently exists to
    *          the specified remote Node.
    */
   public boolean linkExists(NodeID otherNode)
   {
      return LinkManager.getInstance().linkExists(otherNode);
   }
   
   /**
    * <p>
    * This method allows users to obtain notification of link failure.
    * </p>
    * <p>
    * The API surrounding these events is new and will possibly change. Users
    * should be aware of this and only use if stricly necessary.
    * </p>
    * <p>
    * This returns an <code>AltingChannelInput</code> which will receive
    * events signifying that a link to a particular Node has be been dropped.
    * This will receive <code>NodeID</code> objects which signigy the remote
    * Nodes at the other end of links which have been dropped.
    * </p>
    *
    * @return the <code>AltingChannelInput</code> event channel.
    */
   public AltingChannelInput getLinkLostEventChannel()
   {
      return LinkManager.getInstance().getLinkLostEventChannel();
   }
   
   /*-------------------Package Private Methods----------------------------------*/
   
   /*-------------------Inner Classes--------------------------------------------*/
   
   /**
    * An interface for exposing access to the private attributes. This is public.
    */
   public interface Attributes
   {
      public void setSpecifications(Specification[] specs);
      public NodeID setUIFactory(UIFactory factory);
      public void setInitialized();
      public ProtocolManager getProtocolManager();
      public ServiceManager getServiceManager();
      public NodeKey getNodeKey();
      public NodeInitFailedException exception(String msg);
      public void startLinkManager();
   }
   
   /**
    * A class for exposing access to the private attributes. This is private so that only
    * this class can create an instance. The interface (Attributes) is public to allow it
    * to be used once created.
    */
   private class AttributesAccess implements Attributes
   {
      public void setSpecifications(Specification[] specs)
      {
         specifications = specs;
      }
      public NodeID setUIFactory(UIFactory factory)
      {
         uiFactory = factory;
         nodeID = new NodeID(uiFactory.getUIForThisJVM());
         return nodeID;
      }
      public void setInitialized()
      {
         initialized = true;
      }
      public ProtocolManager getProtocolManager()
      {
         return protocolManager;
      }
      public ServiceManager getServiceManager()
      {
         return serviceManager;
      }
      public NodeKey getNodeKey()
      {
         return nodeKey;
      }
      public NodeInitFailedException exception(String msg)
      {
         return new NodeInitFailedException(msg);
      }
      public void startLinkManager()
      {
         LinkManager.getInstance().start();
      }
   }
}