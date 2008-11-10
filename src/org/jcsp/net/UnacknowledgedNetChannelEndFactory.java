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
 * Extends the standard network channel factory to give unacknowledged channel output ends. Use these with caution
 * as the lack of synchronization between sender and receiver can lead to potential problems.
 *
 * @author Quickstone Technologies Limited
 */
public class UnacknowledgedNetChannelEndFactory extends StandardNetChannelEndFactory
{
   /**
    * Creates a new factory object.
    */
   public UnacknowledgedNetChannelEndFactory()
   {
      super();
   }
   
   /**
    * Creates an unacknowledged output channel end suitable for use by a single writer.
    *
    * @param loc address of the input channel end to connect to.
    */
   public NetChannelOutput createOne2Net(NetChannelLocation loc)
   {
      return new One2NetChannel(loc, false);
   }
   
   /**
    * Creates an unacknowledged output channel end suitable for use by multiple writers.
    *
    * @param loc address of the input channel end to connect to.
    */
   public NetSharedChannelOutput createAny2Net(NetChannelLocation loc)
   {
      return new Any2NetChannel(loc, false);
   }
}