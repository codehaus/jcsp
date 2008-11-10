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
 * This generates the <I>Fibonacci</I> sequence on its output channel.
 *
 * <H2>CSProcess Diagram</H2>
 * <H3>External View</H3>
 * <!-- <p><img src="doc-files/Fibonacci1.gif"></p> -->
 * <PRE>
 *         ___________  
 *        |           | out
 *        | Fibonacci |------>
 *        |___________|
 * </PRE>
 * <H3>Internal View</H3>
 * <!-- INCORRECT DIAGRAM: <p><img src="doc-files/Fibonacci2.gif"></p> -->
 * <PRE>
 *         ________________________________
 *        |                                |
 *        |  ____________       ________   |
 *        | |            |     |        |  | out
 *        | | {@link Prefix Prefix (0)} |-->--| {@link Delta2 Delta2} |------>-- 
 *        | |____________|     |________|  |
 *        |        |               |       |
 *        |        ^               V       |
 *        |  ______|_____       ___|___    |
 *        | |            |     |       |   |
 *        | | {@link Prefix Prefix (1)} |--<--| {@link Pairs Pairs} |   |
 *        | |____________|     |_______|   |
 *        |                                |
 *        |                      Fibonacci |
 *        |________________________________|
 * </PRE>
 * <H2>Description</H2>
 * <TT>FibonacciInt</TT> generates the sequence of <I>Fibonacci</I>
 * Numbers on its output channel.
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
 * @author P.H. Welch and P.D. Austin
 */
public class Fibonacci implements CSProcess
{
   /** The output Channel */
   private ChannelOutput out;
   
   /**
    * Construct a new Fibonacci process with the output Channel out.
    *
    * @param out the output channel
    */
   public Fibonacci(ChannelOutput out)
   {
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
      final One2OneChannel d = Channel.one2one();
      
      new Parallel(new CSProcess[] 
                  {
                     new Prefix(new Integer(1), c.in(), d.out()),
                     new Prefix(new Integer(0), d.in(), a.out()),
                     new Delta2(a.in(), b.out(), out),
                     new Pairs(b.in(), c.out())
                  }).run();
   }
}
