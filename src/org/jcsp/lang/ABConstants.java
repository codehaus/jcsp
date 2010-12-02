//{{{ package and import statements
package org.jcsp.lang;
//}}}

//{{{ public interface ABConstants
public interface ABConstants {
	//{{{ constants
	public static final int PREPARED = 0;
	public static final int UNPREPARED = 1;
	public static final int PICKED = 6;


	public static final int NOT_READY = 10;
	public static final int NOT_SYNCING_NOW = 100;
	public static final int PROBABLY_READY = 11;
	public static final int SELECTED = 12;
	public static final int COMPLETE = 13;

	public static final Object SUCCESS = new Object();
	public static final Object FAILURE = new Object();
	public static final Object TIMEOUT = new Object();

	public static final int WAIT_FOR_LOCK = -1;
	public static final int STEALING = -2;
	public static final int WAITING = -3;
	public static final int BETWEEN_GGS = -4;
	public static final int ALT_MONITOR = -5;
	public static final int POST_ALT_MONITOR = -6;
	public static final int PRE_GATEKEEPER = -7;
	public static final int POST_GATEKEEPER = -8;
	public static final int PRE_DISABLE = -9;
	public static final int IN_DISABLE = -10;
	public static final int END_DISABLE = -11;
	public static final int RESET_BARRIERS = -12;
	public static final int REMOVE_FACE = -13;
	public static final int RETURN_DISABLE = -14;
	//}}}

}
//}}}
