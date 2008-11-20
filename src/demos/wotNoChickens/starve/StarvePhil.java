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

/**
 * @author P.H. Welch
 */
class StarvePhil implements CSProcess {

  //A Philosopher thinks for a while -- around 3 seconds -- and then goes to the
  //Canteen for food, consuming what he gets straight away.   This cycle continues
  //indefinitely.
  //
  //Except, that is, for Philosopher 0 ...  who refuses to think and just keeps
  //going to the Canteen.
  //
  //For this Canteen, when there's no chicken, the Philosphers wait for a bit and try again,
  //possibly losing their place in the queue.

  private final String id;
  private final StarveCanteen.Service service;
  private final int thinkTime;
  private final int eatTime;
  private final int waitTime;
  private final boolean greedy;

  public StarvePhil (String id, StarveCanteen.Service service,
                   int thinkTime, int eatTime, int waitTime, boolean greedy) {
    this.id = id;
    this.service = service;
    this.thinkTime = thinkTime;
    this.eatTime = eatTime;
    this.waitTime = waitTime;
    this.greedy = greedy;
  }

  public void run () {
    final CSTimer tim = new CSTimer ();
    int nEaten = 0;
    while (true) {
      // everyone, unless greedy, has a little think
      if (! greedy) {
        System.out.println ("   Phil " + id + " : thinking ... ");
        tim.sleep (thinkTime);   // thinking
      }
      // want chicken
      System.out.println ("   Phil " + id + " : gotta eat ... ");
      while (service.takeChicken (id) == 0) {
        System.out.println ("   Phil " + id + " : wot, no chickens ? it's an outrage !");
        tim.sleep (waitTime); // try again later
      }
      nEaten++;
      System.out.println ("   Phil " + id + " : mmm ... that's good [" + nEaten + " so far]");
      tim.sleep (eatTime);       // eating
    }
  }

}
