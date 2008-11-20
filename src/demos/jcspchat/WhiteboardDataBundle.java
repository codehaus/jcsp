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
import java.io.*;

/**
 * @author Quickstone Technologies Limited
 */
public class WhiteboardDataBundle implements Serializable {
  private int tool;
  private String user = "";
  private Rectangle rect = null;
  private boolean filled = false;
  private String text = "";
  private Color lineColor = null;
  private Color fillColor = null;
  private Object[] freehandArray = null;
  private int stroke = 1;
  private Point p1;
  private Point p2;


  //shape constructor
  public WhiteboardDataBundle(String user,int tool, Rectangle rect, boolean filled, Color lineColor, Color fillColor, int stroke) {
    this.tool = tool;
    this.rect = rect;
    this.filled = filled;
    this.lineColor = lineColor;
    this.fillColor = fillColor;
    this.stroke = stroke;
    this.user = user;
  }
  //line constructor
  public WhiteboardDataBundle(String user,int tool, Color lineColor,int stroke, Point p1, Point p2) {
    this.tool = tool;
    this.user = user;
    this.lineColor = lineColor;
    this.stroke = stroke;
    this.p1 = p1;
    this.p2 = p2;
  }
  //text constructor
  public WhiteboardDataBundle(String user,int tool, Rectangle rect, Color lineColor, String text) {
    this.tool = tool;
    this.user = user;
    this.rect = rect;
    this.lineColor = lineColor;
    this.text = text;
  }
  //wipe constructor
  public WhiteboardDataBundle(int tool) {
    this.tool = tool;
  }
  public int getTool() {
    return tool;
  }
  public int getStroke() {
    return stroke;
  }
  public String getUser() {
    return user;
  }
  public Rectangle getRect() {
    return rect;
  }
  public Point getP1() {
    return p1;
  }
  public Point getP2() {
    return p2;
  }
  public Color getLineColor() {
    return lineColor;
  }
  public Color getFillColor() {
    return fillColor;
  }
  public String getText() {
    return text;
  }
  public boolean isFilled() {
    return filled;
  }


}
