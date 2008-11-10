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
class ItemEventHandler implements ItemListener
{
   /**
    * The Channel down which ItemEvent notifications are sent.
    */
   private ChannelOutput event;
   
   /**
    * constructs a new ItemEventHandler with the specified event Channel.
    *
    * @param event The Channel ItemEvent notifications are sent down.
    */
   public ItemEventHandler(ChannelOutput event)
   {
      this.event  = event;
   }
   
   /**
    * Invoked when an item change occurs on the component the event handler is
    * listening to. Notifies the event process that an ItemEvent has
    * occurred. Some notifications will be lost so there are no guarantees
    * that all events generated will be processed.
    *
    * @param e The parameters associated with this event
    */
   public void itemStateChanged(ItemEvent e)
   {
      event.write(e);
   }
}