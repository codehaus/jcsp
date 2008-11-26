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
class Phil implements CSProcess {

  //A Philosopher thinks for a while -- around 3 seconds -- and then goes to the
  //Canteen for food, consuming what he gets straight away.   This cycle continues
  //indefinitely.
  //
  //Except, that is, for Philosopher 0 ...  who refuses to think and just keeps
  //going to the Canteen.
  //
  //For this Canteen, when there's no chicken, the Philosphers are just kept
  //waiting in the service queue.  The greedy Philosopher no longer loses his
  //place through getting in before the food is cooked and doesn't starve.

  private final  int id;

  private final ChannelOutputInt service;
  private final ChannelInputInt deliver;

  public Phil (int id, ChannelOutputInt service, ChannelInputInt deliver) {
    this.id = id;
    this.service = service;
    this.deliver = deliver;
  }

  public void run () {
    final CSTimer tim = new CSTimer ();
    System.out.println ("      Phil " + id + "  : starting ... ");
    while (true) {
      // everyone, bar Philosopher 0, has a little think
      if (id > 0) {
        tim.after (tim.read () + 3000);   // thinking
      }
      // want chicken
      System.out.println ("      Phil " + id + "  : gotta eat ... ");
      service.write (0);
      deliver.read ();
      System.out.println ("      Phil " + id + "  : mmm ... that's good ... ");
    }
  }

}
