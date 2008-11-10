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
 * Generates sums of successive pairs of input values.
 * <H2>Process Diagram</H2>
 * <H3>External View</H3>
 * <p><IMG SRC="doc-files/PairsInt1.gif"></p>
 * <H3>Internal View</H3>
 * <p><IMG SRC="doc-files/PairsInt2.gif"></p>
 * <P>
 * <H2>Description</H2>
 * <TT>PairsInt</TT> is a process whose output is always the sum of its previous
 * two inputs.
 * <P>
 * Two inputs are needed before any
 * output is produced but that, thereafter, one output is produced for each
 * input.
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
 *
 * @author P.H. Welch and P.D. Austin
 */

public final class PairsInt implements CSProcess
{
   /** The input Channel */
   private final ChannelInputInt in;
   
   /** The output Channel */
   private final ChannelOutputInt out;
   
   /**
    * Construct a new PairsInt process with the input Channel in and the
    * output Channel out.
    *
    * @param in The input Channel
    * @param out The output Channel
    */
   public PairsInt(final ChannelInputInt in, final ChannelOutputInt out)
   {
      this.in = in;
      this.out = out;
   }
   
   /**
    * The main body of this process.
    */
   public void run()
   {
      final One2OneChannelInt a = Channel.one2oneInt();
      final One2OneChannelInt b = Channel.one2oneInt();
      final One2OneChannelInt c = Channel.one2oneInt();
      
      new Parallel(new CSProcess[] 
                  {
                     new Delta2Int(in, a.out(), b.out()),
                     new PlusInt(a.in(), c.in(), out),
                     new TailInt(b.in(), c.out())
                  }).run();
   }
}
