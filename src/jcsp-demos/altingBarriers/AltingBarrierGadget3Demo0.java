
import org.jcsp.demos.util.*;
import org.jcsp.lang.*;
import org.jcsp.util.*;
import org.jcsp.plugNplay.*;

import java.awt.Color;

public class AltingBarrierGadget3Demo0 {

  static final int PLUS = 0;
  static final int CROSS = 1;
  static final int CIRCLE = 2;

  public static void main (String[] argv) {

    final int width = Ask.Int ("\nwidth = ", 10, 30);
    final int depth = Ask.Int ("depth = ", 1, 30);

    final int kind =
      Ask.Int ("shape (0 = plus, 1 = cross, 2 = circle): ", PLUS, CIRCLE);
    
    int span = 0;
    switch (kind) {
      case PLUS:
        span = Ask.Int ("length of arms = ", 1, width/2);
      break;
      case CROSS:
        span = Ask.Int ("length of arms = ", 1, width/2);
      break;
      case CIRCLE:
        span = Ask.Int ("radius = ", 1, width/2);
      break;
    }

    final int offInterval =
      Ask.Int ("off interval (millisecs) = ", 100, 10000);
    final int standbyInterval =
      Ask.Int ("standby interval (millisecs) = ", 100, 20000);
    final int playInterval =
      Ask.Int ("play interval (millisecs) = ", 1000, 1000000000);
    final int countInterval =
      Ask.Int ("count interval (millisecs) = ", 10, 10000);
    
    final Color offColour = Color.black, standbyColour = Color.lightGray;
    
    // make the buttons

    final One2OneChannel[][] click = new One2OneChannel[depth][];
    for (int i = 0; i < depth; i++) {
      click[i] = Channel.one2oneArray (width, new OverWriteOldestBuffer (1));
    }
    
    final One2OneChannel[][] configure = new One2OneChannel[depth][];
    for (int i = 0; i < depth; i++) {
      configure[i] = Channel.one2oneArray (width);
    }

    final FramedButtonGrid buttons =
      new FramedButtonGrid (
        "AltingBarier: Gadget 3, Demo 0", depth, width, 20 + (depth*50), width*50,
        Util.get2DInputArray(configure), Util.get2DOutputArray(click)
      );

    // make labels for the buttons (read-only shared by all the gadgets)
    
    final String[] label = new String[playInterval/countInterval];
    for (int i = 0; i < label.length; i++) {
      label[i] = String.valueOf (i);
    }

    // make the gadgets

    final Any2OneChannel[][] connect = new Any2OneChannel [depth][];
    for (int row = 0; row < depth; row++) {
      connect[row] = Channel.any2oneArray (width);
    }

    final AltingBarrierGadget3[][] gadgets = new
      AltingBarrierGadget3[depth][width];

    for (int row = 0; row < depth; row++) {
      for (int col = 0; col < width; col++) {
        gadgets[row][col] =
          new AltingBarrierGadget3 (
            connect[row][col].in(), shape (kind, row, col, span, Util.get2DOutputArray(connect)),
            click[row][col].in(), configure[row][col].out(),
            offColour, standbyColour,
            offInterval, standbyInterval,
	    playInterval, countInterval,
	    label
          );
      }
    }

    // run everything

    new Parallel (
      new CSProcess[] {
        buttons, new Parallel (gadgets)
      }
    ).run ();

  }

  static ChannelOutput[] shape (
    int kind, int row, int col, int span, ChannelOutput[][] connect
  ) {
    switch (kind) {
      case PLUS:
        return shapePlus (row, col, span, connect);
      case CROSS:
        return shapeCross (row, col, span, connect);
      case CIRCLE:
        return shapeCircle (row, col, span, connect);
      default:
        return null;
    }
  }

  static ChannelOutput[] shapePlus (
    int row, int col, int span, ChannelOutput[][] connect
  ) {
    final int n = 4*span;
    final ChannelOutput[] collect = new ChannelOutput[n];
    int index = 0;
    for (int i = 1; i <= span; i++) {
      collect[index] = get (connect, row-i, col);
      index++;
      collect[index] = get (connect, row, col+i);
      index++;
      collect[index] = get (connect, row+i, col);
      index++;
      collect[index] = get (connect, row, col-i);
      index++;
    }
    return collect;
  }

  static ChannelOutput[] shapeCross (
    int row, int col, int span, ChannelOutput[][] connect
  ) {
    final int n = 4*span;
    final ChannelOutput[] collect = new ChannelOutput[n];
    int index = 0;
    for (int i = 1; i <= span; i++) {
      collect[index] = get (connect, row-i, col-i);
      index++;
      collect[index] = get (connect, row-i, col+i);
      index++;
      collect[index] = get (connect, row+i, col-i);
      index++;
      collect[index] = get (connect, row+i, col+i);
      index++;
    }
    return collect;
  }

  static ChannelOutput[] shapeCircle (
    int row, int col, int span, ChannelOutput[][] connect
  ) {
    final int n = (4*span) - 1;
    final ChannelOutput[] collect = new ChannelOutput[n];
    int index = 0;
    for (int i = 0; i < (span - 1); i ++) {
      col++;
      collect[index] = get (connect, row, col);
      index++;
    }
    col++;
    for (int i = 0; i < span; i++) {
      row--;
      collect[index] = get (connect, row, col);
      index++;
    }
    row--;
    for (int i = 0; i < span; i++) {
      col--;
      collect[index] = get (connect, row, col);
      index++;
    }
    col--;
    for (int i = 0; i < span; i++) {
      row++;
      collect[index] = get (connect, row, col);
      index++;
    }
    return collect;
  }

  static ChannelOutput get (ChannelOutput[][] connect, int row, int col) {
    final int depth = connect.length;
    row = (row + depth)%depth;
    final int width = connect[row].length;
    col = (col + width)%width;
    return connect[row][col];
  }

}
