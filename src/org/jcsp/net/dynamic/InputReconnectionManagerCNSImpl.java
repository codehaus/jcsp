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
import java.io.*;
import java.lang.reflect.*;

/**
 * Implementation of the <code>InputReconnectionManager</code> to work with a CNS service for resolution
 * of the channel's current location.
 *
 * @author Quickstone Technologies Limited
 */
class InputReconnectionManagerCNSImpl implements InputReconnectionManager
{
   /**
    * The underlying input channel end.
    */
   private NetAltingChannelInput chanIn;
   
   /**
    * The factory class used to create the channel end.
    */
   Class channelFactoryClass = null;
   
   /**
    * Identification of the CNS service to use.
    */
   private String cnsServiceNameToUse = null;
   
   /**
    * Name registered with the CNS.
    */
   private String cnsRegisteredName = null;
   
   /**
    * <code>NameAccessLevel</code> used to register the name against with the CNS.
    */
   private NameAccessLevel cnsRegisteredNameAccessLevel = null;
   
   /**
    * The name key allocated to the channel.
    */
   private ChannelNameKey key = null;
   
   /**
    * True if the channel end has been prepared for serialization (migration).
    */
   private transient boolean serializable = false;
   
   /**
    * Current location of the channel.
    */
   private transient NetChannelLocation cnsNetChannelLocation = null;
   
   /**
    * Constant for representing a possible channel state.
    */
   private static final int MSG_CHAN_SERIALIZABLE = 1;
   
   /**
    * Constant for representing a possible channel state.
    */
   private static final int MSG_CHAN_NETWORKED = 2;
   
   /**
    * Constant for representing a possible channel state.
    */
   private static final int MSG_TERMINATE = 3;
   
   /**
    * Unique prefix to use when registering channels with the CNS. It is assumed that only the
    * reconnection managers will be using channels with names of this form.
    */
   static final String anonymousCnsNamePrefix = "org.jcsp.net.cns ANON";
   
   /**
    * Constructs a new <code>InputReconnectionManagerCNSImpl</code> for use with the given input
    * channel. The default CNS service name will be used.
    *
    * @param chanIn the input channel end.
    */
   public InputReconnectionManagerCNSImpl(NetAltingChannelInput chanIn)
   {
      this(chanIn, null);
   }
   
   /**
    * Constructs a new <code>InputReconnectionManagerCNSImpl</code> for use with the given input
    * channel and a specific CNS service.
    *
    * @param chanIn the input channel end.
    * @param cnsServiceName name of the CNS service to use.
    */
   public InputReconnectionManagerCNSImpl(NetAltingChannelInput chanIn, String cnsServiceName)
   {
      super();
      this.chanIn = chanIn;
      this.cnsServiceNameToUse = cnsServiceName;
   }
   
   /**
    * Constructs a new <code>InputReconnectionManagerCNSImpl</code> for use with CNS registered
    * channels using <code>NameAccessLevel</code> application isolation. The default CNS service name
    * is used.
    *
    * @param chanIn the input channel end.
    * @param name the CNS registered name of the channel.
    * @param accessLevel the application isolation level.
    * @param key the allocated channel key.
    */
   public InputReconnectionManagerCNSImpl(NetAltingChannelInput chanIn, String name, 
                                          NameAccessLevel accessLevel, ChannelNameKey key)
   {
      this(chanIn, name, accessLevel, key, null);
   }
   
   /**
    * Constructs a new <code>InputReconnectionManagerCNSImpl</code> for use with CNS registered
    * channels.
    *
    * @param chanIn the input channel end.
    * @param name the CNS registered name of the channel.
    * @param accessLevel the application isolation level.
    * @param key the allocated channel key.
    * @param cnsServiceName name of the CNS service to use.
    */
   public InputReconnectionManagerCNSImpl(NetAltingChannelInput chanIn, String name, NameAccessLevel accessLevel, 
                                          ChannelNameKey key, String cnsServiceName)
   {
      this.chanIn = chanIn;
      this.key = key;
      this.cnsRegisteredName = name;
      this.cnsRegisteredNameAccessLevel = accessLevel;
      this.cnsServiceNameToUse = cnsServiceName;
   }
   
   /**
    * Obtain the <code>NetAltingChannelInput</code> object to use.
    *
    * After serialization, the first call to this method will reconstruct
    * the channel and register it with the CNS.
    *
    * If there is a problem while trying to construct a channel,
    * a <code>ChannelEndContructionException</code> will be thrown.
    *
    * @see org.jcsp.net.dynamic.InputReconnectionManager#getInputChannel()
    */
   public NetAltingChannelInput getInputChannel()
   {
      if (chanIn == null)
      {
         try
         {
            Constructor c = channelFactoryClass.getConstructor(new Class[]{});
            Method createMethod = null;
            Method[] createMethods = channelFactoryClass.getMethods();
            for (int j=0; j<createMethods.length; j++)
               if (NetAltingChannelInput.class.isAssignableFrom(createMethods[j].getReturnType()))
                  if (createMethods[j].getParameterTypes().length == 0)
                     createMethod = createMethods[j];
            
            if (createMethod != null)
            {
               Object factoryObject = c.newInstance(new Object[] {});
               chanIn = (NetAltingChannelInput)createMethod.invoke(factoryObject, null);
            }
         }
         catch (Exception e)
         {
            throw new ChannelEndContructionException("Unable to construct underlying channel.", e);
         }
         try
         {
            CNSUser cnsUser = (cnsServiceNameToUse == null)
                            ? null
                            : (CNSUser) Node.getInstance().getServiceUserObject(cnsServiceNameToUse);
            
            //register the new channel with the CNS
            key = (cnsUser == null)
                ? CNS.register(chanIn, cnsRegisteredName, cnsRegisteredNameAccessLevel, key)
                : cnsUser.register(chanIn, cnsRegisteredName, cnsRegisteredNameAccessLevel, key);
            serializable = false;
         }
         catch (Exception e)
         {
            chanIn.destroyReader();
            throw new ChannelEndContructionException("Unable to register channel with CNS. Channel construction aborted.", e);
         }
      }
      serializable = false;
      return chanIn;
   }
   
   /**
    * Returns the current location of the channel. This method can only be called after
    * <code>getInputChannel()</code> has been called to resolve the channel being managed.
    */
   public NetChannelLocation getCurrentLocation()
   {
      if (chanIn == null)
         throw new IllegalStateException("Input channel does not exist. Need to call getInputChannel().");
      
      if (this.cnsRegisteredName == null)
         //the channel is not registered with the CNS
         //return the actual underlying channel location
         //don't want the users to then register the channel with
         //the CNS
         return new NotNameableNetChannelLocation(chanIn.getChannelLocation());
      else if (cnsNetChannelLocation == null)
      {
         //channel is registered with the CNS
         if (cnsRegisteredNameAccessLevel == null)
            cnsNetChannelLocation = 
                    cnsServiceNameToUse == null
                    ? CNS.resolve(cnsRegisteredName)
                    : ((CNSUser)Node.getInstance().getServiceUserObject(cnsServiceNameToUse)).resolve(cnsRegisteredName);
         else
         {
            cnsNetChannelLocation = 
                    cnsServiceNameToUse == null
                    ? CNS.resolve(cnsRegisteredName, cnsRegisteredNameAccessLevel)
                    : ((CNSUser)Node.getInstance().getServiceUserObject(cnsServiceNameToUse))
                        .resolve(cnsRegisteredName, cnsRegisteredNameAccessLevel);
         }
      }
      return cnsNetChannelLocation;
   }
   
   /**
    * This method must be called before an instance of this class
    * is serializable. If an instance is deserialized, it can be
    * reserialized without calling this method if
    * <code>getInputChannel()</code> has not been called.
    *
    * @see org.jcsp.net.dynamic.InputReconnectionManager#prepareToMove()
    */
   public void prepareToMove()
   {
      if(serializable)
         return;
      if (cnsRegisteredName == null)
      {
         //channel is anonymous or name is not known
         //need to register a name
         try
         {
            NetChannelLocation loc = chanIn.getChannelLocation();
            cnsRegisteredName = anonymousCnsNamePrefix + loc.getStringID();
            cnsRegisteredNameAccessLevel = NameAccessLevel.GLOBAL_ACCESS_LEVEL;
            key = cnsServiceNameToUse == null
                ? CNS.leaseChannelName(cnsRegisteredName, cnsRegisteredNameAccessLevel, null)
                : ((CNSUser)Node.getInstance().getServiceUserObject(cnsServiceNameToUse))
                     .leaseChannelName(cnsRegisteredName, cnsRegisteredNameAccessLevel, null);
            serializable = true;
         }
         catch (Exception e)
         {
            cnsRegisteredName = null;
            cnsRegisteredNameAccessLevel = null;
            key = null;
            return;
         }
      }
      else
      {
         //name is registered - lease it!
         try
         {
            key = cnsServiceNameToUse == null
                ? CNS.leaseChannelName(cnsRegisteredName, cnsRegisteredNameAccessLevel, key)
                : ((CNSUser) Node.getInstance().getServiceUserObject(cnsServiceNameToUse))
                     .leaseChannelName(cnsRegisteredName, cnsRegisteredNameAccessLevel, key);
            serializable = true;
         }
         catch (Exception e)
         {
            return;
         }
      }
      if (serializable) 
         chanIn.destroyReader();
   }
   
   /**
    * Serialization method to write this object to a stream.
    *
    * @param out destination stream to serialize to.
    */
   private void writeObject(ObjectOutputStream out) throws IOException
   {
      if(!serializable)
         throw (new NotSerializableException(getClass().getName()));
      if(chanIn != null)
      {
         if (chanIn instanceof Serializable)
         {
            //channel is serializable so serialize it!
            out.writeInt(MSG_CHAN_SERIALIZABLE);
            out.writeObject(chanIn);
         }
         else
         {
            out.writeInt(MSG_CHAN_NETWORKED);
            out.writeObject(chanIn.getFactoryClass());
            out.writeObject(cnsRegisteredName);
            out.writeObject(cnsRegisteredNameAccessLevel);
            out.writeObject(key);
            out.writeObject(cnsServiceNameToUse);
         }
      }
      else
      {
         out.writeInt(MSG_CHAN_NETWORKED);
         out.writeObject(channelFactoryClass);
         out.writeObject(cnsRegisteredName);
         out.writeObject(cnsRegisteredNameAccessLevel);
         out.writeObject(key);
         out.writeObject(cnsServiceNameToUse);
      }
      out.writeInt(MSG_TERMINATE);
   }
   
   /**
    * Serialization method to read this object from a stream.
    *
    * @param in the source stream.
    */
   private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException
   {
      int i = in.readInt();
      if(i < MSG_TERMINATE)
      {
         switch(i)
         {
            case MSG_CHAN_SERIALIZABLE:
               chanIn = (NetAltingChannelInput)in.readObject();
               break;
            case MSG_CHAN_NETWORKED:
               //the channel does not get constructed here
               //this is done in the getInputChannel() method
               channelFactoryClass = (Class)in.readObject();
               cnsRegisteredName = (String)in.readObject();
               cnsRegisteredNameAccessLevel = (NameAccessLevel)in.readObject();
               key = (ChannelNameKey) in.readObject();
               cnsServiceNameToUse = (String) in.readObject();
               //this gets set to false in the getInputChannel() method
               serializable = true;
               break;
         }
         //read the terminate msg
         in.readInt();
      }
   }
}