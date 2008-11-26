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


import java.util.Random;
import org.jcsp.lang.*;

/**
 * @author P.H. Welch
 */
public class Matrix {

  public static void randomise (final double[][] X, final double limit, final Random random) {
    final double halfLimit = limit/2.0d;
    for (int i = 0; i < X.length; i++) {
      final double[] Xi = X[i];
      for (int j = 0; j < Xi.length; j++) {
        Xi[j] = (limit*random.nextDouble ()) - halfLimit;
      }
    }
  }

  public static void print (final double[][] X, final int rows, final int cols) {
    for (int i = 0; i < rows; i++) {
      final double[] Xi = X[i];
      for (int j = 0; j < cols; j++) {
        if ((j%5) == 0) System.out.println ();
        System.out.print ("\t" + Xi[j]);
      }
    }
    System.out.println ();
  }

  public static boolean same (final double[][] X, final double[][] Y) {
    for (int i = 0; i < X.length; i++) {
      final double[] Xi = X[i];
      final double[] Yi = Y[i];
      for (int j = 0; j < Xi.length; j++) {
        if (Xi[j] != Yi[j]) {
          System.out.println ("X[" + i + "][" + j + "] = " + Xi[j]);
          System.out.println ("Y[" + i + "][" + j + "] = " + Yi[j]);
          return false;
        }
      }
    }
    return true;
  }

  public static void multiply (final double[][] X, final double[][] Y, final double[][] Z) {
    if (X[0].length != Y.length) {
      throw new MultiplyBoundsException ("X[0].length != Y.length");
    }
    if (X.length != Z.length) {
      throw new MultiplyBoundsException ("X.length != Z.length");
    }
    if (Y[0].length != Z[0].length) {
      throw new MultiplyBoundsException ("Y[0].length != Z[0].length");
    }
    for (int i = 0; i < X.length; i++) {
      final double[] Xi = X[i];
      final double[] Zi = Z[i];
      for (int j = 0; j < Y[0].length; j++) {
        double sum = 0.0d;
        for (int k = 0; k < Y.length; k++) {
          sum += Xi[k]*Y[k][j];
        }
        Zi[j] = sum;
      }
    }
  }

  public static void seqMultiply (final double[][] X, final double[][] Y, final double[][] Z) {
    if (X[0].length != Y.length) {
      throw new MultiplyBoundsException ("X[0].length != Y.length");
    }
    if (X.length != Z.length) {
      throw new MultiplyBoundsException ("X.length != Z.length");
    }
    if (Y[0].length != Z[0].length) {
      throw new MultiplyBoundsException ("Y[0].length != Z[0].length");
    }
    final CSProcess[] rowProcess = new CSProcess[X.length];
    for (int i = 0; i < X.length; i++) {
      final int ii = i;
      rowProcess[ii] = new CSProcess () {
        public void run () {
          final double[] Xi = X[ii];
          final double[] Zi = Z[ii];
          final double[][] YY = Y;
          for (int j = 0; j < YY[0].length; j++) {
            double sum = 0.0d;
            for (int k = 0; k < YY.length; k++) {
              sum += Xi[k]*YY[k][j];
            }
            Zi[j] = sum;
          }
        }
      };
    }
    new Sequence (rowProcess).run ();
  }

  public static void parMultiply (final double[][] X, final double[][] Y, final double[][] Z) {
    if (X[0].length != Y.length) {
      throw new MultiplyBoundsException ("X[0].length != Y.length");
    }
    if (X.length != Z.length) {
      throw new MultiplyBoundsException ("X.length != Z.length");
    }
    if (Y[0].length != Z[0].length) {
      throw new MultiplyBoundsException ("Y[0].length != Z[0].length");
    }
    final CSProcess[] rowProcess = new CSProcess[X.length];
    for (int i = 0; i < X.length; i++) {
      final int ii = i;
      rowProcess[ii] = new CSProcess () {
        public void run () {
          final double[] Xi = X[ii];
          final double[] Zi = Z[ii];
          final double[][] YY = Y;
          for (int j = 0; j < YY[0].length; j++) {
            double sum = 0.0d;
            for (int k = 0; k < YY.length; k++) {
              sum += Xi[k]*YY[k][j];
            }
            Zi[j] = sum;
          }
        }
      };
    }
    final Parallel par = new Parallel (rowProcess);
    par.run ();
    par.releaseAllThreads ();
  }

  public static Parallel makeParMultiply (final double[][] X,
                                          final double[][] Y,
                                          final double[][] Z) {
    if (X[0].length != Y.length) {
      throw new MultiplyBoundsException ("X[0].length != Y.length");
    }
    if (X.length != Z.length) {
      throw new MultiplyBoundsException ("X.length != Z.length");
    }
    if (Y[0].length != Z[0].length) {
      throw new MultiplyBoundsException ("Y[0].length != Z[0].length");
    }
    final CSProcess[] rowProcess = new CSProcess[X.length];
    for (int i = 0; i < X.length; i++) {
      final int ii = i;
      rowProcess[ii] = new CSProcess () {
        public void run () {
          final double[] Xi = X[ii];
          final double[] Zi = Z[ii];
          final double[][] YY = Y;
          for (int j = 0; j < YY[0].length; j++) {
            double sum = 0.0d;
            for (int k = 0; k < YY.length; k++) {
              sum += Xi[k]*YY[k][j];
            }
            Zi[j] = sum;
          }
        }
      };
    }
    return new Parallel (rowProcess);
  }

  public static class MultiplyBoundsException extends RuntimeException {

    public MultiplyBoundsException (String s) {
      super (s);
    }

  }

}
