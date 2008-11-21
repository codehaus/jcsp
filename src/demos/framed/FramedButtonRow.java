import java.awt.*;
import jcsp.lang.*;
import jcsp.awt.*;

public class FramedButtonRow implements CSProcess {

  private final ActiveClosingFrame activeClosingFrame;
  
  private final ActiveButton[] button;

  public FramedButtonRow (String title, int nButtons, int pixDown, int pixAcross,
                          ChannelInput[] configure, ChannelOutput[] event) {

    // check everything ...

    if (title == null) {
      throw new IllegalArgumentException ("From FramedButtonRow (title == null) ...");
    }

    if ((nButtons < 1) || (pixDown < 100) || (pixAcross < 100)) {
      throw new IllegalArgumentException ("From FramedButtonRow (nButtons < 1) ...");
    }

    if ((configure == null) || (event == null)) {
      throw new IllegalArgumentException ("From FramedButtonRow (configure == null) ...");
    }

    if ((nButtons != configure.length) || (configure.length != event.length)) {
      throw new IllegalArgumentException ("From FramedButtonRow (nDown != configure.length) ...");
    }

    for (int i = 0; i < configure.length; i++) {
      if ((configure[i] == null) || (event[i] == null)) {
        throw new IllegalArgumentException ("From FramedButtonRow (configure[i] == null) ...");
      }
    }

    // OK - now build ...

    activeClosingFrame = new ActiveClosingFrame (title);
    final ActiveFrame activeFrame = activeClosingFrame.getActiveFrame ();

    button = new ActiveButton[nButtons];
    for (int i = 0; i < nButtons; i++) {
        button[i] = new ActiveButton (configure[i], event[i]);
    }

    activeFrame.setSize (pixAcross, pixDown);
    activeFrame.setLayout (new GridLayout (1, nButtons));
    for (int i = 0; i < button.length; i++) {
      activeFrame.add (button[i]);
    }
    activeFrame.setVisible (true);

  }

  public void run () {
    new Parallel (
      new CSProcess[] {
        activeClosingFrame,
        new Parallel (button)
      }
    ).run ();
  }

}
