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
import java.awt.*;
import java.awt.image.*;
import java.net.*;

/**
 * @author Quickstone Technologies Limited
 */
abstract class Sky {
	
	public static int width = -1, height = -1;
	public static int radius;
	public static int centre;
	
	public static int[] data;
	
	private static class Notify implements ImageObserver {
		
		public int width = -1, height = -1;

		private Barrier bar;
		
		public Notify (Barrier bar) {
			this.bar = bar;
		}
		
		public boolean imageUpdate (Image img, int infoFlags, int x, int y, int width, int height) {
			if ((infoFlags & (ERROR | ABORT)) != 0) {
				System.out.println ("Sky: image size error");
				bar.resign ();
				return false;
			}
			if ((infoFlags & WIDTH) != 0) {
				this.width = width;
			}
			if ((infoFlags & HEIGHT) != 0) {
				this.height = height;
			}
			if ((this.width == -1) || (this.height == -1)) {
				return true;
			} else {
				bar.resign ();
				return false;
			}
		}
		
	}
	
	static {
		try {
    		//System.out.println ("Sky: loading image");
    		final Toolkit tk = Toolkit.getDefaultToolkit ();
    		final URL url = Sky.class.getClassLoader ().getResource ("com/quickstone/jcsp/demos/raytrace/clouds.jpg");
    		if (url == null) throw new NullPointerException ();
    		//System.out.println (url.toString ());
    		final Image img = tk.getImage (url);
    		final Barrier bar = new Barrier (2);
    		final Notify nfy = new Notify (bar);
    		nfy.width = width = img.getWidth (nfy);
    		nfy.height = height = img.getHeight (nfy);
    		if ((width == -1) || (height == -1)) {
    			//System.out.println ("Sky: waiting for async load to complete");
    			bar.sync ();
    			width = nfy.width;
    			height = nfy.height;
    		}
    		if ((width == -1) || (height == -1)) {
    			throw new Exception ();
    		} else {
    			//System.out.println ("Sky: texture " + width + ", " + height);
	    		data = new int[width * height];
	    		final PixelGrabber pg = new PixelGrabber (img, 0, 0, width, height, data, 0, width);
	    		pg.grabPixels ();
    		}
		} catch (Exception e) {
			width = 256;
			height = 256;
			data = new int[width * height];
			for (int i = 0 ; i < width * height; i++) {
				data[i] = i;
			}
		} finally {
    		if (width < height) {
	    		radius = width >> 1;
    		} else {
	    		radius = height >> 1;
    		}
    		centre = (width >> 1) + (height >> 1) * width;
    		//System.out.println ("Sky: ready");
		}
	}

}
