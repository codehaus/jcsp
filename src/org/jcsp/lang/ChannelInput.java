    //////////////////////////////////////////////////////////////////////
    //                                                                  //
    //  JCSP ("CSP for Java") Libraries                                 //
    //  Copyright (C) 1996-2006 Peter Welch and Paul Austin.            //
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
    //  Author contact: P.H.Welch@ukc.ac.uk                             //
    //                                                                  //
    //                                                                  //
    //////////////////////////////////////////////////////////////////////

package org.jcsp.lang;

/**
 * This defines the interface for reading from object channels.
 * <H2>Description</H2>
 * <TT>ChannelInput</TT> defines the interface for reading from object channels.
 * The interface contains only one method - <TT>read()</TT>.  This method
 * will block the calling process until an <TT>Object</TT> has been written
 * to the channel by a process at the other end.  If an <TT>Object</TT> has
 * already been written when this method is called, the method will return
 * without blocking.  Either way, the method returns the <TT>Object</TT>
 * sent down the channel.
 * <P>
 * <TT>ChannelInput</TT> variables are used to hold channels
 * that are going to be used only for <I>input</I> by the declaring process.
 * This is a security matter -- by declaring a <TT>ChannelInput</TT>
 * interface, any attempt to <I>output</I> to the channel will generate
 * a compile-time error.  For example, the following code fragment will
 * not compile:
 *
 * <PRE>
 * void doWrite (ChannelInput c, Object o) {
 *   c.write (o);   // illegal
 * }
 * </PRE>
 *
 * When configuring a <TT>CSProcess</TT> with input channels, they should
 * be declared as <TT>ChannelInput</TT> (or, if we wish to be able to make
 * choices between events, as <TT>AltingChannelInput</TT>)
 * variables.  The actual channel passed,
 * of course, may belong to <I>any</I> channel class that implements
 * <TT>ChannelInput</TT> (or <TT>AltingChannelInput</TT>).
 * <P>
 * The <TT>Object</TT> returned can be cast into the actual
 * class the reader process expects.  If the reader can handle more than one
 * class of <TT>Object</TT> (similar to tagged protocols
 * in <I><B>occam</B></I>), checks should be made before casting.
 * <H2>Examples</H2>
 * <H3>Discard data</H3>
 * <PRE>
 * void doRead (ChannelInput c) {
 *   c.read ();                       // clear the channel
 * }
 * </PRE>
 * <H3>Cast data to expected type</H3>
 * <PRE>
 * void doRead (ChannelInput c) {
 *   Boolean b = (Boolean) c.read();  // will cause a ClassCastException
 *                                    // if read does not return a Boolean
 *   ...  etc.
 * }
 * </PRE>
 * <H3>Cast data after checking type</H3>
 * <PRE>
 * void doRead (ChannelInput c) {
 *   Object o = c.read ();
 *   if (o instanceof Boolean) {
 *     System.out.println ("Boolean: " + (Boolean) o);
 *   }
 *   else if (o instanceof Integer) {
 *     System.out.println ("Integer: " + (Integer) o);
 *   }
 *   else {
 *     System.out.println ("Unexpected Class: " + o);
 *   }
 * }
 * </PRE>
 *
 * @see org.jcsp.lang.AltingChannelInput
 * @see org.jcsp.lang.ChannelOutput
 * @author P.D.Austin
 */

public interface ChannelInput
{
    /**
     * Read an Object from the channel.
     *
     * @return the object read from the channel
     */
    public Object read();
}
