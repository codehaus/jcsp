
  /*************************************************************************
  *                                                                        *
  *  JCSP ("CSP for Java") libraries                                       *
  *  Copyright (C) 1996-2001 Peter Welch and Paul Austin.                  *
  *                                                                        *
  *  This library is free software; you can redistribute it and/or         *
  *  modify it under the terms of the GNU Lesser General Public            *
  *  License as published by the Free Software Foundation; either          *
  *  version 2.1 of the License, or (at your option) any later version.    *
  *                                                                        *
  *  This library is distributed in the hope that it will be useful,       *
  *  but WITHOUT ANY WARRANTY; without even the implied warranty of        *
  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU     *
  *  Lesser General Public License for more details.                       *
  *                                                                        *
  *  You should have received a copy of the GNU Lesser General Public      *
  *  License along with this library; if not, write to the Free Software   *
  *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307,  *
  *  USA.                                                                  *
  *                                                                        *
  *  Author contact: P.H.Welch@kent.ac.uk                                   *
  *                                                                        *
  *************************************************************************/

package org.jcsp.lang;

//{{{  javadoc
/**
 * This extends {@link Guard} and {@link ChannelOutputInt}
 * to enable a process
 * to choose between many integer output (and other) events.
 * <p>
 * A <i>writing-end</i>, obtained only from a {@link One2OneChannelSymmetricInt <i>symmetric</i>}
 * channel by invoking its <tt>out()</tt> method, will implement this interface.
 * <H2>Description</H2>
 * <TT>AltingChannelOutputInt</TT> extends {@link Guard} and {@link ChannelOutputInt}
 * to enable a process
 * to choose between many integer output (and other) events.  The methods inherited from
 * <TT>Guard</TT> are of no concern to users of this package.
 * </P>
 * <H2>Example</H2>
 * <PRE>
 * import org.jcsp.lang.*;
 * 
 * public class AltingOutputIntExample implements CSProcess {
 * 
 *   private final AltingChannelOutputInt out0, out1;
 *   
 *   public AltingOutputIntExample (final AltingChannelOutputInt out0,
 *                                  final AltingChannelOutputInt out1) {
 *     this.out0 = out0;
 *     this.out1 = out1;
 *   }
 * 
 *   public void run () {
 * 
 *     final Guard[] altChans = {out0, out1};
 *     final Alternative alt = new Alternative (altChans);
 * 
 *     while (true) {
 *       switch (alt.select ()) {
 *         case 0:
 *           out0.write (0);
 *           System.out.println ("out0 written");
 *         break;
 *         case 1:
 *           out1.write (1);
 *           System.out.println ("out1 written");
 *         break;
 *       }
 *     }
 * 
 *   }
 * 
 * }
 * </PRE>
 *
 * @see org.jcsp.lang.Guard
 * @see org.jcsp.lang.Alternative
 * @see org.jcsp.lang.One2OneChannelSymmetricInt
 * @see org.jcsp.lang.AltingChannelOutput
 * @author P.H. Welch
 */
//}}}

public abstract class AltingChannelOutputInt extends Guard implements ChannelOutputInt {

  // nothing alse to add

  /**
   * Returns whether the receiver is committed to read from this channel.
   * <P>
   * <I>Note: if this returns true, you must commit to write down this channel.</I>
   *
   * @return state of the channel.
   */
  public abstract boolean pending ();

}
