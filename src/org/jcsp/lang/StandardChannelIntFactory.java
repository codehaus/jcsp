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

import org.jcsp.util.ints.ChannelDataStoreInt;

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
 * <code>RiskyChannelIntFactory</code> to construct the underlying
 * raw channels.</p>
 *
 * @author Quickstone Technologies Limited
 */
public class StandardChannelIntFactory
        implements
        ChannelIntFactory,
        ChannelIntArrayFactory,
        BufferedChannelIntFactory,
        BufferedChannelIntArrayFactory
{
    /**
     * Constructs a new factory.
     */
    public StandardChannelIntFactory()
    {
        super();
    }

    /**
     * Constructs and returns a <code>One2OneChannelInt</code> object.
     *
     * @return the channel object.
     *
     * @see org.jcsp.lang.ChannelIntFactory#createOne2One()
     */
    public One2OneChannelInt createOne2One()
    {
        return new One2OneChannelIntImpl();
    }

    /**
     * Constructs and returns an <code>Any2OneChannelInt</code> object.
     *
     * @return the channel object.
     *
     * @see org.jcsp.lang.ChannelIntFactory#createAny2One()
     */
    public Any2OneChannelInt createAny2One()
    {
        return new Any2OneChannelIntImpl();
    }

    /**
     * Constructs and returns a <code>One2AnyChannelInt</code> object.
     *
     * @return the channel object.
     *
     * @see org.jcsp.lang.ChannelIntFactory#createOne2Any()
     */
    public One2AnyChannelInt createOne2Any()
    {
        return new One2AnyChannelIntImpl();
    }

    /**
     * Constructs and returns an <code>Any2AnyChannelInt</code> object.
     *
     * @return the channel object.
     *
     * @see org.jcsp.lang.ChannelIntFactory#createAny2Any()
     */
    public Any2AnyChannelInt createAny2Any()
    {
        return new Any2AnyChannelIntImpl();
    }

    /**
     * Constructs and returns an array of <code>One2OneChannelInt</code>
     * objects.
     *
     * @param	n	the size of the array of channels.
     * @return the array of channels.
     *
     * @see org.jcsp.lang.ChannelIntArrayFactory#createOne2One(int)
     */
    public One2OneChannelInt[] createOne2One(int n)
    {
        One2OneChannelInt[] toReturn = new One2OneChannelInt[n];
        for (int i = 0; i < n; i++)
            toReturn[i] = createOne2One();
        return toReturn;
    }

    /**
     * Constructs and returns an array of <code>Any2OneChannelInt</code>
     * objects.
     *
     * @param	n	the size of the array of channels.
     * @return the array of channels.
     *
     * @see org.jcsp.lang.ChannelIntArrayFactory#createAny2One(int)
     */
    public Any2OneChannelInt[] createAny2One(int n)
    {
        Any2OneChannelInt[] toReturn = new Any2OneChannelInt[n];
        for (int i = 0; i < n; i++)
            toReturn[i] = createAny2One();
        return toReturn;
    }

    /**
     * Constructs and returns an array of <code>One2AnyChannelInt</code>
     * objects.
     *
     * @param	n	the size of the array of channels.
     * @return the array of channels.
     *
     * @see org.jcsp.lang.ChannelIntArrayFactory#createOne2Any(int)
     */
    public One2AnyChannelInt[] createOne2Any(int n)
    {
        One2AnyChannelInt[] toReturn = new One2AnyChannelInt[n];
        for (int i = 0; i < n; i++)
            toReturn[i] = createOne2Any();
        return toReturn;
    }

    /**
     * Constructs and returns an array of <code>Any2AnyChannelInt</code>
     * objects.
     *
     * @param	n	the size of the array of channels.
     * @return the array of channels.
     *
     * @see org.jcsp.lang.ChannelIntArrayFactory#createAny2Any(int)
     */
    public Any2AnyChannelInt[] createAny2Any(int n)
    {
        Any2AnyChannelInt[] toReturn = new Any2AnyChannelInt[n];
        for (int i = 0; i < n; i++)
            toReturn[i] = createAny2Any();
        return toReturn;
    }

    /**
     * <p>Constructs and returns a <code>One2OneChannelInt</code> object which
     * uses the specified <code>ChannelDataStoreInt</code> object as a buffer.
     * </p>
     * <p>The buffer supplied to this method is cloned before it is inserted into
     * the channel.
     * </p>
     *
     * @param	buffer	the <code>ChannelDataStoreInt</code> to use.
     * @return the buffered channel.
     *
     * @see org.jcsp.lang.BufferedChannelIntFactory#createOne2One(org.jcsp.util.ints.ChannelDataStoreInt)
     * @see org.jcsp.util.ints.ChannelDataStoreInt
     */
    public One2OneChannelInt createOne2One(ChannelDataStoreInt buffer)
    {
        return new BufferedOne2OneChannelIntImpl(buffer);
    }

    /**
     * <p>Constructs and returns a <code>Any2OneChannelInt</code> object which
     * uses the specified <code>ChannelDataStoreInt</code> object as a buffer.
     * </p>
     * <p>The buffer supplied to this method is cloned before it is inserted into
     * the channel.
     * </p>
     *
     * @param	buffer	the <code>ChannelDataStoreInt</code> to use.
     * @return the buffered channel.
     *
     * @see org.jcsp.lang.BufferedChannelIntFactory#createAny2One(org.jcsp.util.ints.ChannelDataStoreInt)
     * @see org.jcsp.util.ints.ChannelDataStoreInt
     */
    public Any2OneChannelInt createAny2One(ChannelDataStoreInt buffer)
    {
        return new BufferedAny2OneChannelIntImpl(buffer);
    }

    /**
     * <p>Constructs and returns a <code>One2AnyChannelInt</code> object which
     * uses the specified <code>ChannelDataStoreInt</code> object as a buffer.
     * </p>
     * <p>The buffer supplied to this method is cloned before it is inserted into
     * the channel.
     * </p>
     *
     * @param	buffer	the <code>ChannelDataStoreInt</code> to use.
     * @return the buffered channel.
     *
     * @see org.jcsp.lang.BufferedChannelIntFactory#createOne2Any(org.jcsp.util.ints.ChannelDataStoreInt)
     * @see org.jcsp.util.ints.ChannelDataStoreInt
     */
    public One2AnyChannelInt createOne2Any(ChannelDataStoreInt buffer)
    {
        return new BufferedOne2AnyChannelIntImpl(buffer);
    }

    /**
     * <p>Constructs and returns a <code>Any2AnyChannelInt</code> object which
     * uses the specified <code>ChannelDataStoreInt</code> object as a buffer.
     * </p>
     * <p>The buffer supplied to this method is cloned before it is inserted into
     * the channel.
     * </p>
     *
     * @param	buffer	the <code>ChannelDataStoreInt</code> to use.
     * @return the buffered channel.
     *
     * @see org.jcsp.lang.BufferedChannelIntFactory#createAny2Any(org.jcsp.util.ints.ChannelDataStoreInt)
     * @see org.jcsp.util.ints.ChannelDataStoreInt
     */
    public Any2AnyChannelInt createAny2Any(ChannelDataStoreInt buffer)
    {
        return new BufferedAny2AnyChannelIntImpl(buffer);
    }

    /**
     * <p>Constructs and returns an array of <code>One2OneChannelInt</code> objects
     * which use the specified <code>ChannelDataStoreInt</code> object as a
     * buffer.
     * </p>
     * <p>The buffer supplied to this method is cloned before it is inserted into
     * the channel. This is why an array of buffers is not required.
     * </p>
     *
     * @param	buffer	the <code>ChannelDataStoreInt</code> to use.
     * @param	n	    the size of the array of channels.
     * @return the array of buffered channels.
     *
     * @see org.jcsp.lang.BufferedChannelIntArrayFactory#createOne2One(org.jcsp.util.ints.ChannelDataStoreInt,int)
     * @see org.jcsp.util.ints.ChannelDataStoreInt
     */
    public One2OneChannelInt[] createOne2One(ChannelDataStoreInt buffer, int n)
    {
        One2OneChannelInt[] toReturn = new One2OneChannelInt[n];
        for (int i = 0; i < n; i++)
            toReturn[i] = createOne2One(buffer);
        return toReturn;
    }

    /**
     * <p>Constructs and returns an array of <code>Any2OneChannelInt</code> objects
     * which use the specified <code>ChannelDataStoreInt</code> object as a
     * buffer.
     * </p>
     * <p>The buffer supplied to this method is cloned before it is inserted into
     * the channel. This is why an array of buffers is not required.
     * </p>
     *
     * @param	buffer	the <code>ChannelDataStoreInt</code> to use.
     * @param	n	    the size of the array of channels.
     * @return the array of buffered channels.
     *
     * @see org.jcsp.lang.BufferedChannelIntArrayFactory#createAny2One(org.jcsp.util.ints.ChannelDataStoreInt,int)
     * @see org.jcsp.util.ints.ChannelDataStoreInt
     */
    public Any2OneChannelInt[] createAny2One(ChannelDataStoreInt buffer, int n)
    {
        Any2OneChannelInt[] toReturn = new Any2OneChannelInt[n];
        for (int i = 0; i < n; i++)
            toReturn[i] = createAny2One(buffer);
        return toReturn;
    }

    /**
     * <p>Constructs and returns an array of <code>One2AnyChannelInt</code> objects
     * which use the specified <code>ChannelDataStoreInt</code> object as a
     * buffer.
     * </p>
     * <p>The buffer supplied to this method is cloned before it is inserted into
     * the channel. This is why an array of buffers is not required.
     * </p>
     *
     * @param	buffer	the <code>ChannelDataStoreInt</code> to use.
     * @param	n	    the size of the array of channels.
     * @return the array of buffered channels.
     *
     * @see org.jcsp.lang.BufferedChannelIntArrayFactory#createOne2Any(org.jcsp.util.ints.ChannelDataStoreInt,int)
     * @see org.jcsp.util.ints.ChannelDataStoreInt
     */
    public One2AnyChannelInt[] createOne2Any(ChannelDataStoreInt buffer, int n)
    {
        One2AnyChannelInt[] toReturn = new One2AnyChannelInt[n];
        for (int i = 0; i < n; i++)
            toReturn[i] = createOne2Any(buffer);
        return toReturn;
    }

    /**
     * <p>Constructs and returns an array of <code>Any2AnyChannelInt</code> objects
     * which use the specified <code>ChannelDataStoreInt</code> object as a
     * buffer.
     * </p>
     * <p>The buffer supplied to this method is cloned before it is inserted into
     * the channel. This is why an array of buffers is not required.
     * </p>
     *
     * @param	buffer	the <code>ChannelDataStoreInt</code> to use.
     * @param	n	    the size of the array of channels.
     * @return the array of buffered channels.
     *
     * @see org.jcsp.lang.BufferedChannelIntArrayFactory#createAny2Any(org.jcsp.util.ints.ChannelDataStoreInt,int)
     * @see org.jcsp.util.ints.ChannelDataStoreInt
     */
    public Any2AnyChannelInt[] createAny2Any(ChannelDataStoreInt buffer, int n)
    {
        Any2AnyChannelInt[] toReturn = new Any2AnyChannelInt[n];
        for (int i = 0; i < n; i++)
            toReturn[i] = createAny2Any(buffer);
        return toReturn;
    }
}
