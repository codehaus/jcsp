//{{{ package and import statements
package org.jcsp.lang;

import java.util.*;
//}}}
//{{{ public class GuardGroup extends Guard
public class GuardGroup extends Guard {

	//{{{ notes about waiting
	/*
 	 *  It doesn't matter if more than one guard becomes ready at a time
 	 *  during a waiting period, that message can be forwarded on to the
 	 *  parent ALT, it is up to the alt to decide which guard is picked.
 	 */ 
	//}}}

	//{{{ private static fields
	//hashtable because hashtables are synchronised
	private static Hashtable processBarrierList;
	//}}}

	//{{{ private fields
	private Guard[] guards;
	private AltableBarrier[] barriers;
	private Alternative parent;
	private Object lastReadyGuard;

	private Vector readyBarriers;
	//}}}

	//{{{ public fields
	//}}}

	//{{{ static block
	static {
		processBarrierList = new Hashtable();
	}
	//}}} 

	//{{{ public GuardGroup(Guard[] guards, AltableBarrier[] barriers)
	public GuardGroup (Guard[] guards, AltableBarrier[] barriers) {

		this.guards = guards;
		this.barriers = barriers;
	}
	//}}}


	//{{{ private methods
	//{{{ private void explicitlyEnableBarriers()
	//}}}
	//{{{ private void endExplicitEnable()
	//}}}
	//{{{ 1 private void expandEqualGreaterList(Object id)
	private void expandEqualGreaterList(Object id) {
		Vector v = (Vector) processBarrierList.get(id);
		if (v == null) {
			v = new Vector();
			processBarrierList.put(id, v);
		}

		for (int i = 0; i < barriers.length; i++) {
			v.add(barriers[i]);
		}
	}
	//}}}
	//{{{ 2 private void clearEqualGreaterList(Object id)
	private void clearEqualGreaterList(Object id) {
		processBarrierList.remove(id);
	}
	//}}}
	//{{{ 3 private Guard checkForReadyGuards()
	private Guard checkForReadyGuards() {
		Guard[] nonBlockingGuards = new Guard[guards.length+1];

		for (int i = 0; i < guards.length; i++) {
			nonBlockingGuards[i] = guards[i];
		}
		nonBlockingGuards[guards.length] = new Skip();

		Alternative nonBlockingAlt = new Alternative(nonBlockingGuards);
		int index = nonBlockingAlt.select();
	
		if (index == guards.length) { // ie was skip guard
			return null; // there were no ready mundane guards
		}

		return nonBlockingGuards[index]; // this Guard was ready
	}
	//}}}
	//{{{ 4 private void eliminateUnreadyBarriers()
	private void eliminateUnreadyBarriers() {
		readyBarriers = new Vector();
		for (int i = 0; i < barriers.length; i++) {
			readyBarriers.add(barriers[i]);
		}

		for (int i = 0; i < barriers.length; i++) {
			if (barriers[i].status == AltableBarrier.NOT_READY) {
				readyBarriers.remove(barriers[i]);
			} else if (barriers[i].status == AltableBarrier.NOT_SYNCING_NOW) {
				readyBarriers.remove(barriers[i]);
			}
		}
	}
	//}}}
	//{{{ 5 private AltableBarrier selectBarrier()
	private AltableBarrier selectBarrier() {
		if (readyBarriers.size() == 0) {
			return null; // no barriers were ready
		}

		int selectedIndex = -1;
		for (int i = 0; i < readyBarriers.size(); i++) {
			AltableBarrier ab = (AltableBarrier) readyBarriers.get(i);
			if (ab.status == AltableBarrier.SELECTED) {
				selectedIndex = i;
				break;
			}
		}

		if (selectedIndex == -1) {
			return (AltableBarrier) readyBarriers.get(0);
		}
		return (AltableBarrier) readyBarriers.get(selectedIndex);

	}	
	//}}}
	//{{{ 6 private AltableBarrier waitOnBarrier(AltableBarrier initial) 
	private AltableBarrier waitOnBarrier(AltableBarrier initial) {
		AltableBarrier barrier = initial;

		//FIXME start timeout if you're first to select barrier

		boolean running = true;
		barrier.attemptSynchronisation();

		while (running) {
			Object o = barrier.in.read();
			if (o instanceof AltableBarrier) {
				barrier = (AltableBarrier) o;
				// assume altablebarrier base knows attempt stopped
				barrier.attemptSynchronisation();
			} else if (o.equals(barrier.SUCCESS)) {
				running = false; // successful syncronisation
			} else if (o.equals(barrier.FAILURE)) {
				barrier = null;
				running = false; // sync attempted failed
			} else if (o.equals(barrier.TIMEOUT)) {
				barrier = null;
				running = false;
				// FIXME set non present guards to not_syncing_now
			}
		}

		return barrier;
	}
	//}}}

	//{{{ private Object anyReady()
	private Object anyReady() {
		AltableBarrier ab = null;
		
		try { synchronized (Class.forName("AltableBarrierBase")) {
			Guard g = checkForReadyGuards();
			if (g != null) {
				return g;
			}

			eliminateUnreadyBarriers();
			ab = selectBarrier();
			if (ab == null) {
				// there were no immediately ready guards
				return null;
			}
			ab = waitOnBarrier(ab);
		} }catch (Exception e) {
			System.out.println("AltableBarrierBase not found, erk");
			e.printStackTrace();
			System.exit(0);
		}

		return ab;
	}
	//}}}
	//}}}
	
	//{{{ methods inherited by Guard
	//{{{ boolean enable(Alternative alt)
	boolean enable(Alternative alt) {
		// now has parent, assign
		parent = alt;

		// expand list of barriers of equal or greater priority barriers
		expandEqualGreaterList(parent); // unique ID is the parent Alt 

		// check if any Guards are ready
		Object o = anyReady();
		if (o == null) {
			return false;
		} else {
			lastReadyGuard = o;
			return true;
		}

	}
	//}}}
	//{{{ boolean disable()
	boolean disable() {
		// check if there are any ready guards
		Object o = anyReady();

		// clear the process' list of equal or greater ranked barriers
		clearEqualGreaterList(parent); //unique ID is the parent Alt

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
