//{{{ package and import statements
package org.jcsp.lang;

import java.util.*;
//}}}
//{{{ public class GuardGroup extends Guard
public class GuardGroup extends Guard {

	//{{{ private static fields
	private static Vector processBarrierList;
	//}}}

	//{{{ private fields
	private Guard[] guards;
	private AltableBarrier[] barriers;
	private Alternative parent;
	private Object lastReadyGuard;
	//}}}

	//{{{ public fields
	//}}}

	//{{{ static block
	static {
		processBarrierList = new Vector();
	}
	//}}} 

	//{{{ public GuardGroup(Guard[] guards, AltableBarrier[] barriers)
	public GuardGroup (Guard[] guards, AltableBarrier[] barriers) {

		this.guards = guards;
		this.barriers = barriers;
	}
	//}}}


	//{{{ private methods
	//{{{ 1 private void expandEqualGreaterList(int processID)
	//}}}
	//{{{ 2 private void clearEqualGreaterList(int processID)
	//}}}
	//{{{ 3 private Guard checkForReadyGuards()
	//}}}
	//{{{ 4 private void eliminateUnreadyBarriers()
	//}}}
	//{{{ 5 private AltableBarrier selectBarrier()
	//}}}
	//{{{ 6 private AltableBarrier waitOnBarrier()
	//}}}

	//{{{ public Object anyReady()
	//}}}
	//}}}
	
	//{{{ methods inherited by Guard
	//{{{ boolean enable(Alternative alt)
	boolean enable(Alternative alt) {
		// now has parent, assign
		parent = alt;

		// expand list of barriers of equal or greater priority barriers
		expandEqualGreaterList(0); // FIXEME getprocessID from somewhere, maybe use the Alternative Object itself as a unique identifier(?)

		// check if any Guards are ready
		Object o = anyReady();
		if (o == null) {
			return false;
		} else {
			lastReadyGurd = o;
			return true;
		}

	}
	//}}}
	//{{{ boolean disable()
	boolean disable() {
		// check if there are any ready guards
		Object o = anyReady();

		// clear the process' list of equal or greater ranked barriers
		clearEqualGreaterList(0);//FIXME as appears in enable

		// parent no longer known
		parent = null;

		// return block
		if (o == null) {
			return false;
		} else {
			lastReadyGuard = o;
			return true;
		}
	}
	//}}}
	//}}}
}
//}}}
