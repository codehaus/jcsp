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
 * This holds on to data from its input channel for a fixed delay before passing
 * it on to its output channel.
 *
 * <H2>Process Diagram</H2>
 * <p><IMG SRC="doc-files/FixedDelayInt1.gif"></p>
 * <H2>Description</H2>
 * <TT>FixedDelayInt</TT> is a process that delays passing on input to its output
 * by a constant delay.
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
 * @see org.jcsp.plugNplay.ints.RegulateInt
 * @see org.jcsp.plugNplay.ints.RegularInt
 * 
 * @author P.H. Welch and P.D. Austin
 */

public final class FixedDelayInt implements CSProcess
{
   /** The input Channel */
   private final ChannelInputInt in;
   
   /** The output Channel */
   private final ChannelOutputInt out;
   
   /**
    * The time the process is to wait in milliseconds between receiving a
    * message and then sending it.
    */
   private final long delayTime;
   
   /**
    * Construct a new FixedDelayInt process with the input Channel in and the
    * output Channel out.
    *
    * @param delayTime the time the process is to wait in milliseconds
    *   between receiving a message and then sending it (a negative
    *   <TT>delayTime</TT> implies no waiting).
    * @param in the input Channel
    * @param out the output Channel
    */
   public FixedDelayInt(final long delayTime, final ChannelInputInt in, final ChannelOutputInt out)
   {
      this.in = in;
      this.out = out;
      this.delayTime = delayTime;
   }
   
   /**
    * The main body of this process.
    */
   public void run()
   {
      final CSTimer tim = new CSTimer();
      while  (true)
      {
         final int i = in.read();
         tim.sleep(delayTime);
         out.write(i);
      }
   }
}
