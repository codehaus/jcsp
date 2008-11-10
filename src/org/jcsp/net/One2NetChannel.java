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
import org.jcsp.util.InfiniteBuffer;
import java.io.*;

/**
 * A channel for network output (TX).  This is a "One2Net" channel,
 * which can only be safely used by one writer.
 *
 * @author Quickstone Technologies Limited
 */
class One2NetChannel implements NetChannelOutput, Serializable
{
   /***********Constructors******************************************************/
   
   /**
    * Constructor which takes the location of a <code>Networked</code>
    * <code>ChannelInput</code> to which to send data.
    * The channel constructed will block after any data is sent
    * until an acknowledgement is returned.
    *
    * A Link to the <code>ChannelInput<code> object's Node will
    * be established if necessary. This Link may be established over any
    * available protocol implementation.
    *
    * This constructor is equivalent to using the
    * (NetChannelLocation, boolean, Profile) constructor with
    * the <code>boolean</code> set to <code>true</code> and
    * the <code>Profile</code> set to <code>null</code>.
    *
    */
   public One2NetChannel(NetChannelLocation channelLocation)
   {
      this(channelLocation, true, null);
   }
   
   /**
    * Constructor which takes the location of a <code>Networked</code>
    * <code>ChannelInput</code> to which to send data and a
    * <code>Profile</code> that any Link used should match.
    *
    * The channel constructed will block after any data is sent
    * until an acknowledgement is returned.
    *
    * A Link to the <code>ChannelInput<code> object's Node will
    * be established if necessary. This Link will match the specified
    * Profile. A <code>RuntimeException</code> will be thrown if a
    * a matching Link cannot be established.
    *
    * This constructor is equivalent to using the
    * (NetChannelLocation, boolean, Profile) constructor with
    * the <code>boolean</code> set to <code>true</code>.
    *
    */
   public One2NetChannel(NetChannelLocation channelLocation, Profile linkProfile)
   {
      this(channelLocation, true, linkProfile);
   }
   
   /**
    * Constructor which takes the location of a <code>Networked</code>
    * <code>ChannelInput</code> to which to send data and a
    * <code>boolean</code> indicating whether or not to obtain
    * acknowledgements.
    *
    * If the acknowledged parameter is <code>true</code>, then the channel
    * constructed will block after any data is sent
    * until an acknowledgement is returned. Otherwise, the channel should not
    * block, however, some flow control may be implemented which may result
    * in blocking.
    *
    * A Link to the <code>ChannelInput<code> object's Node will
    * be established if necessary. This Link may be established over any
    * available protocol implementation.
    *
    * This constructor is equivalent to using the
    * (NetChannelLocation, boolean, Profile) constructor with
    * the <code>Profile</code> set to <code>null</code>.
    *
    */
   public One2NetChannel(NetChannelLocation channelLocation, boolean acknowledged)
   {
      this(channelLocation, acknowledged, null);
   }
   
   
   /**
    * Constructor which takes the location of a <code>Networked</code>
    * <code>ChannelInput</code> to which to send data and a
    * <code>boolean</code> indicating whether or not to obtain
    * acknowledgements.
    *
    * If the acknowledged parameter is <code>true</code>, then the channel
    * constructed will block after any data is sent
    * until an acknowledgement is returned. Otherwise, the channel should not
    * block, however, some flow control may be implemented which may result
    * in blocking.
    *
    * A Link to the <code>ChannelInput<code> object's Node will
    * be established if necessary. This Link will match the specified
    * Profile. A <code>RuntimeException</code> will be thrown if a
    * a matching Link cannot be established.
    *
    */
   public One2NetChannel(NetChannelLocation channelLocation, boolean acknowledged, Profile linkProfile)
   {
      this.acknowledged = acknowledged;
      this.channelLocation = channelLocation;
      this.channelID = new ChannelID(channelLocation.getChannelNodeID(), channelLocation.getVCN());
      this.linkProfile = linkProfile;
      this.maxUnacknowledged = 0;
      
      if (acknowledged)
      {
         IndexManager.ChannelAndIndex chanAndIndex = IndexManager.getInstance().getNewReplyChannel(this);
         fromNetIn = chanAndIndex.channel.in();
         fromNetOut = chanAndIndex.channel.out();
         channelIndex = chanAndIndex.index;
      }
      else
         channelIndex = -1;
   }
   
   /***********Private fields****************************************************/
   
   /**
    * The channel ID of the remote computer.
    * Not valid unless we are connected.
    */
   private transient ChannelID channelID;
   
   /**
    * Our channel index.  (On the local computer, for acknowlegements).
    */
   private transient long channelIndex;
   
   /**
    * The channel we use for recieving from the demuxes.
    */
   private transient AltingChannelInput fromNetIn;
   
   private transient SharedChannelOutput fromNetOut;
   
   /**
    * The channel we use for sending to the network TX.
    * Not valid unless we are connected.
    */
   private transient ChannelOutput toNet;
   
   /**
    * True iff the link's been dropped.
    */
   private transient boolean broken;
   
   /**
    * True iff we're connected to the remote computer.
    */
   private transient boolean connected;
   
   private transient ChannelMessage.Data messageA = new ChannelMessage.Data();
   
   private transient ChannelMessage.Data messageB = new ChannelMessage.Data();
   
   private transient boolean sendMessageA = true;
   
   /**
    * The channel name.
    *
    * @serial
    */
   private NetChannelLocation  channelLocation;
   
   /**
    * The Profile that the link used by this channel should match.
    */
   private final Profile linkProfile;
   
   /**
    * The maximum number of data items "in flight" when a call to write()
    * returns.  The default setting of zero gives standard unbuffered
    * channel semantics.
    *
    * 5/Jun/2002
    * This is not currently used nor exposed to the user.
    * Streaming has been changed so that acknowledgements are
    * either on or off. Flow control really needs to be
    * controlled from the receiving end but this functionality
    * has not yet been implemented.
    *
    * @serial
    */
   private final int maxUnacknowledged;
   
   /**
    * The number of data items currently unacknowledged ("in flight").
    * Always <= maxUnacknowledged + 1.  If it is > maxUnacknowledged,
    * then we must be blocked in write().
    */
   private transient int numUnacknowledged;
   
   /**
    * Indicates whether this channel requires acknowledgements to be
    * returned from the receiver.
    *
    */
   private boolean acknowledged = true;
   
   /***********Public Methods****************************************************/
   
   /**
    * Output data to this channel.  The data must be Serializable.
    * <p>
    * For a normal channel (numUnacknowledged == 0), the contract of this
    * method is as follows:  When the method returns, the data has been sent
    * to the remote computer, and the process (or user-supplied buffer) at
    * the other end has accepted it.  It is safe to modify <tt>data</tt>
    * after the call.  Transmission is guaranteed unless a
    * LinkLostException is thrown, in which case it is impossible to
    * determine whether or not the remote computer recieved the data
    * before the link dropped.
    * <p>
    * For a streaming channel (numUnacknowledged > 0), the contract of
    * this method is slightly different.  When the method returns, the data
    * has started it's trip to the remote computer, but the link may go down
    * so there is no guarantee that it will get there.  Also, since it may
    * not have left this computer yet (the actual transmission is done in
    * parallel with the return from this method), it is *NOT* safe to modify
    * the passed <tt>data</tt> object.  After the <i>next</i> call to this
    * method, then you may assume that the <tt>data</tt> object has been
    * transmitted, and re-use it or let the Java garbage collector deal with
    * it as appropriate.  You must wait a total of <tt>numUnacknowledged</tt>
    * calls to this method before you can assume that the data was recieved
    * - and if any of those calls throw a LinkLostException, then you
    * cannot find out how much of the stream of data was recieved by the
    * remote computer before the link broke.
    * <p>
    * A LinkLostException is thrown if the TCP/IP link is dropped.
    * This could be due to a network problem, but more often it is caused
    * when the remote Java VM crashes or is shut down.  It can also be
    * caused if there is a serialization problem with the TCP/IP link - e.g.
    * an attempt to send a non-serializable object, or an attempt to send a
    * class which doesn't exist or isn't the same version at the recieving
    * end.  Note that LinkLostException is a permanent error - if it
    * is thrown, then any further call to this method will cause it to be
    * thrown again.
    *
    * @param data an object to send over the channel. This should be <CODE>Serializable</CODE>.
    */
   public synchronized void write(Object data)
   {
      if (broken)
         // Oops.
         throw new LinkLostException(this, /*name*/"FILL IN");
      
      if (!connected)
      {
         if (channelID == null)
            channelID = new ChannelID(channelLocation.getChannelNodeID(), channelLocation.getVCN());
         if (channelLocation.getChannelNodeID() == null)
            toNet = LinkManager.getInstance().getTxChannel(channelLocation.getChannelAddress());
         else
            toNet = LinkManager.getInstance().getTxChannel(channelID.getNodeID(), linkProfile);
         connected = true;
      }
      // Now we're set up, do the output.
      // Create a Message object to hold the message
      ChannelMessage.Data message = null;
      
      if (acknowledged)
      {
         if(sendMessageA)
            message = messageA;
         else
            message = messageB;
         sendMessageA = !sendMessageA;
      }
      else
         //if not acknowledge too dangerous to use message pool
         message = new ChannelMessage.Data();
      
      message.destIndex = channelID.getIndex();
      message.sourceIndex = channelIndex;
      message.data = data;
      message.acknowledged = acknowledged;
      
      if (message.destIndex == IndexManager.getInvalidVCN())
         message.destVCNLabel = channelLocation.getChannelLabel();
      else
         message.destVCNLabel = null;
      // Transmit the message
      toNet.write(message);
      
      // One more acknowldgement we should get back.
      if (acknowledged)
      {
         numUnacknowledged++;
         
         // Loop around recieving messages.  We can't continue if the number
         // of acks pending is over the limit.  Otherwise, we just loop
         // until we've read the Acks _or_ we've run out of things to read.
         //
         // (Claim: This is a _fair_ system - i.e. similar to fair ALTing
         // between acks and data to send.  This is because there are
         // never two sends in a row without an attempt to read acks, and
         // this thread never reads two acks in a row without a send.
         // What's more, since the special buffer is merging acks, all
         // pending acks are processed in a single read each time we send).
         boolean gotAcks = false;
         while ((numUnacknowledged > maxUnacknowledged) || ((!gotAcks) && fromNetIn.pending()))
         {
            Object obj = fromNetIn.read();
            if (obj instanceof AcknowledgementsBuffer.Acks)
            {
               numUnacknowledged -= ((AcknowledgementsBuffer.Acks)obj).count;
               // Could (should?) check that numUnacknowledged >= 0 here,
               // but I'm going to assume that the other side isn't that
               // broken.  Besides, even if numUnacknowledged < 0, what can
               // we do about it?
               gotAcks = true;
               if (message.destVCNLabel != null)
               {
                  channelID = new ChannelID(((AcknowledgementsBuffer.Acks)obj).sourceNodeID,
                                                 ((AcknowledgementsBuffer.Acks)obj).vcn);
                  channelLocation.setLocationDetails(channelID.getNodeID(), channelID.getIndex());
               }
            }
            else if (obj instanceof LinkLost)
            {
               LinkLost cl = (LinkLost)obj;
               cl.txChannel.write(cl); // acknowlegement.
               if (cl.txChannel == toNet)
               {
                  broken = true;
                  toNet = null;
                  throw new LinkLostException(this, "Lost Link to " + ((LinkLost) obj).address);
               }
            }
            else if(obj instanceof Message.BounceMessage)
               throw new ReaderIndexException();
            else if(obj instanceof ChannelMessage.WriteRejected)
               throw new ChannelDataRejectedException();
            else
            {
               Node.err.log(this, "One2NetChannel, write: Received unexpected message type: " + 
                            ((obj == null) ? null : obj.getClass()));
               throw new NetChannelError("One2NetChannel received invalid " + 
                       "message over acknowledgement channel. This could be " + 
                       "caused by using a Non-shared NetChannelOutput object " + 
                       "between multiple processes!");
            }
         }
      }
      // Done!
   }
   
   /**
    * Returns a clone of the <code>NetChannelLocation</code> object
    * held by the instance of this class which contains information
    * about the location of the networked <code>ChannelInput</code>
    * object to which this <code>ChannelOutput</code> is connected.
    *
    * @return  the <code>NetChannelLocation</code> of the destination
    *           <code>ChannelInput</code>.
    *
    */
   public NetChannelLocation getChannelLocation()
   {
      try
      {
         return (NetChannelLocation) channelLocation.clone();
      }
      catch (CloneNotSupportedException e)
      {
         return null;
      }
   }
   
   static Any2OneChannel failedLinks = Channel.any2one(new InfiniteBuffer());
   
   static
   {
      new ProcessManager(new CSProcess()
                        {
                           public void run()
                           {
                              while (true)
                              {
                                 One2NetChannel c = (One2NetChannel)failedLinks.in().read();
                                 c.destroyWriter();
                              }
                           }  
                        }).start();
   }
   
   void linkFailed(NodeID remoteID)
   {
      if (channelLocation.getChannelNodeID().equals(remoteID))
         failedLinks.out().write(this); // asynchronously destroy to avoid deadlock
   }
   
   /**
    * This destroys this write end of the channel and frees any resources
    * in the JCSP.NET infrastructure.
    *
    */
   public synchronized void destroyWriter()
   {
      if (channelIndex != IndexManager.getInvalidVCN())
         IndexManager.getInstance().removeChannel(channelIndex, fromNetOut);
      broken = true;
   }
   
   /**
    * Requests that the channel recreates itself and reconnects to the
    * other end of the channel.
    *
    * This method will call <code>refresh()</code> on the
    * <code>NetChannelLocation</code> it holds for its destination.
    * If the instance held is an instance of a sub-class of
    * <code>NetChannelLocation</code> then this provides an oportunity
    * for the instance to refresh its information. This could, for example,
    * be from a naming service.
    *
    */
   public void recreate()
   {
      connected = false;
      channelID = null;
      numUnacknowledged = 0;
      broken = false;
      this.channelLocation.refresh();
   }
   /**
    * Requests that the channel recreates itself and reconnects to the
    * other end of the channel. A new location of the read end can be
    * supplied.
    *
    * @param newLoc the new location of the read end of the channel.
    */
   public void recreate(NetChannelLocation newLoc)
   {
      connected = false;
      channelID = null;
      numUnacknowledged = 0;
      broken = false;
      this.channelLocation = newLoc;
   }
   
   public Class getFactoryClass()
   {
      return StandardNetChannelEndFactory.class;
   }
   
   /**
    * Currently, network channels are unpoisonable so this method has no effect.
    */
   public void poison(int strength) {   
   }   
   
   /***********Private Methods***************************************************/
   
   /**
    * Handles deserialization.  This is responsible for starting up the
    * process which handles output, and for creating the internal channel.
    */
   private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException
   {
      // read non-transient fields.
      stream.defaultReadObject();
      connected = false;
      broken = false;
      
      // Sanity check.
      if (maxUnacknowledged < 0)
         throw new InvalidObjectException("One2NetChannel: maxUnacknowledged < 0");
      
      if (acknowledged)
      {
         IndexManager.ChannelAndIndex chanAndIndex = IndexManager.getInstance().getNewReplyChannel(this);
         fromNetIn = chanAndIndex.channel.in();
         fromNetOut = chanAndIndex.channel.out();
         channelIndex = chanAndIndex.index;

         messageA = new ChannelMessage.Data();
         messageB = new ChannelMessage.Data();
      }
   }
}