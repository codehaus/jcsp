//{{{ package and import statements
package org.jcsp.demos.altableBarriers;

import org.jcsp.lang.*;
import org.jcsp.awt.*;
import java.awt.*;
import java.awt.event.*;
//}}}
//{{{ public class KeyTest
public class KeyEvent {

	public static void main(String[] args) {
		final Frame frame = new Frame("KeyTest");
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});

		int nums = KeyEvent.VK_Z - KeyEvent.VK_A + 1;
		final One2OneChannel chans = new One2OneChannel[nums];
		final int[] keys = new int[nums];
		for (int i = 0; i < nums; i++) {
			int keys[i] = KeyEvent.VK_A + i;
			chans[i] = Channel.createOne2One();
		}
	}
}
//}}}
//{{{ class KeyEventDistributor
class KeyEventDistributor implements CSProcess {
	public ChannelInput in;
	public ChannelOutput[] outs;
	public int[] keys[];

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
	private ChannelInpit mid;
	private AltableBarrier[] lows;

	HighMidLow(AltableBarrier high,
	 ChannelInput mid, AltableBarrier[] lows) {
		this.high = high;
		this.mid = mid;
		this.lows = lows;
	}

	public void run() {
		GuardGroup h = new GuardGroup(new AltableBarrier[] {high});
		GuardGroup l = new guardGroup(lows);
		Alternative alt = new Alternative(new Guard[] {
			h, mid, low
		});

		while (true) {
			int index = alt.priSelect();
			
		}
	}
}
//}}}
