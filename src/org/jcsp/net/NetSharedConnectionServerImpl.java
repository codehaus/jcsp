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
 * @author Quickstone Technologies Limited
 */
class NetSharedConnectionServerImpl extends SharedConnectionServerImpl implements NetSharedConnectionServer
{
   static NetSharedConnectionServerImpl create()
   {
      NetAltingChannelInput in = NetChannelEnd.createNet2One();
      Any2OneChannel synchChan = Channel.any2one(new Buffer(1));
      return new NetSharedConnectionServerImpl(synchChan, in);
   }
   
   private Any2OneChannel synchChan;
   private NetAltingChannelInput in;
   
   /**
    * Constructor for NetSharedConnectionServerImpl.
    */
   private NetSharedConnectionServerImpl(Any2OneChannel synchChan, NetAltingChannelInput in)
   {
      super(in,in,synchChan.in(),synchChan.out(),null);
      this.synchChan = synchChan;
      this.in = in;
   }
   
   /**
    * Returns the server's location.
    *
    * @return the server's <code>NetChannelLocation</code> object.
    *
    * @see org.jcsp.net.Networked#getChannelLocation()
    */
   public NetChannelLocation getChannelLocation()
   {
      return in.getChannelLocation();
   }
   
   /**
    * <p>
    * Produces a duplicate
    * <code>SharedConnectionServer</code> object which
    * may be used by another process.
    * </p>
    * @return a new duplicate <code>SharedConnectionServer</code>
    *          object.
    */
   public SharedConnectionServer duplicate()
   {
      return new NetSharedConnectionServerImpl(synchChan, in);
   }
   
   /**
    * Destroys the server and frees any resources used within
    * the JCSP.NET infrastructure.
    *
    */
   public void destroyServer()
   {
      synchChan.out().write(null);
      in.destroyReader();
      synchChan.in().read();
   }
}