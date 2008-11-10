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

import java.io.*;

/**
 *
 * @author Quickstone Technologies Limited.
 */
class SerializedMessage extends Message implements Serializable
{
   public SerializedMessage(Message msg) throws NotSerializableException, IOException
   {
      serializedObject = new SerializedObject(msg, false);
      destIndex = msg.destIndex;
      sourceIndex = msg.sourceIndex;
   }
   
   SerializedMessage(SerializedObject serializedObject, long destIndex, long sourceIndex)
   {
      this.serializedObject = serializedObject;
      this.destIndex = destIndex;
      this.sourceIndex = sourceIndex;
   }
   
   public byte[] getSerializedData()
   {
      return serializedObject.getSerializedData();
   }
   
   /*--------------------------*/
   
   public Object get() throws ClassNotFoundException, IOException
   {
      return get(null);
   }
   
   public Object get(SerializedObject.InputStreamFactory factory) throws ClassNotFoundException, IOException
   {
      Object o = null;
      if(factory == null)
         o = serializedObject.get();
      else
         o = serializedObject.get(factory);
      
      if(o instanceof Message)
      {
         Message msg = (Message) o;
         msg.sourceID = sourceID;
         msg.txReplyChannel = txReplyChannel;
      }
      return o;
   }
   
   private SerializedObject serializedObject;
   
   private boolean isInternalClass;
   
   private static class AccesibleByteArrayOutputStream extends ByteArrayOutputStream
   {
      public byte[] getByteArray()
      {
         return buf;
      }
   }
}