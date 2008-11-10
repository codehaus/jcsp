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
import org.jcsp.util.filter.*;

// Not sure if this class should be public

/**
 * This class is used in the dynamic class loading mechanism.
 * Instances of the class extract <CODE>Message</CODE> objects from <CODE>SerializedMessage</CODE> objects.
 * Dynamic class loading can be enabled by using the constructor which takes a <CODE>ClassManager</CODE>.
 * If dynamic class loading is enabled and a <CODE>DynamicClassLoaderMessage</CODE> is filtered, 
 * the <CODE>ClassManager</CODE> object is supplied to the Message object and the underlying message extracted.
 * Any classes that need loading from the remote Node are requested and loaded as necessary.
 *
 * @author Quickstone Technologies Limited
 */
class DeserializeChannelFilter implements Filter
{
    /* 5 Jun 2002
     *
     * This class was originally used by the dynamic class loading
     * mechanism as well as the LoopBackLink
     *
     * Have now moved dynamic class loading mechanism into separate
     * package and so have modified this class just to be used
     * by LoopbackLink.
     *
        DeserializeChannelFilter(ClassManager cm) {
                this.cm = cm;
                this.dynamic = true;
        }
     */
   
   DeserializeChannelFilter()
   {
   }
   
   /** This method takes an object and substitutes it for another object based upon the following rules:
    *
    * If the object is an instance of the <CODE>DynamicClassLoaderMessage</CODE> class and dynamic class loading is enabled,
    * then the filter will return the object returned by the supplied object's get method. A <CODE>ClassManager</CODE> will be
    * supplied to the method.
    *
    * If the object is an instance of the <CODE>SerializedMessage</CODE> class then the object's get
    * method will be called. The message being held by the <CODE>SerializedMessage</CODE> will be deserialized
    * but classes will not be dynamically loaded.
    *
    * If the object is not a <CODE>SerializedMessage</CODE> object, then the object itself will be returned without
    * modification.
    * @param object The object to filter.
    * @return the substituted object.
    */
   public Object filter(Object object)
   {
      
      try
      {
         if (object instanceof SerializedMessage)
            return ((SerializedMessage)object).get();
         else
            return object;
      }
      catch (ClassNotFoundException e)
      {
         Node.err.log(this, e);
      }
      catch (IOException e)
      {
         Node.err.log(this, e);
      }
      //return object at the moment decide later - this is only prototype version
      return object;
   }
}