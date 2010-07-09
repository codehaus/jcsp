//{{{ package and import statements
package org.jcsp.lang;
//}}}
//{{{ public class AltableBarrier
public class AltableBarrier implements ABConstants {

	//{{{ constants
	/*
	public static final int NOT_READY = 0;
	public static final int NOT_SYNCING_NOW = 1;
	public static final int PROBABLY_READY = 2;
	public static final int SELECTED = 3;

	public static final int IMPLICIT_READY = 4;
	public static final int EXPLICIT_READY = 5; // trumps all
	public static final int IMPLICIT_NOT_READY = 6;
	public static final int EXPLICIT_NOT_READY = 7; //trumps all but 5
	*/
	/*
	 // this is now in ABConstants
	public static final int PREPARED = 0;
	public static final int UNPREPARED = 1;
	public static final int PICKED = 6;

	public static final int NOT_READY = 2;
	public static final int NOT_SYNCING_NOW = 3;
	public static final int PROBABLY_READY = 4;
	public static final int SELECTED = 5;
	public static final int COMPLETE = 7;

	public static final Object SUCCESS = new Object();
	public static final Object FAILURE = new Object();
	public static final Object TIMEOUT = new Object();
	*/
	//}}}
	//{{{ public fields
	public int status;
	public  AltableBarrierBase parent;
	public GuardGroup guardGroup;
	public int defaultStatus;

	public AltingChannelInput in; // the end the alting process uses
	public ChannelOutput out; // the end the barrier uses.

	public BarrierFace face;
	public Barrier gateKeeper;
	//}}}

	//{{{ constructors
	public AltableBarrier (AltableBarrierBase parent) {
		this (parent, PREPARED);
	}
	public AltableBarrier (AltableBarrierBase parent, int defaultStatus) {
		this.parent = parent;
		status = defaultStatus;
		this.defaultStatus = defaultStatus;

		parent.enroll(this);

		One2OneChannel chan = StandardChannelFactory.getDefaultInstance().createOne2One();

		in = chan.in();
		out = chan.out();

	}
	//}}}


	//{{{ public methods
	//{{{ public void attemptSynchronisation()
	public AltableBarrier attemptSynchronisation() {
		//{{{ comments
		// FIXME Ignore this, read the one after
		/*
 		 * wait() on the AltableBarrier class if not everyone
 		 * has turned up yet, otherwise notifyAll waiting
 		 * parties of the change.  The altmonitor is not released
 		 * but it doesn't need to be because
 		 * (a) we're not listening to anything except AltableBarrier
 		 * events for the time being.
 		 * (b) the write methods of channels only claim the lock on
 		 * their rwMonitors (even if they end up notifying the 
 		 * the altMonitor).  Thus they aren't even blocked
 		 *
 		 */
		/*
		 * tell the barrier that you have picked it, check if this
		 * completes the barrier.  If it does hooray otherwise wait
		 * to be woken up with the news that either the sync did
		 * complete, the barrier you were on was switched or the 
		 * timeout elapsed.
		 */
		//}}}

		//{{{ update status, inc. self, face, etc
		select();
		//}}}
		//{{{ attempt to steal processes from other barriers.
		parent.steal();
		//}}}
		//{{{ if first process, then start a timeout for the
		//synchronisation
		parent.startTimer();
		//}}}
		//{{{ check if complete (then reset) otherwise wait
		// Note check the barrier indictated by the face
		// not the barrier you started syncing on
		// this is because the barrier you are syncing on
		// could have changed
		int parentStatus = face.selected.getStatus();
		if (parentStatus == COMPLETE) {
			System.out.println("horay we synced on " + face.selected);
			synchronise();
			return face.selected;
		} else {
		//}}}
			//{{{ wait to be woken
			// the barrier wasn't ready at the moment, wait.
			// wait on an object which won't change throughout the
			// synchronisation attempt, which other processes can see
			// and which isn't the Alternative's altmonitor
			try {
			face.lock = face.key;
			synchronized (face.lock) {
//				AltableBarrierBase.tokenReciever.out().write(null);
				GuardGroup.releaseLock(face.key);
				face.lock.wait();
				GuardGroup.claimLock(face.key);
			}	
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(0);
			}
//			AltableBarrierBase.tokenGiver.in().read();

			//{{{ check if sync attempt was aborted
			if (this.face.selected != null) {
				System.out.println("horay we synced on " + face.selected);
			} else {
				System.out.println("aborting from wait");
			}	
			return face.selected;	
			//}}}
		}
		//}}}
	}
	//}}}
	//{{{ public void setStatus(int state)
	/*
	 * get back status of the AltableBarrierBase
	 */
	public void setStatus(int state) {
		// normal calls to this method aren't done by a timeout
		setStatus(state, false);
	}
	// this method has extra parameter to say whether the caller of the
	// method is a timeout.  This has a bearing on whether this process
	// should be woken up in case of an abort.
	public int setStatus(int status, boolean isTimeout) {
		int oldStatus = getStatus();
		this.status = status;

//		return parent.checkStatus(this);
		if (getStatus() == NOT_SYNCING_NOW && oldStatus != NOT_SYNCING_NOW) {
			System.out.println("aborting " + this);
			abort(isTimeout);
		} 
		return status;
	}
	//}}}
	//{{{ public void getStatus()
	public int getStatus() {
		return parent.getStatus();
	}
	//}}}
	//{{{ public void setFace(BarrierFace face)
	public void setFace(BarrierFace face) {
		this.face = face;
	}
	//}}}
	//{{{ public boolean isSelected()
	public boolean isSelected() {
		return parent.isSelected();
	}
	//}}}
	//{{{ public void select()
	public void select() {
		if ((face != null) && (face.selected != null)) {
			// change status of old one from PICKED to prepared
			face.selected.setStatus(PREPARED);
		}

		setStatus(PICKED);
		face.selected = this;
		face.topIndex = findMyIndex();	
	}
	//}}}
	//}}}
	
	//{{{ private method reset() 
	//this method should be called by the process completing the
	//syncronisation and should wake all other processes up.
	//should call parent, that should wake sleeping processes
	//up, note that AltableBarriers should have a note of the object
	//on which they are waiting, this should be the barrier's Alt monitor
	//if they didn't initially select any barriers or something else
	//(possibly the Alternative Object itself).  Note it shouldn't be
	//the AltableBarrierBase class itself (as many unrelated processes
	//could all be waiting on it, don't want to wake them all up) nor should
	// it be the the Alternative's altmonitor because any currently enabled
	// 'mundane' guards which become ready at this point could wake the 
	// process up (the process is at the moment only interested in what
	// happens with its barrier sync at this point).
	private void reset() {
		parent.reset(this);
	}
	//}}}
	//{{{ private void abort()
	private void abort() {abort(false);}
	// if the abort is triggered by a timeout then the process which owns
	// this AltableBarrier needs to be woken up too.  This means that it
	// shouldn't be excluded from the abort process.  Thus, 'this' 
	// AltableBarrier is only passed on if the caller *wasn't* a timeout.
	private void abort(boolean isTimeout) {
		if (isTimeout) {
			parent.abort(null);
		} else {
			parent.abort(this);
		}
	}
	//}}}
	//{{{ private boolean fallThrough()
	/*
	 * This method is called whenever a method is about to be woken up due
	 * to an abort.  In the event that a process passes a high priority 
	 * barrier, selects a lower priority one, is stolen when the high 
	 * priority barrier becomes ready and the high priority barrier aborts 
	 * or times out, this method exists to re-evaluate the already visited 
	 * barriers below it.  To put it another way, stealing causes the 
	 * process jump back up the priority structure, fallThrough exists in 
	 * case the process needs to go back down the priority structure to 
	 * reach the place it was stolen from.
	 * It returns whether or not there was a suitable barrier to go back to.
	 */
	private boolean fallThrough() {
		return false;
	}
	//}}}
	//{{{ public static int findMyIndex(AltableBarrier ab) 
	public int findMyIndex() {
		for (int i = 0; i < face.guardGroups.size(); i++) {
			GuardGroup gg = (GuardGroup)face.guardGroups.get(i);
			if (gg == guardGroup) {
				// if the guardGroup being examined is the
				// same as the one to which this AltableBarrier
				// belongs, then return that guardGroup's index
				return i;
			}
		}
		return -1;
	}
	//}}}
	//{{{ private void synchronise()
	public void synchronise() {
		parent.synchronise(this);
	}
	//}}}
	public String toString() {
		return super.toString() + " of " + parent.name;
	}
}
//}}}
