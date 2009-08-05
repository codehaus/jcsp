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
 * <p>
 * Defines an interface for a client end of a connection that
 * can be shared by multiple clients.
 * </p>
 * <p>
 * This object cannot itself be shared between concurrent processes
 * but duplicate objects can be generated that can be used by
 * multiple concurrent processes. This can be achieved using
 * the <code>{@link #duplicate()}</code> method.
 * </p>
 * <p>
 * See <code>{@link ConnectionClient}</code> for a fuller explanation
 * of how to use connection client objects.
 * </p>
 *
 * @author Quickstone Technologies Limited
 */
public interface SharedConnectionClient<T> extends ConnectionClient<T>
{
    /**
     * Returns a duplicates <code>SharedConnectionClient</code> object
     * which may be used by another process to this instance.
     *
     * @return a duplicate <code>SharedConnectionClient</code> object.
     */
    public SharedConnectionClient<T> duplicate();
}
