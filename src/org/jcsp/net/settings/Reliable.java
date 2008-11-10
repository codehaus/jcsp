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
 * Used internally within the JCSP network infrastructure to represent a reliable protocol.
 *
 * @author Quickstone Technologies Limited
 */
public class Reliable extends Spec implements Req
{
   Reliable(boolean reliable, boolean isReq)
   {
      super(SPEC_NAME_RELIABLE, true, isReq);
      this.reliable = reliable;
   }
   
   public String getStringValue()
   {
      return "" + reliable;
   }
   
   public boolean getValue()
   {
      return reliable;
   }
   
   public Class getType()
   {
      return Boolean.TYPE;
   }
   
   public String getComparator()
   {
      return REQ_COMPARATOR_EQUALS;
   }
   
   public int getIntValue()
   {
      throw new UnsupportedOperationException("Type is boolean");
   }
   
   public double getDoubleValue()
   {
      throw new UnsupportedOperationException("Type is boolean");
   }
   
   public boolean getBooleanValue()
   {
      return getValue();
   }
   
   public boolean equals(Object o)
   {
      if(o instanceof Reliable)
      {
         Reliable other = (Reliable) o;
         return reliable == other.reliable;
      }
      return false;
   }
   
   public int hashCode()
   {
      if(reliable)
         return 1;
      else
         return 0;
   }
   
   private boolean reliable;
}