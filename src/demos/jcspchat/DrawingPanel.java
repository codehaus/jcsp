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


import javax.swing.*;
import java.awt.*;
import java.awt.image.*;


/**
 * @author Quickstone Technologies Limited
 */
public class DrawingPanel extends JPanel {
  public static final int whiteboardWidth = 1024;
  public static final int whiteboardHeight = 768;
  final static int FREEHAND = 1;
  final static int LINE = 2;
  final static int RECTANGLE = 3;
  final static int ROUND_RECTANGLE = 4;
  final static int OVAL = 5;
  final static int TEXT = 6;
  final static int WIPE = 999;
  final static Font TEXTFONT = new Font("SansSerif", Font.PLAIN, 16);

  int oldX,oldY,newX,newY;
  int tool = FREEHAND;
  boolean graphicsObjectInitialized = false;
  Rectangle oldRect = new Rectangle(0,0,whiteboardWidth,whiteboardHeight);
  boolean textActive = false;
  boolean filled = true;

  String textInput = "";


  BufferedImage buffer = new BufferedImage(whiteboardWidth,whiteboardHeight,BufferedImage.TYPE_3BYTE_BGR);
  Graphics2D bg = buffer.createGraphics();

  int strokeType = 3;
  Point startDrag = null;

  public DrawingPanel () {    
    bg.setColor(Color.white);
    bg.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
    bg.fillRect(0,0,buffer.getWidth(),buffer.getHeight());
    bg.setFont(TEXTFONT);
  }

  public boolean isFocusable() {   // used to override: isFocusTraversable()
    return true;
  }

  public Rectangle clearOldShape (Rectangle rect, Rectangle newRect) {
    this.getGraphics().drawImage(buffer.getSubimage(rect.x,rect.y,rect.width,rect.height),rect.x,rect.y,this);
    return (Rectangle)newRect.createIntersection(new Rectangle(0,0,whiteboardWidth,whiteboardHeight));
  }

  public int getCurrentTool() {
    return tool;
  }

  public Point getStartDrag() {
    return startDrag;
  }
  public boolean isFilled() {
    return filled;
  }
  public String getTextInput() {
    return textInput;
  }
  public void finishTextInput() {
    textActive = false;
    textInput= "";
  }
  public Point getOldPoint() {
    return new Point(oldX,oldY);
  }
  public int getStroke() {
    return strokeType;
  }
  public boolean isTextActive() {
    return textActive;
  }
  public BufferedImage getBufferedImage() {
    return buffer;
  }


  public void paintComponent(Graphics g) {
    super.paintComponent(g);
    g.drawImage(buffer,0,0,this);

  }
}
