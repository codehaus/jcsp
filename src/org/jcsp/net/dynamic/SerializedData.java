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

/**
 * Serialized form of an object as as part of a <code>DynamicClassLoaderMessage</code>. The object is
 * serialized to a byte array for transfer to another node. This will allow the message to be
 * deserialized to obtain the JFTP reference even if the class held here is not available. When the
 * relevant class has been loaded the data from the byte array can be deserialized.
 *
 * @author Quickstone Technologies Limited
 */

class SerializedData implements Externalizable
{
   /*-------------------Constructors---------------------------------------------*/
   
   /**
    * This is the public used constructor. It takes an object and attempts
    * to serialize it.
    *
    * @param obj	the Object to Serialize.
    * @param storeToString if <code>true</code> will create a string representation of the serialized object.
    * @throws	NotSerializableException	If obj is not Serializable.
    * @throws	IOException		if an IO error occurs during Serialization,
    *							should not happen unless there is a bug.
    */
   
   public SerializedData(Object obj, boolean storeToString) throws NotSerializableException, IOException
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
   SerializedData(byte[] data, String objectToString)
   {
      this.serializedData = data;
      this.objectToString = objectToString;
   }
   
   /*-------------------Public Methods-------------------------------------------*/
   
   /**
    * Returns the binary serialized object.
    */
   public byte[] getSerializedData()
   {
      return serializedData;
   }
   
   /**
    * Deserializes the object from the byte[] array and returns it.
    *
    * @throws ClassNotFoundException if the class is not available locally and should be requested.
    * @throws IOException if there is a problem with the stream.
    */
   public Object get() throws ClassNotFoundException, IOException
   {
      return get(new BasicInputStreamFactory());
   }
   
   /**
    * Deserializes the object from the byte[] array and returns it.
    *
    * @param factory the factory for creating the input stream.
    * @throws ClassNotFoundException if the class is not available locally and should be requested.
    * @throws IOException if there is a problem with the stream.
    */
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
   
   /**
    * Returns the string form of an object.
    */
   public String getObjectToString()
   {
      if(objectToString == null) 
         return "";
      return objectToString;
   }
   
   public void writeExternal(ObjectOutput out) throws IOException
   {
   }
   
   public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException
   {
   }
   
   /**
    * Replaces this object with another during serialization.
    *
    * @return the replacement.
    */
   public Object writeReplace() throws ObjectStreamException
   {
      ExtClass e = new ExtClass();
      e.serializedData = serializedData;
      e.objectToString = objectToString;
      return e;
   }
   
   /*-------------------Attributes-----------------------------------------------*/
   
   /**
    * The binary form of the object in this message.
    */
   private byte[] serializedData;
   
   /**
    * The string representation of the object.
    */
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
   
   /**
    * Factory for creating object input streams.
    *
    * @author Quickstone Technologies Limited
    */
   public interface InputStreamFactory
   {
      /**
       * Creates an object input stream based on the given input stream.
       */
      public ObjectInput create(InputStream in) throws IOException;
   }
   
   /**
    * Implementation of the <code>InputStreamFactory</code> to create a <code>ObjectInputStream</code>.
    *
    * @author Quickstone Technologies Limited
    */
   private class BasicInputStreamFactory implements InputStreamFactory
   {
      /**
       * Creates an <code>ObjectInputStream</code> over the top of the given input stream.
       */
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
      /**
       * Constructs a new instance of this class.
       */
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
         if (arrayLength > 0)
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
         return new SerializedData(serializedData, objectToString);
      }
      
      /**
       * Binary serialized form of the object.
       */
      public byte[] serializedData;
      
      /**
       * String representation of the object.
       */
      public String objectToString = null;
   }
}