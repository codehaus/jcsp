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
 * <I>Black holes</I> anything sent to it.
 * <H2>Process Diagram</H2>
 * <p><img src="doc-files/BlackHole1.gif"></p>
 * <H2>Description</H2>
 * <TT>BlackHole</TT> is a process that accepts everything sent to it.
 * This class can be used to ignore the output from a process while ensuring
 * that the data is always read from the channel.
 * <P>
 * <I>Note: this functionality is (more efficiently) provided by
 * a {@link org.jcsp.lang.BlackHoleChannel}.</I>
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
 * </TABLE>
 *
 * @author P.H. Welch and P.D. Austin
 */

public final class BlackHole implements CSProcess
{
   /** The input Channel */
   private ChannelInput in;
   
   /**
    * Construct a new BlackHole process with the input Channel in.
    *
    * @param in the input channel
    */
   public BlackHole(ChannelInput in)
   {
      this.in = in;
   }
   
   /**
    * The main body of this process.
    */
   public void run()
   {
      while (true)
         in.read();
   }
}
