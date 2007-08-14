package org.jcsp.lang;

class PoisonableAny2AnyChannelIntImpl extends Any2AnyIntImpl
{
	PoisonableAny2AnyChannelIntImpl(int _immunity) {
		super(new PoisonableOne2OneChannelIntImpl(_immunity));
	}
}