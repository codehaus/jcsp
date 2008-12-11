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
import java.awt.event.*;

/**
 * @author P.H. Welch
 */
public class Infection implements CSProcess {

  private final int N_EVOLVERS = 8;

  // private int renderRate;
  
  private int renderEvery;
  private final String[] renderMenu;
  private final int[] renderLookup;
  private int renderChoiceIndex;

  private int infectRate;        // invariant: 0 <= infectRate <= 100 (or 127)
  private int convertRate;       // invariant: 0 <= convertRate <= 100 (or 127)
  private int recoverRate;       // invariant: 0 <= recoverRate <= 100 (or 127)
  private int reinfectRate;      // invariant: 0 <= reinfectRate <= 100 (or 127)

  private int sprayRadius;

  private CSTimer tim = new CSTimer ();     // frame-rate calculation fields
  private long firstFrameTime;
  private int nFrames = 0;
  private int fpsUpdate = 1;
  
  private final AltingChannelInput fromMouse;
  private final AltingChannelInput fromMouseMotion;

  private final AltingChannelInput resetEvent;
  private final ChannelOutput resetConfigure;

  private final AltingChannelInput freezeEvent;
  private final ChannelOutput freezeConfigure;

  // private final AltingChannelInputInt renderRateBarEvent;
  // private final ChannelOutput renderRateBarConfigure;

  private final AltingChannelInput renderChoiceEvent;
  private final ChannelOutput renderChoiceConfigure;

  private final AltingChannelInputInt infectRateBarEvent;
  private final ChannelOutput infectRateBarConfigure;

  private final AltingChannelInputInt convertRateBarEvent;
  private final ChannelOutput convertRateBarConfigure;
  
  private final AltingChannelInputInt recoverRateBarEvent;
  private final ChannelOutput recoverRateBarConfigure;
  
  private final ChannelOutput fpsConfigure;
  private final ChannelOutput infectedConfigure;
  private final ChannelOutput deadConfigure;
  
  // private final ChannelOutput renderRateLabelConfigure;
  private final ChannelOutput infectRateLabelConfigure;
  private final ChannelOutput convertRateLabelConfigure;
  private final ChannelOutput recoverRateLabelConfigure;
  
  private final ChannelOutput toGraphics;
  private final ChannelInput fromGraphics;

  private final AltingChannelInput resizeEvent;

  public Infection (final int infectRate,
                    final int convertRate,
                    final int recoverRate,
                    final int reinfectRate,
                    final int renderChoiceIndex,
                    final int sprayRadius,
                    final AltingChannelInput fromMouse,
                    final AltingChannelInput fromMouseMotion,
                    final AltingChannelInput resetEvent,
                    final ChannelOutput resetConfigure,
                    final AltingChannelInput freezeEvent,
                    final ChannelOutput freezeConfigure,
                    // final AltingChannelInputInt renderRateBarEvent,
                    // final ChannelOutput renderRateBarConfigure,
                    final AltingChannelInputInt infectRateBarEvent,
                    final ChannelOutput infectRateBarConfigure,
                    final AltingChannelInputInt convertRateBarEvent,
                    final ChannelOutput convertRateBarConfigure,
                    final AltingChannelInputInt recoverRateBarEvent,
                    final ChannelOutput recoverRateBarConfigure,
                    final ChannelOutput fpsConfigure,
                    final ChannelOutput infectedConfigure,
                    final ChannelOutput deadConfigure,
                    final AltingChannelInput renderChoiceEvent,
                    final ChannelOutput renderChoiceConfigure,
                    final String[] renderMenu,
                    final int[] renderLookup,
                    // final ChannelOutput renderRateLabelConfigure,
                    final ChannelOutput infectRateLabelConfigure,
                    final ChannelOutput convertRateLabelConfigure,
                    final ChannelOutput recoverRateLabelConfigure,
                    final ChannelOutput toGraphics,
                    final ChannelInput fromGraphics,
                    final AltingChannelInput resizeEvent) {

    // this.renderRate = 100;
    this.infectRate = infectRate;
    this.convertRate = convertRate; // 80;
    this.recoverRate = recoverRate; // 99;
    this.reinfectRate = reinfectRate; // 10;
    this.renderChoiceIndex = renderChoiceIndex; // 1;
    this.sprayRadius = sprayRadius; // 20;
    this.fromMouse = fromMouse;
    this.fromMouseMotion = fromMouseMotion;
    this.resetEvent = resetEvent;
    this.resetConfigure = resetConfigure;
    this.freezeEvent = freezeEvent;
    this.freezeConfigure = freezeConfigure;
    // this.renderRateBarEvent = renderRateBarEvent;
    // this.renderRateBarConfigure = renderRateBarConfigure;
    this.infectRateBarEvent = infectRateBarEvent;
    this.infectRateBarConfigure = infectRateBarConfigure;
    this.convertRateBarEvent = convertRateBarEvent;
    this.convertRateBarConfigure = convertRateBarConfigure;
    this.recoverRateBarEvent = recoverRateBarEvent;
    this.recoverRateBarConfigure = recoverRateBarConfigure;
    this.fpsConfigure = fpsConfigure;
    this.infectedConfigure = infectedConfigure;
    this.deadConfigure = deadConfigure;
    this.renderChoiceEvent = renderChoiceEvent;
    this.renderChoiceConfigure = renderChoiceConfigure;
    this.renderMenu = renderMenu;
    this.renderLookup = renderLookup;
    // this.renderRateLabelConfigure = renderRateLabelConfigure;
    this.infectRateLabelConfigure = infectRateLabelConfigure;
    this.convertRateLabelConfigure = convertRateLabelConfigure;
    this.recoverRateLabelConfigure = recoverRateLabelConfigure;
    this.toGraphics = toGraphics;
    this.fromGraphics = fromGraphics;
    this.resizeEvent = resizeEvent;
  }

  // //     colours          :       Cell.GREEN    Cell.INFECTED    Cell.DEAD
  // //     -------                  ----------    -------------    ---------
  //  
  // private final byte[] reds   = { (byte)0x00,    (byte)0xff,    (byte)0x00};
  // private final byte[] greens = { (byte)0xff,    (byte)0x00,    (byte)0x00};
  // private final byte[] blues  = { (byte)0x00,    (byte)0x00,    (byte)0xff};

  private byte[] pixels;                     // pixel array of Cell matrix

  private byte[][] cell, last_cell;          // matrix of Cells (plus spare)

  // Note: we will maintain in last_cell the previous state of the Cells.

  private int width, height;

  private int[] count = new int[Cell.N_STATES];  // how many in each cell state

  private final static int IDLE = 0, RUNNING = 2, FROZEN = 3, RESET = 4;

  //  IDLE     <==>  "reset"  "FREEZE"    all green           not running
  //  RUNNING  <==>  "reset"  "FREEZE"    some infected/dead  running
  //  FROZEN   <==>  "RESET"  "UNFREEZE"  some infected/dead  not running
  //  RESET    <==>  "reset"  "UNFREEZE"  all green           not running
    
  private int state = IDLE;

  private final static int RESET_EVENT = 0, FREEZE_EVENT = 1;
  private final static int RENDER_CHOICE = 2, INFECT_RATE = 3, CONVERT_RATE = 4, RECOVER_RATE = 5;
  private final static int MOUSE = 6, MOUSE_MOTION = 7, RESIZE_EVENT = 8, SKIP = 9;

  private Guard[] guard;
                           
  private boolean[] preCondition;
  
  private Spray spray;

  //     private methods
  //     -----------------

  /*
  private ColorModel createColorModel () {
    return new IndexColorModel (2, 3, reds, greens, blues);
  }
  */

  private ColorModel createColorModel () {

    byte[] red = new byte[256];
    byte[] green = new byte[256];
    byte[] blue = new byte[256];

    red[Cell.GREEN] = 0;  green[Cell.GREEN] = (byte) 255; blue[Cell.GREEN] = 0;
    red[Cell.INFECTED] = (byte) 255;  green[Cell.INFECTED] = 0; blue[Cell.INFECTED] = 0;
    red[Cell.DEAD] = 0;  green[Cell.DEAD] = 0; blue[Cell.DEAD] = (byte) 255;

    final int nBitsOfColour = 8;
    return new IndexColorModel (nBitsOfColour, red.length, red, green, blue);

  }

  private final Rand random = new Rand ();

  private void initialisePixels () {
    for (int ij = 0; ij < pixels.length; ij++) {
      pixels[ij] = Cell.GREEN;
    }
    for (int j = 0; j < height; j++) {
      for (int i = 0; i < width; i++) {
        cell[j][i] = Cell.GREEN;
      }
    }
    count[Cell.GREEN] = height*width;
    count[Cell.INFECTED] = 0;
    count[Cell.DEAD] = 0;
    infectedConfigure.write (String.valueOf (count[Cell.INFECTED]));
    deadConfigure.write (String.valueOf (count[Cell.DEAD]));
  }

  private void pixelise () {
    int i0 = 0;
    for (int i = 0; i < cell.length; i++) {
      System.arraycopy (cell[i], 0, pixels, i0, width);
      i0 = i0 + width;
    }
  }

  private void byteMatrixCopy (final byte[][] from, final byte[][] to) {
    // assume: from and to are non-null and equally sized ...
    for (int i = 0; i < from.length; i++) {
      System.arraycopy (from[i], 0, to[i], 0, from[i].length);  // fast copy
    }
  }

  private void genByteMatrixCopy (final byte[][] from, final byte[][] to) {
    // assume: from and to are non-null ...
    final int rows = (from.length < to.length) ? from.length : to.length;
    final int cols = (from[0].length < to[0].length) ? from[0].length : to[0].length;
    for (int i = 0; i < rows; i++) {
      System.arraycopy (from[i], 0, to[i], 0, cols);  // fast copy
    }
  }

  private void infect (int i, int j) {     // possibly infect Cell[i][j]
    i = (i < 0) ? i + height : (i >= height) ? i - height : i;
    j = (j < 0) ? j + width : (j >= width) ? j - width : j;
    if (last_cell[i][j] == Cell.GREEN) {
      if (random.bits7 () < infectRate) {
        count[cell[i][j]]--;
        cell[i][j] = Cell.INFECTED;
        count[Cell.INFECTED]++;
      }
    }
  }

  // the following method has been replaced by the Evolve process
  // (instances of which are run in parallel)

  private void evolve () {      // evolves the forest forward forward one cycle
    for (int i = 0; i < height; i++) {
      final byte[] last_row_i = last_cell[i];
      final byte[] row_i = cell[i];
      for (int j = 0; j < width; j++) {
        switch (last_row_i[j]) {
          // case Cell.GREEN:
          // break;
          case Cell.INFECTED:
            infect (i + 1, j);
            infect (i - 1, j);
            infect (i, j + 1);
            infect (i, j - 1);
            if ((i % 2) == 0) {
              infect (i + 1, j + 1);
              infect (i - 1, j - 1);
            } else {
              infect (i - 1, j + 1);
              infect (i + 1, j - 1);
            }
            if (random.bits7 () < convertRate) {
              row_i[j] = Cell.DEAD;
              count[Cell.INFECTED]--;
              count[Cell.DEAD]++;
            }
          break;
          case Cell.DEAD:
            if (random.bits7 () < recoverRate) {
              if (random.bits16 () < reinfectRate) {
                row_i[j] = Cell.INFECTED;
                count[Cell.DEAD]--;
                count[Cell.INFECTED]++;
              } else {
                row_i[j] = Cell.GREEN;
                count[Cell.DEAD]--;
                count[Cell.GREEN]++;
              }
            }
          break;
        }
      }
    }
    pixelise ();
  }

  // report sets results to the number of GREEN, INFECTED and DEAD cells
  // (respectively) in the forest.  It assumes results has a length of 3.

  private void report (final int[] results) {
    results[Cell.GREEN] = 0;
    results[Cell.INFECTED] = 0;
    results[Cell.DEAD] = 0;
    for (int i = 0; i < height; i++) {
      final byte[] row_i = cell[i];
      for (int j = 0; j < width; j++) {
        results[row_i[j]]++;
      }
    }
  }

  private void handle (final Point point, final byte newCellState,
                       final boolean spraying) {
    if (spraying) {
      System.out.println ("Spraying ... " + newCellState + " ... " + point);
      spray.zap (point, newCellState);
    } else {
      System.out.print ("Spotting ... " + newCellState + " ... " + point);
      int i = point.y;
      int j = point.x;
      while (i < 0) i += height;            // mostly won't happen or
      while (i >= height) i -= height;      // will happen only once.
      while (j < 0) j += width;             //         ditto.
      while (j >= width) j -= width;        //         ditto.
      System.out.println (j + ", " + i + ")");
      // if ((0 <= i) && (i < height) && (0 <= j) && (j < width)) {
      byte[] cellRow = cell[i];
      final byte current = cellRow[j];
      if (current != Cell.INFECTED){
        pixels[(i*width) + j] = newCellState;
        count[current]--;
        cellRow[j] = newCellState;
        count[newCellState]++;
      }
      // }
    }
    final int notGreen = count[Cell.INFECTED] + count[Cell.DEAD];
    infectedConfigure.write (String.valueOf (count[Cell.INFECTED]));
    deadConfigure.write (String.valueOf (count[Cell.DEAD]));
    switch (state) {
      case IDLE:
        if (notGreen > 0) {
          preCondition[SKIP] = true;
          nFrames = 0;
          fpsUpdate = 1;
          firstFrameTime = tim.read ();
          state = RUNNING;
        }
      break;
      case RUNNING:
        if (notGreen == 0) {
          preCondition[SKIP] = false;
          state = IDLE;
        }
      break;
      case FROZEN:
        if (notGreen == 0) {
          resetConfigure.write (Boolean.FALSE);
          resetConfigure.write ("reset");
          preCondition[RESET_EVENT] = false;
          state = RESET;
        }
      break;
      case RESET:
        if (notGreen > 0) {
          while (resetEvent.pending ()) resetEvent.read ();
          resetConfigure.write (Boolean.TRUE);
          resetConfigure.write ("RESET");
          preCondition[RESET_EVENT] = true;
          state = FROZEN;
        }
      break;
    }
  }

/*
  private int convertRateToEvery (int rate) {
    switch (rate/10) {
      case 0:
        return Integer.MAX_VALUE;
      case 1:
        return  256;
      case 2:
        return  128;
      case 3:
        return  64;
      case 4:
        return  32;
      case 5:
        return  16;
      case 6:
        return  8;
      case 7:
        return  4;
      case 8:
        return  2;
      case 9: case 10:
        return  1;
    }
    return Integer.MAX_VALUE;      // error if we get here!
  }
*/

  public void run () {

    fpsConfigure.write ("0.0");
    infectedConfigure.write ("0");
    deadConfigure.write ("0");
    
    // renderRateLabelConfigure.write ("1 in " + renderEvery);

    infectRateBarConfigure.write (new Integer (100 - infectRate));
    convertRateBarConfigure.write (new Integer (100 - convertRate));
    recoverRateBarConfigure.write (new Integer (100 - recoverRate));

    infectRateLabelConfigure.write (infectRate + " %");
    convertRateLabelConfigure.write (convertRate + " %");
    recoverRateLabelConfigure.write (recoverRate + " %");
    
    infectRate = ((infectRate*128) + 64)/100;
    convertRate = ((convertRate*128) + 64)/100;
    recoverRate = ((recoverRate*128) + 64)/100;
    
    convertRate = 128 - convertRate;
    recoverRate = 128 - recoverRate;
    
    resetConfigure.write (Boolean.FALSE);
    resetConfigure.write ("reset");
    
    freezeConfigure.write (Boolean.TRUE);
    freezeConfigure.write ("FREEZE");
    
    // renderRateBarConfigure.write (Boolean.TRUE);
    renderChoiceConfigure.write (Boolean.TRUE);
    renderChoiceConfigure.write (new Integer (renderChoiceIndex));
    renderEvery = renderLookup[renderChoiceIndex];
    int cycle = renderEvery;

    infectRateBarConfigure.write (Boolean.TRUE);
    convertRateBarConfigure.write (Boolean.TRUE);
    recoverRateBarConfigure.write (Boolean.TRUE);

    toGraphics.write (GraphicsProtocol.GET_DIMENSION);
    Dimension graphicsDim = (Dimension) fromGraphics.read ();
    System.out.println ("Infection: graphics dimension = " + graphicsDim);

    width = graphicsDim.width;
    height = graphicsDim.height;

    pixels = new byte[width*height];

    cell = new byte[height][width];
    last_cell = new byte[height][width];

    ColorModel model = createColorModel ();

    MemoryImageSource mis = new MemoryImageSource (width, height, model, pixels, 0, width);
    mis.setAnimated (true);
    mis.setFullBufferUpdates (true);

    toGraphics.write (new GraphicsProtocol.MakeMISImage (mis));
    Image image = (Image) fromGraphics.read ();

    final DisplayList display = new DisplayList ();
    toGraphics.write (new GraphicsProtocol.SetPaintable (display));
    fromGraphics.read ();

    final GraphicsCommand[] drawImage = {new GraphicsCommand.DrawImage (image, 0, 0)};
    display.set (drawImage);

    final Thread me = Thread.currentThread ();
    System.out.println ("Infection priority = " + me.getPriority ());
    me.setPriority (Thread.MIN_PRIORITY);
    System.out.println ("Infection priority = " + me.getPriority ());

    guard =
      new Guard[] {
        resetEvent, freezeEvent,
        renderChoiceEvent, infectRateBarEvent, convertRateBarEvent,
        recoverRateBarEvent, fromMouse, fromMouseMotion, resizeEvent, new Skip ()
      };
                           
    preCondition = new boolean[] {false, true, true, true, true, true, true, false, true, false};

    boolean spraying = false;
    boolean controlled = false;
    boolean mousePressed = false;
    byte newCellState = Cell.GREEN;

    spray = new Spray (sprayRadius, cell, pixels, count);

    final Alternative alt = new Alternative (guard);

    initialisePixels ();
    mis.newPixels ();

    Evolve[] evolvers = new Evolve[N_EVOLVERS];
    // final int nRows = height/N_EVOLVERS;
    int startRow = 0;
    long seed = System.currentTimeMillis ();
    for (int i = 0; i < evolvers.length; i++) {
      int nextStartRow = (height*(i + 1))/N_EVOLVERS;
      // evolvers[i] = new Evolve (i*nRows, nRows, cell, last_cell, pixels, seed + (i*1000));
      evolvers[i] = new Evolve (startRow, nextStartRow, cell, last_cell, pixels, seed + (i*1000));
      evolvers[i].infectRate = infectRate;
      evolvers[i].recoverRate = recoverRate;
      evolvers[i].reinfectRate = reinfectRate;
      evolvers[i].convertRate = convertRate;
      startRow = nextStartRow;
    }

    CSProcess parEvolve = new Parallel (evolvers);

    while (true) {
      switch (alt.fairSelect (preCondition)) {
        case RESET_EVENT:
          System.out.println ("Infection: reset event ...");
          resetEvent.read ();
          // assert : state == FROZEN
          resetConfigure.write (Boolean.FALSE);
          resetConfigure.write ("reset");
          preCondition[RESET_EVENT] = false;
          state = RESET;
          System.out.println ("Infection: reset");
          initialisePixels ();
          mis.newPixels ();
          fpsConfigure.write ("0.0");
        break;
        case FREEZE_EVENT:
          freezeEvent.read ();
          switch (state) {
            case IDLE:
              System.out.println ("Infection: freeze");
              freezeConfigure.write ("UNFREEZE");
              state = RESET;
            break;
            case RUNNING:
              System.out.println ("Infection: freeze");
              freezeConfigure.write ("UNFREEZE");
              while (resetEvent.pending ()) resetEvent.read ();
              resetConfigure.write (Boolean.TRUE);
              resetConfigure.write ("RESET");
              preCondition[RESET_EVENT] = true;
              preCondition[SKIP] = false;
              state = FROZEN;
            break;
            case FROZEN:
              System.out.println ("Infection: unfreeze");
              freezeConfigure.write ("FREEZE");
              resetConfigure.write (Boolean.FALSE);
              resetConfigure.write ("reset");
              preCondition[RESET_EVENT] = false;
              preCondition[SKIP] = true;
              nFrames = 0;
              fpsUpdate = 1;
              firstFrameTime = tim.read ();
              state = RUNNING;
            break;
            case RESET:
              System.out.println ("Infection: unfreeze");
              freezeConfigure.write ("FREEZE");
              state = IDLE;
            break;
          }
        break;
        case RENDER_CHOICE:
          final ItemEvent re = (ItemEvent) renderChoiceEvent.read ();
          final String rchoice = (String) re.getItem ();
          int rindex = 0;
          while (!rchoice.equals (renderMenu[rindex])) rindex++;
          renderEvery = renderLookup[rindex];
          // System.out.println ("RENDER_CHOICE: " + rchoice + " --> " + renderEvery);
          cycle = renderEvery;
          /*
          renderRate = 100 - renderRateBarEvent.read ();
          // renderEvery = (renderRate == 0) ? Integer.MAX_VALUE : 100/renderRate;
          // cycle = renderEvery;
          // renderRateLabelConfigure.write (renderRate));
          renderEvery = convertRateToEvery (renderRate);
          if (renderEvery == Integer.MAX_VALUE) {
            renderRateLabelConfigure.write ("0");
          } else {
            renderRateLabelConfigure.write ("1 in " + renderEvery);
          }
          */
        break;
        case INFECT_RATE:
          infectRate = 100 - infectRateBarEvent.read ();
          infectRateLabelConfigure.write (infectRate + " %");
          infectRate = ((infectRate*128) + 64)/100;
          for (int i = 0; i < evolvers.length; i++) {
            evolvers[i].infectRate = infectRate;
          }
        break;
        case CONVERT_RATE:
          convertRate = 100 - convertRateBarEvent.read ();
          convertRateLabelConfigure.write (convertRate + " %");
          convertRate = ((convertRate*128) + 64)/100;
          convertRate = 128 - convertRate;
          for (int i = 0; i < evolvers.length; i++) {
            evolvers[i].convertRate = convertRate;
          }
        break;
        case RECOVER_RATE:
          recoverRate = 100 - recoverRateBarEvent.read ();
          recoverRateLabelConfigure.write (recoverRate + " %");
          recoverRate = ((recoverRate*128) + 64)/100;
          recoverRate = 128 - recoverRate;
          for (int i = 0; i < evolvers.length; i++) {
            evolvers[i].recoverRate = recoverRate;
          }
        break;
        case MOUSE:
          final MouseEvent event = (MouseEvent) fromMouse.read ();
          switch (event.getID ()) {
            case MouseEvent.MOUSE_PRESSED:
              if (controlled) {
                controlled = false;
                preCondition[MOUSE_MOTION] = false;
                if (spraying) {
                  spray.setMask ();
                  spraying = false;
                }
              } else {
                mousePressed = true;
                while (fromMouseMotion.pending ()) fromMouseMotion.read ();
                preCondition[MOUSE_MOTION] = true;
                int modifiers = event.getModifiers ();
                if ((modifiers & InputEvent.BUTTON1_MASK) != 0) {
                  newCellState = Cell.INFECTED;
                } else if ((modifiers & InputEvent.BUTTON2_MASK) != 0) {
                  newCellState = Cell.GREEN;
                } else {
                  newCellState = Cell.DEAD;
                }
                // System.out.println ("MOUSE_PRESSED modifiers = " + modifiers);
                // System.out.println ("SHIFT_MASK = " + InputEvent.SHIFT_MASK);
                // System.out.println ("CTRL_MASK = " + InputEvent.CTRL_MASK);
                // System.out.println ("META_MASK = " + InputEvent.META_MASK);
                // System.out.println ("ALT_MASK = " + InputEvent.ALT_MASK);
                // System.out.println ("BUTTON1_MASK = " + InputEvent.BUTTON1_MASK);
                // System.out.println ("BUTTON2_MASK = " + InputEvent.BUTTON2_MASK);
                // System.out.println ("BUTTON3_MASK = " + InputEvent.BUTTON3_MASK);
                spraying = ((modifiers & InputEvent.SHIFT_MASK) != 0);
                if (newCellState != Cell.GREEN) {  // CTRL_MASK doesn't work on BUTTON2 (JDK1.1/2/3)
                  controlled = ((modifiers & InputEvent.CTRL_MASK) != 0);
                }
                handle (event.getPoint (), newCellState, spraying);
                mis.newPixels ();
              }
            break;
            case MouseEvent.MOUSE_RELEASED:
              mousePressed = false;
              if (! controlled) {
                preCondition[MOUSE_MOTION] = false;
                if (spraying) {
                  spray.setMask ();
                  spraying = false;
                }
              }
            break;
          }
        break;
        case MOUSE_MOTION:
          final MouseEvent motion = (MouseEvent) fromMouseMotion.read ();
          switch (motion.getID ()) {
            case MouseEvent.MOUSE_MOVED:
              if (controlled) {
                handle (motion.getPoint (), newCellState, spraying);
                mis.newPixels ();
              }
            case MouseEvent.MOUSE_DRAGGED:
              if (mousePressed) {
                handle (motion.getPoint (), newCellState, spraying);
                mis.newPixels ();
              }
            break;
          }
        break;
        case RESIZE_EVENT:
          // get the new canvas dimensions
          ComponentEvent e = (ComponentEvent) resizeEvent.read ();
          if (e.getID () != ComponentEvent.COMPONENT_RESIZED) break;
          toGraphics.write (GraphicsProtocol.GET_DIMENSION);
          graphicsDim = (Dimension) fromGraphics.read ();
          System.out.println ("Infection: graphics dimension = " + graphicsDim);
          width = graphicsDim.width;
          height = graphicsDim.height;
          // make new cell matrices
          byte[][] tmp_cell = new byte[height][width];
          genByteMatrixCopy (cell, tmp_cell);
          cell = tmp_cell;
          last_cell = new byte[height][width];
          // correct the counts and show them
          report (count);
          // having trouble with the size of these info boxes ... ??!
          // infectedConfigure.write ("XXXXXXXXX");
          // deadConfigure.write ("XXXXXXXXX");
          // fpsConfigure.write ("XXXXXXXXX");
          infectedConfigure.write (String.valueOf (count[Cell.INFECTED]));
          deadConfigure.write (String.valueOf (count[Cell.DEAD]));
          // make new pixel array
          pixels = new byte[width*height];
          pixelise ();
          // inform all the evolvers
          startRow = 0;
          for (int i = 0; i < evolvers.length; i++) {
            int nextStartRow = (height*(i + 1))/N_EVOLVERS;
            evolvers[i].resize (startRow, nextStartRow, cell, last_cell, pixels);
            startRow = nextStartRow;
          }
          // inform the spray
          spray.resize (sprayRadius, cell, pixels, count);
          // make the new colour model and image and set in the display list
          model = createColorModel ();
          mis = new MemoryImageSource (width, height, model, pixels, 0, width);
          mis.setAnimated (true);
          mis.setFullBufferUpdates (true);
          toGraphics.write (new GraphicsProtocol.MakeMISImage (mis));
          image = (Image) fromGraphics.read ();
          drawImage[0] = new GraphicsCommand.DrawImage (image, 0, 0);
          // redraw
          display.set (drawImage);
        break;
        case SKIP:
          // assert : state == RUNNING
          byteMatrixCopy (cell, last_cell);
          // evolve ();          // sequential version (instead of next 6 lines)
          parEvolve.run ();
          for (int i = 0; i < evolvers.length; i++) {
            for (int j = 0; j < count.length; j++) {
              count[j] += evolvers[i].count[j];
            }
          }
          // render
          cycle--;
          if (cycle == 0) {
            mis.newPixels ();
            cycle = renderEvery;
          }
          // redisplay counts
          nFrames++;
          if (nFrames == fpsUpdate) {
            final long thisFrameTime = tim.read ();
            final int period = (int) (thisFrameTime - firstFrameTime);
            int framesPerTenSeconds = (period == 0) ? 0 : (nFrames*10000) / period;
            fpsConfigure.write (framesPerTenSeconds/10 + "." + framesPerTenSeconds%10);
            fpsUpdate = framesPerTenSeconds/20;
            if (fpsUpdate == 0) fpsUpdate = 1;
            firstFrameTime = thisFrameTime;
            nFrames = 0;
            infectedConfigure.write (String.valueOf (count[Cell.INFECTED]));
            deadConfigure.write (String.valueOf (count[Cell.DEAD]));
          }
          // anything left to do?
          final int notGreen = count[Cell.INFECTED] + count[Cell.DEAD];
          if (notGreen == 0) {
            System.out.println ("Infection: all green ...");
            preCondition[SKIP] = false;   // no infection and no dead cells => stop computing!
            state = IDLE;
            infectedConfigure.write ("0");
            deadConfigure.write ("0");
            mis.newPixels ();
          }
        break;
      }  
    }

  }

}
