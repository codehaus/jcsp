package org.jcsp.net2.mobile;

import java.util.Hashtable;

import org.jcsp.net2.JCSPNetworkException;
import org.jcsp.net2.NetChannel;
import org.jcsp.net2.NetChannelInput;
import org.jcsp.net2.NetChannelLocation;
import org.jcsp.net2.NetChannelOutput;
import org.jcsp.net2.NodeID;

/**
 * @author Kevin
 */
final class DynamicClassLoader
    extends ClassLoader
{
    final NodeID originatingNode;

    NetChannelOutput requestClassData;

    NetChannelInput classDataResponse = NetChannel.net2one();

    final Hashtable classes = new Hashtable();

    DynamicClassLoader(NodeID originator, NetChannelLocation requestLocation)
    {
        super(ClassLoader.getSystemClassLoader());
        this.originatingNode = originator;
        this.requestClassData = NetChannel.one2net(requestLocation);
    }

    protected Class findClass(String className)
        throws ClassNotFoundException, JCSPNetworkException
    {
        try
        {
            Class clazz = Class.forName(className, false, ClassLoader.getSystemClassLoader());
            return clazz;
        }
        catch (ClassNotFoundException cnfe)
        {
            try
            {
                byte[] bytes = this.requestClass(className);
                Class toReturn = this.defineClass(className, bytes, 0, bytes.length);
                this.resolveClass(toReturn);
                return toReturn;
            }
            catch (ClassNotFoundException cnf)
            {
                throw cnf;
            }
        }
    }

    synchronized byte[] requestClass(String className)
        throws ClassNotFoundException
    {
        try
        {
            byte[] bytes = (byte[])classes.get(className);
            if (bytes != null)
            {
                return bytes;
            }
            if (this.requestClassData == null)
            {
                throw new ClassNotFoundException(className);
            }

            ClassRequest req = new ClassRequest(this.originatingNode, className,
                    (NetChannelLocation)this.classDataResponse.getLocation());
            this.requestClassData.write(req);
            ClassData data = (ClassData)classDataResponse.read();
            if (data.bytes == null)
            {
                throw new ClassNotFoundException(className);
            }
            this.classes.put(className, data.bytes);
            return data.bytes;
        }
        catch (JCSPNetworkException jne)
        {
            this.classDataResponse.destroy();
            this.classDataResponse = null;
            this.requestClassData.destroy();
            this.requestClassData = null;
            throw new ClassNotFoundException(className);
        }
    }
}
