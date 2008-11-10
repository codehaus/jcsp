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
import org.jcsp.util.Buffer;

/**
 * <p>
 * Defines a class whose instances should be
 * <code>{@link org.jcsp.lang.SharedAltingConnectionClient}</code>
 * that connect to a <code>ConnectionServer</code> over a JCSP.NET
 * network.
 * </p>
 * <p>
 * Individual instances may not be used by multiple processes but
 * duplicate clients can be obtained by invoking
 * <code>{@link #duplicate()}</code>. These duplicates work over the
 * same connection and each one may be used by a different process.
 * </p>
 *
 * @author Quickstone Technologies Limited
 */
public class NetSharedAltingConnectionClient extends SharedAltingConnectionClient implements NetSharedConnectionClient
{
   static NetSharedAltingConnectionClient create(NetChannelLocation serverLoc)
   {
      Any2OneChannel synchChan = Channel.any2one(new Buffer(1));
      NetChannelOutput openToServer = NetChannelEnd.createOne2Net(serverLoc);
      NetChannelOutput reqToServer = NetChannelEnd.createOne2Net(serverLoc);
      NetAltingChannelInput fromServer = NetChannelEnd.createNet2One(new Buffer(1));
      NetChannelOutput replyToClient = NetChannelEnd.createOne2Net(fromServer.getChannelLocation());
      return new NetSharedAltingConnectionClient(synchChan, fromServer, openToServer, reqToServer, replyToClient);
   }
   
   private Any2OneChannel synchChan;
   private NetAltingChannelInput fromServer;
   private NetChannelOutput openToServer;
   private NetChannelOutput reqToServer;
   private NetChannelOutput backToClient;
   private NetConnectionLocation serverLocation;
   
   /**
    * <p>
    * Constructor for NetSharedAltingConnectionClient.
    * </p>
    *
    * @param fromServer
    * @param synchIn
    * @param toServer
    * @param synchOut
    * @param backToClient
    * @param parent
    */
   protected NetSharedAltingConnectionClient(
                        Any2OneChannel synchChan,
                        NetAltingChannelInput fromServer,
                        NetChannelOutput openToServer,
                        NetChannelOutput reqToServer,
                        NetChannelOutput backToClient)
   {
      super(fromServer, synchChan.in(), openToServer, reqToServer, synchChan.out(), backToClient, null);
      
      this.synchChan = synchChan;
      this.fromServer = fromServer;
      this.openToServer = openToServer;
      this.reqToServer = reqToServer;
      this.backToClient = backToClient;
      
      this.serverLocation = new NetConnectionLocation(openToServer.getChannelLocation(), reqToServer.getChannelLocation());
   }
   
   /**
    * Returns the address location of the connection server.
    *
    * @return the <code>NetChannelLocation</code> object.
    *
    * @see org.jcsp.net.Networked#getChannelLocation()
    */
   public NetChannelLocation getChannelLocation()
   {
      return this.serverLocation;
   }
   
   /**
    * <p>
    * Produces a duplicate
    * <code>NetSharedAltingConnectionClient</code> object which
    * may be used by another process.
    * </p>
    * @return a new duplicate <code>SharedConnectionClient</code>
    *          object.
    */
   public SharedConnectionClient duplicate()
   {
      return new NetSharedAltingConnectionClient(synchChan,
              fromServer,
              openToServer,
              reqToServer,
              backToClient);
   }
   
   /**
    * <p>
    * Destroys this networked client object.
    * </p>
    * <p>
    * This frees any resources used within the JCSP.NET
    * infrastructure.
    * </p>
    *
    */
   public void destroyClient()
   {
      synchChan.out().write(null);
      fromServer.destroyReader();
      synchChan.in().read();
   }
}