//{{{ package and import statements
package org.jcsp.demos.altableBarriers;

import org.jcsp.lang.*;
//}}}

//{{{ public class PauseTest
public class PauseTest {

	//{{{ contants
	public static final int TOTAL_PROCESSES = 20;
	//}}}

	public static void main (String[] args) {
		AltableBarrierBase pause = new AltableBarrierBase("\n\nPAUSE\n\n");
		AltableBarrierBase skip = new AltableBarrierBase("SKIP");
		CSProcess[] procs = new CSProcess[TOTAL_PROCESSES];

		CSProcess tim = SampleProcesses.timProc(5000, pause);
		procs[0] = tim;
		for (int i = 1; i < TOTAL_PROCESSES; i++) {
			GuardedCode pauseCode = new GuardedCode(pause,
			new CSProcess() {
				public void run() {
					System.out.println("I have been paused");
					System.out.println("I will die soon");
					try {
						Thread.sleep(5000);
					} catch (Exception e) {}
					System.out.println("about to die");
					System.exit(0);
				}
			}
			);
			GuardedCode skipCode = new GuardedCode(skip, new Skip());
			CSProcess spin = SampleProcesses.guardedProc(
			  new GuardedCode[] {pauseCode, skipCode}
			);
			procs[i] = spin;
		}

		Parallel par = new Parallel(procs);
		par.run();
		
	}

}
//}}}
