
  /*************************************************************************
  *                                                                        *
  *  JCSP ("CSP for Java") libraries                                       *
  *  Copyright (C) 1996-2008 Peter Welch and Paul Austin.                  *
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
  *  Author contact: P.H.Welch@kent.ac.uk                                   *
  *                                                                        *
  *************************************************************************/

package org.jcsp.lang;

class AltingBarrierCoordinate {     // package-only visible class

  /*
   * This records number of processes active in ALT enable/disable sequences
   * involving a barrier.
   * <P>
   * Only one process may be engaged in an enable sequence involving a barrier.
   * <P>
   * Disable sequences, triggered by a successful barrier enable, may happen
   * in parallel.  Disable sequences, triggered by a successful barrier enable,
   * may not happen in parallel with an enable sequence involving a barrier.
   * <P>
   * Disable sequences involving a barrier, triggered by a successful non-barrier
   * enable, may happen in parallel with an enable sequence involving a barrier.
   * Should the enable sequence complete a barrier that is in a disable sequence
   * (which can't yet have been disabled, else it could not have been completed),
   * the completed barrier will be found (when it is disabled) and that disable
   * sequence becomes as though it had been triggered by that successful barrier
   * enable (rather than the non-barrier event).
   */
  private static int active = 0;

  /** Lock object for coordinating enable/disable sequences. */
  private static Object activeLock = new Object ();

  /* Invoked at start of an enable sequence involving a barrier. */
  static void startEnable () {
    synchronized (activeLock) {
      if (active > 0) {
        try {
	  activeLock.wait ();
          while (active > 0) {
            // This may be a spurious wakeup.  More likely, this is a properly
	    // notified wakeup that has been raced to the 'activelock' monitor
	    // by another thread (quite possibly the notifying one) that has
	    // (re-)acquired it and set 'active' greater than zero.  We have
	    // not instrumented the code to tell the difference.  Either way:
            activeLock.wait ();
	  }
	}
        catch (InterruptedException e) {
          throw new ProcessInterruptedException (e.toString ());
        }
      }
      if (active != 0) {
        throw new JCSP_InternalError (
	  "\n*** AltingBarrier enable sequence starting " +
	  "with 'active' count not equal to zero: " + active
	);
      }
      active = 1;
    }
  }

  /* Invoked at finish of an unsuccessful enable sequence involving a barrier. */
  static void finishEnable () {
    synchronized (activeLock) {
      if (active != 1) {
        throw new JCSP_InternalError (
	  "\n*** AltingBarrier enable sequence finished " +
	  "with 'active' count not equal to one: " + active
	);
      }
      active = 0;
      activeLock.notify ();
    }
  }

  /*
   * Invoked by a successful barrier enable.
   *
   * @param n The number of processes being released to start their disable sequences.
   */
  static void startDisable (int n) {
    if (n <= 0) {
      throw new JCSP_InternalError (
        "\n*** attempt to start " + n + " disable sequences!"
      );
    }
    synchronized (activeLock) {               // not necessary ... ?
      if (active != 1) {
        throw new JCSP_InternalError (
	  "\n*** completed AltingBarrier found in ALT sequence " +
	  "with 'active' count not equal to one: " + active
	);
      }
      active = n;
    }
  }

  /* Invoked at finish of a disable sequence selecting a barrier. */
  static void finishDisable () {
    synchronized (activeLock) {
      if (active < 1) {
        throw new JCSP_InternalError (
	  "\n*** AltingBarrier disable sequence finished " +
	  "with 'active' count less than one: " + active
	);
      }
      active--;
      if (active == 0) {
        activeLock.notify ();
      }
    }
  }

}
