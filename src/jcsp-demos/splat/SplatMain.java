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
import org.jcsp.util.ints.*;
import org.jcsp.awt.*;
import java.awt.*;
import org.jcsp.demos.util.Ask;

/**
 * @author P.H. Welch
 */
public class SplatMain extends ActiveApplet {

  public static final String TITLE = "Splat";
  public static final String DESCR = "Demonstrates pixel access using the active AWT components.";

  public static final int minWidth = 335;
  public static final int minHeight = 100;
  public static final int minBurst = 20;

  public static final int maxWidth = 1024;
  public static final int maxHeight = 768;
  public static final int maxBurst = 1000;

  public static final int squareFactor = 30;

  public void init () {

    final Dimension size = getSize ();

    final int standbyBurst = 100;
    final int standbyAcross = 4;
    final int standbyDown = 1;

    final int nAcross = getAppletInt ("nAcross", 1, size.width/squareFactor, standbyAcross);
    final int nDown = getAppletInt ("nDown", 1, size.height/squareFactor, standbyDown);
    final int burst = getAppletInt ("burst", minBurst, maxBurst, standbyBurst);

    final boolean detach = getAppletBoolean ("detach", false);

    if (detach) {

      // final One2OneChannelInt stopStart = Channel.one2oneInt (new OverWriteOldestBufferInt (1));
      // setStopStartChannel (stopStart);
      final One2OneChannelInt destroy = Channel.one2oneInt (new OverWriteOldestBufferInt (1));
      final One2OneChannelInt destroyAck = Channel.one2oneInt ();
      setDestroyChannels (destroy.out (), destroyAck.in ());
      // setDestroyChannels (destroy, destroyAck, -1);    // cheat for Sun's Java Plug-in

      final Frame frame = new Frame ("Splat");
      frame.setSize (size);
      final Splat splat = new Splat (nAcross, nDown, burst, frame, destroy.in (), destroyAck.out ());
      frame.pack ();
      frame.setLocation ((maxWidth - size.width)/2, (maxHeight - size.height)/2);
      frame.setVisible (true);
      frame.toFront ();
      setProcess (splat);
    } else {
      setProcess (new Splat (nAcross, nDown, burst, this, null, null));
    }

  }

  public static void main (String[] args) {

    Ask.app (TITLE, DESCR);
    Ask.addPrompt ("width", minWidth, maxWidth, 640);
    Ask.addPrompt ("height", minHeight, maxHeight, 480);
    Ask.addPrompt ("squares across", 1, 50, 4);
    Ask.addPrompt ("squares down", 1, 50, 4);
    Ask.addPrompt ("burst", minBurst, maxBurst, 600);
	Ask.show ();
    final int width = Ask.readInt ("width");
    final int height = Ask.readInt ("height");
    final int nAcross = Ask.readInt ("squares across");
    final int nDown = Ask.readInt ("squares down");
    final int burst = Ask.readInt ("burst");
    Ask.blank ();

    final ActiveClosingFrame activeClosingframe = new ActiveClosingFrame ("Splat");
    final ActiveFrame activeFrame = activeClosingframe.getActiveFrame ();
    activeFrame.setSize (width, height);

    final Splat splat = new Splat (nAcross, nDown, burst, activeFrame, null, null);

    activeFrame.pack ();
    activeFrame.setLocation ((maxWidth - width)/2, (maxHeight - height)/2);
    activeFrame.setVisible (true);
    activeFrame.toFront ();

    new Parallel (
      new CSProcess[] {
        activeClosingframe,
        splat
      }
    ).run ();

  }

}
