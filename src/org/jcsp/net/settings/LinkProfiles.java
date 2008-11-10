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
import org.jcsp.net.*;

/**
 * Used internally within the JCSP network infrastructure to represent a set of link profiles.
 *
 * @author Quickstone Technologies Limited
 */
public class LinkProfiles
{
   public void addProfile(LinkProfile p)
   {
      if(p != null)
      {
         if(!profiles.contains(p) && !profileNameMap.containsKey(p.getName()))
         {
            profiles.put(p, p);
            profileNameMap.put(p.getName(), p);
            lastProfile = p;
         }
         else
            throw new LinkProfileAlreadyExistsException("Already have a profile named " + p.getName());
      }
      else
         Node.err.log(this, "Error: Null profile");
   }
   
   public void removeProfile(LinkProfile p)
   {
      if(profiles.contains(p))
      {
         profiles.remove(p);
         profileNameMap.remove(p.getName());
      }
   }
   
   public LinkProfile getProfile(String name)
   {
      return (LinkProfile) profileNameMap.get(name);
   }
   
   public LinkProfile[] getProfiles()
   {
      LinkProfile[] toReturn = new LinkProfile[profiles.size()];
      return (LinkProfile[])profiles.keySet().toArray(toReturn);
   }
   
   public LinkProfile getLastProfile()
   {
      return lastProfile;
   }
   
   public String toString()
   {
      StringBuffer sb = new StringBuffer();
      sb.append("<LinkProfiles>\n");
      LinkProfile[] profiles = getProfiles();
      for(int i=0; i<profiles.length; i++)
         sb.append(JCSPConfig.tabIn(profiles[i].toString())).append("\n");
      sb.append("</LinkProfiles>");
      return sb.toString();
   }
   
   private Hashtable profiles = new Hashtable();
   private Hashtable profileNameMap = new Hashtable();
   private LinkProfile lastProfile = null;
   
   static class LinkProfileAlreadyExistsException extends RuntimeException
   {
      private LinkProfileAlreadyExistsException(String msg)
      {
         super(msg);
      }
   }
}