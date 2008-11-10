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
 * Substitutes a user-configured <I>Object</I> for each Object in the stream
 * flowing through.
 * <H2>Process Diagram</H2>
 * <p><img src="doc-files/Substitute1.gif"></p>
 * <H2>Description</H2>
 * <TT>Substitute</TT> is a process that substitutes the (Object) <TT>o</TT>
 * with which it is configured for everything recieved on its <TT>in</TT> channel.
 * So, its output stream repeats the same Object but its rate of flow is triggered by
 * its input.
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
 *       The object to be sent down the Channel.
 *     </TD>
 *   </TR>
 * </TABLE>
 *
 * @author P.H. Welch and P.D. Austin
 */
public class Substitute implements CSProcess
{
   /** The Object to be sent down the out Channel. */
   private Object o;
   
   /** The input Channel */
   private ChannelInput in;
   
   /** The output Channel */
   private ChannelOutput out;
   
   /**
    * Construct a new Substitute process.
    *
    * @param o the Object to be sent down the out Channel.
    * @param in the input Channel
    * @param out the output Channel
    */
   public Substitute(ChannelInput in, ChannelOutput out, Object o)
   {
      this.in = in;
      this.out = out;
      this.o = o;
   }
   
   /**
    * The main body of this process.
    */
   public void run()
   {
      while (true)
      {
         in.read();
         out.write(o);
      }
   }
}
