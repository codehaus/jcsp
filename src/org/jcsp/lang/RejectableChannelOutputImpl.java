package org.jcsp.lang;

public class RejectableChannelOutputImpl extends ChannelOutputImpl implements
		RejectableChannelOutput {

	public RejectableChannelOutputImpl(ChannelInternals _channel, int _immunity) {
		super(_channel, _immunity);		
	}

}
