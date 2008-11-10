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
 * <p>
 * This class is a Factory that construct a unique identify
 * for this Node. There is only ever one Node per JVM.
 * </p>
 * <p>
 * This class can subclassed and the subclass can be set to be
 * used in place of this class. See the documentation for the
 * <CODE>Node</CODE> class.
 * </p>
 *
 * @author Quickstone Technologies Limited
 */
public class UIFactory
{
   /**
    * <p>
    * Returns a Serializable object that is a unique identifier for this
    * Node. The object returned should support the
    * <CODE>equals(Object)</CODE> and <CODE>hashCode()</CODE> methods
    * of Object.
    * </p>
    *
    * @return a Serializable unique identifier for this JVM
    */
   public NodeUI getUIForThisJVM()
   {
      return new NodeUIImpl();
   }
}