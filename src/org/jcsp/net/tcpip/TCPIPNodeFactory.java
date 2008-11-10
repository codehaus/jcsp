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

package org.jcsp.net.tcpip;

import java.net.*;
import java.util.prefs.*;
import org.jcsp.net.*;
import org.jcsp.net.cns.*;
import org.jcsp.net.security.*;

/**
 * <p>Default factory for simple initialisations using the TCPIPv4 protocol package.</p>
 *
 * <p>Other constructors can be used to specify a particular CNS server. The default behaviour will
 * extract a CNS server from the system property "org.jcsp.tcpip.DefaultCNSServer" with the server specified
 * as a name or IP address with optional port number.</p>
 *
 * <p>If built with 1.4 or higher the user preferences will also be checked but only if a
 * runtime supporting the preferences is available. If not present the system preferences will then
 * be checked.</p>
 *
 * @author Quickstone Technologies Limited
 */
public class TCPIPNodeFactory implements NodeFactory
{
   /**
    * Address of the CNS server.
    */
   private final NodeAddressID cnsServer;
   
   /**
    * Port number to use for the local link server. By default will be system assigned. Call
    * setLocalPort prior to calling Node.init () to set a specific value.
    */
   private int localPort = 0;
   
   /**
    * Optional security service to install.
    */
   private SecurityService security = null;
   
   /**
    * Constructs an instance of this class. This method is provided so that a later implementation can
    * opt to pass construction to an alternative under certain conditions. This is for use by the
    * infrastructure only. Users should call the public constructors.
    */
   public static NodeFactory instantiate()
   {
      return new TCPIPNodeFactory();
   }
   
   /**
    * Construct the server address, assuming the local host if none is specified.
    *
    * @param cnsServerAddress address of CNS server or null to use the local host.
    * @param cnsServerPort port number
    */
   private NodeAddressID construct(String cnsServerAddress, int cnsServerPort)
   {
      try
      {
         return new TCPIPAddressID((cnsServerAddress == null)
                                   ? InetAddress.getLocalHost().getHostName()
                                   : cnsServerAddress, cnsServerPort, true);
      }
      catch (UnknownHostException e)
      {
         throw new RuntimeException("Invalid host - " + cnsServerAddress);
      }
   }
   
   /**
    * Split the server string into a server and port component on the : character. If none is
    * present the default port is assumed and the request passed to the other <code>construct</code>
    * method.
    *
    * @param cnsServer server with optional port number
    */
   private NodeAddressID construct(String cnsServer)
   {
      int cnsServerPort;
      if (cnsServer != null)
      {
         int ix = cnsServer.indexOf(':');
         if (ix < 0)
            cnsServerPort = TCPIPCNSServer.DEFAULT_CNS_PORT;
         else
         {
            cnsServerPort = Integer.parseInt(cnsServer.substring(ix + 1));
            cnsServer = cnsServer.substring(0, ix);
         }
      }
      else
         cnsServerPort = TCPIPCNSServer.DEFAULT_CNS_PORT;
      return construct(cnsServer, cnsServerPort);
   }
   
   /**
    * This code was moved into this method from the constructor below
    * in the hope that code would compile on 1.4 and still run on 1.3 and 1.2,
    *
    */
   private String getServerFromPrefs()
   {
      try
      {
         String svr = Preferences.userNodeForPackage(getClass()).get("DefaultCNSServer", null);
         if (svr == null)
            svr = Preferences.systemNodeForPackage(getClass()).get("DefaultCNSServer", null);
         return svr;
      }
      catch (Exception e)
      {
    	  return null;
      }      
   }
   
   /**
    * Default constructor, connecting to the local host on the default CNS port or to a host name
    * in the system properties. The host name should be specified as <i>name</i>:</i>port</i> or
    * just the host name to use the default CNS port.
    */
   public TCPIPNodeFactory()
   {
      try
      {
         String svr = System.getProperty("org.jcsp.tcpip.DefaultCNSServer");
         if (svr == null)
         {
            try
            {
               if (Class.forName("java.util.prefs.Preferences") != null)
                  svr = getServerFromPrefs();
            }
            catch (ClassNotFoundException e)
            {
            }
         }
         this.cnsServer = construct(svr);
      }
      catch (SecurityException e)
      {
         throw new RuntimeException("Invalid host");
      }
   }
   
   /**
    * Connect to the named host on the default CNS port. If the server name is given as <code>null</code>
    * then the CNS service will not be loaded.
    *
    * @param cnsServer name of server
    */
   public TCPIPNodeFactory(String cnsServer)
   {
      this.cnsServer = (cnsServer == null) ? null : construct(cnsServer);
   }
   
   /**
    * Connect to a named host on a specific port.
    *
    * @param cnsServerAddress name of the server
    * @param cnsServerPort port to connect to
    */
   public TCPIPNodeFactory(String cnsServerAddress, int cnsServerPort)
   {
      cnsServer = construct(cnsServerAddress, cnsServerPort);
   }
   
   /**
    * Initialise the node to listen on all local addresses, and start a CNS service.
    *
    * @param node the node to be initialized
    * @param attribs access to the restricted node attributes
    * @throws NodeInitFailedException if a problem occurs
    */
   public NodeKey initNode(Node node, Node.Attributes attribs) throws NodeInitFailedException
   {
      // Get UI factory from system property; or use default
      UIFactory uiFactory;
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
            return null;
         }
      }
      else
      {
         uiFactory = new UIFactory();
      }
      attribs.setUIFactory(uiFactory);
      
      // Setup protocols ...
      // Setup local addresses (defaults to all local addresses)
      try
      {
         InetAddress[] allLocal = InetAddress.getAllByName(InetAddress.getLocalHost().getHostName());         
         for (int i = 0; i < allLocal.length; i++)
         {
            try
            {
               TCPIPAddressID addr = new TCPIPAddressID(allLocal[i], localPort, true);
               if (!attribs.getProtocolManager().installProtocolServer(addr, null))
                  throw attribs.exception("Unable to start LinkServer on " + addr);
               if (!attribs.getProtocolManager().installProtocolClient(addr.getProtocolID(), null, null))
                  throw attribs.exception("Unable to install protocol " + addr.getProtocolID());
            }
            catch (Exception e)
            {
               if (e instanceof NodeInitFailedException)
                  throw e;
               Node.err.log(this, "Cannot listen on " + allLocal[i]);
            }
         }
      }
      catch (Exception e)
      {
         if (e instanceof NodeInitFailedException)
            throw attribs.exception(e.getMessage());
         Node.err.log(this, "Error determining local addresses");
      }
      // Setup node specifications ...
      // Setup profiles ...
      // Node profiles ...
      // Setup security service
      if (security != null)
      {
         ServiceManager sm = attribs.getServiceManager();
         if (sm.installService(security, "security") && security.init(null) && sm.startService("security"))
            Node.info.log(this, "Security service started");
         else
         {
            Node.info.log(this, "Security service failed to start");
            return null;
         }
      }
      // Start link manager
      attribs.startLinkManager();
      // Node is now initialized
      attribs.setInitialized();
      // Setup CNS service
      if (cnsServer != null)
      {
         ServiceManager sm = attribs.getServiceManager();
         CNSService cnsService = new CNSService(cnsServer, false);
         if (sm.installService(cnsService, CNSService.CNS_DEFAULT_SERVICE_NAME) 
             && cnsService.init(null) 
             && sm.startService(CNSService.CNS_DEFAULT_SERVICE_NAME))
         {
            Node.info.log(this, "CNS Started");
         }
         else
         {
            Node.info.log(this, "CNS failed to start");
            return null;
         }
      }
      return attribs.getNodeKey();
   }
   
   /**
    * Sets the port number that should be used. If this is not called then the system will allocate
    * an arbitrary port.
    *
    * @param port the port number, 0 <= port < 65536.
    */
   public void setLocalPort(int port)
   {
      localPort = port;
   }
   
   /**
    * Sets the security service to be used.
    *
    * @param security the new security authority.
    */
   public void setSecurityAuthority(SecurityService security)
   {
      this.security = security;
   }  
}