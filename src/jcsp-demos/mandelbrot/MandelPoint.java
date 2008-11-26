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


import org.jcsp.demos.util.ComplexDouble;

/**
 * @author P.H. Welch
 */
class MandelPoint {

  protected int maxIterations;

  protected double radiusSquared;

  public MandelPoint (final int maxIterations, final int radius) {
    this.maxIterations = maxIterations;
    radiusSquared = (double) radius*radius;
  }

  public void setMaxIterations (final int maxIterations) {
    this.maxIterations = maxIterations;
  }

  public int compute (final ComplexDouble c) {
    int n = 0;
    ComplexDouble z = (ComplexDouble) c.clone ();
    double zModulusSquared = z.modulusSquared ();
    while ((n < maxIterations) && (zModulusSquared < radiusSquared)) {
      z.mult (z).add (c);
      zModulusSquared = z.modulusSquared ();
      n++;
    }
    return n;
  }

  public int compute (final double a, final double b) {
    int n = 0;
    double x = a;
    double y = b;
    double xSquared = x*x;
    double ySquared = y*y;
    while ((n < maxIterations) && ((xSquared + ySquared) < radiusSquared)) {
      double tmp = (xSquared - ySquared) + a;
      y = ((2.0d*x)*y) + b;
      x = tmp;
      xSquared = x*x;
      ySquared = y*y;
      n++;
    }
    return n;
  }
}
