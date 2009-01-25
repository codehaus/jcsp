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
 * <I>Parallel</I> multiplexes its input integer stream array on to one output stream.
 * <H2>Process Diagram</H2>
 * <p><IMG SRC="doc-files/ParaplexInt1.gif"></p>
 * <H2>Description</H2>
 * <TT>ParaplexInt</TT> is a process to convert multiple streams of
 * <TT>int</TT>s to a single stream.  It assumes data will always be available
 * on all its input streams.  In each cycle, it inputs <I>in parallel</I> one
 * <TT>int</TT> from each of its inputs, packs them into an array and outputs
 * that array as a single communication.
 * <P>
 * The parallel input means that the process will wait until something arrives
 * from every input channel, accepting items in whatever order they turn up.
 * The ordering of the channels in the <TT>in</TT> array, therefore, makes
 * no difference to the functionality of this process.
 * <P>
 * <B>Caution:</B> the process receiving packets from <TT>ParaplexInt</TT>
 * must agree to the following contract:
 * <BLOCKQUOTE>
 * <I>Input of an array
 * packet means that previously input arrays must not be looked at any more (neither
 * by itself nor any other processes to which they may have been passed).</I>
 * </BLOCKQUOTE>
 * Supporting the above, there is one more rule:
 * <BLOCKQUOTE>
 * <I>There must be only one process receiving array packets
 * from <TT>ParaplexInt</TT> (i.e. its output channel must <I>not</I> be connected
 * to a {@link org.jcsp.lang.One2AnyChannelImpl} or {@link org.jcsp.lang.Any2AnyChannelImpl}).</I>
 * </BLOCKQUOTE>
 * The reason for these obligations is to remove the need for <TT>ParaplexInt</TT>
 * to generate a <TT>new</TT> array packet for each <I>paraplexed</I> communication
 * -- an array that will normally be discarded by the receiving process after
 * dealing with its contents.
 * Instead of that, <TT>ParaplexInt</TT> operates a <I>double-buffered</I> protocol,
 * constructing and reusing just two array packets.  It switches between them
 * after every cycle.  In this way, it fills one packet while the process
 * receiving its output consumes the other one.  This is safe so long as that
 * receiving process agrees to the above rules.
 * See the <I>Low Level</I> example in {@link org.jcsp.lang.Parallel} for the details
 * of this implementation.
 * <P>
 * <I>Note:</I> the above two constraints should work naturally with most applications.
 * However, by converting the first rule into a protocol where the receiving process
 * explicitly returns the packet (through an <I>acknowledgment</I> channel)
 * when it has finished using it and before inputting the next one, the second
 * rule could be dropped.  This is trivial to do by piping the output from
 * <TT>ParaplexInt</TT> through a simple cyclic process that inputs a packet,
 * forwards it (down a {@link org.jcsp.lang.One2AnyChannelImpl} or
 * {@link org.jcsp.lang.Any2AnyChannelImpl}) and waits for the acknowledgment
 * (for which only a {@link org.jcsp.lang.One2OneChannelImpl} is needed).
 *
 * <H2>Channel Protocols</H2>
 * <TABLE BORDER="2">
 *   <TR>
 *     <TH COLSPAN="3">Input Channels</TH>
 *   </TR>
 *   <TR>
 *     <TH>in[]</TH>
 *     <TD>int</TD>
 *     <TD>
 *       Most channels in this package carry integers.
 *     </TD>
 *   </TR>
 *   <TR>
 *     <TH COLSPAN="3">Output Channels</TH>
 *   </TR>
 *   <TR>
 *     <TH>out</TH>
 *     <TD>int[]</TD>
 *     <TD>
 *       A packet carrying the <I>paraplexed</I> data.
 *     </TD>
 *   </TR>
 * </TABLE>
 *
 * <H2>Example</H2>
 * <PRE>
 * import org.jcsp.lang.*;
 * import org.jcsp.plugNplay.ints.*;
 * 
 * class ParaplexIntExample {
 * 
 *   public static void main (String[] args) {
 * 
 *     final One2OneChannelInt[] a = Channel.one2oneIntArray (3);
 *     final One2OneChannel b = Channel.one2one ();
 * 
 *     new Parallel (
 *       new CSProcess[] {
 *         new NumbersInt (a[0].out ()),
 *         new SquaresInt (a[1].out ()),
 *         new FibonacciInt (a[2].out ()),
 *         new ParaplexInt (Channel.getInputArray (a), b.out ()),
 *         new CSProcess () {
 *           public void run () {
 *             System.out.println ("\n\t\tNumbers\t\tSquares\t\tFibonacci\n");
 *             while (true) {
 *               int[] data = (int[]) b.in ().read ();
 *               for (int i = 0; i < data.length; i++) {
 *                 System.out.print ("\t\t" + data[i]);
 *               }
 *               System.out.println ();
 *             }
 *           }
 *         }
 *       }
 *     ).run ();
 *   }
 * 
 * }
 * </PRE>
 *
 * @see org.jcsp.plugNplay.ints.DeparaplexInt
 * @see org.jcsp.plugNplay.ints.MultiplexInt
 * @see org.jcsp.plugNplay.ints.DemultiplexInt
 *
 * @author P.H. Welch
 */

public final class ParaplexInt implements CSProcess
{
   /** The input channels */
   private final ChannelInputInt[] in;
   
   /** The output channel */
   private final ChannelOutput out;
   
   /**
    * Construct a new ParaplexInt process from the array of input channels
    * to the output channel.
    * The ordering of the channels in the input array makes
    * no difference to the functionality of this process.
    *
    * @param in the input channels
    * @param out the output channel
    */
   public ParaplexInt(final ChannelInputInt[] in, final ChannelOutput out)
   {
      this.in = in;
      this.out = out;
   }
   
   /**
    * The main body of this process.
    */
   public void run()
   {
      final ProcessReadInt[] inputProcess = new ProcessReadInt[in.length];
      for (int i = 0; i < in.length; i++)
         inputProcess[i] = new ProcessReadInt(in[i]);
      Parallel parInput = new Parallel(inputProcess);
      
      int[][] data = new int[2][in.length];
      int index = 0;
      while (true)
      {
         parInput.run();
         int[] buffer = data[index];
         for (int i = 0; i < in.length; i++)
            buffer[i] = inputProcess[i].value;
         out.write(buffer);
         index = 1 - index;
      }
   }
}
