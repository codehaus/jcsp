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
class ComponentEventHandler implements ComponentListener
{
   /**
    * The Channel action event notifications are sent down.
    */
   private ChannelOutput event;
   
   /**
    * constructs a new ComponentEventHandler with the specified event output
    * Channel.
    *
    * @param event The Channel to send the event notification down
    */
   public ComponentEventHandler(ChannelOutput event)
   {
      this.event  = event;
   }
   
   /**
    * Invoked when the component the event handler is listening to is resized.
    * Notifies the event process that an ComponentEvent has occurred by sending
    * the ComponentEvent Object. Some notifications will be lost so there are no
    * guarantees that all events generated will be processed.
    *
    * @param e The parameters associated with this event
    */
   public void componentResized(ComponentEvent e)
   {
      event.write(e);
   }
   
   /**
    * Invoked when the component the event handler is listening to is moved.
    * Notifies the event process that an ComponentEvent has occurred by sending
    * the ComponentEvent Object. Some notifications will be lost so there are no
    * guarantees that all events generated will be processed.
    *
    * @param e The parameters associated with this event
    */
   public void componentMoved(ComponentEvent e)
   {
      event.write(e);
   }
   
   /**
    * Invoked when the component the event handler is listening to is shown.
    * Notifies the event process that an ComponentEvent has occurred by sending
    * the ComponentEvent Object. Some notifications will be lost so there are no
    * guarantees that all events generated will be processed.
    *
    * @param e The parameters associated with this event
    */
   public void componentShown(ComponentEvent e)
   {
      event.write(e);
   }
   
   /**
    * Invoked when the component the event handler is listening to is hidden.
    * Notifies the event process that an ComponentEvent has occurred by sending
    * the ComponentEvent Object. Some notifications will be lost so there are no
    * guarantees that all events generated will be processed.
    *
    * @param e The parameters associated with this event
    */
   public void componentHidden(ComponentEvent e)
   {
      event.write(e);
   }
}