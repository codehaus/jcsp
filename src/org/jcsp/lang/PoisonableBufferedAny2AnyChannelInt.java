package org.jcsp.lang;

import org.jcsp.util.ints.ChannelDataStoreInt;

class PoisonableBufferedAny2AnyChannelInt extends Any2AnyIntImpl
{
	PoisonableBufferedAny2AnyChannelInt(ChannelDataStoreInt _data, int _immunity) {
		super(new PoisonableBufferedAny2AnyChannelInt(_data,_immunity));
	}
}