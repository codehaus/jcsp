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
 * This process copies input to output, imposing a fixed time delay
 * between these events.
 * <H2>Process Diagram</H2>
 * <p><img src="doc-files/FixedDelay1.gif"></p>
 * <H2>Description</H2>
 * This process copies input to output, imposing a fixed time delay
 * between these events.
 * Note that this does not imply that the rate of output will be regular,
 * since that depends on the rate of input and the rate at which output
 * taken.
 * To impose a regular rate of output, see {@link Regulate}.
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
 *       The out Channel sends the the same type of data (in
 *       fact, the <I>same</I> data) as is input.
 *     </TD>
 *   </TR>
 * </TABLE>
 *
 * @see org.jcsp.plugNplay.Regulate
 * @see org.jcsp.plugNplay.Regular
 *
 * @author P.H. Welch and P.D. Austin
 */

public final class FixedDelay implements CSProcess
{
   /** The input Channel */
   private ChannelInput in;
   
   /** The output Channel */
   private ChannelOutput out;
   
   /**
    * The time the process is to wait in milliseconds between receiving a
    * message and then sending it.
    */
   private long delayTime;
   
   /**
    * This process copies input to output, imposing a fixed time delay
    * between these events.
    *
    * @param delayTime the time the process is to wait in milliseconds
    *   between receiving a message and then sending it &ndash;
    *   a zero or negative value implies no waiting.
    * @param in the input Channel
    * @param out the output Channel
    */
   public FixedDelay(long delayTime, ChannelInput in, ChannelOutput out)
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
      CSTimer tim = new CSTimer();
      while  (true)
      {
         Object o = in.read();
         tim.sleep(delayTime);
         out.write(o);
      }
   }
}
