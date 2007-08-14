package org.jcsp.lang;

class PoisonableOne2AnyChannelImpl extends One2AnyImpl
{
	PoisonableOne2AnyChannelImpl(int _immunity) {
		super(new PoisonableOne2OneChannelImpl(_immunity));
	}
}
