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
public class Polling implements CSProcess {

  private final AltingChannelInput in0;
  private final AltingChannelInput in1;
  private final AltingChannelInput in2;
  private final AltingChannelInput in3;
  private final AltingChannelInput in4;
  private final ChannelOutput out;

  public Polling (final AltingChannelInput in0, final AltingChannelInput in1,
                  final AltingChannelInput in2, final AltingChannelInput in3,
                  final AltingChannelInput in4, final ChannelOutput out) {
    this.in0 = in0;
    this.in1 = in1;
    this.in2 = in2;
    this.in3 = in3;
    this.in4 = in4;
    this.out = out;
  }

  public void run() {

    final Skip skip = new Skip ();
    final Guard[] guards = {in0, in1, in2, in3, in4, skip};
    final Alternative alt = new Alternative (guards);

    while (true) {
      switch (alt.priSelect ()) {
        case 0:
          // ...  process data pending on channel in0
          out.write (in0.read ());
        break;
        case 1:
          // ...  process data pending on channel in1
          out.write (in1.read ());
        break;
        case 2:
          // ...  process data pending on channel in2
          out.write (in2.read ());
        break;
        case 3:
          // ...  process data pending on channel in2
          out.write (in3.read ());
        break;
        case 4:
          // ...  process data pending on channel in2
          out.write (in4.read ());
        break;
        case 5:
          // ...  nothing available for the above ...
          // ...  so get on with something else for a while ...
          // ...  then loop around and poll again ...
          try {Thread.sleep (400);} catch (InterruptedException e) {}
          out.write ("...  so getting on with something else for a while ...");
        break;
      }
    }
  }
}
