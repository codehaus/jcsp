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

class SerializedObject implements Externalizable
{
   
   /*-------------------Constructors---------------------------------------------*/
   
   /**
    * This is the public used constructor. It takes an object and attempts
    * to serialize it.
    *
    * @parma obj	the Object to Serialize.
    * @throws	NotSerializableException	If obj is not Serializable.
    * @throws	IOException		if an IO error occurs during Serialization,
    *							should not happen unless there is a bug.
    */
   
   public SerializedObject(Object obj, boolean storeToString) throws NotSerializableException, IOException
   {
      if(obj == null)
      {
         serializedData = null;
         if (storeToString) 
            objectToString = "null";
         return;
      }
      if (storeToString) 
         objectToString = obj.toString();
      
      AccesibleByteArrayOutputStream serializedDataDestination = new AccesibleByteArrayOutputStream();
      ObjectOutputStream objOut = new ObjectOutputStream(serializedDataDestination);
      
      objOut.writeObject(obj);
      objOut.flush();
      serializedData = serializedDataDestination.getByteArray();
      try
      {
         serializedDataDestination.close();
         objOut.close();
      }
      catch(IOException e)
      {
         e.printStackTrace();
      }
   }
   
   /**
    * A private constructor used during the deserialization process of this
    * object. Externalizable objects require a no-arg constructor so a
    * replacement object is serialized in this object's place. On
    * deserialization, this object is reconstructed using this constructor.
    *
    * @param	data	a byte[] containing the serialized data of the object
    *					that this object is holding.
    * @param	objectToString	The toString value of the stored object.
    */
   SerializedObject(byte[] data, String objectToString)
   {
      this.serializedData = data;
      this.objectToString = objectToString;
   }
   
   /*-------------------Public Methods-------------------------------------------*/
   
   public byte[] getSerializedData()
   {
      return serializedData;
   }
   
   public Object get() throws ClassNotFoundException, IOException
   {
      return get(new BasicInputStreamFactory());
   }
   
   public Object get(InputStreamFactory factory) throws ClassNotFoundException, IOException
   {
      if (serializedData == null) 
         return null;
      ByteArrayInputStream serializedDataSource = new ByteArrayInputStream(serializedData);
      ObjectInput objIn = factory.create(serializedDataSource);
      Object obj = objIn.readObject();
      try
      {
         serializedDataSource.close();
         objIn.close();
      }
      catch(IOException e)
      {
         e.printStackTrace();
      }
      return obj;
   }
  
   public String getObjectToString()
   {
      if (objectToString == null) 
         return "";
      return objectToString;
   }
   
   public void writeExternal(ObjectOutput out) throws IOException
   {
   }
   
   public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException
   {
   }
   
   public Object writeReplace() throws ObjectStreamException
   {
      ExtClass e = new ExtClass();
      e.serializedData = serializedData;
      e.objectToString = objectToString;
      return e;
   }
   
   /*-------------------Attributes-----------------------------------------------*/
   
   private byte[] serializedData;
   
   private String objectToString = null;
   
   /*-------------------Inner Classes--------------------------------------------*/
   
   /**
    * This class exists purely for performance reasons. It provides an
    * accessor to the internal byte[] and means that it does not need to
    * be copied.
    */
   private static class AccesibleByteArrayOutputStream extends ByteArrayOutputStream
   {
      
      public byte[] getByteArray()
      {
         return buf;
      }
   }
   
   public interface InputStreamFactory
   {
      public ObjectInput create(InputStream in) throws IOException;
   }
   
   private class BasicInputStreamFactory implements InputStreamFactory
   {
      public ObjectInput create(InputStream in) throws IOException
      {
         return new ObjectInputStream(in);
      }
   }
   
   /**
    * This class exists because the main class cannot have a no-arg
    * constructor as required by externalizable.
    *
    * On serialization, this object replaces the SerializedObject.
    */
   
   private static class ExtClass implements Externalizable
   {
      public ExtClass()
      {}
      
      public void writeExternal(ObjectOutput out) throws IOException
      {
         if(serializedData == null)
            out.writeInt(0);
         else
         {
            out.writeInt(serializedData.length);
            out.write(serializedData, 0, serializedData.length);
         }
         out.writeObject(objectToString);
      }
      
      public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException
      {
         int arrayLength = in.readInt();
         if(arrayLength > 0)
         {
            serializedData = new byte[arrayLength];
            in.readFully(serializedData);
         }
         else
            serializedData = null;
         objectToString = (String)in.readObject();
      }
      
      public Object readResolve() throws ObjectStreamException
      {
         return new SerializedObject(serializedData, objectToString);
      }
      
      public byte[] serializedData;
      public String objectToString = null;
   }
}