//{{{ package and import statements
package org.jcsp.lang;
//}}}
//{{{ public class AltableBarrier
public class AltableBarrier {

	//{{{ constants
	public static final int NOT_READY = 0;
	public static final int NOT_SYNCING_NOW = 1;
	public static final int PROBABLY_READY = 2;
	public static final int SELECTED = 3;
	//}}}
	//{{{ public fields
	public int status;
	//}}}
}
//}}}
