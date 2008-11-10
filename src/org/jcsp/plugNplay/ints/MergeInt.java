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
 * <I>Merges</I> an array of strictly increasing <TT>int</TT> input streams into one
 * strictly increasing output stream.
 * <H2>Process Diagram</H2>
 * <p><IMG SRC="doc-files/MergeInt1.gif"></p>
 * <H2>Description</H2>
 * <TT>MergeInt</TT> is a process whose output stream is the <I>ordered merging</I>
 * of the integers on its input streams.  It assumes that each input stream is
 * <I>strictly increasing</I> (i.e. with no repeats) sequence of integers.
 * It generates a <I>strictly increasing</I> output stream containing all --
 * and only -- the numbers from its input streams (eliminating any duplicates).
 * <P>
 * <I>Warning: this process assumes that its input channel array has at least
 * two elements.</I>
 * <H2>Channel Protocols</H2>
 * <TABLE BORDER="2">
 *   <TR>
 *     <TH COLSPAN="3">Input Channels</TH>
 *   </TR>
 *   <TR>
 *     <TH>in[]</TH>
 *     <TD>int</TD>
 *     <TD>
 *       <I>Assume</I>: <TT>in.length >= 2</TT>.
 *     </TD>
 *   </TR>
 *   <TR>
 *     <TH COLSPAN="3">Output Channels</TH>
 *   </TR>
 *   <TR>
 *     <TH>out</TH>
 *     <TD>int</TD>
 *     <TD>
 *       All channels in this package carry integers.
 *     </TD>
 *   </TR>
 * </TABLE>
 * <P>
 * <H2>Example</H2>
 * The following example shows how to use <TT>MergeInt</TT> in a small program.
 * The program also uses some of the other <TT>plugNplay</TT> processes. The
 * program prints, in ascending order (up to Integer.MAX_VALUE), all integers
 * whose prime factors consist only of 2, 3, 5 and 7.  Curious readers may like
 * to reason why the <I>infinitely buffered</I> channels are needed.
 *
 * <PRE>
 * import org.jcsp.lang.*;
 * import org.jcsp.util.ints.*;
 * import org.jcsp.plugNplay.ints.*;
 * 
 * public class MergeIntExample {
 * 
 *   public static void main (String[] argv) {
 * 
 *     final One2OneChannelInt[] a = Channel.one2oneIntArray (5);
 *     final One2OneChannelInt[] b = Channel.one2oneIntArray (4, new InfiniteBufferInt ());
 *     final One2OneChannelInt c = Channel.one2oneInt ();
 *     final One2OneChannelInt d = Channel.one2oneInt ();
 * 
 *     new Parallel (
 *       new CSProcess[] {
 *         new MultInt (2, a[0].in (), b[0].out ()),
 *         new MultInt (3, a[1].in (), b[1].out ()),
 *         new MultInt (5, a[2].in (), b[2].out ()),
 *         new MultInt (7, a[3].in (), b[3].out ()),
 *         new MergeInt (Channel.getInputArray (b), c.out ()),
 *         new PrefixInt (1, c.in (), d.out ()),
 *         new DeltaInt (d.in (), Channel.getOutputArray (a)),
 *         new PrinterInt (a[4].in (), "--> ", "\n")
 *       }
 *     ).run ();
 * 
 *   }
 * 
 * }
 * </PRE>
 * <P>
 * <H2>Implementation Note</H2>
 * The implementation sets up a balanced binary tree of parallel
 * <TT>Merge2Int</TT> processes to <I>fan-in</I> the merge from
 * its external input channels to its external output.  It's a nice
 * example of recursion and parallelism -- here's the <TT>run</TT>
 * method:
 * <PRE>
 *   public void run () {
 *     final int n = in.length;  // deduce: n >= 2
 *     switch (n) {
 *       case 2:
 *         new Merge2Int (in[0], in[1], out).run ();
 *       break;
 *       case 3:
 *         final One2OneChannelInt c = Channel.one2oneInt ();
 *         new Parallel (
 *           new CSProcess[] {
 *             new Merge2Int (in[0], in[1], c.out ()),
 *             new Merge2Int (c.in (), in[2], out)
 *           }
 *         ).run ();
 *       break;
 *       default:  // deduce: n >= 4
 *         final int n2 = n/2;
 *         ChannelInputInt[] bottom = new ChannelInputInt[n2];
 *         ChannelInputInt[] top = new ChannelInputInt[n - n2];
 *         for (int i = 0; i < n2; i++) {
 *           bottom[i] = in[i];
 *         }
 *         for (int i = n2; i < n; i++) {
 *           top[i - n2] = in[i];
 *         }
 *         final One2OneChannelInt[] d = Channel.one2oneIntArray (2);
 *         new Parallel (
 *           new CSProcess[] {
 *             new MergeInt (bottom, d[0].out ()),
 *             new MergeInt (top, d[1].out ()),
 *             new Merge2Int (d[0].in (), d[1].in (), out)
 *           }
 *         ).run ();
 *       break;
 *     }
 *   }
 * </PRE>
 *
 * @see org.jcsp.plugNplay.ints.Merge2Int
 *
 * @author P.H. Welch
 */

public final class MergeInt implements CSProcess
{
   /** The input channels */
   final private ChannelInputInt[] in;  // assume: in.length >= 2
   
   /** The output channel */
   final private ChannelOutputInt out;
   
   /**
    * Construct a new <TT>Merge2Int</TT> process with the input channels
    * <TT>in</TT>and the output channel <TT>out</TT>.
    * The ordering of the input channels makes no difference
    * to the behaviour of this process.
    *
    * @param in the input channels (there must be at least 2)
    * @param out the output channel
    */
   public MergeInt(ChannelInputInt[] in, ChannelOutputInt out)
   {
      if (in.length < 2) {
        throw new IllegalArgumentException ("Merge must have at least 2 input channels");
      }
      this.in = in;
      this.out = out;
   }
   
   /**
    * The main body of this process.
    */
   public void run()
   {
      final int n = in.length;  // deduce: n >= 2
      switch (n)
      {
         case 2:
            new Merge2Int(in[0], in[1], out).run();
            break;
         case 3:
            final One2OneChannelInt c = Channel.one2oneInt();
            new Parallel(new CSProcess[]
                        {
                           new Merge2Int(in[0], in[1], c.out()),
                           new Merge2Int(c.in(), in[2], out)
                        }).run();
            break;
         default:  // deduce: n >= 4
            final int n2 = n/2;
            ChannelInputInt[] bottom = new ChannelInputInt[n2];
            ChannelInputInt[] top = new ChannelInputInt[n - n2];
            for (int i = 0; i < n2; i++)
               bottom[i] = in[i];
            for (int i = n2; i < n; i++)
               top[i - n2] = in[i];
            final One2OneChannelInt[] d = Channel.one2oneIntArray(2);
            new Parallel(new CSProcess[] 
                        {
                           new MergeInt(bottom, d[0].out()),
                           new MergeInt(top, d[1].out()),
                           new Merge2Int(d[0].in(), d[1].in(), out)
                        }).run();
            break;
      }
   }
}
