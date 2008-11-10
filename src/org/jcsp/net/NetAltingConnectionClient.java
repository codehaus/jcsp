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

import org.jcsp.lang.AltingConnectionClientImpl;

/**
 * <p>
 * Instances of this class are <code>AltingConnectionClient</code>
 * objects which connect to <code>ConnectionServer</code> objects
 * over a JCSP.NET network.
 * </p>
 * <p>
 * Instances of this class are not guaranteed to be safe to use
 * by muliple concurrent processes. See
 * <code>{@link NetSharedAltingConnectionClient}</code>.
 * </p>
 * <p>
 * Instances can be constructed by using a
 * <code>{@link NetConnectionFactory}</code> or by
 * using the <code>{@link NetConnection}</code> class.
 * </p>
 *
 * @author Quickstone Technologies Limited
 */
public class NetAltingConnectionClient extends AltingConnectionClientImpl implements NetConnectionClient
{
   
   static NetAltingConnectionClient create(NetChannelLocation serverLoc)
   {
      NetChannelOutput openToServer = new One2NetChannel(serverLoc, false);
      NetChannelOutput reqToServer = new One2NetChannel(serverLoc, false);
      NetAltingChannelInput fromServer = NetChannelEnd.createNet2One();
      NetChannelOutput replyToClient = new One2NetChannel(fromServer.getChannelLocation(), false);
      return new NetAltingConnectionClient(fromServer, openToServer, reqToServer, replyToClient);
   }
   
   private NetAltingChannelInput fromServer;
   private NetConnectionLocation location;
   
   NetAltingConnectionClient(NetAltingChannelInput fromServer, NetChannelOutput openToServer, 
           NetChannelOutput reqToServer, NetChannelOutput backToClient)
   {
      super(fromServer, openToServer, reqToServer, backToClient);
      this.fromServer = fromServer;
      this.location = new NetConnectionLocation(openToServer.getChannelLocation(), reqToServer.getChannelLocation());
   }
   
   /**
    * Returns the location of the server.
    *
    * @return the server's <code>NetChannelLocation</code>
    *          object.
    */
   public NetChannelLocation getChannelLocation()
   {
      return this.location;
   }
   
   /**
    * Destroys the client and frees any resources used
    * in the JCSP.NET infrastructure.
    *
    */
   public void destroyClient()
   {
      fromServer.destroyReader();
   }
}