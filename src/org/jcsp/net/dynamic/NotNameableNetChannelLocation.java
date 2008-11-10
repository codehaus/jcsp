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

package org.jcsp.net.dynamic;

import org.jcsp.net.NetChannelLocation;
import org.jcsp.net.cns.LocationNotCNSRegisterable;

/**
 * Instances of this class take another <code>NetChannelLocation</code>
 * object and effectivly "clone" its attributes. The CNS will not
 * register channels at these locations due to this class implementing
 * the <code>LocationNotCNSRegisterable</code> interface.
 *
 * @author Quickstone Technologies Limited
 */
class NotNameableNetChannelLocation extends NetChannelLocation implements LocationNotCNSRegisterable
{
   /**
    * Constructor which takes another <code>NetChannelLocation</code>
    * object to "copy".
    *
    * @param other
    * @throws IllegalArgumentException	if super class constructor throws it.
    */
   public NotNameableNetChannelLocation(NetChannelLocation other) throws IllegalArgumentException
   {
      super(other);
   }
}