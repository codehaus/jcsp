//{{{ package and import statements
package org.jcsp.demos.altableBarriers;

import org.jcsp.lang.*;
//}}}

//{{{ public class ConflictTest
public class ConflictTest {

	public static void main(String[] args) {
		AltableBarrierBase a = new AltableBarrierBase("\n\nA\n\n");
		AltableBarrierBase b = new AltableBarrierBase("\n\nB\n\n");

		int as = 1;
		int bs = 1;
		AltableBarrierBase[] aFirst = new AltableBarrierBase[]{a,b};
		AltableBarrierBase[] bFirst = new AltableBarrierBase[]{b,a};

		CSProcess[] aProcs = new CSProcess[as];
		CSProcess[] bProcs = new CSProcess[bs];

		for (int i = 0; i < as; i++) {
			aProcs[i] = SampleProcesses.priProc(aFirst);
		}
		for (int i = 0; i < bs; i++) {
			bProcs[i] = SampleProcesses.priProc(bFirst);
		}

		(new Parallel(new CSProcess[]{
			new Parallel(aProcs),
			new Parallel(bProcs)
		})).run();
	}
}
//}}}
