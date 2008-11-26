
import org.jcsp.demos.util.*;
import org.jcsp.lang.*;
import org.jcsp.util.*;
import org.jcsp.plugNplay.*;

import java.awt.Color;
import java.util.Random;
import java.util.ArrayList;

public class AltingBarrierGadget6Demo0 {

  public static void main (String[] argv) {

    final int width = Ask.Int ("\nwidth = ", 10, 30);
    final int depth = Ask.Int ("depth = ", 1, 30);

    final int span = Ask.Int ("length of arms = ", 1, width/2);

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
      click[i] = Channel.one2oneArray (width, new OverWriteOldestBuffer (1), width);
    }
    
    final One2OneChannel[][] configure = new One2OneChannel[depth][];
    for (int i = 0; i < depth; i++) {
      configure[i] = Channel.one2oneArray (width);
    }

    final FramedButtonGrid buttons =
      new FramedButtonGrid (
        "AltingBarier: Gadget 6, Demo 0", depth, width,
        20 + (depth*50), width*50, Util.get2DInputArray(configure), Util.get2DOutputArray(click)
      );

    // make labels for the buttons (read-only shared by all the gadgets)
    
    final String[] label = new String[playInterval/countInterval];
    for (int i = 0; i < label.length; i++) {
      label[i] = String.valueOf (i);
    }

    // make the gadgets

    final ArrayList[][] connect = new ArrayList [depth][width];
    for (int row = 0; row < depth; row++) {
      ArrayList[] connectRow = connect[row];
      for (int col = 0; col < width; col++) {
        connectRow[col] = new ArrayList (8*span);   // estimate initial size
      }
    }

    final Random random = new Random ();

    final Barrier bar = new Barrier (width*depth);

    final AltingBarrierGadget6[][] gadgets =
      new AltingBarrierGadget6[depth][width];

    for (int row = 0; row < depth; row++) {
      for (int col = 0; col < width; col++) {
        gadgets[row][col] =
          new AltingBarrierGadget6 (
            connect[row][col], shape (random, row, col, span, connect), bar,
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

  static ArrayList[] shape (
    Random random, int row, int col, int span, ArrayList[][] connect
  ) {
    switch (random.nextInt (3)) {
      case 0:
        return shapePlus (row, col, span, connect);
      case 1:
        return shapeCross (row, col, span, connect);
      case 2:
        return shapeCircle (row, col, span, connect);
      default:
        return null;
    }
  }

  static ArrayList[] shapePlus (
    int row, int col, int span, ArrayList[][] connect
  ) {
    final int n = 4*span;
    final ArrayList[] collect = new ArrayList[n];
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

  static ArrayList[] shapeCross (
    int row, int col, int span, ArrayList[][] connect
  ) {
    final int n = 4*span;
    final ArrayList[] collect = new ArrayList[n];
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

  static ArrayList[] shapeCircle (
    int row, int col, int span, ArrayList[][] connect
  ) {
    final int n = (4*span) - 1;
    final ArrayList[] collect = new ArrayList[n];
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

  static ArrayList get (ArrayList[][] connect, int row, int col) {
    final int depth = connect.length;
    row = (row + depth)%depth;
    final int width = connect[row].length;
    col = (col + width)%width;
    return connect[row][col];
  }

}
