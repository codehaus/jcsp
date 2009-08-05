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
 * Defines an interface for a connection that can be shared
 * by multiple concurrent clients but used by
 * a single server. The server end of the connection can be
 * used as a guard in an <code>Alternative</code>.
 * </p>
 *
 * @author Quickstone Technologies Limited
 */
public interface Any2OneConnection<T> extends ConnectionWithSharedAltingClient<T>
{
    /**
     * Returns a client end of the connection. This may only be
     * safely used by a single process but further calls will
     * return new clients which may be used by other processes.
     *
     * @return a new <code>SharedAltingConnectionClient</code> object.
     */
    public SharedAltingConnectionClient<T> client();

    /**
     * Returns the server end of the connection.
     *
     * @return the instance of the <code>AltingConnectionServer</code>.
     */
    public AltingConnectionServer<T> server();
}
