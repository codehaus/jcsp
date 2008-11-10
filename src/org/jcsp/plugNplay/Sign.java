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
 * Converts each input <TT>Object</TT> to a <TT>String</TT>, prefixing it
 * with a user-defined <TT>sign</TT>.
 * <H2>Process Diagram</H2>
 * <p><img src="doc-files/Sign1.gif"></p>
 * <H2>Description</H2>
 * <TT>Sign</TT> converts each input <TT>Object</TT> to a <TT>String</TT>,
 * prefixing it with a user-defined <TT>sign</TT>.
 * <P>
 * <H2>Channel Protocols</H2>
 * <TABLE BORDER="2">
 *   <TR>
 *     <TH COLSPAN="3">Input Channels</TH>
 *   </TR>
 *   <TR>
 *     <TH>in</TH>
 *     <TD>java.lang.Object</TD>
 *     <TD>
 *       The Channel accepts any class of data.
 *     </TD>
 *   </TR>
 *   <TR>
 *     <TH COLSPAN="3">Output Channels</TH>
 *   </TR>
 *   <TR>
 *     <TH>out</TH>
 *     <TD>java.lang.String</TD>
 *     <TD>
 *       The output will always be of type String.
 *     </TD>
 *   </TR>
 * </TABLE>
 * <H2>Example</H2>
 * The following example shows how to use <TT>Sign</TT> in a small program.
 * <PRE>
 * import org.jcsp.lang.*;
 * import org.jcsp.plugNplay.*;
 * 
 * public class SignExample {
 * 
 *   public static void main (String[] argv) {
 * 
 *     final One2OneChannel[] a = Channel.one2oneArray (3);
 *     final One2OneChannel[] b = Channel.one2oneArray (3);
 *     final One2OneChannel c = Channel.one2one ();
 * 
 *     new Parallel (
 *       new CSProcess[] {
 *         new Numbers (a[0].out ()),
 *         new Fibonacci (a[1].out ()),
 *         new Squares (a[2].out ()),
 *         new Sign ("Numbers ", a[0].in (), b[0].out ()),
 *         new Sign ("            Fibonacci ", a[1].in (), b[1].out ()),
 *         new Sign ("                          Squares ", a[2].in (), b[2].out ()),
 *         new Plex (Channel.getInputArray (b), c.out ()),
 *         new Printer (c.in (), "", "\n")
 *       }
 *     ).run ();
 * 
 *   }
 * 
 * }
 * </PRE>
 * <P>
 *
 * @see org.jcsp.plugNplay.ints.SignInt
 *
 * @author P.H. Welch and P.D. Austin
 */

public final class Sign implements CSProcess
{
   /** The user-defined sign to attach to each item */
   private final String sign;
   
   /** The input Channel */
   private final ChannelInput in;
   
   /** The output Channel */
   private final ChannelOutput out;
   
   /**
    * Construct a new Sign process with the input Channel in and the
    * output Channel out.
    *
    * @param sign the user-defined signature to attach to each item.
    * @param in the input Channel.
    * @param out the output Channel.
    */
   public Sign(final String sign, final ChannelInput in, final ChannelOutput out)
   {
      this.sign = sign;
      this.in = in;
      this.out = out;
   }
   
   /**
    * The main body of this process.
    */
   public void run()
   {
      while (true)
         out.write(sign + in.read());
   }
}
