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

package org.jcsp.plugNplay;

import org.jcsp.lang.*;

/**
 * <I>Merges</I> two strictly increasing <TT>Integer</TT> input streams into one
 * strictly increasing output stream.
 * <H2>Process Diagram</H2>
 * <!-- INCORRECT DIAGRAM: <p><IMG SRC="doc-files/Merge21.gif"></p> -->
 * <PRE>
 *    in0  ________
 *   -->--|        | out
 *    in1 | Merge2 |-->--
 *   -->--|________|
 * </PRE>
 * <H2>Description</H2>
 * <TT>Merge2</TT> is a process whose output stream is the <I>ordered merging</I>
 * of the Integers on its input streams.  It assumes that each input stream is
 * <I>strictly increasing</I> (i.e. with no repeats) sequence of Integers.
 * It generates a <I>strictly increasing</I> output stream containing all
 * -- and only -- the Integers from its input streams (eliminating any duplicates).
 * <H2>Channel Protocols</H2>
 * <TABLE BORDER="2">
 *   <TR>
 *     <TH COLSPAN="3">Input Channels</TH>
 *   </TR>
 *   <TR>
 *     <TH>in0, in1</TH>
 *     <TD>java.lang.Number</TD>
 *     <TD>
 *       Both channels can accept data from any subclass of Number. It is
 *       possible to send Floats down one channel and Integers down the
 *       other. However all values will be converted to ints.
 *     </TD>
 *   </TR>
 *   <TR>
 *     <TH COLSPAN="3">Output Channels</TH>
 *   </TR>
 *   <TR>
 *     <TH>out</TH>
 *     <TD>java.lang.Integer</TD>
 *     <TD>
 *       The output will always be of type Integer.
 *     </TD>
 *   </TR>
 * </TABLE>
 * <H2>Example</H2>
 * The following example shows how to use <TT>Merge2</TT> in a small program.
 * The program also uses some of the other <TT>plugNplay</TT> processes. The
 * program prints, in ascending order (up to Integer.MAX_VALUE), all integers
 * whose prime factors consist only of 2, 3 and 5.  Curious readers may like
 * to reason why the <I>infinitely buffered</I> channels are needed.
 *
 * <PRE>
 * import org.jcsp.lang.*;
 * import org.jcsp.util.*;
 * import org.jcsp.plugNplay.*;
 * 
 * public class Merge2Example {
 * 
 *   public static void main (String[] argv) {
 * 
 *     final One2OneChannel[] a = Channel.one2oneArray (4);
 *     final One2OneChannel[] b = Channel.one2oneArray (3, new InfiniteBuffer ());
 *     final One2OneChannel c = Channel.one2one ();
 *     final One2OneChannel d = Channel.one2one ();
 *     final One2OneChannel e = Channel.one2one ();
 * 
 *     new Parallel (
 *       new CSProcess[] {
 *         new Mult (2, a[0].in (), b[0].out ()),
 *         new Mult (3, a[1].in (), b[1].out ()),
 *         new Mult (5, a[2].in (), b[2].out ()),
 *         new Merge2 (b[0].in (), b[1].in (), c.out ()),
 *         new Merge2 (c.in (), b[2].in (), d.out ()),
 *         new Prefix (1, d.in (), e.out ()),
 *         new Delta (e.in (), Channel.getOutputArray (a)),
 *         new Printer (a[3].in (), "--> ", "\n")
 *       }
 *     ).run ();
 * 
 *   }
 * 
 * }
 * </PRE>
 *
 * @see org.jcsp.plugNplay.Merge
 *
 * @author P.H. Welch
 */

public final class Merge2 implements CSProcess
{
   /** The first input Channel */
   private final ChannelInput in0;
   
   /** The second input Channel */
   private final ChannelInput in1;
   
   /** The output Channel */
   private final ChannelOutput out;
   
   /**
    * Construct a new <TT>Merge2</TT> process with the input channels
    * <TT>in0</TT> and <TT>in1</TT> and the output channel <TT>out</TT>.
    * The ordering of the input channels makes no difference
    * to the behaviour of this process.
    *
    * @param in0 an input channel
    * @param in1 an input channel
    * @param out the output channel
    */
   public Merge2(ChannelInput in0, ChannelInput in1, ChannelOutput out)
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
      Number X0, X1;
      X0 = (Number) in0.read();
      X1 = (Number) in1.read();
      while (true)
      {
         if (X0.intValue() < X1.intValue())
         {
            out.write(X0);
            X0 = (Number) in0.read();
         }
         else if (X0.intValue() > X1.intValue())
         {
            out.write(X1);
            X1 = (Number) in1.read();
         }
         else
         {
            out.write(X0);
            X0 = (Number) in0.read();
            X1 = (Number) in1.read();
         }
      }
   }
}
