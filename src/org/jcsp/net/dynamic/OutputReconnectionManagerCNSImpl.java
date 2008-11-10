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
import org.jcsp.net.cns.*;

/**
 * Implementation of the output reconnection manager that uses a CNS service to track channel ends.
 * If this class is used for the output end, <code>InputReconnectionManagerCNSImpl</code> should be
 * used at the input end.
 *
 * @author Quickstone Technologies Limited
 */

/* NOTE:
 * The reconnection manager can be created with a cnsServiceName for channels that do not use the
 * default CNS service. For channels using the default CNS service, cnsServiceName should be null
 * and not set to the default service name. Any code in this class requiring behaviours from the CNS
 * service should check the value of this attribute and call the default CNS directly if it is null
 * and resolve the service name otherwise. This gives an efficiency gain.
 *
 * The CNS service name is tracked so that channels registered with a non-default CNS under one JVM
 * will not become mapped to the default CNS under peer JVMs following communication of a channel end.
 *
 * If the user passes an invalid service name to the constructor, an exception will only be raised when
 * a channel movement occurs (a NullPointerException). A possible modification would be to look up the
 * service name at construction time and raise an exception if it does not exist.
 *
 * Doesn't work for non-acknowledged channels. Either warn users of this or provide factory methods
 * for migratable channels that ensures the local writing end is acknowledged.
 */
class OutputReconnectionManagerCNSImpl implements OutputReconnectionManager
{
   /**
    * Underlying networked channel output end.
    */
   private transient NetChannelOutput chanOut;
   
   /**
    * Constant specifying the maximum number of retry attempts before aborting a write operation.
    */
   private final int maxAttemptCount = 10;
   
   /**
    * <code>true</code> iff the object can be serialized and sent (migrated) to another node.
    */
   private transient boolean serializable = false;
   
   /**
    * Name of the CNS service to use.
    */
   private String cnsServiceName;
   
   /**
    * Constructs a new <code>OutputReconnectionManagerCNSImpl</code> with the given underlying
    * networked channel output end. The default CNS service name will be used.
    *
    * @param out underlying networked channel end.
    */
   public OutputReconnectionManagerCNSImpl(NetChannelOutput out)
   {
      this(out, null);
   }
   
   /**
    * Constructs a new <code>OutputReconnectionManagerCNSImpl</code> with the given underlying
    * networked channel output end using a specific CNS service.
    *
    * @param out the underlying networked channel end.
    * @param cnsServiceName name of the CNS service to use.
    */
   public OutputReconnectionManagerCNSImpl(NetChannelOutput out, String cnsServiceName)
   {
      super();
      this.chanOut = out;
      this.cnsServiceName = cnsServiceName;
   }
   
   /**
    * @see org.jcsp.net.dynamic.OutputReconnectionManager#getOutputChannel()
    */
   public NetChannelOutput getOutputChannel()
   {
      if(chanOut == null) 
         return null;
      return new NetChannelOutput()
             {
               public void write(Object value)
               {
                  try
                  {
                     chanOut.write(value);
                  }
                  catch(LinkLostException e)
                  {
                     dealWithWriteError(value, e);
                  }
                  catch (ReaderIndexException e)
                  {
                     dealWithWriteError(value, e);
                  }
                  catch(ChannelDataRejectedException e)
                  {
                     dealWithWriteError(value, e);
                  }
               }
         
               public NetChannelLocation getChannelLocation()
               {
                  return chanOut.getChannelLocation();
               }
         
               public void recreate()
               {
                  chanOut.recreate();
               }
         
               public void recreate(NetChannelLocation newLoc)
               {
                  chanOut.recreate(newLoc);
               }
         
               public void destroyWriter()
               {
                  chanOut.destroyWriter();
               }
         
               public Class getFactoryClass()
               {
                  return null;
               }
               
               /**
                * Currently, network channels are unpoisonable so this method has no effect.
                */
               public void poison(int strength) {   
               }               
            };
   }
   
   /**
    * Attempts to resolve the new channel location. This method is called when a write error occurs
    * because the destination is no longer valid. The CNS service is queried to find out where the
    * other channel end moved to. A number of attempts to write to the new location are made. If these
    * fail, the channel is recreated. If the number of retry attempts exceeds the limit set by
    * <code>maxAttemptCount</code> the write is considered to have failed and the original exception
    * is rethrown.
    *
    * @param value the data value that the user was trying to write and that should be written.
    * @param e the exception originally raised to be thrown if the channel cannot be reconnected.
    */
   private void dealWithWriteError(Object value, RuntimeException e)
   {
      boolean written = false;
      int attemptCount = 1;
      
      if (!(this.chanOut.getChannelLocation() instanceof CNSNetChannelLocation))
      {
         //the channel did not obtain location information from the CNS
         String cnsRegisteredName = InputReconnectionManagerCNSImpl.anonymousCnsNamePrefix + 
                                    chanOut.getChannelLocation().getStringID();
         NetChannelLocation newLoc = (cnsServiceName == null)
                                   ? CNS.resolve(cnsRegisteredName)
                                   : ((CNSUser)Node.getInstance().getServiceUserObject(cnsServiceName))
                                       .resolve(cnsRegisteredName);
         chanOut.recreate(newLoc);
      }
      else
         chanOut.recreate();
      while((!written) && (attemptCount < maxAttemptCount))
      {
         try
         {
            chanOut.write(value);
            written = true;
         }
         catch (LinkLostException e2)
         {
            chanOut.recreate();
            attemptCount++;
         }
         catch (ReaderIndexException e2)
         {
            chanOut.recreate();
            attemptCount++;
         }
         catch (ChannelDataRejectedException e2)
         {
            chanOut.recreate();
            attemptCount++;
         }
      }
      if(!written)
         throw e;
   }
   
   /**
    * @see org.jcsp.net.dynamic.OutputReconnectionManager#prepareToMove()
    */
   public void prepareToMove()
   {
      if(serializable)
         return;
      if(chanOut != null)
         serializable = true;
   }
   
   /**
    * Serializes this object to the output stream. Before moving this channel end, <code>prepareToMove</code>
    * should be called.
    *
    * @param out destination stream to serialize to.
    * @throws IOException if there is a problem with the output stream.
    */
   private void writeObject(ObjectOutputStream out) throws IOException
   {
      if(!serializable)
         throw (new NotSerializableException(this.getClass().getName()));
      if(chanOut != null)
      {
         if(chanOut instanceof Serializable)
         {
            out.writeInt(1);
            out.writeObject(chanOut);
            //free up resources in JCSP networked infrastructure
            this.chanOut.destroyWriter();
         }
         else
            throw (new NotSerializableException(this.getClass().getName()));
      }
      out.writeInt(3);
   }
   
   /**
    * Deserializes this object from an input stream.
    *
    * @param in input stream to read the object from.
    * @throws IOException if there is a problem with the input stream.
    * @throws ClassNotFoundException if the class definition for the channel implementation cannot be
    *                                found. This is only likely to occur if there are different versions
    *                                of the infrastructure library on each node.
    */
   private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
   {
      int i = in.readInt();
      if(i < 3)
      {
         switch(i)
         {
            case 1:
            {
               chanOut = (NetChannelOutput)in.readObject();
               i = in.readInt();
               break;
            }
         }
      }
   }
}