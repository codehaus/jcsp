//{{{ package and import statements
package org.jcsp.lang;

import java.util.*;
import org.jcsp.lang.*;
//}}}
//{{{ public class GuardGroup extends Guard
public class GuardGroup extends Guard implements ABConstants {

	//{{{ constants
	//}}}
	//{{{ fields
	public AltableBarrier selectedBarrier;
	public Alternative parent;
	public BarrierFace bf;

	public AltableBarrier[] guards;
	public Object lock;

	public AltableBarrier lastSynchronised;
	//}}}
	
	//{{{ constructor
	public GuardGroup(AltableBarrier[] guards) {
		this.guards = guards;

		selectedBarrier = null;
		parent = null;
		lock = null;
		bf = null;

		lastSynchronised = null;
	}
	//}}}
	
	//{{{ extends Guard
	//{{{ public boolean enable(Alternative alt)
	boolean enable(Alternative alt) {
		claimLock();
		parent = alt;

		createBarrierFace(); //create BarrierFace if neccesary

		boolean checking = true;
		AltableBarrier ab = null;
		// stop checking if a barrier syncrhonisation is successful
		// or if the selectBarrier method can't find anything to
		// preemptively wait on
		while (checking) {
			// select a barrier between top and bottomIndexes
			ab = selectBarrier();
			if (ab != null) {
				setBarrier(ab);
				// attempt synchronisation and get back whatever
				// barrier (if any) was selected
				ab = ab.attemptSynchronisation();
				if (ab != null) {
					checking = false;
				}
			} else {
				checking = false;
			}
		}
		// if ab is not null make sure the correct GuardGroup knows
		// about it
		if (ab != null) {
			ab.guardGroup.lastSynchronised = ab;
		}

		// report true if there was a successful syncrhonisation
		// it will be up to the disable() methods to report which
		// guard group actually successfully syncrhonised.
		releaseLock();
		return (ab != null);
	}
	//}}}
	//{{{ public boolean disable()
	boolean disable() {

		// do other stuff
		removeBarrierFace(); //remove BarrierFace if this is last Guard
					//group
		
		parent = null;
		AltableBarrier temp = lastSynchronised;
		releaseLock();
		return (lastSynchronised != null);
	}
	//}}}
	//{{{ public AltableBarrier lastSynchronised()
	public AltableBarrier lastsynchronised() {
		AltableBarrier temp = lastSynchronised;
		lastSynchronised = null;
		return temp;
	}
	//}}}
	//}}}
	
	//{{{ private methods
	//{{{ private void claimLock() 
	public static void claimLock() {
		AltableBarrierBase.tokenGiver.in().read();
	}
	//}}}
	//{{{ private void releaseLock()
	public static void releaseLock(){
		AltableBarrierBase.tokenReciever.out().write(null);
	}
	//}}}
	//{{{ private void createBarrierFace()
	private void createBarrierFace() {
		//make sure the lastSyncrhonised barrier has been set to null
		lastSynchronised = null;

		// get the BarrierFace associated with the parent ALT
		bf = (BarrierFace) BarrierFace.faces.get(parent);

		if (bf == null) {
			// new BarrierFace needs to be created
			bf = new BarrierFace(parent);
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
		Vector guardGroups = bf.guardGroups;
		
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
			ab = gg.guards[i];
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
			if (ab.getStatus() == PROBABLY_READY) {
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
	private void setBarrier(AltableBarrier ab) {
		bf.selected = ab;	
	}
	//}}}
	//}}}
	
}
//}}}
