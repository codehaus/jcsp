package org.jcsp.lang;

class PoisonableAny2OneChannelImpl extends Any2OneImpl
{
	PoisonableAny2OneChannelImpl(int _immunity) {
		super(new PoisonableOne2OneChannelImpl(_immunity));
	}
}
