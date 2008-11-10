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
 * This is a running-sum integrator of the <TT>ints</TT> on its input stream
 * to its output stream.
 * <H2>Process Diagram</H2>
 * <H3>External View</H3>
 * <!-- INCORRECT DIAGRAM: <p><IMG SRC="doc-files/IntegrateInt1.gif"></p> -->
 * <PRE>
 *          ______________  
 *     in  |              | out
 *    -->--| IntegrateInt |-->---
 *         |______________|
 * </PRE>
 * <H3>Internal View</H3>
 * <!-- <p><IMG SRC="doc-files/IntegrateInt1.gif"></p> -->
 * <PRE>
 *         ____________________________________________
 *        |  __________                   ___________  |
 *     in | |          |                 |           | | out
 *    -->---| {@link PlusInt PlusInt}  |------->---------| {@link Delta2Int Delta2Int} |---->--- 
 *        | |__________|                 |___________| |
 *        |     |                              |       |
 *        |     |       _______________        |       |
 *        |     |      |               |       |       |
 *        |     +---<--| {@link PrefixInt PrefixInt (0)} |---<---+       |
 *        |            |_______________|               |
 *        |                               IntegrateInt |
 *        |____________________________________________|
 * </PRE>
 * <H2>Description</H2>
 * The IntegrateInt class is a process which outputs running totals of
 * the Numbers sent down its input channel.
 * <P>
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

public final class IntegrateInt implements CSProcess
{
   /** The input Channel */
   private final ChannelInputInt in;
   
   /** The output Channel */
   private final ChannelOutputInt out;
   
   /**
    * Construct a new IntegrateInt process with the input Channel in and the
    * output Channel out.
    *
    * @param in the input Channel
    * @param out the output Channel
    */
   public IntegrateInt(final ChannelInputInt in, final ChannelOutputInt out)
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
                     new Delta2Int(a.in(), out, b.out()),
                     new PrefixInt(0, b.in(), c.out()),
                     new PlusInt(in, c.in(), a.out())
                  }).run();
   }
}
