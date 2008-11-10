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

import java.io.*;

/**
 *
 * @author Quickstone Technologies Limited.
 */
class Specification implements Serializable
{
   Specification(String name, int intValue)
   {
      this.name = name;
      this.intValue = intValue;
      strValue = "";
      dblValue = -1;
      booValue = false;
      type = Integer.TYPE;
   }
   
   Specification(String name, String strValue)
   {
      this.name = name;
      this.strValue = strValue;
      intValue = -1;
      dblValue = -1;
      booValue = false;
      type = String.class;
   }
   
   Specification(String name, double dblValue)
   {
      this.name = name;
      this.dblValue = dblValue;
      intValue = -1;
      strValue = "";
      booValue = false;
      type = Double.TYPE;
   }
   
   Specification(String name, boolean booValue)
   {
      this.name = name;
      this.booValue = booValue;
      intValue = -1;
      strValue = "";
      dblValue = -1;
      type = Boolean.TYPE;
   }
   
   public boolean equals(Object o)
   {
      if(o == null || !(o instanceof Specification)) 
         return false;
      Specification other = (Specification) o;
      return type.equals(other.type) 
             && name.equals(other.name) 
             && intValue == other.intValue 
             && strValue.equals(other.strValue)
             && dblValue == other.dblValue 
             && booValue == other.booValue;
   }
   
   public int hashCode()
   {
      return name.hashCode();
   }
   
   final Class type;
   final String name;
   final int intValue;
   final String strValue;
   final double dblValue;
   final boolean booValue;
}