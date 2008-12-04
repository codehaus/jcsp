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
 * This example is based on
 * <A HREF="http://rsb.info.nih.gov/plasma/">Sam's Java Plasma Applet</A>
 * by <A HREF="mailto:t-sammar@microsoft.com">Sam Marshall</A>.  It was modified to use
 * 8-bit images by <A HREF="mailto:M.vanGangelen@element.nl">Menno van Gangelen</A>.
 * This JCSP demonstration is by <A HREF="mailto:P.H.Welch@kent.ac.uk">Peter Welch</A>.
 *
 * @author P.H. Welch
 */

public class PlasmaMain extends ActiveApplet {

  public static final String TITLE = "Plasma";
  public static final String DESCR =
  	"A benchmark for measuring the frame rate of the ActiveCanvas for a simple animation. The thread " +
  	"generating the images responds to changes in the parameters from events sent by the user interface " +
  	"controls.";

  public static final int minWidth = 768;
  public static final int minHeight = 512;

  public static final int maxWidth = 1024;
  public static final int maxHeight = 768;

  public void init () {
    setProcess (new PlasmaNetwork (this));
  }

  public static void main (String[] args) {

    Ask.app (TITLE, DESCR);
    Ask.addPrompt ("width", minWidth, maxWidth, minWidth);
    Ask.addPrompt ("height", minHeight, maxHeight, minHeight);
    Ask.show ();
    final int width = Ask.readInt ("width");
    final int height = Ask.readInt ("height");
    Ask.blank ();

    final ActiveClosingFrame activeClosingframe = new ActiveClosingFrame (TITLE);
    final ActiveFrame activeFrame = activeClosingframe.getActiveFrame ();
    activeFrame.setSize (width, height);

    final PlasmaNetwork plasmaNetwork = new PlasmaNetwork (activeFrame);

    //activeFrame.pack ();
    activeFrame.setLocation ((maxWidth - width)/2, (maxHeight - height)/2);
    activeFrame.setVisible (true);
    activeFrame.toFront ();

    new Parallel (
      new CSProcess[] {
        activeClosingframe,
        plasmaNetwork
      }
    ).run ();

  }

}
