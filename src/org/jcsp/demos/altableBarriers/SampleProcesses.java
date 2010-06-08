//{{{ import and package statements
package org.jcsp.demos.altableBarriers;

import org.jcsp.lang.*;
//}}}

//{{{ public class SampleProcesses
public class SampleProcesses {

	public static CSProcess priProc(AltableBarrierBase[] bars) {
		return (new PriorityProcess(bars));
	}
}
//}}}
//{{{ class PriorityProcess extends CSProcess
class PriorityProcess implements CSProcess {

	Alternative alt;
	GuardGroup[] ggs;

	PriorityProcess(AltableBarrierBase[] barriers) {
		GuardGroup[] ggs = new GuardGroup[barriers.length];
		for (int i = 0; i < ggs.length; i++) {
			AltableBarrier ab = new AltableBarrier(barriers[i]);
			ggs[i] = new GuardGroup(new AltableBarrier[] {ab});
		}
		alt = new Alternative(ggs);
		this.ggs = ggs;
	}

	public void run() {
		while (true) {
			int index = alt.priSelect();
			System.out.println("index = " + index);
			GuardGroup gg = (GuardGroup) ggs[index];
			AltableBarrier ab = (AltableBarrier) gg.lastSynchronised();
			System.out.println(ab);
		}
	}
}
//}}}
