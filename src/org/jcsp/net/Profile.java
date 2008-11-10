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

import org.jcsp.net.settings.*;
import java.util.*;

/**
 * <p>
 * This class is used to hold profiles of Nodes and Links.
 * </p>
 * <p>
 * Node profiles are not currently used.
 * </p>
 *
 * Profile functionality is still being implemented.
 *
 * @author Quickstone Technologies Limited
 */
public class Profile
{
   static void createNewLinkProfile(String name, Requirement[] requirements, boolean exact)
   {
      Profile p = new Profile(name, requirements, exact, false);
      linkProfiles.put(name, p);
   }
   
   static void createNewNodeProfile(String name, Requirement[] requirements, boolean exact)
   {
      Profile p = new Profile(name, requirements, exact, false);
      nodeProfiles.put(name, p);
   }
   
   
   /** Returns a link profile of a certain name from a static collection of
    * link profiles.
    * @param name the name of the <CODE>Profile</CODE> to obtain.
    * @return a matching link profile or null if no match is found.
    */
   public static Profile getLinkProfile(String name)
   {
      return (Profile)linkProfiles.get(name);
   }
   
   /** Returns a Node profile of a certain name from a static collection of
    * Node profiles.
    * @param name the name of the <CODE>Profile</CODE> to obtain.
    * @return a matching Node profile or null if no match is found.
    */
   public static Profile getNodeProfile(String name)
   {
      return (Profile)nodeProfiles.get(name);
   }
   
   static Profile getAlwaysMatchProfile()
   {
      return alwaysMatchProfile;
   }
   
   private static Profile alwaysMatchProfile = new Profile("AlwaysMatchProfile", new Requirement[] {}, false, true);
   
   static Profile getProtocolProfile(ProtocolID protocolID)
   {
      return new Profile("ProtocolProfile " + protocolID.getPosition(),
                         new Requirement[] 
                        {
                           new Requirement(XMLConfigConstants.REQ_NAME_PROTOCOL,
                                           XMLConfigConstants.SPEC_NAME_PROTOCOL,
                                           XMLConfigConstants.REQ_COMPARATOR_EQUALS,
                           protocolID.getClass().toString())
                        }, 
                        false, true);
   }
   
   private static Hashtable linkProfiles = new Hashtable();
   private static Hashtable nodeProfiles = new Hashtable();
   
   private Profile(String name, Requirement[] requirements, boolean exact, boolean system)
   {
      this.name = name;
      this.exact = exact;
      this.system = system;
      this.requirements = requirements;
      Arrays.sort(requirements, new Comparator()
                                {
                                   public int compare(Object o1, Object o2)
                                   {
                                      Requirement r1 = (Requirement) o1;
                                      Requirement r2 = (Requirement) o2;
                                      return r1.specName.compareTo(r2.specName);
                                   }
                                });
   }
   
   /**
    * This tests whether a an array of Specification objects meet the
    * requirements of this profile.
    *
    * This method will sort the array of specs in order of name. This sort
    * will change the original array.
    *
    * @returns	-1 if the specs do not match, 0 if they do not contradict or
    *			1 if they are a positive match.
    */
   int matches(Specification[] specs)
   {
      //sort the array
      if(specs == null)
      {
         if(requirements == null || requirements.length == 0)
            return 1;
         else
            return 0;
      }
      Arrays.sort(specs, new Comparator()
                         {
                            public int compare(Object o1, Object o2)
                            {
                               Specification s1 = (Specification) o1;
                               Specification s2 = (Specification) o2;
                               return s1.name.compareTo(s2.name);
                            }
                         });
      int match = 0;
      int rPos = 0;
      int sPos = 0;
      
      while(rPos < requirements.length && sPos < specs.length && match != -1)
      {
         int comparison = requirements[rPos].specName.compareTo(specs[sPos].name);
         if(comparison == 0)
         {
            if(requirements[rPos].matches(specs[sPos]))
               match++;
            else
               //match failed - return as profile definitely doesn't match
               return -1;
            rPos++;
         }
         else if(comparison < 0)
            rPos++;
         else if(comparison > 0)
            sPos++;
      }
      if (match == requirements.length)
         //every requirement had a match
         return 1;
      else
         //no requirement was contradicted by a specification but
         //there were insufficient specifications to match
         //all the requirements
         return 0;
   }
   
   boolean requiresExactMatch()
   {
      return exact;
   }
   
   /** Compares this Profile with another object.
    * @param o another object to compare with this object.
    * @return <CODE>true</CODE> iff the supplied object is a <CODE>Profile</CODE> object that
    * is exactly equal.
    */
   public boolean equals(Object o)
   {
      if(o==null || !(o instanceof Profile))
         return false;
      Profile other = (Profile) o;
      return name.equals(other.name) 
             && Arrays.equals(requirements, other.requirements) 
             && exact == other.exact
             && system == other.system;
   }
   
   /**
    * Returns a hash code for this object that follows the
    * standard rule for hash codes stated in the <CODE>Object</CODE> class.
    * @return an <CODE>int</CODE> hash code for this object.
    */
   public int hashCode()
   {
      return name.hashCode();
   }
   
   private String name;
   private boolean exact;
   private boolean system;
   private Requirement[] requirements;
}