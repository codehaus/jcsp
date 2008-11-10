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
 * This is a running-sum integrator of the <TT>Integers</TT> on its input stream
 * to its output stream.
 * <H2>Process Diagram</H2>
 * <H3>External View</H3>
 * <!-- INCORRECT DIAGRAM: <p><img src="doc-files/Integrate1.gif"></p> -->
 * <PRE>
 *         ___________  
 *     in |           | out
 *    -->-| Integrate |-->---
 *        |___________|
 * </PRE>
 * <H3>Internal View</H3>
 * <!-- <p><img src="doc-files/Integrate2.gif"></p> -->
 * <PRE>
 *         ___________________________________________ 
 *        |  _______                   ________       |
 *     in | |       |                 |        |      | out
 *    -->---| {@link Plus Plus}  |------->---------| {@link Delta2 Delta2} |--------->--- 
 *        | |_______|                 |________|      |
 *        |     |                        |            |
 *        |     |      ____________      |            |
 *        |     |     |            |     |            |
 *        |     +--<--| {@link Prefix Prefix (0)} |--<--+            |
 *        |           |____________|                  |
 *        |                                 Integrate |
 *        |___________________________________________|
 * </PRE>
 * <H2>Description</H2>
 * The Integrate class is a process which outputs running totals of
 * the Numbers sent down the in Channel.
 * <P>
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
public class Integrate implements CSProcess
{
   /** The input Channel */
   private ChannelInput in;
   
   /** The output Channel */
   private ChannelOutput out;
   
   /**
    * Construct a new Integrate process with the input Channel in and the
    * output Channel out.
    *
    * @param in the input Channel
    * @param out the output Channel
    */
   public Integrate(ChannelInput in, ChannelOutput out)
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
                     new Delta2(a.in(), out, b.out()),
                     new Prefix(new Integer(0), b.in(), c.out()),
                     new Plus(in, c.in(), a.out())
                  }).run();
   }
}
