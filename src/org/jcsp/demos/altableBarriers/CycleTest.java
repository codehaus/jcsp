//{{{ package and import statements
package org.jcsp.demos.altableBarriers;

import org.jcsp.lang.*;
//}}}

//{{{ public class CycleTest
public class CycleTest {
	//{{{ constants
	public static final int CYCLE_BARRIERS = 400;
	public static final int PROCESSES_PER_COMBO = 2;
	public static final int TOTAL;

	static {
		TOTAL = (CYCLE_BARRIERS * PROCESSES_PER_COMBO) + 1;
	}
	//}}}
	
	//{{{ public static void main(String[] args)
	public static void main(String[] args) {
		AltableBarrierBase pause = new AltableBarrierBase("PAUSE");
		AltableBarrierBase[] bars = new AltableBarrierBase[CYCLE_BARRIERS];
		for (int i = 0; i < CYCLE_BARRIERS; i++) {
			bars[i] = new AltableBarrierBase("BAR"+i);
		}

		CSProcess[] procs = new CSProcess[TOTAL];
		procs[0] = SampleProcesses.timProc(5000, pause);

		for(int i = 0; i < CYCLE_BARRIERS; i++) {
			for (int j = 0; j < PROCESSES_PER_COMBO; j++) {
				int x = (i * PROCESSES_PER_COMBO)+j+1;
				AltableBarrierBase low = bars[i];
				AltableBarrierBase high = bars[(i+1)%CYCLE_BARRIERS];
				procs[x] = cycleProc(pause, low, high);
			}
		}

		Parallel par = new Parallel(procs);
		par.run();
	}
	//}}}
	
	//{{{ public static CSProcess CycleProc()
	private static CSProcess cycleProc(AltableBarrierBase p,
	  AltableBarrierBase l, AltableBarrierBase h) {
		final AltableBarrier pause  = new AltableBarrier(p);
		final AltableBarrier low = new AltableBarrier(l);
		final AltableBarrier high = new AltableBarrier(h);
		

		CSProcess proc = new CSProcess() {
		public void run() {
			while (true) {
				GuardGroup pg = new GuardGroup(new AltableBarrier[] {pause});
				GuardGroup cg = new GuardGroup(new AltableBarrier[] {low, high});
				Alternative alt = new Alternative(new GuardGroup[] {pg, cg});

				int index = alt.priSelect();
				if (index == 0) {
					System.out.println("pause called, erk");
					System.exit(0);
				}
			}
		}
		};
		return proc;
	}
	//}}}
}
//}}}
