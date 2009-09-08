//{{{ package and import statements
package org.jcsp.lang;
//}}}
//{{{ public class AltableBarrier
public class AltableBarrier {

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
	//}}}
	//{{{ public fields
	public int status;
	public  AltableBarrierBase parent;
	public GuardGroup guardGroup;

	public AltingChannelInput in; // the end the alting process uses
	public ChannelOutput out; // the end the barrier uses.

	public BarrierFace face;
	//}}}


	//{{{ constructors
	public AltableBarrier (AltableBarrierBase parent) {
		this.parent = parent;
		status = PREPARED;

		parent.enroll(this);

		One2OneChannel chan = StandardChannelFactory.getDefaultInstance().createOne2One();

		in = chan.in();
		out = chan.out();
	}
	//}}}


	//{{{ public methods
	//{{{ public void attemptSynchronisation()
	public void attemptSynchronisation() {
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

		//{{{ update status, inc. self, face, etc
		setStatus(PICKED);
		face.selectedBarrier = this;
		//}}}
		//{{{ attempt to steal processes from other barriers.
		parent.steal();
		//}}}
		//{{{ check if complete (then reset) otherwise wait
		// Note check the barrier indictated by the face
		// not the barrier you started syncing on
		// this is because the barrier you are syncing on
		// could have changed
		int parentStatus = face.selectedBarrier.getStatus();
		if (parentStatus == COMPLETE) {
			System.out.println("horay we synced");
			reset();
		} else {
		//}}}
			//{{{ wait to be woken
			// the barrier wasn't ready at the moment, wait.
			// wait on an object which won't change throughout the
			// synchronisation attempt, which other processes can see
			// and which isn't the Alternative's altmonitor
			AltableBarrierBase.tokenReciever.out().write(null);
			try {
			Object key = face.key;
			synchronized (key) {
				key.wait();
		
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(0);
			}
			AltableBarrierBase.tokenGiver.in().read();

			//{{{ check if sync attempt was aborted
			parentStatus = face.selectedBarrier.getStatus();
			if (parentStatus == COMPLETE) {
				System.out.println("horay we synced");
			} else if (parentStatus == NOT_SYNCING_NOW) {
				System.out.println("aborting");
				abort();
			}		
			//}}}
		}
		//}}}
	}
	//}}}
	//{{{ public void setState(int state)
	/*
	 * get back status of the AltableBarrierBase
	 */
	public int setStatus(int status) {
		this.status = status;
//		return parent.checkStatus(this);
		if (getStatus() == NOT_SYNCING_NOW) {
			System.out.println("aborting");
			abort();
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
	private void abort() {
		//FIXME this abort sequence will need to deal with the
		// possibility of a timeout and will later need to deal
		// with a process 'falling back' through barriers which
		// have already been enabled but which were lower priority
		// than the guard just aborted.

		// {{{ wake up all waiting barriers
		// possibly a call to reset would work???
		// }}}
	}
	//}}}
}
//}}}
