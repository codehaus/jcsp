
  /*************************************************************************
  *                                                                        *
  *  JCSP ("CSP for Java") libraries                                       *
  *  Copyright (C) 1996-2008 Peter Welch and Paul Austin.                  *
  *                                                                        *
  *  This library is free software; you can redistribute it and/or         *
  *  modify it under the terms of the GNU Lesser General Public            *
  *  License as published by the Free Software Foundation; either          *
  *  version 2.1 of the License, or (at your option) any later version.    *
  *                                                                        *
  *  This library is distributed in the hope that it will be useful,       *
  *  but WITHOUT ANY WARRANTY; without even the implied warranty of        *
  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU     *
  *  Lesser General Public License for more details.                       *
  *                                                                        *
  *  You should have received a copy of the GNU Lesser General Public      *
  *  License along with this library; if not, write to the Free Software   *
  *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307,  *
  *  USA.                                                                  *
  *                                                                        *
  *  Author contact: P.H.Welch@kent.ac.uk                                   *
  *                                                                        *
  *************************************************************************/

package org.jcsp.plugNplay.ints;

import org.jcsp.lang.*;
 
/**
 * This process generates a constant stream of <tt>Integer</tt>s at a regular rate.
 * <H2>Process Diagram</H2>
 * <PRE>
 *    ________________
 *   |                | out
 *   | RegularInt (n) |-->--
 *   |________________|
 * </PRE>
 * <H2>Description</H2>
 * This process generates a constant stream of <tt>Integer</tt>s at a regular rate
 * &ndash; at least, it does its best!
 * If the consumption of data is less than the set rate, that rate cannot be sustained.
 * If the consumption failure is only temporary, the set rate will be restored
 * when consumption resumes.
 * <P>
 * The interval (in msecs) defining the output flow rate is given by a constructor argument.
 * <H2>Channel Protocols</H2>
 * <TABLE BORDER="2">
 *   <TR>
 *     <TH COLSPAN="3">Output Channels</TH>
 *   </TR>
 *   <TR>
 *     <TH>out</TH>
 *     <TD>int</TD>
 *     <TD>
 *       A constant value is sent down this channel at a regular rate.
 *     </TD>
 *   </TR>
 * </TABLE>
 *
 * @see org.jcsp.plugNplay.ints.FixedDelayInt
 * @see org.jcsp.plugNplay.ints.RegulateInt
 *
 * @author P.H. Welch
 */

public class RegularInt implements CSProcess {
 
  final private ChannelOutputInt out;
  final private int n;
  final private long interval;
 
  /**
    * Construct the process.
    * 
    * @param out the output channel
    * @param n the value to be generated
    * @param interval the interval between outputs (in milliseconds)
    */
  public RegularInt (final ChannelOutputInt out, final int n, final long interval) {
    this.out = out;
    this.n = n;
    this.interval = interval;
  }
 
  /**
    * The main body of this process.
    */
  public void run () {
 
    final CSTimer tim = new CSTimer ();
    long timeout = tim.read ();       // read the (absolute) time once only
 
    while (true) {
      out.write (n);
      timeout += interval;            // set the next (absolute) timeout
      tim.after (timeout);            // wait until that (absolute) timeout
    }
  }
 
}
