import org.jcsp.lang.*;

public class ProcessManagerExample1 {

  public static void main (String[] argv) {

    final ProcessManager manager = new ProcessManager (
      new CSProcess () {
        public void run () {
          final CSTimer tim = new CSTimer ();
          long timeout = tim.read ();
          int count = 0;
          while (true) {
            System.out.println (count + " :-) managed process running ...");
            count++;
            timeout += 100;
            tim.after (timeout);   // every 1/10th of a second ...
          }
        }
      }
    );

    CSTimer tim = new CSTimer ();
    long timeout = tim.read ();

    System.out.println ("\n\n\t\t\t\t\t*** start the managed process");
    manager.start ();

    for (int i = 0; i < 10; i++) {
      System.out.println ("\n\n\t\t\t\t\t*** I'm still executing as well");
      timeout += 1000;
      tim.after (timeout);         // every second ...
    }

    System.out.println ("\n\n\t\t\t\t\t*** I'm finishing now!");

  }

}
