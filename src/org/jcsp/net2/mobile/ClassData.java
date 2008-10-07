package org.jcsp.net2.mobile;

import java.io.Serializable;

/**
 * @author Kevin
 */
final class ClassData
    implements Serializable
{
    final String className;
    final byte[] bytes;

    ClassData(String name, byte[] classBytes)
    {
        this.className = name;
        this.bytes = classBytes;
    }
}
