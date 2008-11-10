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
 * Used internally within the JCSP network infrastructure to represent a set of plug-ins.
 *
 * @author Quickstone Technologies Limited
 */
public class Plugins
{
   public void addPlugin(Plugin p)
   {
      if(p != null)
      {
         if(!plugins.contains(p) && !pluginNameMap.containsKey(p.getName()))
         {
            plugins.put(p, p);
            pluginNameMap.put(p.getName(), p);
         }
      }
      else
         throw new PluginAlreadyExistsException("Already have a plugin named " + p.getName());
   }
   
   public void removePlugin(Plugin p)
   {
      if(plugins.contains(p))
      {
         plugins.remove(p);
         pluginNameMap.remove(p.getName());
      }
   }
   
   public Plugin getPlugin(String pluginName)
   {
      return (Plugin) pluginNameMap.get(pluginName);
   }
   
   public Plugin[] getPlugins()
   {
      Plugin[] toReturn = new Plugin[plugins.size()];
      int i = 0;
      for (Enumeration e = plugins.keys(); e.hasMoreElements(); )
         toReturn[i++] = (Plugin)e.nextElement();
      return toReturn;
   }
   
   public String toString()
   {
      StringBuffer sb = new StringBuffer();
      sb.append("<Plugins>\n");
      Plugin[] plugins = getPlugins();
      for (int i=0; i<plugins.length; i++)
         sb.append(JCSPConfig.tabIn(plugins[i].toString())).append("\n");
      sb.append("</Plugins>");
      return sb.toString();
   }
   
   private Hashtable plugins = new Hashtable();
   private Hashtable pluginNameMap = new Hashtable();
   
   static class PluginAlreadyExistsException extends RuntimeException
   {
      private PluginAlreadyExistsException(String msg)
      {
         super(msg);
      }
   }
}