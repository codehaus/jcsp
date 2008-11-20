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


//|
//|                       The Scribbling Philosophers
//|                       ===========================
//|
//| This program demonstrates the Crew class for Concurrent-read-exclusive-write
//| access to a shared object.
//|
//| A college consists of five philosophers and a blackboard.  The philosophers spend
//| their time between thinking, looking at what others have written on the blackboard
//| and scribbling on the blackboard themselves.
//|
//| Access to the blackboard is a bit constrained, so only one at a time may scribble.
//| Any number of philosophers may read the blackboard simultaenously, but not whilst
//| a scribbler is scribbling -- space is so tight that someone scribbling on the board
//| completely blocks the view.
//|
//|                              ______________
//|                              |            |       _______________
//|                              | blackboard |       |             |
//|    ---------------------<->--|            |   /-<-|  TimeKeeper |
//|      |   |   |   |   |       |   (CREW)   |   |   |_____________|
//|      :)  :)  :)  :)  :)      |____________|   |
//|      1   2   3   4   5                        v    _______________
//|      |   |   |   |   |                        |    |             |
//|    ---------------------->-------------------------| TextDisplay |
//|                   display/displayInfo              |_____________|
//|
//|

import org.jcsp.lang.*;
import org.jcsp.demos.util.*;

/**
 * @author P.H. Welch
 */
public class CrewCollege {

  public static final String TITLE = "Scribbling Philosophers";
  public static final String DESCR =
  	"Shows the use of the Crew class for Concurrent-read-exclusive-write access to a shared object.\n\n" +

	"A college consists of five philosophers and a blackboard.  The philosophers spend their time between " +
	"thinking, looking at what others have written on the blackboard and scribbling on the blackboard " +
	"themselves.\n\n" +

	"Access to the blackboard is a bit constrained, so only one at a time may scribble. Any number of " +
	"philosophers may read the blackboard simultaenously, but not whilst a scribbler is scribbling -- " +
	"space is so tight that someone scribbling on the board completely blocks the view.";

  public static void main (String argv[]) {

  	Ask.app (TITLE, DESCR);
  	Ask.show ();
  	Ask.blank ();

    final int n_philosophers = 10;
    final int blackboard_size = 10;

    final int[] blackboard = new int[blackboard_size];
    for (int i = 0; i < blackboard_size; i++) {             // initially, there is
      blackboard[i] = -1;                                   // garbage in the shared
    }                                                       // resource (blackboard)

    final Crew crewBlackboard = new Crew (blackboard);

    final Any2OneChannelInt display = Channel.any2oneInt ();
    final One2OneChannelInt displayInfo = Channel.one2oneInt ();

    final CrewPhilosopher[] phil = new CrewPhilosopher[n_philosophers];
    for (int i = 0; i < n_philosophers; i++) {
      phil[i] = new CrewPhilosopher (i, crewBlackboard, display.out (), displayInfo.out ());
    }

    final TimeKeeper timeKeeper = new TimeKeeper (display.out (), displayInfo.out ());

	final CSProcess crewDisplay;

	/*if (argv.length == 1) {
		if (argv[0].equals("vt100")) {
		    crewDisplay = new VT100Display (display.in (), displayInfo.in ());
		} else if (argv[0].equals ("awt")) {
			crewDisplay = new AWTDisplay (display.in (), displayInfo.in ());
		} else {
		    crewDisplay = new CrewDisplay (display.in (), displayInfo.in ());
		}
	} else {
	    crewDisplay = new CrewDisplay (display.in (), displayInfo.in ());
	}*/
	crewDisplay = new AWTDisplay (display.in (), displayInfo.in ());

    new Parallel (
      new CSProcess[] {
        new Parallel (phil),
        timeKeeper,
        crewDisplay
      }
    ).run ();

  }

}

