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

import java.io.Serializable;

/**
 * <p>This is an abstract class that is sub-classed by classes whose
 * instances should represent some kind of identifier and be part of a
 * hierarchy of <code>AbstractID</code> objects.</p>
 *
 * <p>This class has no public or protected constructor so it may
 * not be sub-classed by JCSP users. JCSP.NET defines four implementations
 * of this class; <code>{@link ApplicationID}</code>,
 * <code>{@link NodeID}</code>, <code>{@link DomainID}</code>
 * and <code>{@link GlobalID}</code>.</p>
 *
 * <p>
 * An <code>ApplicationID</code> object identifies a JCSP application.
 * A JCSP Application is formed of a process network initiated at a
 * particular Node, but processes launched on a remote Node may
 * also be part of the same application. The parent <code>AbstractID</code
 * of <code>ApplicationID</code> objects are <code>NodeID</code>
 * objects.
 * </p>
 * <p>
 * A <code>NodeID</code> object identifies a particular JCSP Node. A
 * JCSP Node is a Java Virtual Machine that has been initialized
 * to form part of a JCSP.NET network. The parent <code>AbstractID</code
 * of <code>NodeID</code> objects are <code>DomainID</code>
 * objects.
 * </p>
 * <p>
 * A <code>DomainID</code> object represents a domain of JCSP Nodes.
 * Node Domains are not presently implemented but this class is
 * included for completeness. In the future, it is envisaged that
 * domain controllers will be introduced. At present, all
 * <code>NodeID</code> objects have a parent <code>DomainID</code>
 * equal to the Null Domain (this has a zero length string name).
 * </p>
 * <p>
 * A <code>GlobalID</code> object represents the global Node domain.
 * This includes all domains, their sub-domains, and all Nodes within
 * sub-domains. There only ever needs to be a single instance of this
 * class. The parent of the Null Domain is a
 * <code>GlobalID</code> object.
 * </p>
 *
 * @author Quickstone Technologies Limited
 */

public abstract class AbstractID implements Serializable
{
   AbstractID()
   {
   }
   
   /**
    * Returns this instance's parent <code>AbstractID</code> object.
    *
    * @return the parent <code>AbstractID</code> of this object.
    */
   public abstract AbstractID getParentID();
   
   /**
    * This tests whether another ID is on the same branch of a
    * hierachy. Returns true if either the supplied object
    * is a child (or a child of child etc.) of this object or
    * if the supplied object is a parent (or a parent of a parent
    * etc.) of this object.
    *
    * @return a <code>boolean</code> indicating whether or not
    * 			the supplied object is on the same branch.
    */
   abstract boolean onSameBranch(AbstractID abstractID);
}