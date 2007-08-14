package org.jcsp.lang;

import org.jcsp.util.ChannelDataStore;

class PoisonableBufferedOne2AnyChannel extends One2AnyImpl
{
	PoisonableBufferedOne2AnyChannel(ChannelDataStore _data, int _immunity) {
		super(new PoisonableBufferedOne2OneChannel(_data,_immunity));
	}
}