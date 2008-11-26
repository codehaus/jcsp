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
import org.jcsp.net.*;
import org.jcsp.net.cns.*;
import org.jcsp.net.tcpip.*;

import java.net.*;


/**
 * @author Quickstone Technologies Limited
 * @author P.H. Welch (non-networked original code)
 */
public class MandelbrotMain {

  public static final String TITLE = "Mandelbrot Set (distributed)";
  public static final String DESCR =
  	"Demonstates a distributed farmer/worker/harvester parallelisation. The farmer and harvestor processes " +
  	"will run on this JVM. Workers can run on this machine or elsewhere to generate the actual image.";

  private static final int DEFAULT_WIDTH = 640, DEFAULT_HEIGHT = 480;

  public static void main (String[] args) throws Exception {

	// Get arguments
	String cnsServer = null;
	int width = DEFAULT_WIDTH, height = DEFAULT_HEIGHT;
	for (int i = 0; i < args.length; i++) {
		if (args[i].equals ("-width")) {
			width = Integer.parseInt (args[++i]);
		} else if (args[i].equals ("-height")) {
			height = Integer.parseInt (args[++i]);
		} else {
			cnsServer = args[i];
		}
	}
	if (cnsServer == null) {
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

	final ActiveClosingFrame activeClosingFrame = new ActiveClosingFrame ("Distributed Mandelbrot");
    final ActiveFrame activeFrame = activeClosingFrame.getActiveFrame ();
    activeFrame.setSize (width, height);

    final MandelNetwork mandelbrot = new MandelNetwork (activeFrame);

    activeFrame.pack ();
    activeFrame.setVisible (true);
    activeFrame.toFront ();

    new Parallel (
      new CSProcess[] {
        activeClosingFrame,
        mandelbrot
      }
    ).run ();

  }

}
