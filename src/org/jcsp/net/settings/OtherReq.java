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
 * Used internally within the JCSP network infrastructure to represent miscellaneous requirements.
 *
 * @author Quickstone Technologies Limited
 */
class OtherReq extends OtherSpec implements Req
{
   
   OtherReq(String name, int value, String comparator)
   {
      super(name, value, true);
      this.comparator = comparator;
   }
   
   OtherReq(String name, double value, String comparator)
   {
      super(name, value, true);
      this.comparator = comparator;
   }
   
   OtherReq(String name, boolean value, String comparator)
   {
      super(name, value, true);
      this.comparator = comparator;
   }
   
   OtherReq(String name, String value, String comparator)
   {
      super(name, value, true);
      this.comparator = comparator;
   }
   
   public String getComparator()
   {
      return comparator;
   }
   
   public boolean equals(Object o)
   {
      if(o instanceof OtherReq)
      {
         OtherReq other = (OtherReq) o;
         if (getName().equals(other.getName()) && getType().equals(other.getType()) && comparator.equals(other.comparator))
         {
            if (getType().equals(Integer.TYPE))
               return getIntValue() == other.getIntValue();
            else if (getType().equals(Double.TYPE))
               return getDoubleValue() == other.getDoubleValue();
            else if (getType().equals(Boolean.TYPE))
               return getBooleanValue() == other.getBooleanValue();
            else if (getType().equals(String.class))
               return getStringValue() == other.getStringValue();
         }
      }
      return false;
   }
   
   public String toString()
   {
      StringBuffer sb = new StringBuffer();
      sb.append("<Req name=\"" + getName() + "\" comparator=\"" + comparator + "\" value=\"" + getStringValue() + "\" />");
      return sb.toString();
   }
   
   private String comparator;
}