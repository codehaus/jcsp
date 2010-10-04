//{{{package and import statements
package org.jcsp.lang;

import java.util.*;
//}}}
//{{{ public class AltableBarrierBase
public class AltableBarrierBase implements ABConstants {

	//{{{ constants
	/*
	public static final int NOT_READY = 0;
	public static final int NOT_SYNCING_NOW = 1;
	public static final int PROBABLY_READY = 2;
	public static final int SELECTED = 3;
	public static final int READY = 4;
	*/
	/*
	// these are in ABConstants now
	public static final int PREPARED = 0;
	public static final int UNPREPARED = 1;
	public static final int PICKED = 6;

	public static final int NOT_READY = 2;
	public static final int NOT_SYNCING_NOW = 3;
	public static final int PROBABLY_READY = 4;
	public static final int SELECTED = 5;
	public static final int COMPLETE = 7;
	*/

	public static final long BASE_DELAY = 500; // base time for timer to
	// wait for a synchronisation to happen (ms)

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
//							System.out.println("claimed by " + GuardGroup.lockOwner);
							in.read();
//							System.out.println("released to" + GuardGroup.lockOwner);
						}
					}
				}
		);
		pm.start();
	}
	//}}}

	//{{{ fields
	private Vector committedBarriers;
	private Vector altableBarriers;

	private Vector currentlyCommited;
	private Vector currentlyAlting;

	private int lastStatus = 0;

	public AltableBarrierTimeout timer;
	public String name;

	// the gateKeeper barrier is shared between all enrolled AltableBarriers
	// it is sync'ed on when the 'disable' method is called on a GuardGroup
	// which contains a recently selected AltableBarrier.  This ensures that
	// no process can attempt to re-synchronise on that AltableBarrier until
	// all enrolled processes have been woken up (4/6/2010).
	public Barrier gateKeeper = null;
	//}}}

	//{{{ constructors
	public AltableBarrierBase() {
		this("");
	}
	public AltableBarrierBase(String name) {
		committedBarriers = new Vector();
		altableBarriers = new Vector();

		currentlyCommited = new Vector();
		currentlyAlting = new Vector();	

		lastStatus = getStatus();

		timer = null;
		this.name = name;

		gateKeeper = new Barrier(0);
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

		gateKeeper.enroll();
		child.gateKeeper = gateKeeper;
	}
	//}}}
	//{{{ public void resign(AltableBarrier child) 
	public void resign(AltableBarrier child) {
		int index = altableBarriers.indexOf(child);

		if (index != -1) {
			altableBarriers.remove(child);
		}

		gateKeeper.resign();
		child.gateKeeper = null;
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
		//{{{ old code 
		/*
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
							switchOver(face.selected, bar);
						}
					}
				}
			}
		}
		*/
		//}}}
		for (int i = 0; i < altableBarriers.size(); i++) {
			boolean stolen = false; // make this true if and when the process is stolen
			AltableBarrier ab = (AltableBarrier) altableBarriers.get(i);
			BarrierFace face = (BarrierFace) ab.face;
			AltableBarrier current = null;
			if (face != null) {
				current = face.selected;
			}

			if (face != null && face.lock != null && !face.waking) {
				// if the barrier we are switching to occurs before or at the
				// same time as the previously selected barrier then switch
				// topIndex at this point is the same as the index of the
				// currently selected events
				Vector ggs = face.guardGroups;
				for (int j = 0; j <= face.topIndex; j++) {
					GuardGroup gg = (GuardGroup) ggs.get(j);
					for (int k = 0; k < gg.guards.length; k++) {
						AltableBarrier bar = gg.guards[k];
						if (bar == ab) {
							// the new barrier is equal or higher priority to the
							// the old one
							ab.select();
							stolen = true;
							break;
						}
					}
					if (stolen) {
						break;
					}
				}
			}
		}
	}
	//}}}
	//{{{ public void reset(AltableBarrier invoker)
	public void reset (AltableBarrier invoker) {
		//{{{ cancel timeout
		if (timer != null) {
			timer.kill();
			timer = null;
		}
		//}}}

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
			if (face != null && ab != invoker && ab == face.selected) {
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
	//{{{ public void synchronise(AltableBarrier caller)
	public void synchronise(AltableBarrier caller) {
		// let everyone know that the synchronisation happened
		/*
		 * At the moment it seems that the waking process can assume 
		 * that it has syncrhonised because its 'current' barrier hasn't
		 * been set to zero.
		 */
		// make sure timer is killed
		// wake everyone up
		wake(caller, true);
	}
	//}}}
	//{{{ public void abort(AltableBarrier caller)
	public void abort (AltableBarrier caller) {
		//{{{ make sure everyone knows the sync attempt has failed
		// this means for all waiting barriers which consider this
		// barrier 'selected' change their state to PREPARED and
		// set their selected barrier to null
		for (int i = 0; i < altableBarriers.size(); i++) {
			AltableBarrier ab = (AltableBarrier) altableBarriers.get(i);
			BarrierFace face = ab.face;
			// if the process is currently waiting on this barrier
			if (face != null && face.selected != null && face.selected.parent == this) {
			if (face.lock == null && ab != caller) {
				reportAB(ab);
				throw (new RuntimeException("WHY IS THIS HAPPENNING???"));
			}
			ab.setStatus(PREPARED); // stop being PICKED return to
						// being prepared
			face.selected = null;
			}
		}
		//}}}
		//{{{ wake everyone (*NOT* waiting on altmonitor) up
		wake(caller, false);
		//}}}
	}
	//}}}
	//{{{ public void timeout()
	public void timeout() {
	//{{{ old timeout behaviour
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
			if (face == null || face.selected != ab) {
				ab.setStatus(ab.UNPREPARED);
			}
		}
		//}}}
	//}}}
		// assume that the timer itself knows whether or not to timeout
		for (int i = 0; i < altableBarriers.size(); i++) {
			AltableBarrier ab = (AltableBarrier) altableBarriers.get(i);
			BarrierFace face = ab.face;
			//{{{ if not currently evaluating ALT, set process to UNPREPARED
			if (face == null) {
				ab.setStatus(UNPREPARED, true);
			}
			//}}}
			//{{{ same if evaluating ALT, but not reached ALT monitor
			else if (!(face.lock instanceof Alternative)) {
				ab.setStatus(UNPREPARED, true);
			}
			//}}}
			//{{{ same if reached ALT monitor but ALT does not contain bar
			else {
			boolean containsMe = false;
			for (int j = 0; j < face.guardGroups.size(); j++){
			boolean done = false;
			GuardGroup gg = (GuardGroup) face.guardGroups.get(j);
			for (int k = 0; k < gg.guards.length; k++) {
				if (gg.guards[k].parent == this) {
					done = true;
					containsMe = true;
					break;
				}
			}
			if (done) {
				break;
			}
			}
			if (!containsMe) {
				ab.setStatus(UNPREPARED, true);
			}
			}
			//}}}
		}	
		//{{{ trigger abort if not already triggered.
		if (getStatus() != NOT_SYNCING_NOW) {
			System.out.println("timeout failed to terminate sync");
			System.out.println("aborting anyway");
			abort(null);
		}
		//}}}
		
	}
	//}}}
	//{{{ public void startTimer()
	/*
	 * This method checks if there is a timer, if there isn't
	 * a new one is started.  It is intended for use with a
	 * call to attemptSynchronisation(), thus only the first
	 * process to wait on the barrier will start the timeout
	 */
	public void startTimer() {
		if (timer == null) {
			timer = new AltableBarrierTimeout(
			this, BASE_DELAY * altableBarriers.size());

			(new ProcessManager(timer)).start();	
		}
	}
	//}}}
	//{{{ public boolean isSelected()
	public boolean isSelected() {
		for (int i = 0; i < altableBarriers.size(); i++) {
			AltableBarrier ab = (AltableBarrier) altableBarriers.get(i);
			// has this process selected this barrier?
			boolean checkProcess = true;
			checkProcess = checkProcess && (ab.face !=null);
			checkProcess = checkProcess && (ab.face.selected != null);
			if (checkProcess && (ab.face.selected.parent == this)) {
				return true;
			}
		}
		return false;
	}
	//}}}
	//}}}
	//{{{ private methods
	//{{{ private void wake(AltableBarrier caller, boolean wakeAll)
	private void wake(AltableBarrier caller, boolean wakeAll) {
		// any situation involving waking processes, whether for an
		// abort or successful synchronisation should cancel any
		// timeouts which may still be running
		//{{{ cancel timeout
		if (timer != null) {
			System.out.println("killing timer " + timer);
			timer.kill();
			timer = null;
		}
		//}}}

		for (int i = 0; i < altableBarriers.size(); i++) {
			AltableBarrier ab = (AltableBarrier) altableBarriers.get(i);

			BarrierFace face = ab.face;
			boolean wakeAbort = (face != null && face.lock != null && ab != caller && face.selected == null);
			boolean syncing = (face != null && face.lock != null && face.selected != null && face.selected.parent == caller.parent);
			boolean wakeSync = syncing && ab != caller;
			System.out.print("wakeAbort " + wakeAbort);
			System.out.println(" wakeSync " + wakeSync);
//			if (face != null && face.lock != null && face.selected != null && face.selected.parent == caller.parent && ab != caller) {
			if (wakeAbort || wakeSync) {
				if (wakeAll||face.lock instanceof Alternative){
				System.out.println("trying to wake " + face.lock);
				if (face.spuriousCheck) {
				synchronized (face.lock) {
					face.waking = true;
					face.spuriousCheck = false;
					if (face.lock instanceof Alternative) {
						face.lock.notify();
					} else {
						// waiting on altMonitor
						Alternative alt = (Alternative) face.key;
						alt.schedule();
					}
				}
				} else if (face.selected != null) {
					reportAB(ab);
					throw (new RuntimeException("erk " + face.selected));
				}
				System.out.println("woke " + face.lock);
				}
			} else if (wakeAll && !syncing && ab != caller) {
				System.out.print("face " + face + " barrier " + face.selected + " waking is " + face.waking + " parent " + face.selected.parent + " caller " + caller + " lock " + face.lock + " key " + face.key);
				throw (new RuntimeException("hmmmmmmm"));
			}
		}
	}
	//}}}
	//{{{ public void switchOver (AltableBarrier from, to)
	/* switchOver is deprecated
	 */
	/*
	public void switchOver (AltableBarrier from, AltableBarrier to) {
		from.setStatus(PREPARED); // i.e. no longer picked
		from.face.selected = to;
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
		
		//int index = face.higherBarriers.indexOf(to);
		//for (int i = index; face.higherBarriers.size() > index;) {
		//	face.higherBarriers.remove(i);
		//}
		
	}
	*/
	//}}}
	public void howManyMissing() {
		int count = 0;
		for (int i = 0; i < altableBarriers.size(); i++) {
			AltableBarrier ab = (AltableBarrier)altableBarriers.get(i);
			if (ab.status != PICKED) {
				reportAB(ab);
				count++;
			}
		}
		System.out.println("count = " + count);
	}
	//}}}
	//{{{ public void reportAB(AltableBarrier ab)
	public void reportAB(AltableBarrier ab) {
				BarrierFace f = ab.face;
				AltableBarrier current = null, sync = null;
				Object lock = null;//, key = null;
				boolean waking = false;
				int trace = 1000;
				if (f != null) {
					lock = f.lock;
					current = f.selected;
					waking = f.waking;
					trace = f.trace;
//					sync = f.lastSynchronised;
//					key = f.key;
				}
				System.out.println("missing " + ab + " face is "+ f +" currently on " + current + " lastSynchronised " + sync +" lock is " + lock + " waking? " + waking + " status = " + ab.status + " trace = " + trace);// + " key" + key);

	}
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
