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
import org.jcsp.lang.*;
import org.jcsp.net.*;

/**
 * Wraps an object when it is being sent over a channel by the <code>DataSerializationFilter</code> so
 * that it includes a <code>NetChannelLocation</code> referring to the JFTP request channel to service
 * requests for the class's binary image if it is not held at the receiving end.
 *
 * @author Quickstone Technologies Limited
 */
class DynamicClassLoaderMessage implements Serializable
{
   /**
    * Creates a new <code>DynamicClassLoaderMessage</code> encapsulating the given object.
    *
    * @param data the actual user object being sent.
    * @param classSourceChannelLoc the request channel of the JFTP process.
    */
   public DynamicClassLoaderMessage(Object data, NetChannelLocation classSourceChannelLoc)
   throws NotSerializableException, IOException
   {
      this.classSourceChannelLoc = classSourceChannelLoc;
      this.createTime = System.currentTimeMillis();
      this.serializedData = new SerializedData(data,false);
   }
   
   /**
    * Creation timestamp.
    */
   private long createTime;
   
   /**
    * Diagnostic string identifying the message by timestamp.
    */
   public String toString()
   {
      return "DynamicClassLoaderMessage " + createTime;
   }
   
   /**
    * Public accessor to deserialize and retrieve the object using the specified
    * <code>ClassManager</code>.
    *
    * @param cm the class manager to use.
    * @return the user object passed in this message.
    */
   public Object get(ClassManager cm) throws ClassNotFoundException, IOException
   {
      //JA - probably don't want to create a new factory each time
      if (cm != null)
      {
         Object g =  serializedData.get(new AdvancedInputStreamFactory(cm));
         return g;
      }
      return serializedData.get();
   }
   
   /**
    * Sets the class loader to use when deserializing the object.
    *
    * @param classLoader the new class loader.
    */
   public void setClassLoader(ClassLoader classLoader)
   {
      this.classLoaderToUse = classLoader;
   }
   
   /**
    * The class loader to use when deserializing the object. This is not sent as part of the message -
    * it is for use by methods invoked by the receiver.
    */
   private transient ClassLoader classLoaderToUse = null;
   
   /**
    * The location of the request channel of the sender's JFTP process.
    */
   private final NetChannelLocation classSourceChannelLoc;
   
   /**
    * The serialized form of the user object being sent.
    */
   private SerializedData serializedData;
   
   /**
    * This allows a custom ClassLoader to be used to resolve the object
    * being deserialized.
    *
    * @author Jo Aldous
    */
   private class AdvancedObjectInputStream extends ObjectInputStream
   {
      /**
       * Constructs a new <code>AdvancedObjectInputStream</code>.
       *
       * @param cm the class manager process at this node.
       * @param in the underlying input stream to read data from.
       */
      public AdvancedObjectInputStream(ClassManager cm, InputStream in) throws IOException
      {
         super(in);
         this.cm = cm;
      }
      
      /**
       * Attempts to resolve the class requested using the class manager. If this fails an exception
       * is raised.
       *
       * @param v indicates the class to resolve.
       * @throws IOException if there is a problem with the underlying stream.
       * @throws ClassNotFoundException if the class could not be resolved.
       * @return the resolved class.
       */
      protected Class resolveClass(ObjectStreamClass v) throws IOException, ClassNotFoundException
      {
         One2OneChannel in = Channel.one2one();
         cm.getClass(v.getName(), classSourceChannelLoc, in.out());
         Object reply = in.in().read();
         if(reply instanceof Class)
            return (Class) reply;
         throw new ClassNotFoundException(v.getName());
      }
      
      /**
       * The class manager to use when resolving a class.
       */
      private ClassManager cm;
   }
   
   /**
    * Factory for creating instances of <code>AdvancedObjectInputStream</code> bound to a given
    * class manager.
    */
   private class AdvancedInputStreamFactory implements SerializedData.InputStreamFactory
   {
      /**
       * Constructs a new factory for the given class manager. All streams created by this factory
       * will be associated with this class manager.
       *
       * @param cm class manager to associate will all created streams.
       */
      AdvancedInputStreamFactory(ClassManager cm)
      {
         this.cm = cm;
      }
      
      /**
       * Creates a new input stream.
       *
       * @param in the underlying input stream.
       */
      public ObjectInput create(InputStream in) throws IOException
      {
         return new AdvancedObjectInputStream(cm, in);
      }
      
      /**
       * The class manager to associate with all created input streams.
       */
      private ClassManager cm;
   }
}