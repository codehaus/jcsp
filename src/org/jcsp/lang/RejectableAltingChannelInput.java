package org.jcsp.lang;

/**
 * Defines an interface for an input channel end that gives the reader the ability to reject instead
 * of accepting pending data.
 *
 * @author Quickstone Technologies Limited
 * 
 * @deprecated This channel is superceded by the poison mechanisms, please see {@link PoisonException}
 */
public abstract class RejectableAltingChannelInput extends AltingChannelInput implements RejectableChannelInput
{
    /**
     * Reject any data pending instead of reading it. The currently blocked writer will receive a
     * <Code>ChannelDataRejectedException</code>.
     */
    public abstract void reject();
}