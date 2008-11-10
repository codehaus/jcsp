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

package org.jcsp.net.settings;

/**
 * Used internally within the JCSP network infrastructure to represent a set of system specifications.
 *
 * @author Quickstone Technologies Limited
 */
public abstract class Spec implements XMLConfigConstants
{
   Spec(String name, boolean builtin)
   {
      if(!builtin)
         for (int i = 0; i < RESERVED_SPEC_NAMES.length; i++)
            if(name.equals(RESERVED_SPEC_NAMES[i]))
               throw new ReservedNameException(name);
      
      this.name = name;
   }
   
   Spec(String name, boolean builtin, boolean req)
   {
      this(name, builtin);
      this.isReq = req;
   }
   
   public boolean equals(Object o)
   {
      if(o instanceof Spec)
      {
         Spec other = (Spec) o;
         return name.equals(other.name);
      }
      return false;
   }
   
   public int hashCode()
   {
      return name.hashCode();
   }
   
   public String getName()
   {
      return name;
   }
   
   public String toString()
   {
      StringBuffer sb = new StringBuffer();
      if(isReq)
         sb.append("<Req name=\"" + name + "\" value=\"" + getStringValue() + "\" />");
      else
         sb.append("<Spec name=\"" + name + "\" value=\"" + getStringValue() + "\" />");
      return sb.toString();
   }
   
   public abstract String getStringValue();
   private String name;
   private boolean isReq = false;
   
   static class ReservedNameException extends RuntimeException
   {
      private ReservedNameException(String name)
      {
         super("\"" + name + "\" is reserved.");
      }
   }
}