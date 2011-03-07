//{{{ package and import statements
package org.jcsp.demos.altableBarriers;

import org.jcsp.lang.*;
//}}}
//{{{ public class ConflictTest
public class ConflictTest {
	public static final int ITERATIONS = 1000;

	public static final int PROCESSES = 100;
	public static final int OVERLAP = 4;

	public static final boolean USE_PCOMS = true;
	public static final int DEFAULT = ABConstants.PREPARED;

	//{{{ barrier resolver
	class BarrierResolver implements CSProcess{
	
		Alternative alt;
		boolean isRecorder;
		BarrierResolver (Alternative alt, boolean isRecorder) {
			this.alt = alt;
			this.isRecorder = isRecorder
		}

		public void run() {
			long start = System.currentTimeMillis();
			for (int i = 0; i < ITERATIONS; i++) {
				alt.priSelect();
			}
			long end = System.currentTimeMillis();
			if (isRecorder) {
				System.out.println("time was " end - start);
				System.exit(0);
			}
		}
	}
	//}}}

	public static void main(String[] args) {
		CSProcess[] procs = new CSProcess[PROCESSES];
		Alternative alts = new Alternative[PROCESSES];

		createAlts();


		for (int i = 0; i < PROCESSES; i++) {
			procs[i] = new BarrierResolver(alts[i], (i==0));
		}

		Parallel par = new Parallel(procs);

		par.start();
	}

	public static void createAlts() {
		if (USE_PCOMS) {
			createPCOMS();
		} else {
			createAlting();
		}
	}
	public static void createAlting() {
		AltingBarrier[] abs = new AltingBarrier[PROCESSES];
		for (int i = 0; i < PROCESSES; i++) {
			abs[i] = null;
		}

		for (int i = 0; i < PROCESSES; i++) {
			AltingBarrier[] bars = new AltingBarrier[OVERLAP];

			for (int j = 0; j < OVERLAP; j++) {
				int n = (i+j) % PROCESSES;
				if (abs[n] == null) {
					abs[n] = AltingBarrier.create();
					bars[j] = abs[n];
				} else {
					bars[j] = abs[n].expand();
				}
			}

			alts[i] = new Alternative(bars);
		}
	}

	public static void createPCOMS() {
		AltableBarrierBase[] abbs = new AltableBarrierBase[PROCESSES];

		for(int i = 0; i < PROCESSES; i++) {
			abbs[i] = new AltableBarrierBase("BAR"+i);
		}
		for (int i = 0; i < PROCESSES; i++) {
			AltalbeBarrier[] abs = new AltableBarrier[OVERLAP];
			for (int j = 0; j < OVERLAP; j++) {
				abs[j] = new AltableBarrier(
					abbs[(i+j)%PROCESSES], DEFAULT);
			}
			GuardGroup gg = new GuardGroup(abs);

			alts[i] = new Alternative(new Guard[]{gg});
		}
	}


}
//}}}
