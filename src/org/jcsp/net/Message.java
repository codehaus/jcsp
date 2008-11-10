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

import java.io.Serializable;
import org.jcsp.lang.ChannelOutput;

/**
 * <p>
 * Message to be transmitted.  This is an abstract class containing
 * only header information - you must subclass it to use it.
 * </p>
 * <p>
 * This is a package-private implementation class.
 * </p>
 *
 * @author Quickstone Technologies Limited
 */
// package-private
abstract class Message implements Serializable
{
   public Message()
   {
   }
   
   static final PingMessage PING_MESSAGE = new PingMessage();
   
   static final PingReplyMessage PING_REPLY_MESSAGE = new PingReplyMessage();
   
   public final void bounce(ChannelOutput txChannel)
   {
      BounceMessage msg = new BounceMessage();
      msg.destIndex = sourceIndex;
      msg.sourceIndex = -1;
      txChannel.write(msg);
   }
   
   /**
    * The destination channel index.
    *
    * @serial
    */
   // package-private
   long destIndex;
   
   String destVCNLabel;
   
   /**
    * The source channel index.
    *
    * @serial
    */
   // package-private
   long sourceIndex;
   
   /**
    * The source computer address.  This is not transmitted, instead, it
    * is filled in automatically by the demux on arrival.  (Indeed, it
    * is not usually even filled in at the sending end).
    */
   // package-private
   transient NodeID sourceID;
   
   /**
    * The channel for transmitting replies.  It doesn't make sense to
    * transmit this value (or even to bother to fill it in at the
    * transmitting end), so it is filled in automatically by the demux
    * on arrival.
    */
   // package-private
   transient ChannelOutput txReplyChannel;
   
   public static class BounceMessage extends Message
   {
      private BounceMessage()
      {
      }
   }
   
   public static class PingMessage extends Message
   {
   }
   
   public static class PingReplyMessage extends Message
   {
   }
}