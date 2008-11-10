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
 * Used internally within the JCSP network infrastructure to represent a set of services.
 *
 * @author Quickstone Technologies Limited
 */
public class Services
{
   public void addService(Service s)
   {
      if(s != null)
      {
         if(!serviceNameMap.containsKey(s.getName()))
         {
            serviceNameMap.put(s.getName(), s);
            lastService = s;
            return;
         }
      }
      throw new ServiceAlreadyExistsException("Already have a service named " + s.getName());
   }
   
   public void removeService(Service s)
   {
      if(serviceNameMap.containsKey(s.getName()))
         serviceNameMap.remove(s.getName());
   }
   
   public Service getService(String name)
   {
      return (Service) serviceNameMap.get(name);
   }
   
   public Service getLastService()
   {
      return lastService;
   }
   
   public Service[] getServices()
   {
      Service[] toReturn = new Service[serviceNameMap.size()];
      return (Service[])serviceNameMap.values().toArray(toReturn);
   }
   
   public String toString()
   {
      StringBuffer sb = new StringBuffer();
      sb.append("<Services>\n");
      Service[] services = getServices();
      for(int i=0; i<services.length; i++)
         sb.append(JCSPConfig.tabIn(services[i].toString())).append("\n");
      sb.append("</Services>");
      return sb.toString();
   }
   
   private Hashtable serviceNameMap = new Hashtable();
   private Service lastService = null;
   
   static class ServiceAlreadyExistsException extends RuntimeException
   {
      private ServiceAlreadyExistsException(String msg)
      {
         super(msg);
      }
   }
}