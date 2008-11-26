
import org.jcsp.net2.NetChannel;
import org.jcsp.net2.NetChannelOutput;
import org.jcsp.net2.Node;
import org.jcsp.net2.tcpip.TCPIPNodeAddress;

/**
 * This program is the sending end (i.e. client) of a simple networked Producer-Consumer program. This program shows how
 * to create a numbered channel connection without going through the CNS
 * 
 * @author Kevin Chalmers
 */
public class Sender
{
    public static void main(String[] args)
    {
        // First we need to initialise the Node. Start listening process on port 5000 of this machine
        Node.getInstance().init(new TCPIPNodeAddress(5000));

        // We now need to create a channel to the receiver process. It is listening on port 4000 on this machine, and is
        // using channel 100
        NetChannelOutput out = NetChannel.one2net(new TCPIPNodeAddress(4000), 100);

        // Now send Integers for ever
        int i = 0;

        while (true)
        {
            out.write(new Integer(i));
            i++;
        }
    }
}
