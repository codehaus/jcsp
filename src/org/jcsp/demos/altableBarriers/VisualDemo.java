//{{{ package and import statements
package org.jcsp.demos.altableBarriers;

import org.jcsp.lang.*;
import org.jcsp.awt.*;
import java.util.*;
import java.awt.*;
//}}}

//{{{ public class VisualDemo implements CSProcess
public class VisualDemo implements CSProcess {

	//{{{ constants
	public static HashMap uniqueBarriers = new HashMap(); // maps barriers to colours

	public AltableBarrier high, left, right;
	public Guard mid;
	public Alternative alt;
	public HashMap graphicsMap; // maps AltableBarriers to graphics commands
	public DisplayList dl;
	//}}}

	//{{{ public static void main()
	public static void main(String[] args) {
		final ActiveClosingFrame acf = 
		 new ActiveClosingFrame("Vis. Demo");
		final Frame frame = acf.getActiveFrame();

		int HEIGHT = 10;
		int WIDTH = 17;
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
		ActiveContainer canvasContainer =
		 new ActiveContainer();
		canvasContainer.setLayout(new GridLayout(HEIGHT,WIDTH));

		for (int i = 0; i < nums; i++) {
			bars[i] = new AltableBarrierBase("BAR"+i);
			chans[i] = Channel.createOne2One();
		}
		for (int i = 0; i < nums; i++) {
			AltableBarrierBase ab1 = bars[i];
			AltableBarrierBase ab2 = bars[(i+1)%nums];
			canvasProcs[i] = new ActiveCanvas();
			DisplayList dl = new DisplayList();
			canvasProcs[i].setPaintable(dl);
			canvasProcs[i].setSize(100,100);
			canvasContainer.add(canvasProcs[i]);

			procs[i] = new VisualDemo(pause,
			 ab1, ab2,
			 (AltingChannelInput)chans[i].in(),
			 dl
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
		frame.pack();
		frame.setVisible(true);
		frame.show();

		CSProcess pauseProc = SampleProcesses.timProc(5000, pause);	
		(new Parallel(new CSProcess[] {
			new Parallel(procs),
			new Parallel(randoms),
			new Parallel(canvasProcs),
			button,
//			canvasContainer,
			pauseProc,
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

	//{{{ public VisualDemo()
	public VisualDemo(AltableBarrierBase high, AltableBarrierBase left,
	 AltableBarrierBase right, Guard mid, DisplayList dl) {
		addBarrier(high, Color.RED);
		addBarrier(left);
		addBarrier(right);

		this.high = new AltableBarrier(high);
		this.left = new AltableBarrier(left);
		this.right= new AltableBarrier(right);
		this.mid = mid;
		this.dl = dl;

		alt = new Alternative(new Guard[] {
			new GuardGroup(new AltableBarrier[] {this.high}),
			mid,
			new GuardGroup(new AltableBarrier[] {
			 this.left, this.right})
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
			if (guard instanceof GuardGroup) {
				AltableBarrier temp;
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
			try {	
			Thread.sleep(500);
			} catch(Exception e) {}
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
