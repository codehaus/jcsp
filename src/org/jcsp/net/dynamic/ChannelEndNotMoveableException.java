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
 * Thrown when a channel end cannot be moved.
 *
 * @author Quickstone Technologies Limited
 */
public class ChannelEndNotMoveableException extends RuntimeException
{
   /**
    * Constructs a new <code>ChannelEndNotMoveableException</code> without a detail message or
    * cause.
    */
   public ChannelEndNotMoveableException()
   {
      super();
   }
   
   /**
    * Constructs a new <Code>ChannelEndNotMoveableException</code> with a detail message.
    *
    * @param message the detail message.
    */
   public ChannelEndNotMoveableException(String message)
   {
      super(message);
   }
   
   /**
    * Constructs a new <Code>ChannelEndNotMoveableException</code> with a detail message and
    * underlying cause exception.
    *
    * @param message the detail message.
    * @param cause the exception that caused this one to be raised.
    */
   public ChannelEndNotMoveableException(String message, Throwable cause)
   {
      super(message, cause);
   }
   
   /**
    * Constructs a new <code>ChannelEndNotMoveableException</code> with a cause exception.
    *
    * @param cause the exception that caused this one to be raised.
    */
   public ChannelEndNotMoveableException(Throwable cause)
   {
      super(cause);
   }
}