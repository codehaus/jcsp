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
	public static final int COMPLETE = 7;

	public static final One2AnyChannel tokenGiver;
	public static final Any2OneChannel tokenReciever;
	//}}}
	
	//{{{ static block
	static {
		tokenGiver = Channel.one2any();
		tokenReciever = Channel.any2one();

		final ChannelOutput out = tokenGiver.out();
		final ChannelInput in = tokenReciever.in();

		ProcessManager pm = new ProcessManager(
				new CSProcess() {
					public void run() {
						while (true) {
							out.write(null);
							in.read();
						}
					}
				}
		);
		pm.start();
	}
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

		boolean complete = true, anyPrepared = true;
		for (int i = 0; i < altableBarriers.size(); i++) {
			AltableBarrier ab = (AltableBarrier)altableBarriers.get(i);

			if (ab.status == UNPREPARED) {
				return NOT_SYNCING_NOW;
			} else if (ab.status == PREPARED) {
				anyPrepared = anyPrepared & true;
				complete = false;
			} else if (ab.status == PICKED) {
				anyPrepared = anyPrepared & true;
				complete = complete & true;
			} else {
				anyPrepared = false;
				complete = false;
			}
		}
		if (complete) {
			return COMPLETE;
		} else if (anyPrepared) {
			return PROBABLY_READY;
		} else {
			return NOT_SYNCING_NOW;
		}

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
	//{{{ public void steal()
	// for all enrolled processes look at their AltableBarriers face
	// any which are currently syncing on another process but have
	// this barrier in their list of higher barriers should be switched
	// to this one.
	public void steal() {
		for (int i = 0; i < altableBarriers.size(); i++) {
			AltableBarrier bar = (AltableBarrier) altableBarriers.get(i);

			BarrierFace face = bar.face;
			// only switch if barrier has a face, i.e. is in an alt
			if (face != null) {
				Vector higher = face.higherBarriers;
				for (int j = 0; j < higher.size(); j++) {
					Vector v = (Vector) higher.get(j);
					for (int k = 0; k < v.size(); k++) {
						AltableBarrier bar2 = (AltableBarrier) v.get(k);
						// switch if this barrier is in the process's list of higher barriers
						if (bar.equals(bar2)) {
							switchOver(face.selectedBarrier, bar);
						}
					}
				}
			}
		}
	}
	//}}}
	//{{{ public void reset(AltableBarrier invoker)
	public void reset (AltableBarrier invoker) {
		//{{{ wake up committedBarriers
		for (int i = 0; i < committedBarriers.size(); i++) {
			AltableBarrier cb = (AltableBarrier) committedBarriers.get(i);
			BarrierFace face = cb.face;
			Object key = face.key;
			try {
			synchronized (key) {
				key.notify();
			}
			} catch(Exception e) {
				e.printStackTrace();
				System.exit(0);
			}
		}
		//}}}
		//{{{ wake up altableBarriers
		for (int i = 0; i < altableBarriers.size(); i++) {
			AltableBarrier ab = (AltableBarrier) altableBarriers.get(i);
			BarrierFace face = ab.face;
			/*
 			 * check that the process has entered an ALT, i.e. the
 			 * face is not null.  Check you aren't trying to wake
 			 * the process that invoked the reset (its not waiting
 			 * in the first place).  Check you aren't waking up
 			 * enrolled processes which are legitimately syncing on
 			 * another barrier, i.e. that the process is associated
 			 * with a face which says that it is syncing on the
 			 * barrier which you are currently waking up.
 			 */
			if (face != null && ab != invoker && ab == face.selectedBarrier) {
				// may not have entered an ALT yet
				Object key = face.key;
				try {
				synchronized (key) {
					key.notify();
				}
				} catch(Exception e) {
					e.printStackTrace();
					System.exit(0);
				}
			} else {
				System.out.println("eliminated invoker" + face + " " + ab);
			}
		}
		//}}}


	}
	//}}}
	//{{{ public void timeout()
	public void timeout() {
		//{{{ check timeout not unneccesary
		int status = getStatus();
		if (status == NOT_READY || status == NOT_SYNCING_NOW) {
			// already aborted or otherwise no-one waiting
			// warning, FIXME may cause timeouts to occur
			// if a successful sync happens and another sync
			// attempt starts afterwards but before the timeout
			// must safeguard against this somehow
			return;
		}
		//}}}
		//{{{ mark as UNPREPARED any processes not currently waiting
		// this will automatically trigger an abort for the first
		// absent process
		for (int i = 0; i < altableBarriers.size(); i++) {
			AltableBarrier ab = (AltableBarrier) altableBarriers.get(i);
			BarrierFace face = ab.face;
			if (face == null || face.selectedBarrier != ab) {
				ab.setStatus(ab.UNPREPARED);
			}
		}
		//}}}
	}
	//}}}
	//}}}
	//{{{ private methods
	//{{{ public void switchOver (AltableBarrier from, to)
	public void switchOver (AltableBarrier from, AltableBarrier to) {
		from.setStatus(PREPARED); // i.e. no longer picked
		from.face.selectedBarrier = to;
		to.setStatus(PICKED);
		// remove barriers of lower prioirty than the new
		// barrier
		// FIXME this currently will remove all barriers lower
		// down the list than the new barrier, this is not
		// strictly correct because some of the removed barriers
		// may be of equal priority to the new barrier.
		// need to store the relative priority levels of the barriers
		// in the list so that barriers of equal priority aren't removed

		BarrierFace face = from.face;  // doesn't matter which barrier
						// its from, is same object
		Vector higher = face.higherBarriers;
		int toPriLevel = -1;
		for (int i = 0; i < higher.size(); i++) {
			Vector v = (Vector) higher.get(i);
			int index = v.indexOf(to);
			if (index != -1) {
				// we found the new barrier
				toPriLevel = i;
				break;
			}
		}
		for (int i = toPriLevel+1; higher.size() > i;) {
			face.lowerBarriers.add(higher.remove(i));
		}
		/*
		int index = face.higherBarriers.indexOf(to);
		for (int i = index; face.higherBarriers.size() > index;) {
			face.higherBarriers.remove(i);
		}
		*/
	}
	//}}}
	//}}}


	//{{{ SIGH :(
	// Need to hold a global lock on all AltableBarrierResources to ensure
	// that no other processes muck around with what you are doing.  But
	// if you are waiting for all of the processes syncing on your barrier
	// to turn up, you cannot wait on the global lock because processes
	// waiting on different barriers may also be waiting on the same lock.
	//
	// This means that processes can be woken up by the wrong barrier 
	// completing.  The proposed solution (20/07/2009) is that, instead of 
	// processes taking out a global lock, they instead wait on a channel to
	// the AltableBarrierBase and recieve a token which allows them the
	// same privelges as a global lock.  They then return the lock just
	// before they wait for the barrier to complete.
	//}}}
}
//}}}
