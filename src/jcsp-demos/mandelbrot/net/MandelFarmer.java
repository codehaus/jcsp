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
import java.util.*;

/**
 * @author Quickstone Technologies Limited
 * @author P.H. Welch (non-networked original code)
 */
class MandelFarmer implements CSProcess {

    private final ChannelInput fromControl;

    private final ChannelInput fromHarvester;
    private final ChannelOutput toHarvester;

    private final AltingChannelInput fromCancel;
    private final ChannelOutput toCancel;

    private final AltingChannelInput fromWorkers;
    private final Hashtable toWorkers; // NetChannelLocation -> NetChannelOutput objects

    public MandelFarmer(
        final ChannelInput fromControl,
        final ChannelInput fromHarvester,
        final ChannelOutput toHarvester,
        final AltingChannelInput fromCancel,
        final ChannelOutput toCancel,
        final AltingChannelInput fromWorkers) {
        this.fromControl = fromControl;
        this.fromHarvester = fromHarvester;
        this.toHarvester = toHarvester;
        this.fromCancel = fromCancel;
        this.toCancel = toCancel;
        this.fromWorkers = fromWorkers;
        this.toWorkers = new Hashtable ();
    }

    public void run() {

        final Guard[] guards = new Guard[] { fromCancel, fromWorkers };
        final Alternative alt = new Alternative(guards);

        final int CANCEL = 0;        

        Object object = fromControl.read();
        final int width = ((Integer) object).intValue();
        final double[] X = new double[width];
        toHarvester.write(object);

        object = fromControl.read();
        final int height = ((Integer) object).intValue();
        toHarvester.write(object);

        object = fromControl.read(); // mis
        toHarvester.write(object);

        object = fromControl.read(); // display
        toHarvester.write(object);

        WorkPacket work = new WorkPacket();

        while (true) {

            final FarmPacket packet = (FarmPacket) fromControl.read();
            toHarvester.write(packet);

            System.out.println("\nMandelFarmer: Left = " + packet.left);
            System.out.println("MandelFarmer: Top = " + packet.top);
            System.out.println("MandelFarmer: Size = " + packet.size);
            System.out.println(
                "MandelFarmer: Maximum iterations = " + packet.maxIterations);

            final double shrink = packet.size / ((double) (width - 1));
            for (int i = 0; i < width; i++) {
                X[i] = packet.left + (((double) i) * shrink);
            }

            while (fromCancel.pending())
                fromCancel.read();
            toCancel.write(Boolean.TRUE);

            for (int j = 0; j < height; j++) {
                final double y = packet.top - (((double) j) * shrink);
                if (alt.priSelect() == CANCEL) {
                        fromCancel.read();
                        System.out.println("MandelFarmer.CANCEL: " + j);
                        toCancel.write(Boolean.FALSE);
                        toHarvester.write(new Integer(j));
                        // say how many work packets generated
                        fromHarvester.read();
                        break;
                } else {
                    NetChannelLocation ncl = (NetChannelLocation)fromWorkers.read();
                    work.X = X;
                    work.y = y;
                    work.j = j;
                    work.maxIterations = packet.maxIterations;
                    NetChannelOutput out = (NetChannelOutput)toWorkers.get (ncl);
                    if (out == null) {
                    	out = NetChannelEnd.createOne2Net (ncl);
                    	toWorkers.put (ncl, out);
                    }
                    out.write(work);
                }
            }

            toCancel.write(Boolean.FALSE);

        }

    }

}
