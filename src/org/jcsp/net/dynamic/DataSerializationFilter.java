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

package org.jcsp.net.dynamic;

import java.io.*;
import org.jcsp.net.*;
import org.jcsp.util.filter.*;

/**
 * <p>A filter to be plugged into the sending end of a channel if dynamic class transfer is to be supported
 * over the channel. The receiving end of the channel should have a <code>DeserializeChannelFilter</code>
 * plugged in. Any objects send by this filter will be wrapped in a <code>DynamicClassLoaderMessage</code>
 * object which includes the <code>NetChannelLocation</code> of a channel for the local node's JFTP
 * service.</p>
 *
 * <p>Instances of this class will be created by the <code>DynamicClassLoader</code> service and should
 * be obtained via its <code>getTxFilter</code> method.</p>
 *
 * @author Quickstone Technologies Limited
 */
class DataSerializationFilter implements Filter
{
   /**
    * Constructs a new <code>DataSerializationFilter</code> object.
    *
    * @param senderLoc the location of the JFTP service's request channel.
    */
   //public DataSerializationFilter(ClassManager cm, NetChannelLocation senderLoc) {
   public DataSerializationFilter(NetChannelLocation senderLoc)
   {
      this.senderLoc = senderLoc;
   }
   
   /**
    * Wraps the object in a <code>DynamicClassLoaderMessage</code> complete with the JFTP channel
    * location passed to the filter's constructor.
    *
    * @param obj the object to wrap up
    * @return the wrapped object
    */
   public Object filter(Object obj)
   {
      try
      {
         obj = new DynamicClassLoaderMessage(obj, senderLoc);
      }
      catch (NotSerializableException e)
      {
         throw new RuntimeException("Unable to serialize " + obj);
      }
      catch (IOException e)
      {
         throw new RuntimeException("IOException while trying to serialize " + obj);
      }
      return obj;
   }
   
   /**
    * Location of the JFTP service's request channel.
    */
   private NetChannelLocation senderLoc;
}