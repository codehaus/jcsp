    //////////////////////////////////////////////////////////////////////
    //                                                                  //
    //  JCSP ("CSP for Java") Libraries                                 //
    //  Copyright (C) 1996-2006 Peter Welch and Paul Austin.            //
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
    //  Author contact: P.H.Welch@ukc.ac.uk                             //
    //                                                                  //
    //                                                                  //
    //////////////////////////////////////////////////////////////////////

package org.jcsp.lang;

/**
 * This is a process that immediately terminates <I>and</I>
 * a {@link Guard} that is always ready.
 * <H2>Description</H2>
 * <TT>Skip</TT> is a process that starts, engages in no events, performs no
 * computation and terminates.  It can also be used as a {@link Guard} in
 * an <A HREF="Alternative.html#Polling"><TT>Alternative</TT></A>
 * that is always ready.
 * <P>
 * <I>Note: the process form is included for completeness -- it is one of
 * the fundamental primitives of <B>CSP</B>.</I>
 *
 * @see org.jcsp.lang.Stop
 *
 * @author P.D.Austin
 * @author P.H.Welch
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
