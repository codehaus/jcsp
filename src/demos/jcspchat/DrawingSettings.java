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



import java.awt.*;

/**
 * @author Quickstone Technologies Limited
 */
public class DrawingSettings {
  private Color fillColor;
  private Color lineColor;
  private int strokeSize;
  private boolean filled;
  private int tool;
  private WhiteboardDataBundle currentDrawOp;
  private boolean isDrawing;
  private boolean textActive;

  public DrawingSettings(Color lc, Color fc, boolean f, int s, int t) {
    fillColor = fc;
    lineColor = lc;
    strokeSize = s;
    filled = f;
    tool = t;
  }
  public Color getLineColor() {
    return lineColor;
  }
  public Color getFillColor() {
    return fillColor;
  }
  public boolean isFilled() {
    return filled;
  }
  public int getStroke() {
    return strokeSize;
  }
  public int getTool() {
    return tool;
  }

  public void setLineColor(Color c) {
    lineColor = c;
  }
  public void setFillColor(Color c) {
    fillColor = c;
  }
  public void setFilled(boolean b) {
    filled = b;
  }
  public void setStrokeSize(int i) {
    strokeSize = i;
  }
  public void setTool(int i) {
    tool = i;
    if (i != DrawingPanel.TEXT) {
      this.setTextActive(false);
    }
  }
  public boolean isDrawing() {
    return isDrawing;
  }
  public boolean textActive() {
    return textActive;
  }
  public void setTextActive(boolean b) {
    textActive = b;
  }
}
