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
 * Used internally within the JCSP network infrastructure to represent a service.
 *
 * @author Quickstone Technologies Limited
 */
public class Service
{
   public Service(String name, Class serviceClass, boolean run, int position)
   {
      this.name = name;
      this.serviceClass = serviceClass;
      this.run = run;
      this.position = position;
   }
   
   public void addSetting(Setting setting)
   {
      settings.addSetting(setting);
   }
   
   public void removeSetting(Setting setting)
   {
      settings.removeSetting(setting);
   }
   
   public Setting[] getSettings()
   {
      return settings.getSettings();
   }
   
   public Setting getSetting(String name)
   {
      return settings.getSetting(name);
   }
   
   public void addAddressSetting(AddressSetting setting)
   {
      addressSettings.addSetting(setting);
   }
   
   public void removeAddressSetting(AddressSetting setting)
   {
      addressSettings.removeSetting(setting);
   }
   
   public AddressSetting[] getAddressSettings()
   {
      Setting[] settings = addressSettings.getSettings();
      AddressSetting[] toReturn = new AddressSetting[settings.length];
      for(int i=0; i<toReturn.length; i++)
         toReturn[i] = getAddressSetting(settings[i].getName());
      return toReturn;
   }
   
   public AddressSetting getAddressSetting(String name)
   {
      return (AddressSetting) addressSettings.getSetting(name);
   }
   
   public String getName()
   {
      return name;
   }
   
   public Class getServiceClass()
   {
      return serviceClass;
   }
   
   public boolean getRun()
   {
      return run;
   }
   
   public int getPosition()
   {
      return position;
   }
   
   
   public String toString()
   {
      StringBuffer sb = new StringBuffer();
      sb.append("<Service name=\"" + name + "\" class=\"" + serviceClass + "\" run=\"" + run + "\">\n");
      sb.append(JCSPConfig.tabIn(settings.toString())).append("\n");
      sb.append(JCSPConfig.tabIn(addressSettings.toString())).append("\n");
      sb.append("</Service>");
      return sb.toString();
   }
   
   public boolean equals(Object o)
   {
      if(o instanceof Service)
      {
         Service other = (Service) o;
         return name.equals(other.name) && run == other.run && settings.equals(other.settings)
                && addressSettings.equals(other.addressSettings);
      }
      return false;
   }
   
   public int hashCode()
   {
      return name.hashCode();
   }
   
   private String name;
   private Class serviceClass;
   private boolean run;
   private int position;
   private Settings settings = new Settings();
   private Settings addressSettings = new Settings("AddressSettings");
}