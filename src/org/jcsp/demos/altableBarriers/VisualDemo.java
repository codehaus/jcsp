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
			new GuardGroup(new AltableBarrier[] {high}),
			mid,
			new GuardGroup(new AltableBarrier[] {left, right})
		});

		graphicsMap = new HashMap();
		GraphicsCommand[] commands;
		//{{{ high
		commands = new GraphicsCommand[] {
			new GraphicsCommand.SetColor(Color.black),
			new GraphicsCommand.DrawRect(0,0,100,100),
			new GraphicsCommand.DrawString(high.toString(),50,0),
			new GraphicsCommand.SetColor(
			 (Color) uniqueBarriers.get(high)),
			new GraphicsCommand.FillRect(0,0,50,100)
		};
		graphicsMap.put(this.high, commands);
		//}}}
		//{{{ mid
		commands = new GraphicsCommand[] {
			new GraphicsCommand.SetColor(Color.black),
			new GraphicsCommand.DrawRect(0,0,100,100),
			new GraphicsCommand.DrawString(high.toString(),50,0),
			new GraphicsCommand.FillRect(40,75,60,100)
		};
		graphicsMap.put(this.mid, commands);
		//}}}
		//{{{ left
		commands = new GraphicsCommand[] {
			new GraphicsCommand.SetColor(Color.black),
			new GraphicsCommand.DrawRect(0,0,100,100),
			new GraphicsCommand.DrawString(high.toString(),50,0),
			new GraphicsCommand.SetColor(
			 (Color) uniqueBarriers.get(left)),
			new GraphicsCommand.FillRect(0,75,40,100)
		};
		graphicsMap.put(this.left, commands);
		//}}}
		//{{{ right
		commands = new GraphicsCommand[] {
			new GraphicsCommand.SetColor(Color.black),
			new GraphicsCommand.DrawRect(0,0,100,100),
			new GraphicsCommand.DrawString(high.toString(),50,0),
			new GraphicsCommand.SetColor(
			 (Color) uniqueBarriers.get(right)),
			new GraphicsCommand.FillRect(60,75,100,100)
		};
		graphicsMap.put(this.right, commands);
		//}}}
	}
	//}}}

	//{{{ public void run()
	public void run() {
		while(true) {
			int index = alt.priSelect();
			Guard guard = alt.guard[index];
			Object selected = guard;
			if (guard instanceof GuardGroup) {
				selected = ((GuardGroup) guard).lastSynchronised();
			}

			GraphicsCommand[] commands =
			 (GraphicsCommand[]) graphicsMap.get(selected);

			
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
