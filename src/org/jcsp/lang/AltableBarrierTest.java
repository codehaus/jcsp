//{{{ package and import statements
package org.jcsp.lang;

//}}}

//{{{ public class AltableBarrierTest
public class AltableBarrierTest implements ABConstants {
	//{{{ constants
	private static final int PROCESSES = 100;

	private static AltableBarrierBase base1 = new AltableBarrierBase("Barrier #1");
	private static AltableBarrierBase base2 = new AltableBarrierBase("Barrier #2");
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
					if (guards[index] instanceof GuardGroup) {
						GuardGroup gg = (GuardGroup) guards[index];
						AltableBarrier ab = gg.lastSynchronised();
						System.out.println("the barrier was " + ab);
					}
					System.out.println("picked number " + index);
				}
			};
			
		}
		//{{{ create a process which won't sync on the first guard
		final Guard[] guards = createGuards();
		processes[PROCESSES-1] = new CSProcess() {
			public void run() {
				final Guard[] myGuards = new Guard[] {guards[1]};
				//{{{ let everyone know that the first barrier is not going to be synced on
//				AltableBarrierBase.tokenGiver.in().read();
				
				GuardGroup gg = (GuardGroup) guards[0];
				AltableBarrier noSync = gg.guards[0];
				Alternative alt = new Alternative(myGuards);

				GuardGroup.claimLock(alt);
				
				noSync.setStatus(AltableBarrier.UNPREPARED);

				GuardGroup.releaseLock(alt);

//				AltableBarrierBase.tokenReciever.out().write(null);	
				//}}}
				System.out.println("I am the spoiler");

				int index = alt.priSelect();
				System.out.println("spoiler picked " + index);
				GuardGroup picked = (GuardGroup) myGuards[index];
				System.out.println(picked + " " + picked.lastSynchronised());	
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
