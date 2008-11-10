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
 * <p>
 * This class provides static factory methods for constructing
 * Networked channel ends.
 * </p>
 * <p>
 * The methods are equivalent to the methods defined in
 * <code>{@link NetChannelEndFactory}</code>.
 * </p>
 *
 * @author Quickstone Technologies Limited
 */
public class NetChannelEnd
{
   
   private static StandardNetChannelEndFactory factory = StandardNetChannelEndFactory.getDefaultInstance();
   
   private NetChannelEnd()
   {
      //private constructor to prevent instantiation
   }
   
   /**
    * @see org.jcsp.net.NetChannelEndFactory#createNet2One()
    */
   public static NetAltingChannelInput createNet2One()
   {
      return factory.createNet2One();
   }
   
   /**
    * @see org.jcsp.net.NetLabelledChannelEndFactory#createNet2One(String)
    */
   public static NetAltingChannelInput createNet2One(String label)
   {
      return factory.createNet2One(label);
   }
   
   /**
    * @see org.jcsp.net.NetBufferedChannelEndFactory#createNet2One(ChannelDataStore)
    */
   public static NetAltingChannelInput createNet2One(ChannelDataStore buffer)
   {
      return factory.createNet2One(buffer);
   }
   
   /**
    * @see org.jcsp.net.NetLabelledBufferedChannelEndFactory#createNet2One(String, ChannelDataStore)
    */
   public static NetAltingChannelInput createNet2One(String label, ChannelDataStore buffer)
   {
      return factory.createNet2One(label, buffer);
   }
   
   /**
    * @see org.jcsp.net.NetChannelEndFactory#createNet2Any()
    */
   public static NetSharedChannelInput createNet2Any()
   {
      return factory.createNet2Any();
   }
   
   /**
    * @see org.jcsp.net.NetLabelledChannelEndFactory#createNet2Any(String)
    */
   public static NetSharedChannelInput createNet2Any(String label)
   {
      return factory.createNet2Any(label);
   }
   
   /**
    * @see org.jcsp.net.NetBufferedChannelEndFactory#createNet2Any(ChannelDataStore)
    */
   public static NetSharedChannelInput createNet2Any(ChannelDataStore buffer)
   {
      return factory.createNet2Any(buffer);
   }
   
   /**
    * @see org.jcsp.net.NetLabelledBufferedChannelEndFactory#createNet2Any(String, ChannelDataStore)
    */
   public static NetSharedChannelInput createNet2Any(String label, ChannelDataStore buffer)
   {
      return factory.createNet2Any(label, buffer);
   }
   
   /**
    * @see org.jcsp.net.NetChannelEndFactory#createOne2Net(NetChannelLocation)
    */
   public static NetChannelOutput createOne2Net(NetChannelLocation loc)
   {
      return factory.createOne2Net(loc);
   }
   
   /**
    * @see org.jcsp.net.NetChannelEndFactory#createAny2Net(NetChannelLocation)
    */
   public static NetSharedChannelOutput createAny2Net(NetChannelLocation loc)
   {
      return factory.createAny2Net(loc);
   }
}