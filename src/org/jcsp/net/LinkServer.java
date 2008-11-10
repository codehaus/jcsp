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

/**
 * An abstract class that must be implemented by communication
 * protocol implementations. <CODE>LinkServer</CODE> objects are
 * processes which listen on a certain address for connection
 * requests. When a request is received, a <CODE>Link</CODE> should be spawned in
 * server mode.
 *
 * @author Quickstone Technologies Limited
 */
public abstract class LinkServer
{
   /**
    * Constructor. A LinkServer must have an associated protocolID.
    * @param protocolID The <CODE>ProtocolID</CODE> for the protocol that the concrete implementation of <CODE>LinkServer</CODE> supports.
    * @param linkServerAddressID the <CODE>NodeAddressID</CODE> for this <CODE>LinkServer</CODE> to listen on.
    */
   protected LinkServer(ProtocolID protocolID, NodeAddressID linkServerAddressID)
   {
      if (protocolID == null || linkServerAddressID == null)
         throw new IllegalArgumentException("ProtocolID cannot be null");
      this.protocolID = protocolID;
      this.linkServerAddressID = linkServerAddressID;
   }
   
   /**
    * Create a server on a specifiedNodeAddressID, and start it.  The server
    * is spawned off in parallel, so this call returns immediately. This needs
    * to be implemented by the concrete implementation of this class. This is
    * not enforced by the compiler due to this being a static method.
    *
    * This NEEDS to be overridden.
    *
    * @param addressID The NodeAddressID to accept from
    * @return the instance of <CODE>LinkServer</CODE>.
    */
   protected static LinkServer create(NodeAddressID addressID)
   {
      throw new UnsupportedOperationException();
   }
   
   /**
    * Stops the LinkServer.
    *
    * This NEEDS to be overridden.
    * @return <CODE>true</CODE> iff the <CODE>LinkServer</CODE> has stopped.
    */
   protected boolean stop()
   {
      throw new UnsupportedOperationException();
   }
   
   /**
    *	Gets the protocol that this LinkServer supports.
    *
    * @return	the ProtocolID representing this LinkServers protocol.
    */
   protected final ProtocolID getProtocolID()
   {
      return protocolID;
   }
   
   /**
    * Protected accessor for obtaining the <CODE>NodeAddressID</CODE> on which
    * this server is listening.
    * @return the <CODE>NodeAddressID</CODE> on which this server is listening.
    */
   protected final NodeAddressID getLinkServerAddressID()
   {
      return linkServerAddressID;
   }
   
   private final ProtocolID protocolID;
   private final NodeAddressID linkServerAddressID;
   
}