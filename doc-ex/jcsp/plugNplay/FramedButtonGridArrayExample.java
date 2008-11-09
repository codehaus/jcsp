import org.jcsp.lang.*;
import org.jcsp.util.*;
import org.jcsp.plugNplay.*;


public class FramedButtonGridArrayExample {

  public static void main (String argv[]) {

    // the allEvents channel is wired up to all buttons & reports all button presses ...

    final Any2OneChannel allEvents =
      Channel.any2one (new OverWriteOldestBuffer (10));
  
    // labels for the array of buttons

    final String[] arrayLabel = {"JCSP", "Rocket Science", "CSP", "Goodbye World"};

    final int nButtons = arrayLabel.length;

    // row or column array?

    final boolean horizontal = false;
  
    // initial pixel sizes for the frame for the button array
    
    final int arrayPixDown = 20 + (horizontal ? 120 : nButtons*120);
    final int arrayPixAcross = horizontal ? nButtons*120 : 120;
  
    // make the array of buttons (each one separately configured) ...

    final Any2OneChannel[] arrayEvent = new Any2OneChannel[nButtons];
    
    for (int i = 0; i < nButtons; i++) {
      arrayEvent[i] = allEvents;
    }

    final One2OneChannel[] arrayConfigure = Channel.one2oneArray (nButtons);

    final FramedButtonArray array =
      new FramedButtonArray (
        "FramedButtonArray Demo", nButtons,
	arrayPixDown, arrayPixAcross, horizontal,
	Channel.getInputArray (arrayConfigure), Channel.getOutputArray (arrayEvent)
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
      gridConfigure[i] = Channel.one2oneArray (nAcross);
    }

    final ChannelInput[][] gridConfigureIn = new ChannelInput[nDown][nAcross];
    final ChannelOutput[][] gridEventOut = new ChannelOutput[nDown][nAcross];
    
    for (int i = 0; i < nDown; i++) {
      gridConfigureIn[i] = Channel.getInputArray (gridConfigure[i]);
      gridEventOut[i] = Channel.getOutputArray (gridEvent[i]);
    }

    final FramedButtonGrid grid =
      new FramedButtonGrid (
        "FramedButtonGrid Demo", nDown, nAcross,
        gridPixDown, gridPixAcross, gridConfigureIn, gridEventOut
      );

    // testrig ...

    new Parallel (
    
      new CSProcess[] {
      
        array,

        grid,
        
        new CSProcess () {
        
          public void run () {
    
            for (int i = 0; i < nButtons; i++) {
              arrayConfigure[i].out ().write (arrayLabel[i]);
            }
    
            for (int i = 0; i < nDown; i++) {
              for (int j = 0; j < nAcross; j++) {
                gridConfigure[i][j].out ().write (gridLabel[i][j]);
              }
            }
            
            boolean running = true;
            while (running) {
              final String s = (String) allEvents.in ().read ();
              System.out.println ("Button `" + s + "' pressed ...");
              running = (
                (s != arrayLabel[nButtons - 1]) &&
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
