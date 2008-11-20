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
import java.io.*;

/**
 * @author Quickstone Technologies Limited
 */

public class ConnectionBundle implements Serializable  {
  private ChannelOutput returnChan;
  private String user;
  private boolean connect;

  public ConnectionBundle(String user, ChannelOutput returnChan, boolean connect) {
    this.user = user;
    this.returnChan = returnChan;
    this.connect = connect;
  }
  public ConnectionBundle(String user, boolean connect) {
    this.user = user;
    this.connect = connect;
    this.returnChan = null;
  }
  public ChannelOutput getReturnChan() {
    return returnChan;
  }
  public String getUser() {
    return user;
  }
  public boolean connect() {
    return connect;
  }
}
