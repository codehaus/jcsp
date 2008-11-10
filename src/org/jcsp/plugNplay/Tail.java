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
 * The output stream is the tail of its input stream.
 * <H2>Process Diagram</H2>
 * <p><img src="doc-files/Tail1.gif"></p>
 * <H2>Description</H2>
 * The first Object (i.e. <i>head</i>) of its input stream is not forwarded.
 * The rest (i.e. <i>tail</i>) is copied through unchanged.
 * <P>
 * Two inputs are needed before any output
 * is produced but, thereafter, one output is produced for each input.
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
 *       The in Channel can accept data of any Class.
 *     </TD>
 *   </TR>
 *   <TR>
 *     <TH COLSPAN="3">Output Channels</TH>
 *   </TR>
 *   <TR>
 *     <TH>out</TH>
 *     <TD>java.lang.Object</TD>
 *     <TD>
 *       The out Channel will send data of the same type
 *       as that sent down the in Channel.
 *     </TD>
 *   </TR>
 * </TABLE>
 * <P>
 * <H2>Implementation Note</H2>
 * The implementation uses an {@link Identity} process for the copy loop:
 * <PRE>
 *   public void run () {
 *     in.read ();                        // accept, but discard, the first item
 *     new Identity (in, out).run ();     // copy the rest of the stream
 *   }
 * </PRE>
 *
 * @author P.H. Welch and P.D. Austin
 */

public final class Tail implements CSProcess
{
   /** The input Channel */
   private ChannelInput in;
   
   /** The output Channel */
   private ChannelOutput out;
   
   /**
    * Construct a new Tail process with the input Channel in and the
    * output Channel out.
    *
    * @param in the input Channel
    * @param out the output Channel
    */
   public Tail(ChannelInput in, ChannelOutput out)
   {
      this.in = in;
      this.out = out;
   }
   
   /**
    * The main body of this process.
    */
   public void run()
   {
      in.read();
      new Identity(in, out).run();
   }
}
