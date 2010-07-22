//{{{ package and import statements
package org.jcsp.lang;

import java.util.*;
import org.jcsp.lang.*;
import java.util.concurrent.locks.*;
//}}}
//{{{ public class GuardGroup extends Guard
public class GuardGroup extends Guard implements ABConstants {

	//{{{ constants
	//}}}
	//{{{ field
	public AltableBarrier selectedBarrier;
	public Alternative parent;
	public BarrierFace bf;

	public AltableBarrier[] guards;
	public Object lock;

	public AltableBarrier lastSynchronised;
	//}}}

	public static Object lockOwner = null;
	private static ReentrantLock globalLock = new ReentrantLock();
	
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
		System.out.println("enable has been called on " + this);
		//{{{ make sure all AltableBarriers know they belong to this gg
		for (int i = 0; i < guards.length; i++) {
			guards[i].guardGroup = this;
		}
		//}}}

		System.out.println("lock to be claimed");
		claimLock(alt);
		System.out.println("lock has been claimed");
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
				System.out.println("initially selected " + ab);
				ab = ab.attemptSynchronisation();
				if (ab != null) {
					checking = false;
				} else {
					// if you attempted a synchronisation
					// and you get null back it means there
					// was an abort / timeout.  You should
					// reset the waking flag to false
					bf.waking = false;
				}
			} else {
				System.out.println("None were ready in " + this);
				bf.waking = false;
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
		boolean returnThis = (ab != null);
		if (returnThis) {
			disable();
		} else if (!isLastGroup()) {
			releaseLock(alt);
		} else {
			System.out.println("\n\n\n" + this + "is about to wait on the altMonitor ...");
			// not returning true and is last guard group
			// don't release lock.  Do nothing
		}
		System.out.println(this + " is returning " + (ab != null) + ab);
		return (returnThis);
	}

	//}}}
	//{{{ public boolean disable()
	boolean disable() {
		Object key = parent;
		claimLock(key);
		// do other stuff
		// set face.waking to false (process has finished being 
		// woken up)

		AltableBarrier temp = lastSynchronised;
		if (temp != null) {
			bf.waking = false;
		} else if (bf.waking == true) {
			boolean wokenByThis = false;
			for (int i = 0; i < guards.length; i++) {
				if (guards[i] == bf.selected) {
					wokenByThis = true;
					break;
				}
			}
			// this means the process was woken up while waiting on
			// the altMonitor so its lastSynchronised field won't
			// be set to the selected barrier
			if (wokenByThis) {
				temp = bf.selected;
				lastSynchronised = temp;
				bf.waking = false;
			}
		}

		bf.selected = null;
		removeBarrierFace(); //remove BarrierFace if this is last Guard
					//group
		
		parent = null;
		if (lastSynchronised != null) {
			lastSynchronised.setStatus(PREPARED);
		} else {
			resetBarriers();
		}
		releaseLock(key);
		System.out.println(this + " disable method has " + lastSynchronised + " as picked");

		if (lastSynchronised != null) {
			System.out.println("SYNCING ON GATEKEEPER");
			lastSynchronised.gateKeeper.sync();
			System.out.println("SYNCED ON GATEKEEPER");

			claimLock(key);
			resetBarriers();		
			releaseLock(key);
		}

		return (lastSynchronised != null);
	}
	//}}}
	//{{{ public AltableBarrier lastSynchronised()
	public AltableBarrier lastSynchronised() {
		AltableBarrier temp = lastSynchronised;
		lastSynchronised = null;
		return temp;
	}
	//}}}
	//}}}
	
	//{{{ private methods
	//{{{ private void claimLock() 
	public static void claimLock(Object claimant) {
	/*
//		if (lockOwner != claimant) {
			AltableBarrierBase.tokenGiver.in().read();
			lockOwner = claimant;
//		}
	*/
		globalLock.lock();
		lockOwner = claimant;
	}
	//}}}
	//{{{ private void releaseLock()
	public static void releaseLock(Object claimant){
	/*
//		if (lockOwner == claimant) {
			AltableBarrierBase.tokenReciever.out().write(null);
			claimant = null;
//		}
	*/
		lockOwner = null;
		while (globalLock.getHoldCount() > 0) {
			globalLock.unlock();
		}
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

		for (int i = 0; i < guards.length; i++)	{
			guards[i].face = bf;
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
	//{{{ private boolean isLastGroup()
	private boolean isLastGroup() {
		boolean isLast = (bf.bottomIndex >= bf.guardGroups.size());
		if (isLast) {
			System.out.println(this + " is last.");
		}
		return isLast;
	}
	//}}}
	//{{{ private void resetBarriers()
	private void resetBarriers() {
		for (int i = 0; i < guards.length; i++)	{
			//System.out.println("this is where the problem is");
			guards[i].setStatus(guards[i].defaultStatus);
		}
	}
	//}}}
	//}}}
	
}
//}}}
