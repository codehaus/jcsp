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

import org.jcsp.net.settings.*;

/**
 *
 * @author Quickstone Technologies Limited.
 */
class PingRequirement extends Requirement
{
   PingRequirement(int intValue, int acceptableDifference)
   {
      super(XMLConfigConstants.REQ_NAME_MAXPING,
            XMLConfigConstants.SPEC_NAME_PING,
            XMLConfigConstants.REQ_COMPARATOR_LESS,
            intValue);
      if(intValue < 0 || acceptableDifference < 0)
         throw new IllegalArgumentException("Illegal arguments - a value is less than zero.");
      this.acceptableDifference = acceptableDifference;
   }
   
   public boolean matches(Specification spec)
   {
      if(this.specName.equals(spec.name) && this.type.equals(spec.type))
      {
         if(intValue > spec.intValue)
            //This object is greater than the other object - i.e. the other
            //object is less than this object
            return true;
         else if(spec.intValue - intValue <= acceptableDifference)
            //This object is less than the other object - i.e. the other
            //object is greater than this object
            return true;
      }
      return false;
   }
   
   final int acceptableDifference;
}