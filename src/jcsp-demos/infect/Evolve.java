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

/**
 * @author P.H. Welch
 */
public class Evolve implements CSProcess {

  // private int firstRow, stopRow;
  private int startRow, nextStartRow;
  private byte[][] cell, last_cell;
  private int width, height;
  private byte[] pixels;

  private final Rand random;

  public int infectRate, recoverRate, reinfectRate, convertRate;   // set before & change between runs

  public int[] count = new int[Cell.N_STATES];                     // consume after each run

  public Evolve (
    int startRow, int nextStartRow, byte[][] cell, byte[][] last_cell, byte[] pixels, long seed
  ) {
    resize (startRow, nextStartRow, cell, last_cell, pixels);
    random = new Rand (seed);
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

  public void resize (
    int startRow, int nextStartRow, byte[][] cell, byte[][] last_cell, byte[] pixels
  ) {
    this.startRow = startRow;
    this.nextStartRow = nextStartRow;
    this.cell = cell;
    this.last_cell = last_cell;
    this.pixels = pixels;
    width = cell[0].length;
    height = cell.length;
  }

  public void run () {                    // evolves given part of the forest forward one cycle
    for (int i = 0; i < count.length; i++) count[i] = 0;
    int pixIndex = startRow*width;
    for (int i = startRow; i < nextStartRow; i++) {
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
      System.arraycopy (cell[i], 0, pixels, pixIndex, width);
      pixIndex = pixIndex + width;
    }
  }

}
