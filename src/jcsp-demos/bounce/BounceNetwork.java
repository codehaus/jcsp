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
import org.jcsp.lang.*;

/**
 * @author P.H. Welch
 */
public class BounceNetwork implements CSProcess {

  private final Bounce[] bounce;

  public BounceNetwork (final BounceMain.Params[] params,
                        final URL documentBase,
                        final Container parent) {

    bounce = new Bounce[params.length];

    final Object[] images = new Object[params.length];
    final Panel[] panel = new Panel[params.length];

    for (int i = 0; i < params.length; i++) {
      images[i] = ImageLoader.load (params[i], documentBase);
      bounce[i] = new Bounce ((Image[]) images[i],
                              params[i].background,
                              parent, params.length);
      panel[i] = bounce[i].getPanel ();
    }

    parent.setLayout (new GridLayout (params.length, 1));
    parent.add (panel[0]);
    parent.add (panel[1]);
    parent.add (panel[2]);

  }

  public void run () {

    new Parallel (bounce).run ();

  }

}

