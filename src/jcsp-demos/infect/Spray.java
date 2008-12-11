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


import java.awt.Point;

/**
 * @author P.H. Welch
 */
class Spray {

  private int radius;         // radius of spray

  private byte[][] cell;      // matrix of Cells
  private int height;
  private int width;

  private byte[] pixels;      // pixel array of Cell matrix

  private int[] count;        // how many Cells of each state

  private boolean[][] mask;
  
  private final Rand random = new Rand ();

  public Spray (int radius, byte[][] cell, byte[] pixels, int[] count) {
    resize (radius, cell, pixels, count);
    setMask ();
    // printMask ();
  }

  public void resize (int radius, byte[][] cell, byte[] pixels, int[] count) {
    this.radius = radius;
    this.cell = cell;
    this.height = cell.length;
    this.width = cell[0].length;
    this.pixels = pixels;
    this.count = count;
  }

  public void setMask () {
    final int BIG = 131072;
    mask = new boolean[(2*radius) - 1][(2*radius) - 1];
    final int r2 = radius*radius;
    for (int j = 0; j < mask.length; j++) {
      final boolean[] row = mask[j];
      final int y = radius - j;
      final int y2 = y*y;
      for (int i = 0; i < row.length; i++) {
        final int x = radius - i;
        final int x2 = x*x;
        final int d2 = x2 + y2;
        final int chance = BIG - ((BIG*d2)/r2);
        row[i] = (random.bits (17) < chance);
      }
    }
  }

  private void printMask () {
    for (int j = 0; j < mask.length; j++) {
      final boolean[] row = mask[j];
      for (int i = 0; i < row.length; i++) {
        if (row[i]) {
          System.out.print ("*");
        } else {
          System.out.print (" ");
        }
      }
      System.out.println ();
    }
  }

  public void printCells () {
    for (int j = 0; j < cell.length; j++) {
      final byte[] row = cell[j];
      for (int i = 0; i < row.length; i++) {
        switch (row[i]) {
          case Cell.GREEN:
            System.out.print (".");
          break;
          case Cell.INFECTED:
            System.out.print ("I");
          break;
          case Cell.DEAD:
            System.out.print ("D");
          break;
        }
      }
      System.out.println ();
    }
  }

  public void zap (final Point point, final byte state) {
  
    final int j = point.x;
    final int i = point.y;
    
    final int mini = i - (radius - 1);
    final int maxi = i + (radius - 1);
    final int minj = j - (radius - 1);
    final int maxj = j + (radius - 1);
    
    final int miniClip = (mini < 0) ? 0 : mini;
    final int maxiClip = (maxi >= height) ? height - 1 : maxi;
    final int minjClip = (minj < 0) ? 0 : minj;
    final int maxjClip = (maxj >= width) ? width - 1 : maxj;
    
    final int miniMask = miniClip - mini;
    final int minjMask = minjClip - minj;

    // System.out.println ("i = " + i + " j = " + j);
    // System.out.println ();
    // System.out.println ("mini = " + mini + " maxi = " + maxi);
    // System.out.println ("minj = " + minj + " maxj = " + maxj);
    // System.out.println ();
    // System.out.println ("miniClip = " + miniClip + " maxiClip = " + maxiClip);
    // System.out.println ("minjClip = " + minjClip + " maxjClip = " + maxjClip);
    // System.out.println ();
    // System.out.println ("miniMask = " + miniMask + " minjMask = " + minjMask);
    // System.out.println ();

    int mi = miniMask;
    for (int ci = miniClip; ci <= maxiClip; ci++) {
      byte[] cellRow = cell[ci];
      boolean[] maskRow = mask[mi];
      int mj = minjMask;
      int pj = (ci*width) + minjClip;
      for (int cj = minjClip; cj <= maxjClip; cj++) {
        if (maskRow[mj]) {
          final byte current = cellRow[cj];
          if (current != Cell.INFECTED) {
            pixels[pj] = state;
            count[current]--;
            cellRow[cj] = state;
            count[state]++;
          }
        }
        mj++;
        pj++;
      }
      mi++;
    }

  }

  public void zap2 (final Point point, final byte state) {
  
    int j = point.x;
    int i = point.y;
    
    while (i < 0) i += height;            // mostly won't happen or
    while (i >= height) i -= height;      // will happen only once.
    while (j < 0) j += width;             //         ditto.
    while (j >= width) j -= width;        //         ditto.
    
    final int mini = i - (radius - 1);
    final int maxi = i + (radius - 1);
    final int minj = j - (radius - 1);
    final int maxj = j + (radius - 1);

    // System.out.println ("i = " + i + " j = " + j);
    // System.out.println ();
    // System.out.println ("mini = " + mini + " maxi = " + maxi);
    // System.out.println ("minj = " + minj + " maxj = " + maxj);
    // System.out.println ();
    // System.out.println ("miniClip = " + miniClip + " maxiClip = " + maxiClip);
    // System.out.println ("minjClip = " + minjClip + " maxjClip = " + maxjClip);
    // System.out.println ();
    // System.out.println ("miniMask = " + miniMask + " minjMask = " + minjMask);
    // System.out.println ();

    int mi = 0;
    for (int ci = mini; ci <= maxi; ci++) {
      final int cii = (ci < 0) ? ci + height : (ci >= height) ? ci - height : ci;
      byte[] cellRow = cell[cii];
      boolean[] maskRow = mask[mi];
      int mj = 0;
      for (int cj = minj; cj <= maxj; cj++) {
        final int cjj = (cj < 0) ? cj + width : (cj >= width) ? cj - width : cj;
        if (maskRow[mj]) {
          final byte current = cellRow[cjj];
          if (current != Cell.INFECTED) {
            final int pjj = (cii*width) + cjj;
            pixels[pjj] = state;
            count[current]--;
            cellRow[cjj] = state;
            count[state]++;
          }
        }
        mj++;
      }
      mi++;
    }

  }

}
