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
 * This demultiplexes data from its input channel to its output channel array.
 *
 * <H2>Process Diagram</H2>
 * <p><img src="doc-files/Deparaplex1.gif"></p>
 * <H2>Description</H2>
 * <TT>Deparaplex</TT> is a process to convert the single stream of
 * <TT>Object[]</TT> packets sent from a {@link Paraplex} process on the other
 * end of its <TT>in</TT> channel back to separate streams (its <TT>out</TT>
 * channels).  It assumes that {@link Paraplex} operates on the same
 * size array of channels as its <TT>out</TT> array.  It conforms to
 * contract required by {@link Paraplex} for a process receiving its packets.
 * <P>
 * In each cycle, it inputs one packet and outputs its contents <I>in parallel</I>
 * to each of its output channels.
 * The parallel output means that the process will wait until each item is accepted
 * by every channel -- in whatever order is demanded by its environment.
 * The ordering of the channels in the <TT>out</TT> array, therefore, makes
 * no difference to the functionality of this process.
 *
 * <H2>Channel Protocols</H2>
 * <TABLE BORDER="2">
 *   <TR>
 *     <TH COLSPAN="3">Input Channels</TH>
 *   </TR>
 *   <TR>
 *     <TH>in</TH>
 *     <TD>Object[]</TD>
 *     <TD>
 *       A packet carrying the <I>paraplexed</I> data.
 *   </TR>
 *   <TR>
 *     <TH COLSPAN="3">Output Channels</TH>
 *   </TR>
 *   <TR>
 *     <TH>out[]</TH>
 *     <TD>Object</TD>
 *     <TD>
 *       Most channels in this package carry integers.
 *     </TD>
 *   </TR>
 * </TABLE>
 *
 * <H2>Example</H2>
 * <PRE>
 * import org.jcsp.lang.*;
 * import org.jcsp.plugNplay.*;
 * 
 * public class DeparaplexExample {
 * 
 *   public static void main (String[] args) {
 * 
 *     final One2OneChannel[] a = Channel.one2oneArray (3);
 *     final One2OneChannel b = Channel.one2one ();
 *     final One2OneChannel[] c = Channel.one2oneArray (3);
 *     final One2OneChannel d = Channel.one2one ();
 * 
 *     new Parallel (
 *       new CSProcess[] {
 *         new Numbers (a[0].out ()),
 *         new Squares (a[1].out ()),
 *         new Fibonacci (a[2].out ()),
 *         new Paraplex (Channel.getInputArray (a), b.out ()),
 *         new Deparaplex (b.in (), Channel.getOutputArray (c)),
 *         new Paraplex (Channel.getInputArray (c), d.out ()),
 *         new CSProcess () {
 *           public void run () {
 *             System.out.println ("\n\t\tNumbers\t\tSquares\t\tFibonacci\n");
 *             while (true) {
 *               Object[] data = (Object[]) d.in ().read ();
 *               for (int i = 0; i < data.length; i++) {
 *                 System.out.print ("\t\t" + data[i]);
 *               }
 *               System.out.println ();
 *             }
 *           }
 *         }
 *       }
 *     ).run ();
 *   }
 * 
 * }
 * </PRE>
 *
 * @see org.jcsp.plugNplay.Paraplex
 * @see org.jcsp.plugNplay.Multiplex
 * @see org.jcsp.plugNplay.Demultiplex
 *
 * @author P.H. Welch
 */

public final class Deparaplex implements CSProcess
{
   /** The input Channel */
   private final ChannelInput in;
   
   /** The output Channels */
   private final ChannelOutput[] out;
   
   /**
    * Construct a new Deparaplex process with the input Channel in and the output
    * Channels out. The ordering of the Channels in the out array make
    * no difference to the functionality of this process.
    *
    * @param in the input channel
    * @param out the output Channels
    */
   public Deparaplex(final ChannelInput in, final ChannelOutput[] out)
   {
      this.in = in;
      this.out = out;
   }
   
   /**
    * The main body of this process.
    */
   public void run()
   {
      final ProcessWrite[] outputProcess = new ProcessWrite[out.length];
      for (int i = 0; i < out.length; i++)
         outputProcess[i] = new ProcessWrite(out[i]);
      Parallel parOutput = new Parallel(outputProcess);
 
      while (true)
      {
         Object[] data = (Object[]) in.read();
         for (int i = 0; i < data.length; i++)
            outputProcess[i].value = data[i];
         parOutput.run();
      }
   }
}
