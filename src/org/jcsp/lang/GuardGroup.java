//{{{ package and import statements
package org.jcsp.picoms;

import java.util.*;
import org.jcsp.lang.*;
//}}}
//{{{ public class GuardGroup extends Guard
public class GuardGroup extends Guard {

	//{{{ constants
	//}}}
	//{{{ fields
	public AltableBarrier selectedBarrier;
	public Alternative parent;
	//}}}
	
	//{{{ extends Guard
	//{{{ public boolean enable(Alternative alt)
	boolean enable(Alternative alt) {
		return false;
	}
	//}}}
	//{{{ public boolean disable()
	boolean disable() {
		return false;
	}
	//}}}
	//}}}
}
//}}}
