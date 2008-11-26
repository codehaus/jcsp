package org.jcsp.lang;

import java.util.*;

// {{{ The ALT class
public class ALT extends Guard {

	//{{{ constants
	// ALT states
	public static final int ENABLING = 0;
	public static final int WAITING = 1;
	public static final int READY = 2;
	public static final int INACTIVE = 3;

	// enumeration of default traversal methods
	public static final int ARB = 0; // regualar ALT
	public static final int PRI = 1; // PRI ALT
	public static final int FAIR= 2; // FAIR ALT

	//{{{ anonymous inner class for standard ALTs
	public TraversalMethod PRI_STANDARD = new TraversalMethod() {
		public Guard enableALT (ALT alt, Vector enabled) {
			for (int i = 0; i < guards.length; i++) {
				Guard guard = guards[i];
				enabled.add(guard);
				if (guard.enable()) {
					return guard;
				}
			}

			return null;
		}

		public Guard disableALT(Vector enabled) {
			Guard selected = null;
			for (int i = enabled.size()-1; i>=0; i--) {
				Guard guard = (Guard) enabled.remove(i);
				if (guard.disable()) {
					selected = guard;
				}
			}
		}
	};
	//}}}
	//}}}
	//{{{ fields
	private Object altMonitor;
	private ALT parent;
	private Guard[] guards;
	private Vector enabledGuards;
	private TraversalMethod defaultTraversal;

	private boolean hasTimer = false; // any timers?
	private boolean hasAltBar1 = false; // any oracle barriers
	private boolean hasAltBar2 = false; // any dnw3 barriers

	private long timeout_msecs; // the earliest timeout.
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

	// {{{ public final int select()
	public final int select() {
		//{{{ notes on what this method should do FIXME delete/update
		// start enabling sequence
		// if none initially found - wait
		// wait for timeout or event
		// select actual barrier on disabling sequence
		// return index
		/*
  		 * should not return index but should return primative
  		 */
		//}}}
		// {{{ enabling sequence
		enable(this);
		//}}}
		//{{{ waiting procedure
		if (state == ENABLE) {
			// if still enabling wait to be woken up again
			// this will mean that at least one guard is now
			// ready, disabling sequence will determine which
			// one
			
			state = WAITING;
			while (state == WAITING) {
				if (hasTimeout) {
					long delay = timeout_msecs - System.currentTimeMillis();
					if (delay > Spurious.earlyTimeout) {
						getMonitor().wait(delay);
					}
				} else {
					getMonitor().wait();
				}

				// FIXME sort out any possible logging here
			}
		}
		//}}}
		//{{{ selecting
		disable();
		return selected;
		//}}}
	}
	//}}}
	

	//{{{ public Object getMonitor()
	/*
 	 * get the monitor that the entire ALT tree works from
 	 */
	public Object getMonitor() {
		if (parent != null) {
			return parent.getMonitor();
		}
		return altMonitor;
	}
	//}}}

	//{{{ boolean enable (Alternative alt)
	/*
	 * enable all of the guards in this ALT and return true if any
	 * of them are ready.
	 */
	boolean enable(Alternative alt) {return false;}
	//}}}
	//{{{ boolean disable()
	boolean disable() {return false;}
        //}}}
}
// }}}
