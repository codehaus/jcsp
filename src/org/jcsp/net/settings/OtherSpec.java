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
 * Used internally within the JCSP network infrastructure to represent misellaneous specifications.
 *
 * @author Quickstone Technologies Limited
 */
public class OtherSpec extends Spec
{
   OtherSpec(String name, int value, boolean isReq)
   {
      super(name, false, isReq);
      intValue = value;
      strValue = "" + value;
      type = Integer.TYPE;
   }
   
   OtherSpec(String name, double value, boolean isReq)
   {
      super(name, false, isReq);
      dblValue = value;
      strValue = "" + value;
      type = Double.TYPE;
   }
   
   OtherSpec(String name, boolean value, boolean isReq)
   {
      super(name, false, isReq);
      booValue = value;
      strValue = "" + value;
      type = Boolean.TYPE;
   }
   
   OtherSpec(String name, String value, boolean isReq)
   {
      super(name, false, isReq);
      strValue = value;
      type = String.class;
   }
   
   public Class getType()
   {
      return type;
   }
   
   public int getIntValue()
   {
      return intValue;
   }
   
   public double getDoubleValue()
   {
      return dblValue;
   }
   
   public boolean getBooleanValue()
   {
      return booValue;
   }
   
   public String getStringValue()
   {
      return strValue;
   }
   
   public boolean equals(Object o)
   {
      if(o instanceof OtherSpec)
      {
         OtherSpec other = (OtherSpec) o;
         if(getName().equals(other.getName()) && type.equals(other.type))
         {
            if(type.equals(Integer.TYPE))
               return intValue == other.intValue;
            else if(type.equals(Double.TYPE))
               return dblValue == other.dblValue;
            else if(type.equals(Boolean.TYPE))
               return booValue == other.booValue;
            else if(type.equals(String.class))
               return strValue == other.strValue;
         }
      }
      return false;
   }
   
   private Class type;
   private int intValue = -1;
   private double dblValue = -1.0;
   private boolean booValue = false;
   private String strValue = null;
}