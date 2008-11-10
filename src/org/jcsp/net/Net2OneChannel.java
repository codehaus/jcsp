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
import org.jcsp.util.ChannelDataStore;

/**
 * A channel for network input (RX).  This is a "Net2One" channel,
 * which can only be used by one reader at a time.
 *
 * @author Quickstone Technologies Limited
 */
class Net2OneChannel extends NetAltingChannelInput
{
   static Net2OneChannel create(String label)
   {
      RejectableOne2OneChannel chan = new RejectableOne2OneChannel();
      return new Net2OneChannel(label, chan.inAlt(), chan);
   }
   
   static Net2OneChannel create()
   {
      RejectableOne2OneChannel chan = new RejectableOne2OneChannel();
      return new Net2OneChannel(chan.inAlt(), chan);
   }
   
   static Net2OneChannel create(ChannelDataStore buffer)
   {
      RejectableBufferedOne2OneChannel chan = new RejectableBufferedOne2OneChannel(buffer);
      return new Net2OneChannel(chan.inAlt(), chan);
   }
   
   static Net2OneChannel create(String label, ChannelDataStore buffer)
   {
      RejectableBufferedOne2OneChannel chan = new RejectableBufferedOne2OneChannel(buffer);
      return new Net2OneChannel(label, chan.inAlt(), chan);
   }
   
   /**
    * Creates a channel which receives data on a labelled VCN.
    *
    * @param   label   The label to apply to this channel's VCN.
    * @throws  IllegalArgumentException if the label supplied
    *                  is a <code>null</code> reference.
    */
   private Net2OneChannel(String label, AltingChannelInput actualChan, RejectableChannel ch) throws IllegalArgumentException
   {
      super(actualChan);
      if (label == null)
         throw new IllegalArgumentException("Label supplied is null");
      if (label != "")
         Node.info.log(this, "Creating a channel with VCN label: " + label);
      this.label = label;
      netChannelInputProcess = new NetChannelInputProcess(label,ch);
      new ProcessManager(netChannelInputProcess).start();
   }
   
   /**
    * Creates a zero-buffered channel reader.
    * The <code>getChannelLocation()</code> method can
    * be called to obtain the location information of the constructed
    * channel.
    */
   private Net2OneChannel(AltingChannelInput actualChan, RejectableChannel ch)
   {
      super(actualChan);
      this.label = null;
      netChannelInputProcess = new NetChannelInputProcess(this.label,ch);
      new ProcessManager(netChannelInputProcess).start();
   }
   
   /**
    * Returns a new <code>NetChannelLocation</code> object which holds the
    * information necessary for a networked <code>ChannelOutput</code> to
    * establish a connection to this channel reader.
    *
    * @return the location information for this channel reader.
    */
   public NetChannelLocation getChannelLocation()
   {
      return new NetChannelLocation(Node.getInstance().getNodeID(), netChannelInputProcess.getChannelIndex());
   }
   
   /**
    * Destroys this end of the channel.
    */
   public void destroyReader()
   {
      netChannelInputProcess.breakChannel();
   }
   
   /**
    * This method should not be called.  The implementation channel is not
    * available to subclasses of Net2OneChannel.
    *
    * @return Always null.
    */
   protected AltingChannelInput getChannel()
   {
      return null;
   }
   
   long getChannelIndex()
   {
      return netChannelInputProcess.getChannelIndex();
   }
   
   public Class getFactoryClass()
   {
      return StandardNetChannelEndFactory.class;
   }
   
   /**
    * Currently, network channels are unpoisonable so this method has no effect.
    */
   public void poison(PoisonException poison) {   
   }
   /**
    * Currently, network channels are unpoisonable so this method will never throw a PoisonException
    */
   public void checkPoison() throws PoisonException {   
   }
   
   
   /*-----------------Attributes-------------------------------------------------*/
   
   private final String label;
   
   private NetChannelInputProcess netChannelInputProcess;
}