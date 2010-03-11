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
	public BarrierFace bf;

	public AltableBarrier[] guards;
	public Object lock;
	//}}}
	
	//{{{ constructor
	public GuardGroup(AltableBarrier[] guards) {
		this.guards = guards;

		selectedBarrier = null;
		parent = null;
		lock = null;
		bf = null;
	}
	//}}}
	
	//{{{ extends Guard
	//{{{ public boolean enable(Alternative alt)
	boolean enable(Alternative alt) {
		claimLock();
		parent = alt;

		createBarrierFace(); //create BarrierFace if neccesary

		// select a barrier between top and bottomIndexes
		AltableBarrier ab = selectBarrier();

		return false;
	}
	//}}}
	//{{{ public boolean disable()
	boolean disable() {

		// do other stuff
		removeBarrierFace(); //remove BarrierFace if this is last Guard
					//group
		
		parent = null;		
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
	//{{{ private void createBarrierFace()
	private void getBarrierFace() {
		// get the BarrierFace associated with the parent ALT
		bf = BarrierFace.faces.get(parent);

		if (bf == null) {
			// new BarrierFace needs to be created
			bf = BarrierFace(parent);
			bf.topIndex = 0;
			bf.bottomIndex = 0;
		} else {
			bf.topIndex = 0;
			bf.bottomIndex++;
		}
	}
	//}}}
	//{{{ private void removeBarrierFace()
	private void removeBarrierFace() {
		bf.bottomIndex--;
		
		if (bf.bottomIndex < 0) {
			// no more GuardGroups to disable in ALT, dispose of bf
			bf.dispose();
		}
		
		bf = null;
	}
	//}}}
	private AltableBarrier selectBarrier() {
		Vector guardGroups = bf.guardGroups();
		
		for (int i = bf.topIndex; i <= bf.bottomIndex; i++) {
			GuardGroup gg = (GuardGroup) guardGroups.get(i);

			AltableBarrier ab = selectBarrier(gg);

			if (ab != null) {
				bf.topIndex = i;
				return ab;
			}
		}
		return null;
	}

	private AltableBarrier selectBarrier (GuardGroup gg) {
		
	}
	//}}}
	
}
//}}}
