
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

import java.util.*;
import java.awt.*;

public class PongPaddle implements CSProcess {

  public final static int WIDTH = 10;
  public final static int HIT = 0;
  public final static int MISS = 1;
  
  public final static int UP = 0;
  public final static int DOWN = 1;
  
  private final static int SECONDS = 1000;        // JCSP Timer units are milliseconds
  
  private final static int[] FACTOR_HEIGHT = {3, 5, 7, 9};
  private final static int[] HEIGHT_INTERVAL = {8*SECONDS, 5*SECONDS, 2*SECONDS, SECONDS/2};
  
  private final static int DELTA_HEIGHT = 2;
  private final static int MIN_HEIGHT = 20;

  private final static int WOBBLE_INTERVAL = 10;  // milliseconds
  private final static int WOBBLE_COUNT = 2000;   // i.e. 20 seconds
  private final static int WOBBLE_CHANCE = 10;    // percent
  
  private final static int UNIT_DELTA_Y = 4;
  private final static int MAX_DELTA_FACTOR = 3;
  private final static int MIN_DELTA_FACTOR = -MAX_DELTA_FACTOR;
  private final static int[] DELTA_WEIGHT = {-6, -3, -1, 0, 1, 3, 6};

  private final boolean leftPaddle;
  private final int speed;
  private final AltingChannelInputInt move;
  private final AltingChannelInput fromBalls;
  private final ChannelOutputInt toBalls;
  private final ChannelOutputInt toScorer;
  private final AltingChannelInput fromControl;
  private final DisplayList displayList;

  public PongPaddle (boolean leftPaddle, int speed, AltingChannelInputInt move,
                     AltingChannelInput fromBalls, ChannelOutputInt toBalls,
                     ChannelOutputInt toScorer, AltingChannelInput fromControl,
                     DisplayList displayList) {
    this.leftPaddle = leftPaddle;
    this.speed = speed;
    this.move = move;
    this.fromBalls = fromBalls;
    this.toBalls = toBalls;
    this.toScorer = toScorer;
    this.fromControl = fromControl;
    this.displayList = displayList;
  }

  private final static class Graphic implements GraphicsCommand.Graphic {
    public Color colour, background;
    public int x, y, width, height, backgroundHeight;
    // invariant : (0 <= y < maxY) where (maxY == (backgroundHeight - height))
    public void doGraphic (java.awt.Graphics g, java.awt.Component c) {
      g.setColor (background);
      g.fillRect (x, 0, width, backgroundHeight);
      g.setColor (colour);
      g.fillRect (x, y, width, height);
    }
  }

  private Random random;

  private final int range (int n) {      // returns a random integer in the range [0, n - 1]
    int i = random.nextInt ();           // needed since random.nextInt (<int>) not in JDK1.1 :-(
    if (i < 0) {
      if (i == Integer.MIN_VALUE) {      // guard against minint !
        i = 42;
      } else {
        i = -i;
      }
    }
    return i % n;
  }

  private boolean wobbleFlag = false;
  private int wobbleCount = WOBBLE_COUNT;

  private final int computeInterval (final int height,
                                     final int[] threshold) {
    if (wobbleFlag) {
      wobbleCount--;
      if (wobbleCount == 0) {
        wobbleFlag = false;
        wobbleCount = WOBBLE_COUNT;
      }
      return WOBBLE_INTERVAL;
    }
    for (int i = threshold.length - 1; i >= 0; i--) {
      if (height <= threshold[i]) return HEIGHT_INTERVAL[i];
    }
    return HEIGHT_INTERVAL[0];  // won't get here!
  }

  public void run () {

    System.out.println ("Paddle " + leftPaddle + " running ...");
    
    final CSTimer moveTimer = new CSTimer ();
    long moveInterval = (long) (((float) SECONDS)/((float) speed) + 0.5);
    long moveTimeout = 0;
    
    final CSTimer heightTimer = new CSTimer ();

    final Alternative alt = new Alternative (
      new Guard[] {move, fromBalls, fromControl, moveTimer, heightTimer}
    );
    
    final boolean[] preCondition = {true, true, true, false, true};
    
    final int MOVE = 0;
    final int BALLS = 1;
    final int CONTROL = 2;
    final int MOVE_TIMEOUT = 3;
    final int HEIGHT_TIMEOUT = 4;

    final Dimension graphicsDim = (Dimension) fromControl.read ();
    System.out.println ("Paddle " + leftPaddle + ": " + graphicsDim);

    final int displaySlot = displayList.extend (GraphicsCommand.NULL);
    System.out.println ("Paddle " + leftPaddle + ": displaySlot = " + displaySlot);

    fromControl.read ();    // let control continue

    final int[] threshold = new int[FACTOR_HEIGHT.length];
    for (int i = 0; i < FACTOR_HEIGHT.length; i++) {
      threshold[i] = graphicsDim.height/FACTOR_HEIGHT[i];
    }
    final int MAX_HEIGHT = threshold[0];

    Graphic oldGraphic = new Graphic ();
    Graphic newGraphic = new Graphic ();

    GraphicsCommand oldCommand = new GraphicsCommand.General (oldGraphic);
    GraphicsCommand newCommand = new GraphicsCommand.General (newGraphic);

System.out.println ("Paddle " + leftPaddle + ": priority = " + PriParallel.getPriority ());
    PriParallel.setPriority (Thread.MAX_PRIORITY);
System.out.println ("Paddle " + leftPaddle + ": priority = " + PriParallel.getPriority ());

    random = new Random (moveTimer.read () + moveTimer.hashCode ());

    while (true) {

      toScorer.write (0);

      // initialise data for new paddle ...

      newGraphic.colour = Color.red;
      newGraphic.width = WIDTH;
      newGraphic.height = MAX_HEIGHT;
      newGraphic.background = Color.white;
      newGraphic.backgroundHeight = graphicsDim.height;
      newGraphic.x = leftPaddle ? 0 : graphicsDim.width - WIDTH;
      newGraphic.y = (graphicsDim.height - newGraphic.height)/2;

      oldGraphic.colour = newGraphic.colour;
      oldGraphic.width = newGraphic.width;
      oldGraphic.height = newGraphic.height;
      oldGraphic.background = newGraphic.background;
      oldGraphic.backgroundHeight = newGraphic.backgroundHeight;
      oldGraphic.x = newGraphic.x;
      
      displayList.change (newCommand, displaySlot);
      
      Graphic tmpGraphic = oldGraphic;
      oldGraphic = newGraphic;
      newGraphic = tmpGraphic;

      GraphicsCommand tmpCommand = oldCommand;
      oldCommand = newCommand;
      newCommand = tmpCommand;

      int maxY = graphicsDim.height - newGraphic.height;
      int deltaHeight = -DELTA_HEIGHT;

System.out.println ("Paddle " + leftPaddle + ": initialX,Y = " + newGraphic.x + ", " + newGraphic.y);
System.out.println ("Paddle " + leftPaddle + ": initialW,H = " + newGraphic.width + ", " + newGraphic.height);

      int deltaFactor = 0;
      int deltaY = deltaFactor*UNIT_DELTA_Y;

      preCondition[MOVE_TIMEOUT] = false;
      
      int heightInterval = computeInterval (oldGraphic.height, threshold);
System.out.println ("Paddle " + leftPaddle + ": heightInterval = " + heightInterval);
      
      long heightTimeout = heightTimer.read() + heightInterval;
      heightTimer.setAlarm (heightTimeout);
      
      boolean playing = true;

      while (playing) {

        boolean paddleMove = false;
        boolean paddleHeight = false;
        boolean paddleColour = false;

        switch (alt.fairSelect (preCondition)) {
        
          case MOVE:
            switch (move.read ()) {
              case UP:
                if (deltaFactor == -1) {
                  preCondition[MOVE_TIMEOUT] = false;
                  deltaFactor = 0;
                } else if (deltaFactor == 0) {
                  moveTimeout = moveTimer.read ();
                  preCondition[MOVE_TIMEOUT] = true;
                  deltaFactor = 1;
                  deltaY = DELTA_WEIGHT[deltaFactor + MAX_DELTA_FACTOR]*UNIT_DELTA_Y;
                  paddleMove = true;
                } else if (deltaFactor < MAX_DELTA_FACTOR) {
                  deltaFactor++;
                  deltaY = DELTA_WEIGHT[deltaFactor + MAX_DELTA_FACTOR]*UNIT_DELTA_Y;
                  paddleMove = true;
                }
              break;
              case DOWN:
                if (deltaFactor == 1) {
                  preCondition[MOVE_TIMEOUT] = false;
                  deltaFactor = 0;
                } else if (deltaFactor == 0) {
                  moveTimeout = moveTimer.read () + moveInterval;
                  moveTimer.setAlarm (moveTimeout);
                  preCondition[MOVE_TIMEOUT] = true;
                  deltaFactor = -1;
                  deltaY = DELTA_WEIGHT[deltaFactor + MAX_DELTA_FACTOR]*UNIT_DELTA_Y;
                  paddleMove = true;
                } else if (deltaFactor > MIN_DELTA_FACTOR) {
                  deltaFactor--;
                  deltaY = DELTA_WEIGHT[deltaFactor + MAX_DELTA_FACTOR]*UNIT_DELTA_Y;
                  paddleMove = true;
                }
              break;
            }
          break;
          
          case BALLS:
            final PongBall.Info ball = (PongBall.Info) fromBalls.read ();
            if ((oldGraphic.y <= ball.y) && (ball.y < oldGraphic.y + oldGraphic.height)) {
              toBalls.write (HIT);
              toScorer.write (ball.zing);
              newGraphic.colour = (oldGraphic.colour == Color.red) ? Color.blue : Color.red;
              newGraphic.height = oldGraphic.height;
              newGraphic.y = oldGraphic.y;
              paddleColour = true;
            } else {
              toBalls.write (MISS);
              toScorer.write (-(2*ball.zing)/3);
            }
          break;
          
          case CONTROL:
            fromControl.read ();
            playing = false;
          break;
          
          case MOVE_TIMEOUT:
            paddleMove = true;
          break;
          
          case HEIGHT_TIMEOUT:
            newGraphic.colour = oldGraphic.colour;
            newGraphic.height = oldGraphic.height + deltaHeight;
            if (newGraphic.height <= MIN_HEIGHT) {
              newGraphic.height = MIN_HEIGHT;
              deltaHeight = -deltaHeight;
            } else if (newGraphic.height >= MAX_HEIGHT) {
              newGraphic.height = MAX_HEIGHT;
              deltaHeight = -deltaHeight;
            }
            maxY = graphicsDim.height - newGraphic.height;
            newGraphic.y = oldGraphic.y + ((oldGraphic.height - newGraphic.height)/2);
            if (newGraphic.y < 0) {
              newGraphic.y = 0;
            } else if (newGraphic.y >= maxY) {
              newGraphic.y = maxY;
            }
            if (!wobbleFlag) wobbleFlag = (range (100) < WOBBLE_CHANCE);
            heightInterval = computeInterval (newGraphic.height, threshold);
// System.out.println ("Paddle " + leftPaddle + " height = " + newGraphic.height +
//                     " : heightInterval = " + heightInterval +
//                     " : wobbleFlag = " + wobbleFlag);
            heightTimeout += heightInterval;
            heightTimer.setAlarm (heightTimeout);
            paddleHeight = true;
          break;
        }

        if (paddleMove) {
          newGraphic.colour = oldGraphic.colour;
          newGraphic.height = oldGraphic.height;
          newGraphic.y = oldGraphic.y - deltaY;
          if ((newGraphic.y < 0) && (deltaY > 0)) {
            newGraphic.y = 0;
            preCondition[MOVE_TIMEOUT] = false;
            deltaFactor = 0;
          } else if ((newGraphic.y > maxY) && (deltaY < 0)) {
            newGraphic.y = maxY;
            preCondition[MOVE_TIMEOUT] = false;
            deltaFactor = 0;
          } else {
            moveTimeout += moveInterval;
            moveTimer.setAlarm (moveTimeout);
          }
        }
        
        if (paddleMove || paddleColour || paddleHeight) {
          displayList.change (newCommand, displaySlot);
          tmpGraphic = oldGraphic;
          oldGraphic = newGraphic;
          newGraphic = tmpGraphic;
          tmpCommand = oldCommand;
          oldCommand = newCommand;
          newCommand = tmpCommand;
        }

      }

      System.out.println ("Paddle " + leftPaddle + ": dead");

      displayList.change (GraphicsCommand.NULL, displaySlot);

    }

  }

}
