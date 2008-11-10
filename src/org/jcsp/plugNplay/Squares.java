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
 * Generates the <TT>Integer</TT> stream <TT>1*1, 2*2, 3*3, etc</TT> by a somewhat unusual route.
 *
 * <H2>Process Diagram</H2>
 * <H3>External View</H3>
 * <p><img src="doc-files/Squares1.gif"></p>
 * <H3>Internal View</H3>
 * <p><img src="doc-files/Squares2.gif"></p>
 * <H2>Description</H2>
 * The Squares class is a process which generates a sequence of squares of
 * Natural numbers that are output down the out Channel.
 * <P>
 * <H2>Channel Protocols</H2>
 * <TABLE BORDER="2">
 *   <TR>
 *     <TH COLSPAN="3">Input Channels</TH>
 *   </TR>
 *   <TR>
 *   </TR>
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
 * @author P.H. Welch and P.D. Austin
 */
public class Squares implements CSProcess
{
   /** The output Channel */
   private ChannelOutput out;
   
   /**
    * Construct a new Squares process with the output Channel out.
    *
    * @param out the output channel
    */
   public Squares(ChannelOutput out)
   {
      this.out = out;
   }
   
   /**
    * The main body of this process.
    */
   public void run()
   {
      One2OneChannel a = Channel.one2one();
      One2OneChannel b = Channel.one2one();
      
      new Parallel(new CSProcess[] 
                  {
                     new Numbers(a.out()),
                     new Integrate(a.in(), b.out()),
                     new Pairs(b.in(), out)
                  }).run();
   }
}
