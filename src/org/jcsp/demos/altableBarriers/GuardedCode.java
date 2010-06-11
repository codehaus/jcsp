//{{{ package and import statements
package org.jcsp.demos.altableBarriers;

import org.jcsp.lang.*;
//}}}

//{{{ public class GuardedCode 
public class GuardedCode {

	public AltableBarrierBase guard;
	public CSProcess code;

	public GuardedCode (AltableBarrierBase guard, CSProcess code) {
		this.guard = guard;
		this.code = code;
	}
}
//}}}
