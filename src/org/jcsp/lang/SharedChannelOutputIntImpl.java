package org.jcsp.lang;

public class SharedChannelOutputIntImpl implements SharedChannelOutputInt {
	
	private ChannelInternalsInt channel;
	private int immunity;
	
	SharedChannelOutputIntImpl(ChannelInternalsInt _channel, int _immunity) {
		channel = _channel;
		immunity = _immunity;
	}

	public void write(int object) {
		channel.write(object);

	}

	public void poison(int strength) {
		if (strength > immunity) {
			channel.writerPoison(strength);
		}
	}

}