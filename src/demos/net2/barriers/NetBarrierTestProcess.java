/**
 * 
 */
import org.jcsp.lang.Barrier;
import org.jcsp.lang.CSProcess;

/**
 * @author Kevin
 */
public class NetBarrierTestProcess
    implements CSProcess
{
    private final Barrier toSync;

    int n;

    public NetBarrierTestProcess(Barrier bar, int procNum)
    {
        this.toSync = bar;
        this.n = procNum;
    }

    public void run()
    {
        while (true)
        {
            System.out.println("Process " + this.n + " syncing");
            this.toSync.sync();
            System.out.println("Process " + this.n + " released");
        }
    }

}
