package org.jcsp.lang;

class PoisonableAny2AnyChannelImpl extends Any2AnyImpl
{
	PoisonableAny2AnyChannelImpl(int _immunity) {
		super(new PoisonableOne2OneChannelImpl(_immunity));
	}
}