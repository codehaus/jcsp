//{{{package and import statements
package org.jcsp.lang;

import java.util.*;
//}}}
//{{{ public class AltableBarrierBase
public class AltableBarrierBase {

	//{{{ constants
	public static final int NOT_READY = 0;
	public static final int NOT_SYNCING_NOW = 1;
	public static final int PROBABLY_READY = 2;
	public static final int SELECTED = 3;
	//}}}

	//{{{ private fields
	private Vector committedBarriers;
	private Vector altableBarriers;
	//}}}

	//{{{ constructors
	public AltableBarrierBase() {
		committedBarriers = new Vector();
		altableBarriers = new Vector();	
	}
	//}}}

	//{{{ public methods
	//{{{ public int getStatus()
	public int getStatus() {
		// if any committed barriers are not ready then not ready
		
		// if any altable barriers are not syncing now then
		// status is not syncing now

		// if any altable barriers have selected this barrier then 
		// status is selected

		// otherwise status is probably ready
		return PROBABLY_READY;
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
	//}}}

	//}}}
}
//}}}
