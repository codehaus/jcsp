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

/**
 * Messages used by channels.
 *
 * <p>This is a package-private implementation class.
 * @author Quickstone Technologies Limited
 */
// package-private
abstract class ChannelMessage extends Message
{
   /**
    * Data from channel output to channel input.
    *
    * <p>This is a package-private implementation class.
    *
    */
   // package-private
   static final class Data extends ChannelMessage
   {
      /**
       * The actual data being transmitted.
       * This needs to be Serializable.
       *
       * @see java.io.Serializable
       *
       * @serial
       */
      Object data;
      
      boolean acknowledged = true;
      
      //debug code
      public String toString()
      {
         return "MessageData: " + data;
      }
   }
   
   /**
    * An acknowledgement.
    *
    * <p>This is a package-private implementation class.
    *
    */
   // package-private
   static final class Ack extends ChannelMessage
   {
   }
   
   static final class WriteRejected extends ChannelMessage
   {
   }
}