//{{{ package and import statements
package org.jcsp.lang;
//}}}
//{{{ public class AltableBarrier
public class AltableBarrier {

	//{{{ constants
	public static final int NOT_READY = 0;
	public static final int NOT_SYNCING_NOW = 1;
	public static final int PROBABLY_READY = 2;
	public static final int SELECTED = 3;

	public static final int IMPLICIT_READY = 4;
	public static final int EXPLICIT_READY = 5; // trumps all
	public static final int IMPLICIT_NOT_READY = 6;
	public static final int EXPLICIT_NOT_READY = 7; //trumps all but 5

	public static final Object SUCCESS = new Object();
	public static final Object FAILURE = new Object();
	public static final Object TIMEOUT = new Object();
	//}}}
	//{{{ public fields
	public int status;
	private AltableBarrierBase parent;
	public GuardGroup guardGroup;

	public AltingChannelInput in; // the end the alting process uses
	public ChannelOutput out; // the end the barrier uses.
	//}}}


	//{{{ constructors
	public AltableBarrier (AltableBarrierBase parent) {
		this.parent = parent;
		status = IMPLICIT_READY;

		parent.enroll(this);

		One2OneChannel chan = StandardChannelFactory.getDefaultInstance().createOne2One();

		in = chan.in();
		out = chan.out();
	}
	//}}}


	//{{{ public methods
	//{{{ public void attemptSynchronisation()
	public void attemptSynchronisation() {
		//FIXME:  danger, this method is invoked as a result of
		// an Alternative's select method being called.  This means
		// that the thread running this method cannot have a lock
		// on the AltableBarrier Class object AND wait on other
		// parties turning up.  lock on it must be resigned before
		// one can wait for other parties to turn up, need to think
		// about that ...
	}
	//}}}
	//{{{ public void setState(int state)
	/*
	 * get back status of the AltableBarrierBase
	 */
	public int setStatus(int status) {
		if (this.status == EXPLICIT_READY && status == IMPLICIT_NOT_READY) {
			// do nothing
		} else if (this.status == EXPLICIT_NOT_READY && status == IMPLICIT_READY){
			// do nothing
		} else {
			this.status = status;
		}
		return parent.checkStatus(this);
	}
	//}}}
	//}}}
}
//}}}
