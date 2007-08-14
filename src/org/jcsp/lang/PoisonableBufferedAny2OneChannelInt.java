package org.jcsp.lang;

import org.jcsp.util.ints.ChannelDataStoreInt;

class PoisonableBufferedAny2OneChannelInt extends Any2OneIntImpl
{
	PoisonableBufferedAny2OneChannelInt(ChannelDataStoreInt _data, int _immunity) {
		super(new PoisonableBufferedOne2OneChannelInt(_data,_immunity));
	}
}