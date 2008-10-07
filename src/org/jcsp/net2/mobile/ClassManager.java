package org.jcsp.net2.mobile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;

import org.jcsp.lang.CSProcess;
import org.jcsp.net2.JCSPNetworkException;
import org.jcsp.net2.NetChannel;
import org.jcsp.net2.NetChannelInput;
import org.jcsp.net2.NetChannelOutput;
import org.jcsp.net2.Node;

/**
 * @author Kevin
 */
final class ClassManager
    implements CSProcess
{
    static Hashtable classLoaders = new Hashtable();

    static NetChannelInput in = NetChannel.numberedNet2One(10);

    public void run()
    {
        while (true)
        {
            try
            {
                ClassRequest req = (ClassRequest)in.read();
                if (req.originatingNode.equals(Node.getInstance().getNodeID()))
                {
                    String className = req.className.replace('.', '/') + ".class";
                    InputStream is = ClassLoader.getSystemResourceAsStream(className);
                    try
                    {
                        if (is != null)
                        {
                            int read = 0;
                            byte[] bytes = new byte[is.available()];
                            while (read < bytes.length)
                                read += is.read(bytes, read, bytes.length - read);
                            ClassData resp = new ClassData(req.className, bytes);
                            NetChannelOutput out = NetChannel.one2net(req.returnLocation);
                            out.asyncWrite(resp);
                            out.destroy();
                            out = null;
                        }
                        else
                        {
                            ClassData resp = new ClassData(req.className, null);
                            NetChannelOutput out = NetChannel.one2net(req.returnLocation);
                            out.asyncWrite(resp);
                            out.destroy();
                            out = null;
                        }
                    }
                    catch (IOException ioe)
                    {
                        ClassData resp = new ClassData(req.className, null);
                        NetChannelOutput out = NetChannel.one2net(req.returnLocation);
                        out.asyncWrite(resp);
                        out.destroy();
                    }
                }
                else
                {
                    DynamicClassLoader loader = (DynamicClassLoader)ClassManager.classLoaders.get(req.originatingNode);
                    if (loader == null)
                    {
                        ClassData resp = new ClassData(req.className, null);
                        NetChannelOutput out = NetChannel.one2net(req.returnLocation);
                        out.asyncWrite(resp);
                        out.destroy();
                    }
                    else
                    {
                        try
                        {
                            byte[] bytes = loader.requestClass(req.className);
                            ClassData resp = new ClassData(req.className, bytes);
                            NetChannelOutput out = NetChannel.one2net(req.returnLocation);
                            out.asyncWrite(resp);
                            out.destroy();
                        }
                        catch (ClassNotFoundException cnf)
                        {
                            ClassData resp = new ClassData(req.className, null);
                            NetChannelOutput out = NetChannel.one2net(req.returnLocation);
                            out.asyncWrite(resp);
                            out.destroy();
                        }
                    }
                }
            }
            catch (JCSPNetworkException jne)
            {
                // Do nothing
            }
        }
    }
}
