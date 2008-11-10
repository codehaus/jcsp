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

package org.jcsp.net.remote;

/**
 * Wraps up a non-RuntimeException into a runtime exception that can be ignored or caught and
 * rethrown if required.
 *
 * @author Quickstone Technologies Limited
 */
public class RemoteSpawnException extends RuntimeException
{
   /** The actual exception. */
   public final Throwable cause;
   
   /**
    * Constructs a new exception.
    *
    * @param cause the actual exception.
    */
   public RemoteSpawnException(Throwable cause)
   {
      this.cause = cause;
   }
   
   /**
    * Rethrows the actual exception.
    */
   public void rethrow() throws Throwable
   {
      throw cause;
   }
   
   /**
    * Prints the stack trace of the actual exception.
    */
   public void printStackTrace()
   {
      cause.printStackTrace();
   }
   
   /**
    * Returns a string representation of the actual exception.
    */
   public String toString()
   {
      return cause.toString();
   }
}