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


import java.awt.*;
import java.net.*;

/**
 * @author P.H. Welch
 */
public class ImageLoader {

  final private static MediaTracker tracker = new MediaTracker (new Canvas ());

  public static Image[] load (BounceMain.Params params, final URL documentBase) {

    final int nImages = (params.to - params.from) + 1;
    if (nImages < 0) return null;

    final Image[] images = new Image[nImages];

    if (documentBase == null) {     // we're an application -- so load from a file
      for (int i = 0; i < nImages; i++) {
        String file = params.path + (i + params.from) + params.suffix;
        images[i] = Toolkit.getDefaultToolkit ().getImage (file);
        System.out.println ("ImageLoader.load: " + file + " " + images[i]);
      }
    } else {                        // we're an applet -- so load from a URL
      try {
        for (int i = 0; i < nImages; i++) {
          URL url = new URL (documentBase, params.path + (i + params.from) + params.suffix);
          images[i] = Toolkit.getDefaultToolkit ().getImage (url);
          System.out.println ("ImageLoader.load: " + url + " " + images[i]);
        }
      } catch (MalformedURLException e) {
        System.out.println ("ImageLoader.load: MalformedURLException" + e);
      }
    }

    for (int i = 0; i < nImages; i++) {
      tracker.addImage (images[i], 0);
    }
    try {
      tracker.waitForAll ();
    }
    catch (InterruptedException e) {
      System.out.println (e);
      System.exit (-1);
    }

    return images;

  }

}
