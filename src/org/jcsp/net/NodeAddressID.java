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
 * JCSP.NET communication protocol implementations must provide a concrete
 * implementation of this class. The instances of the class implementations
 * should hold address information for JCSP Node <CODE>LinkServer</CODE>
 * processes. For example, a TCP/IP implementation would most likely hold
 * an IP address and port number.
 * </p>
 * <p>
 * There is no defined way in this class as to how <CODE>NodeAddressID</CODE>
 * objects should be created.  The JCSP infrastructure never looks at the
 * underlying address information, it simply needs to be able compare one address
 * with another. The <CODE>equals(Object)</CODE> should be implemented in such a
 * way that this can be achieved.
 * </p>
 * <p>
 * Implementations of the <CODE>ProtocolID</CODE> class should implement the
 * <CODE>createAddressID(String, boolean)</CODE> method so that
 * <CODE>NodeAddressID</CODE> objects for that protocol can be created from a
 * <CODE>String</CODE>. The <CODE>boolean parameter</CODE> indicates whether or
 * not the address is globally unique.
 * </p>
 *
 * @author Quickstone Technologies Limited
 */
public abstract class NodeAddressID implements Serializable, Cloneable
{
   /**
    * <p>
    * Compares the instance of the class implementing this interface
    * with another object. This should return <CODE>true</CODE> if the other
    * object represents the same address.
    * </p>
    * @param obj another object that should be another <CODE>NodeAddressID</CODE>.
    * @return <CODE>true</CODE> if the other object is another <CODE>NodeAddressID</CODE> representing
    * the same address.
    */
   public abstract boolean equals(Object obj);
   
   /**
    * <p>
    * Returns a hash code for this object. Two <CODE>NodeAddressID</CODE> objects
    * should return the same hash code if they equal as governed by the rules
    * for the <CODE>equals(Object)</CODE> method.
    * </p>
    * @return an <CODE>int</CODE> hash code.
    */
   public abstract int hashCode();
   
   /**
    * <p>
    * Returns a clone of the instance of the class implementing this interface.
    * </p>
    * @throws CloneNotSupportedException if the implementation
    *          class does not support the clone method.
    * @return a clone of the object that this is being called on.
    */
   public Object clone() throws CloneNotSupportedException
   {
      return super.clone();
   }
   
   /**
    * Returns the <CODE>ProtocolID</CODE> for the protocol that is
    * associated with this <CODE>NodeAddressID</CODE>.
    * @return this <CODE>NodeAddressID</CODE> object's <CODE>ProtocolID</CODE>.
    */
   public abstract ProtocolID getProtocolID();
   
   /**
    * <p>
    * Returns <CODE>true</CODE> if this <CODE>NodeAddressID</CODE> is known to be
    * unique within the global org.jcsp.net domain.
    * </p>
    * <p>
    * A Node may have several addresses. If a Node is on an internal network that
    * is connected to the Internet, it may have one local address and one address
    * that is accessible from the Internet. The Internet address may be globally
    * unique whereas the local address may be duplicated in other internal networks.
    * The <CODE>NodeAddressID</CODE> object representing the Internet address should
    * return <CODE>true</CODE> when this method is called whereas the
    * <CODE>NodeAddressID</CODE> representing the local address should return
    * <CODE>false</CODE>.
    * </p>
    * @return <CODE>true</CODE> iff this <CODE>NodeAddressID</CODE> is globally unique.
    */
   public abstract boolean isGloballyUnique();
   
   /**
    * <p>
    * Returns a <CODE>String</CODE> that contains all information necessary for
    * reconstucting this object from a <CODE>String</CODE>. The object is
    * reconstructed by calling the static <CODE>getAddressIDFromString(String)</CODE>
    * method.
    * </p>
    * @return a <CODE>String</CODE> that can be passed to the static <CODE>getAddressIDFromString(String)</CODE>
    * method in order to reconstuct the object.
    * @deprecated This is going to be removed from the API.
    * The purpose of this mechanism was so that channel
    * information could be encoded into anonymous channels'
    * channel names.
    */
   protected abstract String getStringForm();
   
   /**
    * <p>
    * This is is a static method that should be overriden by concrete implementations
    * of this class. Ideally this method would be abstract but the language does
    * not allow abstract static methods.
    * </p>
    * <p>
    * The method constructs a <CODE>NodeAddressID</CODE> object from a
    * <CODE>String</CODE> that is in the form of that returned by the
    * <CODE>getStringForm()</CODE> method. The object returned by this method
    * should equal the object that returned the <CODE>String</CODE> when its
    * <CODE>getStringForm()</CODE> methdo was called.
    * </p>
    *
    * @param stringForm the <CODE>String</CODE> used to construct the <CODE>NodeAddressID</CODE>.
    * @throws IllegalArgumentException if the <CODE>String</CODE> supplied is invalid.
    * @return a newly constructed <CODE>NodeAddressID</CODE> object.
    */
   protected static NodeAddressID getAddressIDFromString(String stringForm)
   throws IllegalArgumentException
   {
      //have made this protected 17/04/2002 - not sure why it wasn't.
      return null;
   }
}