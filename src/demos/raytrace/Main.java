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
import org.jcsp.util.*;
import org.jcsp.net.*;
import org.jcsp.net.tcpip.*;
import org.jcsp.net.cns.*;
import java.awt.*;
import java.net.*;
import java.awt.event.*;

/**
 * @author Quickstone Technologies Limited
 */
public class Main {
	
	public static final int BUFFERING = 5;//PROD:5
	
	private static final int DEFAULT_WIDTH = 640,
								DEFAULT_HEIGHT = 480;

	public static void main (String[] args) throws Exception {
		
		// Get the command line
		String cnsServer = null;
		int width = DEFAULT_WIDTH;
		int height = DEFAULT_HEIGHT;
		boolean fullScreen = false;
		for (int i = 0; i < args.length; i++) {
			if (args[i].equals ("-width")) {
				width = Integer.parseInt (args[++i]);
			} else if (args[i].equals ("-height")) {
				height = Integer.parseInt (args[++i]);
			} else if (args[i].equals ("-fullscreen")) {
				fullScreen = true;
			} else {
				cnsServer = args[i];
			}
		}
		if (cnsServer == null) {
			/*Ask.app (
				"Ray Tracer",
				"Implements a basic ray-tracing algorithm incorporating reflection, refraction and texture mapped " +
				"surfaces, distributed using a farmer/worker/harvester approach. This is the farmer and harvester " +
				"implementation. A set of channels are registered with a CNS which one or more workers can use to " +
				"join the network. This approach allows workers to dynamically join (and leave) to demonstrate " +
				"the performance/scaleability as the network grows.");
			Ask.addPrompt ("CNS address");
			Ask.show ();*/
            NodeKey key =
              Node.getInstance().init(
                new TCPIPAddressID(
                  InetAddress.getLocalHost().getHostAddress(),
                  TCPIPCNSServer.DEFAULT_CNS_PORT,
                  true));
			CNS.install(key);
	        NodeAddressID cnsAddress = Node.getInstance().getNodeID().getAddresses()[0];
	        CNSService.install(key, cnsAddress);
		} else {
			Node.getInstance ().init (new TCPIPNodeFactory (cnsServer));
		}
		
		// Establish the NET channels
		final NetAltingChannelInput workers2demux = CNS.createNet2One ("org.jcsp.demos.raytrace.demux");
		final NetAltingChannelInput workerJoin = CNS.createNet2One ("org.jcsp.demos.raytrace.join");
		final NetAltingChannelInput workerLeave = CNS.createNet2One ("org.jcsp.demos.raytrace.leave");
		System.out.println ("Main: waiting for initial worker");
		NetChannelLocation ncl = (NetChannelLocation)workerJoin.read ();
		final NetChannelOutput[] toWorkers = new NetChannelOutput [] { NetChannelEnd.createOne2Net (ncl) };
		
		// Widget control channels
		final One2OneChannel frameControl = Channel.one2one ();
							  
		// Widget event channels
		final One2OneChannel frameEvent = Channel.one2one (new OverWritingBuffer (10)),
						      canvasEvent = Channel.one2one (new OverWritingBuffer (10)),
						      mouseEvent = Channel.one2one (new OverWritingBuffer (1)),
						      keyEvent = Channel.one2one (new OverWritingBuffer (10));
		
		// Graphics channels
        final Any2OneChannel toGraphics = Channel.any2one();
        final One2OneChannel fromGraphics = Channel.one2one();
        
        // FWH network
        final One2OneChannel farmer2harvester = Channel.one2one (),
        					  frameDemux[] = Channel.one2oneArray (BUFFERING, new InfiniteBuffer ()),
        					  ui2farmer = Channel.one2one ();

        // Set up the canvas
        final ActiveCanvas activeCanvas = new ActiveCanvas ();
        activeCanvas.addComponentEventChannel (canvasEvent.out ());
        activeCanvas.setGraphicsChannels(toGraphics.in(), fromGraphics.out());
        activeCanvas.setSize(width, height);
        activeCanvas.addMouseMotionEventChannel (mouseEvent.out ());
        activeCanvas.addKeyEventChannel (keyEvent.out ());

        // Set up the frame
        final ActiveFrame activeFrame = new ActiveFrame (frameControl.in (), frameEvent.out (), "Ray Tracing Demonstration");
        activeFrame.add (activeCanvas);
        
        // Try and go full screen ?
        if (fullScreen) {
            final GraphicsEnvironment graphEnv = GraphicsEnvironment.getLocalGraphicsEnvironment ();
            final GraphicsDevice graphDev = graphEnv.getDefaultScreenDevice ();
            try {
                if (graphDev.isFullScreenSupported ()) {
                	activeFrame.setUndecorated (true);
    		        activeFrame.pack ();
                    activeFrame.setVisible (true);
                    activeFrame.toFront ();
                	graphDev.setFullScreenWindow (activeFrame);
                	graphDev.setDisplayMode (new DisplayMode (width, height, 32, DisplayMode.REFRESH_RATE_UNKNOWN));
                }
            } catch (Throwable e) {
            	System.out.println ("Full screen display failed - available modes:");
            	DisplayMode[] dms = graphDev.getDisplayModes ();
            	outer: for (int i = 0; i < dms.length; i++) {
            		if (dms[i].getBitDepth () >= 24) {
            			for (int j = 0; j < i; j++) {
            				if ((dms[j].getWidth () == dms[i].getWidth ())
            				 && (dms[j].getHeight () == dms[i].getHeight ())
            				 && (dms[j].getBitDepth () == dms[i].getBitDepth ())) continue outer;
            			}
    	       			System.out.println ("\t" + dms[i].getWidth () + "x" + dms[i].getHeight () + "x" + dms[i].getBitDepth ());
            		}
            	}
            	activeFrame.dispose ();
            	activeFrame.setUndecorated (false);
            }
        }
        if (!activeFrame.isDisplayable ()) {
        	activeFrame.pack ();
        	activeFrame.setVisible (true);
        	activeFrame.toFront ();
        }
        
        // Widget event ALT
        final Alternative alt = new Alternative (new Guard[] { frameEvent.in (), canvasEvent.in (), mouseEvent.in (), keyEvent.in () });
		
		final Parallel par = new Parallel (new CSProcess[] {
			activeCanvas,
			activeFrame,
			new CSProcess () {
				public void run () {
					while (true) {
						ComponentEvent e;
						switch (alt.select ()) {
							case 0 : // FRAME
        						e = (ComponentEvent)frameEvent.in ().read ();
        						switch (e.getID ()) {
        							case WindowEvent.WINDOW_CLOSING :
        								System.out.println ("AWT: closing window");
        								toGraphics.out ().write (null);
        								frameControl.out ().write (null);
        								ui2farmer.out ().write (null);
        								return;
        						}
        						break;
							case 1 : // CANVAS
        						e = (ComponentEvent)canvasEvent.in ().read ();
        						switch (e.getID ()) {
        							case ComponentEvent.COMPONENT_RESIZED :
        								Dimension newSize = activeCanvas.getSize ();
        								if ((newSize.getWidth () > 4) && (newSize.getHeight () > 4)) {
        									System.out.println ("AWT: canvas resized to " + (int)newSize.getWidth () + ", " + (int)newSize.getHeight ());
        									ui2farmer.out ().write (new Farmer.ResizeMessage ((int)newSize.getWidth (), (int)newSize.getHeight ()));
        								}
        								break;
        						}
        						break;
							case 2 : // MOUSE
								e = (ComponentEvent)mouseEvent.in ().read ();
								switch (e.getID ()) {
									case MouseEvent.MOUSE_MOVED :
										ui2farmer.out ().write (e);
										break;
								}
								break;
							case 3 : // KEY
								e = (ComponentEvent)keyEvent.in ().read ();
								switch (e.getID ()) {
									case KeyEvent.KEY_PRESSED :
										ui2farmer.out ().write (e);
										break;
								}
								break;
						}
					}
				}
			},
			new Farmer (toWorkers, farmer2harvester.out (), ui2farmer.in (), workerJoin, workerLeave),
			new FrameDeMux (workers2demux, Channel.getOutputArray (frameDemux)),
			new Harvester (Channel.getInputArray (frameDemux), farmer2harvester.in (), toGraphics.out (), fromGraphics.in ())
		});
		
		par.run ();
		
	}
	
}
