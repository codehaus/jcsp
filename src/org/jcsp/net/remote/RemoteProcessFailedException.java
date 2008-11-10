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

import org.jcsp.lang.*;

/**
 * Thrown if a remote process terminates abruptly with a non-zero error code.
 *
 * @author Quickstone Technologies Limited
 */
public class RemoteProcessFailedException extends RuntimeException
{
   /** The error code returned by the process. */
   private final int errorCode;
   
   /** The offending process. */
   private final CSProcess process;
   
   /**
    * Constructs a new exception.
    *
    * @param ec the exit code from the remote JVM.
    * @param proc the process that was running.
    */
   public RemoteProcessFailedException(int ec, CSProcess proc)
   {
      errorCode = ec;
      process = proc;
   }
   
   /**
    * Returns a string description of the exception.
    */
   public String toString()
   {
      return "Remote process '" + process.toString() + "' failed with error code " + errorCode;
   }
   
   /**
    * Returns the error code of the remote JVM.
    */
   public int getErrorCode()
   {
      return errorCode;
   }
   
   /**
    * Returns the process that was running when the error occurred.
    */
   public CSProcess getFailedProcess()
   {
      return process;
   }
}