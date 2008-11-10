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

/**
 * Thrown when a reconnection mannager is unable to recreate the underlying channel.
 *
 * @author Quickstone Technologies Limited
 */
public class ChannelEndContructionException extends RuntimeException
{
   /**
    * Constructs a <code>ChannelEndConstructionException</code> without a detail message or cause.
    */
   public ChannelEndContructionException()
   {
      super();
   }
   
   /**
    * Constructs a <code>ChannelEndConstructionException</code> with a detail message.
    *
    * @param message the detail message indicating why the exception was raised.
    */
   public ChannelEndContructionException(String message)
   {
      super(message);
   }
   
   /**
    * Constructs a <code>ChannelEndContructionException</code> with a detail message and underlying
    * cause.
    *
    * @param message the detail message.
    * @param cause the exception that was caught while trying to perform the construction operation.
    */
   public ChannelEndContructionException(String message, Throwable cause)
   {
      super(message, cause);
   }
   
   /**
    * Constructs a <code>ChannelEndConstructionException</code> with a cause indicator.
    *
    * @param cause the exception that was caught while trying to perform the construction operation.
    */
   public ChannelEndContructionException(Throwable cause)
   {
      super(cause);
   }
}