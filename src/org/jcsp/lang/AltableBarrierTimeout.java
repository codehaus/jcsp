//{{{ package and import statements
package org.jcsp.lang;
//}}}

//{{{ public class AltableBarrierTimeout implements CSProcess
public class AltableBarrierTimeout implements CSProcess {

	//{{{ fields
	private long delay;
	private AltableBarrierBase parent;
	private boolean shouldTimeout;
	//}}}

	//{{{ constructors
	public AltableBarrierTimeout (AltableBarrierBase parent, long delay) {
		this.parent = parent;
		this.delay = delay;
		shouldTimeout = true;
	}
	//}}}
	
	//{{{ CSProcess method
	public void run() {
		try{
			Thread.currentThread().wait(delay);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// get token
//		AltableBarrierBase.tokenGiver.in().read();
		GuardGroup.claimLock(this);

		if (shouldTimeout) {
			parent.timeout();
		} else {
			System.out.println("I was killed " + this);
		}	

		// return token
//		AltableBarrierBase.tokenReciever.out().write(null);
		GuardGroup.releaseLock(this);
	}
	//}}}
	
	//{{{ public void kill()
	/*
	 * this method is called by a process which aborts or completes a
	 * synchronisation attempt, it sets a flag which stops the timeout
	 * process doing anything when it times out (as there is now no need
	 * for it to).  As such it is only called by processes which hold the
	 * global lock and the data it acts upon is only read by the this 
	 * process when it has in turn claimed the lock, thus it is safe.
	 */
	public void kill() {
		shouldTimeout = false;
	}
	//}}}
}
//}}}
