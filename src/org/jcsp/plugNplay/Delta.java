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
 * to its array of output channels.
 *
 * <H2>Process Diagram</H2>
 * <p><img src="doc-files/Delta1.gif"></p>
 * <H2>Description</H2>
 * The Delta class is a process which has an infinite loop that waits
 * for Objects of any type to be sent down the in Channel. The process then
 * writes the reference to the Object in parallel down each of the Channels
 * in the out array.
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
 *     <TH>out[]</TH>
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
public final class Delta implements CSProcess
{
   /** The input Channel */
   private ChannelInput  in;
   
   /** The output Channels */
   private ChannelOutput[] out;
   
   /**
    * Construct a new Delta process with the input Channel in and the output
    * Channels out. The ordering of the Channels in the out array make
    * no difference to the functionality of this process.
    *
    * @param in the input channel
    * @param out the output Channels
    */
   public Delta(ChannelInput in, ChannelOutput[] out)
   {
      this.in   = in;
      this.out = out;
   }
   
   /**
    * The main body of this process.
    */
   public void run()
   {
      try {
         ProcessWrite[] procs = new ProcessWrite[out.length];
         for (int i = 0; i < out.length; i++)
            procs[i] = new ProcessWrite(out[i]);
         Parallel par = new Parallel(procs);
      
         while (true)
         {
            Object value = in.read();
            for (int i = 0; i < out.length; i++)
               procs[i].value = value;
            par.run();
         }
      } catch (PoisonException p) {
         // <i>don't know which channel was posioned ... so, poison them all!</i>
         int strength = p.getStrength ();   // <i>use same strength of poison</i>
         in.poison (strength);
         for  (int i = 0; i < out.length; i++) {
            out[i].poison (strength);
         }
      }
   }

}
