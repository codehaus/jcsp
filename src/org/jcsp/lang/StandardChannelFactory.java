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

package org.jcsp.lang;

import org.jcsp.util.ChannelDataStore;

/**
 * <p>This class acts as a Factory for creating
 * channels. It can create non-buffered and buffered channels
 * and also arrays of non-buffered and buffered channels.</p>
 *
 * <p>The Channel objects created by this Factory are formed of
 * separate objects for the read and write ends. Therefore the
 * <code>ChannelInput</code> object cannot be cast into the
 * <code>ChannelOutput</code> object and vice-versa.</p>
 *
 * <p>The current implementation uses an instance of the
 * <code>RiskyChannelFactory</code> to construct the underlying
 * raw channels.</p>
 *
 * @author Quickstone Technologies Limited
 */
public class StandardChannelFactory<T>
        implements ChannelFactory<T>, ChannelArrayFactory<T>, BufferedChannelFactory<T>, BufferedChannelArrayFactory<T>
{
    private static StandardChannelFactory defaultInstance = new StandardChannelFactory();

    /**
     * Constructs a new factory.
     */
    public StandardChannelFactory()
    {
        super();
    }

    /**
     * Returns a default instance of a channel factory.
     */
    public static StandardChannelFactory getDefaultInstance()
    {
        return defaultInstance;
    }

    /**
     * Constructs and returns a <code>One2OneChannel</code> object.
     *
     * @return the channel object.
     *
     * @see org.jcsp.lang.ChannelFactory#createOne2One()
     */
    public One2OneChannel<T> createOne2One()
    {
        return new One2OneChannelImpl<T>();
    }

    /**
     * Constructs and returns an <code>Any2OneChannel</code> object.
     *
     * @return the channel object.
     *
     * @see org.jcsp.lang.ChannelFactory#createAny2One()
     */
    public Any2OneChannel<T> createAny2One()
    {
        return new Any2OneChannelImpl<T>();
    }

    /**
     * Constructs and returns a <code>One2AnyChannel</code> object.
     *
     * @return the channel object.
     *
     * @see org.jcsp.lang.ChannelFactory#createOne2Any()
     */
    public One2AnyChannel<T> createOne2Any()
    {
        return new One2AnyChannelImpl<T>();
    }

    /**
     * Constructs and returns an <code>Any2AnyChannel</code> object.
     *
     * @return the channel object.
     *
     * @see org.jcsp.lang.ChannelFactory#createAny2Any()
     */
    public Any2AnyChannel<T> createAny2Any()
    {
        return new Any2AnyChannelImpl<T>();
    }

    /**
     * Constructs and returns an array of <code>One2OneChannel</code>
     * objects.
     *
     * @param	n	the size of the array of channels.
     * @return the array of channels.
     *
     * @see org.jcsp.lang.ChannelArrayFactory#createOne2One(int)
     */
    public One2OneChannel<T>[] createOne2One(int n)
    {
        One2OneChannel<T>[] toReturn = new One2OneChannel[n];
        for (int i = 0; i < n; i++)
            toReturn[i] = createOne2One();
        return toReturn;
    }

    /**
     * Constructs and returns an array of <code>Any2OneChannel</code>
     * objects.
     *
     * @param	n	the size of the array of channels.
     * @return the array of channels.
     *
     * @see org.jcsp.lang.ChannelArrayFactory#createAny2One(int)
     */
    public Any2OneChannel<T>[] createAny2One(int n)
    {
        Any2OneChannel<T>[] toReturn = new Any2OneChannel[n];
        for (int i = 0; i < n; i++)
            toReturn[i] = createAny2One();
        return toReturn;
    }

    /**
     * Constructs and returns an array of <code>One2AnyChannel</code>
     * objects.
     *
     * @param	n	the size of the array of channels.
     * @return the array of channels.
     *
     * @see org.jcsp.lang.ChannelArrayFactory#createOne2Any(int)
     */
    public One2AnyChannel<T>[] createOne2Any(int n)
    {
        One2AnyChannel<T>[] toReturn = (One2AnyChannel<T>[]) new One2AnyChannel[n];
        for (int i = 0; i < n; i++)
            toReturn[i] = createOne2Any();
        return toReturn;
    }

    /**
     * Constructs and returns an array of <code>Any2AnyChannel</code>
     * objects.
     *
     * @param	n	the size of the array of channels.
     * @return the array of channels.
     *
     * @see org.jcsp.lang.ChannelArrayFactory#createAny2Any(int)
     */
    public Any2AnyChannel[] createAny2Any(int n)
    {
        Any2AnyChannel<T>[] toReturn = (Any2AnyChannel<T>[]) new Any2AnyChannel[n];
        for (int i = 0; i < n; i++)
            toReturn[i] = createAny2Any();
        return toReturn;
    }

    /**
     * <p>Constructs and returns a <code>One2OneChannel</code> object which
     * uses the specified <code>ChannelDataStore</code> object as a buffer.
     * </p>
     * <p>The buffer supplied to this method is cloned before it is inserted into
     * the channel.
     * </p>
     *
     * @param	buffer	the <code>ChannelDataStore</code> to use.
     * @return the buffered channel.
     *
     * @see org.jcsp.lang.BufferedChannelFactory#createOne2One(org.jcsp.util.ChannelDataStore)
     * @see org.jcsp.util.ChannelDataStore
     */
    public One2OneChannel<T> createOne2One(ChannelDataStore<T> buffer)
    {
        return new BufferedOne2OneChannel<T>(buffer);
    }

    /**
     * <p>Constructs and returns a <code>Any2OneChannel</code> object which
     * uses the specified <code>ChannelDataStore</code> object as a buffer.
     * </p>
     * <p>The buffer supplied to this method is cloned before it is inserted into
     * the channel.
     * </p>
     *
     * @param	buffer	the <code>ChannelDataStore</code> to use.
     * @return the buffered channel.
     *
     * @see org.jcsp.lang.BufferedChannelFactory#createAny2One(org.jcsp.util.ChannelDataStore)
     * @see org.jcsp.util.ChannelDataStore
     */
    public Any2OneChannel<T> createAny2One(ChannelDataStore<T> buffer)
    {
        return new BufferedAny2OneChannel<T>(buffer);
    }

    /**
     * <p>Constructs and returns a <code>One2AnyChannel</code> object which
     * uses the specified <code>ChannelDataStore</code> object as a buffer.
     * </p>
     * <p>The buffer supplied to this method is cloned before it is inserted into
     * the channel.
     * </p>
     *
     * @param	buffer	the <code>ChannelDataStore</code> to use.
     * @return the buffered channel.
     *
     * @see org.jcsp.lang.BufferedChannelFactory#createOne2Any(org.jcsp.util.ChannelDataStore)
     * @see org.jcsp.util.ChannelDataStore
     */
    public One2AnyChannel<T> createOne2Any(ChannelDataStore<T> buffer)
    {
        return new BufferedOne2AnyChannel<T>(buffer);
    }

    /**
     * <p>Constructs and returns a <code>Any2AnyChannel</code> object which
     * uses the specified <code>ChannelDataStore</code> object as a buffer.
     * </p>
     * <p>The buffer supplied to this method is cloned before it is inserted into
     * the channel.
     * </p>
     *
     * @param	buffer	the <code>ChannelDataStore</code> to use.
     * @return the buffered channel.
     *
     * @see org.jcsp.lang.BufferedChannelFactory#createAny2Any(org.jcsp.util.ChannelDataStore)
     * @see org.jcsp.util.ChannelDataStore
     */
    public Any2AnyChannel<T> createAny2Any(ChannelDataStore<T> buffer)
    {
        return new BufferedAny2AnyChannel<T>(buffer);
    }

    /**
     * <p>Constructs and returns an array of <code>One2OneChannel</code> objects
     * which use the specified <code>ChannelDataStore</code> object as a
     * buffer.
     * </p>
     * <p>The buffer supplied to this method is cloned before it is inserted into
     * the channel. This is why an array of buffers is not required.
     * </p>
     *
     * @param	buffer	the <code>ChannelDataStore</code> to use.
     * @param	n	    the size of the array of channels.
     * @return the array of buffered channels.
     *
     * @see org.jcsp.lang.BufferedChannelArrayFactory#createOne2One(org.jcsp.util.ChannelDataStore,int)
     * @see org.jcsp.util.ChannelDataStore
     */
    public One2OneChannel<T>[] createOne2One(ChannelDataStore<T> buffer, int n)
    {
        One2OneChannel<T>[] toReturn = (One2OneChannel<T>[]) new One2OneChannel[n];
        for (int i = 0; i < n; i++)
            toReturn[i] = createOne2One(buffer);
        return toReturn;
    }

    /**
     * <p>Constructs and returns an array of <code>Any2OneChannel</code> objects
     * which use the specified <code>ChannelDataStore</code> object as a
     * buffer.
     * </p>
     * <p>The buffer supplied to this method is cloned before it is inserted into
     * the channel. This is why an array of buffers is not required.
     * </p>
     *
     * @param	buffer	the <code>ChannelDataStore</code> to use.
     * @param	n	    the size of the array of channels.
     * @return the array of buffered channels.
     *
     * @see org.jcsp.lang.BufferedChannelArrayFactory#createAny2One(org.jcsp.util.ChannelDataStore,int)
     * @see org.jcsp.util.ChannelDataStore
     */
    public Any2OneChannel<T>[] createAny2One(ChannelDataStore<T> buffer, int n)
    {
        Any2OneChannel<T>[] toReturn = (Any2OneChannel<T>[]) new Any2OneChannel[n];
        for (int i = 0; i < n; i++)
            toReturn[i] = createAny2One(buffer);
        return toReturn;
    }

    /**
     * <p>Constructs and returns an array of <code>One2AnyChannel</code> objects
     * which use the specified <code>ChannelDataStore</code> object as a
     * buffer.
     * </p>
     * <p>The buffer supplied to this method is cloned before it is inserted into
     * the channel. This is why an array of buffers is not required.
     * </p>
     *
     * @param	buffer	the <code>ChannelDataStore</code> to use.
     * @param	n	    the size of the array of channels.
     * @return the array of buffered channels.
     *
     * @see org.jcsp.lang.BufferedChannelArrayFactory#createOne2Any(org.jcsp.util.ChannelDataStore,int)
     * @see org.jcsp.util.ChannelDataStore
     */
    public One2AnyChannel<T>[] createOne2Any(ChannelDataStore<T> buffer, int n)
    {
        One2AnyChannel<T>[] toReturn = (One2AnyChannel<T>[]) new One2AnyChannel[n];
        for (int i = 0; i < n; i++)
            toReturn[i] = createOne2Any(buffer);
        return toReturn;
    }

    /**
     * <p>Constructs and returns an array of <code>Any2AnyChannel</code> objects
     * which use the specified <code>ChannelDataStore</code> object as a
     * buffer.
     * </p>
     * <p>The buffer supplied to this method is cloned before it is inserted into
     * the channel. This is why an array of buffers is not required.
     * </p>
     *
     * @param	buffer	the <code>ChannelDataStore</code> to use.
     * @param	n	    the size of the array of channels.
     * @return the array of buffered channels.
     *
     * @see org.jcsp.lang.BufferedChannelArrayFactory#createAny2Any(org.jcsp.util.ChannelDataStore,int)
     * @see org.jcsp.util.ChannelDataStore
     */
    public Any2AnyChannel<T>[] createAny2Any(ChannelDataStore<T> buffer, int n)
    {
        Any2AnyChannel<T>[] toReturn = (Any2AnyChannel<T>[]) new Any2AnyChannel[n];
        for (int i = 0; i < n; i++)
            toReturn[i] = createAny2Any(buffer);
        return toReturn;
    }
}
