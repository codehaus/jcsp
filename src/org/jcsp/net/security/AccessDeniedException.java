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

package org.jcsp.net.security;

/**
 * <p>Thrown by the security authority implementations if the credentials supplied are not correct or
 * another error occurs as a result of user parameters.</p>
 *
 * @author Quickstone Technologies Limited
 */
public class AccessDeniedException extends Exception
{
   /**
    * The reason the exception was raised.
    */
   private final String reason;
   
   /**
    * The security authority raising the exception.
    */
   private final SecurityAuthority auth;
   
   /**
    * Creates a new exeception.
    *
    * @param auth the authority raising the exception.
    * @param reason the reason the exception was raised.
    */
   public AccessDeniedException(SecurityAuthority auth, String reason)
   {
      super("Access Denied");
      this.reason = reason;
      this.auth = auth;
   }
   
   /**
    * Returns a printable string describing the exception.
    */
   public String toString()
   {
      return "Access denied by " + auth.toString() + " - " + reason;
   }
}