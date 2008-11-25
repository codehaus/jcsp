
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
import org.jcsp.awt.*;

public class FlasherNetworkSSD implements CSProcess {

  final private long period;
  final private ActiveApplet activeApplet;

  public FlasherNetworkSSD (final long period,
                            final ActiveApplet activeApplet) {
    this.period = period;
    this.activeApplet = activeApplet;
  }
    
  public void run () {

    final One2OneChannel mouseEvent = Channel.one2one ();
    final One2OneChannel appletConfigure = Channel.one2one ();
    final One2OneChannelInt stopStart = Channel.one2oneInt ();
    final One2OneChannelInt destroy = Channel.one2oneInt ();
    final One2OneChannelInt destroyAck = Channel.one2oneInt ();

    activeApplet.addMouseEventChannel (mouseEvent.out());
    activeApplet.setConfigureChannel (appletConfigure.in());
    activeApplet.setStopStartChannel (stopStart.out());
    activeApplet.setDestroyChannels (destroy.out(), destroyAck.in());
    // activeApplet.setDestroyChannels (destroy.out(), destroyAck.in(), -1);

    new Parallel (
      new CSProcess[] {
        activeApplet,
        new FlasherControl (period, mouseEvent.in(), appletConfigure.out()),
        new CSProcess () {
          public void run () {
            while (true) {
              switch (stopStart.in().read ()) {
                case ActiveApplet.STOP:
                  System.out.println ("FlasherNetworkSSD: ActiveApplet.STOP received");
                break;
                case ActiveApplet.START:
                  System.out.println ("FlasherNetworkSSD: ActiveApplet.START received");
                break;
                default:
                  System.out.println ("FlasherNetworkSSD: ActiveApplet.<not STOP/START> received");
                break;
              }
            }
          }
        },
        new CSProcess () {
          public void run () {
            while (true) {
              switch (destroy.in().read ()) {
                case ActiveApplet.DESTROY:
                  System.out.println ("FlasherNetworkSSD: ActiveApplet.DESTROY received");
                  destroyAck.out().write (0);
                break;
                default:
                  System.out.println ("FlasherNetworkSSD: ActiveApplet.<not DESTROY> received");
                break;
              }
            }
          }
        }
      }
    ).run ();

  }

}
