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
    //////////////////////////////////////////////////////////////////////

package org.jcsp.lang;

/**
 * All <i>channel-ends</i> implement this inteface.
 * <p>
 * Channels are immune to poisoning unless explicitly constructed to be <i>poisonable</i>
 * (by those <tt>static</tt> methods in {@link Channel} that prescribe a level
 * of <i>immunity</i> &ndash; e.g. {@link Channel#one2one(int) <i>this one</i>}).
 * </p>
 * <p>
 * <i>Poisonable</i> channels are immune to poisons with strength below the level
 * of <i>immunity</i> given when they were constructed.
 * Above this level, the poison will take effect.
 * </p>
 * <p>
 * Any attempt to use a poisoned channel will cause a {@link PoisonException}
 * to be thrown.
 * Processes blocked on a channel that gets poisoned will be awoken with
 * a thrown {@link PoisonException}.
 * </p>
 * <p>
 * Poisoning is an effective technique for the safe interruption of processes and
 * termination of process networks or sub-networks (of any connected topology).
 * Note that poisoning a channel is non-blocking.
 * Also, poisoning an already poisoned channel does no (extra) harm.
 * </p>
 * <p>
 * The algorithm is simple.
 * One or more processes may independently decide to poison their environment
 * and terminate.
 * To do this, they poison their external channels, tidy up and die.
 * Any process accessing or waiting on a poisoned channel gets a {@link PoisonException}
 * thrown: this should be caught, local affairs settled and external channels
 * posioned before the process terminates.
 * That's all.
 * </p>
 * <p>
 * An example is shown in {@link org.jcsp.plugNplay.Generate} and
 * <a href="Alternative.html#FairMuxTime">here</a>,
 * where the channels in the example network are constructed to be poisonable
 * (with an immunity of zero) and <tt>FairMuxTime</tt> initiates the shut-down
 * (by injecting poison of strength 42).
 * </p>
 * <p>
 * To bring down only a <i>sub-network</i>, the internal channels of the subnet
 * should have an immunity lower than its external channels.
 * One of the subnet processes must choose (or be told) to release poison with
 * strength one greater than the subnet channel immunity.
 * That will terminate just the subnet, leaving the surrounding processes unaffected
 * &ndash; until they next try to communicate with the shut-down subnet.
 * This is no problem so long as the subnet is being replaced, which is
 * the usual reason for closing the previous one.
 * </p>
 *
 * @see org.jcsp.lang.PoisonException
 */

public interface Poisonable {

  /**
   * This injects <i>poison</i> into the channel.
   * If the channel was not explicitly constructed to be poisonable or
   * if the strength of poison is not greater than the channel immunity level,
   * the poison will have no effect.
   *
   * @param strength the strength of the poison (must be &gt;= 0).
   *
   */
  public void poison(int strength);

}
