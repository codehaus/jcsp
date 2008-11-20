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

/**
 * @author P.H. Welch
 */
public class AbortControl implements CSProcess {
 
  private final AltingChannelInput fromButton;
  private final ChannelOutput toButton;
  private final AltingChannelInputInt cancel;
  private final ChannelOutputInt abort;
 
  public AbortControl (final AltingChannelInput fromButton,
                       final ChannelOutput toButton,
                       final AltingChannelInputInt cancel,
                       final ChannelOutputInt abort) {
    this.fromButton = fromButton;
    this.toButton = toButton;
    this.cancel = cancel;
    this.abort = abort;
  }

  public void run () {

    final Alternative alt = new Alternative (new Guard[] {fromButton, cancel});
    final int BUTTON = 0;
    final int CANCEL = 1;

    while (true) {

      cancel.read ();                       // get ready for next launch

      while (fromButton.pending ()) fromButton.read ();  // debounce
      toButton.write ("ABORT");
      toButton.write (Boolean.TRUE);        // enable the button

      switch (alt.priSelect ()) {
        case BUTTON:                        // abort button pressed
          abort.write (0);                  // try to abort the rocket
          toButton.write (Boolean.FALSE);   // disable the button
          toButton.write ("abort");
          fromButton.read ();               // clear the signal
          cancel.read ();                   // acknowledgement of the abort
        break;
        case CANCEL:                        // the rocket has been fired
          cancel.read ();                   // clear the signal
          abort.write (0);                  // acknowledge the firing
          toButton.write (Boolean.FALSE);   // disable the button
          toButton.write ("abort");
        break;
      }

    }

  }

}
