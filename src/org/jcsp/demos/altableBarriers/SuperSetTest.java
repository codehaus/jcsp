//{{{ package and import statements
package org.jcsp.demos.altableBarriers;

import org.jcsp.lang.*;
//}}}

//{{{ public class SuperSetTest
public class SuperSetTest {
	//{{{ description
	/*
 	 * This class is used to test whether or not PICOMS AltableBarriers are
 	 * capable of selecting a high priority barrier (a) where that high 
 	 * priority barrier's set of enrolled processes is a strict super-set 
 	 * of the enrolled processes of 2 other barriers (b and c).  Further 
 	 * the intersection of b and c must be null and all processes must 
 	 * enrolled on a and (b or c) must be in a position to choose either 
 	 * (with a being higher priority than the other 2 being the only 
 	 * constraint).
 	 */
	//}}}
	
	//{{{ constants
	public static final int NUM_B = 100;
	public static final int NUM_C = 100;
	public static final int NUM_SKIP = 0;
	public static final int TOTAL;

	static {
		TOTAL = NUM_B + NUM_C + NUM_SKIP + 1;
	}
	//}}}

	//{{{ public static void main() 
	public static void main(String [] args) {
		AltableBarrierBase a = new AltableBarrierBase("PAUSE");
		AltableBarrierBase b = new AltableBarrierBase("B");
		AltableBarrierBase c = new AltableBarrierBase("C");

		CSProcess[] procs = new CSProcess[TOTAL];
		int i = 0;
		procs[i] = SampleProcesses.timProc(5000, a);
		i++;
		
		for (int startPoint = i; i < startPoint + NUM_B; i++) {
			CSProcess proc = createPauseSpinProc(a, b);
			procs[i] = proc;
		}
		for (int startPoint = i; i < startPoint + NUM_C; i++) {
			CSProcess proc = createPauseSpinProc(a, c);
			procs[i] = proc;
		}
		for (int startPoint = i; i < startPoint + NUM_SKIP; i++) {
			CSProcess proc = createPauseSpinProc(a,
			  new AltableBarrierBase());
			procs[i] = proc;
		}

		Parallel par = new Parallel(procs);
		par.run();
	}
	//}}}
	
	//{{{ public static CSProcess createPauseSpinProc
	public static CSProcess createPauseSpinProc (
	  AltableBarrierBase pause, AltableBarrierBase spin) {
		GuardedCode pauseCode = new GuardedCode(pause,
			new CSProcess() {
			public void run() {
				System.out.println("I have been paused");
				System.out.println("will die soon");

				try {
				Thread.sleep(5000);
				} catch (Exception e) {}

				System.out.println("about to die");
				System.exit(0);
			}
			}
		);
		GuardedCode spinCode = new GuardedCode (spin, new Skip());
		CSProcess proc = SampleProcesses.guardedProc(
		  new GuardedCode[] {pauseCode, spinCode}
		);

		return proc;
	}
	//}}}
}
//}}}
