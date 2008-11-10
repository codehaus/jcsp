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

package org.jcsp.net.cns;

import java.io.*;
import org.jcsp.net.*;

/**
 * This class is only visible to this package and does not need to be
 * used by JCSP users.
 *
 * The class is used for sending messages between the CNS server
 * process and CNSService client processes.
 *
 * @author Quickstone Technologies Limited
 */
abstract class CNSMessage implements Serializable
{
   static class LogonMessage extends CNSMessage
   {
      NetChannelLocation replyLocation;
   }
   
   static class LogonReplyMessage extends CNSMessage
   {
      boolean success;
   }
   
   static abstract class CNSRequestMessage extends CNSMessage
   {
      NetChannelLocation replyLocation;
      int RequestIndex;
      String name;
      NameAccessLevel accessLevel;
   }
   
   static abstract class CNSReplyMessage extends CNSMessage
   {
      int RequestIndex;
   }
   
   static class RegisterRequest extends CNSRequestMessage
   {
      NetChannelLocation channelLocation;
      ChannelNameKey key;
   }
   
   static class ResolveRequest extends CNSRequestMessage
   {
   }
   
   static class LeaseRequest extends CNSRequestMessage
   {
      ChannelNameKey key;
   }
   
   static class DeregisterRequest extends CNSRequestMessage
   {
      ChannelNameKey key;
   }
   
   static class RegisterReply extends CNSReplyMessage
   {
      ChannelNameKey key;
   }
   
   static class ResolveReply extends CNSReplyMessage
   {
      NetChannelLocation channelLocation;
      String name;
      NameAccessLevel accessLevel;
   }
   
   static class LeaseReply extends CNSReplyMessage
   {
      ChannelNameKey key;
   }
   
   static class DeregisterReply extends CNSReplyMessage
   {
      boolean success;
   }
}