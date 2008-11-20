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
 * This example is loosely based on
 * A HREF="http://www.comp.lancs.ac.uk/computing/users/rgg/miscellaneous/Programming.html">
 * Jeffrey Hider's Geoapplet</A>
 * by <A HREF="mailto:jlhider@geocities.com">Jeffrey L. Hider</A>.
 * This JCSP demonstration is by <A HREF="mailto:P.H.Welch@kent.ac.uk">Peter Welch</A>.
 *
 * @author P.H. Welch
 */

public class FireworksMain extends ActiveApplet {

  public static final String TITLE = "Fireworks";
  public static final String DESCR =
  	"Demonstrates real-time user interaction using the Active AWT components and event channels. A particle " +
  	"simulation routine runs in the background, generating the next frame at timed intervals. The timer " +
  	"controlling these frames is a guard. Other guards are used for user events such as the mouse moving " +
  	"or keys being pressed. This allows thread-safe control of the simulation as it runs.\n" +
  	"\n" +
  	"When running, click the mouse on the window to add a spray of particles. Keys S, C, L, and P control " +
  	"the simulation.";

  public static final int minWidth = 300;
  public static final int maxWidth = 1024;

  public static final int maxHeight = 1024;
  public static final int minHeight = 100;

  public static final int minMaxParticles = 1000;
  public static final int maxMaxParticles = 50000;
  public static final int defaultMaxParticles = 15000;

  public static final int minStillCount = 10;
  public static final int maxStillCount = 1000;
  public static final int defaultStillCount = 100;

  public static final int minDragCount = 10;
  public static final int maxDragCount = 1000;
  public static final int defaultDragCount = 100;

  public static final int minSpeed = 1;
  public static final int maxSpeed = 100;
  public static final int defaultSpeed = 35;

  public static final int minScale = 0;
  public static final int maxScale = 10;
  public static final int defaultScale = 6;

  public static final int minMaxDeltaY = 1;
  public static final int maxMaxDeltaY = 10000;
  public static final int defaultMaxDeltaY = 10000;

  public static final int minLaunchDeltaX = 1;
  public static final int maxLaunchDeltaX = 10000;
  public static final int defaultLaunchDeltaX = 1200;

  public static final int minLaunchDeltaY = 1;
  public static final int maxLaunchDeltaY = 10000;
  public static final int defaultLaunchDeltaY = 1200;

  public static final int minAccY = 1;
  public static final int maxAccY = 1000;
  public static final int defaultAccY = 256;

  public void init () {
    final int maxParticles = getAppletInt ("maxParticles", minMaxParticles, maxMaxParticles, defaultMaxParticles);
    final int stillCount = getAppletInt ("stillCount", minStillCount, maxStillCount, defaultStillCount);
    final int dragCount = getAppletInt ("dragCount", minDragCount, maxDragCount, defaultDragCount);
    final int speed = getAppletInt ("speed", minSpeed, maxSpeed, defaultSpeed);
    final int accY = getAppletInt ("accY", minAccY, maxAccY, defaultAccY);
    final int maxDeltaY = getAppletInt ("maxDeltaY", minMaxDeltaY, maxMaxDeltaY, defaultMaxDeltaY);
    final int launchDeltaX = getAppletInt ("launchDeltaX", minLaunchDeltaX, maxLaunchDeltaX, defaultLaunchDeltaX);
    final int launchDeltaY = getAppletInt ("launchDeltaY", minLaunchDeltaY, maxLaunchDeltaY, defaultLaunchDeltaY);
    final int scale = getAppletInt ("scale", minScale, maxScale, defaultScale);
    setProcess (
      new FireNetwork (
        maxParticles, stillCount, dragCount, speed, accY,
        maxDeltaY, launchDeltaX, launchDeltaY, scale, this
      )
    );
  }

  public static void main (String[] args) {

    Ask.app (TITLE, DESCR);
    Ask.addPrompt ("width", minWidth, maxWidth, 640);
    Ask.addPrompt ("height", minHeight, maxHeight, 480);
    Ask.show ();
    final int width = Ask.readInt ("width");
    final int height = Ask.readInt ("height");
    Ask.blank ();

/*
    final int maxParticles = Ask.Int ("maxParticles = ", minMaxParticles, maxMaxParticles);
    final int stillCount = Ask.Int ("stillCount = ", minStillCount, maxStillCount);
    final int dragCount = Ask.Int ("dragCount = ", minDragCount, maxDragCount);
    final int speed = Ask.Int ("speed (frames per second) = ", minSpeed, maxSpeed);
    final int accY = Ask.Int ("accY = ", minAccY, maxAccY);
    final int maxDeltaY = Ask.Int ("maxDeltaY = ", minMaxDeltaY, maxMaxDeltaY);
    final int launchDeltaX = Ask.Int ("launchDeltaX = ", minLaunchDeltaX, maxLaunchDeltaX);
    final int launchDeltaY = Ask.Int ("launchDeltaY = ", minLaunchDeltaY, maxLaunchDeltaY);
    final int scale = Ask.Int ("scale = ", minScale, maxScale);
    System.out.println ();
*/

    final int maxParticles = defaultMaxParticles;
    final int stillCount = defaultStillCount;
    final int dragCount = defaultDragCount;
    final int speed = defaultSpeed;
    final int accY = defaultAccY;
    final int maxDeltaY = defaultMaxDeltaY;
    final int launchDeltaX = defaultLaunchDeltaX;
    final int launchDeltaY = defaultLaunchDeltaY;
    final int scale = defaultScale;
    System.out.println ("maxParticles = " + maxParticles);
    System.out.println ("stillCount = " + stillCount);
    System.out.println ("dragCount = " + dragCount);
    System.out.println ("speed = " + speed);
    System.out.println ("accY = " + accY);
    System.out.println ("maxDeltaY = " + maxDeltaY);
    System.out.println ("launchDeltaX = " + launchDeltaX);
    System.out.println ("launchDeltaY = " + launchDeltaY);
    System.out.println ("scale = " + scale);
    System.out.println ();

    final ActiveClosingFrame activeClosingframe = new ActiveClosingFrame (TITLE);
    final ActiveFrame activeFrame = activeClosingframe.getActiveFrame ();
    activeFrame.setSize (width, height);

    final FireNetwork fireNetwork =
      new FireNetwork (
        maxParticles, stillCount, dragCount, speed, accY,
        maxDeltaY, launchDeltaX, launchDeltaY, scale, activeFrame
      );

    activeFrame.pack ();
    activeFrame.setLocation ((maxWidth - width)/2, (maxHeight - height)/2);
    activeFrame.setVisible (true);
    activeFrame.toFront ();

    new Parallel (
      new CSProcess[] {
        activeClosingframe,
        fireNetwork
      }
    ).run ();

  }

}
