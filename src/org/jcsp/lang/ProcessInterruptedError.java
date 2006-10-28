    //////////////////////////////////////////////////////////////////////
    //                                                                  //
    //  JCSP ("CSP for Java") Libraries                                 //
    //  Copyright (C) 1996-2001 Peter Welch and Paul Austin.            //
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
    //                  mailbox@quickstone.com                          //
    //                                                                  //
    //////////////////////////////////////////////////////////////////////

package org.jcsp.lang;

/**
 * This is thrown if a process is interrupted whilst blocked during synchronisation
 * - processes should never be interrupted other than in exceptional circumstances
 * where alternative termination is not possible.
 *
 * Some browsers, when shutting down an <I>applet</I>, may do this to processes
 * spawned by an {@link org.jcsp.awt.ActiveApplet} that have not died naturally.
 *
 * Alternatively, this may be raised by processes stopped prematurely as a result of
 * a call to <TT>Parallel.destroy</TT>, or by calling <TT>stop</TT> on the
 * <TT>ProcessManager</TT> responsible for the process (or network).
 *
 * @author P.H.Welch
 */

public class ProcessInterruptedError extends Error
{
    public ProcessInterruptedError(String s)
    {
        super("Process interrupted while " + s);
    }
}
