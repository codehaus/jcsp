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

/**
 * <p>
 * Instances of this class are <code>AltingConnectionServer</code>
 * objects which allow connections from <code>ConnectionClient</code>
 * objects from over a JCSP.NET network.
 * </p>
 * <p>
 * Instances of this class are not guaranteed to be safe to use
 * by muliple concurrent processes. See
 * <code>{@link NetSharedConnectionServer}</code> for a server
 * class that may be used between multiple processes, however
 * this may not be ALTed over.
 * </p>
 * <p>
 * Instances can be constructed by using a
 * <code>{@link NetConnectionFactory}</code> or by
 * using the <code>{@link NetConnection}</code> class.
 * </p>
 *
 * @author Quickstone Technologies Limited
 */
class NetAltingConnectionServer extends AltingConnectionServerImpl implements NetConnectionServer
{
   static NetAltingConnectionServer create()
   {
      //buffering done at sending end
      NetAltingChannelInput chan = NetChannelEnd.createNet2One();
      return new NetAltingConnectionServer(chan);
   }
   
   private NetAltingChannelInput chan;
   
   private NetAltingConnectionServer(NetAltingChannelInput chan)
   {
      super(chan, chan);
      this.chan = chan;
   }
   
   /**
    * Returns the server's location.
    *
    * @return the server's <code>NetChannelLocation</code>
    *          object.
    */
   public NetChannelLocation getChannelLocation()
   {
      return chan.getChannelLocation();
   }
   
   /**
    * Destroys the server and frees any resources used
    * in the JCSP.NET infrastructure.
    */
   public void destroyServer()
   {
      chan.destroyReader();
   }
}