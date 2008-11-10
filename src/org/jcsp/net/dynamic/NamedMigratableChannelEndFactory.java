//////////////////////////////////////////////////////////////////////
//                                                                  //
//  JCSP ("CSP for Java") Libraries                                 //
//  Copyright (C) 1996-2008 Peter Welch and Paul Austin.            //
//                2001-2004 Quickstone Technologies Limited.        //
//                                                                  //
//  This library is free software; you can redistribute it and/or   //
//  modify it under the terms of the GNU Lesser General Public      //
//  License as published by the Free Software Foundation; either    //
//  version 2.1 of the License, or (at your option) any later       //
//  version.                                                        //
//                                                                  //
//  This library is distributed in the hope that it will be         //
//  useful, but WITHOUT ANY WARRANTY; without even the implied      //
//  warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR         //
//  PURPOSE. See the GNU Lesser General Public License for more     //
//  details.                                                        //
//                                                                  //
//  You should have received a copy of the GNU Lesser General       //
//  Public License along with this library; if not, write to the    //
//  Free Software Foundation, Inc., 59 Temple Place, Suite 330,     //
//  Boston, MA 02111-1307, USA.                                     //
//                                                                  //
//  Author contact: P.H.Welch@kent.ac.uk                             //
//                                                                  //
//                                                                  //
//////////////////////////////////////////////////////////////////////

package org.jcsp.net.dynamic;

import org.jcsp.net.cns.NameAccessLevel;

/**
 * Factory interface for creating migratable networked channel input ends using a CNS service.
 *
 * @author Quickstone Technologies Limited
 */
public interface NamedMigratableChannelEndFactory
{
   /**
    * Creates a named migratable networked channel input end that can be used as a guard in an <code>Alternative</code>.
    *
    * @param name the name to use.
    * @return the created channel end.
    */
   public MigratableAltingChannelInput createNet2One(String name);
   
   /**
    * Creates a named migratable networked channel input end that can be used as a guard in an <code>Alternative</code>.
    *
    * @param name the name to use.
    * @param nameAccessLevel the namespace to declare the name within.
    * @return the created channel end.
    */
   public MigratableAltingChannelInput createNet2One(String name, NameAccessLevel nameAccessLevel);
   
   /**
    * Creates a networked migratable channel output end connected to the input end created with the given name.
    *
    * @param name the name the input end was created with.
    * @return the created channel end.
    */
   public MigratableChannelOutput createOne2Net(String name);
   
   /**
    * Creates a networked migratable channel output end connected to the input end created with the given name.
    *
    * @param name the name the input end was created with.
    * @return the created channel end.
    */
   public MigratableChannelOutput createOne2Net(String name, NameAccessLevel accessLevel);
}