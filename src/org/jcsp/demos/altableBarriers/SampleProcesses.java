//{{{ import and package statements
package org.jcsp.demos.altableBarriers;

import org.jcsp.lang.*;
import java.io.*;
//}}}

//{{{ public class SampleProcesses
public class SampleProcesses {

	public static CSProcess priProc(AltableBarrierBase[] bars) {
		return (new PriorityProcess(bars));
	}

	public static CSProcess arbProc(AltableBarrierBase[] bars) {
		return (new ArbitraryProcess(bars));
	}
	
	public static CSProcess waitProc(AltableBarrierBase base,
	  BufferedReader in) {
		return (new WaitingProcess(base, in));
	}

	public static CSProcess kybProc(AltableBarrierBase base) {
		return (new WaitingProcess(base));
	}
}
//}}}
//{{{ class PriorityProcess implments CSProcess
class PriorityProcess implements CSProcess {

	Alternative alt;
	GuardGroup[] ggs;

	//{{{ PriorityProcess (AltableBarrierBase[] barriers)
	PriorityProcess(AltableBarrierBase[] barriers) {
		GuardGroup[] ggs = new GuardGroup[barriers.length];
		for (int i = 0; i < ggs.length; i++) {
			AltableBarrier ab = new AltableBarrier(barriers[i]);
			ggs[i] = new GuardGroup(new AltableBarrier[] {ab});
		}
		alt = new Alternative(ggs);
		this.ggs = ggs;
	}
	//}}}

	//{{{ public void run()
	public void run() {
		while (true) {
			int index = alt.priSelect();
			System.out.println("index = " + index);
			GuardGroup gg = (GuardGroup) ggs[index];
			AltableBarrier ab = gg.lastSynchronised();
			System.out.println(ab);
		}
	}
	//}}}
}
//}}}
//{{{ class ArbitraryProcess implements CSProcess
class ArbitraryProcess implements CSProcess {

	Alternative alt;
	GuardGroup gg;

	//{{{ ArbitraryProcess (AltableBarrierBase[] barriers)
	ArbitraryProcess (AltableBarrierBase[] barriers) {
		AltableBarrier[] abs = new AltableBarrier[barriers.length];
		for (int i = 0; i < abs.length; i++) {
			AltableBarrier ab = new AltableBarrier(barriers[i]);
			abs[i] = ab;
		}
		gg = new GuardGroup(abs);
		alt = new Alternative(new Guard[] {gg});
	}
	//}}}

	//{{{ public void run() 
	public void run() {
		while (true) {
			int index = alt.priSelect();
			AltableBarrier ab = gg.lastSynchronised();

			System.out.println(ab);
		}
	}
	//}}}
}
//}}}

//{{{ class WaitingProcess implements CSProcess
class WaitingProcess implements CSProcess {

	BufferedReader in;
	AltableBarrier bar;

	//{{{ WaitingProcess (AltableBarrierBase base, BufferedReader in)
	WaitingProcess (AltableBarrierBase base) {
		this (base, new BufferedReader(
		  new InputStreamReader (System.in) 
		));
	}
	WaitingProcess (AltableBarrierBase base, BufferedReader in) {
		this.in = in;
		bar = new AltableBarrier(base, ABConstants.UNPREPARED);
	}
	//}}}	

	//{{{ public void run()
	public void run() {
		GuardGroup gg = new GuardGroup(new AltableBarrier[] {bar});
		Alternative alt = new Alternative(new Guard[] {gg});
		while (true) {
			try {
				in.read();
			} catch (IOException e) {}
			System.out.println("Got input");
			alt.priSelect();
			System.out.println("Barrier synced");
		}
	}
	//}}}
}
//}}}
