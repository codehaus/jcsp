package org.jcsp.lang;

class PoisonableOne2AnyChannelIntImpl extends One2AnyIntImpl
{
	PoisonableOne2AnyChannelIntImpl(int _immunity) {
		super(new PoisonableOne2OneChannelIntImpl(_immunity));
	}
}