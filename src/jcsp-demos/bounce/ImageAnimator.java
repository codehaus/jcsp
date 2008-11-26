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
import org.jcsp.lang.*;
import org.jcsp.awt.*;

/**
 * @author P.H. Welch
 */
public class ImageAnimator implements CSProcess {

  private final ChannelInputInt in;
  private final ChannelOutput toGraphics;
  private final ChannelInput fromGraphics;
  private final Display display;
  private final Image[] images;
  private int dx;
  private int dy;
  
  /**
   * Construct a new ImageAnimator that will cycle.
   *
   * @param in the channel used to direct the animation
   * @param toGraphics the channel to the graphics component
   * @param fromGraphics the channel from the graphics component
   * @param display the display-channel to the graphics component
   * @param images the array of images to be displayed on the animation
   * @param dx the x displacement between each frame
   * @param dy the y displacement between each frame
   */
  public ImageAnimator (ChannelInputInt in, ChannelOutput toGraphics, ChannelInput fromGraphics,
                        Display display, Image[] images, int dx, int dy) {
    this.in = in;
    this.toGraphics = toGraphics;
    this.fromGraphics = fromGraphics;
    this.display = display;
    this.images = images;
    this.dx = dx;
    this.dy = dy;
  }

  private Dimension dimension;

  private int lastX = 0;
  private int lastY = 0;

  private final GraphicsCommand[] gCommand = {null, null, null};

  public void run () {

    final Thread me = Thread.currentThread ();
    System.out.println ("ImageAnimator " + " priority = " + me.getPriority ());
    me.setPriority (Thread.MIN_PRIORITY);
    System.out.println ("ImageAnimator " + " priority = " + me.getPriority ());

    toGraphics.write (GraphicsProtocol.GET_DIMENSION);
    dimension = (Dimension) fromGraphics.read ();
    toGraphics.write (GraphicsProtocol.GET_BACKGROUND);
    gCommand[0] = new GraphicsCommand.SetColor ((Color) fromGraphics.read ());
    gCommand[1] = new GraphicsCommand.FillRect (0, 0, dimension.width, dimension.height);
    final int nImages = images.length;
    int frame = 0;
    displayFirstImage (images[frame]);
    while (true) {
      final int delta = in.read ();
      frame = (frame + delta + nImages) % nImages;
      displayNextImage (images[frame]);
    }
  }
  
  /**
   * Called to update the graphics display
   *
   * @param image the image to draw
   */
  public void displayFirstImage (final Image image) {
    final int imageWidth = image.getWidth (null);
    final int imageHeight = image.getHeight (null);
    lastX = (dimension.width - imageWidth)/2;
    lastY = (dimension.height - imageHeight)/2;
    gCommand[2] = new GraphicsCommand.DrawImage (image, lastX, lastY);
    display.set (gCommand);
  }
  
  /**
   * Called to update the graphics display
   *
   * @param image the image to draw
   */
  public void displayNextImage (final Image image) {
    final int imageWidth = image.getWidth (null);
    final int imageHeight = image.getHeight (null);
    lastX += dx;
    if ((lastX < 0) | ((lastX + imageWidth) >= dimension.width)) {
      dx = -dx;
      lastX += dx;
    }
    lastY += dy;
    if ((lastY < 0) | ((lastY + imageHeight) >= dimension.height)) {
      dy = -dy;
      lastY += dy;
    }
    gCommand[2] = new GraphicsCommand.DrawImage (image, lastX, lastY);
    display.set (gCommand);
  }

}
