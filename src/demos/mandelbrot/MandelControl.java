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
import java.awt.event.*;
import java.awt.image.*;
import java.util.*;

/**
 * @author P.H. Welch
 */
class MandelControl implements CSProcess {

  private final int minMaxIterations;
  private final int maxMaxIterations;

  private final AltingChannelInput mouseChannel;
  private final AltingChannelInput mouseMotionChannel;
  private final AltingChannelInput keyChannel;

  private final ChannelOutput scrollConfigure;
  private final AltingChannelInput scrollChannel;
  private final String[] scrollMenu;
  private final ChannelOutput iterationsConfigure;
  private final AltingChannelInput iterationsChannel;
  private final String[] iterationsMenu;
  private final ChannelOutput targetConfigure;
  private final AltingChannelInput targetChannel;
  private final String[] targetMenu;
  private final ChannelOutput colourConfigure;
  private final AltingChannelInput colourChannel;
  private final String[] colourMenu;
  private final ChannelOutput forwardConfigure;
  private final AltingChannelInput forwardChannel;
  private final ChannelOutput backwardConfigure;
  private final AltingChannelInput backwardChannel;
  private final ChannelOutput[] infoConfigure;

  private final ChannelOutput request;
  private final ChannelInput reply;

  private final ChannelOutput toGraphics;
  private final ChannelInput fromGraphics;

  private final Alternative allControl, fbControl;

  private final int FORWARD_EVENT = 0;
  private final int BACKWARD_EVENT = 1;
  private final int MOUSE_EVENT = 2;
  private final int MOUSE_MOTION_EVENT = 3;
  private final int KEY_EVENT = 4;
  private final int SCROLL_EVENT = 5;
  private final int ITERATIONS_EVENT = 6;
  private final int TARGET_EVENT = 7;
  private final int COLOUR_EVENT = 8;

  public MandelControl (final int minMaxIterations, final int maxMaxIterations,
                        final AltingChannelInput mouseChannel,
                        final AltingChannelInput mouseMotionChannel,
                        final AltingChannelInput keyChannel,
                        final ChannelOutput scrollConfigure,
                        final AltingChannelInput scrollChannel,
                        final String[] scrollMenu,
                        final ChannelOutput iterationsConfigure,
                        final AltingChannelInput iterationsChannel,
                        final String[] iterationsMenu,
                        final ChannelOutput targetConfigure,
                        final AltingChannelInput targetChannel,
                        final String[] targetMenu,
                        final ChannelOutput colourConfigure,
                        final AltingChannelInput colourChannel,
                        final String[] colourMenu,
                        final ChannelOutput forwardConfigure,
                        final AltingChannelInput forwardChannel,
                        final ChannelOutput backwardConfigure,
                        final AltingChannelInput backwardChannel,
                        final ChannelOutput[] infoConfigure,
                        final ChannelOutput request, final ChannelInput reply,
                        final ChannelOutput toGraphics, final ChannelInput fromGraphics) {

    this.minMaxIterations = minMaxIterations;
    this.maxMaxIterations = maxMaxIterations;
    this.mouseChannel = mouseChannel;
    this.mouseMotionChannel = mouseMotionChannel;
    this.keyChannel = keyChannel;
    this.scrollConfigure = scrollConfigure;
    this.scrollChannel = scrollChannel;
    this.scrollMenu = scrollMenu;
    this.iterationsConfigure = iterationsConfigure;
    this.iterationsChannel = iterationsChannel;
    this.iterationsMenu = iterationsMenu;
    this.targetConfigure = targetConfigure;
    this.targetChannel = targetChannel;
    this.targetMenu = targetMenu;
    this.colourConfigure = colourConfigure;
    this.colourChannel = colourChannel;
    this.colourMenu = colourMenu;
    this.forwardConfigure = forwardConfigure;
    this.forwardChannel = forwardChannel;
    this.backwardConfigure = backwardConfigure;
    this.backwardChannel = backwardChannel;
    this.infoConfigure = infoConfigure;
    this.request = request;
    this.reply = reply;
    this.toGraphics = toGraphics;
    this.fromGraphics = fromGraphics;

    allControl = new Alternative (
      new AltingChannelInput[] {
        forwardChannel, backwardChannel,
        mouseChannel, mouseMotionChannel, keyChannel,
        scrollChannel, iterationsChannel, targetChannel, colourChannel
      }
    );

    fbControl = new Alternative (
      new AltingChannelInput[] {
        forwardChannel, backwardChannel
      }
    );

  }

  //     key run-time parameters
  //     -----------------------

  private final double initialSize = 4.2;
  private final double initialLeft = -2.1;

  private int width;    //  global ?
  private int height;    //  global ?

  private double doubleWidth;    //  global ?
  private double aspectRatio;    //  global ?

  private Target box;

  private FarmPacket packet = new FarmPacket ();

  private final Vector pax = new Vector ();      // for saving FarmPacket(ets)
  private int vecPax = 0;      // number in the vector
  private int maxPax = 0;      // number currently valid (maxPax <= vecPax)
  private int currentPax = 0;  // number currently on display (currentPax <= maxPax)

  private int scrolling = FarmPacket.SCROLL_SILENT;

  private final int nColourModels = 2;
  private MemoryImageSource[] mis = new MemoryImageSource[nColourModels];
  private int currentColourModel = 0;

  private final GraphicsCommand whiteXOR =
    new GraphicsCommand.SetXORMode (Color.white);

  private final GraphicsCommand whiteCOLOR =
    new GraphicsCommand.SetColor (Color.white);

  private final GraphicsCommand blackCOLOR =
    new GraphicsCommand.SetColor (Color.black);

  private final GraphicsCommand[] gcFirst = {null};
  private final GraphicsCommand[] gcImage = {null};
  private final GraphicsCommand[] gcSketch = {whiteCOLOR, null};

  private final GraphicsCommand[] draw = new GraphicsCommand[nColourModels];

  private final DisplayList display = new DisplayList ();

/*
  private final GraphicsProtocol gpFirst =
    new GraphicsProtocol.Set (gcFirst);
  private final GraphicsProtocol gpImage =
    new GraphicsProtocol.Set (gcImage);

  private final GraphicsProtocol gpSketch =
    new GraphicsProtocol.Update (gcSketch);

  private final GraphicsProtocol gpChangeImage =
    new GraphicsProtocol.Change (0, gcImage);
  private final GraphicsProtocol gpChangeSketch =
    new GraphicsProtocol.Change (1, gcSketch);
*/

  private final CSTimer tim = new CSTimer ();

  //     protected methods
  //     -----------------

  protected ColorModel createColorModel (final int minMaxIterations,
                                         final int maxMaxIterations) {

    System.out.println("createColorModel: " + minMaxIterations + " " + maxMaxIterations);

    byte[] red = new byte[maxMaxIterations + 1];
    byte[] green = new byte[maxMaxIterations + 1];
    byte[] blue = new byte[maxMaxIterations + 1];

    final int redF = 127;
    final int greenF = 30;
    final int blueF = 50;

    red[0] = 0;
    green[0] = 0;
    blue[0] = 0;

    for (int i = 1; i < (maxMaxIterations); i++) {
      red[i] = (byte) (((i*redF) % 256) - 128);
      green[i] = (byte) (((i*greenF) % 256) - 128);
      blue[i] = (byte) (((i*blueF) % 256) - 128);
    }

    int j = minMaxIterations;
    while (j <= maxMaxIterations) {
      red[j] = 0;
      green[j] = 0;
      blue[j] = 0;
      j *= 2;
    }

    System.out.println("createColorModel: --> IndexColorModel");
    final int nBitsOfColour = 8;
    try {
      return new IndexColorModel (nBitsOfColour, red.length, red, green, blue);
    } catch (ArrayIndexOutOfBoundsException e) {
      System.out.println("createColorModel: " + e);
      System.exit (-1);
      return null;
    }

  }

  protected void fade (final byte[] downColour, final byte[] upColour,
                       final int start, final int finish, final int step) {
    int up = 1;
    int down = 255;
    for (int i = start; i < finish; i++) {
      upColour[i] = (byte) up;
      downColour[i] = (byte) down;
      up += step;
      down -= step;
    }
  }


  protected ColorModel createColorModel (final int variety) {

    System.out.println("createColorModel: 256");

    byte[] red = new byte[256];
    byte[] green = new byte[256];
    byte[] blue = new byte[256];

    red[0] = 0;
    green[0] = 0;
    blue[0] = 0;

    switch (variety) {

      case 0:

        final int redF = 127;
        final int greenF = 30;
        final int blueF = 50;

        for (int i = 1; i < 256; i++) {
          red[i] = (byte) (((i*redF) % 256) - 128);
          green[i] = (byte) (((i*greenF) % 256) - 128);
          blue[i] = (byte) (((i*blueF) % 256) - 128);
        }

      break;
      case 1:

        fade (green, blue, 1, 86, 3);
        fade (blue, red, 86, 171, 3);
        fade (red, green, 171, 256, 3);

      break;

    }

    System.out.println("createColorModel: --> IndexColorModel");
    final int nBitsOfColour = 8;
    return new IndexColorModel (nBitsOfColour, red.length, red, green, blue);

  }

  public void run () {

    toGraphics.write (GraphicsProtocol.GET_DIMENSION);
    final Dimension graphicsDim = (Dimension) fromGraphics.read ();
    System.out.println ("MandelControl: graphics dimension = " + graphicsDim);

    width = graphicsDim.width;
    height = graphicsDim.height;

    doubleWidth = (double) width;                          // (width - 1) ???
    aspectRatio = ((double) height)/doubleWidth;

    packet.pixels = new byte[width*height];
    packet.left = initialLeft; packet.top = (initialSize*aspectRatio)/2.0;
    packet.size = initialSize; packet.maxIterations = minMaxIterations;
    // packet.scrolling, packet.colouring and packet.colourModel are maintained separately

    final Image[] image = new Image[nColourModels];

    for (int i = 0; i < nColourModels; i++) {
      mis[i] = new MemoryImageSource (width, height, createColorModel (i),
                                      packet.pixels, 0, width);
      mis[i].setAnimated (true);
      mis[i].setFullBufferUpdates (true);
      toGraphics.write (new GraphicsProtocol.MakeMISImage (mis[i]));
      image[i] = (Image) fromGraphics.read ();
      draw[i] = new GraphicsCommand.DrawImage (image[i], 0, 0);
    }
    currentColourModel = 0;

    toGraphics.write (new GraphicsProtocol.SetPaintable (display));
    fromGraphics.read ();

    request.write (new Integer (width));
    request.write (new Integer (height));
    request.write (mis);
    request.write (display);

    box = new Target (width, height);

    gcFirst[0] = new GraphicsCommand.ClearRect (0, 0, width, height);
    gcImage[0] = draw[currentColourModel];  // not needed ???
    display.set (gcFirst);
    gcSketch[1] = whiteCOLOR;
    display.extend (gcSketch);

    toGraphics.write (GraphicsProtocol.REQUEST_FOCUS);
    fromGraphics.read ();

    buildFirstPicture ();  // first picture disallows cancel

    while (true) {
      defineNextPicture ();  // enable ... disable controls
      buildPicture ();       // enable ... disable cancel
    }

  }

  private void buildFirstPicture () {
    infoConfigure[0].write ("computing ...");
    infoConfigure[1].write ("first ...");
    infoConfigure[2].write ("image ...");
    packet.colouring = gcSketch[0];
    packet.scrolling = scrolling;
    packet.colourModel = currentColourModel;
    final long t0 = tim.read ();
    request.write (packet);
    packet = (FarmPacket) reply.read ();     // packet.pixels and packet.ok have been set
    final long t1 = tim.read ();
    System.out.println ("buildPicture: " + (t1 - t0) + " milliseconds ...");
    gcImage[0] = draw[currentColourModel];
    display.set (gcImage);
    mis[currentColourModel].newPixels ();
    infoConfigure[0].write ((new Double (packet.top)).toString ());
    infoConfigure[1].write ((new Double (packet.left)).toString ());
    infoConfigure[2].write ((new Double (packet.size/doubleWidth)).toString ());
    savePacket ();
  }

  private void buildPicture () {
    switch (scrolling) {
      case FarmPacket.SCROLL_UP: case FarmPacket.SCROLL_DOWN:
        gcImage[0] = draw[currentColourModel];
        display.set (gcImage);
        infoConfigure[0].write ((new Double (packet.top)).toString ());
        infoConfigure[1].write ((new Double (packet.left)).toString ());
        infoConfigure[2].write ((new Double (packet.size/doubleWidth)).toString ());
      break;
      case FarmPacket.SCROLL_SILENT: case FarmPacket.NO_SCROLL:
        infoConfigure[0].write ("computing ...");
        infoConfigure[1].write ("next ...");
        infoConfigure[2].write ("image ...");
      break;
    }
    packet.colouring = gcSketch[0];
    packet.scrolling = scrolling;
    packet.colourModel = currentColourModel;
    final long t0 = tim.read ();
    request.write (packet);
    packet = (FarmPacket) reply.read ();     // packet.pixels and packet.ok have been set
    final long t1 = tim.read ();
    System.out.println ("buildPicture: " + (t1 - t0) + " milliseconds ..." + packet.ok);
    if (packet.ok) {
      savePacket ();
    } else {
      packet.copy ((FarmPacket) pax.elementAt (currentPax - 1));
      int pindex = 0;
      int pmax = 256;
      while (pmax < packet.maxIterations) {
        pmax *= 2;
        pindex++;
      }
      iterationsConfigure.write (new Integer (pindex));
    }
/*
    switch (scrolling) {
      case FarmPacket.SCROLL_UP: case FarmPacket.SCROLL_DOWN:
        if (! packet.ok) {
          mis[currentColourModel].newPixels ();
          gcImage[0] = draw[currentColourModel];
          toGraphics.write (gpImage);
          fromGraphics.read ();
          infoConfigure[0].write ((new Double (packet.top)).toString ());
          infoConfigure[1].write ((new Double (packet.left)).toString ());
          infoConfigure[2].write ((new Double (packet.size/doubleWidth)).toString ());
        }
      break;
      case FarmPacket.SCROLL_SILENT: case FarmPacket.NO_SCROLL:
        gcImage[0] = draw[currentColourModel];
        toGraphics.write (gpImage);
        fromGraphics.read ();
        if (packet.ok) mis[currentColourModel].newPixels ();
        infoConfigure[0].write ((new Double (packet.top)).toString ());
        infoConfigure[1].write ((new Double (packet.left)).toString ());
        infoConfigure[2].write ((new Double (packet.size/doubleWidth)).toString ());
      break;
    }
*/
    mis[currentColourModel].newPixels ();
    gcImage[0] = draw[currentColourModel];
    display.set (gcImage);
    infoConfigure[0].write ((new Double (packet.top)).toString ());
    infoConfigure[1].write ((new Double (packet.left)).toString ());
    infoConfigure[2].write ((new Double (packet.size/doubleWidth)).toString ());
  }

  private void savePacket () {
    if (currentPax == vecPax) {
      try {
        pax.addElement (packet.clone ());
        vecPax++;
        currentPax++;
        maxPax = currentPax;
      } catch (OutOfMemoryError e) {
        System.out.println ("*** BUST: " + e);
        final FarmPacket pack = (FarmPacket) pax.elementAt (0);
        pax.removeElementAt (0);
        pack.copy (packet);
        pax.addElement (pack);
      }
    } else {
      ((FarmPacket) pax.elementAt (currentPax)).copy (packet);
      currentPax++;
      maxPax = currentPax;
    }
System.out.println ("savePacket: " + currentPax + " " + maxPax + " " + vecPax);
  }

  protected void defineNextPicture () {

    final int INITIAL = 0;
    final int VISIBLE_BOX = 1;

    MouseEvent mouseEvent = null;
    KeyEvent keyEvent = null;

    scrollConfigure.write (Boolean.TRUE);
    iterationsConfigure.write (Boolean.TRUE);
    targetConfigure.write (Boolean.TRUE);
    colourConfigure.write (Boolean.TRUE);
    if (currentPax > 1) backwardConfigure.write (Boolean.TRUE);
    if (currentPax < maxPax) forwardConfigure.write (Boolean.TRUE);
System.out.println ("defineNextPicture: " + currentPax + " " + maxPax + " " + vecPax);

    int state = INITIAL;
    boolean newPicture = true;
    boolean running = true;
    while (running) {
      while (keyChannel.pending ()) keyChannel.read ();         // clear keystrokes
      while (mouseChannel.pending ()) mouseChannel.read ();     // clear mouse clicks
      toGraphics.write (GraphicsProtocol.REQUEST_FOCUS);
      fromGraphics.read ();
      switch (state) {
        case INITIAL:
          switch (allControl.fairSelect ()) {
            case MOUSE_EVENT:
              mouseEvent = (MouseEvent) mouseChannel.read ();
              if (mouseEvent.getID () == MouseEvent.MOUSE_PRESSED) {
                box.reset (mouseEvent);
                gcSketch[1] = box.makeGraphicsCommand ();
                display.extend (gcSketch);
                forwardConfigure.write (Boolean.FALSE);
                backwardConfigure.write (Boolean.FALSE);
                state = VISIBLE_BOX;
              }
            break;
            case MOUSE_MOTION_EVENT:
              mouseEvent = (MouseEvent) mouseMotionChannel.read ();  // ignore mouse movements here
            break;
            case KEY_EVENT:
              keyEvent = (KeyEvent) keyChannel.read ();
              if (keyEvent.getID () == KeyEvent.KEY_RELEASED) {
                switch (keyEvent.getKeyCode ()) {
                  case KeyEvent.VK_RIGHT:
                    packet.maxIterations *= 2;
                    if (packet.maxIterations > maxMaxIterations) packet.maxIterations = maxMaxIterations;
                    int pindex = 0;
                    int pmax = 256;
                    while (pmax < packet.maxIterations) {
                      pmax *= 2;
                      pindex++;
                    }
                    iterationsConfigure.write (new Integer (pindex));
                    gcSketch[1] = whiteCOLOR;
                    display.extend (gcSketch);
                    newPicture = false;                  // only changed packet.maxIterations
                    running = false;
                  break;
                  case KeyEvent.VK_LEFT:
                    packet.maxIterations /= 2;
                    if (packet.maxIterations < minMaxIterations) packet.maxIterations = minMaxIterations;
                    int ppindex = 0;
                    int ppmax = 256;
                    while (ppmax < packet.maxIterations) {
                      ppmax *= 2;
                      ppindex++;
                    }
                    iterationsConfigure.write (new Integer (ppindex));
                    gcSketch[1] = whiteCOLOR;
                    display.extend (gcSketch);
                    newPicture = false;                  // only changed packet.maxIterations
                    running = false;
                  break;
                  case KeyEvent.VK_W:
                    gcSketch[0] = whiteCOLOR;
                    targetConfigure.write (new Integer (0));
                  break;
                  case KeyEvent.VK_B:
                    gcSketch[0] = blackCOLOR;
                    targetConfigure.write (new Integer (1));
                  break;
/*
                  case KeyEvent.VK_X:
                    gcSketch[0] = whiteXOR;
                    targetConfigure.write (new Integer (2));
                  break;
*/
                  case KeyEvent.VK_S:
                    if (0 != currentColourModel) {
                      currentColourModel = 0;
                      mis[currentColourModel].newPixels ();
                      gcImage[0] = draw[currentColourModel];
                      display.change (gcImage, 0);
                    }
                    colourConfigure.write (new Integer (0));
                  break;
                  case KeyEvent.VK_F:
                    if (1 != currentColourModel) {
                      currentColourModel = 1;
                      mis[currentColourModel].newPixels ();
                      gcImage[0] = draw[currentColourModel];
                      display.change (gcImage, 0);
                    }
                    colourConfigure.write (new Integer (1));
                  break;
                }
              }
            break;
            case SCROLL_EVENT:
              final ItemEvent se = (ItemEvent) scrollChannel.read ();
              final String schoice = (String) se.getItem ();
              scrolling = 0;
              while (schoice != scrollMenu[scrolling]) scrolling++;
            break;
            case ITERATIONS_EVENT:
              final ItemEvent ie = (ItemEvent) iterationsChannel.read ();
              final String ichoice = (String) ie.getItem ();
              int iindex = 0;
              while (ichoice != iterationsMenu[iindex]) iindex++;
              packet.maxIterations = 256;
              for (int i = 0; i < iindex; i++) packet.maxIterations *= 2;
              System.out.println ("MaxIterations = " +  + packet.maxIterations);
              gcSketch[1] = whiteCOLOR;
              display.extend (gcSketch);
              newPicture = false;                  // only changed packet.maxIterations
              running = false;
            break;
            case TARGET_EVENT:
              final ItemEvent te = (ItemEvent) targetChannel.read ();
              final String tchoice = (String) te.getItem ();
              int tindex = 0;
              while (tchoice != targetMenu[tindex]) tindex++;
              switch (tindex) {
                case 0:
                  gcSketch[0] = whiteCOLOR;
                break;
                case 1:
                  gcSketch[0] = blackCOLOR;
                break;
/*
                case 2:
                  gcSketch[0] = whiteXOR;
                break;
*/
              }
            break;
            case COLOUR_EVENT:
              final ItemEvent ce = (ItemEvent) colourChannel.read ();
              final String cchoice = (String) ce.getItem ();
              int cindex = 0;
              while (cchoice != colourMenu[cindex]) cindex++;
              if (cindex != currentColourModel) {
                currentColourModel = cindex;
                mis[currentColourModel].newPixels ();
                gcImage[0] = draw[currentColourModel];
                display.change (gcImage, 0);
              }
            break;
            case FORWARD_EVENT:
              forwardChannel.read ();
              if (currentPax < maxPax) {     // in case the GUI was slow at disabling ...
                backwardConfigure.write (Boolean.TRUE);
                scrollConfigure.write (Boolean.FALSE);
                iterationsConfigure.write (Boolean.FALSE);
                targetConfigure.write (Boolean.FALSE);
                colourConfigure.write (Boolean.FALSE);
                fbOption (true);
              }
            break;
            case BACKWARD_EVENT:
              backwardChannel.read ();
              if (currentPax > 1) {          // in case the GUI was slow at disabling ...
                forwardConfigure.write (Boolean.TRUE);
                scrollConfigure.write (Boolean.FALSE);
                iterationsConfigure.write (Boolean.FALSE);
                targetConfigure.write (Boolean.FALSE);
                colourConfigure.write (Boolean.FALSE);
                currentPax--;
                packet.copy ((FarmPacket) pax.elementAt (currentPax - 1));
                mis[currentColourModel].newPixels ();
                infoConfigure[0].write ((new Double (packet.top)).toString ());
                infoConfigure[1].write ((new Double (packet.left)).toString ());
                infoConfigure[2].write ((new Double (packet.size/((double) width))).toString ());
                int pindex = 0;
                int pmax = 256;
                while (pmax < packet.maxIterations) {
                  pmax *= 2;
                  pindex++;
                }
                iterationsConfigure.write (new Integer (pindex));
                final long timeout = tim.read () + 100;
                tim.after (timeout);
                fbOption (false);
              }
            break;
          }
        break;
        case VISIBLE_BOX:
          switch (allControl.fairSelect ()) {
            case MOUSE_EVENT:
              mouseEvent = (MouseEvent) mouseChannel.read ();
              if (mouseEvent.getID () == MouseEvent.MOUSE_PRESSED) {
                int modifiers = mouseEvent.getModifiers ();
                if ((modifiers & InputEvent.BUTTON1_MASK) != 0) {
                  display.extend (gcSketch);
                  running = false;
                } else
                if ((modifiers & InputEvent.BUTTON3_MASK) != 0) {
                  gcImage[0] = draw[currentColourModel];
                  display.set (gcImage);
                  if (currentPax > 1) backwardConfigure.write (Boolean.TRUE);
                  if (currentPax < maxPax) forwardConfigure.write (Boolean.TRUE);
                  state = INITIAL;
                }
              }
            break;
            case MOUSE_MOTION_EVENT:
              mouseEvent = (MouseEvent) mouseMotionChannel.read ();
              if (mouseEvent.getID () == MouseEvent.MOUSE_MOVED) {
                box.move (mouseEvent);
                gcSketch[1] = box.makeGraphicsCommand ();
                display.change (gcSketch, 1);
              }
            break;
            case KEY_EVENT:
              keyEvent = (KeyEvent) keyChannel.read ();
              switch (keyEvent.getID ()) {
                case KeyEvent.KEY_PRESSED:
                  switch (keyEvent.getKeyCode ()) {
                    case KeyEvent.VK_UP:
                      box.zoomUp ();
                      gcSketch[1] = box.makeGraphicsCommand ();
                      display.change (gcSketch, 1);
                    break;
                    case KeyEvent.VK_DOWN:
                      box.zoomDown ();
                      gcSketch[1] = box.makeGraphicsCommand ();
                      display.change (gcSketch, 1);
                    break;
                  }
                break;
                case KeyEvent.KEY_RELEASED:
                  switch (keyEvent.getKeyCode ()) {
                    case KeyEvent.VK_ENTER:
                      // toGraphics.write (gpImage);
                      // romGraphics.read ();
                      display.extend (gcSketch);
                      running = false;
                    break;
                    case KeyEvent.VK_RIGHT:
                      packet.maxIterations *= 2;
                      if (packet.maxIterations > maxMaxIterations) packet.maxIterations = maxMaxIterations;
                      int pindex = 0;
                      int pmax = 256;
                      while (pmax < packet.maxIterations) {
                        pmax *= 2;
                        pindex++;
                      }
                      iterationsConfigure.write (new Integer (pindex));
                      System.out.println ("MaxIterations = " +  + packet.maxIterations);
                    break;
                    case KeyEvent.VK_LEFT:
                      packet.maxIterations /= 2;
                      if (packet.maxIterations < minMaxIterations) packet.maxIterations = minMaxIterations;
                      int ppindex = 0;
                      int ppmax = 256;
                      while (ppmax < packet.maxIterations) {
                        ppmax *= 2;
                        ppindex++;
                      }
                      iterationsConfigure.write (new Integer (ppindex));
                      System.out.println ("MaxIterations = " +  + packet.maxIterations);
                    break;
                    case KeyEvent.VK_W:
                      gcSketch[0] = whiteCOLOR;
                      targetConfigure.write (new Integer (0));
                      display.change (gcSketch, 1);
                    break;
                    case KeyEvent.VK_B:
                      gcSketch[0] = blackCOLOR;
                      targetConfigure.write (new Integer (1));
                      display.change (gcSketch, 1);
                    break;
/*
                    case KeyEvent.VK_X:
                      gcSketch[0] = whiteXOR;
                      targetConfigure.write (new Integer (2));
                      display.change (gcSketch, 1);
                    break;
*/
                    case KeyEvent.VK_S:
                      if (0 != currentColourModel) {
                        currentColourModel = 0;
                        mis[currentColourModel].newPixels ();
                        gcImage[0] = draw[currentColourModel];
                        display.change (gcImage, 0);
                      }
                      colourConfigure.write (new Integer (0));
                    break;
                    case KeyEvent.VK_F:
                      if (1 != currentColourModel) {
                        currentColourModel = 1;
                        mis[currentColourModel].newPixels ();
                        gcImage[0] = draw[currentColourModel];
                        display.change (gcImage, 0);
                      }
                      colourConfigure.write (new Integer (1));
                    break;
                  }
                break;
              }
            break;
            case SCROLL_EVENT:
              final ItemEvent se = (ItemEvent) scrollChannel.read ();
              final String schoice = (String) se.getItem ();
              scrolling = 0;
              while (schoice != scrollMenu[scrolling]) scrolling++;
            break;
            case ITERATIONS_EVENT:
              final ItemEvent ie = (ItemEvent) iterationsChannel.read ();
              final String ichoice = (String) ie.getItem ();
              int iindex = 0;
              while (ichoice != iterationsMenu[iindex]) iindex++;
              packet.maxIterations = 256;
              for (int i = 0; i < iindex; i++) packet.maxIterations *= 2;
              System.out.println ("MaxIterations = " +  + packet.maxIterations);
            break;
            case TARGET_EVENT:
              final ItemEvent te = (ItemEvent) targetChannel.read ();
              final String tchoice = (String) te.getItem ();
              int tindex = 0;
              while (tchoice != targetMenu[tindex]) tindex++;
              switch (tindex) {
                case 0:
                  gcSketch[0] = whiteCOLOR;
                break;
                case 1:
                  gcSketch[0] = blackCOLOR;
                break;
/*
                case 2:
                  gcSketch[0] = whiteXOR;
                break;
*/
              }
              display.change (gcSketch, 1);
            break;
            case COLOUR_EVENT:
              final ItemEvent ce = (ItemEvent) colourChannel.read ();
              final String cchoice = (String) ce.getItem ();
              int cindex = 0;
              while (cchoice != colourMenu[cindex]) cindex++;
              if (cindex != currentColourModel) {
                currentColourModel = cindex;
                mis[currentColourModel].newPixels ();
                gcImage[0] = draw[currentColourModel];
                display.change (gcImage, 0);
              }
            break;
            case FORWARD_EVENT:
              forwardChannel.read ();
            break;
            case BACKWARD_EVENT:
              backwardChannel.read ();
            break;
          }
        break;
      }
    }
    scrollConfigure.write (Boolean.FALSE);
    iterationsConfigure.write (Boolean.FALSE);
    targetConfigure.write (Boolean.FALSE);
    colourConfigure.write (Boolean.FALSE);
    forwardConfigure.write (Boolean.FALSE);
    backwardConfigure.write (Boolean.FALSE);
    if (newPicture) {
      packet.left += packet.size*(((double) (box.zoomX - box.dX))/((double) width));
      packet.top -= packet.size*(((double) (box.zoomY - box.dY))/((double) width));
      packet.size *= ((double) box.dX2)/((double) width);
    }
  }

  private void fbOption (final boolean forwards) {
    final FarmPacket npacket = (FarmPacket) pax.elementAt (currentPax);
    final double shrink = doubleWidth/packet.size;
    final double nwidth = npacket.size*shrink;
    gcSketch[1] = new GraphicsCommand.DrawRect (
      (int) ((npacket.left - packet.left)*shrink),
      (int) ((packet.top - npacket.top)*shrink),
      (int) nwidth,
      (int) (nwidth*aspectRatio)
    );
    display.extend (gcSketch);
    if (forwards) {
      forwardConfigure.write ("Confirm");
      backwardConfigure.write ("Cancel");
    } else {
      backwardConfigure.write ("Confirm");
      forwardConfigure.write ("Cancel");
    }
    if (currentPax == maxPax) forwardConfigure.write (Boolean.FALSE);
    switch (fbControl.fairSelect ()) {          // wait for user
      case FORWARD_EVENT:
        forwardChannel.read ();
        packet.copy (npacket);
        gcImage[0] = draw[currentColourModel];
        display.set (gcImage);
        mis[currentColourModel].newPixels ();
        currentPax++;
        int pindex = 0;
        int pmax = 256;
        while (pmax < packet.maxIterations) {
          pmax *= 2;
          pindex++;
        }
        iterationsConfigure.write (new Integer (pindex));
        infoConfigure[0].write ((new Double (packet.top)).toString ());
        infoConfigure[1].write ((new Double (packet.left)).toString ());
        infoConfigure[2].write ((new Double (packet.size/((double) width))).toString ());
      break;
      case BACKWARD_EVENT:
        backwardChannel.read ();
        gcImage[0] = draw[currentColourModel];
        display.set (gcImage);
      break;
    }
    forwardConfigure.write ("Forward");
    backwardConfigure.write ("Backward");
    scrollConfigure.write (Boolean.TRUE);
    iterationsConfigure.write (Boolean.TRUE);
    targetConfigure.write (Boolean.TRUE);
    colourConfigure.write (Boolean.TRUE);
    if (currentPax == 1) backwardConfigure.write (Boolean.FALSE);
    if (currentPax == maxPax) forwardConfigure.write (Boolean.FALSE);
  }

}
