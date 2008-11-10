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
 * Instances of this class take two <code>NetConnectionLocation</code>
 * objects. One for a connection's server channel and one for a
 * connection's further request channel.
 *
 * Instances of this class can be used as the open channel's
 * <code>NetChannelLocation</code> object while the further request
 * channel's can be obtained by calling
 * <code>getRequestChannelLocation()</code>.
 *
 * @author Quickstone Technologies Limited
 */
class NetConnectionLocation extends NetChannelLocation
{
   private NetChannelLocation reqLoc;
   
   /**
    * Constructor for NetConnectionLocation.
    * @param other
    * @throws IllegalArgumentException
    */
   public NetConnectionLocation(NetChannelLocation open, NetChannelLocation req) throws IllegalArgumentException
   {
      super(open);
      this.reqLoc = req;
   }
   
   public NetChannelLocation getRequestChannelLocation()
   {
      return this.reqLoc;
   }
}