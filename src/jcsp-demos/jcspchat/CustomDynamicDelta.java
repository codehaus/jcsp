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


import java.util.*;
import org.jcsp.lang.*;
import org.jcsp.plugNplay.*;

/**
 * @author Quickstone Technologies Limited
 */
public final class CustomDynamicDelta implements CSProcess {

  private AltingChannelInput in;
  private AltingChannelInput config;

  private Hashtable hash;
  private Parallel par;

  public CustomDynamicDelta (AltingChannelInput in, AltingChannelInput config) {
    this.in  = in;
    par = new Parallel ();
    hash = new Hashtable ();
    this.config = config;
  }

  public void run () {
    AltingChannelInput[] chans = {config, in};
    Alternative alt = new Alternative (chans);
    while (true) {
      switch (alt.priSelect ()) {
        case 0:
          System.out.println("dd: reading config chan");
          System.out.println("dd: hashtable looks like this:");
          System.out.println(hash);
          Object object = config.read ();

          if (object instanceof ConnectionBundle) {
            ConnectionBundle cb = (ConnectionBundle)object;
            if (hash.containsKey (cb.getUser())) {
              System.out.println("dd: removing chan");
              removeOutputChannel (cb);
            }
            else {
              addOutputChannel (cb);
            }
          }
        break;
        case 1:
          Object message = in.read ();
          Enumeration hashChans = hash.elements ();
          while (hashChans.hasMoreElements ()) {
            ((ProcessWrite) hashChans.nextElement ()).value = message;
          }
          par.run ();
        break;
      }
    }
  }

  private void addOutputChannel (ConnectionBundle cb) {
    ProcessWrite p = new ProcessWrite (cb.getReturnChan());
    par.addProcess (p);
    hash.put (cb.getUser(), p);
  }

  private void removeOutputChannel (ConnectionBundle cb) {
    System.out.println("removing outputchan " + cb.getUser() );
    ProcessWrite p = (ProcessWrite) hash.get (cb.getUser());
    par.removeProcess (p);
    hash.remove (cb.getUser());
    System.out.println("removed");
  }
}
