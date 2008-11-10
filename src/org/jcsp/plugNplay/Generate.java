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
 * Generates an infinite (constant) sequence of <TT>Integer</TT>s.
 * <H2>Process Diagram</H2>
 * <p><img src="doc-files/Generate1.gif"></p>
 * <H2>Description</H2>
 * <TT>Generate</TT> is a process that generates an infinite sequence
 * of the integer, <TT>n</TT>, with which it is configured.
 * <H2>Channel Protocols</H2>
 * <TABLE BORDER="2">
 *   <TR>
 *     <TH COLSPAN="3">Output Channels</TH>
 *   </TR>
 *   <TR>
 *     <TH>out</TH>
 *     <TD>java.lang.Integer</TD>
 *     <TD>
 *       The output will always be of type Integer.
 *     </TD>
 *   </TR>
 * </TABLE>
 *
 * @author P.H. Welch and P.D. Austin and P.H. Welch
 */

public final class Generate implements CSProcess
{
   /** The output Channel */
   private final ChannelOutput out;
   
   /** The output number */
   private final Integer N;
   
   /**
    * Construct a new <TT>Generate</TT> process with the output channel <TT>out</TT>.
    *
    * @param out the output channel
    * @param n the integer to generate
    */
   public Generate(final ChannelOutput out, final int n)
   {
      this.out = out;
      N = new Integer(n);
   }
   
   /**
    * The main body of this process.
    */
   public void run()
   {
      try {
	while (true)
          out.write(N);
      } catch (PoisonException p) {
	// nothing to do
      }
   }
}
