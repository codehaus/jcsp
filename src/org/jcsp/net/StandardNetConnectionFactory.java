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
 * <p>
 * This is an implementation of <code>{@link NetConnectionFactory}</code>.
 * </p>
 *
 * @author Quickstone Technologies Limited
 */
public class StandardNetConnectionFactory implements NetConnectionFactory
{
   /**
    * Constructor for StandardNetConnectionFactory.
    */
   public StandardNetConnectionFactory()
   {
      super();
   }
   
   /**
    * @see org.jcsp.net.NetConnectionFactory#createNet2One()
    */
   public NetAltingConnectionServer createNet2One()
   {
      return NetAltingConnectionServer.create();
   }
   
   /**
    * @see org.jcsp.net.NetConnectionFactory#createNet2Any()
    */
   public NetSharedConnectionServer createNet2Any()
   {
      return NetSharedConnectionServerImpl.create();
   }
   
   /**
    * @see org.jcsp.net.NetConnectionFactory#createOne2Net(NetChannelLocation)
    */
   public NetAltingConnectionClient createOne2Net(NetChannelLocation serverLoc)
   {
      return NetAltingConnectionClient.create(serverLoc);
   }
   
   /**
    * @see org.jcsp.net.NetConnectionFactory#createAny2Net(NetChannelLocation)
    */
   public NetSharedAltingConnectionClient createAny2Net(NetChannelLocation serverLoc)
   {
      return NetSharedAltingConnectionClient.create(serverLoc);
   }
}