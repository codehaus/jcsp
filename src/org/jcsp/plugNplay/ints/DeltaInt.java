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
 * This process broadcasts integers arriving on its input channel <I>in parallel</I>
 * to its array of output channels.
 *
 * <H2>Process Diagram</H2>
 * <p><IMG SRC="doc-files/DeltaInt1.gif"></p>
 * <H2>Description</H2>
 * <TT>Delta2Int</TT> is a process that broadcasts (<I>in parallel</I>) on its
 * array of output channels everything that arrives on its input channel.
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
 *     <TH>out[]</TH>
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

public final class DeltaInt implements CSProcess
{
   /** The input Channel */
   private final ChannelInputInt  in;
   
   /** The output Channels */
   private final ChannelOutputInt[] out;
   
   /**
    * Construct a new DeltaInt process with the input Channel in and the output
    * Channels out. The ordering of the Channels in the out array make
    * no difference to the functionality of this process.
    *
    * @param in the input channel
    * @param out the output Channels
    */
   public DeltaInt(final ChannelInputInt in, final ChannelOutputInt[] out)
   {
      this.in = in;
      this.out = out;
   }
   
   /**
    * The main body of this process.
    */
   public void run()
   {
      final ProcessWriteInt[] procs = new ProcessWriteInt[out.length];
      for (int i = 0; i < out.length; i++)
         procs[i] = new ProcessWriteInt(out[i]);
      Parallel par = new Parallel(procs);
      
      while (true)
      {
         final int value = in.read();
         for (int i = 0; i < out.length; i++)
            procs[i].value = value;
         par.run();
      }
   }
}
