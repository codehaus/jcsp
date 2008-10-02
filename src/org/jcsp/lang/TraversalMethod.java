package org.jcsp.lang;

import java.util.*;

//{{{ public interface TraversalMethod
public interface TraversalMethod {

	//{{{ public Guard enableALT (ALT alt, Vector enabled)	
	/*
	 * enableALT, this method traverses all guards in an ALT until it finds
	 * one that is ready, returning null if it doesn't find one.
	 * the 'enabled' arguement allows all currently enabled guards to be
	 * stored and to be disabled in reverse order when disabling the ALT
	 *
	 * FIXME don't want the ALT arguement to a concrete class, better would
	 * be an interface or abstract class, allowing easier maintainance of
	 * JCSP in future
	 */
	public Guard enableALT(ALT alt, Vector enabled);
	//}}}

	//{{{ public Guard disableALT
	/*
	 * disable all the currently enabled guards in reverse order and
	 * return the last currently active guard (i.e. the one which was
	 * highest priority).
	 */
	public Guard disableALT(Vector enabled);
	//}}}
}
//}}}
