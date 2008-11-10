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
class ContainerEventHandler implements ContainerListener
{
   /**
    * The Channel action event notifications are sent down.
    */
   private ChannelOutput event;
   
   /**
    * constructs a new ContainerEventHandler with the specified event output
    * Channel.
    *
    * @param event The Channel to send the event notification down
    */
   public ContainerEventHandler(ChannelOutput event)
   {
      this.event  = event;
   }
   
   /**
    * Invoked when the Container the event handler is listening to has a new
    * Component added to it. Notifies the event process that a
    * ContainerEvent has occurred by sending the ContainerEvent Object.
    * Some notifications will be lost so there are no guarantees that all
    * events generated will be processed.
    *
    * @param e The parameters associated with this event
    */
   public void componentAdded(ContainerEvent e)
   {
      event.write(e);
   }
   
   /**
    * Invoked when the Container the event handler is listening to has a new
    * Component removed from it. Notifies the event process that a
    * ContainerEvent has occurred by sending the ContainerEvent Object.
    * Some notifications will be lost so there are no guarantees that all
    * events generated will be processed.
    *
    * @param e The parameters associated with this event
    */
   public void componentRemoved(ContainerEvent e)
   {
      event.write(e);
   }
}