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
	//}}}

}
//}}}
