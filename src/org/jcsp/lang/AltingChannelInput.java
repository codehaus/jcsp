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

package org.jcsp.lang;

/**
 * This extends {@link Guard} and {@link ChannelInput}
 * to enable a process to choose between many object input (and other) events.
 * <p>
 * A <i>reading-end</i>, obtained from a <i>one-one</i> or <i>any-one</i>
 * channel by invoking its <tt>in()</tt> method, will extend this abstract class.
 * <H2>Description</H2>
 * <TT>AltingChannelInput</TT> extends {@link Guard} and {@link ChannelInput}
 * to enable a process
 * to choose between many object input (and other) events.  The methods inherited from
 * <TT>Guard</TT> are of no concern to users of this package.
 * </P>
 * <H2>Example</H2>
 * <PRE>
 * import org.jcsp.lang.*;
 * <I></I>
 * public class AltingExample implements CSProcess {
 * <I></I>
 *   private final AltingChannelInput in0, in1;
 *   <I></I>
 *   public AltingExample (final AltingChannelInput in0,
 *                         final AltingChannelInput in1) {
 *     this.in0 = in0;
 *     this.in1 = in1;
 *   }
 * <I></I>
 *   public void run () {
 * <I></I>
 *     final Guard[] altChans = {in0, in1};
 *     final Alternative alt = new Alternative (altChans);
 * <I></I>
 *     while (true) {
 *       switch (alt.select ()) {
 *         case 0:
 *           System.out.println ("in0 read " + in0.read ());
 *         break;
 *         case 1:
 *           System.out.println ("in1 read " + in1.read ());
 *         break;
 *       }
 *     }
 * <I></I>
 *   }
 * <I></I>
 * }
 * </PRE>
 *
 * @see org.jcsp.lang.Guard
 * @see org.jcsp.lang.Alternative
 * @author P.D. Austin and P.H. Welch
 */

public abstract class AltingChannelInput<T> extends Guard implements ChannelInput<T>
{
    // nothing alse to add ... except ...

    /**
     * Returns whether there is data pending on this channel.
     * <P>
     * <I>Note: if there is, it won't go away until you read it.  But if there
     * isn't, there may be some by the time you check the result of this method.</I>
     *
     * @return state of the channel.
     */
    public abstract boolean pending();
}
