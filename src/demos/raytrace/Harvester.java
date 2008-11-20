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
import org.jcsp.awt.*;
import java.awt.*;
import java.awt.image.*;

/**
 * @author Quickstone Technologies Limited
 */
class Harvester implements CSProcess {

	private final ChannelInput[] fromWorkers;
	private final ChannelInput fromFarmer;
	private final ChannelOutput toGraphics;
	private final ChannelInput fromGraphics;

	public Harvester (ChannelInput[] fromWorkers, ChannelInput fromFarmer,
	                    ChannelOutput toGraphics, ChannelInput fromGraphics) {
		this.fromWorkers = fromWorkers;
		this.fromFarmer = fromFarmer;
		this.toGraphics = toGraphics;
		this.fromGraphics = fromGraphics;
	}

	public void run () {
		System.out.println ("Harvester: started");
		final Counter counter = new Counter ();
		final DisplayList displayList = new DisplayList ();
		int currentFrame = 0;
		int width = 0, height = 0;
		int[] pixels = null;
		MemoryImageSource mis = null;
		Image image = null;
		int numWorkers = 0, nextNumWorkers = 0, nextNumWorkersFrame = 0;
	    toGraphics.write (new GraphicsProtocol.SetPaintable (displayList));
	    fromGraphics.read ();
		while (true) {
			// Synchronize with farmer
			//System.out.println ("Harvester: synchronize with farmer");
			ImagePacket ip = (ImagePacket)fromFarmer.read ();
			if (ip != null) {
				System.out.println ("Harvester: updating image configuration");
				width = ip.width;
				height = ip.height;
				pixels = new int[width * height];
				mis = new MemoryImageSource (width, height, pixels, 0, width);
				mis.setAnimated (true);
				toGraphics.write (new GraphicsProtocol.MakeMISImage (mis));
  				image = (Image)fromGraphics.read ();
  				displayList.set (new GraphicsCommand.DrawImage (image, 0, 0, width, height));
  				if (ip.numWorkers != numWorkers) {
  					nextNumWorkers = ip.numWorkers;
  					nextNumWorkersFrame = ip.frame;
  				}
			}
			// Read in the work packets for the current frame
			if (nextNumWorkersFrame == currentFrame) {
				numWorkers = nextNumWorkers;
			}
			int i = 0;
			try {
				//System.out.println ("Harvester: read frame " + currentFrame + " results");
    			for (; i < numWorkers; i++) {
    				ResultPacket rp = (ResultPacket)fromWorkers[currentFrame % fromWorkers.length].read ();
    				rp.expand ();
    				int j = rp.offset * width, x = 0;
    				for (int kR = 0, kG = rp.color.length / 3, kB = (kG << 1); kB < rp.color.length; ) {
    					int a = (rp.color[kR++] << 16) & 0xFF0000;
    					a |= (rp.color[kG++] << 8) & 0xFF00;
    					pixels[j++] = 0xFF000000 | a | (rp.color[kB++] & 0xFF);
    					if (++x >= width) {
    						j += (rp.step - 1) * width;
    						x = 0;
    					}
    				}
    				rp.discard ();
    			}
    			mis.newPixels ();
			} catch (ArrayIndexOutOfBoundsException e) {
				System.out.println ("Harvester: array index problems");
				// Image has probably changed size. I don't care for elegance so just skip any outstanding packets
				i++;
				for (; i < numWorkers; i++) {
    				fromWorkers[currentFrame % fromWorkers.length].read ();
				}
			}
			currentFrame++;
			counter.click ();
		}
	}

}
