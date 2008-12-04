    //////////////////////////////////////////////////////////////////////
    //                                                                  //
    //  JCSP ("CSP for Java") Libraries                                 //
    //  Copyright (C) 1996-2008 Peter Welch and Paul Austin.            //
    //                2001-2004 Quickstone Technologies Limited.        //
    //                                                                  //
    //  This library is free software; you can redistribute it and/or   //
    //  modify it under the terms of the GNU Lesser General Public      //
    //  License as published by the Free Software Foundation; either    //
    //  version 2.1 of the License, or (at your option) any later       //
    //  version.                                                        //
    //                                                                  //
    //  This library is distributed in the hope that it will be         //
    //  useful, but WITHOUT ANY WARRANTY; without even the implied      //
    //  warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR         //
    //  PURPOSE. See the GNU Lesser General Public License for more     //
    //  details.                                                        //
    //                                                                  //
    //  You should have received a copy of the GNU Lesser General       //
    //  Public License along with this library; if not, write to the    //
    //  Free Software Foundation, Inc., 59 Temple Place, Suite 330,     //
    //  Boston, MA 02111-1307, USA.                                     //
    //                                                                  //
    //  Author contact: P.H.Welch@kent.ac.uk                             //
    //                                                                  //
    //                                                                  //
    //////////////////////////////////////////////////////////////////////


import org.jcsp.lang.*;
import org.jcsp.awt.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.*;

/**
 * @author P.H. Welch
 */
class PlasmaControl implements CSProcess {

  private final ChannelOutput codeConfigure;
  private final AltingChannelInput codeChannel;
  private final ChannelOutput colourConfigure;
  private final AltingChannelInput colourChannel;
  private final String[] colourMenu;
  private final ChannelOutput scaleConfigure;
  private final AltingChannelInput scaleChannel;
  private final String[] scaleMenu;
  private final ChannelOutput freezeConfigure;
  private final AltingChannelInput freezeChannel;
  private final ChannelOutput fpsConfigure;
  private final AltingChannelInput resizeChannel;

  private final ChannelOutput toGraphics;
  private final ChannelInput fromGraphics;

  public PlasmaControl (final ChannelOutput codeConfigure,
                        final AltingChannelInput codeChannel,
                        final ChannelOutput colourConfigure,
                        final AltingChannelInput colourChannel,
                        final String[] colourMenu,
                        final ChannelOutput scaleConfigure,
                        final AltingChannelInput scaleChannel,
                        final String[] scaleMenu,
                        final ChannelOutput freezeConfigure,
                        final AltingChannelInput freezeChannel,
                        final ChannelOutput fpsConfigure,
                        final ChannelOutput toGraphics, final ChannelInput fromGraphics,
                        final AltingChannelInput resizeChannel) {

    this.codeConfigure = codeConfigure;
    this.codeChannel = codeChannel;
    this.colourConfigure = colourConfigure;
    this.colourChannel = colourChannel;
    this.colourMenu = colourMenu;
    this.scaleConfigure = scaleConfigure;
    this.scaleChannel = scaleChannel;
    this.scaleMenu = scaleMenu;
    this.freezeConfigure = freezeConfigure;
    this.freezeChannel = freezeChannel;
    this.fpsConfigure = fpsConfigure;
    this.toGraphics = toGraphics;
    this.fromGraphics = fromGraphics;
    this.resizeChannel = resizeChannel;

  }


  //     protected methods
  //     -----------------

  protected void fade (final byte[] downColour, final byte[] upColour,
                       final int start, final int finish, final int step) {
    int up = 0;
    int down = 255;
    for (int i = start; i < finish; i++) {
      upColour[i] = (byte) up;
      downColour[i] = (byte) down;
      up += step;
      down -= step;
    }
  }


  protected ColorModel createColorModel (final int variety) {

    byte[] red = new byte[256];
    byte[] green = new byte[256];
    byte[] blue = new byte[256];

    switch (variety) {

      case 0:

        fade (green, blue, 0, 86, 3);
        fade (blue, red, 86, 171, 3);
        fade (red, green, 171, 256, 3);

      break;
      case 1:

        fade (blue, red, 0, 128, 2);
        fade (red, blue, 128, 256, 2);

      break;
      case 2:

        fade (green, red, 0, 128, 2);
        fade (red, green, 128, 256, 2);

      break;
      case 3:

        fade (blue, green, 0, 128, 2);
        fade (green, blue, 128, 256, 2);

      break;
      case 4:

        final int redF = 127;
        final int greenF = 30;
        final int blueF = 50;

        for (int i = 0; i < 256; i++) {
          red[i] = (byte) (((i*redF) % 256) - 128);
          green[i] = (byte) (((i*greenF) % 256) - 128);
          blue[i] = (byte) (((i*blueF) % 256) - 128);
        }

      break;

    }

    final int nBitsOfColour = 8;
    return new IndexColorModel (nBitsOfColour, red.length, red, green, blue);

  }

  protected int[] createWaveTable (final int size) {
    int[] table = new int[size];
    for (int i = 0; i < size; i++) {
       table[i] = (int) (32*(1 + Math.sin(((double)i*2*Math.PI)/size)));
    }
    return table;
  }

  public void run () {

    final int nScales = scaleMenu.length;
    final int[] scale = new int[nScales];
    int scaleFactor = 1;
    for (int s = 0; s < nScales; s++) {
       scale[s] = scaleFactor;
       scaleFactor *= 2;
    }
    final int initialScale = 3;            // 1:8

    final int nColourModels = 5;
    MemoryImageSource[] mis = new MemoryImageSource[nColourModels];
    final int initialColourModel = 0;
    final Image[] image = new Image[nColourModels];

    int[] waveTable;

    final GraphicsCommand[] gcImage = {null};
    final GraphicsCommand[] draw = new GraphicsCommand[nColourModels];
    final DisplayList displayList = new DisplayList ();

    toGraphics.write (GraphicsProtocol.GET_DIMENSION);
    Dimension graphicsDim = (Dimension) fromGraphics.read ();
    System.out.println ("PlasmaControl: graphics dimension = " + graphicsDim);

    int givenWidth = graphicsDim.width;
    int givenHeight = graphicsDim.height;

    byte[] pixels = new byte[givenWidth*givenHeight];  // for the computed image

    int scaledWidth, scaledHeight;
    scaledWidth = givenWidth/scale[initialScale];
    scaledHeight = givenHeight/scale[initialScale];
    waveTable = createWaveTable ((scaledWidth + scaledHeight)*2);
    for (int i = 0; i < nColourModels; i++) {
      mis[i] = new MemoryImageSource (
        scaledWidth, scaledHeight, createColorModel (i), pixels, 0, scaledWidth
      );
      mis[i].setAnimated (true);
      mis[i].setFullBufferUpdates (true);
      toGraphics.write (new GraphicsProtocol.MakeMISImage (mis[i]));
      image[i] = (Image) fromGraphics.read ();
      draw[i] = new GraphicsCommand.DrawImage (image[i], 0, 0, givenWidth, givenHeight);
    }

    int currentScale = initialScale;
    int currentColourModel = initialColourModel;
    MemoryImageSource currentMIS = mis[currentColourModel];
    scaledWidth = givenWidth/scale[currentScale];
    scaledHeight = givenHeight/scale[currentScale];
    int[] currentWaveTable = waveTable;
    int currentWaveTableSize = currentWaveTable.length;

    toGraphics.write (new GraphicsProtocol.SetPaintable (displayList));
    fromGraphics.read ();

    freezeConfigure.write ("Freeze");
    fpsConfigure.write ("0");
    scaleConfigure.write (new Integer (currentScale));
    colourConfigure.write (new Integer (currentColourModel));

    gcImage[0] = draw[currentColourModel];

    // about to start cycling -- drop our priority to the minimum ...

    final Thread me = Thread.currentThread ();
    System.out.println ("Plasma priority = " + me.getPriority ());
    me.setPriority (Thread.MIN_PRIORITY);
    System.out.println ("Plasma priority = " + me.getPriority ());

    // the following are the main compute variables ...

    String scode = "2  5  1  4      6  3  3  0";
    final int nCodes = 8;

    codeConfigure.write (scode);

    final int[] icode = new int[nCodes];    // int versions of scode
    final int[] mcode = new int[nCodes];    // modulo currentWaveTableSize versions of scode

    decode (scode, icode, mcode);           // extracts icode (mcode used as workspace)
    modulate (icode, mcode, currentWaveTableSize);    // ensures non-negative mcode

    int speed0 = mcode[0], speed1 = mcode[1];
    int speed2 = mcode[2], speed3 = mcode[3];

    int increment0 = mcode[4], increment1 = mcode[5];
    int increment2 = mcode[6], increment3 = mcode[7];

    int position0 = 0, position1 = 0, position2 = 0, position3 = 0;

    final int CODE_EVENT = 0;
    final int SCALE_EVENT = 1;
    final int RESIZE_EVENT = 2;
    final int COLOUR_EVENT = 3;
    final int FREEZE_EVENT = 4;
    final int SKIP_EVENT = 5;

    final Guard[] guard = {
      codeChannel, scaleChannel, resizeChannel, colourChannel, freezeChannel, new Skip ()
    };

    final boolean[] preCondition = new boolean[guard.length];
    for (int i = 0; i < preCondition.length; i++) preCondition[i] = true;

    final Alternative alt = new Alternative (guard);

    final CSTimer tim = new CSTimer ();
    long firstFrameTime = tim.read ();
    long frames = 0;
    long fpsUpdate = 1;

    boolean displayChange = true;

    while(true) {

      // invariant: 0 <= position[i] < currentWaveTableSize
      // invariant: 0 <= speed[i] < currentWaveTableSize
      // invariant: 0 <= increment[i] < currentWaveTableSize

      switch (alt.priSelect (preCondition)) {

        case CODE_EVENT:
          String s = (String) codeChannel.read ();
          // System.out.println ("CODE_EVENT: " + s);
          if (decode (s, icode, mcode)) {
            scode = s;
            modulate (icode, mcode, currentWaveTableSize);
            speed0 = mcode[0]; speed1 = mcode[1];
            speed2 = mcode[2]; speed3 = mcode[3];
            increment0 = mcode[4]; increment1 = mcode[5];
            increment2 = mcode[6]; increment3 = mcode[7];
          } else {
            codeConfigure.write (scode);
          }
        break;

        case SCALE_EVENT:
          final ItemEvent se = (ItemEvent) scaleChannel.read ();
          final String schoice = (String) se.getItem ();
          // System.out.println ("SCALE_EVENT: " + schoice);
          int sindex = 0;
          while (!schoice.equals (scaleMenu[sindex])) sindex++;
          if (sindex == currentScale) break;
          currentScale = sindex;
          // fall through to RESIZE_EVENT

        case RESIZE_EVENT :
          if (resizeChannel.pending ()) {
            ComponentEvent e = (ComponentEvent)resizeChannel.read ();
            if (e.getID () != ComponentEvent.COMPONENT_RESIZED) break;
          }
          toGraphics.write (GraphicsProtocol.GET_DIMENSION);
          graphicsDim = (Dimension) fromGraphics.read ();
          System.out.println ("PlasmaControl: graphics dimension = " + graphicsDim);
          givenWidth = graphicsDim.width;
          givenHeight = graphicsDim.height;
          pixels = new byte[givenWidth*givenHeight];  // for the computed image
          scaledWidth = givenWidth/scale[currentScale];
          scaledHeight = givenHeight/scale[currentScale];
          waveTable = createWaveTable ((scaledWidth + scaledHeight)*2);
          for (int i = 0; i < nColourModels; i++) {
            mis[i] = new MemoryImageSource (
              scaledWidth, scaledHeight, createColorModel (i), pixels, 0, scaledWidth
            );
            mis[i].setAnimated (true);
            mis[i].setFullBufferUpdates (true);
            toGraphics.write (new GraphicsProtocol.MakeMISImage (mis[i]));
            image[i] = (Image) fromGraphics.read ();
            draw[i] = new GraphicsCommand.DrawImage (image[i], 0, 0, givenWidth, givenHeight);
          }
          currentMIS = mis[currentColourModel];
          scaledWidth = givenWidth/scale[currentScale];
          scaledHeight = givenHeight/scale[currentScale];
          currentWaveTable = waveTable;
          currentWaveTableSize = currentWaveTable.length;
          position0 %= currentWaveTableSize;
          position1 %= currentWaveTableSize;
          position2 %= currentWaveTableSize;
          position3 %= currentWaveTableSize;
          modulate (icode, mcode, currentWaveTableSize);
          speed0 = mcode[0]; speed1 = mcode[1];
          speed2 = mcode[2]; speed3 = mcode[3];
          increment0 = mcode[4]; increment1 = mcode[5];
          increment2 = mcode[6]; increment3 = mcode[7];
          firstFrameTime = tim.read ();
          frames = 0;
          fpsUpdate = 1;
          gcImage[0] = draw[currentColourModel];
          displayChange = true;
        break;

        case COLOUR_EVENT:
          final ItemEvent ce = (ItemEvent) colourChannel.read ();
          final String cchoice = (String) ce.getItem ();
          int cindex = 0;
          while (!cchoice.equals (colourMenu[cindex])) cindex++;
          if (cindex != currentColourModel) {
            currentColourModel = cindex;
            currentMIS = mis[currentColourModel];
            currentMIS.newPixels ();
            gcImage[0] = draw[currentColourModel];
            displayList.set (gcImage);
          }
        break;

        case FREEZE_EVENT:
          freezeChannel.read ();
          if (preCondition[SKIP_EVENT]) {
            freezeConfigure.write ("Unfreeze");
            preCondition[SKIP_EVENT] = false;
          } else {
            freezeConfigure.write ("Freeze");
            preCondition[SKIP_EVENT] = true;
            firstFrameTime = tim.read ();
            frames = 0;
            fpsUpdate = 1;
          }
        break;

        case SKIP_EVENT:

          int tposition0 = position0;
          int tposition1 = position1;

          int index = 0;

          for (int y = 0; y < scaledHeight; y++) {
            int tposition2 = position2;
            int tposition3 = position3;
            int tempval = currentWaveTable[tposition0] + currentWaveTable[tposition1];
            for (int x = 0; x < scaledWidth; x++) {
              pixels[index++] = (byte)
                (tempval + currentWaveTable[tposition2] + currentWaveTable[tposition3]);
              tposition2 = (tposition2 + increment2) % currentWaveTableSize;
              tposition3 = (tposition3 + increment3) % currentWaveTableSize;
            }
            tposition0 = (tposition0 + increment0) % currentWaveTableSize;
            tposition1 = (tposition1 + increment1) % currentWaveTableSize;
          }

          position0 = (position0 + speed0) % currentWaveTableSize;
          position1 = (position1 + speed1) % currentWaveTableSize;
          position2 = (position2 + speed2) % currentWaveTableSize;
          position3 = (position3 + speed3) % currentWaveTableSize;
    
          frames++;
          if (frames == fpsUpdate) {
	    final long thisFrameTime = tim.read ();
            final long period = thisFrameTime - firstFrameTime;
            long framesPerTenSeconds = (period == 0) ? 0 : (frames*10000) / period;
            fpsConfigure.write (framesPerTenSeconds/10 + "." + framesPerTenSeconds%10);
            fpsUpdate = framesPerTenSeconds/20;
            if (fpsUpdate == 0) fpsUpdate = 1;
	    firstFrameTime = thisFrameTime;
	    frames = 0;
          }

          currentMIS.newPixels ();
          if (displayChange) {
            displayChange = false;
            displayList.set (gcImage);
          }

        break;

      }

    }

  }

  private boolean decode (String s, int[] icode, int[] mcode) {
    try {
      if (s == null) throw new NoSuchElementException ();
      StringTokenizer tokens = new StringTokenizer (s);
      for (int i = 0; i < mcode.length; i++) {
        String item = tokens.nextToken();
        int check = Integer.valueOf (item.trim ()).intValue ();
        // if (check < 0) throw new NoSuchElementException ();
        mcode[i] = check;
        // System.out.println ("code[" + i + "] = " + mcode[i]);
      }
      if (tokens.hasMoreTokens ()) throw new NoSuchElementException ();
      for (int i = 0; i < icode.length; i++) {
        icode[i] = mcode[i];
      }
      // System.out.println ("codes OK");
      return true;
    } catch (NoSuchElementException e) {
      // System.out.println ("codes BAD");
      return false;
    } catch (NumberFormatException e2) {
      // System.out.println ("codes BAD");
      return false;
    }
  }

  private void modulate (int[] icode, int[] mcode, int n) {
    for (int i = 0; i < icode.length; i++) {
      // System.out.print ("code[" + i + "] = " + icode[i]);
      mcode[i] = icode[i] % n;
      // System.out.print (", "  + mcode[i]);
      if (mcode[i] < 0) mcode[i] += n;
      // System.out.println (", "  + mcode[i]);
    }
  }

}
