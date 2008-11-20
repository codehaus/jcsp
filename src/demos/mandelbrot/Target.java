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


import org.jcsp.awt.*;
import java.awt.event.*;

/**
 * @author P.H. Welch
 */
class Target {

  private final int width;
  private final int height;

  public Target (final int width, final int height) {
    this.width = width;
    this.height = height;
  }

  int zoomX, zoomY;

  final double initialZoom = 0.1;
  double zoom = initialZoom;

  final double zoomAdjust = 1.2;

  final double maxZoom = 0.75;
  final double minZoom = 0.03;

  boolean shiftZoom = false;

  final double maxShiftZoom = 1.0;

  int dX, dY, dX2, dY2;

  public void reset (MouseEvent mouseEvent) {
    zoom = initialZoom;
    dX = (int) (((double) width)*(zoom/2.0));
    dY = (int) (((double) height)*(zoom/2.0));
    dX2 = (int) (((double) width)*zoom);
    dY2 = (int) (((double) height)*zoom);
    zoomX = mouseEvent.getX ();
    zoomY = mouseEvent.getY ();
    shiftZoom = ((mouseEvent.getModifiers () & InputEvent.SHIFT_MASK) != 0);
  }

  public void move (MouseEvent mouseEvent) {
    zoomX = mouseEvent.getX ();
    zoomY = mouseEvent.getY ();
  }

  public void zoomUp () {
    zoom *= zoomAdjust;
    if (shiftZoom) {
      if (zoom > maxShiftZoom) zoom = maxShiftZoom;
    } else {
      if (zoom > maxZoom) zoom = maxZoom;
    }
    dX = (int) (((double) width)*(zoom/2.0));
    dY = (int) (((double) height)*(zoom/2.0));
    dX2 = (int) (((double) width)*zoom);
    dY2 = (int) (((double) height)*zoom);
  }

  public void zoomDown () {
    zoom /= zoomAdjust;
    if (zoom < minZoom) zoom = minZoom;
    dX = (int) (((double) width)*(zoom/2.0));
    dY = (int) (((double) height)*(zoom/2.0));
    dX2 = (int) (((double) width)*zoom);
    dY2 = (int) (((double) height)*zoom);
  }

  public GraphicsCommand makeGraphicsCommand () {
    if (!shiftZoom) {
      if (zoomX < dX) {
        zoomX = dX;
      } else if (zoomX >= (width - dX)) {
        zoomX = (width - dX) - 1;
      }
      if (zoomY < dY) {
        zoomY = dY;
      } else if (zoomY >= (height - dY)) {
        zoomY = (height - dY) - 1;
      }
    }
    return new GraphicsCommand.DrawRect (zoomX - dX, zoomY - dY, dX2, dY2);
  }

}
