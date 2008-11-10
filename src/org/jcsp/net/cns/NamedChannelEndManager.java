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

package org.jcsp.net.cns;

import org.jcsp.net.*;

/**
 * <p>
 * Classes implementing this interface act as factories for constructing
 * <code>NetChannelInput</code> and <code>NetChannelOutput</code> objects
 * (see {@link NamedChannelEndFactory}).
 * </p>
 * <p>
 * They also supply methods for destroying the channel ends created, either
 * an individual end or all constructed so far. The later provides a convenient
 * way for a releasing all org.jcsp.net resources used by a process network. An
 * instance of an implementing class could be passed as parameter around a
 * process network and used to construct all channels. Once the network has
 * terminated the rousources can be released by calling
 * <code>{@link #destroyAllChannelEnds()}</code>.
 * </p>
 * <p>
 * If an attempt is made to destroy a channel end that was not constructed
 * by the instance of this class that was invoked, then a
 * <code>WrongFactoryException</code> should be thrown.
 * </p>
 * @author Quickstone Technologies Limited
 */
public interface NamedChannelEndManager extends NamedChannelEndFactory
{
   /**
    * Destroys an individual <code>NetChannelInput</code> object
    * that was constructed with this instance. This will deregister
    * the channel name and destroy the channel end.
    *
    * @param chanInEnd  the channel end to destroy.
    */
   public void destroyChannelEnd(NetChannelInput chanInEnd);
   
   /**
    * Destroys an individual <code>NetChannelOutput</code> object
    * that was constructed with this instance. This will simply
    * destroy the channel end.
    *
    * @param chanOutEnd the channel end to destroy.
    */
   public void destroyChannelEnd(NetChannelOutput chanOutEnd);
   
   /**
    * Destroys all channel ends constructed with this instance
    * of the factory.
    */
   public void destroyAllChannelEnds();
}
