import org.jcsp.lang.*;

public class ProcessManagerExample0 {

  public static void pause (int time) {
    try {Thread.sleep (time);} catch (InterruptedException e) {}
  }

  public static void main (String[] argv) {

    System.out.println ("*** start the managed process");

    new ProcessManager (
      new CSProcess () {
        public void run () {
          while (true) {
            System.out.println (":-) managed process running in the background");
            pause (500);
          }
        }
      }
    ).start ();

    System.out.println ("*** I'm still executing as well");
    System.out.println ("*** I'm going to take 5 ...");
    pause (5000);
    System.out.println ("*** I'll take another 5 ...");
    pause (5000);
    System.out.println ("*** I'll finish now ... so the network should as well.");
  }
}