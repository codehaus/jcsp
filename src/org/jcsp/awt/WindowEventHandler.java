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

import org.jcsp.lang.*;
import java.awt.event.*;

/**
 * @author P.D. Austin and P.H. Welch
 */
class WindowEventHandler implements WindowListener
{
   /**
    * The Channel action event notifications are sent down.
    */
   protected ChannelOutput event;
   
   /**
    * constructs a new WindowEventHandler with the specified output Channel.
    *
    * @param event The Channel to send the event notification down
    */
   public WindowEventHandler(ChannelOutput event)
   {
      this.event  = event;
   }
   
   /**
    * Invoked when the Component the event handler is listening to has the window
    * opened. Notifies the event process that a WindowEvent has
    * occurred by sending the WindowEvent Object. Some notifications will be
    * lost so there are no guarantees that all events generated will be
    * processed.
    *
    * @param e The parameters associated with this event
    */
   public void windowOpened(WindowEvent e)
   {
      event.write(e);
   }
   
   /**
    * Invoked when the Component the event handler is listening to has the window
    * start to close. Notifies the event process that a WindowEvent has
    * occurred by sending the WindowEvent Object. Some notifications will be
    * lost so there are no guarantees that all events generated will be
    * processed.
    *
    * @param e The parameters associated with this event
    */
   public void windowClosing(WindowEvent e)
   {
      event.write(e);
   }
   
   /**
    * Invoked when the Component the event handler is listening to has the window
    * closed. Notifies the event process that a WindowEvent has
    * occurred by sending the WindowEvent Object. Some notifications will be
    * lost so there are no guarantees that all events generated will be
    * processed.
    *
    * @param e The parameters associated with this event
    */
   public void windowClosed(WindowEvent e)
   {
      event.write(e);
   }
   
   /**
    * Invoked when the Component the event handler is listening to has the window
    * iconified. Notifies the event process that a WindowEvent has
    * occurred by sending the WindowEvent Object. Some notifications will be
    * lost so there are no guarantees that all events generated will be
    * processed.
    *
    * @param e The parameters associated with this event
    */
   public void windowIconified(WindowEvent e)
   {
      event.write(e);
   }
   
   /**
    * Invoked when the Component the event handler is listening to has the window
    * deiconified. Notifies the event process that a WindowEvent has
    * occurred by sending the WindowEvent Object. Some notifications will be
    * lost so there are no guarantees that all events generated will be
    * processed.
    *
    * @param e The parameters associated with this event
    */
   public void windowDeiconified(WindowEvent e)
   {
      event.write(e);
   }
   
   /**
    * Invoked when the Component the event handler is listening to has the window
    * activated. Notifies the event process that a WindowEvent has
    * occurred by sending the WindowEvent Object. Some notifications will be
    * lost so there are no guarantees that all events generated will be
    * processed.
    *
    * @param e The parameters associated with this event
    */
   public void windowActivated(WindowEvent e)
   {
      event.write(e);
   }
   
   /**
    * Invoked when the Component the event handler is listening to has the window
    * deactivated. Notifies the event process that a WindowEvent has
    * occurred by sending the WindowEvent Object. Some notifications will be
    * lost so there are no guarantees that all events generated will be
    * processed.
    *
    * @param e The parameters associated with this event
    */
   public void windowDeactivated(WindowEvent e)
   {
      event.write(e);
   }
}