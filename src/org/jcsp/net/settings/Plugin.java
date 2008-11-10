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
 * Used internally within the JCSP network infrastructure to represent a plug-in.
 *
 * @author Quickstone Technologies Limited
 */
public class Plugin
{
   public Plugin(String name, Class pluginClass)
   {
      this.name = name;
      this.pluginClass = pluginClass;
   }
   
   public String getName()
   {
      return name;
   }
   
   public Class getPluginClass()
   {
      return pluginClass;
   }
   
   public boolean equals(Object o)
   {
      if(o instanceof Plugin)
      {
         Plugin other = (Plugin) o;
         return name.equals(other.name) && pluginClass.equals(other.pluginClass);
      }
      return false;
   }
   
   public int hashCode()
   {
      return name.hashCode();
   }
   
   public String toString()
   {
      StringBuffer sb = new StringBuffer();
      sb.append("<Plugin name=\"" + name + "\" pluginClass=\"" + pluginClass + "\" />");
      return sb.toString();
   }
   
   private String name;
   private Class pluginClass;
}