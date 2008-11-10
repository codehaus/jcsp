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

package org.jcsp.net.cns;

import org.jcsp.net.*;
import java.util.*;

/**
 * This class implements the <code>NamedChannelEndManager</code> interface;
 * providing methods for both constructing and destroying channels.
 *
 * This implementation uses the standard Channel Name Server. Instances
 * interact with a local <code>CNSService</code> through the use of a
 * <code>CNSUser</code> object. The default constructor assumes the use
 * of the default CNS service and obtains the reference itself. The
 * constructor which takes a <code>CNSUser</code> object allows the use of
 * an alternative CNS service. A <code>CNSUser</code> object can be
 * obtained by calling a <code>CNSService</code> object's
 * <code>getUserObject()</code> method
 * ({@link org.jcsp.net.cns.CNSService#getUserObject()}).
 *
 * For further information see
 * {@link org.jcsp.net.cns.NamedChannelEndFactory}.
 *
 * @see org.jcsp.net.cns.NamedChannelEndFactory
 * @see org.jcsp.net.cns.NamedChannelEndManager
 * @see org.jcsp.net.cns.CNSService
 *
 *
 * @author Quickstone Technologies Limited
 */
public class CNSChannelEndManager implements NamedChannelEndManager
{
   private Hashtable channelInputRegistrations = new Hashtable();
   private Hashtable channelOutputsCreated = new Hashtable();
   private CNSUser cnsUser;
   private NetChannelEndFactory factoryToUse;
   
   /**
    * Constructor for CNSChannelEndManager.
    */
   public CNSChannelEndManager()
   {
      super();
      factoryToUse = StandardNetChannelEndFactory.getDefaultInstance();
      if (CNSService.staticServiceRef != null)
         this.cnsUser = (CNSUser)CNSService.staticServiceRef.getUserObject();
   }
   
   public CNSChannelEndManager(CNSUser cnsUser)
   {
      super();
      factoryToUse = StandardNetChannelEndFactory.getDefaultInstance();
      this.cnsUser = cnsUser;
   }
   
   private void checkCnsService()
   {
      if (this.cnsUser == null)
      {
         if (CNSService.staticServiceRef != null)
            this.cnsUser = (CNSUser)CNSService.staticServiceRef.getUserObject();
         if (this.cnsUser == null)
            throw new NullPointerException("CNS service to use is null.");
      }
   }
   
   /**
    * Constructs a <code>NetAltingChannelInput</code> object and
    * registers its location with the supplied name in the global namespace
    * of a channel naming service.
    *
    * @param name the name against which to register the channel.
    *
    * @return the constructed <code>NetAltingChannelInput</code> object.
    *
    * @see org.jcsp.net.cns.NamedChannelEndFactory#createNet2One(String)
    */
   public NetAltingChannelInput createNet2One(String name)
   {
      return createNet2One(name, null);
   }
   
   /**
    * Constructs a <code>NetAltingChannelInput</code> object and
    * registers its location with the supplied name in specified
    * namespace of a channel naming service.
    *
    * @param name the name against which to register the channel.
    * @param nameAccessLevel the namespace in which to register the name.
    *
    * @return the constructed <code>NetAltingChannelInput</code> object.
    *
    * @see org.jcsp.net.cns.NamedChannelEndFactory#createNet2One(String, NameAccessLevel)
    */
   public NetAltingChannelInput createNet2One(String name, NameAccessLevel nameAccessLevel)
   {
      NetAltingChannelInput chanIn = factoryToUse.createNet2One();
      checkCnsService();
      ChannelNameKey key = (nameAccessLevel != null) 
                         ? cnsUser.register(chanIn, name, nameAccessLevel)
                         : cnsUser.register(chanIn, name);
      
      ChannelRegistration cr = new ChannelRegistration(name, nameAccessLevel, key);
      channelInputRegistrations.put(chanIn, cr);
      return chanIn;
   }
   
   /**
    * Constructs a <code>NetSharedChannelInput</code> object and
    * registers its location with the supplied name in the global namespace
    * of a channel naming service.
    *
    * @param name the name against which to register the channel.
    *
    * @return the constructed <code>NetSharedChannelInput</code> object.
    *
    * @see org.jcsp.net.cns.NamedChannelEndFactory#createNet2Any(String)
    */
   public NetSharedChannelInput createNet2Any(String name)
   {
      return createNet2Any(name, null);
   }
   
   /**
    * Constructs a <code>NetSharedChannelInput</code> object and
    * registers its location with the supplied name in specified
    * namespace of a channel naming service.
    *
    * @param name the name against which to register the channel.
    * @param nameAccessLevel the namespace in which to register the name.
    *
    * @return the constructed <code>NetSharedChannelInput</code> object.
    *
    * @see org.jcsp.net.cns.NamedChannelEndFactory#createNet2Any(String, NameAccessLevel)
    */
   public NetSharedChannelInput createNet2Any(String name, NameAccessLevel nameAccessLevel)
   {
      NetSharedChannelInput chanIn = factoryToUse.createNet2Any();
      checkCnsService();
      ChannelNameKey key = (nameAccessLevel != null) 
                         ? cnsUser.register(chanIn, name, nameAccessLevel) 
                         : cnsUser.register(chanIn, name);
      
      ChannelRegistration cr = new ChannelRegistration(name, nameAccessLevel, key);
      channelInputRegistrations.put(chanIn, cr);
      return chanIn;
   }
   
   /**
    * Constructs a <code>NetChannelOutput</code> object connected
    * to a <code>NetChannelInput</code> located at a location
    * resolved from the specified channel name.
    *
    * @param name the name of the channel from which to resolve the location.
    *
    * @return the constructed <code>NetChannelOutput</code> object.
    *
    * @see org.jcsp.net.cns.NamedChannelEndFactory#createOne2Net(String)
    */
   public NetChannelOutput createOne2Net(String name)
   {
      return createOne2Net(name, null);
   }
   
   /**
    * Constructs a <code>NetChannelOutput</code> object connected
    * to a <code>NetChannelInput</code> located at a location
    * resolved from the specified channel name that exists in the supplied
    * namespace.
    *
    * @param name the name of the channel from which to resolve the location.
    * @param accessLevel the namespace in which the channel name exists.
    *
    * @return the constructed <code>NetChannelOutput</code> object.
    *
    * @see org.jcsp.net.cns.NamedChannelEndFactory#createOne2Net(String, NameAccessLevel)
    */
   public NetChannelOutput createOne2Net(String name, NameAccessLevel accessLevel)
   {
      
      checkCnsService();
      NetChannelOutput chanOut = factoryToUse.createOne2Net(cnsUser.resolve(name, accessLevel));
      
      //never use the value for 2Net channels but it must not be null
      channelOutputsCreated.put(chanOut, this);
      return chanOut;
   }
   
   /**
    * Constructs a <code>NetSharedChannelOutput</code> object connected
    * to a <code>NetChannelInput</code> located at a location
    * resolved from the specified channel name.
    *
    * @param name the name of the channel from which to resolve the location.
    *
    * @return the constructed <code>NetChannelOutput</code> object.
    *
    * @see org.jcsp.net.cns.NamedChannelEndFactory#createAny2Net(String)
    *
    */
   public NetSharedChannelOutput createAny2Net(String name)
   {
      return createAny2Net(name, null);
   }
   
   /**
    * Constructs a <code>NetSharedChannelOutput</code> object connected
    * to a <code>NetChannelInput</code> located at a location
    * resolved from the specified channel name that exists in the supplied
    * namespace.
    *
    * @param name the name of the channel from which to resolve the location.
    * @param accessLevel the namespace in which the channel name exists.
    *
    * @return the constructed <code>NetChannelOutput</code> object.
    *
    * @see org.jcsp.net.cns.NamedChannelEndFactory#createAny2Net(String, NameAccessLevel)
    */
   public NetSharedChannelOutput createAny2Net(String name, NameAccessLevel accessLevel)
   {
      checkCnsService();
      NetSharedChannelOutput chanOut = factoryToUse.createAny2Net(cnsUser.resolve(name, accessLevel));
      channelOutputsCreated.put(chanOut, this);
      return chanOut;
   }
   
   /**
    * Destroys an individual <code>NetChannelInput</code> object
    * that was constructed with this instance. This will deregister
    * the channel name and destroy the channel end.
    *
    * @param chanInEnd  the channel end to destroy.
    * @see org.jcsp.net.cns.NamedChannelEndManager#destroyChannelEnd(NetChannelInput)
    */
   public void destroyChannelEnd(NetChannelInput chanInEnd)
   {
      ChannelRegistration registration = (ChannelRegistration) channelInputRegistrations.remove(chanInEnd);
      if (registration != null)
      {
         //channel must have been in the collection so ok
         //to deregister it from the CNS
         chanInEnd.destroyReader();
         checkCnsService();
         cnsUser.deregisterChannelName(registration.name, registration.accessLevel, registration.key);
      }
      else
      {
         //channel was not created by this factory
         throw new WrongFactoryException("Channel End was not created by this factory");
      }
   }
   
   /**
    * Destroys an individual <code>NetChannelOutput</code> object
    * that was constructed with this instance. This will simply
    * destroy the channel end.
    *
    * @param chanInEnd  the channel end to destroy.
    * @see org.jcsp.net.cns.NamedChannelEndManager#destroyChannelEnd(NetChannelOutput)
    */
   public void destroyChannelEnd(NetChannelOutput chanOutEnd)
   {
      if (channelOutputsCreated.remove(chanOutEnd) != null)
         //channel was in the HashTable
         chanOutEnd.destroyWriter();
      else
         //channel was not created by this factory
         throw new WrongFactoryException("Channel End was not created by this factory");
   }
   
   /**
    * Destroys all channel ends constructed with this instance
    * of the factory.
    * @see o.jcsp.net.cns.NamedChannelEndManager#destroyAllChannelEnds()
    */
   public void destroyAllChannelEnds()
   {
      NetChannelInput[] inputChans = new NetChannelInput[channelInputRegistrations.size()];
      NetChannelOutput[] outputChans = new NetChannelOutput[channelOutputsCreated.size()];
      Enumeration keys = channelInputRegistrations.keys();
      int i=0;
      while (keys.hasMoreElements())
      {
         inputChans[i] = (NetChannelInput) keys.nextElement();
         i++;
      }
      keys = channelOutputsCreated.keys();
      i=0;
      while (keys.hasMoreElements())
      {
         outputChans[i] = (NetChannelOutput)keys.nextElement();
         i++;
      }
      //code for destroying channels
      for (i=0; i<inputChans.length; i++)
         destroyChannelEnd(inputChans[i]);
      for (i=0; i<outputChans.length; i++)
         destroyChannelEnd(outputChans[i]);
   }
   
   private static class ChannelRegistration
   {
      ChannelRegistration(String name, NameAccessLevel accessLevel, ChannelNameKey key)
      {
         this.name = name;
         this.accessLevel = accessLevel;
         this.key = key;
      }
      
      String name;
      NameAccessLevel accessLevel;
      ChannelNameKey key;
   }
}