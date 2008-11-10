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

package org.jcsp.plugNplay.ints;

import org.jcsp.lang.*;

/**
 * <I>Fair</I> multiplexes its input integer stream array into one output stream
 * (carrying source channel and data pairs).
 * <H2>Process Diagram</H2>
 * <p><IMG SRC="doc-files/MultiplexInt1.gif"></p>
 * <H2>Description</H2>
 * <TT>MultiplexInt</TT> is a process to convert multiple streams of
 * <TT>int</TT>s to a single stream in such a way that it can be
 * {@link DemultiplexInt de-multiplexed} later.
 * The <I>protocol</I> on the outgoing multiplexed stream consists of
 * an <TT>int</TT>, that represents the channel identity of the
 * multiplexed data, followed by the multiplexed data.
 * <P>
 * The ordering of the channels in the <TT>in</TT> array makes
 * no difference to the functionality of this process -- the multiplexing
 * services all channels fairly.
 * <H2>Channel Protocols</H2>
 * <TABLE BORDER="2">
 *   <TR>
 *     <TH COLSPAN="3">Input Channels</TH>
 *   </TR>
 *   <TR>
 *     <TH>in[]</TH>
 *     <TD>int</TD>
 *     <TD>
 *       All channels in this package carry integers.
 *     </TD>
 *   </TR>
 *   <TR>
 *     <TH COLSPAN="3">Output Channels</TH>
 *   </TR>
 *   <TR>
 *     <TH>out</TH>
 *     <TD>int, int</TD>
 *     <TD>
 *       An <TT>out</TT> message is an index followed by the multiplexed data.
 *     </TD>
 *   </TR>
 * </TABLE>
 * <H2>Example</H2>
 * The following example shows how to use <TT>MultiplexInt</TT> in a small program.
 * <PRE>
 * import org.jcsp.lang.*;
 * import org.jcsp.plugNplay.ints.*;
 * 
 * public class MultiplexIntExample {
 * 
 *   public static void main (String[] argv) {
 * 
 *     final One2OneChannelInt[] a = Channel.one2oneIntArray (3);
 *     final One2OneChannelInt b = Channel.one2oneInt ();
 * 
 *     new Parallel (
 *       new CSProcess[] {
 *         new NumbersInt (a[0].out ()),
 *         new FibonacciInt (a[1].out ()),
 *         new SquaresInt (a[2].out ()),
 *         new MultiplexInt (Channel.getInputArray (a), b.out ()),
 *         new CSProcess () {
 *           public void run () {
 *             String[] key = {"Numbers ",
 *                             "            Fibonacci ",
 *                             "                          Squares "};
 *             while (true) {
 *               System.out.print (key[b.in ().read ()]);   // print channel source
 *               System.out.println (b.in ().read ());      // print multiplexed data
 *             }
 *           }
 *         }
 *       }
 *     ).run ();
 * 
 *   }
 * 
 * }
 * </PRE>
 *
 * @see org.jcsp.plugNplay.ints.DemultiplexInt
 * @see org.jcsp.plugNplay.ints.ParaplexInt
 * @see org.jcsp.plugNplay.ints.DeparaplexInt
 * @see org.jcsp.plugNplay.ints.PlexInt
 *
 * @author P.H. Welch and P.D. Austin
 */

public final class MultiplexInt implements CSProcess
{
   /** The input channels */
   private final AltingChannelInputInt[] in;
   
   /** The output channel */
   private final ChannelOutputInt out;
   
   /**
    * Construct a new MultiplexInt process with the input Channel in and the output
    * Channels out.  The ordering of the Channels in the in array make
    * no difference to the functionality of this process -- the multiplexing
    * services all channels fairly.
    *
    * @param in the input channels
    * @param out the output channel
    */
   public MultiplexInt(final AltingChannelInputInt[] in, final ChannelOutputInt out)
   {
      this.in = in;
      this.out = out;
   }
   
   /**
    * The main body of this process.
    */
   public void run()
   {
      final Alternative alt = new Alternative(in);
      while (true)
      {
         int index = alt.fairSelect();
         out.write(index);
         out.write(in[index].read());
      }
   }
}
