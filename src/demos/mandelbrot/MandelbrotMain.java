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
import org.jcsp.demos.util.Ask;

/**
 * @author P.H. Welch
 */
public class MandelbrotMain extends ActiveApplet {

  public static final String TITLE = "Mandelbrot";
  public static final String DESCR =
    	"Generates the mandelbrot set in an interactive browser window. The browser allows the color scheme to be modified, " +
    	"the number of iterations varied and to zoom into the generated image. This demonstration shows the " +
    	"farmer/worker/harvester approach to parallelisation and the handling of AWT events within the farmer to control " +
    	"the image.\n\n" +
    	"The parameters below specify the size of the generated image in pixels.\n\n" +
    	"To zoom into the image, left click and a box will appear to select an area. Use the up/down cursor keys to adjust " +
    	"the size of this area. Click again to zoom.";

  public static final int minWidth = 640;
  public static final int minHeight = 350;

  public static final int maxWidth = 1024;
  public static final int maxHeight = 768;

  public void init () {
    setProcess (new MandelNetwork (this));
  }

  public static void main (String[] args) {

    Ask.app (TITLE, DESCR);
    Ask.addPrompt ("width", minWidth, maxWidth, 800);
    Ask.addPrompt ("height", minHeight, maxHeight, 600);
    Ask.show ();
    final int width = Ask.readInt ("width");
    final int height = Ask.readInt ("height");
    Ask.blank ();

    final ActiveClosingFrame activeClosingframe = new ActiveClosingFrame ("Mandelbrot Set");
    final ActiveFrame activeFrame = activeClosingframe.getActiveFrame ();
    activeFrame.setSize (width, height);

    final MandelNetwork mandelbrot = new MandelNetwork (activeFrame);

    activeFrame.pack ();
    activeFrame.setLocation ((maxWidth - width)/2, (maxHeight - height)/2);
    activeFrame.setVisible (true);
    activeFrame.toFront ();

    new Parallel (
      new CSProcess[] {
        activeClosingframe,
        mandelbrot
      }
    ).run ();

  }

}
