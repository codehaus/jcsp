//{{{ package and import statements
package org.jcsp.lang;
//}}}

//{{{ public class AltableBarrierTimeout extends CSProcess
public class AltableBarrierTimeout extends CSProcess {

	//{{{ fields
	private long delay;
	private AltableBarrierBase parent;
	//}}}

	//{{{ constructors
	public AltableBarrierTimeout (AltableBarrierBase parent, long delay) {
		this.parent = parent;
		this.delay = delay;
	}
	//}}}
	
	//{{{ CSProcess method
	public void run() {
		Thread.currentThread.wait(delay);

		// get token
		AltableBarrierBase.tokenGiver.in().read();

		parent.timeout();	

		// return token
		AltableBarrierBase.tokenReciever.out.write(null);
	}
	//}}}
}
//}}}
