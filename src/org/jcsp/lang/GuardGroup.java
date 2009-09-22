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
	public AltableBarrier[] barriers;
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
			barriers[i].setStatus(barriers[i].PREPARED);
		}
	}
	//}}}
	//{{{ private void endExplicitEnable()
	private void endExplicitEnable() {
		for (int i = 0; i < barriers.length; i++) {
			barriers[i].setStatus(barriers[i].UNPREPARED);
		}
	}
	//}}}
	//{{{  private void expandEqualGreaterList(Object id)
	private void expandEqualGreaterList(Object id) {
		/*
		 * FIXME currently stores a hash of process (represented by
		 * Alternative object) and a list of higher priority guards
		 * Needs let all of the guards so far what its current 
		 * status is (?face?)
		 */

		/*
		Vector v = (Vector) processBarrierList.get(id);
		if (v == null) {
			v = new Vector();
			processBarrierList.put(id, v);
		}

		for (int i = 0; i < barriers.length; i++) {
			v.add(barriers[i]);
		}
		*/

		// FIXME needs to be undone when barrier list is contracted

		BarrierFace face = (BarrierFace) processBarrierList.get(id);
		if (face == null) {
			face = new BarrierFace(new Vector(),new Vector(), null, id);
			processBarrierList.put(id, face);
		}

		Vector v = new Vector(); // list of barriers at this priority
		for (int i = 0; i < barriers.length; i++) {
			barriers[i].setFace(face);
//			face.higherBarriers.add(barriers[i]);
			v.add(barriers[i]);
		}
		face.higherBarriers.add(v);
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
		//{{{ record all the barriers you can sync on
		// update them so that they know that their process
		// is ready to sync on them
		readyBarriers = new Vector();
		for (int i = 0; i < barriers.length; i++) {
			readyBarriers.add(barriers[i]);
			barriers[i].setStatus(AltableBarrier.PREPARED); 
		}
		//}}}

		//{{{
		for (int i = 0; i < barriers.length; i++) {
			if (barriers[i].getStatus() == AltableBarrier.NOT_READY) {
				readyBarriers.remove(barriers[i]);
			} else if (barriers[i].getStatus() == AltableBarrier.NOT_SYNCING_NOW) {
				readyBarriers.remove(barriers[i]);
			}
		}
		//}}}
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
		System.out.println("attempting synchronisation on " + barrier);
		barrier.attemptSynchronisation();
		

		barrier = barrier.face.selectedBarrier;
		System.out.println("synchronisation attempted and got " + barrier);

		if (barrier != null && barrier.getStatus() == barrier.COMPLETE) {
			if (Arrays.asList(barriers).contains(barrier)) {
				// barrier was in this guard group
				return barrier;
			} else {
				System.out.println("synced on a different barrier");
				// set the lastReadyguard up in the guard group
				// that this barrier does belong to
				barrier.guardGroup.lastReadyGuard = barrier;
			}
		}

		//{{{
		/*
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
		*/
		//}}}
		return null;
	}
	//}}}

	//{{{ private Object anyReady()
	private Object anyReady() {
		return anyReady(true);
	}
	
	private Object anyReady(boolean checkBarriers) {
		if (lastReadyGuard != null) {
			return lastReadyGuard;
		}

		AltableBarrier ab = null;
		
		Guard g = checkForReadyGuards();
		if (g != null) {
			return g;
		}
		
		if (checkBarriers) {
			eliminateUnreadyBarriers();
			ab = selectBarrier();
			System.out.println("selected barrier " + ab);
			if (ab == null) {
				// there were no immediately ready guards
				return null;
			}
			System.out.println("waiting on barrier " + ab);
			ab = waitOnBarrier(ab);
			System.out.println("waited and got " + ab + " back");
		}

		return ab;
	}
	//}}}
	//}}}
	
	//{{{ methods inherited by Guard
	//{{{ boolean enable(Alternative alt)
	boolean enable(Alternative alt) {
		System.out.println("about to enable " + this);
//		try { synchronized (Class.forName("org.jcsp.lang.AltableBarrierBase")) {
		AltableBarrierBase.tokenGiver.in().read(); // get token
			System.out.println("Guard " + this + " enabled");
			// now has parent, assign
			parent = alt;
	
			// expand list of barriers of equal or greater priority barriers
			expandEqualGreaterList(parent); // unique ID is the parent Alt 

			// check if any Guards are ready
			System.out.println("calling anyReady()");
			Object o = anyReady();
			System.out.println("got " + o + " from anyReady()");

		if (o != null) {
			lastReadyGuard = o;
		}
		AltableBarrierBase.tokenReciever.out().write(null);

		return (o != null);

//		}} catch (Exception e) {
//			e.printStackTrace();
//			System.exit(0);
//		}
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

//		try { synchronized (Class.forName("org.jcsp.lang.AltableBarrierBase")) {
		AltableBarrierBase.tokenGiver.in().read();		
			// check if there are any ready guards
			Object o = anyReady();

			// clear the process' list of equal or greater ranked barriers
			clearEqualGreaterList(parent); //unique ID is the parent Alt

			// parent no longer known
			parent = null;

		AltableBarrierBase.tokenReciever.out().write(null);
			
		// return block
		if (o == null) {
			return false;
		} else {
			lastReadyGuard = o;
			return true;
		}
			
//		}} catch (Exception e) {
//			e.printStackTrace();
//			System.exit(0);
//		}
//		return false;
	}
	//}}}
	//}}}
}
//}}}
