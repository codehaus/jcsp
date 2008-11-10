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
import java.io.*;

/**
 * Prints each <TT>Object</TT> from its input channel to a <TT>PrintStream</TT>.
 * <H2>Process Diagram</H2>
 * <p><img src="doc-files/Printer1.gif"></p>
 * <H2>Description</H2>
 * <TT>Printer</TT> is a process for printing each object from its
 * <TT>in</TT> channel to a <TT>PrintStream</TT> object (by default,
 * <TT>System.out</TT>).
 * <P>
 * For convenience, <TT>Printer</TT> may be configured with prefix and postfix
 * strings with which to decorate its output.
 *
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
 * <H2>Example</H2>
 * See the example in {@link Merge}.
 *
 * @see org.jcsp.lang.Parallel
 *
 * @author P.H. Welch and P.D. Austin
 */

public class Printer implements CSProcess
{
   /** The channel from which to read */
   private ChannelInput in;
   
   /** The stream to which to write */
   private PrintStream printStream;
   
   /** The string to write in front of each object */
   private String prefix;
   
   /** The string to write after each object */
   private String postfix;
   
   /**
    * Construct a new <TT>Printer</TT> with <TT>System.out</TT> as
    * its <TT>PrintStream</TT> and empty prefix and postfix strings.
    *
    * @param in the channel from which to read
    */
   public Printer(final ChannelInput in)
   {
      this(in, System.out, "", "");
   }
   
   /**
    * Construct a new <TT>Printer</TT> with <TT>System.out</TT> as
    * its <TT>PrintStream</TT>.
    *
    * @param in the channel from which to read
    * @param prefix the string to write in front of each object
    * @param postfix the string to write after each object
    */
   public Printer(final ChannelInput in, final String prefix, final String postfix)
   {
      this(in, System.out, prefix, postfix);
   }
   
   /**
    * Construct a new <TT>Printer</TT> with empty prefix and postfix strings.
    *
    * @param in the channel from which to read
    * @param printStream the stream to which to write
    */
   public Printer(final ChannelInput in, final PrintStream printStream)
   {
      this(in, printStream, "", "");
   }
   
   /**
    * Construct a new <TT>Printer</TT>.
    *
    * @param in he channel from which to read
    * @param printStream the stream to which to write
    * @param prefix the string to write in front of each object
    * @param postfix the string to write after each object
    */
   public Printer(final ChannelInput in, final PrintStream printStream,
           final String prefix, final String postfix)
   {
      this.in = in;
      this.printStream = printStream;
      this.prefix = prefix;
      this.postfix = postfix;
   }
   
   /**
    * The main body of this process.
    */
   public synchronized void run()
   {
      try {
	while (true)
        {
           printStream.print(prefix);
           printStream.print(in.read());
           printStream.print(postfix);
        }
      } catch (PoisonException p) {
	// nothing to do
      }
   }
}
