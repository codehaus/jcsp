//{{{ package and import statements
package org.jcsp.demos.altableBarriers;

import org.jcsp.lang.*;
//}}}

//{{{ public class PauseTest
public class PauseTest {

	public static void main (String[] args) {
		AltableBarrierBase pause = new AltableBarrierBase("\n\nPAUSE\n\n");
		AltableBarrierBase skip = new AltableBarrierBase("SKIP");

		CSProcess tim = SampleProcesses.timProc(50, pause);
		CSProcess spin = SampleProcesses.priProc(
		  new AltableBarrierBase[] {pause, skip}
		);

		Parallel par = new Parallel(new CSProcess[] {tim, spin});
		par.run();
		
	}

}
//}}}
