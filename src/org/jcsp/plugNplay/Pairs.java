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
 * Generates sums of successive pairs of input <TT>Integer</TT>s.
 * <H2>Process Diagram</H2>
 * <H3>External View</H3>
 * <p><img src="doc-files/Pairs1.gif"></p>
 * <H3>Internal View</H3>
 * <p><img src="doc-files/Pairs2.gif"></p>
 * <H2>Description</H2>
 * <TT>Pairs</TT> is a process whose output is always the sum of its previous
 * two inputs.
 * <P>
 * Two inputs are needed before any
 * output is produced but that, thereafter, one output is produced for each
 * input.
 * <H2>Channel Protocols</H2>
 * <TABLE BORDER="2">
 *   <TR>
 *     <TH COLSPAN="3">Input Channels</TH>
 *   </TR>
 *   <TR>
 *     <TH>in</TH>
 *     <TD>java.lang.Number</TD>
 *     <TD>
 *       The Channel can accept data from any subclass of Number.  All values
 *       will be converted to ints.
 *     </TD>
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

public class Pairs implements CSProcess
{
   /** The input Channel */
   private ChannelInput in;
   
   /** The output Channel */
   private ChannelOutput out;
   
   /**
    * Construct a new Pairs process with the input Channel in and the
    * output Channel out.
    *
    * @param in The input Channel
    * @param out The output Channel
    */
   public Pairs(ChannelInput in, ChannelOutput out)
   {
      this.in = in;
      this.out = out;
   }
   
   /**
    * The main body of this process.
    */
   public void run()
   {
      
      final One2OneChannel a = Channel.one2one();
      final One2OneChannel b = Channel.one2one();
      final One2OneChannel c = Channel.one2one();
      
      new Parallel(new CSProcess[] 
                  {
                     new Delta2(in, a.out(), b.out()),
                     new Plus(a.in(), c.in(), out),
                     new Tail(b.in(), c.out())
                  }).run();
   }
}
