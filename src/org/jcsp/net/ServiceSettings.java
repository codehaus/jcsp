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

import java.util.*;

/**
 * <p>
 * This class is used to hold settings that can be passed to
 * services when they are initialized. Two types of settings can
 * be held. The basic type of settings that can be held are
 * <CODE>String</CODE> name,value pairs. Only one setting can exist per name.
 * The other type of settings that can be held are address settings. Each
 * address settings can hold a list of <CODE>NodeAddressID</CODE> objects
 * against a <CODE>String</CODE> name.
 * </p>
 * <p>
 * The class also has a field for storing the name of the Service. This can
 * be obtained by calling the <CODE>getServiceName()</CODE> method.
 * </p>
 *
 * @author Quickstone Technologies Limited
 */
public class ServiceSettings
{
   /**
    * Constructor which takes the name of the serive for which this object
    * holds settings.
    * @param name the name of the service.
    */
   public ServiceSettings(String name)
   {
      this.name = name;
   }
   
   /**
    * An accessor for obtaining the name of the service for which this object
    * holds settings.
    * @return the <CODE>String</CODE> name of the service.
    */
   public String getServiceName()
   {
      return name;
   }
   
   /**
    * Adds a named address setting.
    * @param name the name of the setting to add.
    * @param address the address to add to the setting.
    */
   public void addAddress(String name, NodeAddressID address)
   {
      Object o = addresses.get(name);
      if(o != null)
      {
         if(o instanceof Vector)
            ((Vector) o).add(address);
         else
         {
            Vector v = new Vector();
            v.add(o);
            v.add(address);
            addresses.put(name, v);
         }
      }
      else
         addresses.put(name, address);
   }
   
   /**
    * Gets the addresses held in a setting of a specified name.
    * @param name the name of the setting
    * @return an array of <CODE>NodeAddressID</CODE> objects held in the setting.
    */
   public NodeAddressID[] getAddresses(String name)
   {
      Object o = addresses.get(name);
      if(o == null)
         return new NodeAddressID[] {};
      else if(o instanceof NodeAddressID)
         return new NodeAddressID[] {(NodeAddressID) o};
      else
      {
         Vector v = (Vector) o;
         return (NodeAddressID[]) v.toArray(new NodeAddressID[v.size()]);
      }
   }
   
   /**
    * Returns an array of <CODE>String</CODE> objects containing the names of all
    * address settings held in this object.
    * @return a <CODE>String</CODE> array of all the address setting names.
    */
   public String[] getAddressNames()
   {
      return (String[])addresses.keySet().toArray(new String[addresses.size()]);
   }
   
   /**
    * Returns an array of <CODE>String</CODE> objects containing the names of all
    * <CODE>String</CODE> name, value settings held in this object.
    * @return a <CODE>String</CODE> array of all the <CODE>String</CODE> name, value setting names.
    */
   public String[] getSettingNames()
   {
      return (String[]) settings.keySet().toArray(new String[settings.size()]);
   }
   
   /**
    * Adds a <CODE>String</CODE> name, value setting to this object.
    * @param name the name of the setting to add.
    * @param value the value of the setting.
    */
   public void addSetting(String name, String value)
   {
      settings.put(name, value);
   }
   
   /**
    * Gets the <CODE>String</CODE> value held in named
    * <CODE>String</CODE> name, value pair setting.
    * @param name the name of the setting of which to obtain the value.
    * @return the value of the setting.
    */
   public String getSetting(String name)
   {
      return (String) settings.get(name);
   }
   
   private Hashtable addresses = new Hashtable();
   private Hashtable settings = new Hashtable();
   private String name;
}