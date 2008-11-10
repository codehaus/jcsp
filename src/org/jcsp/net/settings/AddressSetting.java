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
 * Used internally within the JCSP network infrastructure to represent an address setting.
 *
 * @author Quickstone Technologies Limited
 */
public class AddressSetting extends Setting
{
   public AddressSetting(String name, String value, String protocolID)
   {
      super(name, value);
      this.protocolID = protocolID;
   }
   
   public String getProtocolID()
   {
      return protocolID;
   }
   
   public AddressSetting getAlternate()
   {
      return alternate;
   }
   
   public boolean addAlternate(AddressSetting alternate)
   {
      if(alternate == null || !getName().equals(alternate.getName()))
         return false;
      AddressSetting prevRAdd = this;
      AddressSetting rAdd = alternate;
      while(rAdd != null)
      {
         if(rAdd == this || rAdd == alternate) 
            break;
         prevRAdd = rAdd;
         rAdd = rAdd.alternate;
      }
      prevRAdd.alternate = alternate;
      return true;
   }
   
   public boolean equals(Object o)
   {
      if(o instanceof AddressSetting)
      {
         AddressSetting other = (AddressSetting) o;
         if(protocolID.equals(other.protocolID) && getName().equals(other.getName()) && getValue().equals(other.getValue()))
            return true;
      }
      return false;
   }
   
   public String toString()
   {
      StringBuffer sb = new StringBuffer();
      sb.append("<Setting name=\"" + getName() + "\" value=\"" + getValue() + "\" protocolID=\"" + protocolID + "\" />");
      if(alternate != null)
         sb.append("\n").append(alternate.toString());
      return sb.toString();
   }
   
   private String protocolID;
   private AddressSetting alternate = null;
}