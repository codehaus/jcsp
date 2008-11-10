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

import java.util.*;

/**
 * Unsed internally within the JCSP network infrastructure to represent a set of addresses.
 *
 * @author Quickstone Technologies Limited
 */
public class Addresses
{
   public void addAddress(Address a)
   {
      if(a != null)
      {
         if(!addresses.contains(a))
         {
            addresses.put(a, a);
            lastAddress = a;
         }
      }
      else
         throw new AddressAlreadyExistsException("Already have an address of value " + a.getValue() + 
                                                 " for protocol " + a.getProtocolID());
   }
   
   public void removeAddress(Address a)
   {
      if(addresses.contains(a))
         addresses.remove(a);
   }
   
   public Address[] getAddresses()
   {
      Address[] toReturn = new Address[addresses.size()];
      return (Address[])addresses.keySet().toArray(toReturn);
   }
   
   public Address getLastAddress()
   {
      return lastAddress;
   }
   
   public String toString()
   {
      StringBuffer sb = new StringBuffer();
      sb.append("<Addresses>\n");
      Address[] addresses = getAddresses();
      for(int i=0; i<addresses.length; i++)
         sb.append(JCSPConfig.tabIn(addresses[i].toString())).append("\n");
      sb.append("</Addresses>");
      return sb.toString();
   }
   
   private Hashtable addresses = new Hashtable();
   private Address lastAddress = null;
   
   static class AddressAlreadyExistsException extends RuntimeException
   {
      private AddressAlreadyExistsException(String msg)
      {
         super(msg);
      }
   }
}