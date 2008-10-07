package org.jcsp.net2;

import org.jcsp.lang.ChannelOutput;

final class ConnectionData
{
    int vconnn = -1;

    byte state = ConnectionDataState.INACTIVE;

    ChannelOutput toConnection = null;

    ChannelOutput openServer = null;
}
