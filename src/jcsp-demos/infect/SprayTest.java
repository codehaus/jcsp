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
import org.jcsp.demos.util.Ask;

/**
 * @author P.H. Welch
 */
class SprayTest {

  public static void main (String[] args) {

    int radius = 20;
    byte[][] cell = new byte[50][50];
    byte[] pixels = new byte[50*50];
    int[] count = new int[Cell.N_STATES];

    count[Cell.GREEN] = 50*50;
    count[Cell.INFECTED] = 0;
    count[Cell.DEAD] = 0;
    
    Spray spray = new Spray (radius, cell, pixels, count);

    char[] which = {'g', 'i', 'd'};

    while (true) {
      System.out.println ();
      final int i = Ask.Int ("i = ", 0, 49);
      final int j = Ask.Int ("j = ", 0, 49);
      final char ch = Ask.Char ("g/i/d? ", which);
      final byte state = (ch == 'g') ? Cell.GREEN : (ch == 'i') ? Cell.INFECTED : Cell.DEAD;
      System.out.println ();
      spray.zap (new Point (i, j), state);
      spray.printCells ();
      System.out.println ();
      System.out.println ("GREEN = " + count[Cell.GREEN]);
      System.out.println ("INFECTED = " + count[Cell.INFECTED]);
      System.out.println ("DEAD = " + count[Cell.DEAD]);
      spray.setMask ();
    }
  }

}
