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

  private final int nPhilosophers;

  private final AltingChannelAccept philChannel;
  private final AltingChannelAccept forkChannel;
  private final AltingChannelInputInt securityChannel;
  private final AltingChannelInputInt clockChannel;

  private String space (int n) {
  	String s = "  ";
  	for (int i = 0; i < n; i++) {
  		s = s + "  ";
  	}
  	return s;
  }

  // constructors

  public TextDisplay (int nPhilosophers,
                      PhilChannel philChannel,
                      ForkChannel forkChannel,
                      AltingChannelInputInt securityChannel,
                      AltingChannelInputInt clockChannel) {
    this.nPhilosophers = nPhilosophers;
    this.philChannel = philChannel;
    this.forkChannel = forkChannel;
    this.securityChannel = securityChannel;
    this.clockChannel = clockChannel;
  }

  private interface inner extends CSProcess, PhilReport, ForkReport {};

  // public methods

  public void run () {

    new inner () {

      public void thinking (int id) {
        System.out.println (space(id) + "Philosopher " + id +
                            " is thinking ...");
      }

      public void hungry (int id) {
        System.out.println (space(id) + "Philosopher " + id +
                            " is hungry ...");
      }

      public void sitting (int id) {
        System.out.println (space(id) + "Philosopher " + id +
                            " is sitting ...");
      }

      public void eating (int id) {
        System.out.println (space(id) + "Philosopher " + id +
                            " is eating ...");
      }

      public void leaving (int id) {
        System.out.println (space(id) + "Philosopher " + id +
                            " is leaving ...");
      }

      public void forkUp (int id, int philId) {
        System.out.println (space(philId) + "Philosopher " + philId +
                            " has picked up fork " + id + " ...");
      }

      public void forkDown (int id, int philId) {
        System.out.println (space(philId) + "Philosopher " + philId +
                            " has put down fork " + id + " ...");
      }

      public void run () {

        final Alternative alt = new Alternative (
          new Guard[] {philChannel, forkChannel, securityChannel, clockChannel}
        );
        final int PHIL = 0;
        final int FORK = 1;
        final int SECURITY = 2;
        final int CLOCK = 3;

        System.out.println ("\nCollege starting with " + nPhilosophers +
                            " philosophers\n");

        while (true) {
          switch (alt.fairSelect ()) {
            case PHIL:
              philChannel.accept (this);
            break;
            case FORK:
              forkChannel.accept (this);
            break;
            case SECURITY:
              final int nSitting = securityChannel.read ();
              System.out.println ("Security: " + nSitting + " sat down ...");
            break;
            case CLOCK:
              final int tick = clockChannel.read ();
              System.out.println ("\n[TICK " + tick + "]\n");
            break;
          }
        }

      }

    }.run ();

  }

}

