
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
 * This is thrown for an illegal operation on an {@link AltingBarrier}.
 *
 * <H2>Description</H2>
 * Currently, there are the following causes:
 * <UL>
 *   <LI>
 *     different threads trying to operate on the same front-end;
 *   <LI>
 *     attempt to use as a {@link Guard} whilst resigned;
 *   <LI>
 *     attempt to {@link AltingBarrier#sync sync} whilst resigned;
 *   <LI>
 *     attempt to {@link AltingBarrier#resign resign} whilst resigned;
 *   <LI>
 *     attempt to {@link AltingBarrier#enroll enroll} whilst enrolled;
 *   <LI>
 *     attempt to {@link AltingBarrier#expand expand} whilst resigned;
 *   <LI>
 *     attempt to {@link AltingBarrier#contract contract} whilst resigned;
 *   <LI>
 *     attempt to {@link AltingBarrier#contract contract} with an array
 *     of front-ends not supplied by {@link AltingBarrier#expand expand};
 *   <LI>
 *     attempt to {@link AltingBarrier#mark mark} whilst resigned (caused
 *     by a process transfering a <i>front-end</i> in that state).
 * </UL>
 *
 * @author P.H. Welch
 */
//}}}

public class AltingBarrierError extends Error {

  public AltingBarrierError (String s) {
    super (s);
  }

}
