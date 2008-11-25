import java.awt.*;
import java.awt.event.*;
import org.jcsp.lang.*;
import org.jcsp.util.*;
import org.jcsp.awt.*;

public class ActiveCanvasExample {

  public static void main (String argv[]) {

    final ActiveClosingFrame activeClosingFrame =
      new ActiveClosingFrame ("ActiveCanvas Example");
    final Frame frame = activeClosingFrame.getActiveFrame ();

    final One2OneChannel mouseEvent = Channel.one2one (new OverWriteOldestBuffer (10));

    final DisplayList displayList = new DisplayList ();

    final ActiveCanvas canvas = new ActiveCanvas ();
    canvas.addMouseEventChannel (mouseEvent.out ());
    canvas.setPaintable (displayList);
    canvas.setSize (600, 400);

    frame.add (canvas);
    frame.pack ();
    frame.setVisible (true);

    new Parallel (
      new CSProcess[] {
        activeClosingFrame,
        canvas,
        new CSProcess () {
          public void run () {
            final String clickMessage = "D O U B L E - C L I C K   T H E   M O U S E   T O   E X I T";
            final String clickPlea =    "    P L E A S E   M O V E   T H E   M O U S E   B A C K    ";
            final GraphicsCommand[] mouseEntered =
              {new GraphicsCommand.SetColor (Color.cyan),
               new GraphicsCommand.FillRect (0, 0, 600, 400),
               new GraphicsCommand.SetColor (Color.black),
               new GraphicsCommand.DrawString (clickMessage, 140, 200)};
            final GraphicsCommand[] mouseExited =
              {new GraphicsCommand.SetColor (Color.pink),
               new GraphicsCommand.FillRect (0, 0, 600, 400),
               new GraphicsCommand.SetColor (Color.black),
               new GraphicsCommand.DrawString (clickPlea, 140, 200)};
            final GraphicsCommand mousePressed =
              new GraphicsCommand.DrawString (clickMessage, 160, 220);
            final GraphicsCommand mouseReleased =
              new GraphicsCommand.DrawString (clickMessage, 140, 200);
            displayList.set (mouseExited);
            boolean running = true;
            while (running) {
              final MouseEvent event = (MouseEvent) mouseEvent.in ().read ();
              switch (event.getID ()) {
                case MouseEvent.MOUSE_ENTERED:
                  System.out.println ("MOUSE_ENTERED");
                  displayList.set (mouseEntered);
                break;
                case MouseEvent.MOUSE_EXITED:
                  System.out.println ("MOUSE_EXITED");
                  displayList.set (mouseExited);
                break;
                case MouseEvent.MOUSE_PRESSED:
                  System.out.println ("MOUSE_PRESSED");
                  displayList.change (mousePressed, 3);
                break;
                case MouseEvent.MOUSE_RELEASED:
                  System.out.println ("MOUSE_RELEASED");
                  displayList.change (mouseReleased, 3);
                break;
                case MouseEvent.MOUSE_CLICKED:
                  if (event.getClickCount() > 1) {
                    System.out.println ("MOUSE_DOUBLE_CLICKED ... goodbye!");
                    running = false;
                  }
                break;
              }
            }
            frame.setVisible (false);
            System.exit (0);
          }
        }
      }
    ).run ();
  }

}
