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
import org.jcsp.demos.util.Ask;

/**
 * @author P.H. Welch
 */
public class BounceMain extends ActiveApplet {

  public static final int minWidth = 300;
  public static final int minHeight = 100;

  public static final int maxWidth = 1024;
  public static final int maxHeight = 768;

  public static final Params[] params =
    {new Params ("images/cube/", 1, 5, ".gif", Color.white),
     new Params ("images/duke/", 1, 17, ".gif", Color.lightGray),
     new Params ("images/earth/", 1, 30, ".gif", Color.white)};

  public void init () {
    setProcess (new BounceNetwork (params, getDocumentBase (), this));
  }

  public static final String TITLE = "Bounce";
  public static final String DESCR =
  	"Demonstrates animation of images using the ActiveCanvas. Three animation processes are created, each " +
  	"with an image that it will move around. ActiveScroll processes allow the speed of the animation to be " +
  	"controlled by sending messages to the animator process.";

  public static void main (String[] args) {

    Ask.app (TITLE, DESCR);
    Ask.addPrompt ("width", minWidth, maxWidth, 640);
    Ask.addPrompt ("height", minHeight, maxHeight, 480);
	Ask.show ();
    final int width = Ask.readInt ("width");
    final int height = Ask.readInt ("height");
    Ask.blank ();

    final ActiveClosingFrame activeClosingframe = new ActiveClosingFrame (TITLE);
    final ActiveFrame activeFrame = activeClosingframe.getActiveFrame ();
    activeFrame.setSize (width, height);

    final BounceNetwork bounceNetwork = new BounceNetwork (params, null, activeFrame);

    activeFrame.pack ();
    activeFrame.setLocation ((maxWidth - width)/2, (maxHeight - height)/2);
    activeFrame.setVisible (true);
    activeFrame.toFront ();

    new Parallel (
      new CSProcess[] {
        activeClosingframe,
        bounceNetwork
      }
    ).run ();

  }

  public static class Params {

    public final String path, suffix;
    public final int from, to;
    public final Color background;

    public Params (String path, int from, int to, String suffix,
                   Color background) {
      this.path = path;
      this.from = from;
      this.to = to;
      this.suffix = suffix;
      this.background = background;
    }

 }

}
