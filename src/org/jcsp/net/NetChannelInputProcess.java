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

/**
 * A process for network input (RX).
 *
 * <p>This is a package-private implementation class.
 *
 * @author Quickstone Technologies Limited
 */
// package-private
class NetChannelInputProcess implements CSProcess
{
   /**
    * Constructor which takes a label to assign to the channel's
    * VCN and a <code>ChannelOutput</code> object on which to output
    * any data received from over the network.
    *
    * @param label the label to assign to the VCN. If this is
    *               <code>null</code> then no label will be assigned.
    * @param out   the <out>ChannelOutput</code> object on which to
    *               forward any data received from over the network.
    *
    * @author Jo Aldous
    */
   NetChannelInputProcess(String label, RejectableChannel out)
   {
      this.out = out;
      IndexManager.ChannelAndIndex chanAndIndex = IndexManager.getInstance().getNewChannel(label);
      fromNetIn = chanAndIndex.channel.in();
      fromNetOut = chanAndIndex.channel.out();
      channelIndex = chanAndIndex.index;
      alt = new Alternative(new Guard[] {stopChannel.in(), fromNetIn});
      channelID = new ChannelID(Node.getInstance().getNodeID(), channelIndex);
   }
   
   void breakChannel()
   {
      //Remove channel from index manager - must be done before rejecting any data
      IndexManager.getInstance().removeChannel(channelIndex,fromNetOut);
      //reject data from channel
      out.in().reject();
      stopChannel.out().write(this);
   }
   
   long getChannelIndex()
   {
      return channelIndex;
   }
   
   /**
    * Reads a message from the network communications channel.
    * Acknowledges and ignores LinkLost messages. <p>
    *
    * @return The first Message read from the network.
    */
   private Object readFromNetChannel()
   {
      int selection = alt.priSelect();
      if(selection == 0)
      {
         stopChannel.in().read();
         
         //request to stop the server - reject any more messages
         while (fromNetIn.pending())
         {
            Object obj = fromNetIn.read();
            if (obj instanceof LinkLost)
            {
               LinkLost cl = (LinkLost)obj;
               cl.txChannel.write(cl); // acknowlegement.
            }
            else
            {
               ChannelMessage.Data message;
               try
               {
                  //reject any messages pending
                  message = (ChannelMessage.Data)obj;
               }
               catch (ClassCastException e)
               {
                  Node.err.log(this, "NetChannelInputProcess received an unexpected message type");
                  break;
               }
               ChannelMessage.WriteRejected writeRejected = new ChannelMessage.WriteRejected();
               writeRejected.destIndex = message.sourceIndex;
               writeRejected.sourceIndex = channelIndex;
               
               // Send the rejection
               message.txReplyChannel.write(writeRejected);
            }
         }
         return null;
      }
      else
      {
         Object obj = fromNetIn.read();
         while (obj instanceof LinkLost)
         {
            LinkLost cl = (LinkLost)obj;
            cl.txChannel.write(cl); // acknowlegement.
            obj = fromNetIn.read();
         }
         return obj;
      }
   }
   
   /**
    * Handle this link.  Gets data and sends acknowlegements.
    * Runs forever.
    */
   public void run()
   {
      boolean stop = false;
      while (stop == false)
      {
         // Read a message from TCP/IP.
         Object obj = readFromNetChannel();
         if(obj == null)
         {
            stop = true;
            Node.info.log(this, "NetChanInProc stopping");
         }
         else if (obj instanceof ChannelMessage.Data)
         {
            // Deal with it.
            
            ChannelMessage.Data message = (ChannelMessage.Data)obj;
            // Output the data (will block until the user process reads it)
            try
            {
               out.out().write(message.data);
               if (message.acknowledged)
               {
                  // Prepare an acknowlegement message
                  //ChanndelMessage.Ack ack = new ChannelMessage.Ack();
                  ChannelMessage.Ack ack = null;
                  if(sendAckA)
                     ack = this.ackA;
                  else
                     ack = this.ackB;
                  this.sendAckA = !sendAckA;
                  ack.destIndex = message.sourceIndex;
                  ack.sourceIndex = channelIndex;
                  // Send the acknowlegement
                  message.txReplyChannel.write(ack);
               }
            }
            catch (ChannelDataRejectedException e)
            {
               //data never read
               e.printStackTrace();
               if (message.acknowledged)
               {
                  ChannelMessage.WriteRejected writeRejected = new ChannelMessage.WriteRejected();
                  writeRejected.destIndex = message.sourceIndex;
                  writeRejected.sourceIndex = channelIndex;
                  
                  // Send the rejection
                  message.txReplyChannel.write(writeRejected);
               }
            }
         }
         else
         {
            Node.err.log(this, "Unexpected message type received: " +  obj.getClass());
         }
      }
      
   }
   
   /**
    * Our channel index.
    */
   private long channelIndex;
   
   /**
    * Our channel ID.
    */
   private ChannelID channelID;
   
   /**
    * The name of this channel.
    */
   private String name;
   
   /**
    * The channel we use for recieving from the demuxes.
    */
   private AltingChannelInput fromNetIn;
   private SharedChannelOutput fromNetOut;
   /**
    * The channel used for output.
    */
   private RejectableChannel out;
   
   private Any2OneChannel stopChannel = Channel.any2one();
   
   private Alternative alt;
   
   private transient ChannelMessage.Ack ackA = new ChannelMessage.Ack();
   
   private transient ChannelMessage.Ack ackB = new ChannelMessage.Ack();
   
   private transient boolean sendAckA = true;
}