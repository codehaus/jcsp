package org.jcsp.lang;

class RejectableAltingChannelInputImpl extends RejectableAltingChannelInput {	
	
	private ChannelInternals channel;
	private int immunity;
	
	RejectableAltingChannelInputImpl(ChannelInternals _channel, int _immunity) {
		channel = _channel;
		immunity = _immunity;
	}
	
	
	public boolean pending() {
		return channel.readerPending();
	}
	
	boolean disable() {
		return channel.readerDisable();
	}

	boolean enable(Alternative alt) {
		return channel.readerEnable(alt);
	}

	public void endRead() {
		channel.endRead();
	}

	public Object read() {
		return channel.read();
	}

	public Object startRead() {
		return channel.startRead();
	}

	public void poison(int strength) {
		if (strength > immunity) {
			channel.readerPoison(strength);
		}
	}


	public void reject()
    {
    	channel.readerPoison(Integer.MAX_VALUE);
    }
}
