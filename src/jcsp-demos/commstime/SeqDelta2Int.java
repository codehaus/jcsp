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


import org.jcsp.lang.*;

/**
 * This process broadcasts integers arriving on its input channel <I>in sequence</I>
 * to its two output channels.
 *
 * <H2>Process Diagram</H2>
 * <!-- INCORRECT DIAGRAM: <p><IMG SRC="doc-files/Delta2Int1.gif"></p> -->
 * <PRE>
 *         ___________  out0 
 *    in  |           |--->---
 *   -->--| Delta2Int | out1
 *        |___________|--->---
 * </PRE>
 * <H2>Description</H2>
 * <TT>Delta2Int</TT> is a process that broadcasts (<I>in parallel</I>) on its two output channels
 * everything that arrives on its input channel.
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
 *     <TH>out0, out1</TH>
 *     <TD>int</TD>
 *     <TD>
 *       The output Channels will carry a broadcast of whatever
 *       integers are sent down the in Channel.
 *     </TD>
 *   </TR>
 * </TABLE>
 *
 * @author P.H. Welch and P.D. Austin
 */

public final class SeqDelta2Int implements CSProcess
{
   /** The input Channel */
   private final ChannelInputInt  in;
   
   /** The first output Channel */
   private final ChannelOutputInt out0;
   
   /** The second output Channel */
   private final ChannelOutputInt out1;
   
   /**
    * Construct a new Delta2Int process with the input Channel in and the output
    * Channels out0 and out1. The ordering of the Channels out0 and out1 make
    * no difference to the functionality of this process.
    *
    * @param in the input channel
    * @param out0 an output Channel
    * @param out1 an output Channel
    */
   public SeqDelta2Int(final ChannelInputInt in, final ChannelOutputInt out0, final ChannelOutputInt out1)
   {
      this.in   = in;
      this.out0 = out0;
      this.out1 = out1;
   }
   
   /**
    * The main body of this process.
    */
   public void run()
   {
      while (true)
      {
         int value = in.read();
	 out0.write(value);
	 out1.write(value);
      }
   }
}
