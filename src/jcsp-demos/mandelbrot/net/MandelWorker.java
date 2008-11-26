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
import org.jcsp.net.*;
import org.jcsp.net.tcpip.*;
import org.jcsp.net.cns.*;
import org.jcsp.demos.util.*;

/**
 * @author Quickstone Technologies Limited
 * @author P.H. Welch (non-networked original code)
 */
public class MandelWorker implements CSProcess {

  private static final int NUM_THREADS = 10;

  public static final String TITLE = "Mandelbrot Set (distributed)";
  public static final String DESCR =
  	"Mandelbrot worker process. Please give the name of the machine running the main program interface.";

  private final SharedChannelOutput toFarmer;
  private final NetChannelInput fromFarmer;
  private final NetChannelLocation id;
  private final SharedChannelOutput toHarvester;

  private MandelWorker (final SharedChannelOutput toFarmer,
                         final NetChannelInput fromFarmer,
                         final NetChannelLocation id,
                         final SharedChannelOutput toHarvester) {
    this.toFarmer = toFarmer;
    this.fromFarmer = fromFarmer;
    this.id = id;
    this.toHarvester = toHarvester;
  }

  public void run () {

    final int radius = 2;

    final MandelPoint mandel = new MandelPoint (0, radius);

    ResultPacket result = new ResultPacket ();

    int count = 0;

    //System.out.println ("Worker " + id + " priority = " + PriParallel.getPriority ());
    PriParallel.setPriority (Thread.MIN_PRIORITY);
    //System.out.println ("Worker " + id + " priority = " + PriParallel.getPriority ());

    toFarmer.write (id); // request for work
    WorkPacket work = (WorkPacket) fromFarmer.read ();

    final int nPoints = work.X.length;
    result.points = new byte[nPoints];

    while (true) {
      if ((count % 100) == 0) System.out.println (Thread.currentThread ().getName () + " - Working ... " + count );
      count++;
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
      toHarvester.write (result);
      toFarmer.write (id);
      work = (WorkPacket) fromFarmer.read ();
    }

  }

  public static void main (String[] args) throws Exception {

  	// Start up
  	if (args.length == 0) {
        Ask.app (TITLE, DESCR);
        Ask.addPrompt ("CNS Address");
        Ask.show ();
        Node.getInstance ().init (new TCPIPNodeFactory (Ask.readStr ("CNS Address")));
        Ask.blank ();
  	} else {
        Node.getInstance ().init (new TCPIPNodeFactory (args[0]));
  	}

  	// Connect to the farmer and harvester
  	NetChannelInput fromFarmer = NetChannelEnd.createNet2One ();
  	NetChannelLocation id = fromFarmer.getChannelLocation ();
  	System.out.println ("Connecting to farmer");
  	NetSharedChannelOutput toFarmer = CNS.createAny2Net ("org.jcsp.demos.mandelbrot.net.Farmer");
  	System.out.println ("Connecting to harvester");
  	NetSharedChannelOutput toHarvester = CNS.createAny2Net ("org.jcsp.demos.mandelbrot.net.Harvester");
  	System.out.println ("Ready");

  	// Create some workers
  	CSProcess workers[] = new CSProcess[NUM_THREADS];
  	for (int i = 0; i < workers.length; i++) {
  		workers[i] = new MandelWorker (toFarmer, fromFarmer, id, toHarvester);
  	}
  	new Parallel (workers).run ();

  }

}
