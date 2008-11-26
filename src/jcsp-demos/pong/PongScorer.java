
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
    //////////////////////////////////////////////////////////////////////


import org.jcsp.lang.*;

public class PongScorer implements CSProcess {

  private final AltingChannelInputInt[] fromPaddle;
  private final ChannelOutput[] configureLabel;

  public PongScorer (final AltingChannelInputInt[] fromPaddle,
                     final ChannelOutput[] configureLabel) {
    this.fromPaddle = fromPaddle;
    this.configureLabel = configureLabel;
  }

  public void run () {

    final Alternative alt = new Alternative (fromPaddle);
  
    int left = 0;
    int right = 0;

    configureLabel[0].write ("0");
    configureLabel[1].write ("0");

    while (true) {
      switch (alt.fairSelect ()) {
        case 0:
          final int scoreLeft = fromPaddle[0].read ();
          left = (scoreLeft == 0) ? 0 : left + scoreLeft;
          configureLabel[0].write ((new Integer (left)).toString ());
        break;    
        case 1:
          final int scoreRight = fromPaddle[1].read ();
          right = (scoreRight == 0) ? 0 : right + scoreRight;
          configureLabel[1].write ((new Integer (right)).toString ());
        break;
      }
    }

  }

}
    
