package org.jcsp.lang;

class AltingChannelInputIntImpl extends AltingChannelInputInt {

	private ChannelInternalsInt channel;
	private int immunity;
	
	AltingChannelInputIntImpl(ChannelInternalsInt _channel, int _immunity) {
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

	public int read() {
		return channel.read();
	}

	public int startRead() {
		return channel.startRead();
	}

	public void poison(int strength) {
		if (strength > immunity) {
			channel.readerPoison(strength);
		}
	}

}