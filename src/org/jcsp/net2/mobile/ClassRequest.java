package org.jcsp.net2.mobile;

import java.io.Serializable;

import org.jcsp.net2.NetChannelLocation;
import org.jcsp.net2.NodeID;

/**
 * @author Kevin
 */
final class ClassRequest
    implements Serializable
{
    final NodeID originatingNode;
    final String className;
    final NetChannelLocation returnLocation;

    ClassRequest(NodeID originator, String name, NetChannelLocation response)
    {
        this.originatingNode = originator;
        this.className = name;
        this.returnLocation = response;
    }
}
