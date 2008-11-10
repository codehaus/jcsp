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

import java.io.*;

/**
 * <p>
 * A Class whose instances represent a unique identifier for a JCSP.NET domain.
 * </p>
 * <p>
 * See {@link AbstractID} for a further explanation of domains and the use
 * of this class.
 * </p>
 *
 * @author Quickstone Technologies Limited
 */
public final class DomainID extends AbstractID implements Serializable, Comparable
{
   private boolean nullDom = false;
   private String name = "";
   
   private static final String NullDomainStringForm = "NullDomain";
   private static final String DomainStringFormPrefix = "Domain :";
   
   /**
    * Constructor which constructs an ID for the Null Domain.
    *
    */
   DomainID()
   {
      //constructor for null domain
      nullDom = true;
   }
   
   /**
    * Public constructor for a <CODE>DomainID</CODE> which takes
    * the name of the domain as a parameter.
    * @param name The name of the domain.
    */
   public DomainID(String name)
   {
      this.name = name;
   }
   
   /**
    *
    * @deprecated not needed now channel names abstracted
    */
   static DomainID createFromStringForm(String str) throws IllegalArgumentException
   {
      if (str == null)
         throw new IllegalArgumentException("No String supplied");
      if (str.equals(DomainStringFormPrefix + NullDomainStringForm))
         return getNullDomainID();
      else
         return new DomainID(str.substring(DomainStringFormPrefix.length()));
   }
   
   /**
    * This compares an object with this object.
    * @param o an object to compare with this object.
    * @return <CODE>true</CODE> iff the supplied object is a non-null <CODE>DomainID</CODE> which represents the same Domain.
    */
   public boolean equals(Object o)
   {
      if (o == null || !(o instanceof DomainID))
         return false;

      DomainID other = (DomainID) o;
      
      if (nullDom)
      {
         if (other.isNullDomain())
            return true;
         return false;
      }
      //change later to make more secure
      return name.equals(other.getDomainName());
   }
   
   /**
    * <p>
    * Returns an <CODE>int</CODE> hash code for the current object.
    * </p>
    * <p>
    * This obeys the standard hash code rules.
    * </p>
    * @return an <CODE>int</CODE> hash code.
    */
   public int hashCode()
   {
      return name.hashCode();
   }
   
   /**
    * <p>
    * Compares this object with another object and returns whether
    * the supplied object is equals, smaller or larger.
    * </p>
    * @param o The object to compare with this object.
    * @return 0 if the supplied object is equal, a negative integer if the supplied object is smaller or a positive integer if the other object is larger.
    */
   public int compareTo(Object o)
   {
      DomainID other = (DomainID) o;
      if (nullDom && other.nullDom)
         return 0;
      return name.compareTo(other.name);
   }
   
   boolean onSameBranch(AbstractID abstractID)
   {
      if (abstractID == null)
         return false;
      if (abstractID instanceof GlobalID)
         return true;
      if (abstractID instanceof DomainID)
         return this.equals(abstractID);
      if (abstractID instanceof NodeID || abstractID instanceof ApplicationID)
         //abstractID below this in name hierachy - ask it
         return abstractID.onSameBranch(this);
      return false;
   }
   
   boolean isNullDomain()
   {
      return nullDom;
   }
   
   static DomainID getNullDomainID()
   {
      return new DomainID();
   }
   
   /**
    * <p>
    * Public accessor for the Domain name.
    * </p>
    * <p>
    * Returns "" if this represents the Null Domain.
    * </p>
    * @return the domain name as a <CODE>String</CODE>.
    */
   public String getDomainName()
   {
      return name;
   }
   
   /**
    * <p>
    * Returns a human readable <CODE>String</CODE> showing this domain
    * name and any parent domains.
    * </p>
    * @return the human readable <CODE>String</CODE>.
    */
   public String toString()
   {
      if (nullDom)
         return "\\" + getParentID();
      return getParentID() + "\\" + name;
   }
   
   /**
    *
    * @deprecated not needed now channel names abstracted
    */
   String getStringForm()
   {
      if (nullDom)
         return DomainStringFormPrefix + NullDomainStringForm;
      else
         return DomainStringFormPrefix + name;
   }
   
   /**
    * <p>
    * Returns the identifier representing this domain's parent
    * domain.
    * </p>
    * <p>
    * At present this always returns the global domain's ID.
    * </p>
    * @return the parent <code>AbstractID</code>.
    */
   public AbstractID getParentID()
   {
      return GlobalID.instance;
   }
}