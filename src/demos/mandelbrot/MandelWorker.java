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
class MandelWorker implements CSProcess {

  private final int id;

  private final int minMaxIterations;
  private final int maxMaxIterations;

  private final ChannelInput fromFarmer;
  private final ChannelOutput toFarmer;

  private final ChannelInput fromHarvester;
  private final ChannelOutput toHarvester;

  public MandelWorker (final int id,
                       final int minMaxIterations,
                       final int maxMaxIterations,
                       final ChannelInput fromFarmer,
                       final ChannelOutput toFarmer,
                       final ChannelInput fromHarvester,
                       final ChannelOutput toHarvester) {
    this.id = id;
    this.minMaxIterations = minMaxIterations;
    this.maxMaxIterations = maxMaxIterations;
    this.fromFarmer = fromFarmer;
    this.toFarmer = toFarmer;
    this.fromHarvester = fromHarvester;
    this.toHarvester = toHarvester;
  }

  public void run () {

    final int radius = 2;

    final MandelPoint mandel = new MandelPoint (0, radius);

    WorkPacket work = new WorkPacket ();
    ResultPacket result = new ResultPacket ();

    int count = 0;

    System.out.println ("Worker " + id + " priority = " + PriParallel.getPriority ());
    PriParallel.setPriority (Thread.MIN_PRIORITY);
    System.out.println ("Worker " + id + " priority = " + PriParallel.getPriority ());

    toFarmer.write (work);
    work = (WorkPacket) fromFarmer.read ();

    final int nPoints = work.X.length;
    result.points = new byte[nPoints];

    while (true) {
      count++;
      if ((count % 100) == 0) System.out.println ("Worker " + id + " working ... " + count );
      mandel.setMaxIterations (work.maxIterations);
      for (int i = 0; i < nPoints; i++) {
        final int iterations = mandel.compute (work.X[i], work.y);
        if ((iterations == work.maxIterations) || (iterations == 0)) {
          result.points[i] = 0;
        } else {
          final byte biterations = (byte) iterations;
          if (biterations == 0) {
            result.points[i] = 1;  // OK for smooth colouring (rough ==> 127 ???)
          } else {
            result.points[i] = biterations;
          }
        }
      }
      result.j = work.j;
      toHarvester.write (result);  // these communication-pairs could be in PAR
      fromHarvester.read ();
      toFarmer.write (work);
      work = (WorkPacket) fromFarmer.read ();
      Thread.yield ();
    }

  }

}
