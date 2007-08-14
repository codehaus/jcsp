package org.jcsp.lang;

import org.jcsp.util.ChannelDataStore;

class PoisonableBufferedAny2OneChannel extends Any2OneImpl
{
	PoisonableBufferedAny2OneChannel(ChannelDataStore _data, int _immunity) {
		super(new PoisonableBufferedOne2OneChannel(_data,_immunity));
	}
}
