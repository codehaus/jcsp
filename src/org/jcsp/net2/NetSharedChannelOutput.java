package org.jcsp.net2;

import org.jcsp.lang.SharedChannelOutput;

/**
 * Defines a networked ChannelOutput that is safe to be used by multiple concurrent processes. For more information see
 * NetChannelOutput and SharedChannelOutput. To create an instance, see the relevant factory method.
 * 
 * @see NetChannelOutput
 * @see SharedChannelOutput
 * @see NetChannel
 * @author Kevin Chalmers (updated from Quickstone Technologies)
 */
public interface NetSharedChannelOutput<T>
    extends NetChannelOutput<T>, SharedChannelOutput<T>
{
    // Nothing to add
}
