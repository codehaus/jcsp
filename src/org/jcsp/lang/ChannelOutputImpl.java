package org.jcsp.lang;

public class ChannelOutputImpl implements ChannelOutput {
	
	private ChannelInternals channel;
	private int immunity;
	
	ChannelOutputImpl(ChannelInternals _channel, int _immunity) {
		channel = _channel;
		immunity = _immunity;
	}

	public void write(Object object) {
		channel.write(object);

	}

	public void poison(int strength) {
		if (strength > immunity) {
			channel.writerPoison(strength);
		}
	}

}
