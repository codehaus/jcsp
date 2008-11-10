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

/**
 * Interface that a class can implement to indicate that instances allow
 * channel names within a naming service to be managed.
 *
 * @see CNSService
 * @see CNSService#getUserObject()
 *
 * @author Quickstone Technologies Limited
 */
public interface CNSUser
{
   /**
    * <p>
    * This method resolves a channel name into a
    * <code>NetChannelLocation</code> object. The name should be
    * assumed to be in the global name space (see
    * {@link NameAccessLevel#GLOBAL_ACCESS_LEVEL}).
    * </p>
    *
    * @param name the name of channel to resolve.
    * @return the location to which the name resolved.
    */
   public NetChannelLocation resolve(String name);
   
   /**
    * <p>
    * This method resolves a channel name into a
    * <code>NetChannelLocation</code> object. The name must
    * exist in the specified name space.
    * </p>
    * <p>
    * The name space is specified by passing in a
    * <code>{@link NameAccessLevel}</code> object. A name space
    * includes itself and any name space higher up in the
    * hierarchy. The global name space is the highest level of
    * name space.
    * </p>
    * @param name the name of channel to resolve.
    * @param accessLevel  the name space in which to resolve
    *                      the channel name.
    * @return the location to which the name resolved.
    */
   public NetChannelLocation resolve(String name, NameAccessLevel accessLevel);
   
   /**
    * <p>
    * This method allows a channel (or any instance of a class implementing
    * <code>{@link Networked}</code>) to be registered with a Naming
    * Service Implementation. The name will be registered in the
    * global name space.
    * </p>
    * @param owner the <code>Networked</code> object whose location
    *               should be registered.
    * @param name the name against which to register the channel.
    * @return the <code>ChannelNameKey</code> needed for managing
    *          the name registration or <code>null</code> if registration
    *          failed.
    */
   public ChannelNameKey register(Networked owner, String name);
   
   /**
    * <p>
    * This method allows a channel (or any instance of a class implementing
    * <code>{@link Networked}</code>) to be registered with a Naming Service
    * Implementation.
    * </p>
    * <p>
    * The name will be registered in the specified name space. This is
    * specified by passing in a <code>{@link NameAccessLevel}</code> object.
    * </p>
    * @param owner the <code>Networked</code> object whose location
    *               should be registered.
    * @param name the name against which to register the channel.
    * @param accessLevel the name space in which to register the channel name.
    * @return the <code>ChannelNameKey</code> needed for managing
    *          the name registration or <code>null</code> if registration
    *          failed.
    *
    * @see NameAccessLevel
    */
   public ChannelNameKey register(Networked owner, String name, NameAccessLevel accessLevel);
   
   /**
    * <p>
    * This method allows a channel (or any instance of a class implementing
    * <code>{@link Networked}</code>) that has previously been registered
    * with the to be reregistered with a Naming Service implementation.
    * The name should have been leased (see <code>
    * {@link #leaseChannelName(String, NameAccessLevel, ChannelNameKey)}
    * </code>). It is necessaray to supply the key that was obtained when
    * the name was leased. If the channel name has not previously been
    * registered, then the key can be specified as <code>null</code>.
    * </p>
    * <p>
    * The name will be registered in the in the global name space.
    * </p>
    * @param owner the <code>Networked</code> object whose location
    *               should be registered.
    * @param name the name against which to register the channel.
    * @param accessLevel the name space in which to register the channel name.
    * @return the <code>ChannelNameKey</code> needed for managing
    *          the name registration or <code>null</code> if registration
    *          failed.
    */
   public ChannelNameKey register(Networked owner, String name, ChannelNameKey key);
   
   /**
    * <p>
    * This method allows a channel (or any instance of a class implementing
    * <code>{@link Networked}</code>) that has previously been registered
    * to be reregistered with the Naming Service implementation. The name
    * should have been leased (see <code>
    * {@link #leaseChannelName(String, NameAccessLevel, ChannelNameKey)}
    * </code>). It is necessaray to supply the key that was obtained when
    * the name was leased. If the channel name has not previously been
    * registered, then the key can be specified as <code>null</code>.
    * </p>
    * <p>
    * The name will be registered in the specified name space. This is
    * specified by passing in a <code>{@link NameAccessLevel}</code> object.
    * </p>
    *
    * @param owner the <code>Networked</code> object whose location
    *               should be registered.
    * @param name the name against which to register the channel.
    * @param accessLevel the name space in which to register the channel name.
    * @param key the <code>ChannelNameKey</code> returned when the
    *             name was leased.
    * @return the <code>ChannelNameKey</code> needed for managing
    *          the name registration or <code>null</code> if registration
    *          failed.
    * @see NameAccessLevel
    */
   public ChannelNameKey register(Networked owner, String name, NameAccessLevel accessLevel, ChannelNameKey key);
   
   /**
    * <p>
    * This method allows a channel's location to be registered against a
    * name in the Naming Service implementation. It differs from the other
    * register methods in that it takes a <code>NetChannelLocation</code>
    * object instead of a <code>Networked</code> object. This can be obtained
    * from a <code>Networked</code> object by invoking its
    * <code>getChannelLocation()</code> method (see
    * <code>{@link Networked#getChannelLocation()}</code>).
    * </p>
    * <p>
    * The name will be registered in the specified name space. This is
    * specified by passing in a <code>{@link NameAccessLevel}</code> object.
    * </p>
    * This method also allows a leased channel name to be registered against
    * an actual location (see <code>
    * {@link #register(Networked, String, NameAccessLevel, ChannelNameKey)}
    * </code>).
    * </p>
    *
    * @param ownerLocation the location of a channel to be registered
    *                       against the name.
    * @param name the name against which to register the channel.
    * @param accessLevel the name space in which to register the channel name.
    * @param key the <code>ChannelNameKey</code> returned when the
    *             name was leased.
    * @return the <code>ChannelNameKey</code> needed for managing
    *          the name registration or <code>null</code> if registration
    *          failed.
    *
    * @see #register(Networked, String, NameAccessLevel, ChannelNameKey)
    * @see NameAccessLevel
    */
   public ChannelNameKey register(NetChannelLocation ownerLocation, String name, NameAccessLevel accessLevel,ChannelNameKey key);
   
   /**
    * <p>
    * Leases the channel name within the specified name space
    * from the Naming Service Implemenation. Once a name has been leased,
    * a channel cannot be registered with that name without supplying
    * the key returned by the call to this method.
    * </p>
    * <p>
    * Depending on the implementation, leases may expire, however, they
    * may be renew by recalling this mehtod. Leases
    * can be given up by deregistering the name. Leases should remain even
    * after the obtaining Node has shut down. This allows a channel to move
    * its registered location to another channel on a different Node. There
    * should be no danger of another party managing to obtain the name as
    * transfering a registered name into a lease should be an atomic action.
    *
    * @param  name  the name to lease.
    * @param  accessLevel the name space in which to lease the name.
    * @param  channelKey   the key to use if the name is currently
    *                       registered or leased.
    *
    * @return a new <code>ChannelNameKey</code> needed for managing the
    *          lease or <code>null</code> if leasing failed.
    *
    * @throws ChannelNameException  if the channel name is invalid.
    * @throws NameAccessLevelException if the specifed name space is
    *                                   invalid.
    *
    */
   public ChannelNameKey leaseChannelName(String name, NameAccessLevel accessLevel, ChannelNameKey channelKey)
   throws ChannelNameException, NameAccessLevelException;
   
   /**
    * <p>
    * This deregisters a registered or leased Channel name from the
    * Naming Service implementation.
    * A boolean is returned to indicate whether deregistration was successful.
    * </p>
    *
    * @param   name    the name of the channel as a String.
    * @param   nameAccessLevel the nameAccessLevel of the channel.
    * @param   channelKey  the ChannelNameKey to use to deregister the
    *                      Channel name.
    *
    * @return a boolean indicating success.
    */
   public boolean deregisterChannelName(String name, NameAccessLevel accessLevel, ChannelNameKey channelKey);
}