package org.jcsp.lang;

import org.jcsp.util.ChannelDataStore;

class PoisonableBufferedAny2AnyChannel extends Any2AnyImpl
{
	PoisonableBufferedAny2AnyChannel(ChannelDataStore _data, int _immunity) {
		super(new PoisonableBufferedOne2OneChannel(_data,_immunity));
	}
}