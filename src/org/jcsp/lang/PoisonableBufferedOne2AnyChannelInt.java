package org.jcsp.lang;

import org.jcsp.util.ints.ChannelDataStoreInt;

class PoisonableBufferedOne2AnyChannelInt extends One2AnyIntImpl
{
	PoisonableBufferedOne2AnyChannelInt(ChannelDataStoreInt _data, int _immunity) {
		super(new PoisonableBufferedOne2OneChannelInt(_data,_immunity));
	}
}