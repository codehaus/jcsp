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

/**
 * @author Quickstone Technologies Limited
 * @author P.H. Welch (non-networked original code)
 */
class FarmPacket implements Cloneable {

  public static final int STRIDE_SILENT = 5;

  public static final int SCROLL_SILENT = 0;
  public static final int SCROLL_UP = 1;
  public static final int SCROLL_DOWN = 2;
  public static final int NO_SCROLL = 3;

  public int maxIterations, scrolling, colourModel;

  public double left, top, size;

  public GraphicsCommand colouring;

  public boolean ok;

  public byte[] pixels;

  public void copy (final FarmPacket p) {
    maxIterations = p.maxIterations;
    scrolling = p.scrolling;
    colourModel = p.colourModel;
    left= p.left;
    top = p.top;
    size = p.size;
    colouring = p.colouring;
    ok = p.ok;
    System.arraycopy (p.pixels, 0, pixels, 0, pixels.length);
  }

  public Object clone () {
    try {
      final FarmPacket packet = (FarmPacket) super.clone ();
      packet.pixels = (byte[]) pixels.clone ();
      return packet;
    } catch (CloneNotSupportedException e) {
      System.out.println (e);
      System.exit (-1);
      return null;
    }
  }

}
