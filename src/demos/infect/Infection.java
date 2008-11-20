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

package org.jcsp.demos.infect;

import org.jcsp.lang.*;
import org.jcsp.awt.*;
import java.awt.*;
import java.awt.image.*;
import java.util.Random;

/**
 * @author P.H. Welch
 */
public class Infection implements CSProcess {

  protected int rate;

  private final AltingChannelInput in;

  private final AltingChannelInputInt scrollEvent;

  private final ChannelOutput feedBack, scrollConfigure, infoConfigure, rateConfigure;

  private final ChannelOutput toGraphics;
  private final ChannelInput fromGraphics;

  public Infection (final int rate,
                    final AltingChannelInput in,
                    final AltingChannelInputInt scrollEvent,
                    final ChannelOutput scrollConfigure,
                    final ChannelOutput infoConfigure,
                    final ChannelOutput rateConfigure,
                    final ChannelOutput feedBack,
                    final ChannelOutput toGraphics,
                    final ChannelInput fromGraphics) {

    this.rate = rate;
    this.scrollEvent = scrollEvent;
    this.scrollConfigure = scrollConfigure;
    this.infoConfigure = infoConfigure;
    this.rateConfigure = rateConfigure;
    this.in = in;
    this.feedBack = feedBack;
    this.toGraphics = toGraphics;
    this.fromGraphics = fromGraphics;
  }

  //     colours          :   Tree.green    Tree.infected    Tree.dead
  //     -------              ----------    -------------    ---------
  
  protected final byte[] reds   = { (byte)0x00,    (byte)0xff,    (byte)0x00};
  protected final byte[] greens = { (byte)0xff,    (byte)0x00,    (byte)0x00};
  protected final byte[] blues  = { (byte)0x00,    (byte)0x00,    (byte)0xff};

  //     pixel array and key run-time parameters
  //     ---------------------------------------

  protected byte[] pixels;                     // pixel array of Tree matrix

  protected byte[][] tree, last_tree;          // matrix of Trees (plus spare)

  // Note: we will maintain in last_tree the previous state of the trees
  // in this Forest.

  protected int width, height;

  protected int[] count = new int[Tree.nStates];  // how many in each state

  //     protected methods
  //     -----------------

  protected ColorModel createColorModel () {
    return new IndexColorModel (2, 3, reds, greens, blues);
  }

  protected final Random random = new Random ();

  protected final int range (int n) {
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

  protected void initialisePixels () {
    for (int ij = 0; ij < pixels.length; ij++) {
      pixels[ij] = Tree.green;
    }
    for (int j = 0; j < height; j++) {
      for (int i = 0; i < width; i++) {
        tree[j][i] = Tree.green;
      }
    }
    count[Tree.green] = height*width;
    count[Tree.infected] = 0;
    count[Tree.dead] = 0;
    infoConfigure.write ((new Integer (count[Tree.infected] +
                                       count[Tree.dead])).toString ());
  }

  protected void centrePixel () {
    int i = width/2;
    int j = height/2;
    pixels[(j*width) + i] = Tree.infected;
    count[tree[j][i]]--;
    tree[j][i] = Tree.infected;
    count[Tree.infected]++;
    infoConfigure.write ((new Integer (count[Tree.infected] +
                                       count[Tree.dead])).toString ());
  }

  protected void randomPixel () {
    int i = range (width);
    int j = range (height);
    pixels[(j*width) + i] = Tree.infected;
    count[tree[j][i]]--;
    tree[j][i] = Tree.infected;
    count[Tree.infected]++;
    infoConfigure.write ((new Integer (count[Tree.infected] +
                                       count[Tree.dead])).toString ());
  }

  protected void pixelise () {
    int i0 = 0;
    for (int i = 0; i < tree.length; i++) {
      System.arraycopy (tree[i], 0, pixels, i0, width);
      i0 = i0 + width;
    }
  }

  protected void byteMatrixCopy (byte[][] from, byte[][] to) {
    // assume: from and to are equally sized ...
    for (int i = 0; i < from.length; i++) {
      System.arraycopy (from[i], 0, to[i], 0, from[i].length);  // fast copy
    }
  }

  protected void infect (int i, int j) {     // possibly infect Tree[i][j]
    if ((0 <= i) && (i < height) && (0 <= j) && (j < width)) {
      if (last_tree[i][j] == Tree.green) {
        if (range (100) < rate) {
          count[tree[i][j]]--;
          tree[i][j] = Tree.infected;
          count[Tree.infected]++;
        }
      }
    }
  }

  protected void evolve () {                 // evolves the forest forward
    byteMatrixCopy (tree, last_tree);        // forward one cycle
    for (int i = 0; i < height; i++) {
      final byte[] last_row_i = last_tree[i];
      final byte[] row_i = tree[i];
      for (int j = 0; j < width; j++) {
        switch (last_row_i[j]) {
          case Tree.green:
            break;
          case Tree.infected:
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
            row_i[j] = Tree.dead;
            count[Tree.infected]--;
            count[Tree.dead]++;
            break;
          case Tree.dead:
            row_i[j] = Tree.green;
            count[Tree.dead]--;
            count[Tree.green]++;
        }
      }
    }
  }

  // report sets results to the number of green, infected and dead trees
  // (respectively) in the forest.  It assumes results has a length of 3.
  //
  // Not used any more -- since these counts are maintained automatically.

  protected void report (int[] results) {
    results[Tree.green] = 0;
    results[Tree.infected] = 0;
    results[Tree.dead] = 0;
    for (int i = 0; i < height; i++) {
      final byte[] row_i = tree[i];
      for (int j = 0; j < width; j++) {
        results[row_i[j]]++;
      }
    }
  }

  protected String rateString () {
    if (rate < 10) return "               " + rate;
    if (rate < 100) return "              " + rate;
    return "             100";
  }

  public void run () {

    infoConfigure.write ((new Integer (0)).toString ());
    rateConfigure.write ((new Integer (rate)).toString ());
    scrollConfigure.write (Boolean.TRUE);

    toGraphics.write (GraphicsProtocol.GET_DIMENSION);
    final Dimension graphicsDim = (Dimension) fromGraphics.read ();
    System.out.println ("Infection: graphics dimension = " + graphicsDim);

    width = graphicsDim.width;
    height = graphicsDim.height;

    pixels = new byte[width*height];

    tree = new byte[height][width];
    last_tree = new byte[height][width];

    final ColorModel model = createColorModel ();

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
    System.out.println ("Infection priority = " + me.getPriority ());
    me.setPriority (Thread.MIN_PRIORITY);
    System.out.println ("Infection priority = " + me.getPriority ());

    int state = InfectionControl.reset;
    
    final int STATS_INTERVAL = 1;
    int countDown = STATS_INTERVAL;

    final Guard[] guard = {in, scrollEvent, new Skip ()};
    final boolean[] preCondition = {true, true, false};
    final int NEW_STATE = 0;
    final int SCROLL = 1;
    final int RUNNING = 2;

    final Alternative alt = new Alternative (guard);

    initialisePixels ();
    mis.newPixels ();

    while (true) {
      switch (alt.priSelect (preCondition)) {
        case NEW_STATE:
          state = ((Integer) in.read ()).intValue ();
          switch (state) {
            case InfectionControl.reset:
              System.out.println ("Infection: reset");
              initialisePixels ();
              mis.newPixels ();
              preCondition[RUNNING] = false;
            break;
            case InfectionControl.random:
              System.out.println ("Infection: random");
              randomPixel ();
              mis.newPixels ();
              preCondition[RUNNING] = false;
            break;
            case InfectionControl.centre:
              System.out.println ("Infection: centre");
              centrePixel ();
              mis.newPixels ();
              preCondition[RUNNING] = false;
            break;
            case InfectionControl.running:
              System.out.println ("Infection: running");
              preCondition[RUNNING] = true;
            break;
            case InfectionControl.frozen:
              System.out.println ("Infection: frozen");
              preCondition[RUNNING] = false;
            break;
          }
        break;
        case SCROLL:
          rate = 100 - scrollEvent.read ();
          rateConfigure.write ((new Integer (rate)).toString ());
          System.out.println ("Infection: scrolling ... " + rate);
        break;
        case RUNNING:
          evolve ();
          pixelise ();
          mis.newPixels ();
          if ((count[Tree.infected] + count[Tree.dead]) == 0) {
            infoConfigure.write ((new Integer (count[Tree.infected] +
                                               count[Tree.dead])).toString ());
            // System.out.println ("Infection: all healthy !!!");
            preCondition[RUNNING] = false;
            feedBack.write (Boolean.TRUE);
          }
          if (countDown == 0) {
            countDown = STATS_INTERVAL;
            infoConfigure.write ((new Integer (count[Tree.infected] +
                                               count[Tree.dead])).toString ());
            // System.out.print ("Infection:");
            // for (int t = 0; t < Tree.nStates; t++) {
            //   System.out.print (" " + count[t]);
            // }
            // System.out.println ();
          } else {
            countDown--;
          }
        break;
      }  
    }

  }

}

