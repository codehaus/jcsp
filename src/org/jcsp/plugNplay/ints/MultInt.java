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
 * Scales an integer stream.
 *
 * <H2>Process Diagram</H2>
 * <p><IMG SRC="doc-files/MultInt1.gif"></p>
 * <H2>Description</H2>
 * <TT>MultInt</TT> multiplies each integer that flows through it by the multiplier
 * with which it is configured.
 * <H2>Channel Protocols</H2>
 * <TABLE BORDER="2">
 *   <TR>
 *     <TH COLSPAN="3">Input Channels</TH>
 *   </TR>
 *   <TR>
 *     <TH>in</TH>
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
 * <P>
 * <H2>Example</H2>
 * The following example shows how to use the MultInt process in a small program.
 * The program also uses some of the other plugNplay processes. The
 * program generates a sequence of numbers, multiplies them by 42 and prints
 * this on the screen.
 *
 * <PRE>
 * import org.jcsp.lang.*;
 * import org.jcsp.plugNplay.ints.*;
 * 
 * public class MultIntExample {
 * 
 *   public static void main (String[] argv) {
 * 
 *     final One2OneChannelInt a = Channel.one2oneInt ();
 *     final One2OneChannelInt b = Channel.one2oneInt ();
 * 
 *     new Parallel (
 *       new CSProcess[] {
 *         new NumbersInt (a.out ()),
 *         new MultInt (42, a.in (), b.out ()),
 *         new PrinterInt (b.in (), "--> ", "\n")
 *       }
 *     ).run ();
 * 
 *   }
 * 
 * }
 * </PRE>
 *
 * @author P.H. Welch and P.D. Austin
 */

public final class MultInt implements CSProcess
{
   /** The multiplier */
   private final int n;
   
   /** The input Channel */
   private final ChannelInputInt in;
   
   /** The output Channel */
   private final ChannelOutputInt out;
   
   /**
    * Construct a new MultInt process with the input Channel in and the
    * output Channel out.
    *
    * @param n the multiplier
    * @param in the input channel
    * @param out the output channel
    */
   public MultInt(int n, final ChannelInputInt in, final ChannelOutputInt out)
   {
      this.n = n;
      this.in = in;
      this.out = out;
   }
   
   /**
    * The main body of this process.
    */
   public void run()
   {
      while (true)
         out.write(n*(in.read()));
   }
}
