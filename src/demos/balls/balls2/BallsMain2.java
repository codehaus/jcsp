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
import org.jcsp.awt.*;
import org.jcsp.demos.util.Ask;

/**
 * @author P.H. Welch
 */
public class BallsMain2 extends ActiveApplet {

  public static final int minWidth = 300;
  public static final int maxWidth = 1024;

  public static final int maxHeight = 768;
  public static final int minHeight = 100;

  public static final int minBalls = 1;
  public static final int maxBalls = 100;
  public static final int defaultBalls = 10;

  public static final int minSpeed = 1;
  public static final int maxSpeed = 100;
  public static final int defaultSpeed = 20;

  public static final int minLife = 1;
  public static final int maxLife = 100;
  public static final int defaultLife = 20;

  public void init () {
    final int nBalls = getAppletInt ("balls", minBalls, maxBalls, defaultBalls);
    final int speed = getAppletInt ("speed", minSpeed, maxSpeed, defaultSpeed);
    final int life = getAppletInt ("life", minLife, maxLife, defaultLife);
    setProcess (new BallsNetwork2 (nBalls, speed, life, this));
  }

  public static final String TITLE = "Bouncing Balls [different update]";
  public static final String DESCR =
  	"Shows the use of a DisplayList and the ActiveCanvas when animating a number of objects. Each ball " +
  	"process is given a reference to an information object to be updated. When all balls have updated " +
  	"these objects then a single request is issued by the control process to update the actual canvas. This " +
  	"implementation avoids the need to give the whole DisplayList object to each ball process. This gives " +
  	"a more secure system as the ball processes are then unable to accidentally (or maliciously) alter " +
  	"a display slot not allocated to them.";

  public static void main (String[] args) {

  	Ask.app (TITLE, DESCR);
  	Ask.addPrompt ("width", minWidth, maxWidth, 640);
  	Ask.addPrompt ("height", minHeight, maxHeight, 480);
  	Ask.addPrompt ("balls", minBalls, maxBalls, defaultBalls);
  	Ask.addPrompt ("speed (movements/second)", minSpeed, maxSpeed, defaultSpeed);
  	Ask.addPrompt ("life (seconds/ball)", minLife, maxLife, defaultLife);
  	Ask.show ();
  	final int width = Ask.readInt ("width");
  	final int height = Ask.readInt ("height");
  	final int nBalls = Ask.readInt ("balls");
  	final int speed = Ask.readInt ("speed (movements/second)");
  	final int life = Ask.readInt ("life (seconds/ball)");
  	Ask.blank ();

    final ActiveClosingFrame activeClosingframe = new ActiveClosingFrame (TITLE);
    final ActiveFrame activeFrame = activeClosingframe.getActiveFrame ();
    activeFrame.setSize (width, height);

    final BallsNetwork2 ballsNetwork =
      new BallsNetwork2 (nBalls, speed, life, activeFrame);

    activeFrame.pack ();
    activeFrame.setLocation ((maxWidth - width)/2, (maxHeight - height)/2);
    activeFrame.setVisible (true);
    activeFrame.toFront ();

    new Parallel (
      new CSProcess[] {
        activeClosingframe,
        ballsNetwork
      }
    ).run ();

  }

}
