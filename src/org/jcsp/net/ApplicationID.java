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
 * A Class whose instances represent a unique identifier for a JCSP.NET application.
 * An application is defined as being a process network that forms a complete program.
 * Applications have a parent Node which is the Node on which the application was started.
 * </p>
 * <p>
 * For a full explanation, see <code>{@link AbstractID}</code>.
 * </p>
 * @author Quickstone Technologies Limited
 */
public class ApplicationID extends AbstractID implements Serializable
{
   private NodeID nodeID;
   private int appID;
   
   ApplicationID(NodeID nodeID, int appID)
   {
      this.nodeID = nodeID;
      this.appID = appID;
   }
   
   /**
    * <p>
    * Returns a <CODE>String</CODE> representation of this object.
    * The current implemenation returns a human readable
    * <CODE>String</CODE> which shows the application's home
    * <CODE>NodeID</CODE> and the integer application id.
    * </p>
    *
    * @return the <CODE>String</CODE> representing this Application ID.
    */
   public String toString()
   {
      return "Application " + " " + nodeID + " " + appID;
   }
   
   /**
    * <p>
    * Compares the supplied <CODE>Object</CODE> with this
    * <CODE>ApplicationID</CODE>.
    * </p>
    * @param o another <CODE>Object</CODE> to compare with
    *         this <CODE>ApplicationID</CODE>.
    * @return <CODE>true</CODE> iff the parameter o is an
    *          <CODE>ApplicationID</CODE> that represents the
    *          same application as this object.
    */
   public boolean equals(Object o)
   {
      if (o == null || !(o instanceof ApplicationID))
         return false;
      ApplicationID other = (ApplicationID)o;
      if (nodeID.equals(other.nodeID))
         return appID == other.appID;
      return false;
   }
   
   /**
    * <p>
    * Returns a hash code for this Object. Two equal
    * <CODE>ApplicationID</CODE> objects return the same hash code.
    * </p>
    * @return an <CODE>int</CODE> hash code.
    */
   public int hashCode()
   {
      return nodeID.hashCode() + appID;
   }
   
   boolean onSameBranch(AbstractID abstractID)
   {
      if (abstractID == null)
         return false;
      if (abstractID instanceof ApplicationID)
         return this.equals(abstractID);
      return nodeID.onSameBranch(abstractID);
   }
   /**
    * Returns the parent <code>NodeID</code> of this
    * object.
    *
    * @return the parent <code>NodeID</code>.
    */
   public AbstractID getParentID()
   {
      return nodeID;
   }
}