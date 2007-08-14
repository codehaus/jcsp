package org.jcsp.lang;

class RejectableChannelInputImpl extends ChannelInputImpl implements RejectableChannelInput {	
	
	public RejectableChannelInputImpl(ChannelInternals _channel, int _immunity) {
		super(_channel, _immunity); 
	}

	public void reject()
    {
    	super.poison(Integer.MAX_VALUE);
    }
}
