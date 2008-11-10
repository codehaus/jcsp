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

package org.jcsp.net;
import java.io.Serializable;


/**
 * A channel for network output (TX).  This is a "Any2Net" channel,
 * which can be safely used by multiple writers.
 * <p>
 * Note that this is merely a thread-safe wrapper around
 * {@link One2NetChannel}
 *
 * @see One2NetChannel
 * @author Quickstone Technologies Limited
 */
class Any2NetChannel implements NetSharedChannelOutput, Serializable
{
   /***********Constructors******************************************************/
   
   /**
    * Equivalent to the <code>(NetChannelLocation)</code> constructor of
    * <code>One2NetChannel</code>.
    *
    * @see One2NetChannel
    */
   public Any2NetChannel(NetChannelLocation channelLocation)
   {
      impl = new One2NetChannel(channelLocation);
   }
   
   /**
    * Equivalent to the <code>(NetChannelLocation, boolean)</code> constructor
    * of <code>One2NetChannel</code>.
    *
    * @see One2NetChannel
    */
   public Any2NetChannel(NetChannelLocation channelLocation, boolean acknowledged)
   {
      impl = new One2NetChannel(channelLocation, acknowledged);
   }
   
   /**
    * Equivalent to the <code>(NetChannelLocation, Profile)</code> constructor
    * of <code>One2NetChannel</code>.
    *
    * @see One2NetChannel
    */
   public Any2NetChannel(NetChannelLocation channelLocation, Profile linkProfile)
   {
      impl = new One2NetChannel(channelLocation, linkProfile);
   }
   
   /**
    * Equivalent to the <code>(NetChannelLocation, boolean, Profile)</code>
    * constructor of <code>One2NetChannel</code>.
    *
    * @see One2NetChannel
    */
   public Any2NetChannel(NetChannelLocation channelLocation, boolean acknowledged, Profile linkProfile)
   {
      impl = new One2NetChannel(channelLocation, acknowledged, linkProfile);
   }
   
   /***********Private fields****************************************************/
   /**
    * Implementation
    */
   private One2NetChannel impl;
   
   /***********Public Methods****************************************************/
   
   /**
    * Output data to this channel.  The data must be Serializable.
    * <p>
    * Note that this is merely a thread-safe wrapper around
    * {@link One2NetChannel#write(Object)} - see that method for full
    * documentation.
    *
    * @param data The data to send over the channel. The object should implement the <CODE>Serializable</CODE> interface in order to be sent over a network.
    */
   public void write(Object data)
   {
      synchronized (impl)
      {
         impl.write(data);
      }
   }
   
   /**
    * Public accessor for obtaining the location of the read
    * end of this channel.
    *
    * @return a <code>NetChannelLocation</code> object containing
    *          information needed to connect to
    */
   public NetChannelLocation getChannelLocation()
   {
      return impl.getChannelLocation();
   }
   
   /**
    *  Requests that the channel recreates itself and reconnects to the
    *  other end of the channel.
    */
   public void recreate()
   {
      synchronized (impl)
      {
         impl.recreate();
      }
   }
   
   /**
    * Requests that the channel recreates itself and reconnects
    * to the other end of the channel. A new reader location
    * must be supplied.
    *
    * @param loc the new location of the reader.
    */
   public void recreate(NetChannelLocation loc)
   {
      synchronized (impl)
      {
         impl.recreate(loc);
      }
   }
   
   /**
    * Destroys the write end of a channel and frees any resources
    * used within the JCSP networking infrastructure.
    *
    */
   public void destroyWriter()
   {
      synchronized (impl)
      {
         impl.destroyWriter();
      }
   }
   
   public Class getFactoryClass()
   {
      return StandardNetChannelEndFactory.class;
   }
   
   /**
    * Currently, network channels are unpoisonable so this method has no effect.
    */
   public void poison(int strength) {   
   }   
}