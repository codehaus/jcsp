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

import java.util.*;

/**
 * <p>
 * The <CODE>ProtocolManager</CODE> class is a singleton class.
 * See the documentation for the <CODE>Node</CODE> class
 * for information on how to obtain a reference to its instance.
 * </p>
 * <p>
 * A reference to a Node's <CODE>ProtocolManager</CODE> allows the Nodes installed
 * protocols to be managed. Installing a protocol server creates a
 * <CODE>LinkServer</CODE> that listens on a specified <CODE>NodeAddressID</CODE>.
 * In order to connect to a Node's <CODE>LinkServer</CODE>, a Node must have
 * a matching protocol client installed in its local <CODE>ProtocolManager</CODE>.
 * </p>
 * <p>
 * When protocol clients and servers are installed, an array of
 * <CODE>Specification</CODE> objects can be supplied. These are
 * specifications that the client or server meets. The specifications for
 * a given protocol can be obtained and then used to test whether a set of
 * specifications match a given <CODE>Profile</CODE>.
 * </p>
 * @author Quickstone Technologies Limited
 */
public class ProtocolManager
{
   /*-------------------Singleton Class Instance---------------------------------*/
   
   private static ProtocolManager instance = new ProtocolManager();
   
   /*-------------------Private Constructor--------------------------------------*/
   
   private ProtocolManager()
   {
   }
   
   /*----------------------Methods-----------------------------------------------*/
   
   static ProtocolManager getInstance()
   {
      return instance;
   }
   
   /** Returns an array of <CODE>Specification</CODE> objects that are held for
    * a specified protocol.
    * @param protocolID the <CODE>ProtocolID</CODE> of a protocol
    * @return the specifications of the supplied protocol.
    */
   public Specification[] getProtocolSpecifications(ProtocolID protocolID)
   {
      return (Specification[]) protocolSpecifications.get(protocolID);
   }
   
   /** Returns an array of <CODE>Specification</CODE> objects that are held for
    * a specified local address.
    * @param addressID a <CODE>NodeAddressID</CODE> that should match an address on which a local
    * <CODE>LinkServer</CODE> is listening.
    * @return the set of specifications that are held against
    * the specified address.
    */
   public Specification[] getAddressSpecifications(NodeAddressID addressID)
   {
      return (Specification[]) addressSpecifications.get(addressID);
   }
   
   /** Installs a <CODE>LinkServer</CODE> listening on a specified
    * <CODE>NodeAddressID</CODE> and holds the specified set of
    * <CODE>Specification</CODE> objects against the address.
    * @param addressID the address on which the <CODE>LinkServer</CODE> should be started.
    * @param specifications the specifications to hold against the address.
    * @return <CODE>true</CODE> iff the server is successfully installed.
    */
   public boolean installProtocolServer(NodeAddressID addressID, Specification[] specifications)
   {
      if(addressID == null) 
         return false;
      synchronized(linkServers)
      {
         if(!linkServers.contains(addressID))
         {
            ProtocolID pID = addressID.getProtocolID();
            LinkServer ls = pID.startLinkServer(addressID);
            if(ls != null)
            {
               linkServers.put(addressID, ls);
               if(specifications != null)
                  addressSpecifications.put(addressID, specifications);
               
               //update Node with new address
               NodeID thisNode = Node.getInstance().getActualNode();
               synchronized(thisNode)
               {
                  //add the address taken from LinkServer in case it
                  //has been modified
                  thisNode.addAddress(ls.getLinkServerAddressID());
               }
               return true;
            }
         }
      }
      return false;
   }
   
   /** Stops the <CODE>LinkServer</CODE> that is listening on the specified address.
    * @param addressID the <CODE>NodeAddressID</CODE> on which the <CODE>LinkServer</CODE> to
    * stop is listening.
    * @return <CODE>true</CODE> if, after returning, no <CODE>LinkServer</CODE> is listening
    * on the specified adddress.
    */
   public boolean stopProtocolServer(NodeAddressID addressID)
   {
      if(addressID == null) 
         return false;
      if(linkServers.contains(addressID))
      {
         LinkServer ls = (LinkServer)linkServers.get(addressID);
         if(ls != null)
         {
            if(ls.stop())
            {
               linkServers.remove(addressID);
               addressSpecifications.remove(addressID);
               return true;
            }
         }
      }
      else
         return true;
      return false;
   }
   
   /** Installs a protocol client so that links can be established to Nodes with
    * <CODE>LinkServer</CODE> processes listening on the specified protocol.
    * @param protocolID The <CODE>ProtocolID</CODE> of the protocol to install.
    * @param specifications The specification of the protocol being installed.
    * @param settings a <CODE>HashTable</CODE> that can contain settings that are passed
    * to the protocol's <CODE>Builder</CODE>.
    * @return <CODE>true</CODE> iff the protocol client is successfully installed or
    *          has already been installed.
    */
   public boolean installProtocolClient(ProtocolID protocolID, Specification[] specifications, Hashtable settings)
   {
      if(protocolID == null) 
         return false;
      if(protocolClients.get(protocolID) != null) 
         return true;
      LinkFactory.Builder builder = protocolID.getLinkBuilder(settings);
      if(LinkFactory.getInstance().installBuilder(builder))
      {
         protocolClients.put(protocolID, builder);
         if(specifications != null)
            protocolSpecifications.put(protocolID, specifications);
         return true;
      }
      return false;
   }
   
   /** Removes the installed protocol client for a specified protocol.
    * @param protocolID the <CODE>ProtocolID</CODE> of the protocol client to remove.
    * @return <CODE>true</CODE> iff a matching protocol client has been successfully removed.
    */
   public boolean removeProtocolClient(ProtocolID protocolID)
   {
      if(protocolID == null) 
         return false;
      LinkFactory.Builder builder = (LinkFactory.Builder)protocolClients.get(protocolID);
      if(builder == null) 
         return false;
      
      if(LinkFactory.getInstance().removeBuilder(protocolID, builder))
      {
         protocolClients.remove(protocolID);
         protocolSpecifications.remove(protocolID);
         return true;
      }
      return false;
   }
   
   /*----------------------Attributes--------------------------------------------*/
   
   private Hashtable linkServers = new Hashtable();
   
   /**
    * This Hashtable contains protocolID's as the keys which map to
    * builders that build the links for that protocol.
    */
   private Hashtable protocolClients = new Hashtable();
   
   private Hashtable addressSpecifications = new Hashtable();
   
   private Hashtable protocolSpecifications = new Hashtable();
}