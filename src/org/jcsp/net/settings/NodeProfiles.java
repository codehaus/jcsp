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
 * Used internally within the JCSP network infrastructure to represent a set of node profiles.
 *
 * @author Quickstone Technologies Limited
 */
public class NodeProfiles
{
   public void addProfile(NodeProfile p)
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
            throw new NodeProfileAlreadyExistsException("Already have a profile named " + p.getName());
      }
   }
   
   public void removeProfile(NodeProfile p)
   {
      if(profiles.contains(p))
      {
         profiles.remove(p);
         profileNameMap.remove(p.getName());
      }
   }
   
   public NodeProfile getProfile(String name)
   {
      return (NodeProfile) profileNameMap.get(name);
   }
   
   public NodeProfile[] getProfiles()
   {
      NodeProfile[] toReturn = new NodeProfile[profiles.size()];
      return (NodeProfile[])profiles.keySet().toArray(toReturn);
   }
   
   public NodeProfile getLastProfile()
   {
      return lastProfile;
   }
   
   public String toString()
   {
      StringBuffer sb = new StringBuffer();
      sb.append("<NodeProfiles>\n");
      NodeProfile[] profiles = getProfiles();
      for(int i=0; i<profiles.length; i++)
         sb.append(JCSPConfig.tabIn(profiles[i].toString())).append("\n");
      sb.append("</NodeProfiles>");
      return sb.toString();
   }
   
   private Hashtable profiles = new Hashtable();
   private Hashtable profileNameMap = new Hashtable();
   private NodeProfile lastProfile = null;
   
   static class NodeProfileAlreadyExistsException extends RuntimeException
   {
      private NodeProfileAlreadyExistsException(String msg)
      {
         super(msg);
      }
   }
}