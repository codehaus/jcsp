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
 * Scales an <TT>Integer</TT> stream.
 *
 * <H2>Process Diagram</H2>
 * <p><img src="doc-files/Mult1.gif"></p>
 * <H2>Description</H2>
 * <TT>Mult</TT> multiplies each Integer that flows through it by the multiplier
 * with which it is configured.
 * <H2>Channel Protocols</H2>
 * <TABLE BORDER="2">
 *   <TR>
 *     <TH COLSPAN="3">Input Channels</TH>
 *   </TR>
 *   <TR>
 *     <TH>in</TH>
 *     <TD>java.lang.Number</TD>
 *     <TD>
 *       The Channel can accept data from any subclass of Number.   All values
 *       will be converted to ints.
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
 * The following example shows how to use the Mult process in a small program.
 * The program also uses some of the other plugNplay processes. The
 * program generates a sequence of Integers, multiplies them by 42 and prints
 * this on the screen.
 *
 * <PRE>
 * import org.jcsp.lang.*;
 * import org.jcsp.plugNplay.*;
 * 
 * public class MultExample {
 * 
 *   public static void main (String[] argv) {
 * 
 *     final One2OneChannel a = Channel.one2one ();
 *     final One2OneChannel b = Channel.one2one ();
 * 
 *     new Parallel (
 *       new CSProcess[] {
 *         new Numbers (a.out ()),
 *         new Mult (42, a.in (), b.out ()),
 *         new Printer (b.in (), "--> ", "\n")
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

public final class Mult implements CSProcess
{
   /** The multiplier */
   private final int n;
   
   /** The input Channel */
   private final ChannelInput in;
   
   /** The output Channel */
   private final ChannelOutput out;
   
   /**
    * Construct a new Mult process with the input Channel in and the
    * output Channel out.
    *
    * @param n the multiplier
    * @param in the input channel
    * @param out the output channel
    */
   public Mult(int n, final ChannelInput in, final ChannelOutput out)
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
      {
         final int i = ((Integer) in.read()).intValue();
         out.write(new Integer(n*i));
      }
   }
}
