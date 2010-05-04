//{{{ package and import statements
package org.jcsp.lang;

//}}}

//{{{ public class AltableBarrierTest
public class AltableBarrierTest implements ABConstants {
	//{{{ constants
	private static final int PROCESSES = 4;

	private static AltableBarrierBase base1 = new AltableBarrierBase();
	private static AltableBarrierBase base2 = new AltableBarrierBase();
	//}}}

	public static void main (String[] args) {
		CSProcess[] processes = new CSProcess[PROCESSES];

		for (int i = 0; i < PROCESSES-1; i++) {
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
		//{{{ create a process which won't sync on the first guard
		processes[PROCESSES-1] = new CSProcess() {
			public void run() {
				final Guard[] guards = createGuards();
				final Guard[] myGuards = new Guard[] {guards[1]};
				//{{{ let everyone know that the first barrier is not going to be synced on
				AltableBarrierBase.tokenGiver.in().read();
				
				GuardGroup gg = (GuardGroup) guards[0];
				AltableBarrier noSync = gg.guards[0];
				
				noSync.setStatus(AltableBarrier.UNPREPARED);

				AltableBarrierBase.tokenReciever.out().write(null);	
				//}}}
				System.out.println("I am the spoiler");

				Alternative alt = new Alternative(myGuards);
				int index = alt.priSelect();
				System.out.println("spoiler picked " + index);	
			}	
		};
		//}}}

		Parallel par = new Parallel(processes);
		par.run();
	}

	private static Guard[] createGuards() {
		AltableBarrier bar1 = new AltableBarrier(base1);
		AltableBarrier bar2 = new AltableBarrier(base2);

		GuardGroup g1 = new GuardGroup(new AltableBarrier[]{bar1});
		GuardGroup g2 = new GuardGroup(new AltableBarrier[]{bar2});

		return new Guard[] {g1, g2};
	}

}
//}}}
