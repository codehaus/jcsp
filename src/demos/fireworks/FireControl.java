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
import java.util.*;
import java.awt.*;
import java.awt.event.*;

/**
 * @author P.H. Welch
 */
public class FireControl implements CSProcess {

  private final AltingChannelInput fromMouse;        // mouse events
  private final AltingChannelInput fromMouseMotion;  // mouse & mouse motion events
  private final AltingChannelInput fromKeyboard;     // keyboard events
  private final AltingChannelInput fromCanvas;		 // canvas resize events

  private final DisplayList displayList;             // only one slot used

  private final ChannelOutput toGraphics;            // only to find out the canvas dimensions
  private final ChannelInput fromGraphics;

  private final int maxParticles;                    // may need to be more flexible

  private int stillCount;                            // number of particles created each cycle
  private int dragCount;                             // number of particles created each drag

  private final int speed;                           // frames/second for the display

  private int accY;                                  // gravity acting on each particle (scaled)
  private int maxDeltaY;                             // terminal downward velocity for particles (scaled)

  private int launchDeltaX;                          // maximum particle horizontal launch velocity (scaled)
  private int launchDeltaY;                          // maximum particle vertical launch velocity (scaled)

  private int launchDeltaX2;                         // 2 * launchDeltaX
  private int launchDeltaY2;                         // 2 * launchDeltaY
  private final int minBias = 4;
  private final int maxBias = 16;
  private int bias = 12;
  private int launchDeltaYbias;                      // (bias * launchDeltaY)>>2

  private int scale;                                 // log-2 mapping to actual canvas dimension

  private final Random random = new Random ();

  private final float hueMin = 0.0f;
  private final float hueMax = 1.0f;
  private final int maxColours = 768;
  private final Color[] colour;
  private int colourIndex = 0;
  
  private Dimension graphicsDim;					 // current drawing size

  public FireControl (AltingChannelInput fromMouse,
                      AltingChannelInput fromMouseMotion,
                      AltingChannelInput fromKeyboard,
                      AltingChannelInput fromCanvas,
                      DisplayList displayList,
                      ChannelOutput toGraphics, ChannelInput fromGraphics,
                      int maxParticles, int stillCount, int dragCount, int speed,
                      int accY, int maxDeltaY, int launchDeltaX, int launchDeltaY,
                      int scale) {
    this.fromMouse = fromMouse;
    this.fromMouseMotion = fromMouseMotion;
    this.fromKeyboard = fromKeyboard;
    this.fromCanvas = fromCanvas;
    this.displayList = displayList;
    this.toGraphics = toGraphics;
    this.fromGraphics = fromGraphics;
    this.maxParticles = maxParticles;
    this.stillCount = stillCount;
    this.dragCount = dragCount;
    this.speed = speed;
    this.accY = accY;
    this.maxDeltaY = maxDeltaY;
    this.launchDeltaX = launchDeltaX;
    this.launchDeltaY = launchDeltaY;
    this.launchDeltaX2 = launchDeltaX*2;
    this.launchDeltaY2 = launchDeltaY*2;
    this.launchDeltaYbias = (launchDeltaY*bias)>>2;
    this.scale = scale;
    this.colour = new Color[maxColours];
    setColours ();
  }

  private void setColours () {
    // final float hueIncrement = (hueMax - hueMin)/((float) maxColours);
    // final float saturation = 0.50f;
    // final float brightness = 1.00f;
    // for (int i = 0; i < maxColours; i++) {
    //   // this.colour[i] = new Color (random.nextInt ());
    //   this.colour[i] = new Color (
    //     hueMin + (((float) i)*hueIncrement), saturation, brightness
    //   );
// System.out.println ("setColours : " + i + " : " + this.colour[i]);
    // }
    int up = 0;
    int down = 255;
    for (int i = 0; i < 256; i++) {
      colour[i] = new Color (0, down, up);
      up++;
      down--;
    }
    up = 0;
    down = 255;
    for (int i = 256; i < 512; i++) {
      colour[i] = new Color (up, 0, down);
      up++;
      down--;
    }
    up = 0;
    down = 255;
    for (int i = 512; i < 768; i++) {
      colour[i] = new Color (down, up, 0);
      up++;
      down--;
    }
    for (int i = 0; i < 768; i += 16) {
      colour[i] = Color.white;
      up++;
      down--;
    }
  }

  private final class Graphic implements GraphicsCommand.Graphic {
    public int scale;
    public Particle[] particle;
    public int nParticles;
    public void doGraphic (java.awt.Graphics g, java.awt.Component c) {
      Particle.setScale (scale);
      g.clearRect (0, 0, graphicsDim.width, graphicsDim.height);
      for (int i = 0; i < nParticles; i++) {
        particle[i].paint (g);
      }
    }
  }

  /**
   * returns a random integer in the range [0, n - 1]
   */
  private final int range (int n) {
    int i = random.nextInt ();
    if (i < 0) {
      i = (i == Integer.MIN_VALUE) ? 42 : -i;      // guard against minint !
    }
    return i % n;
  }

  private void createParticles (int n, Point mouseLocation, Graphic graphic) {
    final int maxColours4 = maxColours<<4;
    final int x = (mouseLocation.x)<<scale;
    final int y = (mouseLocation.y)<<scale;
    int j = graphic.nParticles;
    if ((n + j) > maxParticles) n = maxParticles - j;
    for (int i = 0; i < n; i++) {
      graphic.particle[j].setAttributes (
        x, y,
        range (launchDeltaX2) - launchDeltaX,
        range (launchDeltaY2) - launchDeltaYbias,
        colour[colourIndex>>4]
      );
      colourIndex++;
      if (colourIndex == maxColours4) colourIndex = 0;
      j++;
    }
    graphic.nParticles = j;
  }

  private void createParticles (int n, Point oldMouseLocation, Point newMouseLocation,
                                Graphic graphic) {
    final int maxColours4 = maxColours<<4;
    final int oldX = (oldMouseLocation.x)<<scale;
    final int oldY = (oldMouseLocation.y)<<scale;
    final int newX = (newMouseLocation.x)<<scale;
    final int newY = (newMouseLocation.y)<<scale;
    final int diffX = newX - oldX;
    final int diffY = newY - oldY;
    int j = graphic.nParticles;
    if ((n + j) > maxParticles) n = maxParticles - j;
    for (int i = 0; i < n; i++) {
      graphic.particle[j].setAttributes (
        oldX + ((i*diffX)/n), oldY + ((i*diffY)/n),
        range (launchDeltaX2) - launchDeltaX,
        range (launchDeltaY2) - launchDeltaYbias,
        colour[colourIndex>>4]
      );
      colourIndex++;
      if (colourIndex == maxColours4) colourIndex = 0;
      j++;
    }
    graphic.nParticles = j;
  }

  public void run () {

    toGraphics.write (GraphicsProtocol.GET_DIMENSION);
    graphicsDim = (Dimension) fromGraphics.read ();
    System.out.println ("FireControl : graphics dimension = " + graphicsDim);

    Particle.setConstants (accY, maxDeltaY, scale, graphicsDim);

    Graphic oldGraphic = new Graphic ();
    oldGraphic.scale = scale;
    oldGraphic.particle = new Particle[maxParticles];
    oldGraphic.nParticles = 0;

    Graphic newGraphic = new Graphic ();
    newGraphic.scale = scale;
    newGraphic.particle = new Particle[maxParticles];
    newGraphic.nParticles = 0;

    for (int i = 0; i < maxParticles; i++) {
      oldGraphic.particle[i] = new Particle ();
      newGraphic.particle[i] = new Particle ();
    }

    GraphicsCommand oldCommand = new GraphicsCommand.General (oldGraphic);
    GraphicsCommand newCommand = new GraphicsCommand.General (newGraphic);

    final CSTimer tim = new CSTimer ();
    final long second = 1000;               // JCSP Timer units are milliseconds
    long interval = (long) (((float) second)/((float) speed) + 0.5);
    System.out.println ("FireControl : interval = " + interval);

    final Guard[] guard = {tim, fromMouse, fromMouseMotion, fromKeyboard, fromCanvas };
    final int TIMEOUT = 0;
    final int MOUSE = 1;
    final int MOUSE_MOTION = 2;
    final int KEYBOARD = 3;
    final int RESIZE = 4;
    final Alternative alt = new Alternative (guard);
    final boolean[] preCondition = {false, true, true, true, true};

    Point mouseLocation = new Point (0, 0), oldMouseLocation = null;
    boolean buttonPressed = false;
    boolean continuous = false;

    tim.setAlarm (tim.read () + interval);

    while (true) {

      toGraphics.write (GraphicsProtocol.REQUEST_FOCUS);
      fromGraphics.read ();

      preCondition[TIMEOUT] = buttonPressed || continuous ||
                              (oldGraphic.nParticles > 0) ||
                              (newGraphic.nParticles > 0);

      switch (alt.priSelect (preCondition)) {

        case TIMEOUT: {

          if ((buttonPressed || continuous) && (newGraphic.nParticles == 0)) {
            createParticles (stillCount, mouseLocation, newGraphic);
          }

          int j = newGraphic.nParticles;
          for (int i = 0; i < oldGraphic.nParticles; i++) {
            if (j == maxParticles) break;
            if (oldGraphic.particle[i].move (newGraphic.particle[j])) j++;
          }
          newGraphic.nParticles = j;

          newGraphic.scale = scale;

// System.out.println ("TIMEOUT : " + mouseLocation + " : " + newGraphic.nParticles);

          displayList.set (newCommand);

          final Graphic tmpA = oldGraphic;
          oldGraphic = newGraphic;
          newGraphic = tmpA;

          final GraphicsCommand tmpB = oldCommand;
          oldCommand = newCommand;
          newCommand = tmpB;

          newGraphic.nParticles = 0;
          tim.setAlarm (tim.read () + interval);

          break;

        }

        case MOUSE: {

          MouseEvent mouseEvent = (MouseEvent) fromMouse.read ();
          switch (mouseEvent.getID ()) {
            case MouseEvent.MOUSE_PRESSED: {
              if ((mouseEvent.getModifiers () & InputEvent.BUTTON2_MASK) != 0) {
System.out.println ("MIDDLE MOUSE_PRESSED (freeze) : " + 
                    newGraphic.nParticles + " : " + oldGraphic.nParticles);
                mouseEvent = (MouseEvent) fromMouse.read ();
                while (mouseEvent.getID () != MouseEvent.MOUSE_PRESSED) {
                  mouseEvent = (MouseEvent) fromMouse.read ();
                }
System.out.println ("SOME MOUSE_PRESSED (running) : ");
              } else {
                mouseLocation = mouseEvent.getPoint ();
// System.out.println ("MOUSE_PRESSED : " + mouseLocation);
                // createParticles (stillCount, mouseLocation, newGraphic);
                buttonPressed = true;
                if ((mouseEvent.getModifiers () & InputEvent.BUTTON3_MASK) != 0) {
                  continuous = ! continuous;
// System.out.println ("RIGHT MOUSE_PRESSED : " + continuous);
                }
              }
              break;
            }
            case MouseEvent.MOUSE_RELEASED: {
              if (buttonPressed) {
                mouseLocation = mouseEvent.getPoint ();
// System.out.println ("MOUSE_RELEASED : " + mouseLocation +
//                     " : " + newGraphic.nParticles + " : " + oldGraphic.nParticles);
                buttonPressed = false;
              }
              break;
            }
          }

          break;

        }

        case MOUSE_MOTION: {

          final MouseEvent mouseEvent = (MouseEvent) fromMouseMotion.read ();
          switch (mouseEvent.getID ()) {
            case MouseEvent.MOUSE_DRAGGED: {
// System.out.println ("MOUSE_DRAGGED : buttonPressed = " + buttonPressed);
              if (buttonPressed) {
                oldMouseLocation = mouseLocation;
                mouseLocation = mouseEvent.getPoint ();
                createParticles (dragCount, oldMouseLocation, mouseLocation, newGraphic);
// System.out.println ("MOUSE_DRAGGED : " + mouseLocation + " : " + newGraphic.nParticles);
              }
              break;
            }
          }

          break;

        }

        case KEYBOARD: {

          KeyEvent keyEvent = (KeyEvent) fromKeyboard.read ();
          if (keyEvent.getID () == KeyEvent.KEY_PRESSED) {
            switch (keyEvent.getKeyCode ()) {
              case KeyEvent.VK_P: {
                if ((keyEvent.getModifiers () & InputEvent.SHIFT_MASK) != 0) {
                  stillCount = (5*stillCount)/4;
                  dragCount = (5*dragCount)/4;
                } else {
                  stillCount = (4*stillCount)/5;
                  dragCount = (4*dragCount)/5;
                  if (stillCount < 4) stillCount = 4;
                  if (dragCount < 4) dragCount = 4;
                }
System.out.println ("KEYBOARD : P " + stillCount + ", " + dragCount);
                break;
              }
              case KeyEvent.VK_L: {
                if ((keyEvent.getModifiers () & InputEvent.SHIFT_MASK) != 0) {
                  launchDeltaX = (5*launchDeltaX)/4;
                  launchDeltaY = (5*launchDeltaY)/4;
                } else {
                  launchDeltaX = (4*launchDeltaX)/5;
                  launchDeltaY = (4*launchDeltaY)/5;
                  if (launchDeltaX < 4) launchDeltaX = 4;
                  if (launchDeltaY < 4) launchDeltaY = 4;
                }
System.out.println ("KEYBOARD : L " + launchDeltaX + ", " + launchDeltaY);
                launchDeltaX2 = launchDeltaX*2;
                launchDeltaY2 = launchDeltaY*2;
                launchDeltaYbias = (launchDeltaY*bias)>>2;
                break;
              }
              case KeyEvent.VK_C: {
                if ((keyEvent.getModifiers () & InputEvent.SHIFT_MASK) != 0) {
                  if (bias < maxBias) bias++;
                } else {
                  if (bias > minBias) bias--;
                }
                launchDeltaYbias = (launchDeltaY*bias)>>2;
System.out.println ("KEYBOARD : C " + bias);
                break;
              }
              case KeyEvent.VK_S: {
                if ((keyEvent.getModifiers () & InputEvent.SHIFT_MASK) == 0) {
                  if (scale < 10) {
                    scale++;
                    mouseLocation.x >>= 1;
                    mouseLocation.y>>= 1;
                  }
                } else {
                  if (scale > 1) {
                    scale--;
                    mouseLocation.x <<= 1;
                    mouseLocation.y <<= 1;
                  }
                }
System.out.println ("KEYBOARD : S " + scale);
                Particle.setConstants (accY, maxDeltaY, scale, graphicsDim);
                break;
              }
            }
          }

          break;

        }
        
        case RESIZE : {
        
          ComponentEvent e = (ComponentEvent)fromCanvas.read ();
          if (e.getID () == ComponentEvent.COMPONENT_RESIZED) {
          	
            toGraphics.write (GraphicsProtocol.GET_DIMENSION);
            graphicsDim = (Dimension) fromGraphics.read ();
            System.out.println ("FireControl : graphics dimension = " + graphicsDim);
            Particle.setConstants (accY, maxDeltaY, scale, graphicsDim);
          	
          }
        	
          break;
        }


      }

    }

  }

}

