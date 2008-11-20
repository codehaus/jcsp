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
import java.awt.event.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.image.*;
import org.jcsp.lang.*;

/**
 * @author Quickstone Technologies Limited
 */
public class WhiteboardSendProcess implements CSProcess {
  private DrawingPanel dp;
  private DrawingSettings ds;
  private ChannelOutput out;
  private ChannelOutput lpbk;
  private int oldX,oldY,newX,newY,sx,ex,sy,ey;
  private Point startDrag;
  private String user;
  private Point endDrag;
  private Rectangle oldRect = new Rectangle(0,0,DrawingPanel.whiteboardWidth,DrawingPanel.whiteboardHeight);
  private String textInput = "";
  private Runnable runner;
  private Graphics2D dg;
  private Graphics2D bg;
  private JButton wipeButton;
  private BufferedImage bi;
  private JScrollPane scrollPane;
  private boolean filled = true;
  private boolean localDrawActive = false;
  private ChannelInputInt in;
  private JComboBox toolMenu;
  private JFrame chatPlusFrame;
  private ChannelOutput connectChan;
  private ChannelOutput serverOutChan;


  public WhiteboardSendProcess(DrawingSettings ds, DrawingPanel panel, JButton wipeButton, JScrollPane scrollPane, JComboBox toolMenu, JFrame chatPlusFrame,ChannelOutput output, ChannelInputInt in, String user, ChannelOutput connectChan, ChannelOutput serverOutChan) {
    this.in = in;
    this.user = user;
    this.ds = ds;
    this.serverOutChan = serverOutChan;
    this.connectChan = connectChan;
    this.chatPlusFrame = chatPlusFrame;
    this.toolMenu = toolMenu;
    this.scrollPane = scrollPane;
    dp = panel;
    bi = dp.getBufferedImage();
    this.wipeButton = wipeButton;

    out = output;
    dg = (Graphics2D)dp.getGraphics();
    dg.setFont(DrawingPanel.TEXTFONT);
    this.addListeners();
  }


  private void drawLocalShapes() {
    sx = startDrag.x;
    sy = startDrag.y;
    ex = newX;
    ey = newY;
    if (ex < sx) {
      ex = startDrag.x;
      sx = newX;
    }
    if (ey < sy) {
      ey = startDrag.y;
      sy = newY;
    }
    final Rectangle newRect = new Rectangle(sx-10,sy-10,ex-sx+20,ey-sy+20);
    dg = (Graphics2D)dp.getGraphics();
    bg = (Graphics2D)bi.createGraphics();
    bg.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
    Runnable runner = new Runnable() {
      public void run() {
      }
    };
    switch (ds.getTool()) {
      case DrawingPanel.OVAL:
        runner = new Runnable() {
          public void run() {
            oldRect = dp.clearOldShape(oldRect,newRect);
            dg.setColor(ds.getLineColor());
            dg.setStroke(WhiteboardReceiveProcess.strokes[ds.getStroke()-1]);
            dg.drawOval(sx,sy,ex-sx,ey-sy);
          }
        };
        break;
      case DrawingPanel.ROUND_RECTANGLE:
        runner = new Runnable() {
          public void run() {
            oldRect = dp.clearOldShape(oldRect,newRect);
            int edgeRadius = (Math.min(ex-sx,ey-sy))/10;
            dg.setColor(ds.getLineColor());
            dg.setStroke(WhiteboardReceiveProcess.strokes[ds.getStroke()-1]);
            dg.drawRoundRect(sx,sy,ex-sx,ey-sy,edgeRadius,edgeRadius);
          }
        };
        break;
      case DrawingPanel.LINE:
        runner = new Runnable() {
          public void run() {
            oldRect = dp.clearOldShape(oldRect,newRect);
            dg.setColor(ds.getLineColor());
            dg.setStroke(WhiteboardReceiveProcess.strokes[ds.getStroke()-1]);
            dg.drawLine(startDrag.x,startDrag.y,newX,newY);
          }
        };
        break;
      case DrawingPanel.RECTANGLE:
        runner = new Runnable() {
          public void run() {
            oldRect = dp.clearOldShape(oldRect,newRect);
            dg.setColor(ds.getLineColor());
            dg.setStroke(WhiteboardReceiveProcess.strokes[ds.getStroke()-1]);
            dg.drawRect(sx,sy,ex-sx,ey-sy);
          }
        };
        break;
    }
    SwingUtilities.invokeLater(runner);
  }

  private void drawLocalText() {
    SwingUtilities.invokeLater(new Runnable () {
      public void run() {

        dp.paintImmediately(new Rectangle(oldX-20,oldY-20,dg.getFontMetrics().stringWidth(textInput)+40, dg.getFontMetrics().getHeight()+40));
        dg = (Graphics2D)dp.getGraphics();
        dg.setFont(DrawingPanel.TEXTFONT);
        dg.setColor(ds.getLineColor());
        dg.drawString(textInput+"|",oldX,oldY);
      }
    });
  }


  private void addListeners() {

    chatPlusFrame.addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        connectChan.write(new ConnectionBundle(user,false));
        out.write(new MessageObject(user,user + " DISCONNECTED\n",MessageObject.DISCONNECT));

        System.exit(0);
      }
    });



    toolMenu.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        ds.setTool(toolMenu.getSelectedIndex()+1);
        if (ds.getTool() != DrawingPanel.TEXT) {
          ds.setTextActive(false);
          textInput = "";
        }
        dp.repaint();
      }
    });
    scrollPane.getViewport().addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        if (ds.textActive()) {
          dg = (Graphics2D)dp.getGraphics();
          WhiteboardSendProcess.this.drawLocalText();
        }
      }
    });
    MouseHandler mh = new MouseHandler();

    dp.addHierarchyBoundsListener(new HierarchyBoundsAdapter() {
      public void ancestorResized(HierarchyEvent e) {
        dg = (Graphics2D)dp.getGraphics();
      }
    });

    wipeButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        out.write(new WhiteboardDataBundle(DrawingPanel.WIPE));
      }
    });


    dp.addMouseListener(mh);
    dp.addMouseMotionListener(mh);
    dp.addKeyListener(new KeyAdapter() {
      boolean dontType = false;
      public void keyTyped(KeyEvent e) {

        if (ds.getTool() == DrawingPanel.TEXT && ds.textActive()) {

          if (dontType) {
            dontType = false;
          }
          else if (e.getKeyChar() != KeyEvent.CHAR_UNDEFINED) {

            textInput = textInput+e.getKeyChar();
            WhiteboardSendProcess.this.drawLocalText();
          }
        }
      }

      public void keyPressed(KeyEvent e) {

        if (ds.getTool() == DrawingPanel.TEXT && ds.textActive()) {

          if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
            if (textInput.length() > 0) {
              textInput = textInput.substring(0,textInput.length()-1);
            }
            WhiteboardSendProcess.this.drawLocalText();
            dontType = true;
          }
          if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            ds.setTextActive(false);
            textInput = "";
            dp.paintImmediately(new Rectangle(oldX-20,oldY-20,dg.getFontMetrics().stringWidth(textInput+"  ")+40, dg.getFontMetrics().getHeight()+40));
          }
          if (e.getKeyCode() == KeyEvent.VK_ENTER) {

            out.write(new WhiteboardDataBundle(WhiteboardSendProcess.this.user,DrawingPanel.TEXT,new Rectangle(WhiteboardSendProcess.this.startDrag.x,WhiteboardSendProcess.this.startDrag.y,1,1),ds.getLineColor(),textInput));
            ds.setTextActive(false);
            textInput = "";
          }
        }
      }

    });
  }

  public void run() {
    while (true) {
      in.read();
      if (ds.textActive()) {
        this.drawLocalText();
      }
      if (localDrawActive) {
        this.drawLocalShapes();
      }
    }
  }

  public Rectangle calcRect(Point start, Point end) {
    int sx = start.x;
    int sy = start.y;
    int ex = end.x;
    int ey = end.y;
    if (ex < sx) {
      ex = start.x;
      sx = end.x;
    }
    if (ey < sy) {
      ey = start.y;
      sy = end.y;
    }
    return new Rectangle(sx,sy,ex-sx,ey-sy);
  }

  private class MouseHandler extends MouseAdapter implements MouseMotionListener {

    public void mouseMoved (MouseEvent e) {}
    public void mouseReleased (MouseEvent e) {
      localDrawActive = false;
      endDrag = e.getPoint();
      switch (ds.getTool()) {
        case DrawingPanel.FREEHAND:
        break;
        case DrawingPanel.LINE:
          out.write(new WhiteboardDataBundle(user, ds.getTool(),ds.getLineColor(),ds.getStroke(),new Point(startDrag.x,startDrag.y), new Point(endDrag.x,endDrag.y)));
          break;
        case DrawingPanel.TEXT:
          break;
        default:
          Rectangle rect = WhiteboardSendProcess.this.calcRect(startDrag,endDrag);
          out.write(new WhiteboardDataBundle(user, ds.getTool(),rect,ds.isFilled(),ds.getLineColor(), ds.getFillColor(),ds.getStroke()));
      }
    }
    public void mousePressed(MouseEvent e) {
      dp.requestFocus();
      if (!ds.textActive()) {
        oldX = e.getX();
        oldY = e.getY();
        newX = e.getX();
        newY = e.getY();
        startDrag = e.getPoint();
        switch (ds.getTool()) {
          case DrawingPanel.TEXT:
            ds.setTextActive(true);
            dp.paintImmediately(0,0,DrawingPanel.whiteboardWidth,DrawingPanel.whiteboardHeight);
            dg.drawString(textInput+"|",oldX,oldY);
            break;
          case DrawingPanel.FREEHAND:
            bg = (Graphics2D)bi.createGraphics();
            bg.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
            bg.setColor(ds.getLineColor());
            bg.setStroke(WhiteboardReceiveProcess.strokes[ds.getStroke()-1]);
            bg.drawLine(oldX,oldY,newX,newY);
            dp.paintImmediately(Math.min(oldX,newX)-10,Math.min(oldY,newY)-10,Math.abs(oldX-newX)+20,Math.abs(oldY-newY)+20);
            out.write(new WhiteboardDataBundle(user,ds.getTool(),ds.getLineColor(),ds.getStroke(),new Point (oldX, oldY),new Point(newX,newY)));
        }
      }
    }
    public void mouseDragged(MouseEvent e) {
      if (!ds.textActive()) {
        oldX = newX;
        oldY = newY;
        newX = e.getX();
        newY = e.getY();
        switch (ds.getTool()) {
          case DrawingPanel.TEXT:
            break;
          case DrawingPanel.FREEHAND:
            bg.setColor(ds.getLineColor());
            bg.setStroke(WhiteboardReceiveProcess.strokes[ds.getStroke()-1]);
            bg.drawLine(oldX,oldY,newX,newY);
            dp.paintImmediately(Math.min(oldX,newX)-10,Math.min(oldY,newY)-10,Math.abs(oldX-newX)+20,Math.abs(oldY-newY)+20);
            //System.out.println("sending freehand info: " + oldX + ", " + oldY + " - " + newX + ", " + newY);
            out.write(new WhiteboardDataBundle(user,ds.getTool(),ds.getLineColor(),ds.getStroke(),new Point (oldX, oldY),new Point(newX,newY)));
          break;
          default:
            WhiteboardSendProcess.this.drawLocalShapes();
            break;
        }
        localDrawActive = true;
      }
    }
  }
}






