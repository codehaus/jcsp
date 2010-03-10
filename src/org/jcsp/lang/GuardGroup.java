//{{{ package and import statements
package org.jcsp.lang;

import java.util.*;
import org.jcsp.lang.*;
//}}}
//{{{ public class GuardGroup extends Guard
public class GuardGroup extends Guard {

	//{{{ constants
	//}}}
	//{{{ fields
	public AltableBarrier selectedBarrier;
	public Alternative parent;

	public AltableBarrier[] guards;
	public Object lock;
	//}}}
	
	//{{{ constructor
	public GuardGroup(AltableBarrier[] guards) {
		this.guards = guards;

		selectedBarrier = null;
		parent = null;
		lock = null;
	}
	//}}}
	
	//{{{ extends Guard
	//{{{ public boolean enable(Alternative alt)
	boolean enable(Alternative alt) {
		claimLock();

		parent = alt;

		return false;
	}
	//}}}
	//{{{ public boolean disable()
	boolean disable() {

		// do other stuff
		
		releaseLock();
		return false;
	}
	//}}}
	//}}}
	
	//{{{ private methods
	//{{{ private void claimLock() 
	private void claimLock() {
	}
	//}}}
	//{{{ private void releaseLock()
	private void releaseLock(){}
	//}}}
	//}}}
}
//}}}
