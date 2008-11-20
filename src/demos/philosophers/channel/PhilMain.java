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
import org.jcsp.demos.util.*;

/**
 * @author P.H. Welch
 */
public class PhilMain {

  public static final String TITLE = "Dining Philosophers";
  public static final String DESCR =
  	"Shows the 'dining philosophers' deadlock problem and solution implemented using JCSP channels. " +
  	"Each of the philosophers attempts to claim the shared resources (forks) via channels (which may block). " +
  	"Deadlock is prevented by the security guard which each philosopher must communicate " +
  	"with to gain permission to claim a fork.";

  public static void main (String[] args) {

  	Ask.app (TITLE, DESCR);
  	Ask.addPrompt ("philosophers", 1, 100, 10);
  	Ask.show ();
  	final int nPhilosophers = Ask.readInt ("philosophers");
  	Ask.blank ();

    Any2OneChannel report = Channel.any2one ();

    new Parallel (
      new CSProcess[] {
        new DiningPhilosophersCollege (nPhilosophers, report.out ()),
        new TextDisplay (nPhilosophers, report.in ())
      }
    ).run ();
  }

}
