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
import org.jcsp.net.cns.*;
import org.jcsp.net.tcpip.*;
import org.jcsp.util.*;
import org.jcsp.demos.util.*;
import java.io.*;

/**
 * @author Quickstone Technologies Limited
 */
public final class Worker implements CSProcess {

	private final ChannelInput fromFarmer;
	private final ChannelOutput toHarvester;

	private Worker (ChannelInput fromFarmer, ChannelOutput toHarvester) {
		this.fromFarmer = fromFarmer;
		this.toHarvester = toHarvester;
	}

	public final void run () {
		System.out.println ("Worker: started");
		final ResultPacket[] results = new ResultPacket[Main.BUFFERING];
		for (int i = 0; i < Main.BUFFERING; i++) results[i] = new ResultPacket ();
		int currentResult = 0;
		final Counter frameCounter = new Counter (), dataCounter = new Counter ();
		while (true) {
			final ResultPacket result = results[currentResult];
			if (++currentResult >= Main.BUFFERING) currentResult = 0;
			// Request some work
			//System.out.println ("Worker: waiting for work");
			final WorkPacket work = (WorkPacket)fromFarmer.read ();
			// Determine the result packet
			result.frame = work.frame;
			result.offset = work.offset;
			result.step = work.step;
			int workLength = work.width * (work.height / work.step) * 3;
			if ((result.color == null) || (result.color.length != workLength)) {
				System.out.println ("Worker: allocating new result block");
				result.color = new byte[workLength];
			}
			dataCounter.dataStart ();
			int i = 0, j = workLength / 3, k = (j << 1);
			final ImageDef image = work.image;
			final double wh = (double)work.height, ww = (double)work.width;
			for (int y = work.offset; y < work.height; y += work.step) {
				final double _y = (double)y;
				double left_x = (_y * (image.view_window_x4 - image.view_window_x1)) / wh + image.view_window_x1;
				double left_y = (_y * (image.view_window_y4 - image.view_window_y1)) / wh + image.view_window_y1;
				double left_z = (_y * (image.view_window_z4 - image.view_window_z1)) / wh + image.view_window_z1;
				final double step_x = ((_y * (image.view_window_x3 - image.view_window_x2)) / wh + image.view_window_x2 - left_x) / ww;
				final double step_y = ((_y * (image.view_window_y3 - image.view_window_y2)) / wh + image.view_window_y2 - left_y) / ww;
				final double step_z = ((_y * (image.view_window_z3 - image.view_window_z2)) / wh + image.view_window_z2 - left_z) / ww;
				for (int x = 0; x < work.width; x++) {
					// trace the ray
					int c = image.trace (
						image.camera_x,
						image.camera_y,
						image.camera_z,
						left_x - image.camera_x,
						left_y - image.camera_y,
						left_z - image.camera_z,
						ImageDef.AMBIENT,
						0);
					result.color[i++] = (byte)(c >> 16);
					result.color[j++] = (byte)(c >> 8);
					result.color[k++] = (byte)c;
					left_x += step_x;
					left_y += step_y;
					left_z += step_z;
				}
			}
			frameCounter.click ();
			dataCounter.dataEnd (workLength);
			// Send to harvester
			//System.out.println ("Worker: writing frame " + result.frame + " to harvester");
			toHarvester.write (result);
		}
	}

	private final Object identity_sync = new Object ();
	private int identity_count = 0;

	private final class TerminatingIdentity implements CSProcess {
		private final ChannelInput in;
		private final ChannelOutput out;
		public TerminatingIdentity (ChannelInput in, ChannelOutput out) {
			this.in = in;
			this.out = out;
		}
		public void run () {
			while (true) {
				Object o = in.read ();
				if (o == null) System.exit (1);
				synchronized (identity_sync) {
					identity_count++;
				}
				out.write (o);
			}
		}
	}

	private final class FlushingIdentity implements CSProcess {
		private final AltingChannelInput in, flush;
		private final ChannelOutput out;
		public FlushingIdentity (AltingChannelInput in, AltingChannelInput flush, ChannelOutput out) {
			this.in = in;
			this.flush = flush;
			this.out = out;
		}
		public void run () {
			final Alternative alt = new Alternative (new Guard[] { flush, in });
			while (true) {
				if (alt.priSelect () == 0) {
					flush.read ();
					while (true) {
						synchronized (identity_sync) {
							if (identity_count == 0) System.exit (0);
							System.out.println ("Worker: " + identity_count + " frame(s) left");
						}
						Object o = in.read ();
        				synchronized (identity_sync) {
        					identity_count--;
        				}
        				out.write (o);
					}
				} else {
    				Object o = in.read ();
    				synchronized (identity_sync) {
    					identity_count--;
    				}
    				out.write (o);
				}
			}
		}
	}

	public static final void main (String[] args) throws Exception {

		// Get the command line
		if (args.length != 1) {
			/*Ask.app (
				"Ray Tracer",
				"Implements a basic ray-tracing algorithm incorporating reflection, refraction and texture mapped " +
				"surfaces, distributed using a farmer/worker/harvester approach. This is a worker node. By specifying " +
				"the address of a CNS server that the farmer/harvester registered with it can join an existing network." +
				"This demonstration shows performance/scaleability using JCSP. Workers can join and leave at any time. " +
				"To leave the network, press ENTER. Do not terminate the program with CTRL+C or the worker may not " +
				"deregister itself with the farmer and cause a deadlock.");
			Ask.addPrompt ("CNS address");*/
			Ask.app (
				"Ray Tracer",
				"Implements a basic ray-tracing algorithm incorporating reflection, refraction and texture mapped " +
				"surfaces, distributed using a farmer/worker/harvester approach. This is a worker node. By specifying " +
				"the address of the interface it can join an existing network." +
				"This demonstration shows performance/scaleability using JCSP. Workers can join and leave at any time. " +
				"To leave the network, press ENTER. Do not terminate the program with CTRL+C or the worker may not " +
				"deregister itself with the farmer and cause a deadlock.");
			Ask.addPrompt ("Server address");
			Ask.show ();
			Node.getInstance ().init (new TCPIPNodeFactory (Ask.readStr ("Server address")));
		} else {
			Node.getInstance ().init (new TCPIPNodeFactory (args[0]));
		}

		// Establish the NET connections
		final NetChannelOutput toHarvester = CNS.createOne2Net ("org.jcsp.demos.raytrace.demux");
		final NetChannelOutput joinNetwork = CNS.createOne2Net ("org.jcsp.demos.raytrace.join");
		final NetChannelOutput leaveNetwork = CNS.createOne2Net ("org.jcsp.demos.raytrace.leave");

		final NetChannelInput fromFarmer = NetChannelEnd.createNet2One ();

		System.out.println ("Worker: joining network");
		joinNetwork.write (fromFarmer.getChannelLocation ());

		final One2OneChannel farmer2worker = Channel.one2one (new InfiniteBuffer ()),
							  worker2harvester = Channel.one2one (new InfiniteBuffer ()),
							  flushSignal = Channel.one2one ();

		Worker w = new Worker (farmer2worker.in (), worker2harvester.out ());
		new Parallel (
			new CSProcess[] {
				w.new TerminatingIdentity (fromFarmer, farmer2worker.out ()),
				w,
				w.new FlushingIdentity (worker2harvester.in (), flushSignal.in (), toHarvester),
				new CSProcess () {
					public void run () {
    					try {
    						System.in.read ();
    					} catch (IOException e) {
    					}
    					System.out.println ("Worker: leaving network");
    					leaveNetwork.write (fromFarmer.getChannelLocation ());
    					System.out.println ("Worker: flushing outstanding packets");
    					flushSignal.out ().write (null);
					}
				}
			}
		).run ();

	}

}
