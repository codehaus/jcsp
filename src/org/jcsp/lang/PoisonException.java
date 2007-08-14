package org.jcsp.lang;

public class PoisonException extends ChannelDataRejectedException {

	private int strength;
	
	protected PoisonException(int _strength) {
		//super("PoisonException, strength: " + _strength);
		strength = _strength;
	}
	
	public int getStrength() {
		return strength;
	}
	
	
}
