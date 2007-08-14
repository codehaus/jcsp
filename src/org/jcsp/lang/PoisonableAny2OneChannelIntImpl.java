package org.jcsp.lang;

class PoisonableAny2OneChannelIntImpl extends Any2OneIntImpl
{
	PoisonableAny2OneChannelIntImpl(int _immunity) {
		super(new PoisonableOne2OneChannelIntImpl(_immunity));
	}
}