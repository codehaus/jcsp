//{{{ package and import statements
package org.jcsp.lang;

import java.util.*;
//}}}
//{{{ description
/*
 * A class for storing information about which barriers are currently
 * high priority for a given process as well as which barrier a process
 * is currently enrolled on.
 *
 * whenever a barrier is enabled, a BarrierFace object should be given to
 * the that barrier's front-end so that (if and when that barrier is actually
 * selected by a process) the information about transfering a process from
 * one barrier sync to another is readily available.  When a barrier is
 * disabled again its BarrierFace object should be set to null.
 */
//}}}
//{{{ public class BarrierFace
public class BarrierFace {

	//{{{ fields
	public Vector higherBarriers, lowerBarriers;
	public AltableBarrier selectedBarrier;
	public Object key;
	//}}}

	//{{{ public BarrierFace(higherBarriers, selectedBarrier, key)
	public BarrierFace(Vector higherBarriers, Vector lowerBarriers, AltableBarrier selectedBarrier, Object key) {
		this.higherBarriers = higherBarriers;
		this.lowerBarriers = lowerBarriers;
		this.selectedBarrier = selectedBarrier;
		this.key = key;
	}
	//}}}
}
//}}}
