
import java.util.Random;

import org.jcsp.lang.ProcessManager;
import org.jcsp.net2.NetBarrier;
import org.jcsp.net2.NetBarrierEnd;
import org.jcsp.net2.Node;
import org.jcsp.net2.tcpip.TCPIPNodeAddress;

/**
 * This program creates a local NetBarrier with two processes syncing upon it
 * 
 * @author Kevin
 */
public class LocalBarrier
{
    public static void main(String[] args)
    {
        // Initialise the Node
        Node.getInstance().init(new TCPIPNodeAddress());

        // Create a new NetBarrier server end, index 100. 1 locally enrolled process, 1 remote
        NetBarrier server = NetBarrierEnd.numberedNetBarrier(100, 1, 1);

        // Now start up the Server process
        new ProcessManager(new NetBarrierTestProcess(server, 1)).start();

        // Now enroll with the NetBarrier
        NetBarrier client = NetBarrierEnd.netBarrier(Node.getInstance().getNodeID(), 100, 1);

        // Randomly sync with the Barrier
        Random rand = new Random();

        while (true)
        {
            try
            {
                // Wait randomly upto 5 seconds
                Thread.sleep(Math.abs(rand.nextLong() % 5000));
                System.out.println("Syncing client end...");
                client.sync();
                System.out.println("Client end synced...");
            }
            catch (InterruptedException ie)
            {
                // Do nothing. Shouldn't happen
            }
        }
    }
}
