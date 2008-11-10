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

package org.jcsp.awt;

import java.awt.event.*;
import org.jcsp.lang.*;

/**
 * @author P.D. Austin and P.H. Welch
 */
class ActionEventHandler implements ActionListener
{
    /** The Channel down which ActionEvent commands are sent. */
    private ChannelOutput event;

    /**
     * constructs a new ActionEventHandler with the specified output Channel.
     *
     * @param event The Channel to send the event notification down
     */
    public ActionEventHandler(ChannelOutput event) 
    {
        this.event = event;
    }

    /**
     * Invoked when an action occurs on the component. It assumes the
     * event channel is being serviced (e.g. by an overwriting channel).
     *
     * @param e the parameter associated with this event
     */
    public void actionPerformed(ActionEvent e) 
    {
        event.write(e.getActionCommand());
    }
}
