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
import org.jcsp.plugNplay.*;
import java.awt.event.*;
import java.util.*;

/**
 * @author Quickstone Technologies Limited
 */
final class Farmer implements CSProcess {
	
	private static final NetChannelEndFactory factory = new UnacknowledgedNetChannelEndFactory ();
	
	private final Random rnd = new Random ();
	
	private ChannelOutput[] toWorkers;
	private ProcessWrite[] writeProcs;
	private final Parallel parWrite;
	private final ChannelOutput toHarvester;
	private final AltingChannelInput fromUI;
	private final AltingChannelInput workerJoin;
	private final AltingChannelInput workerLeave;
	private int numWorkers;
	
	private final int colors[] = new int[] { 0xFF0000, 0x00FF00, 0xFFFF00, 0x0000FF, 0xFF00FF, 0x00FFFF, 0xFFFFFF, ImageDef.COLOR_MIRRORED, ImageDef.COLOR_TRANSPARENT };
	private final int ball[] = new int[] { 0, 7, 2, 7, 3, 8 };
	
	private int imageWidth, imageHeight, requestedWidth, requestedHeight;
	private final ImageDef[] imageDefs = new ImageDef[Main.BUFFERING];
	private ImageDef imageDef;
	private WorkPacket[][] work;
	private int frameCount = 0;
	
	private double vertRotation = 0.0, horRotation = 0.0;
	
	public static final class ResizeMessage {
		public final int width, height;
		public ResizeMessage (int width, int height) {
			this.width = width;
			this.height = height;
		}
	}
	
	public Farmer (ChannelOutput[] toWorkers,
					ChannelOutput toHarvester, AltingChannelInput fromUI,
					AltingChannelInput workerJoin, AltingChannelInput workerLeave) {
		this.toWorkers = toWorkers;
		this.toHarvester = toHarvester;
		this.fromUI = fromUI;
		this.workerJoin = workerJoin;
		this.workerLeave = workerLeave;
		this.numWorkers = toWorkers.length;
		this.writeProcs = new ProcessWrite[numWorkers];
		this.parWrite = new Parallel ();
		this.work = new WorkPacket[numWorkers][Main.BUFFERING];
		for (int i = 0; i < numWorkers; i++) {
			for (int j = 0; j < Main.BUFFERING; j++) {
				work[i][j] = new WorkPacket ();
			}
			writeProcs[i] = new ProcessWrite (toWorkers[i]);
		}
		parWrite.addProcess (writeProcs);
	}
	
	private final ImageDef makeImageDef () {
		ImageDef image = new ImageDef ();

		image.sphere_x = new double[6];
		image.sphere_y = new double[6];
		image.sphere_z = new double[6];
		image.sphere_r = new double[6];
		image.sphere_c = new int[6];
		
		for (int i = 0; i < 6; i++) {
			image.sphere_y[i] = 500 - (i * 50);
			image.sphere_r[i] = 300;
			image.sphere_c[i] = colors[ball[i]];
		}

		image.light_y = -300;
		image.floor_y = 800;

		return image;
	}
	
	private final void updateHarvester () {
		final ImagePacket image = new ImagePacket ();
		image.width = imageWidth;
		image.height = imageHeight;
		image.frame = frameCount;
		image.numWorkers = numWorkers;
		toHarvester.write (image);
		for (int i = 0; i < numWorkers; i++) {
			for (int j = 0; j < Main.BUFFERING; j++) {
				work[i][j].width = imageWidth;
				work[i][j].height = imageHeight;
				work[i][j].step = numWorkers;
				work[i][j].offset = i;
			}
		}
	}
	
	private double rp_result_x, rp_result_y, rp_result_z;
	private final void rotatePoint (double x, double y, double z, double cosYZ, double sinYZ, double cosXZ, double sinXZ) {
		rp_result_y = y * cosYZ - z * sinYZ;
		rp_result_z = z * cosYZ + y * sinYZ;
		rp_result_x = x * cosXZ - rp_result_z * sinXZ;
		rp_result_z = rp_result_z * cosXZ + x * sinXZ;
	}
	
	private final void updateWorkers () {
		int frameNo = frameCount;
		imageDef = imageDefs [frameCount % Main.BUFFERING];
		double ang = (double)frameNo * 6.28 / 100.0;
		for (int i = 0; i < imageDef.sphere_x.length; i++) {
			imageDef.sphere_x[i] = 800 * Math.cos (ang + (i * 6.28 / imageDef.sphere_x.length));
			imageDef.sphere_z[i] = 800 * Math.sin (ang + (i * 6.28 / imageDef.sphere_x.length));
			imageDef.sphere_y[i] = 500 * Math.cos ((i * 6.28 / imageDef.sphere_x.length) - ang);
		}
		imageDef.light_x = 1500 * Math.cos (-ang);
		imageDef.light_z = 1500 * Math.sin (-ang);
		
		double cosH = Math.cos (horRotation),
				sinH = Math.sin (horRotation),
				cosV = Math.cos (vertRotation),
				sinV = Math.sin (vertRotation);
				
		rotatePoint (0, 0, 2100, cosV, sinV, cosH, sinH);
		imageDef.camera_x = rp_result_x;
		imageDef.camera_y = rp_result_y;
		imageDef.camera_z = rp_result_z;
		
		rotatePoint (-550, -499, 1503, cosV, sinV, cosH, sinH);
		imageDef.view_window_x1 = rp_result_x;
		imageDef.view_window_y1 = rp_result_y;
		imageDef.view_window_z1 = rp_result_z;
		
		rotatePoint (550, -499, 1503, cosV, sinV, cosH, sinH);
		imageDef.view_window_x2 = rp_result_x;
		imageDef.view_window_y2 = rp_result_y;
		imageDef.view_window_z2 = rp_result_z;
		
		rotatePoint (550, 499, 1503, cosV, sinV, cosH, sinH);
		imageDef.view_window_x3 = rp_result_x;
		imageDef.view_window_y3 = rp_result_y;
		imageDef.view_window_z3 = rp_result_z;
		
		rotatePoint (-550, 499, 1503, cosV, sinV, cosH, sinH);
		imageDef.view_window_x4 = rp_result_x;
		imageDef.view_window_y4 = rp_result_y;
		imageDef.view_window_z4 = rp_result_z;
		
		for (int i = 0; i < numWorkers; i++) {
			work[i][frameCount % Main.BUFFERING].frame = frameCount;
			work[i][frameCount % Main.BUFFERING].image = imageDef;
			writeProcs[i].value = work[i][frameCount % Main.BUFFERING];
			//toWorkers[i].write (work[i][frameCount % Main.BUFFERING]);
		}
		//long t = System.currentTimeMillis ();
		parWrite.run ();
		//System.out.println ("Farmer: update workers - " + (System.currentTimeMillis () - t) + "ms");
		frameCount++;
	}
	
	private final void workerLeaves (NetChannelLocation ncl) {
		ChannelOutput toWorkers2[] = new ChannelOutput[numWorkers - 1];
		WorkPacket work2[][] = new WorkPacket[numWorkers - 1][];
		ProcessWrite writeProcs2[] = new ProcessWrite[numWorkers - 1];
		int j = 0;
		for (int i = 0; i < numWorkers; i++) {
			if (!((NetChannelOutput)toWorkers[i]).getChannelLocation ().equals (ncl)) {
				toWorkers2[j] = toWorkers[i];
				work2[j] = work[i];
				writeProcs2[j] = writeProcs[i];
				j++;
			}
		}
		toWorkers = toWorkers2;
		work = work2;
		writeProcs = writeProcs2;
		parWrite.removeAllProcesses ();
		parWrite.addProcess (writeProcs);
		numWorkers--;
		// recalculate size
		imageWidth = ((requestedWidth + (numWorkers - 1)) / numWorkers) * numWorkers;
		imageHeight = ((requestedHeight + (numWorkers - 1)) / numWorkers) * numWorkers;
	}
	
	private final void workerJoins (NetChannelLocation ncl) {
		ChannelOutput toWorkers2[] = new ChannelOutput[numWorkers + 1];
		WorkPacket work2[][] = new WorkPacket[numWorkers + 1][];
		ProcessWrite writeProcs2[] = new ProcessWrite[numWorkers + 1];
		System.arraycopy (toWorkers, 0, toWorkers2, 0, numWorkers);
		System.arraycopy (work, 0, work2, 0, numWorkers);
		System.arraycopy (writeProcs, 0, writeProcs2, 0, numWorkers);
		toWorkers2[numWorkers] = factory.createOne2Net (ncl);
		work2[numWorkers] = new WorkPacket[Main.BUFFERING];
		writeProcs2[numWorkers] = new ProcessWrite (toWorkers2[numWorkers]);
		for (int i = 0; i < Main.BUFFERING; i++) {
			work2[numWorkers][i] = new WorkPacket ();
		}
		toWorkers = toWorkers2;
		work = work2;
		writeProcs = writeProcs2;
		parWrite.addProcess (writeProcs2[numWorkers]);
		numWorkers++;
		// recalculate size
		imageWidth = ((requestedWidth + (numWorkers - 1)) / numWorkers) * numWorkers;
		imageHeight = ((requestedHeight + (numWorkers - 1)) / numWorkers) * numWorkers;
	}
	
	public final void run () {		
		boolean configChanged = false;
		int changeLock = Main.BUFFERING + 1;
		System.out.println ("Farmer: started");
		for (int i = 0; i < Main.BUFFERING; i++) {
			imageDefs[i] = makeImageDef ();
		}
		ResizeMessage rm = (ResizeMessage)fromUI.read ();
		requestedWidth = rm.width;
		requestedHeight = rm.height;
		imageWidth = ((requestedWidth + (numWorkers - 1)) / numWorkers) * numWorkers;
		imageHeight = ((requestedHeight + (numWorkers - 1)) / numWorkers) * numWorkers;
		updateHarvester ();
		for (int i = 0; i < Main.BUFFERING - 1; i++) {
			updateWorkers ();
		}
		main: while (true) {
			// Write out a load of work (non-blocking)
			updateWorkers ();
			// Check for pending UI events
			while (fromUI.pending ()) {
				Object o = fromUI.read ();
				if (o instanceof MouseEvent) {
					MouseEvent me = (MouseEvent)o;
					vertRotation = 1.570796 - ((double)me.getY () * 1.9 / (double)imageHeight);
					horRotation = (double)me.getX () * 6.28 / (double)imageWidth;
				} else if (o instanceof KeyEvent) {
					KeyEvent ke = (KeyEvent)o;
					char c = ke.getKeyChar ();
					int b = -1;
					if ((c >= 'A') && (c <= 'F')) {
						b = c - 'A';
					} else if ((c >= 'a') && (c <= 'f')) {
						b = c - 'a';
					} else if ((c >= '1') && (c <= '6')) {
						b = c - '1';
					}
					if (b >= 0) {
						System.out.println ("Farmer: ball " + (b + 1) + " color change");
						int nc = ball[b] = (ball[b] + 1) % colors.length;
						for (int j = 0; j < Main.BUFFERING; j++) {
							imageDefs[j].sphere_c[b] = colors[nc];
						}
					}
				} else if (o instanceof ResizeMessage) {
					rm = (ResizeMessage)o;
					imageWidth = ((rm.width + (numWorkers - 1)) / numWorkers) * numWorkers;
					imageHeight = ((rm.height + (numWorkers - 1)) / numWorkers) * numWorkers;
					configChanged = true;
				} else if (o == null) {
					break main;
				}
			}
			if (changeLock == 0) {
    			// Check for anyone wanting to join
    			while (workerJoin.pending ()) {
    				System.out.println ("Farmer: worker is joining");
    				NetChannelLocation ncl = (NetChannelLocation)workerJoin.read ();
    				workerJoins (ncl);
    				configChanged = true;
    				changeLock = Main.BUFFERING + 1;
    				break;
    			}
    			// Check for anyone wanting to leave
    			while ((workerLeave.pending ()) && (numWorkers > 1)) {
    				System.out.println ("Farmer: worker is leaving");
    				NetChannelLocation ncl = (NetChannelLocation)workerLeave.read ();
    				workerLeaves (ncl);
    				configChanged = true;
    				changeLock = Main.BUFFERING + 1;
    				break;
    			}
			} else {
				changeLock--;
			}
			// Synchronize with the harvester
			if (configChanged) {
				updateHarvester ();
				configChanged = false;
			} else {
				toHarvester.write (null);
			}
		}
		System.out.println ("Farmer: poisoning workers");
		for (int i = 0; i < numWorkers; i++) {
			toWorkers[i].write (null);
		}
		System.out.println ("Farmer: system-exit");
		System.exit (0);
	}
	
}
