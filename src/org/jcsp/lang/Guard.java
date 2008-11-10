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

package org.jcsp.lang;

/**
 * This is the super-class for all {@link Alternative} events selectable by a process.
 * <H2>Description</H2>
 * <TT>Guard</TT> defines an abstract interface to be implemented by events competing
 * for selection by a process executing an {@link Alternative}.  Its methods have
 * only <I>package</I> visibility within <TT>org.jcsp.lang</TT> and are of no concern to
 * <I>users</I> of this package.  Currently, JCSP supports channel inputs, accepts,
 * timeouts and skips as guards.
 * <P>
 * <I>Note: for those familiar with the <I><B>occam</B></I> multiprocessing
 * language, classes implementing </I><TT>Guard</TT><I> correspond to process
 * guards for use within </I><TT>ALT</TT><I> constructs.</I>
 *
 * @see org.jcsp.lang.CSTimer
 * @see org.jcsp.lang.Skip
 * @see org.jcsp.lang.AltingChannelInput
 * @see org.jcsp.lang.AltingChannelInputInt
 * @see org.jcsp.lang.Alternative
 * @author P.D. Austin
 * @author P.H. Welch
 */

public abstract class Guard
{
    /**
     * Returns true if the event is ready.  Otherwise, this enables the guard
     * for selection and returns false.
     * <P>
     * <I>Note: this method should only be called by the Alternative class</I>
     *
     * @param alt the Alternative class that is controlling the selection
     * @return true if and only if the event is ready
     */
    abstract boolean enable(Alternative alt);

    /**
     * Disables the guard for selection. Returns true if the event was ready.
     * <P>
     * <I>Note: this method should only be called by the Alternative class</I>
     *
     * @return true if and only if the event was ready
     */
    abstract boolean disable();
    
    /**
     * Schedules the process performing the given Alternative to run again.
     * This is intended for use by advanced users of the library who want to
     * create their own Guards that are not in the org.jcsp.lang package.
     * 
     * @param alt The Alternative to schedule
     */
    protected void schedule (Alternative alt) {
    	alt.schedule();
    }
}
