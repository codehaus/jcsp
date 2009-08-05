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
 * An interface to connection.  This is used by servers which wish to
 * {@link Alternative ALT} over a connection.  Note that you cannot have
 * more than one server serving an AltingConnectionServer.
 *
 * @see ConnectionServer
 * @see ConnectionClient
 * @see Connection
 *
 * @author Quickstone Technologies Limited
 */
public abstract class AltingConnectionServer<T> extends Guard implements ConnectionServer<T>
{
    /**
     * The channel used to ALT over.
     */
    private AltingChannelInput<ConnectionMessage<T>> altingChannel;

    /**
     * Constructor.
     *
     * Note that this is only intended for use by JCSP, and should
     * not be called by user processes.  Users should use one of the
     * subclasses.
     *
     * @param altingChannel The channel used to implement the Guard
     */
    protected AltingConnectionServer(AltingChannelInput<ConnectionMessage<T>> altingChannel)
    {
        this.altingChannel = altingChannel;
    }

    /**
     * Returns the channel used to implement the Guard.
     *
     * Note that this method is only intended for use by
     * JCSP, and should not be called by user processes.
     *
     * Concrete subclasses should override this method to
     * return null, to ensure that the alting channel is
     * kept private.
     *
     * @return The channel passed to the constructor.
     */
    protected AltingChannelInput<ConnectionMessage<T>> getAltingChannel()
    {
        return altingChannel;
    }

    /**
     * <code>ConnectionServer</code> implementations are likely to be
     * implemented over channels. Multiple channels from the client
     * to server may be used; one could be used for the initial
     * connection while another one could be used for data requests.
     *
     * This method allows sub-classes to specify which channel should
     * be the next one to be alted over.
     *
     * @param	chan	the channel to be ALTed over.
     */
    protected void setAltingChannel(AltingChannelInput<ConnectionMessage<T>> chan)
    {
        altingChannel = chan;
    }

    /**
     * Returns true if the event is ready.  Otherwise, this enables the guard
     * for selection and returns false.
     * <P>
     * <I>Note: this method should only be called by the Alternative class</I>
     *
     * @param alt the Alternative class that is controlling the selection
     * @return true if and only if the event is ready
     */
    boolean enable(Alternative alt)
    {
        return altingChannel.enable(alt);
    }

    /**
     * Disables the guard for selection. Returns true if the event was ready.
     * <P>
     * <I>Note: this method should only be called by the Alternative class</I>
     *
     * @return true if and only if the event was ready
     */
    boolean disable()
    {
        return altingChannel.disable();
    }

    /**
     * Returns whether there is an open() pending on this connection. <p>
     *
     * <i>Note: if there is, it won't go away until you accept it.  But if
     * there isn't, there may be one by the time you check the result of
     * this method.</i>
     *
     * @return true only if open() will complete without blocking.
     */
    public boolean pending()
    {
        return altingChannel.pending();
    }
}
