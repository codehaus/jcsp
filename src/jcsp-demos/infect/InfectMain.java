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
public class InfectMain extends ActiveApplet {

  public static final int minWidth = 350;
  public static final int maxWidth = 1024;

  public static final int minHeight = 350;
  public static final int maxHeight = 768;

  public static final int minInfectRate = 0;
  public static final int maxInfectRate = 100;
  public static int initialInfectRate = 25;

  public static final int initialConvertRate  = 80;
  public static final int initialRecoverRate = 99;

  public static final int reinfectRate = 10;

  public static final int initialRenderChoiceIndex = 0;

  public static final int sprayRadius = 20;

  public void init () {
    initialInfectRate = getAppletInt ("rate", minInfectRate, maxInfectRate, initialInfectRate);
    setProcess (
      new InfectNetwork (
        initialInfectRate, initialConvertRate, initialRecoverRate,
        reinfectRate, initialRenderChoiceIndex, sprayRadius, this
      )
    );
  }

  public static void main (String[] args) {

    System.out.println ("\nInfect starting ...\n");

    final int width = Ask.Int ("width = ", minWidth, maxWidth);
    final int height = Ask.Int ("height = ", minHeight, maxHeight);
    System.out.println ();

    initialInfectRate = Ask.Int ("infection rate = ", minInfectRate, maxInfectRate);
    System.out.println ();

    final ActiveClosingFrame activeClosingframe = new ActiveClosingFrame ("Infect");
    final ActiveFrame activeFrame = activeClosingframe.getActiveFrame ();
    activeFrame.setSize (width, height);

    System.out.println ("InfectMain: initialRenderChoiceIndex = " + initialRenderChoiceIndex);

    final InfectNetwork infect =
      new InfectNetwork (
        initialInfectRate, initialConvertRate, initialRecoverRate,
        reinfectRate, initialRenderChoiceIndex, sprayRadius, activeFrame
      );

    activeFrame.pack ();
    activeFrame.setLocation ((maxWidth - width)/2, (maxHeight - height)/2);
    activeFrame.setVisible (true);
    activeFrame.toFront ();

    new Parallel (
      new CSProcess[] {
        activeClosingframe,
        infect
      }
    ).run ();

  }

}
