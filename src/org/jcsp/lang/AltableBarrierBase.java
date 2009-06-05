//{{{package and import statements
package org.jcsp.lang;

import java.util.*;
//}}}
//{{{ public class AltableBarrierBase
public class AltableBarrierBase {

	//{{{ constants
	/*
	public static final int NOT_READY = 0;
	public static final int NOT_SYNCING_NOW = 1;
	public static final int PROBABLY_READY = 2;
	public static final int SELECTED = 3;
	public static final int READY = 4;
	*/
	public static final int PREPARED = 0;
	public static final int UNPREPARED = 1;
	public static final int PICKED = 6;

	public static final int NOT_READY = 2;
	public static final int NOT_SYNCING_NOW = 3;
	public static final int PROBABLY_READY = 4;
	public static final int SELECTED = 5;
	//}}}

	//{{{ private fields
	private Vector committedBarriers;
	private Vector altableBarriers;

	private Vector currentlyCommited;
	private Vector currentlyAlting;

	private int lastStatus = 0;
	//}}}

	//{{{ constructors
	public AltableBarrierBase() {
		committedBarriers = new Vector();
		altableBarriers = new Vector();

		currentlyCommited = new Vector();
		currentlyAlting = new Vector();	

		lastStatus = getStatus();
	}
	//}}}

	//{{{ public methods
	public AltableBarrier createChild() {
		return (new AltableBarrier(this));
	}
	//{{{ public int getStatus()
	public int getStatus() {
		for (int i = 0; i < committedBarriers.size(); i++) {
			AltableBarrier ab = (AltableBarrier)committedBarriers.get(i);
			if (ab.status == UNPREPARED) {
				return NOT_READY;
			}
		}

		boolean ready = true, selected = false;
		for (int i = 0; i < altableBarriers.size(); i++) {
			AltableBarrier ab = (AltableBarrier)altableBarriers.get(i);

			if (ab.status == UNPREPARED) {
				return NOT_SYNCING_NOW;
			} else if (ab.status == PICKED) {
				return SELECTED;
			}
		}
		return PROBABLY_READY;
		// if any committed barriers are not ready then not ready

		// if any altable barriers are not syncing now then
		// status is not syncing now

		// if any altable barriers have selected this barrier then 
		// status is selected

		// if all of the altable barriers have selected this 
		// barrier then it is ready

		// otherwise status is probably ready
	}
	//}}}
	//{{{ public void enroll(AltableBarrier child)
	public void enroll(AltableBarrier child) {
		altableBarriers.add(child);
	}
	//}}}
	//{{{ public void resign(AltableBarrier child) 
	public void resign(AltableBarrier child) {
		int index = altableBarriers.indexOf(child);

		if (index != -1) {
			altableBarriers.remove(child);
		}
	}
	//}}}
	//{{{ public void setStatus(AltableBarrier child, int status) 
	public void setStatus(AltableBarrier child, int status) {
		//if becomes unready notify to abort
		//if becomes ready notify to be selectable
	}
	//}}}	

	//{{{ public void checkStatus(AltableBarrier caller)
	/*
	 * check that the status of the barrier as a whole hasn't changed,
	 * if it has notify interested parties, ignore the one whose current
	 * thread of control it is. it can't be waiting for anything.
	 */
	public int checkStatus(AltableBarrier caller) {
		/*
		int temp = getStatus();

		if (temp != lastStatus) {  // may need to notify people
			if (
			 (lastStatus == NOT_READY || lastStatus == NOT_SYNCING_NOW) &&
			 (temp != NOT_READY && temp != NOT_SYNCING_NOW) 
			) {
				// notify waiting processes that this barrier is ready
			}

			if ((lastStatus == PROBABLY_READY || lastStatus == SELECTED) &&
			 (temp == NOT_SYNCING_NOW)) {
				// abort synchronisation attempt
			}

			if (temp == READY) {
				// all processes turned up, notify them via channels
				// except the one which called this method
			}
		}

		lastStatus = temp;

		return lastStatus;
		*/
		return -1;
		
	}
	//}}}
	//}}}

}
//}}}
