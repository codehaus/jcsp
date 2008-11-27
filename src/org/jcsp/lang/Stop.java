
  /*************************************************************************
  *                                                                        *
  *  JCSP ("CSP for Java") libraries                                       *
  *  Copyright (C) 1996-2001 Peter Welch and Paul Austin.                  *
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

//{{{  javadoc
/**
 * This is a process that starts, engages in no events, performs no
 * computation but refuses to terminate.
 * <H2>Description</H2>
 * <TT>Stop</TT> is a process that starts, engages in no events, performs no
 * computation but refuses to terminate.
 * <p>
 * It can also be used as a {@link Guard} in an {@link Alternative} that is
 * never ready.
 * Of course, this is equivalent to it (and its defended process) not being
 * there at all!
 * <P>
 * <I>Note: this process is included for completeness &ndash; it is one of the fundamental
 * primitives of <B>CSP</B>, where it represents a broken process and is a unit of
 * external choice.
 * In JCSP, it is a unit of {@link Alternative}.</I>
 *
 * @see org.jcsp.lang.Skip
 *
 * @author P.D. Austin and P.H. Welch
 *
 */
//}}}

public class Stop extends Guard implements CSProcess {

  /**
   * Enables this guard.
   *
   * @param alt the Alternative doing the enabling.
   */
  boolean enable (Alternative alt) {
    Thread.yield ();
    return false;
  }

  /**
   * Disables this guard.
   */
  boolean disable () {
    return false;
  }

  /**
   * This process starts, engages in no events, performs no computation
   * and refuses to terminate.
   * <p>
   */
  public void run () {
    Object lock = new Object ();
    synchronized (lock) {
      try {
        lock.wait ();
	while (true) {
	  if (Spurious.logging) {
	    SpuriousLog.record (SpuriousLog.StopRun);
	  }
	  lock.wait ();
        }	  

      }
      catch (InterruptedException e) {
        throw new ProcessInterruptedException ("*** Thrown from Stop.run ()\n"
                                             + e.toString ());
      }
    }
  }

}
