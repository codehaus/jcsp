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
import java.lang.reflect.*;

/**
 * <p>
 * A Unique Identifier for a Node.
 * This class is different from <CODE>NodeID</CODE> in that it does not describe the
 * Node's location.
 * </p>
 * <p>
 * Implementations of this class should provide a constructor that takes a
 * <CODE>String</CODE> parameter which is in the format of the <CODE>String</CODE>
 * returned from the <CODE>getImplStringForm()</CODE> method.
 * </p>
 *
 * @author Quickstone Technologies Limited
 */
public abstract class NodeUI implements Serializable, Comparable
{
   final String getStringForm()
   {
      String[] array = new String[2];
      array[0] = this.getClass().getName();
      array[1] = getImplStringForm();
      return Utils.arrayToString(array);
   }
   
   /** Returns a <CODE>String</CODE> that can be supplied to the
    * static <CODE>createFromStringForm(String)</CODE> method in order to
    * create an equal <CODE>NodeUI</CODE> object.
    *
    * @return a <CODE>String</CODE> containing the necessary information to recreate this <CODE>NodeUI</CODE> object.
    */
   protected abstract String getImplStringForm();
   
   /** Creates a NodeUI from a String in the format of that returned from the
    * <CODE>getImplStringForm()</CODE> method.
    * @param stringForm the <CODE>String</CODE> to use in creating the <CODE>NodeUI</CODE> object.
    * @return a newly created <CODE>NodeUI</CODE> object.
    */
   public static final NodeUI createFromStringForm(String stringForm)
   {
      String[] strings = Utils.stringToArray(stringForm);
      if(strings.length < 2)
         throw new IllegalArgumentException("Incorrect string supplied.");
      try
      {
         Class nodeUIClass = Class.forName(strings[0]);
         Constructor constr = nodeUIClass.getConstructor(new Class[] {String.class});
         NodeUI instance = (NodeUI)constr.newInstance(new Object[] {strings[1]});
         return instance;
      }
      catch (NoSuchMethodException e)
      {
         throw new IllegalArgumentException("Constructor with String does not exist.");
      }
      catch (Exception e)
      {
         throw new IllegalArgumentException("Unable to create constructor from String provided.");
      }
   }
   
   /** Compares this <CODE>NodeUI</CODE> with another object.
    * This method checks that the supplied object is a <CODE>NodeUI</CODE> object
    * and then calls the implementation of the <CODE>checkEqual(NodeUI)</CODE>
    * method and returns its result value as the result of this method.
    * @param o an object to compare with this object.
    * @return <CODE>true</CODE> iff the supplied object is a <CODE>NodeUI</CODE> and the
    * result of a call to the <CODE>checkEqual(NodeUI)</CODE> method is <CODE>true</CODE>.
    */
   public final boolean equals(Object o)
   {
      if(o == null || !(o instanceof NodeUI) || !(this.getClass().equals(o.getClass()))) 
         return false;
      return checkEqual((NodeUI) o);
   }
   
   /** Compares this <CODE>NodeUI</CODE> with another object. This method
    * calls the implementation of the <CODE>getComparisonString()</CODE> method
    * on both this <CODE>NodeUI</CODE> object and the <CODE>NodeUI</CODE> object
    * supplied. The <CODE>String</CODE> class' <CODE>compareTo(Object)</CODE> is
    * used to perform the comparison. The method is called on the <CODE>String</CODE>
    * returned by this object and the <CODE>String</CODE> returned by the other object
    * is supplied as the parameter.
    * @param o another object to compare with this object.
    * @throws ClassCastException if the supplied object is not a <CODE>NodeUI</CODE> object.
    * @return an <CODE>int</CODE> value that follows the rules of the
    * <CODE>compareTo(Object)</CODE> method in the <CODE>String</CODE> class.
    */
   public final int compareTo(Object o) throws ClassCastException
   {
      NodeUI other = (NodeUI) o;
      return getComparisonString().compareTo(other.getComparisonString());
   }
   
   /**
    * An abstract method to be implemented to return whether another
    * <CODE>NodeUI</CODE> object is equal to this instance. If the the
    * <CODE>NodeUI</CODE> is of a different implementation class, the method
    * should return <CODE>false</CODE>.
    * @param other a <CODE>NodeUI</CODE> to compare with this <CODE>NodeUI</CODE>.
    * @return <CODE>true</CODE> iff the supplied <CODE>NodeUI</CODE> is equal to this one.
    */
   protected abstract boolean checkEqual(NodeUI other);
   
   /** This is an abstract method that should be implemented to return a
    * <CODE>String</CODE> that can be used by the <CODE>compareTo(Object)</CODE>
    * method in order to compare two <CODE>NodeUI</CODE> objects.
    *
    * Two <CODE>NodeUI</CODE> objects should return equal
    * <CODE>String</CODE> objects iff the two <CODE>NodeUI</CODE> objects are equal.
    * @return a <CODE>String</CODE> that follows the above rules.
    */
   protected abstract String getComparisonString();
   
   /** Returns an <CODE>int</CODE> hash code for this object.
    * Two <CODE>NodeUI</CODE> objects will return equal hash codes if the two objects
    * are equal.
    * @return an <CODE>int</CODE> hash code.
    */
   public abstract int hashCode();
}