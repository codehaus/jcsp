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
import org.jcsp.demos.util.Ask;
import org.jcsp.lang.*;

/**
 * @author P.H. Welch
 */
public class TestMatrix {

  public static final String TITLE = "Matrix";
  public static final String DESCR =
  	"Compares the speed of a sequential matrix multiplication algorithm and a parallelised one. The 'seed' " +
  	"parameter is used to initialize a random number generator. The 'limit' parameter sets the range of " +
  	"numbers generated. A, B and C specify the size of the matrices. An A*B matrix is multiplied by a B*C " +
  	"matrix. The number of benchmarks allows a number of trials to be made and an average time taken.\n" +
  	"\n" +
  	"The SEQ uses purely sequential logic. The PAR uses parallel logic but reallocates the threads each time. " +
  	"The reuse-PAR uses the same set of threads each time so no thread allocation overheads get considered.";

  public static void main (String[] args) {

	Ask.app (TITLE, DESCR);
	Ask.addPrompt ("seed", 1, 100, 1);
	Ask.addPrompt ("limit", 1, 2000, 100);
	Ask.addPrompt ("A", 1, 4096, 200);
	Ask.addPrompt ("B", 1, 4096, 200);
	Ask.addPrompt ("C", 1, 4096, 200);
	Ask.addPrompt ("benchmarks", 1, 2000, 10);
    Ask.show ();
    long seed = Ask.readInt ("seed");
    double limit = (double)Ask.readInt ("limit");
    int A = Ask.readInt ("A");
    int B = Ask.readInt ("B");
    int C = Ask.readInt ("C");
    int nBenchmarks = Ask.readInt ("benchmarks");
    Ask.blank ();

    Random random = new Random (seed);

    double[][] X = new double[A][B];
    double[][] Y = new double[B][C];
    double[][] Z = new double[A][C];
    double[][] ZZ = new double[A][C];

    System.out.println ("Initialising arrays X[A][B], Y[B][C] ...");
    Matrix.randomise (X, limit, random);
    Matrix.randomise (Y, limit, random);

    System.out.println ("Printing array X[A][B], ...");
    Matrix.print (X, 3, 3);
    System.out.println ("\nPrinting array Y[B][C] ...");
    Matrix.print (Y, 3, 3);

    CSTimer tim = new CSTimer ();
    long t0, t1, time;

    System.out.println ("\nMultiplying X*Y --> ZZ ...");
    t0 = tim.read ();
    Matrix.multiply (X, Y, ZZ);
    t1 = tim.read ();
    time = t1 - t0;
    System.out.println ("Completed in " + time + " milliseconds ...");
    System.out.println ("\nPrinting array ZZ[A][C] ...");
    Matrix.print (ZZ, 3, 3);

    ////////////////////////////////////////////////////////////////////////////////////

    double sum = 0.0d, sumsq = 0.0d;

    for (int i = 0; i < nBenchmarks; i++) {
      System.out.println ("\n(SEQ) Multiplying X*Y --> Z ...");
      t0 = tim.read ();
      Matrix.seqMultiply (X, Y, Z);
      t1 = tim.read ();
      time = t1 - t0;
      System.out.println ("[" + (i + 1) + "/" + nBenchmarks +
                          "] Completed in " + time + " milliseconds ...");
      sum += time;
      sumsq += time*time;
    }

    System.out.println ("\nPrinting array Z[A][C] ...");
    Matrix.print (Z, 3, 3);

    System.out.println ("\nChecking array Z[A][C] against ZZ[A][C] ...");
    if (Matrix.same (Z, ZZ)) {
      System.out.println ("... checked OK");
    } else {
      System.out.println ("... check FAILED");
    }

    double mean = sum/nBenchmarks;
    double top = sumsq - ((sum*sum)/nBenchmarks);
    double stdev = Math.sqrt(top/(nBenchmarks - 1));

    System.out.println ("\n[" + A + "][" + B + "] * [" + B + "][" + C + "] ==> [" + A + "][" + C + "]");
    System.out.println ("number of benchmarks = " + nBenchmarks);
    System.out.println ("mean = " + mean);
    System.out.println ("standard deviation = " + stdev);

    Matrix.randomise (Z, limit, random);

    ////////////////////////////////////////////////////////////////////////////////////

    sum = 0.0d; sumsq = 0.0d;

    for (int i = 0; i < nBenchmarks; i++) {
      System.out.println ("\n(PAR) Multiplying X*Y --> Z ...");
      t0 = tim.read ();
      Matrix.parMultiply (X, Y, Z);
      t1 = tim.read ();
      time = t1 - t0;
      System.out.println ("[" + (i + 1) + "/" + nBenchmarks +
                          "] Completed in " + time + " milliseconds ...");
      sum += time;
      sumsq += time*time;
    }

    System.out.println ("\nPrinting array Z[A][C] ...");
    Matrix.print (Z, 3, 3);

    System.out.println ("\nChecking array Z[A][C] against ZZ[A][C] ...");
    if (Matrix.same (Z, ZZ)) {
      System.out.println ("... checked OK");
    } else {
      System.out.println ("... check FAILED");
    }

    mean = sum/nBenchmarks;
    top = sumsq - ((sum*sum)/nBenchmarks);
    stdev = Math.sqrt(top/(nBenchmarks - 1));

    System.out.println ("\n[" + A + "][" + B + "] * [" + B + "][" + C + "] ==> [" + A + "][" + C + "]");
    System.out.println ("number of benchmarks = " + nBenchmarks);
    System.out.println ("mean = " + mean);
    System.out.println ("standard deviation = " + stdev);

    Matrix.randomise (Z, limit, random);

    ////////////////////////////////////////////////////////////////////////////////////

    Parallel par = Matrix.makeParMultiply (X, Y, Z);

    sum = 0.0d; sumsq = 0.0d;

    for (int i = 0; i < nBenchmarks; i++) {
      System.out.println ("\n(reuse-PAR) Multiplying X*Y --> Z ...");
      t0 = tim.read ();
      par.run ();
      t1 = tim.read ();
      time = t1 - t0;
      System.out.println ("[" + (i + 1) + "/" + nBenchmarks +
                          "] Completed in " + time + " milliseconds ...");
      sum += time;
      sumsq += time*time;
    }

    System.out.println ("\nPrinting array Z[A][C] ...");
    Matrix.print (Z, 3, 3);

    System.out.println ("\nChecking array Z[A][C] against ZZ[A][C] ...");
    if (Matrix.same (Z, ZZ)) {
      System.out.println ("... checked OK");
    } else {
      System.out.println ("... check FAILED");
    }

    mean = sum/nBenchmarks;
    top = sumsq - ((sum*sum)/nBenchmarks);
    stdev = Math.sqrt(top/(nBenchmarks - 1));

    System.out.println ("\n[" + A + "][" + B + "] * [" + B + "][" + C + "] ==> [" + A + "][" + C + "]");
    System.out.println ("number of benchmarks = " + nBenchmarks);
    System.out.println ("mean = " + mean);
    System.out.println ("standard deviation = " + stdev);

    Matrix.randomise (Z, limit, random);

    System.exit (0);

  }

}

