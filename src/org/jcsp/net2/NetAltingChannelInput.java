package org.jcsp.net2;

import org.jcsp.lang.AltingChannelInput;
import org.jcsp.lang.AltingChannelInputWrapper;

/**
 * A NetChannelInput that may be used as a guard. This class describes the abstract interface of such a channel. To
 * create an instance of this class, use the standard NetChannel factory, or the CNS. For information on the usage of
 * this object, see AltingChannelInput
 * 
 * @see AltingChannelInput
 * @see org.jcsp.lang.ChannelInput
 * @see NetChannelInput
 * @see NetChannel
 * @author Quickstone Technologies
 */
public abstract class NetAltingChannelInput<T>
    extends AltingChannelInputWrapper<T>
    implements NetChannelInput<T>
{

    /**
     * Creates a new NetAltingChannelInput, with the given channel as the guard
     * 
     * @param in
     *            The channel that is used within the alternative
     */
    protected NetAltingChannelInput(AltingChannelInput<T> in)
    {
        super(in);
    }
}
