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
 * This defines the interface for writing to object channels.
 * <p>
 * A <i>writing-end</i>, conforming to this interface,
 * is obtained from a channel by invoking its <tt>out()</tt> method.
 * <H2>Description</H2>
 * <TT>ChannelOutput</TT> defines the interface for writing to object channels.
 * The interface contains only one method - <TT>write(Object o)</TT>.
 * This method will block the calling process until the <TT>Object</TT> has
 * been accepted by the channel.  In the (default) case of a zero-buffered
 * synchronising CSP channel, this happens only when a process at the other
 * end of the channel invokes (or has already invoked) a <TT>read()</TT>.
 * <P>
 * <TT>ChannelOutput</TT> variables are used to hold channels
 * that are going to be used only for <I>output</I> by the declaring process.
 * This is a security matter -- by declaring a <TT>ChannelOutput</TT>
 * interface, any attempt to <I>input</I> from the channel will generate
 * a compile-time error.  For example, the following code fragment will
 * not compile:
 *
 * <PRE>
 * Object doRead (ChannelOutput c) {
 *   return c.read ();   // illegal
 * }
 * </PRE>
 *
 * When configuring a <TT>CSProcess</TT> with output channels, they should
 * be declared as <TT>ChannelOutput</TT> variables.  The actual channel passed,
 * of course, may belong to <I>any</I> channel class that implements
 * <TT>ChannelOutput</TT>.
 * <P>
 * Instances of any class may be written to a channel.
 *
 * <H2>Example</H2>
 * <PRE>
 * void doWrite (ChannelOutput c, Object o) {
 *   c.write (o);
 * }
 * </PRE>
 *
 * @see org.jcsp.lang.SharedChannelOutput
 * @see org.jcsp.lang.ChannelInput
 * @author P.D. Austin
 */

public interface ChannelOutput<T> extends Poisonable
{
    /**
     * Write an Object to the channel.
     *
     * @param object the object to write to the channel
     */
    public void write(T object);
}
