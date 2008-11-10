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
 * This class provides static factory methods for constructing
 * various different types of int channel objects. There are also methods
 * for constructing arrays of identical int channels.
 *
 * The current implementation constructs "safe" channels which have separate
 * delegate objects for their read and write ends. This stops a
 * <code>ChannelInputInt</code> from being cast into a <code>ChannelOutputInt</code>
 * object. The <code>SafeChannelIntFactory</code> class is used to construct the
 * channels.
 *
 * Non-safe channels can be constructed by using an instance of the
 * <code>StandardChannelIntFactory</code> class. The channels produced by this
 * factory have read and write ends implemented by the same object. This is
 * is more efficient (there are two extra objects and delegate method calls)
 * but could lead to errors if users make incorrect casts.
 *
 * @deprecated To create integer channels, use the methods in the Channel class.
 *
 * @author Quickstone Technologies Limited
 */
public class ChannelInt
{
    /**
     * Private constructor to stop users from instantiating this class.
     */
    private ChannelInt()
    {
        //this class should not be instantiated
    }

    /**
     * The factory to be used by this class. The class should implement
     * ChannelIntFactory, ChannelIntArrayFactory, BufferedChannelIntFactory and BufferedChannelIntArrayFactory.
     */
    private static final StandardChannelIntFactory factory = new StandardChannelIntFactory();


    /* Methods that are the same as the Factory Methods */

    /**
     * Constructs and returns a <code>One2OneChannelInt</code> object.
     *
     * @return the channel object.
     *
     * @see org.jcsp.lang.ChannelIntFactory#createOne2One()
     */
    public static One2OneChannelInt createOne2One()
    {
        return factory.createOne2One();
    }

    /**
     * Constructs and returns an <code>Any2OneChannelInt</code> object.
     *
     * @return the channel object.
     *
     * @see org.jcsp.lang.ChannelIntFactory#createAny2One()
     */
    public static Any2OneChannelInt createAny2One()
    {
        return factory.createAny2One();
    }

    /**
     * Constructs and returns a <code>One2AnyChannelInt</code> object.
     *
     * @return the channel object.
     *
     * @see org.jcsp.lang.ChannelIntFactory#createOne2Any()
     */
    public static One2AnyChannelInt createOne2Any()
    {
        return factory.createOne2Any();
    }

    /**
     * Constructs and returns an <code>Any2AnyChannelInt</code> object.
     *
     * @return the channel object.
     *
     * @see org.jcsp.lang.ChannelIntFactory#createAny2Any()
     */
    public static Any2AnyChannelInt createAny2Any()
    {
        return factory.createAny2Any();
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
    public static One2OneChannelInt[] createOne2One(int n)
    {
        return factory.createOne2One(n);
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
    public static Any2OneChannelInt[] any2oneArray(int n)
    {
        return factory.createAny2One(n);
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
    public static One2AnyChannelInt[] createOne2Any(int n)
    {
        return factory.createOne2Any(n);
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
    public static Any2AnyChannelInt[] createAny2Any(int n)
    {
        return factory.createAny2Any(n);
    }

    /**
     * Constructs and returns a <code>One2OneChannelInt</code> object which
     * uses the specified <code>ChannelDataStoreInt</code> object as a buffer.
     *
     * @param	buffer	the <code>ChannelDataStoreInt</code> to use.
     * @return the buffered channel.
     *
     * @see org.jcsp.lang.BufferedChannelIntFactory#createOne2One(ChannelDataStoreInt)
     * @see org.jcsp.util.ints.ChannelDataStoreInt
     */
    public static One2OneChannelInt createOne2One(ChannelDataStoreInt buffer)
    {
        return factory.createOne2One(buffer);
    }

    /**
     * Constructs and returns a <code>Any2OneChannelInt</code> object which
     * uses the specified <code>ChannelDataStoreInt</code> object as a buffer.
     *
     * @param	buffer	the <code>ChannelDataStoreInt</code> to use.
     * @return the buffered channel.
     *
     * @see org.jcsp.lang.BufferedChannelIntFactory#createAny2One(ChannelDataStoreInt)
     * @see org.jcsp.util.ints.ChannelDataStoreInt
     */
    public static Any2OneChannelInt createAny2One(ChannelDataStoreInt buffer)
    {
        return factory.createAny2One(buffer);
    }

    /**
     * Constructs and returns a <code>One2AnyChannelInt</code> object which
     * uses the specified <code>ChannelDataStoreInt</code> object as a buffer.
     *
     * @param	buffer	the <code>ChannelDataStoreInt</code> to use.
     * @return the buffered channel.
     *
     * @see org.jcsp.lang.BufferedChannelIntFactory#createOne2Any(ChannelDataStoreInt)
     * @see org.jcsp.util.ints.ChannelDataStoreInt
     */
    public static One2AnyChannelInt createOne2Any(ChannelDataStoreInt buffer)
    {
        return factory.createOne2Any(buffer);
    }

    /**
     * Constructs and returns a <code>Any2AnyChannelInt</code> object which
     * uses the specified <code>ChannelDataStoreInt</code> object as a buffer.
     *
     * @param	buffer	the <code>ChannelDataStoreInt</code> to use.
     * @return the buffered channel.
     *
     * @see org.jcsp.lang.BufferedChannelIntFactory#createAny2Any(ChannelDataStoreInt)
     * @see org.jcsp.util.ints.ChannelDataStoreInt
     */
    public static Any2AnyChannelInt createAny2Any(ChannelDataStoreInt buffer)
    {
        return factory.createAny2Any(buffer);
    }

    /**
     * Constructs and returns an array of <code>One2OneChannelInt</code> objects
     * which use the specified <code>ChannelDataStoreInt</code> object as a
     * buffer.
     *
     * @param	buffer	the <code>ChannelDataStoreInt</code> to use.
     * @param	n	    the size of the array of channels.
     * @return the array of buffered channels.
     *
     * @see org.jcsp.lang.BufferedChannelIntArrayFactory#createOne2One(ChannelDataStoreInt, int)
     * @see org.jcsp.util.ints.ChannelDataStoreInt
     */
    public static One2OneChannelInt[] createOne2One(ChannelDataStoreInt buffer, int n)
    {
        return factory.createOne2One(buffer, n);
    }

    /**
     * Constructs and returns an array of <code>Any2OneChannelInt</code> objects
     * which use the specified <code>ChannelDataStoreInt</code> object as a
     * buffer.
     *
     * @param	buffer	the <code>ChannelDataStoreInt</code> to use.
     * @param	n	    the size of the array of channels.
     * @return the array of buffered channels.
     *
     * @see org.jcsp.lang.BufferedChannelIntArrayFactory#createAny2One(ChannelDataStoreInt, int)
     * @see org.jcsp.util.ints.ChannelDataStoreInt
     */
    public static Any2OneChannelInt[] any2oneArray(ChannelDataStoreInt buffer, int n)
    {
        return factory.createAny2One(buffer, n);
    }

    /**
     * Constructs and returns an array of <code>One2AnyChannelInt</code> objects
     * which use the specified <code>ChannelDataStoreInt</code> object as a
     * buffer.
     *
     * @param	buffer	the <code>ChannelDataStoreInt</code> to use.
     * @param	n	    the size of the array of channels.
     * @return the array of buffered channels.
     *
     * @see org.jcsp.lang.BufferedChannelIntArrayFactory#createOne2Any(ChannelDataStoreInt, int)
     * @see org.jcsp.util.ints.ChannelDataStoreInt
     */
    public static One2AnyChannelInt[] createOne2Any(ChannelDataStoreInt buffer, int n)
    {
        return factory.createOne2Any(buffer, n);
    }

    /**
     * Constructs and returns an array of <code>Any2AnyChannelInt</code> objects
     * which use the specified <code>ChannelDataStoreInt</code> object as a
     * buffer.
     *
     * @param	buffer	the <code>ChannelDataStoreInt</code> to use.
     * @param	n	    the size of the array of channels.
     * @return the array of buffered channels.
     *
     * @see org.jcsp.lang.BufferedChannelIntArrayFactory#createAny2Any(ChannelDataStoreInt, int)
     * @see org.jcsp.util.ints.ChannelDataStoreInt
     */
    public static Any2AnyChannelInt[] createAny2Any(ChannelDataStoreInt buffer, int n)
    {
        return factory.createAny2Any(buffer, n);
    }

    /**
     * Constructs and returns an array of input channel ends, each of which can be shared by multiple
     * concurrent readers. The returned array, <code>r</code>, is constructed such that
     * <code>r[i] = c[i].in ()</code> for <code>0 <= i < c.length</code>.
     *
     * @param c the array of channel to obtain input ends from.
     * @return the array of channel input ends.
     */
    public static SharedChannelInputInt[] getInputArray(Any2AnyChannelInt[] c)
    {
        SharedChannelInputInt[] in = new SharedChannelInputInt[c.length];
        for (int i = 0; i < c.length; i++)
            in[i] = c[i].in();
        return in;
    }

    /**
     * Constructs and returns an array of input channel ends, each of which can be used as guards
     * in an <code>Alternative</code>. The returned array, <code>r</code>, is constructed such that
     * <code>r[i] = c[i].in ()</code> for <code>0 <= i < c.length</code>.
     *
     * @param c the array of channel to obtain input ends from.
     * @return the array of channel input ends.
     */
    public static AltingChannelInputInt[] getInputArray(Any2OneChannelInt[] c)
    {
        AltingChannelInputInt[] in = new AltingChannelInputInt[c.length];
        for (int i = 0; i < c.length; i++)
            in[i] = c[i].in();
        return in;
    }

    /**
     * Constructs and returns an array of input channel ends, each of which can be shared by multiple
     * concurrent readers. The returned array, <code>r</code>, is constructed such that
     * <code>r[i] = c[i].in ()</code> for <code>0 <= i < c.length</code>.
     *
     * @param c the array of channel to obtain input ends from.
     * @return the array of channel input ends.
     */
    public static SharedChannelInputInt[] getInputArray(One2AnyChannelInt[] c)
    {
        SharedChannelInputInt[] in = new SharedChannelInputInt[c.length];
        for (int i = 0; i < c.length; i++)
            in[i] = c[i].in();
        return in;
    }

    /**
     * Constructs and returns an array of input channel ends, each of which can be used as guards
     * in an <code>Alternative</code>. The returned array, <code>r</code>, is constructed such that
     * <code>r[i] = c[i].in ()</code> for <code>0 <= i < c.length</code>.
     *
     * @param c the array of channel to obtain input ends from.
     * @return the array of channel input ends.
     */
    public static AltingChannelInputInt[] getInputArray(One2OneChannelInt[] c)
    {
        AltingChannelInputInt[] in = new AltingChannelInputInt[c.length];
        for (int i = 0; i < c.length; i++)
            in[i] = c[i].in();
        return in;
    }

    /**
     * Constructs and returns an array of output channel ends, each of which can be shared by multiple
     * concurrent writers. The returned array, <code>r</code>, is constructed such that
     * <code>r[i] = c[i].out ()</code> for <code>0 <= i < c.length</code>.
     *
     * @param c the array of channel to obtain output ends from.
     * @return the array of output input ends.
     */
    public static SharedChannelOutputInt[] getOutputArray(Any2AnyChannelInt[] c)
    {
        SharedChannelOutputInt[] in = new SharedChannelOutputInt[c.length];
        for (int i = 0; i < c.length; i++)
            in[i] = c[i].out();
        return in;
    }

    /**
     * Constructs and returns an array of output channel ends, each of which can be shared by multiple
     * concurrent writers. The returned array, <code>r</code>, is constructed such that
     * <code>r[i] = c[i].out ()</code> for <code>0 <= i < c.length</code>.
     *
     * @param c the array of channel to obtain output ends from.
     * @return the array of output input ends.
     */
    public static SharedChannelOutputInt[] getOutputArray(Any2OneChannelInt[] c)
    {
        SharedChannelOutputInt[] in = new SharedChannelOutputInt[c.length];
        for (int i = 0; i < c.length; i++)
            in[i] = c[i].out();
        return in;
    }

    /**
     * Constructs and returns an array of output channel ends, each of which can only be used by a
     * single writer. The returned array, <code>r</code>, is constructed such that
     * <code>r[i] = c[i].out ()</code> for <code>0 <= i < c.length</code>.
     *
     * @param c the array of channel to obtain output ends from.
     * @return the array of output input ends.
     */
    public static ChannelOutputInt[] getOutputArray(One2AnyChannelInt[] c)
    {
        ChannelOutputInt[] in = new ChannelOutputInt[c.length];
        for (int i = 0; i < c.length; i++)
            in[i] = c[i].out();
        return in;
    }

    /**
     * Constructs and returns an array of output channel ends, each of which can only be used by a
     * single writer. The returned array, <code>r</code>, is constructed such that
     * <code>r[i] = c[i].out ()</code> for <code>0 <= i < c.length</code>.
     *
     * @param c the array of channel to obtain output ends from.
     * @return the array of output input ends.
     */
    public static ChannelOutputInt[] getOutputArray(One2OneChannelInt[] c)
    {
        ChannelOutputInt[] in = new ChannelOutputInt[c.length];
        for (int i = 0; i < c.length; i++)
            in[i] = c[i].out();
        return in;
    }
}
