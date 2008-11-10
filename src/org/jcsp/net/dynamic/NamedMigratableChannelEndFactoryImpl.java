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

import org.jcsp.net.*;
import org.jcsp.net.cns.*;

/**
 * Implementation of the factory for creating named migratable networked channel ends.
 *
 * @author Quickstone Technologies Limited
 */
public class NamedMigratableChannelEndFactoryImpl implements NamedMigratableChannelEndFactory
{
   /**
    * CNS service name to use.
    */
   private String cnsServiceName;
   
   /**
    * CNS user reference.
    */
   private CNSUser cnsUser;
   
   /**
    * Factory to create the underlying networked channel ends.
    */
   private NetChannelEndFactory factoryToUse;
   
   /**
    * Constructs a new <code>NamedMigratableChannelEndFactoryImpl</code> for use with the given
    * CNS service name.
    *
    * @param cnsServiceName the name of the CNS service to use.
    */
   public NamedMigratableChannelEndFactoryImpl(String cnsServiceName)
   {
      super();
      factoryToUse = StandardNetChannelEndFactory.getDefaultInstance();
      this.cnsServiceName = cnsServiceName;
      cnsUser = (CNSUser)Node.getInstance().getServiceUserObject(cnsServiceName);
   }
   
   /**
    * Constructs a new <code>NamedMigratableChannelEndFactoryImpl</code> using the default CNS
    * service name.
    */
   public NamedMigratableChannelEndFactoryImpl()
   {
      this(CNSService.CNS_DEFAULT_SERVICE_NAME);
   }
   
   /**
    * @see org.jcsp.net.dynamic.NamedMigratableChannelEndFactory#createNet2One(String)
    */
   public MigratableAltingChannelInput createNet2One(String name)
   {
      return createNet2One(name, null);
   }
   
   /**
    * @see org.jcsp.net.dynamic.NamedMigratableChannelEndFactory#createNet2One(String, NameAccessLevel)
    */
   public MigratableAltingChannelInput createNet2One(String name, NameAccessLevel nameAccessLevel)
   {
      NetAltingChannelInput chanIn = factoryToUse.createNet2One();
      ChannelNameKey key = (nameAccessLevel != null)
                         ? cnsUser.register(chanIn, name, nameAccessLevel)
                         : cnsUser.register(chanIn, name);
      
      InputReconnectionManager mgr = new InputReconnectionManagerCNSImpl(chanIn, name, nameAccessLevel, key, cnsServiceName);
      return new MigratableAltingChannelInputImpl(mgr);
   }
   
   /**
    * @see org.jcsp.net.dynamic.NamedMigratableChannelEndFactory#createOne2Net(String)
    */
   public MigratableChannelOutput createOne2Net(String name)
   {
      return createOne2Net(name);
   }
   
   /**
    * @see org.jcsp.net.dynamic.NamedMigratableChannelEndFactory#createOne2Net(String, NameAccessLevel)
    */
   public MigratableChannelOutput createOne2Net(String name, NameAccessLevel accessLevel)
   {
      NetChannelLocation loc = (accessLevel != null)
                             ? cnsUser.resolve(name)
                             : cnsUser.resolve(name, accessLevel);
      return new MigratableChannelOutputImpl(factoryToUse.createOne2Net(loc));
   }
}