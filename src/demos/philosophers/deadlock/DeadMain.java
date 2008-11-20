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
public class DeadMain {

  public static final String TITLE = "Dining Philosophers [deadlocking]";
  public static final String DESCR =
  	"Shows the 'dining philosophers' deadlock problem. Each of the philosophers attempts to claim the " +
  	"shared resources (forks) via channels (which may block). Deadlock may occur if all philosophers are " +
  	"seated and each holds one fork. This is a non-deterministic system and so the point at which deadlock " +
  	"occurs (if at all) cannot be predicted. When deadlock occurs, the clock will continue ticking but " +
  	"no actions will be written to the screen by the philosophers as each is waiting to pick up another " +
  	"fork.";

  public static void main (String[] args) {

  	Ask.app (TITLE, DESCR);
  	Ask.addPrompt ("philosophers", 1, 100, 5);
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
