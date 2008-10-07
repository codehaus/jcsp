package org.jcsp.net2.mobile;

import java.io.Serializable;

import org.jcsp.net2.NetChannelLocation;

/**
 * @author Kevin
 */
final class MobileChannelMessage
    implements Serializable
{
    static final int REQUEST = 1;

    static final int CHECK = 2;

    static final int CHECK_RESPONSE = 3;

    int type = -1;

    boolean ready = false;

    NetChannelLocation inputLocation = null;
}
