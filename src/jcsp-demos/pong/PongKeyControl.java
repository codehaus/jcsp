
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
    //////////////////////////////////////////////////////////////////////


import org.jcsp.lang.*;
import java.awt.event.*;

public class PongKeyControl implements CSProcess {

  private final ChannelInput keyboard;
  private final ChannelOutputInt leftMove;
  private final ChannelOutputInt rightMove;

  public PongKeyControl (final ChannelInput keyboard,
                         final ChannelOutputInt leftMove,
                         final ChannelOutputInt rightMove) {
    this.keyboard = keyboard;
    this.leftMove = leftMove;
    this.rightMove = rightMove;
  }

  public void run () {
System.out.println ("PongKeyControl starting ...");
    while (true) {
      final KeyEvent keyEvent = (KeyEvent) keyboard.read ();
      if (keyEvent.getID () == KeyEvent.KEY_PRESSED) {
        switch (keyEvent.getKeyCode ()) {
          case KeyEvent.VK_A:
            leftMove.write (PongPaddle.UP);
          break;
          case KeyEvent.VK_Z:
            leftMove.write (PongPaddle.DOWN);
          break;
          case KeyEvent.VK_K:
            rightMove.write (PongPaddle.UP);
          break;
          case KeyEvent.VK_M:
            rightMove.write (PongPaddle.DOWN);
          break;
        }
      }
    }
  }

}
