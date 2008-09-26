// {{{ The ALT class
public class ALT extends Guard {

	//{{{ constants
	public static final int ENABLING = 0;
	public static final int WAITING = 1;
	public static final int READY = 2;
	public static final int INACTIVE = 3;
	//}}}
	//{{{ fields
	private ALT parent;
	private Guard[] guards;
	private Vector enabledGuards;

	private boolean hasTimer = false; // any timers?
	private boolean hasAltBar1 = false; // any oracle barriers
	private boolean hasAltBar2 = false; // any dnw3 barriers
        //}}}

	//{{{ constructors
	public ALT (Guard[] guards) {
		this(null, guards);
	}

	public ALT (ALT parent, Guard[] guards) {
		this.parent = parent;
		this.guards = guards;

		for (int i = 0; i < guards.length; i++) {
			if (guard[i] instanceof CSTimer) {
				hasTimer = true;
			}
			if (guard[i] instanceof MultiwaySynchronisation) {
				hasAltBar1 = true;
			}
			if (guard[i] instanceof AltableBarrier) {
				hasAltBar2 = true;
			}
		}
	}
	//}}}

	public final int select() {
		// start enabling sequence
		// if none initially found - wait
		// wait for timeout or event
		// select actual barrier on disabling sequence
		// return index
		/*
  		 * should not return index but should return primative
  		 */

		enable(this);
		if (state == ENABLE)
	}

	//{{{ enable (Alternative alt)
	/*
	 * enable all of the guards in this ALT and return true if any
	 * of them are ready.
	 */
	boolean enable(Alternative alt) {return false;}
	//}}}
	//{{{ disable()
	boolean disable() {return false;}
        //}}}
}
// }}}
