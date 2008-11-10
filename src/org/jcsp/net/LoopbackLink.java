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

import org.jcsp.lang.*;
import java.io.*;
import org.jcsp.util.filter.*;

/**
 *
 * @author Quickstone Technologies Limited.
 */
class LoopbackLink extends Link
{
   /**
    * Constructs a new loopback link for efficient network channels that are on the same JVM.
    */
   LoopbackLink()
   {
      super(null, false, true);
      loopBack.outFilter().addWriteFilter(new LoopbackSerializationFilter());
      loopBack.inFilter().addReadFilter(new DeserializeChannelFilter());
   }
   
   protected void runTxRxLoop()
   {
      new Parallel(new CSProcess[] {
         new TxLoop(),
         new RxLoop()
      }).run();
   }
   
   /**
    * Returns the other computer's ID.
    *
    * This method is safe to call while the process is running, however
    * it will return null if the other computer has not yet identified
    * itself.
    *
    * @return ID of connected computer.
    */
   protected NodeID getRemoteNodeID()
   {
      return Node.getInstance().getNodeID();
   }
   
   private final FilteredOne2OneChannel loopBack = FilteredChannel.createOne2One();
   
   /**
    * The process which does transmission to the stream.
    */
   private class TxLoop implements CSProcess
   {
      /**
       * The run method.
       */
      public void run()
      {
         try
         {
            ChannelInput txIn = txChannel.in();
            ChannelOutput loopbackTx = loopBack.out();
            Object obj = txIn.read();
            while (!(obj instanceof TxLoopPoison))
            {
               try
               {
                  loopbackTx.write(obj);
               }
               catch (Exception e)
               {
                  e.printStackTrace();
               }
               
               obj = txIn.read();
            }
         }
         catch (Exception ex)
         {
            Node.err.log(this, "ERROR in LoopbackLink - EXCEPTION in TxLoop - this should never happen!!");
            Node.err.log(this, ex);
         }
      }
   }
   
   /**
    * The process which recieves from the stream.
    */
   private class RxLoop implements CSProcess
   {
      /**
       * The run method.
       */
      public void run()
      {
         try
         {
            // Now enter demux loop
            ChannelInput in = loopBack.in();
            while (true)
               deliverReceivedObject(in.read());
         }
         catch (Exception ex)
         {
            Node.err.log(this, "ERROR in LoopbackLink - EXCEPTION in TxLoop - this should never happen!!");
            Node.err.log(this, ex);
         }
      }
   }
   
   /**
    * An object of this type is used by RxLoop to poison TxLoop.
    */
   private static class TxLoopPoison
   {
   }
   
   private class LoopbackSerializationFilter implements Filter
   {
      public Object filter(Object obj)
      {
         if(obj instanceof Message)
         {
            Message msg = (Message) obj;
            try
            {
               msg = new SerializedMessage(msg);
            }
            catch (NotSerializableException e)
            {
               e.printStackTrace();
               throw new IllegalArgumentException("Data not Serializable");
            }
            catch (IOException e)
            {
               e.printStackTrace();
               throw new IllegalArgumentException("Error while trying to Serialize data");
            }
            return msg;
         }
         else
            throw new IllegalArgumentException("Object not a message");
      }
   }
}
