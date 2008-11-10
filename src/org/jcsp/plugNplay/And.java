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
 * Bitwise <I>ands</I> two <TT>Integer</TT> streams to one stream.
 *
 * <H2>Process Diagram</H2>
 * <p><img src="doc-files/And1.gif"></p>
 * <H2>Description</H2>
 * This is a process with an infinite loop that waits for a <tt>Object</tt> of
 * type <tt>Number</tt> to be sent down each of its input channels.
 * The loop body then calculates the bitwise AND on the values of the two
 * <tt>Number</tt>s and writes the result as a new <tt>Integer</tt> to its
 * output channel.
 * <P>
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
 * <P>
 * <H2>Example</H2>
 * The following example shows how to use the And process in a small program.
 * The program also uses some of the other building block processes. The
 * program generates a sequence of numbers and rounds each odd number down to
 * the nearest even number and prints this on the screen.
 *
 * <PRE>
 * import org.jcsp.lang.*;
 * import org.jcsp.plugNplay.*;
 * 
 * public class AndExample {
 * 
 *   public static void main (String[] argv) {
 * 
 *     final One2OneChannel a = Channel.one2one ();
 *     final One2OneChannel b = Channel.one2one ();
 *     final One2OneChannel c = Channel.one2one ();
 * 
 *     new Parallel (
 *       new CSProcess[] {
 *         new Numbers (a.out ()),
 *         new Generate (b.out (), Integer.MAX_VALUE - 1),
 *         new And (a.in (), b.in (), c.out ()),
 *         new Printer (c.in (), "--> ", "\n")
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
public final class And implements CSProcess
{
   /** The first input Channel */
   private ChannelInput in0;
   
   /** The second input Channel */
   private ChannelInput in1;
   
   /** The output Channel */
   private ChannelOutput out;
   
   /**
    * Construct a new And process with the input Channels in0 and in1 and the
    * output Channel out. The ordering of the Channels in0 and in1 make
    * no difference to the functionality of this process.
    *
    * @param in0 the first input Channel
    * @param in1 the second input Channel
    * @param out the output Channel
    */
   public And(ChannelInput in0, ChannelInput in1, ChannelOutput out)
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
      ProcessRead[] procs = {new ProcessRead(in0), new ProcessRead(in1)};
      Parallel par = new Parallel(procs);
      
      while (true)
      {
         par.run();
         int i0 = ((Number)procs[0].value).intValue();
         int i1 = ((Number)procs[1].value).intValue();
         out.write(new Integer(i0 & i1));
      }
   }
}
