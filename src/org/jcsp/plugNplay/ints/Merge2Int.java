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
 * <I>Merges</I> two strictly increasing <TT>int</TT> input streams into one
 * strictly increasing output stream.
 * <H2>Process Diagram</H2>
 * <!-- INCORRECT DIAGRAM: <p><IMG SRC="doc-files/MergeInt1.gif"></p> -->
 * <PRE>
 *    in0  ___________
 *   -->--|           | out
 *    in1 | Merge2Int |-->--
 *   -->--|___________|
 * </PRE>
 * <H2>Description</H2>
 * <TT>Merge2Int</TT> is a process whose output stream is the <I>ordered merging</I>
 * of the integers on its input streams.  It assumes that each input stream is
 * <I>strictly increasing</I> (i.e. with no repeats) sequence of integers.
 * It generates a <I>strictly increasing</I> output stream containing all -- and only
 * -- the numbers from its input streams (eliminating any duplicates).
 * <H2>Channel Protocols</H2>
 * <TABLE BORDER="2">
 *   <TR>
 *     <TH COLSPAN="3">Input Channels</TH>
 *   </TR>
 *   <TR>
 *     <TH>in0, in1</TH>
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
 *     <TD>int</TD>
 *     <TD>
 *       All channels in this package carry integers.
 *     </TD>
 *   </TR>
 * </TABLE>
 * <H2>Example</H2>
 * The following example shows how to use <TT>Merge2Int</TT> in a small program.
 * The program also uses some of the other <TT>plugNplay</TT> processes. The
 * program prints, in ascending order (up to Integer.MAX_VALUE), all integers
 * whose prime factors consist only of 2, 3 and 5.  Curious readers may like
 * to reason why the <I>infinitely buffered</I> channels are needed.
 *
 * <PRE>
 * import org.jcsp.lang.*;
 * import org.jcsp.util.ints.*;
 * import org.jcsp.plugNplay.ints.*;
 * 
 * public class Merge2IntExample {
 * 
 *   public static void main (String[] argv) {
 * 
 *     final One2OneChannelInt[] a = Channel.one2oneIntArray (4);
 *     final One2OneChannelInt[] b = Channel.one2oneIntArray (3, new InfiniteBufferInt ());
 *     final One2OneChannelInt c = Channel.one2oneInt ();
 *     final One2OneChannelInt d = Channel.one2oneInt ();
 *     final One2OneChannelInt e = Channel.one2oneInt ();
 * 
 *     new Parallel (
 *       new CSProcess[] {
 *         new MultInt (2, a[0].in (), b[0].out ()),
 *         new MultInt (3, a[1].in (), b[1].out ()),
 *         new MultInt (5, a[2].in (), b[2].out ()),
 *         new Merge2Int (b[0].in (), b[1].in (), c.out ()),
 *         new Merge2Int (c.in (), b[2].in (), d.out ()),
 *         new PrefixInt (1, d.in (), e.out ()),
 *         new DeltaInt (e.in (), Channel.getOutputArray (a)),
 *         new PrinterInt (a[3].in (), "--> ", "\n")
 *       }
 *     ).run ();
 * 
 *   }
 * 
 * }
 * </PRE>
 *
 * @see org.jcsp.plugNplay.ints.MergeInt
 *
 * @author P.H. Welch
 */

public final class Merge2Int implements CSProcess
{
   /** The first input Channel */
   private final ChannelInputInt in0;
   
   /** The second input Channel */
   private final ChannelInputInt in1;
   
   /** The output Channel */
   private final ChannelOutputInt out;
   
   /**
    * Construct a new <TT>Merge2Int</TT> process with the input channels
    * <TT>in0</TT> and <TT>in1</TT> and the output channel <TT>out</TT>.
    * The ordering of the input channels makes no difference
    * to the behaviour of this process.
    *
    * @param in0 an input channel
    * @param in1 an input channel
    * @param out the output channel
    */
   public Merge2Int(ChannelInputInt in0, ChannelInputInt in1, ChannelOutputInt out)
   {
      this.in0 = in0;
      this.in1 = in1;
      this.out = out;
   }
   
   /**
    * The main body of this process.
    */
   public void run()
   {
      int x0, x1;
      x0 = in0.read();
      x1 = in1.read();
      while (true)
      {
         if (x0 < x1)
         {
            out.write(x0);
            x0 = in0.read();
         }
         else if (x0 > x1)
         {
            out.write(x1);
            x1 = in1.read();
         }
         else
         {
            out.write(x0);
            x0 = in0.read();
            x1 = in1.read();
         }
      }
   }
}
