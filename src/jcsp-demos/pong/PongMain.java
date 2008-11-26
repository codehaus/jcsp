
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
import org.jcsp.awt.*;
import java.awt.*;

import org.jcsp.demos.util.Ask;

public class PongMain extends ActiveApplet {

  public static final int minWidth = 300;
  public static final int maxWidth = 1024;

  public static final int maxHeight = 768;
  public static final int minHeight = 100;

  public static final int minBalls = 1;
  public static final int maxBalls = 100;
  public static final int defaultBalls = 100;

  public static final int minSpeed = 15;
  public static final int maxSpeed = 100;
  public static final int defaultSpeed = 35;

  public static final int minPaddleSpeed = 15;
  public static final int maxPaddleSpeed = 100;
  public static final int defaultPaddleSpeed = 35;

  public static final int minLife = 5;
  public static final int maxLife = 100;
  public static final int defaultLife = 50;

  public void init () {
    final int nBalls = getAppletInt ("balls", minBalls, maxBalls, defaultBalls);
    final int speed = getAppletInt ("speed", minSpeed, maxSpeed, defaultSpeed);
    final int paddleSpeed = getAppletInt ("paddleSpeed", minPaddleSpeed,
                                          maxPaddleSpeed, defaultPaddleSpeed);
    final int life = getAppletInt ("life", minLife, maxLife, defaultLife);
    setProcess (new PongNetwork (nBalls, speed, paddleSpeed, life, this));
  }

  public static void main (String[] args) {
  
    System.out.println ("\nPong starting ...\n");

    final int width = Ask.Int ("width = ", minWidth, maxWidth);
    final int height = Ask.Int ("height = ", minHeight, maxHeight);
    System.out.println ();

    final int nBalls = Ask.Int ("balls = ", minBalls, maxBalls);
    final int speed = Ask.Int ("speed (ball movements per second) = ", minSpeed, maxSpeed);
    final int paddleSpeed = Ask.Int ("paddleSpeed (paddle movements per second) = ",
                                     minPaddleSpeed, maxPaddleSpeed);
    final int life = Ask.Int ("life (seconds per ball) = ", minLife, maxLife);
    System.out.println ();

    final ActiveClosingFrame activeClosingframe = new ActiveClosingFrame ("Multi Pong");
    final ActiveFrame activeFrame = activeClosingframe.getActiveFrame ();
    activeFrame.setSize (width, height);

    final PongNetwork pongNetwork =
      new PongNetwork (nBalls, speed, paddleSpeed, life, activeFrame);

    activeFrame.pack ();
    activeFrame.setLocation ((maxWidth - width)/2, (maxHeight - height)/2);
    activeFrame.setVisible (true);
    activeFrame.toFront ();

    new Parallel (
      new CSProcess[] {
        activeClosingframe,
        pongNetwork
      }
    ).run ();

  }

}
