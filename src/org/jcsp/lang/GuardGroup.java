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
		if (ab != null) {
			setBarrier(ab);
			ab.attemptSyncrhonisation();
		}

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
	//{{{ private AltableBarrier selectBarrier() 
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
	//}}}

	//{{{ private AltableBarrier selectBarrier(GuardGroup gg)
	/*
	 * This method goes through all of the AltableBarriers in a GuardGroup,
	 * enables them by setting their status to PREPARED and checking which
	 * are PROBABLY_READY prioritising the first of which which has already
	 * been selected by another process
	 */
	private AltableBarrier selectBarrier (GuardGroup gg) {
		AltableBarrier ab = null;
		// set status of all to prepared
		for (int i = 0; i < gg.guards.length; i++) {
			AltableBarrier ab = gg.guards[i];
			ab.setStatus(PREPARED);
		}
		// check for preselected barriers
		for (int i = 0; i < gg.guards.length; i++) {
			ab = gg.guards[i];
			if (ab.isSelected()) {
				return ab;
			}	
		}
		// no barriers were preselected, select the first
		// barrier which is PROBABLY_READY
		for (int i = 0; i < gg.guards.length; i++) {
			ab = gg.guards[i];
			if (ab.getState() == PROBABLY_READY) {
				return ab;
			}
		}
		// if here then no barriers were PROBABLY_READY
		// return nothing
		return null;
	}
	//}}}
	//{{{ private void setBarrier(AltableBarrier ab)
	/*
	 * This method Alters the BarrierFace object to reflect the selection
	 * of the AltableBarrier ab.
	 */
	private void setBarrier(AltalbeBarrier ab) {
		bf.selected = ab;	
	}
	//}}}
	//}}}
	
}
//}}}
