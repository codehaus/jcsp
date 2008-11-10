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

import org.jcsp.lang.ProcessManager;
import org.jcsp.lang.RejectableBufferedOne2AnyChannel;
import org.jcsp.lang.RejectableChannel;
import org.jcsp.lang.RejectableOne2AnyChannel;
import org.jcsp.util.ChannelDataStore;

/**
 * A channel for network input (RX).  This is a "Net2Any" channel,
 * which can be safely used by multiple readers on the same Node.
 *
 * @author Quickstone Technologies Limited
 */
class Net2AnyChannel implements NetSharedChannelInput, Networked
{
   /*-----------------Attributes-------------------------------------------------*/
   
   /**
    * The channel name.
    */
   private final String label;

   /**
    * The local channel used for output from the recieving process
    */
   private RejectableChannel ch;
   
   private NetChannelInputProcess netChannelInputProcess;
   
   
   public Net2AnyChannel(String label) throws NullPointerException
   {
      if (label == null)
         throw new NullPointerException("Label supplied is null");
      if (label != "")
         Node.info.log(this, "Creating a channel with VCN label: " + label);
      ch = new RejectableOne2AnyChannel();
      netChannelInputProcess = new NetChannelInputProcess(label,ch);
      this.label = label;
   }
   
   /**
    * <p>
    * Creates an anonymous input channel.
    * </p>
    * <p>
    * To create writers that write to this channel, you need to call
    * getChannelName() to get a valid name for this channel.  You
    * will need to use some other means (e.g. a named channel) to pass
    * the channel name to the writing computer.
    * </p>
    */
   public Net2AnyChannel()
   {
      ch = new RejectableOne2AnyChannel();
      this.label = null;
      netChannelInputProcess = new NetChannelInputProcess(label,ch);
      new ProcessManager(netChannelInputProcess).start();
   }
   
   /**
    * <p>
    * Creates an anonymous, buffered input channel.
    * </p>
    * <p>
    * To create writers that write to this channel, you need to call
    * getChannelName() to get a valid name for this channel.  You
    * will need to use some other means (e.g. a named channel) to pass
    * the channel name to the writing computer.
    * </p>
    *
    * @param buffer The ChannelDataStore to use.
    */
   public Net2AnyChannel(ChannelDataStore buffer)
   {
      ch = new RejectableBufferedOne2AnyChannel(buffer);
      netChannelInputProcess = new NetChannelInputProcess(null,ch);
      this.label = null;
      new ProcessManager(netChannelInputProcess).start();
   }
   
   /**
    *
    *
    *
    *
    */
   public Net2AnyChannel(String label, ChannelDataStore buffer)
   {
      if (label == null)
         throw new NullPointerException("Label supplied is null");
      if (label != "")
         Node.info.log(this, "Creating a channel with VCN label: " + label);
      this.label = label;
      ch = new RejectableBufferedOne2AnyChannel(buffer);
      netChannelInputProcess = new NetChannelInputProcess(label,ch);
      new ProcessManager(netChannelInputProcess).start();
   }
   
   /**
    * Read data from this channel.  This can safely be called by
    * multiple readers.
    *
    * @return The object read from the network.
    */
   public Object read()
   {
      return ch.in().read();
   }
   
   public Object startRead()
   {
     return ch.in().startRead();
   }
   
   public void endRead()
   {
     ch.in().endRead();
   }
   
   
   /**
    * Currently, network channels are unpoisonable so this method has no effect.
    */
   public void poison(int strength) {   
   }   
   
   public NetChannelLocation getChannelLocation()
   {
      return new NetChannelLocation(Node.getInstance().getNodeID(), netChannelInputProcess.getChannelIndex());
   }
   
   public Class getFactoryClass()
   {
      return StandardNetChannelEndFactory.class;
   }
   
   public void destroyReader()
   {
      netChannelInputProcess.breakChannel();
   }
}