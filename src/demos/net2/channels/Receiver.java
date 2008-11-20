
import org.jcsp.net2.NetChannel;
import org.jcsp.net2.NetChannelInput;
import org.jcsp.net2.Node;
import org.jcsp.net2.tcpip.TCPIPNodeAddress;

/**
 * This is the receiver process of the Producer-Consumer program. Run this program first
 * 
 * @author Kevin Chalmers
 */
public class Receiver
{
    public static void main(String[] args)
    {
        // Initialise the Node. We are listening on port 4000
        Node.getInstance().init(new TCPIPNodeAddress(4000));

        // Now create a channel numbered 100
        NetChannelInput in = NetChannel.numberedNet2One(100);

        // Loop forever, printing our input
        while (true)
        {
            System.out.println(in.read());
        }
    }
}
