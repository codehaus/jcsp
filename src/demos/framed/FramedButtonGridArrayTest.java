import jcsp.lang.*;
import jcsp.util.*;
import jcsp.plugNplay.*;


public class FramedButtonGridArrayTest {

  public static void main (String argv[]) {

    // the allEvents channel is wired up to all buttons & reports all button presses ...

    final Any2OneChannel allEvents =
      Any2OneChannel.create (new OverWriteOldestBuffer (10));
  
    // initial pixel sizes for the row of buttons

    final int rowPixDown = 100;
    final int rowPixAcross = 500;
  
    // labels for the row of buttons

    final String[] rowLabel = {"JCSP", "Rocket Science", "CSP", "Goodbye World"};

    final int nButtons = rowLabel.length;

    // make the row of buttons (each one separately configured) ...

    final Any2OneChannel[] rowEvent = new Any2OneChannel[nButtons];
    
    for (int i = 0; i < nButtons; i++) {
      rowEvent[i] = allEvents;
    }

    final One2OneChannel[] rowConfigure = One2OneChannel.create (nButtons);

    final FramedButtonArray row =
      new FramedButtonArray (
        "FramedButtonArray Demo", nButtons,
	rowPixDown, rowPixAcross, true,
	rowConfigure, rowEvent
      );
  
    // initial pixel sizes for the grid of buttons

    final int gridPixDown = 200;
    final int gridPixAcross = 500;
  
    // labels for the grid of buttons

    final String[][] gridLabel = {
      new String[] {"Java", "occam", "Handel"},
      new String[] {"C", "C++", "C#"},
      new String[] {"Haskell", "Modula", "Shut Down"}
    };

    final int nDown = gridLabel.length;
    final int nAcross = gridLabel[0].length;

    // make the grid of buttons (each one separately configured) ...

    final Any2OneChannel[][] gridEvent = new Any2OneChannel[nDown][nAcross];
    
    for (int i = 0; i < nDown; i++) {
      for (int j = 0; j < nAcross; j++) {
        gridEvent[i][j] = allEvents;
      }
    }

    final One2OneChannel[][] gridConfigure = new One2OneChannel[nDown][nAcross];
    
    for (int i = 0; i < nDown; i++) {
      gridConfigure[i] = One2OneChannel.create (nAcross);
    }

    final FramedButtonGrid grid =
      new FramedButtonGrid (
        "FramedButtonGrid Demo", nDown, nAcross,
        gridPixDown, gridPixAcross, gridConfigure, gridEvent
      );

    // testrig ...

    new Parallel (
    
      new CSProcess[] {
      
        row,

        grid,
        
        new CSProcess () {
        
          public void run () {
    
            for (int i = 0; i < nButtons; i++) {
              rowConfigure[i].write (rowLabel[i]);
            }
    
            for (int i = 0; i < nDown; i++) {
              for (int j = 0; j < nAcross; j++) {
                gridConfigure[i][j].write (gridLabel[i][j]);
              }
            }
            
            boolean running = true;
            while (running) {
              final String s = (String) allEvents.read ();
              System.out.println ("Button `" + s + "' pressed ...");
              running = (
                (s != rowLabel[nButtons - 1]) &&
                (s != gridLabel[nDown - 1][nAcross - 1])
              );
            }
            
            System.exit (0);
            
          }
          
        }
        
      }
    ).run ();

  }

}
