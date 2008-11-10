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
 * <I>Fair</I> multiplexes its input Object stream array into one output stream.
 * <H2>Process Diagram</H2>
 * <p><img src="doc-files/Plex1.gif"></p>
 * <H2>Description</H2>
 * <TT>Plex</TT> is a process whose output stream is a <I>fair</I> multiplexing
 * of its input streams.  It makes no assumptions about the traffic patterns occuring
 * on the input streams and ensures that no input stream will be starved by busy siblings.
 * It guarantees that if an input stream has data pending, no other stream will be
 * serviced <I>twice</I> before that data is serviced.
 * <H2>Channel Protocols</H2>
 * <TABLE BORDER="2">
 *   <TR>
 *     <TH COLSPAN="3">Input Channels</TH>
 *   </TR>
 *   <TR>
 *     <TH>in[]</TH>
 *     <TD>java.lang.Object</TD>
 *     <TD>
 *       The input streams.
 *     </TD>
 *   </TR>
 *   <TR>
 *     <TH COLSPAN="3">Output Channels</TH>
 *   </TR>
 *   <TR>
 *     <TH>out</TH>
 *     <TD>java.lang.Object</TD>
 *     <TD>
 *       The multiplexed output stream.
 *     </TD>
 *   </TR>
 * </TABLE>
 * <H2>Example</H2>
 * The following example shows how to use <TT>Plex</TT> in a small program.
 * <PRE>
 * import org.jcsp.lang.*;
 * import org.jcsp.plugNplay.*;
 * 
 * public class PlexExample {
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
 * <H2>Implemntation Note</H2>
 * For information, here is the <TT>run</TT> method for this process:
 *
 * <PRE>
 *   public void run () {
 *     Alternative alt = new Alternative (in);       // in is the input channel array
 *     while (true) {
 *       out.write (in[alt.fairSelect ()].read ());  // out is the output channel
 *     }
 *   }
 * </PRE>
 *
 * @see org.jcsp.plugNplay.Plex2
 * @see org.jcsp.plugNplay.Multiplex
 *
 * @author P.H. Welch
 */

public final class Plex implements CSProcess
{
   /** The first input Channel */
   private final AltingChannelInput[] in;
   
   /** The output Channel */
   private final ChannelOutput out;
   
   /**
    * Construct a new <TT>Plex</TT> process with input channels
    * <TT>in</TT> and output channel <TT>out</TT>.
    * The ordering of the input channels makes no difference
    * to the behaviour of this process.
    *
    * @param in the input channels
    * @param out the output channel
    */
   public Plex(AltingChannelInput[] in, ChannelOutput out)
   {
      this.in = in;
      this.out = out;
   }
   
   /**
    * The main body of this process.
    */
   public void run()
   {
      Alternative alt = new Alternative(in);
      while (true)
         out.write(in[alt.fairSelect()].read());
   }
}
