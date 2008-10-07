package org.jcsp.net2.mobile;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;

/**
 * @author Kevin
 */
final class DynamicObjectInputStream
    extends ObjectInputStream
{
    final DynamicClassLoader dcl;

    DynamicObjectInputStream(InputStream is, DynamicClassLoader loader)
        throws IOException
    {
        super(is);
        this.dcl = loader;
    }

    protected Class resolveClass(ObjectStreamClass desc)
        throws IOException, ClassNotFoundException
    {
        return dcl.loadClass(desc.getName());
    }
}
