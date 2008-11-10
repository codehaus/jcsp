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
 * This process broadcasts objects arriving on its input channel <I>in parallel</I>
 * to its two output channels.
 *
 * <H2>Process Diagram</H2>
 * <!-- INCORRECT DIAGRAM: <p><img src="doc-files/Delta21.gif"></p> -->
 * <PRE>
 *         ________  out0 
 *    in  |        |--->---
 *   -->--| Delta2 | out1
 *        |________|--->---
 * </PRE>
 * <H2>Description</H2>
 * The Delta2 class is a process which has an infinite loop that waits
 * for Objects of any type to be sent down the in Channel. The process then
 * writes the reference to the Object in parallel down the out0 and out1
 * Channels.
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
 *     <TH>out0, out1</TH>
 *     <TD>java.lang.Object</TD>
 *     <TD>
 *       The output Channels will carry a broadcast of whatever
 *       is sent down the in Channel.
 *     </TD>
 *   </TR>
 * </TABLE>
 *
 * @author P.H. Welch and P.D. Austin
 */
public final class Delta2 implements CSProcess
{
   /** The input Channel */
   private ChannelInput  in;
   
   /** The first output Channel */
   private ChannelOutput out0;
   
   /** The second output Channel */
   private ChannelOutput out1;
   
   /**
    * Construct a new Delta2 process with the input Channel in and the output
    * Channels out0 and out1. The ordering of the Channels out0 and out1 make
    * no difference to the functionality of this process.
    *
    * @param in the input channel
    * @param out0 an output Channel
    * @param out1 an output Channel
    */
   public Delta2(ChannelInput in, ChannelOutput out0, ChannelOutput out1)
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
      ProcessWrite[] parWrite = {new ProcessWrite(out0), new ProcessWrite(out1)};
      Parallel par = new Parallel(parWrite);
      while (true)
      {
         Object value = in.read();
         parWrite[0].value = value;
         parWrite[1].value = value;
         par.run();
      }
   }
}
