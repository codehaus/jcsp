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


import org.jcsp.lang.*;
import org.jcsp.plugNplay.Printer;
import org.jcsp.demos.util.*;

/**
 * @author P.H. Welch
 */
public class RegulateTest {

  public static final String TITLE = "Regulated Process Behaviour";
  public static final String DESCR =
  		"Shows how a process with periodic behaviour can use an alt to have its actions regulated externally. " +
  		"The Regulate process creates numbers at regular intervals. Instead of sleeping for those intervals " +
  		"it makes use of a timeout guard within an Alt. Other guards allow the timeout interval to be reset " +
  		"and the number it outputs to be modified.";

  public static void main (String[] args) {

  	Ask.app (TITLE, DESCR);
  	Ask.show ();
  	Ask.blank ();

    final One2OneChannel a = Channel.one2one();
    final One2OneChannel b = Channel.one2one();
    final One2OneChannel reset = Channel.one2one();

    new Parallel (
      new CSProcess[] {
        new Variate (a.out(), 5000, 10, 2),
        new Regulate (a.in(), reset.in(), b.out(), 500),
        new Printer (b.in(), "RegulateTest ==> ", "\n"),
        new CSProcess () {
          // this controls the Regulate process, switching its firing
          // rate between a half and one second.  The switches occur
          // every five seconds.
          public void run () {
            final Long halfSecond = new Long (500);
            final Long second = new Long (1000);
            final CSTimer tim = new CSTimer ();
            long timeout = tim.read ();
            while (true) {
              timeout += 5000;
              tim.after (timeout);
              System.out.println ("                    <== now every second");
              reset.out().write (second);
              timeout += 5000;
              tim.after (timeout);
              System.out.println ("                    <== now every half second");
              reset.out().write (halfSecond);
            }
          }
        }
      }
    ).run ();

  }

}
