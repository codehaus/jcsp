//{{{ package and import statements
package org.jcsp.demos.altableBarriers;

import org.jcsp.lang.*;
import org.jcsp.awt.*;
import java.util.*;
import java.awt.*;
//}}}

//{{{ public class VisualDemo implements CSProcess
public class Bloodclot implements CSProcess {

	//{{{ fields
	public static final int EMPTY = 0;
	public static final int FULL = 1;
	public static final int ALMOST = 2;
 
	public static HashMap uniqueBarriers = new HashMap(); // maps barriers to colours

	public AltableBarrier high, left, right;
	public Guard mid;
	public Alternative alt, unpause;
	public HashMap graphicsMap; // maps AltableBarriers to graphics commands
	public DisplayList dl;

	public AltableBarrier[] pass;
	public AltableBarrier tock;
	public int state = EMPTY;

	public GraphicsCommand[] empty, full;
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
		final AltableBarrierBase tick = new AltableBarrierBase("TOCK");
		AltableBarrierBase[] bars = new AltableBarrierBase[nums+2];
		ActiveCanvas[] canvasProcs = new ActiveCanvas[nums];
		CSProcess[] procs = new CSProcess[nums];
		CSProcess[] randoms = new CSProcess[nums];
		final One2OneChannel buttonChan = Channel.createOne2One();
		final ActiveButton button = new ActiveButton(
		 null, buttonChan.out(), "Pause");
		ActiveContainer canvasContainer =
		 new ActiveContainer();
		canvasContainer.setLayout(new GridLayout(HEIGHT,WIDTH));

		for (int i = 0; i < nums+2; i++) {
			bars[i] = new AltableBarrierBase("PASS"+i);
		}
		for (int i = 0; i < nums; i++) {
			AltableBarrierBase ab1 = bars[i];
			AltableBarrierBase ab2 = bars[(i+1)%nums];
			canvasProcs[i] = new ActiveCanvas();
			DisplayList dl = new DisplayList();
			canvasProcs[i].setPaintable(dl);
			canvasProcs[i].setSize(100,100);
			canvasContainer.add(canvasProcs[i]);
			AltableBarrierBase[] myPasses = 
			  new AltableBarrierBase[]{
			  bars[i], bars[i+1], bars[i+2]
			};

			procs[i] = new Bloodclot(myPasses,
			 tick, dl
			);
		}
		frame.setLayout(new BorderLayout());
		frame.add(canvasContainer, BorderLayout.CENTER);
		frame.add(button, BorderLayout.SOUTH);
		frame.pack();
		frame.setVisible(true);
		frame.show();

		AltableBarrierBase[] spawnBars = new AltableBarrierBase[]{
		  bars[0], bars[1]
		};
		CSProcess spawn = new SpawnProcess(spawnBars, tick);
		CSProcess pauseProc = SampleProcesses.timProc(5000, pause);	
		(new Parallel(new CSProcess[] {
			new Parallel(procs),
			new Parallel(canvasProcs),
			spawn,
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

	//{{{ public Bloodclot()
	public Bloodclot(AltableBarrierBase[] passes, AltableBarrierBase tick,
	  DisplayList dl) {
		addBarrier(tick, Color.RED);
		addBarrier(passes[0]);
		addBarrier(passes[1]);
		addBarrier(passes[2]);

		for (int i = 0; i < passes.length; i++) {
			this.pass[i] = new AltableBarrier(passes[i]);
		}
		this.tock = new AltableBarrier(tick);
//		this.high = new AltableBarrier(high);
//		this.left = new AltableBarrier(left);
//		this.right= new AltableBarrier(right);
//		this.mid = mid;
		this.dl = dl;

//		alt = new Alternative(new Guard[] {
//			new GuardGroup(new AltableBarrier[] {this.high}),
//			mid,
//			new GuardGroup(new AltableBarrier[] {
//			 this.left, this.right})

//		});
//		unpause = new Alternative(new Guard[] {
//			new GuardGroup(new AltableBarrier[] {this.high})
//		});

		graphicsMap = new HashMap();
		GraphicsCommand[] commands;
		//{{{ EMPTY
		commands = new GraphicsCommand[] {
			new GraphicsCommand.SetColor(Color.black),
			new GraphicsCommand.DrawRect(0,0,100,100),
			new GraphicsCommand.SetColor(Color.white),
			new GraphicsCommand.FillRect(1,1,99,99)
		};
		empty = commands;
		//}}}
		//{{{ FULL
		commands = new GraphicsCommand[] {
			new GraphicsCommand.SetColor(Color.black),
			new GraphicsCommand.DrawRect(0,0,100,100),
			new GraphicsCommand.SetColor(Color.red),
			new GraphicsCommand.FillRect(1,1,99,99)
		};
		full = commands;
		//}}}
		//{{{ high
		commands = new GraphicsCommand[] {
			new GraphicsCommand.SetColor(Color.black),
			new GraphicsCommand.DrawRect(0,0,100,100),
			new GraphicsCommand.DrawString("",0,50),
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
			new GraphicsCommand.DrawString("",0,50),
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
			new GraphicsCommand.DrawString("",0,50),
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
			if (state == FULL) {
				FULL();
			} else if (state == EMPTY) {
				EMPTY();
			} else {
				ALMOST();
			}
		}
	}
	//}}}

	//{{{ private void FULL()
	private void FULL() {
		GuardGroup gg = new GuardGroup(new AltableBarrier[]{
			pass[1],
			pass[2],
			tock
		});
		Alternative alt = new Alternative(new Guard[]{gg});
		int index = alt.priSelect();
		AltableBarrier selected = gg.lastSynchronised();

		if (selected == pass[1]) {
			state = EMPTY;
		} else if (selected == pass[2]) {
			// do nothing but pick pass[1] afterwards	
		} else { // tock select
			state = FULL;
			draw();
		}

		if (selected != pass[2]) {
			return;
		}

		gg = new GuardGroup(new AltableBarrier[]{pass[1]});
		alt = new Alternative(new GuardGroup[]{gg});
		index = alt.priSelect();
		
		gg.lastSynchronised();
		// lastSynchronised will be pass[1]

		state = EMPTY;
	}
	//}}}
	//{{{ private void EMPTY()
	private void  EMPTY() {
		//can't do pass[i] but can do anything else
		GuardGroup gg = new GuardGroup(new AltableBarrier[] {
			pass[1],
			pass[2],
			tock
		});
		Alternative alt = new Alternative(new Guard[]{gg});
		int index = alt.priSelect();
		AltableBarrier selected = gg.lastSynchronised();
		if (selected == pass[1]) {
			state = ALMOST;
		} else if (selected == pass[2]) {
			state = EMPTY;
		} else { // tock selected
			state = EMPTY;
			draw();
		}
	}
	//}}} 
	//{{{ private void ALMOST
	private void ALMOST() {
		GuardGroup gg = new GuardGroup(new AltableBarrier[] {
			pass[2],
			tock
		});
		Alternative alt = new Alternative(new Guard[]{gg});
		int index = alt.priSelect();
		AltableBarrier selected = gg.lastSynchronised();

		if (selected == pass[2]) {
			state = ALMOST;
		} else { // tock selected
			state = FULL;
			draw();
		}
	}
	//}}}

	//{{{ private void draw()
	public void draw() {
		if (state == FULL) {
			dl.set(full);
		} else {
			dl.set(empty);
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
//{{{ class SpawnProcess extends CSProcess
class SpawnProcess implements CSProcess {
	//{{{ fields
	AltableBarrier[] pass;
	AltableBarrier tock;

	Random r;
	boolean canPass = true;
	//}}}
	//{{{ SpawnProcess
	SpawnProcess(AltableBarrierBase[] passes, AltableBarrierBase tick){
		pass = new AltableBarrier[passes.length];

		this.tock = new AltableBarrier(tick);
		for(int i = 0; i < passes.length; i++) {
			pass[i] = new AltableBarrier(passes[i]);
		}

		r = new Random();
	}
	//}}}

	//{{{ public void run()
	public void run(){
		passNew();
		int count = 0;
		int threshold = r.nextInt(10) + 2;
		GuardGroup gg = new GuardGroup(new AltableBarrier[]{
			pass[1], tock
		});
		Alternative alt = new Alternative(new Guard[]{gg});
		while (true) {
			int index = alt.priSelect();	
			AltableBarrier bar = gg.lastSynchronised();
			if (bar == pass[1]) {
				canPass = true;
			} else {
				// nothing special for tock
			}
			count++;
	
			if (count > threshold && canPass) {
				passNew();
				count = 0;
				threshold = r.nextInt(10) + 2;
			}
		}
	}
	//}}}

	//{{{ private void passNew()
	private void passNew() {
		GuardGroup gg = new GuardGroup(new AltableBarrier[]{pass[0]});
		Alternative alt = new Alternative(new Guard[]{gg});

		gg.lastSynchronised(); // don't need result
		canPass = false;
	}
	//}}}

}
//}}}
