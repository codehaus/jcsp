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
class MouseEventHandler implements MouseListener
{
   /**
    * The channel down which action event notifications are sent.
    */
   private ChannelOutput event;
   
   /**
    * constructs a new MouseEventHandler with the specified output channel
    *
    * @param event the Channel to which to send the event notification
    */
   public MouseEventHandler(ChannelOutput event)
   {
      this.event  = event;
   }
   
   /**
    * Assumes the event channel is being serviced (eg by an overwriting channel).
    *
    * @param e The parameters associated with this event
    */
   public void mouseClicked(MouseEvent e)
   {
      event.write(e);
   }
   
   /**
    * Assumes the event channel is being serviced (eg by an overwriting channel).
    *
    * @param e The parameters associated with this event
    */
   public void mousePressed(MouseEvent e)
   {
      event.write(e);
   }
   
   /**
    * Assumes the event channel is being serviced (eg by an overwriting channel).
    *
    * @param e The parameters associated with this event
    */
   public void mouseReleased(MouseEvent e)
   {
      event.write(e);
   }
   
   /**
    * Assumes the event channel is being serviced (eg by an overwriting channel).
    *
    * @param e The parameters associated with this event
    */
   public void mouseEntered(MouseEvent e)
   {
      event.write(e);
   }
   
   /**
    * Assumes the event channel is being serviced (eg by an overwriting channel).
    *
    * @param e The parameters associated with this event
    */
   public void mouseExited(MouseEvent e)
   {
      event.write(e);
   }
}