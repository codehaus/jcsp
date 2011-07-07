//{{{ package and import statements
package org.jcsp.demos.altableBarriers;

import org.jcsp.lang.*;
import org.jcsp.awt.*;
import java.util.*;
import java.awt.*;
//}}}

//{{{ public class VisualDemo implements CSProcess
public class VisualDemo3 implements CSProcess {

	//{{{ constants
	public static HashMap uniqueBarriers = new HashMap(); // maps barriers to colours

	public static int OVERLAP = 3;

	public AltableBarrier high, left, right;
	public AltableBarrier[] mids;
	public Guard mid;
	public Alternative alt, unpause;
	public HashMap graphicsMap; // maps AltableBarriers to graphics commands
	public DisplayList dl;

	public ChannelOutput toReporter;
	//}}}

	//{{{ public static void main()
	public static void main(String[] args) {
		final ActiveClosingFrame acf = 
		 new ActiveClosingFrame("Vis. Demo");
		final Frame frame = acf.getActiveFrame();

		int HEIGHT = 6;
		int WIDTH = 6;
		int nums = WIDTH * HEIGHT;
		final AltableBarrierBase pause = new AltableBarrierBase("PAUSE");
		AltableBarrierBase[] bars = new AltableBarrierBase[nums];
		ActiveCanvas[] canvasProcs = new ActiveCanvas[nums];
		CSProcess[] procs = new CSProcess[nums];
		CSProcess[] randoms = new CSProcess[nums];
		final One2OneChannel[] chans = new One2OneChannel[nums];
		final One2OneChannel buttonChan = Channel.createOne2One();
		final ActiveButton button = new ActiveButton(
		 null, buttonChan.out(), "Pause");
		final Any2OneChannel toSyncCounter = Channel.createAny2One();
		ActiveContainer canvasContainer =
		 new ActiveContainer();
		canvasContainer.setLayout(new GridLayout(HEIGHT,WIDTH));

		SyncCounter syncCounter = new SyncCounter(toSyncCounter.in());
		ChannelOutput outToSyncCounter = toSyncCounter.out();

		for (int i = 0; i < nums; i++) {
			bars[i] = new AltableBarrierBase("BAR"+i);
			chans[i] = Channel.createOne2One();
		}
		for (int i = 0; i < nums; i++) {
			AltableBarrierBase[] abs =
			 new AltableBarrierBase[OVERLAP];
			for (int j = 0; j < abs.length; j++) {
				abs[j] = bars[(i+j)%nums];
			}
			AltableBarrierBase ab1 = bars[i];
			AltableBarrierBase ab2 = bars[(i+1)%nums];
			canvasProcs[i] = new ActiveCanvas();
			DisplayList dl = new DisplayList();
			canvasProcs[i].setPaintable(dl);
			canvasProcs[i].setSize(100,100);
			canvasContainer.add(canvasProcs[i]);

			procs[i] = new VisualDemo3(pause,
			 abs[0], abs[OVERLAP-1], abs,
			 (AltingChannelInput)chans[i].in(),
			 dl, outToSyncCounter
			);
			final ChannelOutput out = chans[i].out();
			randoms[i] = new CSProcess() {
				public void run() {
				Random r = new Random();
				while (true) {
					try {
					Thread.sleep(r.nextInt(10000));
					} catch (Exception e) {}
					out.write("blah");
				}
				}
			};
		}
		frame.setLayout(new BorderLayout());
		frame.add(canvasContainer, BorderLayout.CENTER);
		frame.add(button, BorderLayout.SOUTH);
		frame.add(syncCounter, BorderLayout.EAST);
		frame.pack();
		frame.setVisible(true);
		frame.show();

		CSProcess pauseProc = SampleProcesses.timProc(5000, pause);	
		(new Parallel(new CSProcess[] {
			new Parallel(procs),
			//new Parallel(randoms),
			new Parallel(canvasProcs),
			syncCounter,
			button,
//			canvasContainer,
			//pauseProc,
			acf,
			//{{{ pause button
			/*
			new CSProcess() {
				public void run() {
				ChannelInput in = buttonChan.in();
				AltableBarrier ab = new AltableBarrier(
				 pause, ABConstants.UNPREPARED);
				Alternative alt = new Alternative(new Guard[] {
				 new GuardGroup(new AltableBarrier[]{ab})
				});

				while (true) {
					System.out.println("\n\n\n\n\n\n\n\n");
					in.read();
					alt.priSelect();
				}
				}
			}
			*/
			//}}}
		})).run();
	}
	//}}}

	//{{{ public VisualDemo3()
	public VisualDemo3(AltableBarrierBase high, AltableBarrierBase left,
	 AltableBarrierBase right, AltableBarrierBase[] bars, 
	 Guard mid, DisplayList dl,
	 ChannelOutput toReporter) {
		addBarrier(high, Color.RED);
		addBarrier(left);
		addBarrier(right);

		this.high = new AltableBarrier(high);
		this.left = new AltableBarrier(left);
		this.right= new AltableBarrier(right);
		this.mids = new AltableBarrier[bars.length];
		for (int i = 0; i < mids.length; i++) {
			if (bars[i] == left) {
				mids[i] = this.left;
			} else if (bars[i] == right) {
				mids[i] = this.right;
			} else {
				mids[i] = new AltableBarrier(bars[i]);
			}
			addBarrier(bars[i]);
		}
		this.mid = mid;
		this.dl = dl;
		this.toReporter = toReporter;

		recalculateAlt();
/*
		alt = new Alternative(new Guard[] {
			//new GuardGroup(new AltableBarrier[] {this.high}),
			//mid,
			new GuardGroup(new AltableBarrier[] {
			 this.left, this.right})
		});
*/
		unpause = new Alternative(new Guard[] {
			new GuardGroup(new AltableBarrier[] {this.high})
		});

		graphicsMap = new HashMap();
		GraphicsCommand[] commands;
		//{{{ high
		commands = new GraphicsCommand[] {
			new GraphicsCommand.SetColor(Color.black),
			new GraphicsCommand.DrawRect(0,0,100,100),
			new GraphicsCommand.DrawString(high.name,0,50),
			new GraphicsCommand.SetColor(
			 (Color) uniqueBarriers.get(high)),
			new GraphicsCommand.FillRect(0,0,100,25)
		};
		graphicsMap.put(this.high, commands);
		//}}}
		//{{{ mid
		commands = new GraphicsCommand[] {
			new GraphicsCommand.SetColor(Color.black),
			new GraphicsCommand.DrawRect(0,0,100,100),
			new GraphicsCommand.DrawString("MID", 0,50),
			new GraphicsCommand.SetColor(Color.blue),
			new GraphicsCommand.FillRect(45,50,10,25)
		};
		graphicsMap.put(this.mid, commands);
		//}}}
		//{{{ mids
		for (int i = 0; i < mids.length; i++) {
			commands = new GraphicsCommand[] {
				new GraphicsCommand.SetColor(Color.black),
				new GraphicsCommand.DrawRect(0,0,100,100),
				new GraphicsCommand.DrawString(bars[i].name,0,50),
				new GraphicsCommand.SetColor(
				 (Color) uniqueBarriers.get(bars[i])),
				new GraphicsCommand.FillRect(0,75,100,25)
			};
			graphicsMap.put(mids[i], commands);
		}
		//}}}
		//{{{ left
		commands = new GraphicsCommand[] {
			new GraphicsCommand.SetColor(Color.black),
			new GraphicsCommand.DrawRect(0,0,100,100),
			new GraphicsCommand.DrawString(left.name,0,50),
			new GraphicsCommand.SetColor(
			 (Color) uniqueBarriers.get(left)),
			new GraphicsCommand.FillRect(0,75,40,25)
		};
		graphicsMap.put(this.left, commands);
		//}}}
		//{{{ right
		commands = new GraphicsCommand[] {
			new GraphicsCommand.SetColor(Color.black),
			new GraphicsCommand.DrawRect(0,0,100,100),
			new GraphicsCommand.DrawString(right.name,0,50),
			new GraphicsCommand.SetColor(
			 (Color) uniqueBarriers.get(right)),
			new GraphicsCommand.FillRect(60,75,40,25)
		};
		graphicsMap.put(this.right, commands);
		//}}}
	}
	//}}}

	//{{{ public void run()
	public void run() {
		while(true) {
			dl.set(new GraphicsCommand[] {
				new GraphicsCommand.SetColor(Color.white),
				new GraphicsCommand.FillRect(0,0,100,100),
				new GraphicsCommand.SetColor(Color.black),
				new GraphicsCommand.DrawRect(0,0,100,100),
				GraphicsCommand.NULL
			});

			int index = alt.priSelect();
			Guard guard = alt.guard[index];
			Object selected = guard;
			AltableBarrier temp = null;
			if (guard instanceof GuardGroup) {
				temp = ((GuardGroup) guard).lastSynchronised();
				selected = temp;
				if (temp.face != null) {
					throw (new RuntimeException("argh face"));
				}
			} else {
				((ChannelInput) selected).read();
			}
			
			GraphicsCommand[] commands =
			 (GraphicsCommand[]) graphicsMap.get(selected);
			dl.set(commands);

			if (temp == high) {
				unpause.priSelect();
			} else if (temp != null) {
				// communicate to aggregator

				// set the low priority barrier to the
				// last selected.
				recalculateAlt(temp);
				toReporter.write(temp.parent.name);				
			}

			try {	
			Thread.sleep(500);
			} catch(Exception e) {}
		}
	}
	//}}}

	//{{{ private void recalculateAlt(AltableBarrier lastSelected)
	private void recalculateAlt() {
		recalculateAlt(null);
	}
	private void recalculateAlt(AltableBarrier lastSelected) {
		Vector topBarriers = new Vector();
		for (int i = 0; i < mids.length; i++) {
			topBarriers.add(mids[i]);
		}
		if (lastSelected != null) {
			topBarriers.remove(lastSelected);
		}

		AltableBarrier[] bars = new AltableBarrier[topBarriers.size()];
		for (int i = 0; i < bars.length; i++) {
			bars[i] = (AltableBarrier) topBarriers.get(i);
		}



		if (lastSelected != null) {
			alt = new Alternative(new Guard[]{
			  new GuardGroup(bars),
			  new GuardGroup(new AltableBarrier[]{lastSelected})
			});
		} else {
			alt = new Alternative(new Guard[]{
			  new GuardGroup(bars)
			});
		}
	}
	//}}}

	// {{{ private static void addBarrier()
	private static void addBarrier(AltableBarrierBase bar) {
		Random r = new Random();
		Color col = new Color(0,r.nextInt(256),0);
		addBarrier(bar, col);
	}

	private static void addBarrier(AltableBarrierBase bar, Color col) {
		if (uniqueBarriers.get(bar) == null) {
			uniqueBarriers.put(bar, col);
		}		
	}
	//}}}

}
//}}}

//{{{ class SyncCounter extends ActiveContainer
class SyncCounter extends ActiveContainer {

	ChannelInput in;
	HashMap barCount;
	

	final int NUM_LABELS = 80;
	Label[] labels;

	SyncCounter (ChannelInput in) {

		this.in = in;

		this.barCount = new HashMap();

		setLayout(new GridLayout((NUM_LABELS / 4), 4));
		initialiseGrid();
	}

	public void run() {
		CSProcess proc = new CSProcess(){
			public void run() {
				try {
				run2();
				} catch (Exception e) {
					e.printStackTrace();
					System.exit(0);
				}
			}
		};
		ProcessManager manager = new ProcessManager(proc);
		manager.start();

		super.run();		
	}

	public void run2() {
		final int COM_THRESHOLD = 300;
		int com_counter = 0;
		while (true) {
			String s = (String) in.read();
			com_counter++;

			Integer count = (Integer) barCount.get(s);
	
			if (count == null) {
				barCount.put(s, new Integer(1));
			} else {
				int i = count.intValue();
				i++;
				barCount.put(s, new Integer(i));
			}


			if (com_counter > COM_THRESHOLD) {
				com_counter = 0;
				updateGrid();
			}
		}
	}


	private void initialiseGrid() {
		labels = new Label[NUM_LABELS];
		for (int i = 0; i < NUM_LABELS; i++) {
			labels[i] = new Label("##########");
			add(labels[i]);
		}
	}

	private void updateGrid() {
		Object[] keys = barCount.keySet().toArray();

		int keyCount = 0;
		for (int i = 0; i < NUM_LABELS; i = i + 2) {
			if (keyCount < keys.length) {
				String key = (String) keys[keyCount];
				Integer value = 
				  (Integer) barCount.get(key);
				labels[i].setText(key);
				labels[i+1].setText(value.toString());
			} else {
				labels[i].setText("");
				labels[i+1].setText("");
			}
			keyCount++;
		}
	}
}
//}}}
