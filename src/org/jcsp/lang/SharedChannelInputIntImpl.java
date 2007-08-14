package org.jcsp.lang;

class SharedChannelInputIntImpl implements SharedChannelInputInt {

	private ChannelInternalsInt channel;
	private int immunity;
	
	SharedChannelInputIntImpl(ChannelInternalsInt _channel, int _immunity) {
		channel = _channel;
		immunity = _immunity;
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