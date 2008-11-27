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
 * This is a process that immediately terminates <I>and</I>
 * a {@link Guard} that is always ready.
 * <H2>Description</H2>
 * <TT>Skip</TT> is a process that starts, engages in no events, performs no
 * computation and terminates.
 * <p>
 * It can also be used as a {@link Guard} in
 * an {@link Alternative} that is always ready.
 * This makes it useful for <a href="Alternative.html#Polling"><i>polling</i></a>
 * a set of guards to test if any are ready:
 * include it as the last element of the guard array and
 * {@link Alternative#priSelect() priSelect}.
 * <P>
 * <I>Note: the process is also included for completeness &ndash; it is one of
 * the fundamental primitives of <B>CSP</B>, where it is a unit of sequential
 * composition and parallel interleaving.
 * In JCSP, it is a unit of {@link Sequence}, {@link Parallel} and {@link PriParallel} .</I>
 *
 * @see org.jcsp.lang.Stop
 *
 * @author P.D. Austin
 * @author P.H. Welch
 *
 */

public class Skip extends Guard implements CSProcess
{
    /**
     * Enables this guard.
     *
     * @param alt the Alternative doing the enabling.
     */
    boolean enable(Alternative alt)
    {
        Thread.yield();
        return true;
    }

    /**
     * Disables this guard.
     */
    boolean disable()
    {
        return true;
    }

    /**
     * The main body of this process.
     */
    public void run()
    {
    }
}
