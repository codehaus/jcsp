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
	private void explicitlyEnableBarriers() {
		for (int i = 0; i < barriers.length; i++) {
			barriers[i].setStatus(barriers[i].EXPLICIT_READY);
		}
	}
	//}}}
	//{{{ private void endExplicitEnable()
	private void endExplicitEnable() {
		for (int i = 0; i < barriers.length; i++) {
			barriers[i].setStatus(barriers[i].IMPLICIT_READY);
		}
	}
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

		return ab;
	}
	//}}}
	//}}}
	
	//{{{ methods inherited by Guard
	//{{{ boolean enable(Alternative alt)
	boolean enable(Alternative alt) {
		try { synchronized (Class.forName("AltableBarrierBase")) {

			// now has parent, assign
			parent = alt;
	
			// expand list of barriers of equal or greater priority barriers
			expandEqualGreaterList(parent); // unique ID is the parent Alt 

			// check if any Guards are ready
			Object o = anyReady();
			if (o == null) {
				/*
				 * FIXME: There was no immediately ready guard
				 * in this group.  Need to assume that 
				 * Alternative will enter a waiting state, must
				 * enable mundane and PICOMS guards so that
				 * they can wake up a sleeping Alternative.
				 */
				return false;
			} else {
				lastReadyGuard = o;
				return true;
			}
		}} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
	}
	//}}}
	//{{{ boolean disable()

	/*
	 * Note: FIXME added 11/1/2009.  Disable sequence is run in reverse
	 * priority order by the surrounding Alternative class.  If a high
	 * priority guard becomes 'probably ready' while waiting and 
	 * reactivates the Alternative, only for the synchronisation to fail
	 * later, the lower priority guards will have already been disabled.
	 * Possible solutions are for high priority guard groups to keep track
	 * of lower priotity ones (in order to reactivate them later if the
	 * synchronisation attempt fails) OR to allow (somehow??) the
	 * synchronisation attempt to go ahead (and possibly) fail WITHOUT
	 * waking up the alternative UNTIL A FULLY SUCCESSFUL SYNCHRONISATION
	 * HAPPENS, neater on paper but PICOMS relies on mundane guards not
	 * interrupting synchronisation attempts ... Have to think about that
	 * one.  Another possibility is to replace the behaviour of all
	 * waiting barriers with the AltingBarrier (as opposed to PICOMS
	 * barrier) behaviour, i.e. when the Alternative enters a waiting
	 * state it treats all PICOMS barriers as if they have no priority.
	 * hmmmm ... is this a compromise in priority too far or unavoidable?
	 */
	boolean disable() {
		try { synchronized (Class.forName("AltableBarrierBase")) {
		
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
		}} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
	}
	//}}}
	//}}}
}
//}}}
