package org.jcsp.net2;

import java.util.Hashtable;

final class ConnectionManager
{
    private static int index = 50;

    private final Hashtable connections = new Hashtable();

    private static ConnectionManager instance = new ConnectionManager();

    private ConnectionManager()
    {

    }

    static ConnectionManager getInstance()
    {
        return instance;
    }

    synchronized void create(ConnectionData data)
    {
        Integer objIndex = new Integer(index);
        while (this.connections.get(objIndex) != null)
        {
            objIndex = new Integer(++index);
        }

        data.vconnn = index;

        this.connections.put(objIndex, data);

        index++;
    }

    synchronized void create(int idx, ConnectionData data)
        throws IllegalArgumentException
    {
        Integer objIndex = new Integer(idx);
        if (this.connections.get(objIndex) != null)
        {
            throw new IllegalArgumentException("Connection of given number already exists");
        }

        data.vconnn = idx;

        this.connections.put(objIndex, data);

        if (idx == ConnectionManager.index)
        {
            index++;
        }
    }

    ConnectionData getConnection(int idx)
    {
        Integer objIndex = new Integer(idx);
        return (ConnectionData)this.connections.get(objIndex);
    }

    void removeConnection(ConnectionData data)
    {
        Integer objIndex = new Integer(data.vconnn);
        this.connections.remove(objIndex);
    }
}
