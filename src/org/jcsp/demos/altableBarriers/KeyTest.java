//{{{ package and import statements
package org.jcsp.demos.altableBarriers;

import org.jcsp.lang.*;
import org.jcsp.awt.*;
import java.awt.*;
import java.awt.event.*;
//}}}
//{{{ public class KeyTest
public class KeyTest {

	public static void main(String[] args) {
		final Frame frame = new Frame("KeyTest");
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});

		int nums = KeyEvent.VK_Z - KeyEvent.VK_A + 1;
		final One2OneChannel[] chans = new One2OneChannel[nums];
		final ChannelOutput[] outs = new ChannelOutput[nums];
		final int[] keys = new int[nums];
		for (int i = 0; i < nums; i++) {
			keys[i] = KeyEvent.VK_A + i;
			chans[i] = Channel.createOne2One();
			outs[i] = chans[i].out();
		}
		final Any2OneChannel keyDis = Channel.createAny2One();
		final One2OneChannel buttonChannel = Channel.createOne2One();
		final ActiveButton button = new ActiveButton(null, buttonChannel.out(), "Button");
		KeyEventDistributor ked = new KeyEventDistributor(
		 keyDis.in(), outs, keys);
		frame.add(button);
		frame.setSize(500, 500);
		frame.show();

		KeyboardFocusManager kfm =
		 KeyboardFocusManager.getCurrentKeyboardFocusManager();
		kfm.addKeyEventPostProcessor(new KeyEventPostProcessor() {
			public boolean postProcessKeyEvent(KeyEvent e) {
				keyDis.out().write(e);
				return false;
			}
		});

		AltableBarrierBase pause = new AltableBarrierBase("PAUSE");
		AltableBarrierBase[] bars = new AltableBarrierBase[nums];
		CSProcess[] procs = new CSProcess[nums];

		for (int i = 0; i < nums; i++) {
			bars[i] = new AltableBarrierBase("BAR"+i);
		}
		for (int i = 0; i < nums; i++) {
			AltableBarrier ab1 = new AltableBarrier(bars[i]);
			AltableBarrier ab2 = new AltableBarrier(bars[(i+1)%nums]);
			AltableBarrier kill = new AltableBarrier(pause);

			if (i != 20) {
				procs[i] = new HighMidLow(kill, chans[i].in(),
				 new AltableBarrier[] {ab1, ab2});
			} else {
				procs[i] = new HighMidLow(kill,
				 buttonChannel.in(),
				 new AltableBarrier[] {ab1, ab2});
			}
		}
		
		(new Parallel(new CSProcess[] {
			new Parallel(procs),
			button,
			ked,
			SampleProcesses.timProc(5000, pause)
			
		})).run();		
	}
}
//}}}
//{{{ class KeyEventDistributor
class KeyEventDistributor implements CSProcess {
	public ChannelInput in;
	public ChannelOutput[] outs;
	public int[] keys;

	public KeyEventDistributor(ChannelInput in,
	 ChannelOutput[] outs, int[] keys) {
		this.in = in;
		this.outs = outs;
		this.keys = keys;
	}

	public void run() {
		while(true) {
			KeyEvent e = (KeyEvent) in.read();
			int code = e.getKeyCode();
			for (int i = 0; i < keys.length; i++) {
				if (keys[i] == code) {
					outs[i].write(e);
					break;
				}
			}
		}
	}
}
//}}}
//{{{ class HighMidLow implements CSProcess
class HighMidLow implements CSProcess {

	private AltableBarrier high;
	private AltingChannelInput mid;
	private AltableBarrier[] lows;

	HighMidLow(AltableBarrier high,
	 AltingChannelInput mid, AltableBarrier[] lows) {
		this.high = high;
		this.mid = mid;
		this.lows = lows;
	}

	public void run() {
		GuardGroup h = new GuardGroup(new AltableBarrier[] {high});
		GuardGroup l = new GuardGroup(lows);
		Alternative alt = new Alternative(new Guard[] {
			h, mid, l
		});

		while (true) {
			int index = alt.priSelect();
			
			Guard selected = alt.guard[index];
			if (selected instanceof GuardGroup) {
				AltableBarrier ab = ((GuardGroup)selected).lastSynchronised();
				System.out.println(ab);
				if (ab.equals(high)) {
					System.exit(0);
				}
			} else {
				Object o = mid.read();
				System.out.println(o);
			}
		}
	}
}
//}}}
