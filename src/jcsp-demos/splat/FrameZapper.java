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
import java.awt.*;

/**
 * @author P.H. Welch
 */
class FrameZapper implements CSProcess {

  private final Frame frame;
  private final ChannelInputInt destroy;
  private final ChannelOutputInt destroyAck;

  public FrameZapper (final Frame frame,
                      final ChannelInputInt destroy,
                      final ChannelOutputInt destroyAck) {
    this.frame = frame;
    this.destroy = destroy;
    this.destroyAck = destroyAck;
  }

  public void run () {

    destroy.read ();
    System.out.println ("FrameZapper: destroy.read () ... zapping frame ...");

    // Test cheat for continued operation of detached frame (Sun's Java Plug-in)
    // Timer tim = new Timer ();
    // for (int i = 45; i >= 0; i--) {
    //   tim.after (tim.read () + 1000);
    //   System.out.println ("FrameZapper: counting ... " + i);
    //  }

    frame.setVisible (false);
    frame.dispose ();
    System.out.println ("FrameZapper: frame zapped ... acknowledging destroy ...");
    destroyAck.write (0);

  }

}
