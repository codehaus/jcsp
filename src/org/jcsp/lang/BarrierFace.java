//{{{ package and import statements
package org.jcsp.lang;

import java.util.*;
import org.jcsp.lang.*;
//}}}

//{{{ public class BarrierFace
public class BarrierFace implements ABConstants {

	//{{{ static block
	static {
		faces = new HashMap();
	}
	//}}}

	//{{{ public static fields
	public static HashMap faces;
	//}}}

	//{{{ fields
	public Vector guardGroups;
	public Object key;
	public AltableBarrier selected;
	public Object lock;

	public int topIndex, bottomIndex;

	// flag to prevent stealing during either the syncrhonising or aborting
	// sequence, set to true between the times when a process is awoken and
	// the time when it claims the global lock, false at other times.
	public boolean waking;
	//}}}
	

	//{{{ public BarrierFace (Alternative alt)
	public BarrierFace(Alternative alt) {
		key = alt;
		guardGroups = new Vector();
		for (int i = 0; i < alt.guard.length; i++) {
			if (alt.guard[i] instanceof GuardGroup) {
				guardGroups.add(alt.guard[i]);
			}
		}
		faces.put(key, this);

		selected = null;
		lock = null;

		topIndex = 0;
		bottomIndex = 0;

		waking = false;
	}
	//}}}
	
	//{{{ public void dispose() 
	public void dispose() {
		faces.remove(key);
	}
	//}}}
}
//}}}
