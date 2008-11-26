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

/**
 * @author P.H. Welch
 */
public class Particle {

  private static int accY;            // gravity
  private static int maxDeltaY;       // terminal downward velocity

  private static int scale;           // log-2 mapping to actual canvas dimension
  private static int height;          // canvas height (scaled up)
  private static int width;           // canvas width (scaled up)

  public static void setScale (int scale) {
    Particle.scale = scale;
  }

  public static void setConstants (int accY, int maxDeltaY, int scale, Dimension dim) {
    Particle.accY = accY;
    Particle.maxDeltaY = maxDeltaY;
    // Particle.scale = scale;
    Particle.height = (dim.height)<<scale;
    Particle.width = (dim.width)<<scale;
  }

  private int X, Y, oldX, oldY, deltaX, deltaY;     // scaled up coordinates
  private Color colour;

  public void setAttributes (int X, int Y, int deltaX, int deltaY, Color colour) {
    this.oldX = X;
    this.oldY = Y;
    this.X = X + deltaX;
    this.Y = Y + deltaY;
    this.deltaX = deltaX;
    this.deltaY = deltaY + accY;
    if (this.deltaY > maxDeltaY) this.deltaY = maxDeltaY;
    this.colour = colour;
  }

  public void paint (Graphics g) {
    g.setColor (colour);
    g.drawLine (oldX>>scale, oldY>>scale, X>>scale, Y>>scale);
  }

  public boolean move (Particle p) {
    // if ((X < 0) || (X >= width) || (Y >= height)) return false;
    if (((X < 0) && (deltaX < 0)) || ((X >= width) && (deltaX > 0))) return false;
    if ((Y >= height) && (deltaY > 0)) {
      deltaY = deltaY/2;
      if (deltaY < accY) {
        return false;
      } else {
        deltaY = -deltaY;
      }
    }
    p.setAttributes (X, Y, deltaX, deltaY, colour);
    return true;
  }

  public void scaleChange (int scaleDelta) {
    X >>= scaleDelta;
    Y >>= scaleDelta;
    oldX >>= scaleDelta;
    oldY >>= scaleDelta;
  }

}
