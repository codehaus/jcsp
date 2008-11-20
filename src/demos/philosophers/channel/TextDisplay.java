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
class TextDisplay implements CSProcess {

  // attributes

  private final ChannelInput in;
  private final int nPhilosophers;

  // constructors

  public TextDisplay (int nPhilosophers, ChannelInput in) {
    this.nPhilosophers = nPhilosophers;
    this.in = in;
  }

  private String space (int n) {
    String s = "  ";
    for (int i = 0; i < n; i++) {
      s = s + "  ";
    }
    return s;
  }

  // public methods

  public void run () {
    ISReport report;
    int id, state, philId;
    System.out.println ("\nCollege starting with " + nPhilosophers +
                        " philosophers\n");
    while (true) {
      report = (ISReport) in.read ();
      id = report.getId ();
      state = report.getState ();
      if (report instanceof PhilReport) {
        switch (state) {
          case PhilReport.THINKING:
            System.out.println (space(id) + "Philosopher " + id +
                                " is thinking ...");
          break;
          case PhilReport.HUNGRY:
            System.out.println (space(id) + "Philosopher " + id +
                                " is hungry ...");
          break;
          case PhilReport.SITTING:
            System.out.println (space(id) + "Philosopher " + id +
                                " is sitting ...");
          break;
          case PhilReport.EATING:
            System.out.println (space(id) + "Philosopher " + id +
                                " is eating ...");
          break;
          case PhilReport.LEAVING:
            System.out.println (space(id) + "Philosopher " + id +
                                " has finished eating  ...");
          break;
        }
      } else if (report instanceof ForkReport) {
        philId = ((ForkReport) report).getPhilId ();
        switch (state) {
          case ForkReport.DOWN:
            System.out.println (space(philId) + "Philosopher " + philId +
                                " has put down fork " + id + " ...");
          break;
          case ForkReport.UP:
            System.out.println (space(philId) + "Philosopher " + philId +
                                " has picked up fork " + id + " ...");
          break;
        }
      } else if (report instanceof SecurityReport) {
        System.out.println ("Security: " + id + " sat down ...");
      } else if (report instanceof ClockReport) {
        System.out.println ("\n[TICK " + id + "]\n");
      } else {
        System.out.println ("\nBad report !!!");
      }
    }
  }

}

