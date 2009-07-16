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
 * This defines the interface for a symmetric <i>one-to-one</i> Object channel.
 * The <i>symmetry</i> relates to the use of its channel ends as {@link Guard <i>guards</i>}
 * in an {@link Alternative}: both ends may be so used.
 * <P>
 * The only methods provided are to obtain the <i>ends</i> of the channel,
 * through which all reading and writing operations are done.
 * Only an appropriate <i>channel-end</i> should be plugged into a process
 * &ndash; not the <i>whole</i> channel.
 * A process may use its external channels in one direction only
 * &ndash; either for <i>writing</i> or <i>reading</i>.
 * </P>
 * <P>Actual channels conforming to this interface are made using the relevant
 * <tt>static</tt> construction methods from {@link Channel}.
 * Only fully {@link Channel#one2oneSymmetric() <i>synchronising</i>} channels are currently supported.
 * </P>
 * <H2>Description</H2>
 * <TT>One2OneChannelImpl</TT> is an interface for a symmetric <i>one-to-one</i> Object channel.
 * Multiple readers or multiple writers are not allowed.
 * <P>
 * Both the reading and writing processes may {@link Alternative <TT>ALT</TT>} on this channel.
 * </P>
 * <P>
 * The semantics of the channel is that of CSP &ndash; i.e. it is
 * zero-buffered and fully synchronised.  The reading process must wait
 * for a matching writer and vice-versa.
 * <P>
 * </P>
 * These channels may be constructed by the {@link Channel#one2oneSymmetric()}.
 * Channel poisoning and buffering are not currently supported for these channels.
 * </P>
 *
 * @see org.jcsp.lang.Alternative
 * @see org.jcsp.lang.One2OneChannel
 *
 * @author P.H. Welch
 * @author N.C.C. Brown
 */

public interface One2OneChannelSymmetric<T> {

  /**
   * Returns the input channel end.
   */
  public AltingChannelInput<T> in ();

  /**
   * Returns the output channel end.
   */
  public AltingChannelOutput<T> out ();

}
