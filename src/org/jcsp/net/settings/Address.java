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
 * Used internally within the JCSP network infrastructure to represent a single address.
 *
 * @author Quickstone Technologies Limited
 */
public class Address
{
   public Address(String protocolID, String value, boolean unique)
   {
      this.protocolID = protocolID;
      this.value = value;
      this.unique = unique;
   }
   
   public String getProtocolID()
   {
      return protocolID;
   }
   
   public String getValue()
   {
      return value;
   }
   
   public void addSpec(Spec spec)
   {
      specs.addSpec(spec);
   }
   
   public void removeSpec(Spec spec)
   {
      specs.removeSpec(spec);
   }
   
   public Spec[] getSpecs()
   {
      return specs.getSpecs();
   }
   
   public boolean isUnique()
   {
      return unique;
   }
   
   public boolean equals(Object o)
   {
      if(o instanceof Address)
      {
         Address other = (Address) o;
         if(protocolID.equals(other.protocolID) && value.equals(other.value))
            return true;
      }
      return false;
   }
   
   public String toString()
   {
      StringBuffer sb = new StringBuffer();
      sb.append("<Address protocolID=\"" + protocolID + "\" value=\"" + value + "\" unique=\"" + unique + "\">\n");
      sb.append(JCSPConfig.tabIn(specs.toString())).append("\n");
      sb.append("</Address>");
      return sb.toString();
   }
   
   private String protocolID;
   private String value;
   private boolean unique;
   private Specs specs = new Specs();
}