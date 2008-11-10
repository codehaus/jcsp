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
 * Used internally within the JCSP network infrastructure to represent a protocol requirement.
 *
 * @author Quickstone Technologies Limited
 */
public class ReqProtocol extends Spec implements Req, XMLConfigConstants
{
   ReqProtocol(String protocolid)
   {
      super(REQ_NAME_PROTOCOL, true);
      this.protocolid = protocolid;
   }
   
   public String getStringValue()
   {
      return protocolid;
   }
   
   public String getComparator()
   {
      return REQ_COMPARATOR_EQUALS;
   }
   
   public int getIntValue()
   {
      throw new UnsupportedOperationException("Type is string");
   }
   
   public double getDoubleValue()
   {
      throw new UnsupportedOperationException("Type is string");
   }
   
   public boolean getBooleanValue()
   {
      throw new UnsupportedOperationException("Type is string");
   }
   
   public Class getType()
   {
      return String.class;
   }
   
   public String getValue()
   {
      return protocolid;
   }
   
   public boolean equals(Object o)
   {
      if(o instanceof ReqProtocol)
      {
         ReqProtocol other = (ReqProtocol) o;
         return protocolid.equals(other.protocolid);
      }
      return false;
   }
   
   public int hashCode()
   {
      return protocolid.hashCode();
   }
   
   private String protocolid;
}