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

import org.jcsp.util.ChannelDataStore;

/**
 * A standard implementation of the JCSP.NET
 * Networked channel factory interaces.
 *
 * @author Quickstone Technologies Limited
 */
public class StandardNetChannelEndFactory
implements NetChannelEndFactory, NetLabelledChannelEndFactory, NetBufferedChannelEndFactory, NetLabelledBufferedChannelEndFactory
{
   private static StandardNetChannelEndFactory instance = new StandardNetChannelEndFactory();
   
   private final Profile linkProfile;
   
   public StandardNetChannelEndFactory()
   {
      linkProfile = null;
   }
   
   /**
    * Creates a factory that creates links using a given profile rather than the default one.
    */
   public StandardNetChannelEndFactory(Profile profile)
   {
      linkProfile = profile;
   }
   
   public static StandardNetChannelEndFactory getDefaultInstance()
   {
      return instance;
   }
   
   /**
    * @see org.jcsp.net.NetChannelEndFactory#createNet2One()
    */
   public NetAltingChannelInput createNet2One()
   {
      return Net2OneChannel.create();
   }
   
   /**
    * @see org.jcsp.net.NetLabelledChannelEndFactory#createNet2One(String)
    */
   public NetAltingChannelInput createNet2One(String label)
   {
      return Net2OneChannel.create(label);
   }
   
   /**
    * @see org.jcsp.net.NetBufferedChannelEndFactory#createNet2One(ChannelDataStore)
    */
   public NetAltingChannelInput createNet2One(ChannelDataStore buffer)
   {
      return Net2OneChannel.create(buffer);
   }
   
   /**
    * @see org.jcsp.net.NetLabelledBufferedChannelEndFactory#createNet2One(String, ChannelDataStore)
    */
   public NetAltingChannelInput createNet2One(String label, ChannelDataStore buffer)
   {
      return Net2OneChannel.create(label, buffer);
   }
   
   /**
    * @see org.jcsp.net.NetChannelEndFactory#createNet2Any()
    */
   public NetSharedChannelInput createNet2Any()
   {
      return new Net2AnyChannel();
   }
   
   /**
    * @see org.jcsp.net.NetLabelledChannelEndFactory#createNet2Any(String)
    */
   public NetSharedChannelInput createNet2Any(String label)
   {
      return new Net2AnyChannel(label);
   }
   
   /**
    * @see org.jcsp.net.NetBufferedChannelEndFactory#createNet2Any(ChannelDataStore)
    */
   public NetSharedChannelInput createNet2Any(ChannelDataStore buffer)
   {
      return new Net2AnyChannel(buffer);
   }
   
   /**
    * @see org.jcsp.net.NetLabelledBufferedChannelEndFactory#createNet2Any(String, ChannelDataStore)
    */
   public NetSharedChannelInput createNet2Any(String label, ChannelDataStore buffer)
   {
      return new Net2AnyChannel(label, buffer);
   }
   
   /**
    * @see org.jcsp.net.NetChannelEndFactory#createOne2Net(NetChannelLocation)
    */
   public NetChannelOutput createOne2Net(NetChannelLocation loc)
   {
      return new One2NetChannel(loc, linkProfile);
   }
   
   /**
    * @see org.jcsp.net.NetChannelEndFactory#createAny2Net(NetChannelLocation)
    */
   public NetSharedChannelOutput createAny2Net(NetChannelLocation loc)
   {
      return new Any2NetChannel(loc, linkProfile);
   }
}