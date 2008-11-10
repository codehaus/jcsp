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

/**
 * This is a default implementation for <CODE>NodeUI</CODE> that is used if no
 * other implementation is supplied.
 *
 * @author Quickstone Technologies Limited
 */
public class NodeUIImpl extends NodeUI
{
   /** Constructor which is used as standard to create a new <CODE>NodeUIImpl</CODE>
    * object.
    */
   protected NodeUIImpl()
   {
      time = System.currentTimeMillis();
      mem = Runtime.getRuntime().freeMemory();
      hashCode = new Object().hashCode();
   }
   
   /**
    * A constructor which takes a <CODE>String</CODE> that should be in the
    * format of that returned by the <CODE>getImplStringForm()</CODE> method.
    *
    * @param s a <CODE>String</CODE> used to construct the <CODE>NodeUIImpl</CODE> object.
    */
   public NodeUIImpl(String s)
   {
      try
      {
         int start = 0;
         int nlPos = s.indexOf('\n');
         if(nlPos < 0 || nlPos == s.length() - 1)
            throw new IllegalArgumentException("Invalid String");
         time = Long.parseLong(s.substring(0, nlPos));
         start = nlPos + 1;
         nlPos = s.indexOf('\n', start);
         if(nlPos < 0 || nlPos == s.length() - 1)
            throw new IllegalArgumentException("Invalid String");
         mem = Long.parseLong(s.substring(start, nlPos));
         start = nlPos + 1;
         hashCode = Integer.parseInt(s.substring(start, s.length()));
      }
      catch (NumberFormatException e)
      {
         throw new IllegalArgumentException("Invalid String");
      }
   }
   
   /**
    * Returns a <CODE>String</CODE> object that can be used with the constructor
    * that takes a <CODE>String</CODE> parameter.
    *
    * @return a <CODE>String</CODE> that holds this object in "string form".
    */
   public String getImplStringForm()
   {
      return time+"\n"+mem+"\n"+hashCode;
   }
   
   /** Compares another <CODE>NodeUI</CODE> with this <CODE>NodeUIImpl</CODE>.
    *
    * @param other another <CODE>NodeUI</CODE> to compare with this object.
    * @return <CODE>true</CODE> iff the <CODE>NodeUI</CODE> supplied is a <CODE>NodeUIImpl</CODE> object that contains
    * the same internal unique identifier as this object.
    */
   public final boolean checkEqual(NodeUI other)
   {
      if(other == null || !(other instanceof NodeUIImpl)) 
         return false;
      NodeUIImpl aOther = (NodeUIImpl) other;
      return (time == aOther.time) && (mem == aOther.mem) && (hashCode == aOther.hashCode);
   }
   
   /** Returns a <CODE>String</CODE> that can be used by the superclass to compare
    * two <CODE>NodeUI</CODE> objects.
    * @return a <CODE>String</CODE> that follows the rules stated for this method in the
    * <CODE>NodeUI</CODE> class.
    */
   protected String getComparisonString()
   {
      return time + "" + mem + "" + hashCode;
   }
   
   /** Returns a hash code for this object.
    * @return an <CODE>int</CODE> hash code for this object.
    */
   public final int hashCode()
   {
      return hashCode;
   }
   
   private long time;
   private long mem;
   private int hashCode;
}