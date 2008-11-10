
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

//{{{  javadoc
/**
 * This holds the static flag (indicating whether spurious wakeups should be logged)
 * and early timeout allowance (for {@link Alternative}s with {@link CSTimer} guards).
 * 
 * <H2>Description</H2>
 * These fields are held in this separate class to minimise class loading when
 * spurious wakeups are not logged - the default condition.
 *
 * @see org.jcsp.lang.SpuriousLog
 *
 * @author P.H. Welch
 */
//}}}

class Spurious {     // package-only visible

  /**
   * If logging is required, this flag should be set <i>before</i> any concurrency
   * is started.  It should only be set <i>once</i> using {@link SpuriousLog#start()}.
   * There is no concurrency protection!
   */
  static public boolean logging = false;

  /**
   * This is the allowed early timeout (in msecs).  Some JVMs timeout on calls
   * of <tt>wait (timeout)</tt> early - this specifies how early JCSP will tolerate.
   * <p>
   * We need this to distinguish between a <i>JVM-early</i> timeout (that should
   * be accepted) and a <i>spurious wakeup</i> (that should not).  The value to
   * which this field should be set is machine dependant.  For JVMs that do not
   * return early timeouts, it should be set to zero.  For many, it should be
   * left at the default value (4).  If {@link Spurious#logging} is enabled,
   * counts of spurious wakeups versus accepted early timeouts on <tt>select</tt>
   * operations on {@link Alternative}s can be obtained; this field should be
   * set to minimise the former.
   * <p>
   * This field should be set <i>before</i> any concurrency is started.
   * It should only be set <i>once</i> using {@link SpuriousLog#setEarlyTimeout(long)}.
   * There is no concurrency protection!
   */
  static public long earlyTimeout = 9;

}
