
  /*************************************************************************
  *                                                                        *
  *  JCSP ("CSP for Java") libraries                                       *
  *  Copyright (C) 1996-2006 Peter Welch and Paul Austin.                  *
  *                                                                        *
  *  This library is free software; you can redistribute it and/or         *
  *  modify it under the terms of the GNU Lesser General Public            *
  *  License as published by the Free Software Foundation; either          *
  *  version 2.1 of the License, or (at your option) any later version.    *
  *                                                                        *
  *  This library is distributed in the hope that it will be useful,       *
  *  but WITHOUT ANY WARRANTY; without even the implied warranty of        *
  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU     *
  *  Lesser General Public License for more details.                       *
  *                                                                        *
  *  You should have received a copy of the GNU Lesser General Public      *
  *  License along with this library; if not, write to the Free Software   *
  *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307,  *
  *  USA.                                                                  *
  *                                                                        *
  *  Author contact: P.H.Welch@ukc.ac.uk                                   *
  *                                                                        *
  *************************************************************************/

package org.jcsp.lang;

//{{{  javadoc
/**
 * This is the <i>front-end</i> for a <i>barrier</i> that can be used as
 * a {@link Guard} in an {@link Alternative}.
 * <p>
 * <H2>Description</H2>
 * An <i>alting</i> barrier is represented by a family of <tt>AltingBarrier</tt>
 * <i>front-ends</i>.  Each <i>process</i> using the barrier must do so via its
 * own <i>front-end</i>.  A new barrier is created by the static {@link #create create}
 * method, which returns an array of <i>front-ends</i>.  If necessary, further
 * <i>front-ends</i> may be made from an existing one (see {@link #expand expand}).
 *
 * @author P.H.Welch
 */
//}}}

public class AltingBarrier extends Guard {

  /** This references the barrier on which this is enrolled. */
  private final AltingBarrierBase base;

  /** Link to the next <i>front-end</i> (used by {'link AltingBarrierBase}). */
  AltingBarrier next = null;

  /** The process offering this barrier (protected by the base monitor). */
  private Alternative alt = null;

  /** Safety check. */
  private Thread myThread = null;

  /** Another safety check. */
  private boolean enrolled = true;

  /** Package-only constructor (used by {@link AltingBarrierBase}). */
  AltingBarrier (AltingBarrierBase base, AltingBarrier next) {
    this.base = base;
    this.next = next;
  }

  /**
   * This must be used to create a new <i>alting</i> barrier.
   *
   * @param n the number of processes to be enrolled of this barrier.
   *
   * @return an array of <i>front-end</i> objects for this barrier -- one for
   *   each process enrolled.  It is the invoker's responsibility to pass
   *   these on to those processes.  Processes must stick with just one of these
   *   -- no swapping!
   * 
   * @throws IllegalArgumentException if <tt>n</tt> <= <tt>0</tt>.
   */
  public static AltingBarrier[] create (int n) {
    if (n <= 0) {
      throw new IllegalArgumentException (
        "\n*** An AltingBarrier must have at least one process enrolled, not " + n
      );
    }
    return new AltingBarrierBase ().expand (n);
  }

  /**
   * This expands the number of processes enrolled in this <i>alting</i> barrier.
   *
   * @param n the number of processes to be added to this barrier.
   *
   * @return an array of new <i>front-end</i> objects for this barrier -- one for
   *   each new process enrolled.  It is the invoker's responsibility to pass
   *   these on to those processes.  The barrier cannot now complete without
   *   their participation.
   * 
   * @throws IllegalArgumentException if <tt>n</tt> <= <tt>0</tt>.
   * 
   * @throws AltingBarrierError if currently resigned.
   */
  public AltingBarrier[] expand (int n) {
    if (n <= 0) {
      throw new IllegalArgumentException (
        "\n*** Expanding an AltingBarrier must be by at least one, not " + n
      );
    }
    synchronized (base) {
      if (!enrolled) {
        throw new AltingBarrierError (
	  "\n*** AltingBarrier expand whilst resigned."
	);
      }
      return base.expand (n);
    }
  }

  /**
   * This contracts the number of processes enrolled in this <i>alting</i> barrier.
   *
   * @param ab the <i>front-ends</i> being discarded from this barrier.
   *   This array must be unaltered from one previously delivered by
   *   an {@link #expand expand}.
   * 
   * @throws IllegalArgumentException if <tt>ab</tt> is <tt>null</tt> or zero length.
   * 
   * @throws AltingBarrierError if currently resigned or the given array is <i>not</i>
   *   one previously delivered by an {@link #expand expand}.
   */
  public void contract (AltingBarrier[] ab) {
    if (ab == null) {
      throw new IllegalArgumentException (
        "\n*** AltingBarrier contract given a null array."
      );
    }
    if (ab.length == 0) {
      throw new IllegalArgumentException (
        "\n*** AltingBarrier contract given an empty array."
      );
    }
    synchronized (base) {
      if (!enrolled) {
        throw new AltingBarrierError (
	  "\n*** AltingBarrier contract whilst resigned."
	);
      }
      base.contract (ab);
    }
  }

  boolean enable (Alternative a) {            // package-only visible
    synchronized (base) {
      if (myThread == null) {
        myThread = Thread.currentThread ();
      } 
      else if (myThread != Thread.currentThread ()) {
        throw new AltingBarrierError (
	  "\n*** AltingBarrier front-end enable by more than one Thread."
	);
      }
      if (!enrolled) {
        throw new AltingBarrierError (
	  "\n*** AltingBarrier front-end enable whilst resigned."
	);
      }
      if (alt != null) {                      // in case the same barrier
        return false;                         // occurs more than once in
      }                                       // the same Alternative.
      if (base.enable ()) {
        a.setBarrierTrigger ();               // let Alternative know we did it
        return true;
      } else {
        alt = a;
        return false;
      }
    }
  }

  boolean disable () {                        // package-only visible
    synchronized (base) {
      if (alt == null) {                      // in case the same barrier
        return false;                         // occurs more than once in
      }                                       // the same Alternative.
      if (base.disable ()) {
        alt.setBarrierTrigger ();             // let Alternative know we did it
        alt = null;
        return true;
      } else {
        alt = null;
        return false;
      }
    }
  }

  /**
   * This is the call-back from a successful 'base.enable'.  If it was us
   * that invoked 'base.enable', our 'alt' is null and we don't need to be
   * scheduled!  If we are resigned, ditto.  Whoever is calling this 'schedule'
   * has the 'base' monitor.
   */
  void schedule () {                          // package-only visible
    if (alt != null) {
      alt.schedule ();
    }
  }

  /**
   * A process may resign only if it is enrolled.
   * A resigned process may not offer to synchronise on this barrier
   * (until a subsequent {@link #enroll enroll}).
   * Other processes can complete the barrier (represented by this front-end)
   * without participation by the resigned process.
   * 
   * @throws AltingBarrierError if currently resigned.
   */
  public void resign () {
    synchronized (base) {
      if (!enrolled) {
        throw new AltingBarrierError (
          "\n*** AltingBarrier resign whilst not enrolled."
        );
      }
      enrolled = false;
      base.resign ();
    }
  }

  /**
   * A process may enroll only if it is resigned.
   * A re-enrolled process may resume offering to synchronise on this barrier
   * (until a subsequent {@link #resign resign}).
   * Other processes cannot complete the barrier (represented by this front-end)
   * without participation by the re-enrolled process.
   * 
   * @throws AltingBarrierError if currently enrolled.
   */
  public void enroll () {
    synchronized (base) {
      if (enrolled) {
        throw new AltingBarrierError (
          "\n*** AltingBarrier enroll whilst not resigned."
        );
      }
      enrolled = true;
      base.enroll ();
    }
  }

  /**
   * A process may hand its barrier front-end over to another process,
   * but the receiving process must invoke this method before using it.
   * Beware that the process that handed it over must no longer use it.
   */
  public void mark () {
    synchronized (base) {
      myThread = Thread.currentThread ();
    }
  }

  /**
   * Syncs with the barrier, without alting.  A process must be enrolled on the barrier
   * before it can sync.
   *
   */
  public void sync() {
    //TODO implement this directly:
    
    Alternative alt = new Alternative(new Guard[] {this} );
    alt.select();
  }
}
