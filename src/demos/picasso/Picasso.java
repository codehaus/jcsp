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


import java.awt.*;
import java.awt.event.*;
import org.jcsp.lang.*;
import org.jcsp.awt.*;

/**
 * @author P.H. Welch
 */
public class Picasso implements CSProcess {

  final protected int width;
  final protected int height;
  final protected AltingChannelInput mouseEvent;
  final protected AltingChannelInput mouseMotionEvent;
  // final protected ChannelOutput toGraphics;
  // final protected ChannelInput fromGraphics;
  final protected DisplayList display;

  public Picasso (Dimension size,
                  AltingChannelInput mouseEvent, AltingChannelInput mouseMotionEvent,
                  // ChannelOutput toGraphics, ChannelInput fromGraphics) {
                  DisplayList display) {
    this.width = size.width;
    this.height = size.height;
    this.mouseEvent = mouseEvent;
    this.mouseMotionEvent = mouseMotionEvent;
    this.display = display;
    // this.toGraphics = toGraphics;
    // this.fromGraphics = fromGraphics;
  }

  // final protected DisplayList display = new DisplayList ();

  final protected String clickMessage = "C L I C K   T H E   M O U S E   T O   D R A W";
  final protected String clickPlea = "P L E A S E   M O V E   T H E   M O U S E   B A C K";

  protected GraphicsCommand[] mouseEntered, mouseExited;         // fixed commands

  protected void setMouseEntered () {
    final GraphicsCommand[] tmp = {
      new GraphicsCommand.SetColor (Color.cyan),
      new GraphicsCommand.FillRect (0, 0, width, height),
      new GraphicsCommand.SetColor (Color.black),
      new GraphicsCommand.DrawString (clickMessage, (width-(5*clickMessage.length ()))/2, height/2)
    };
    mouseEntered = tmp;
  }

  protected void setmouseExited () {
    final GraphicsCommand[] tmp = {
      new GraphicsCommand.SetColor (Color.pink),
      new GraphicsCommand.FillRect (0, 0, width, height),
      new GraphicsCommand.SetColor (Color.black),
      new GraphicsCommand.DrawString (clickPlea, (width-(5*clickPlea.length ()))/2, height/2)
    };
    mouseExited = tmp;
  }

  final protected GraphicsCommand[] mouseDrawLine = {null};  // filled in dynamically

  final protected int targetSize = 50;
  final protected GraphicsCommand[] mouseTarget = {null};    // filled in dynamically

  protected Point point = new Point ();
  protected boolean drawing = false;
  protected boolean targetting = false;

  protected void handleMouseEvent (final MouseEvent event) {
    switch (event.getID ()) {
      case MouseEvent.MOUSE_ENTERED:
        display.change (mouseEntered, 0);
      break;
      case MouseEvent.MOUSE_EXITED:
        display.change (mouseExited, 0);
      break;
      case MouseEvent.MOUSE_PRESSED:
        int modifiers = event.getModifiers ();
        if ((modifiers & InputEvent.BUTTON1_MASK) != 0) {
          if (targetting) {
            targetting = false;
            display.set (mouseEntered);
          }
          drawing = ! drawing;
          if (drawing) point = event.getPoint ();
        } else
        if ((modifiers & InputEvent.BUTTON2_MASK) != 0) {
          drawing = false;
          targetting = ! targetting;
          display.set (mouseEntered);
          if (targetting) {
            point = event.getPoint ();
            mouseTarget[0] =
              new GraphicsCommand.DrawRect (
                point.x - (targetSize/2),
                point.y - (targetSize/2),
                targetSize, targetSize
              );
            display.extend (mouseTarget);
          }
        }
        if ((modifiers & InputEvent.BUTTON3_MASK) != 0) {
          drawing = false;
          targetting = false;
          display.set (mouseEntered);
        }
      break;
    }
  }

  protected void handleMouseMotionEvent (final MouseEvent motion) {
    final Point newPoint = motion.getPoint ();
    if (drawing) {
      final GraphicsCommand mouseDrawLine =
        new GraphicsCommand.DrawLine (point.x, point.y, newPoint.x, newPoint.y);
      point = newPoint;
      display.extend (mouseDrawLine);
    } else {
      final GraphicsCommand mouseTarget =
        new GraphicsCommand.DrawRect (newPoint.x - (targetSize/2),
                                      newPoint.y - (targetSize/2),
                                      targetSize, targetSize);
      display.change (mouseTarget, mouseEntered.length);
    }
  }

  public void run () {
    final AltingChannelInput[] mouse = {mouseEvent, mouseMotionEvent};
    final int MOUSE_EVENT = 0;
    final int MOUSE_MOTION_EVENT = 1;
    final boolean[] preCondition = {true, false};
    final Alternative alt = new Alternative (mouse);
    // toGraphics.write (new GraphicsProtocol.SetPaintable (display));
    // fromGraphics.read ();
    setMouseEntered ();
    setmouseExited ();
    display.set (mouseExited);
    while (true) {
      preCondition[1] = drawing | targetting;
      switch (alt.select (preCondition)) {
        case MOUSE_EVENT:
          handleMouseEvent ((MouseEvent) mouseEvent.read ());
        break;
        case MOUSE_MOTION_EVENT:
          handleMouseMotionEvent ((MouseEvent) mouseMotionEvent.read ());
        break;
      }
    }
  }

}
