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
import java.awt.*;
import java.awt.image.*;
import java.util.Random;

/**
 * @author P.H. Welch
 */
public class Splatter implements CSProcess {

  protected int nAcross, nDown;
  final protected int burst;

  final private AltingChannelInput in;

  final private ChannelOutput rearrangeConfigure;
  final private AltingChannelInput rearrangeEvent;

  final private ChannelOutput toGraphics;
  final private ChannelInput fromGraphics;

  public Splatter (final int nAcross, final int nDown, final int burst,
                   final AltingChannelInput in,
                   final ChannelOutput rearrangeConfigure,
                   final AltingChannelInput rearrangeEvent,
                   final ChannelOutput toGraphics,
                   final ChannelInput fromGraphics) {

    this.nAcross = nAcross;
    this.nDown = nDown;
    this.burst = burst;
    this.in = in;
    this.rearrangeConfigure = rearrangeConfigure;
    this.rearrangeEvent = rearrangeEvent;
    this.toGraphics = toGraphics;
    this.fromGraphics = fromGraphics;

  }

  //     colours                :      red        blue        black
  //     -------                       ---        ----        -----
  
  final protected byte[] reds   = {(byte)0xff, (byte)0x00, (byte)0x00};
  final protected byte[] greens = {(byte)0x00, (byte)0x00, (byte)0x00};
  final protected byte[] blues  = {(byte)0x00, (byte)0xff, (byte)0x00};

  final protected static byte red   = 0;
  final protected static byte blue  = 1;
  final protected static byte black = 2;

  //     pixel array and key run-time parameters
  //     ---------------------------------------

  protected byte[] pixels;

  protected int width, height, wStride, hStride, wGap, hGap;

  //     protected methods
  //     -----------------

  protected ColorModel setColorModel () {
    return new IndexColorModel (2, 3, reds, greens, blues);
  }

  protected void computeGeometry () {

    final int dw = width/((2*nAcross) + 1);
    final int dh = height/((2*nDown) + 1);
    final int box = Math.min (dw, dh);           // size of each red box

    // The following four constants are scaled to avoid rounding errors later.

    wGap = width - (nAcross*box);                // (width) gap between boxes
    hGap = height - (nDown*box);                 // (height) gap between boxes

    wStride = (box*(nAcross + 1)) + wGap;        // (width) stride between boxes
    hStride = (box*(nDown + 1)) + hGap;          // (height) stride between boxes

  }

  protected void initialisePixels () {
    for (int h = 0; h < height; h++) {
      final int hh = h*(nDown + 1);
      for (int w = 0; w < width; w++) {
        final int ww = w*(nAcross + 1);
        if (((ww % wStride) > wGap) && ((hh % hStride) > hGap)) {
          pixels[(h*width) + w] = red;
        } else {
          pixels[(h*width) + w] = blue;
        }
      }
    }
  }

  protected void clearPixels () {
    for (int ij = 0; ij < pixels.length; ij++) {
      pixels[ij] = black;
    }
  }

  protected void splatPixels () {
    for (int i = 0; i < burst; i++) {
      pixels[range (pixels.length)] = black;
    }
  }

  protected void unsplatPixels () {
    for (int i = 0; i < burst; i++) {
      final int ij = range (pixels.length);
      final int ww = (ij % width)*(nAcross + 1);
      final int hh = (ij / width)*(nDown + 1);
      if (((ww % wStride) > wGap) && ((hh % hStride) > hGap)) {
        pixels[ij] = red;
      } else {
        pixels[ij] = blue;
      }
    }
  }

  final protected Random random = new Random ();

  final protected int range (int n) {
    int i = random.nextInt ();
    if (i < 0) {
      if (i == Integer.MIN_VALUE) {      // guard against minint !
        i = 42;
      } else {
        i = -i;
      }
    }
    return i % n;
  }

  public void run () {

    rearrangeConfigure.write (nAcross + " x " + nDown);

    toGraphics.write (GraphicsProtocol.GET_DIMENSION);
    final Dimension graphicsDim = (Dimension) fromGraphics.read ();
    System.out.println ("Splatter: graphics dimension = " + graphicsDim);

    width = graphicsDim.width;
    height = graphicsDim.height;

    pixels = new byte[width*height];

    computeGeometry ();

    final ColorModel model = setColorModel ();

    final MemoryImageSource mis = new MemoryImageSource (width, height, model, pixels, 0, width);
    mis.setAnimated (true);
    mis.setFullBufferUpdates (true);

    toGraphics.write (new GraphicsProtocol.MakeMISImage (mis));
    final Image image = (Image) fromGraphics.read ();

    final DisplayList display = new DisplayList ();
    toGraphics.write (new GraphicsProtocol.SetPaintable (display));
    fromGraphics.read ();

    final GraphicsCommand[] drawImage = {new GraphicsCommand.DrawImage (image, 0, 0)};
    display.set (drawImage);

    final Thread me = Thread.currentThread ();
    System.out.println ("Splatter priority = " + me.getPriority ());
    me.setPriority (Thread.MIN_PRIORITY);
    System.out.println ("Splatter priority = " + me.getPriority ());

    int state = SplatterControl.restart;
    boolean splatting = false;

    final Guard[] guard = {in, rearrangeEvent, new Skip ()};
    final boolean[] preCondition = {true, true, false};
    final int NEW_STATE = 0;
    final int REARRANGE = 1;
    final int RUNNING = 2;

    final Alternative alt = new Alternative (guard);

    initialisePixels ();
    mis.newPixels ();

    while (true) {
      switch (alt.priSelect (preCondition)) {
        case NEW_STATE:
          state = ((Integer) in.read ()).intValue ();
          switch (state) {
            case SplatterControl.restart:
              System.out.println ("Splatter: restart");
              initialisePixels ();
              mis.newPixels ();
              preCondition[RUNNING] = false;
            break;
            case SplatterControl.frozen:
              System.out.println ("Splatter: frozen");
              preCondition[RUNNING] = false;
            break;
            case SplatterControl.clear:
              System.out.println ("Splatter: clear");
              clearPixels ();
              mis.newPixels ();
              preCondition[RUNNING] = false;
            break;
            case SplatterControl.splatting:
              System.out.println ("Splatter: splatting");
              preCondition[RUNNING] = true;
              splatting = true;
            break;
            case SplatterControl.unsplatting:
              System.out.println ("Splatter: unsplatting");
              preCondition[RUNNING] = true;
              splatting = false;
            break;
          }
        break;
        case REARRANGE:
          rearrangeEvent.read ();
          nAcross = 1 + range (width/SplatMain.squareFactor);
          nDown = 1 + range (height/SplatMain.squareFactor);
          rearrangeConfigure.write (nAcross + " x " + nDown);
          System.out.println ("Splatter: rearranging" + " " + nAcross + " " + nDown);
          computeGeometry ();
          if (state == SplatterControl.restart) {
            initialisePixels ();
            mis.newPixels ();
          }
        break;
        case RUNNING:
          if (splatting) {
            splatPixels ();
          } else {
            unsplatPixels ();
          }
          mis.newPixels ();
        break;
      }  
    }

  }

}

