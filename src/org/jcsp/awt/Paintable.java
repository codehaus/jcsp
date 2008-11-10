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

import java.awt.Graphics;
import java.awt.Component;

/**
 * <I>Active</I> components may delegate their <TT>paint</TT> and <TT>update</TT>
 * methods to objects implementing this interface.
 * <H2>Description</H2>
 * Objects implementing <TT>Paintable</TT> may be passed to an <I>Active</I>
 * component (such as {@link ActiveCanvas}).  The <I>Active</I> component will
 * then {@link #register <TT>register</TT>} with the <TT>Paintable</TT> object
 * and delegate to it its <TT>paint</TT> and <TT>update</TT> methods.
 * <P>
 * The <TT>Paintable</TT> object may either be passed <I>statically</I>
 * (via the component's {@link ActiveCanvas#setPaintable <TT>setPaintable</TT>}
 * method, before the component starts running) or <I>dynamically</I>
 * (via the component's
 * {@link ActiveCanvas#setGraphicsChannels <TT>toGraphics/fromGraphics</TT>}
 * channels).
 * <P>
 * <I>Note: these operations are currently supported only for</I>
 * {@link ActiveCanvas <TT>ActiveCanvas</TT>}<I> components.</I>
 * <P>
 * A <TT>CSProcess</TT> may choose to implement <TT>Paintable</TT> itself and take
 * responsibility for its own painting/updating.  However, this would break the JCSP
 * design pattern that the thread(s) of control within a running process have exclusive
 * access to the process state (since painting/updating is actually done by the
 * Java <I>event thread</I>).  It is, therefore,  better to delegate this task to
 * a different (and passive) object such as a {@link DisplayList}.
 *
 * @see org.jcsp.awt.ActiveCanvas
 * @see org.jcsp.awt.DisplayList
 *
 * @author P.H. Welch
 */

public interface Paintable
{
   /**
    * Register the <TT>Component</TT> that will delegate its <TT>paint</TT> and
    * <TT>update</TT> methods here.
    *
    * @param c the Component that will do the delegating.
    */
   public void register(final Component c);
   
   /**
    * This is the call-back delegated here by the registered <TT>Component</TT>.
    * It will normally be the JVM <I>event thread</I> that is making this call.
    *
    * @param g the graphics context for the painting.
    */
   public void paint(final Graphics g);
   
   /**
    * This is the call-back delegated here by the registered <TT>Component</TT>.
    * It will normally be the JVM <I>event thread</I> that is making this call.
    *
    * @param g the graphics context for the painting.
    */
   public void update(final Graphics g);
}