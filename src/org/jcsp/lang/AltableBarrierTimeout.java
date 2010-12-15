//{{{ package and import statements
package org.jcsp.lang;

import java.io.*;
//}}}

//{{{ public class AltableBarrierTimeout implements CSProcess
public class AltableBarrierTimeout implements CSProcess {

	//{{{ static fields
	public static final String PATH="./log.txt";

	private static final PrintStream logOut;
	//}}}

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

	//{{{ static block
	static {
		File f = new File(PATH);
		OutputStream o = null;
		try {
			o = new FileOutputStream(f, false);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
		logOut = new PrintStream(o);
	}
	//}}}
	
	//{{{ CSProcess method
	public void run() {
		System.out.println(this + " has begun running");
		try{
			Thread.sleep(delay);
		} catch (Exception e) {
			e.printStackTrace();
		}
		//System.out.println(this + " timeout has elapsed");
		// get token
//		AltableBarrierBase.tokenGiver.in().read();
		GuardGroup.claimLock(this);


		if (shouldTimeout) {
//			System.out.println("tried to call parent timeout "+ this);
			// do some logging here
			if (parent.barrierSize() > 2) {
				parent.reportABs(logOut);
				logOut.println("##########################");
			}
			// timeout here
			parent.timeout();
		} else {
//			System.out.println("I was killed " + this);
		}	

		// return token
//		AltableBarrierBase.tokenReciever.out().write(null);
//		System.out.println("about to release " + this);
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
