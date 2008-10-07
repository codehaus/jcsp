package org.jcsp.net2.mobile;

import java.io.Serializable;

import org.jcsp.lang.ProcessManager;
import org.jcsp.net2.NetChannelLocation;
import org.jcsp.net2.NodeID;

/**
 * @author Kevin
 */
final class DynamicClassLoaderMessage
    implements Serializable
{
    static
    {
        ClassManager classManager = new ClassManager();
        new ProcessManager(classManager).start();
    }

    final NodeID originatingNode;
    final NetChannelLocation requestLocation;
    final byte[] bytes;

    DynamicClassLoaderMessage(NodeID originator, NetChannelLocation request, byte[] classData)
    {
        this.originatingNode = originator;
        this.requestLocation = request;
        this.bytes = classData;
    }
}
