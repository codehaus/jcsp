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
 * Used internally within the JCSP network infrastructure to represent a set of system settings.
 *
 * @author Quickstone Technologies Limited
 */
public class Settings
{
   
   public Settings()
   {
      name = "Settings";
   }
   
   public Settings(String name)
   {
      this.name = name;
   }
   
   public void addSetting(Setting s)
   {
      if(s != null)
      {
         if(!settings.contains(s) && !settingNameMap.containsKey(s.getName()))
         {
            settings.put(s, s);
            settingNameMap.put(s.getName(), s);
         }
      }
      else
         throw new SettingAlreadyExistsException("Already have a setting named " + s.getName());
   }
   
   public void removeSetting(Setting s)
   {
      if(settings.contains(s))
      {
         settings.remove(s);
         settingNameMap.remove(s.getName());
      }
   }
   
   public Setting getSetting(String name)
   {
      return (Setting) settingNameMap.get(name);
   }
   
   public Setting[] getSettings()
   {
      Setting[] toReturn = new Setting[settings.size()];
      return (Setting[])settings.keySet().toArray(toReturn);
   }
   
   public String toString()
   {
      StringBuffer sb = new StringBuffer();
      sb.append("<" + this.name + ">\n");
      Setting[] settings = getSettings();
      for(int i=0; i<settings.length; i++)
         sb.append(JCSPConfig.tabIn(settings[i].toString())).append("\n");
      sb.append("</" + this.name + ">");
      return sb.toString();
   }
   
   private Hashtable settings = new Hashtable();
   private Hashtable settingNameMap = new Hashtable();
   String name;
   
   static class SettingAlreadyExistsException extends RuntimeException
   {
      private SettingAlreadyExistsException(String msg)
      {
         super(msg);
      }
   }
}