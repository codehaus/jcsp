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

package org.jcsp.net.cns;

import java.util.*;
import java.io.*;

/**
 * Instances of this hold a key for a particular channel.
 * <CODE>ChannelNameKey</CODE> objects are issued by the channel
 * name server.  They must be supplied to the channel name server
 * along with any channel management requests (channel deregistration,
 * relocation etc.).
 *
 * This class has no public constructor.
 *
 * @author Quickstone Technologies Limited
 */
public final class ChannelNameKey implements Serializable
{
   private int hashCode;
   private final long val;
   private final long key;
   
   ChannelNameKey(long val)
   {
      this.val = val;
      Random rnd = new Random(System.currentTimeMillis() - Runtime.getRuntime().freeMemory() + val);
      key = rnd.nextLong();
      hashCode = new Long(val).hashCode();
   }
   
   /**
    * Compares another <CODE>Object</CODE> with this
    * <CODE>ChannelNameKey</CODE>.
    *
    * @param o the other <CODE>Object</CODE> to compare with
    *           this <CODE>Object</CODE>.
    * @return <CODE>true</CODE> iff <CODE>o</CODE> is a non-null
    *          <CODE>ChannelNameKey</CODE> object which holds the
    *          same key as this object.
    */
   public boolean equals(Object o)
   {
      if (o == null || !(o instanceof ChannelNameKey))
         return false;
      ChannelNameKey other = (ChannelNameKey) o;
      return val == other.val && key == other.key;
   }
   
   /**
    * Returns an <CODE>int</CODE> hash code for this object.
    *
    * @return an <CODE>int</CODE> hash code.
    */
   public int hashCode()
   {
      return hashCode;
   }
}