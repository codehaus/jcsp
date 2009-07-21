//{{{ package and import statements
package org.jcsp.lang;

//}}}

//{{{ public class AltableBarrierTest
public class AltableBarrierTest {
	//{{{ constants
	private static final int PROCESSES = 9;

	private static AltableBarrierBase base1 = new AltableBarrierBase();
	private static AltableBarrierBase base2 = new AltableBarrierBase();
	//}}}

	public static void main (String[] args) {
		CSProcess[] processes = new CSProcess[PROCESSES];

		for (int i = 0; i < PROCESSES; i++) {
			final Guard[] guards = createGuards();
			final int processNumber = i;

			processes[i] = new CSProcess() {
				public void run() {
					System.out.println("I am process " + processNumber);
					Alternative alt = new Alternative(guards);

					int index = alt.priSelect();

					System.out.println("picked number " + index);
				}
			};
			
		}

		Parallel par = new Parallel(processes);
		par.run();
	}

	private static Guard[] createGuards() {
		AltableBarrier bar1 = new AltableBarrier(base1);
		AltableBarrier bar2 = new AltableBarrier(base2);

		GuardGroup g1 = new GuardGroup(new Guard[]{}, new AltableBarrier[]{bar1});
		GuardGroup g2 = new GuardGroup(new Guard[]{}, new AltableBarrier[]{bar2});

		return new Guard[] {g1, g2};
	}
}
//}}}
