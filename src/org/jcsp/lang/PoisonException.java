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
 * This exception is thrown when a process tries to use a channel that
 * has been poisoned.
 *
 * @see org.jcsp.lang.Poisonable
 */

public class PoisonException extends ChannelDataRejectedException {

  private int strength;

  /**
   * JCSP users should not have to construct these.
   *
   * @param strength the strength of this exception
   *   (which will normally be the strength of the poison in the channel).
   */
  protected PoisonException (int strength) {
    //super("PoisonException, strength: " + strength);
    this.strength = strength;
  }

  /**
   * Once this exception has been caught, the catching process should
   * poison all its channels with the strength held by the exception
   * (which will normally be the strength of the poison in the channel
   * that caused the exception to be thrown).
   * See the example handler at the end of
   * <a href="Alternative.html#FairMuxTime">this section</a>
   * of the documentation of {@link Alternative}.
   */
  public int getStrength() {
    return strength;
   }

}
