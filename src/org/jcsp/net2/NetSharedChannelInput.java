package org.jcsp.net2;

import org.jcsp.lang.SharedChannelInput;

/**
 * Defines a networked ChannelInput that is safe to be used by multiple concurrent processes. For more information see
 * NetChannelInput and SharedChannelInput. To create an instance, see the relevant factory method.
 * 
 * @see NetChannelInput
 * @see SharedChannelInput
 * @see NetChannel
 * @author Kevin Chalmers (updated from Quickstone Technologies)
 */
public interface NetSharedChannelInput<T>
    extends SharedChannelInput<T>, NetChannelInput<T>
{
    // Nothing new to declare
}
