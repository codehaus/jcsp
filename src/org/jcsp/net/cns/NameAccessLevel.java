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

package org.jcsp.net.cns;

import java.io.*;
import org.jcsp.net.*;

/**
 * This class allows name spaces to be identified around
 * <code>AbstractID</code> objects (currently including
 * {@link org.jcsp.net.ApplicationID},
 * {@link org.jcsp.net.NodeID},
 * {@link org.jcsp.net.DomainID} and
 * {@link org.jcsp.net.GlobalID}
 * ). An <code>AbstractID</code> object can have a single parent
 * <code>AbstractID</code> object. This allows a tree-like hierarchy
 * to be formed which in turn allows a name space hierarchy.
 * </p>
 * <p>
 * For example, two <code>ApplicationID</code> objects, A and B, may each
 * have a parent <code>NodeID</code> Z. The namespaces created around
 * A and B will each be a superset of the namespace of Z. Channels registered
 * in Z's namespace can be resolved in either A's or B's namespace. A channel
 * registered in A's namespace may neither be resolved in Z's namespace nor
 * B's.
 * </p>
 *
 * @author Quickstone Technologies Limited
 */
public final class NameAccessLevel implements Serializable
{
   /*------------Attributes-----------------------------------------------------*/
   private final AbstractID abstractID;

   /**
    * The <code>NameAccessLevel</code> for the global namespace.
    */
   public static final NameAccessLevel GLOBAL_ACCESS_LEVEL = new NameAccessLevel(GlobalID.instance);
   
   /*------------Constructor----------------------------------------------------*/
   
   /**
    * <p>
    * Constructor which takes an <code>AbstractID</code> to use
    * for identifying the namespace.
    *
    */
   public NameAccessLevel(AbstractID abstractID)
   {
      this.abstractID = abstractID;
   }
   
   /*------------Methods overriden from Object----------------------------------*/
   
   /**
    * Compares an object with this object.
    *
    * @return <code>ture</code> iff the other object is a
    *          <code>NameAccessLevel</code> object which has an
    *          equal underlying <code>AbstractID</code>.
    *
    * @see org.jcsp.net.AbstractID
    */
   public boolean equals(Object o)
   {
      if(o == null || !(o instanceof NameAccessLevel)) 
         return false;
      NameAccessLevel other = (NameAccessLevel) o;
      return abstractID.equals(other.abstractID);
   }
   
   /**
    * Returns a hash code for this object obeying the standard rules
    * for a hash code.
    *
    * @see java.lang.Object#hashCode()
    */
   public int hashCode()
   {
      return abstractID.hashCode();
   }
   
   /**
    * Returns a string representation of this object.
    *
    * @return a human readable string.
    */
   public String toString()
   {
      return abstractID.toString();
   }
   
   /*------------Package level methods------------------------------------------*/
   
   /**
    * Accessor for the underlying <code>AbstractID</code> object.
    *
    * @return the underlying <code>AbstractID</code>.
    */
   AbstractID getLevelAbstractID()
   {
      return abstractID;
   }
}