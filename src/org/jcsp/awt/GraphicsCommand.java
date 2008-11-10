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

package org.jcsp.awt;

import java.awt.*;
import org.jcsp.lang.TaggedProtocol;

/**
 * This is the tagged protocol interpreted by objects
 * (such as {@link DisplayList}) that implement the {@link Display} interface.
 * <H2>Description</H2>
 * <TT>GraphicsCommand</TT> is the {@link org.jcsp.lang.TaggedProtocol} interpreted by
 * objects (such as {@link DisplayList}) that implement the {@link Display} interface.
 * It is a collection of static <I>graphics command</I> classes
 * and a couple of immutable <I>graphics command</I> objects.
 * The constructors for each command class set the parameters for the command.
 * The immutable objects are commands that take no parameters.
 * <P>
 * A <TT>Display</TT> object provides a secure interface between an active user
 * process and an active graphics component (such as {@link ActiveCanvas}).
 * It enables all methods of <TT>java.awt.graphics</TT>
 * to be executed on the active graphics component.
 * User processes send an array of <TT>GraphicsCommand</TT>s
 * by invoking {@link Display#set <TT>set</TT>},
 * {@link Display#extend <TT>extend</TT>} or {@link Display#change <TT>change</TT>}
 * on the <TT>Display</TT>.
 * <P>
 * The meaning of each command is defined by the corresponding method
 * in <TT>java.awt.graphics</TT>.  For example, the {@link GraphicsCommand.DrawImage}
 * constructors correspond to the <TT>java.awt.graphics.drawImage</TT> methods.
 *
 * @see org.jcsp.awt.Display
 * @see org.jcsp.awt.DisplayList
 * @see org.jcsp.awt.ActiveCanvas
 * @see org.jcsp.lang.TaggedProtocol
 *
 * @author P.H. Welch
 */

public abstract class GraphicsCommand extends TaggedProtocol
{
   GraphicsCommand(final int tag)
   {
      super(tag);
   }
   
   static final int NULL_TAG = 0;
   static final int TRANSLATE = 1;
   static final int SET_COLOR = 2;
   static final int SET_PAINT_MODE_TAG = 3;
   static final int SET_XOR_MODE = 4;
   static final int SET_FONT = 5;
   static final int CLIP_RECT = 6;
   static final int SET_CLIP = 7;
   static final int COPY_AREA = 8;
   static final int DRAW_LINE = 9;
   static final int FILL_RECT = 10;
   static final int DRAW_RECT = 11;
   static final int CLEAR_RECT = 12;
   static final int DRAW_ROUND_RECT = 13;
   static final int FILL_ROUND_RECT = 14;
   static final int DRAW_3D_RECT = 15;
   static final int FILL_3D_RECT = 16;
   static final int DRAW_OVAL = 17;
   static final int FILL_OVAL = 18;
   static final int DRAW_ARC = 19;
   static final int FILL_ARC = 20;
   static final int DRAW_POLYLINE = 21;
   static final int DRAW_POLYGON = 22;
   static final int FILL_POLYGON = 23;
   static final int DRAW_STRING = 24;
   static final int DRAW_CHARS = 25;
   static final int DRAW_BYTES = 26;
   static final int DRAW_IMAGE = 27;
   static final int GENERAL = 28;
   
   static private final class Null extends GraphicsCommand
   {
      public Null()
      {
         super(NULL_TAG);
      }
   }
   
   /**
    * This is the null command whose interpretation is a <I>no-op</I>.
    */
   static public final GraphicsCommand NULL = new Null();
   
   /**
    * This is the (immutable) command object for
    * {@link java.awt.Graphics#translate <TT>java.awt.Graphics.translate</TT>}.
    */
   static public final class Translate extends GraphicsCommand
   {
      final int x;
      final int y;
      
      public Translate(final int x, final int y)
      {
         super(TRANSLATE);
         this.x = x;
         this.y = y;
      }
   }
   
   /**
    * This is the (immutable) command object for
    * {@link java.awt.Graphics#setColor <TT>java.awt.Graphics.setColor</TT>}.
    */
   static public final class SetColor extends GraphicsCommand
   {
      final Color c;
      
      public SetColor(final Color c)
      {
         super(SET_COLOR);
         this.c = c;
      }
   }
   
   /**
    * This is the (immutable) command object for
    * {@link java.awt.Graphics#setPaintMode <TT>java.awt.Graphics.setPaintMode</TT>}.
    */
   static private final class SetPaintMode extends GraphicsCommand
   {
      public SetPaintMode()
      {
         super(SET_PAINT_MODE_TAG);
      }
   }
   
   /**
    * This is the (immutable) command object for
    * {@link java.awt.Graphics#setPaintMode <TT>java.awt.Graphics.setPaintMode</TT>}.
    */
   static public final GraphicsCommand SET_PAINT_MODE = new SetPaintMode();
   
   /**
    * This is the (immutable) command object for
    * {@link java.awt.Graphics#setXORMode <TT>java.awt.Graphics.setXORMode</TT>}.
    */
   static public final class SetXORMode extends GraphicsCommand
   {
      final Color c;
      
      public SetXORMode(final Color c)
      {
         super(SET_XOR_MODE);
         this.c = c;
      }
   }
   
   /**
    * This is the (immutable) command object for
    * {@link java.awt.Graphics#setFont <TT>java.awt.Graphics.setFont</TT>}.
    */
   static public final class SetFont extends GraphicsCommand
   {
      final Font f;
      
      public SetFont(final Font f)
      {
         super(SET_FONT);
         this.f = f;
      }
   }
   
   /**
    * This is the (immutable) command object for
    * {@link java.awt.Graphics#clipRect <TT>java.awt.Graphics.clipRect</TT>}.
    */
   static public final class ClipRect extends GraphicsCommand
   {
      final int x;
      final int y;
      final int width;
      final int height;
      
      public ClipRect(final int x, final int y, final int width, final int height)
      {
         super(CLIP_RECT);
         this.x = x;
         this.y = y;
         this.width = width;
         this.height = height;
      }
   }
   
   /**
    * This is the (immutable) command object for
    * {@link java.awt.Graphics#setClip <TT>java.awt.Graphics.setClip</TT>}.
    */
   static public final class SetClip extends GraphicsCommand
   {
      final int cliptag;
      final int x;
      final int y;
      final int width;
      final int height;
      final Shape s;
      
      public SetClip(final int x, final int y, final int width, final int height)
      {
         super(SET_CLIP);
         this.cliptag = 0;
         this.x = x;
         this.y = y;
         this.width = width;
         this.height = height;
         this.s = null;
      }
      
      public SetClip(final Shape s)
      {
         super(SET_CLIP);
         this.cliptag = 1;
         this.s = s;
         this.x = 0;
         this.y = 0;
         this.width = 0;
         this.height = 0;
      }
   }
   
   /**
    * This is the (immutable) command object for
    * {@link java.awt.Graphics#copyArea <TT>java.awt.Graphics.copyArea</TT>}.
    */
   static public final class CopyArea extends GraphicsCommand
   {
      final int x;
      final int y;
      final int width;
      final int height;
      final int dx;
      final int dy;
      
      public CopyArea(final int x, final int y, final int width, final int height, final int dx, final int dy)
      {
         super(COPY_AREA);
         this.x = x;
         this.y = y;
         this.width = width;
         this.height = height;
         this.dx = dx;
         this.dy = dy;
      }
   }
   
   /**
    * This is the (immutable) command object for
    * {@link java.awt.Graphics#drawLine <TT>java.awt.Graphics.drawLine</TT>}.
    */
   static public final class DrawLine extends GraphicsCommand
   {
      final int x1;
      final int y1;
      final int x2;
      final int y2;
      
      public DrawLine(final int x1, final int y1, final int x2, final int y2)
      {
         super(DRAW_LINE);
         this.x1 = x1;
         this.y1 = y1;
         this.x2 = x2;
         this.y2 = y2;
      }
   }
   
   /**
    * This is the (immutable) command object for
    * {@link java.awt.Graphics#fillRect <TT>java.awt.Graphics.fillRect</TT>}.
    */
   static public final class FillRect extends GraphicsCommand
   {
      final int x;
      final int y;
      final int width;
      final int height;
      
      public FillRect(final int x, final int y, final int width, final int height)
      {
         super(FILL_RECT);
         this.x = x;
         this.y = y;
         this.width = width;
         this.height = height;
      }
   }
   
   /**
    * This is the (immutable) command object for
    * {@link java.awt.Graphics#drawRect <TT>java.awt.Graphics.drawRect</TT>}.
    */
   static public final class DrawRect extends GraphicsCommand
   {
      final int x;
      final int y;
      final int width;
      final int height;
      
      public DrawRect(final int x, final int y, final int width, final int height)
      {
         super(DRAW_RECT);
         this.x = x;
         this.y = y;
         this.width = width;
         this.height = height;
      }
   }
   
   /**
    * This is the (immutable) command object for
    * {@link java.awt.Graphics#clearRect <TT>java.awt.Graphics.clearRect</TT>}.
    */
   static public final class ClearRect extends GraphicsCommand
   {
      final int x;
      final int y;
      final int width;
      final int height;
      
      public ClearRect(final int x, final int y, final int width, final int height)
      {
         super(CLEAR_RECT);
         this.x = x;
         this.y = y;
         this.width = width;
         this.height = height;
      }
   }
   
   /**
    * This is the (immutable) command object for
    * {@link java.awt.Graphics#drawRoundRect <TT>java.awt.Graphics.drawRoundRect</TT>}.
    */
   static public final class DrawRoundRect extends GraphicsCommand
   {
      final int x;
      final int y;
      final int width;
      final int height;
      final int arcWidth;
      final int arcHeight;
      
      public DrawRoundRect(final int x, final int y, final int width, final int height,
              final int arcWidth, final int arcHeight)
      {
         super(DRAW_ROUND_RECT);
         this.x = x;
         this.y = y;
         this.width = width;
         this.height = height;
         this.arcWidth = arcWidth;
         this.arcHeight = arcHeight;
      }
   }
   
   /**
    * This is the (immutable) command object for
    * {@link java.awt.Graphics#fillRoundRect <TT>java.awt.Graphics.fillRoundRect</TT>}.
    */
   static public final class FillRoundRect extends GraphicsCommand
   {
      final int x;
      final int y;
      final int width;
      final int height;
      final int arcWidth;
      final int arcHeight;
      
      public FillRoundRect(final int x, final int y, final int width, final int height,
              final int arcWidth, final int arcHeight)
      {
         super(FILL_ROUND_RECT);
         this.x = x;
         this.y = y;
         this.width = width;
         this.height = height;
         this.arcWidth = arcWidth;
         this.arcHeight = arcHeight;
      }
   }
   
   /**
    * This is the (immutable) command object for
    * {@link java.awt.Graphics#draw3DRect <TT>java.awt.Graphics.draw3DRect</TT>}.
    */
   static public final class Draw3DRect extends GraphicsCommand
   {
      final int x;
      final int y;
      final int width;
      final int height;
      final boolean raised;
      
      public Draw3DRect(final int x, final int y, final int width, final int height,
              final boolean raised)
      {
         super(DRAW_3D_RECT);
         this.x = x;
         this.y = y;
         this.width = width;
         this.height = height;
         this.raised = raised;
      }
   }
   
   /**
    * This is the (immutable) command object for
    * {@link java.awt.Graphics#translate <TT>java.awt.Graphics.translate</TT>}.
    */
   static public final class Fill3DRect extends GraphicsCommand
   {
      final int x;
      final int y;
      final int width;
      final int height;
      final boolean raised;
      
      public Fill3DRect(final int x, final int y, final int width, final int height,
              final boolean raised)
      {
         super(FILL_3D_RECT);
         this.x = x;
         this.y = y;
         this.width = width;
         this.height = height;
         this.raised = raised;
      }
   }
   
   /**
    * This is the (immutable) command object for
    * {@link java.awt.Graphics#drawOval <TT>java.awt.Graphics.drawOval</TT>}.
    */
   static public final class DrawOval extends GraphicsCommand
   {
      final int x;
      final int y;
      final int width;
      final int height;
      
      public DrawOval(final int x, final int y, final int width, final int height)
      {
         super(DRAW_OVAL);
         this.x = x;
         this.y = y;
         this.width = width;
         this.height = height;
      }
   }
   
   /**
    * This is the (immutable) command object for
    * {@link java.awt.Graphics#fillOval <TT>java.awt.Graphics.fillOval</TT>}.
    */
   static public final class FillOval extends GraphicsCommand
   {
      final int x;
      final int y;
      final int width;
      final int height;
      
      public FillOval(final int x, final int y, final int width, final int height)
      {
         super(FILL_OVAL);
         this.x = x;
         this.y = y;
         this.width = width;
         this.height = height;
      }
   }
   
   /**
    * This is the (immutable) command object for
    * {@link java.awt.Graphics#drawArc <TT>java.awt.Graphics.drawArc</TT>}.
    */
   static public final class DrawArc extends GraphicsCommand
   {
      final int x;
      final int y;
      final int width;
      final int height;
      final int startAngle;
      final int arcAngle;
      
      public DrawArc(final int x, final int y, final int width, final int height,
              final int startAngle, final int arcAngle)
      {
         super(DRAW_ARC);
         this.x = x;
         this.y = y;
         this.width = width;
         this.height = height;
         this.startAngle = startAngle;
         this.arcAngle = arcAngle;
      }
   }
   
   /**
    * This is the (immutable) command object for
    * {@link java.awt.Graphics#fillArc <TT>java.awt.Graphics.fillArc</TT>}.
    */
   static public final class FillArc extends GraphicsCommand
   {
      final int x;
      final int y;
      final int width;
      final int height;
      final int startAngle;
      final int arcAngle;
      
      public FillArc(final int x, final int y, final int width, final int height,
              final int startAngle, final int arcAngle)
      {
         super(FILL_ARC);
         this.x = x;
         this.y = y;
         this.width = width;
         this.height = height;
         this.startAngle = startAngle;
         this.arcAngle = arcAngle;
      }
   }
   
   /**
    * This is the (immutable) command object for
    * {@link java.awt.Graphics#drawPolyline <TT>java.awt.Graphics.drawPolyline</TT>}.
    */
   static public final class DrawPolyline extends GraphicsCommand
   {
      final int[] xPoints;
      final int[] yPoints;
      final int nPoints;
      
      public DrawPolyline(final int[] xPoints, final int[] yPoints, final int nPoints)
      {
         super(DRAW_POLYLINE);
         this.xPoints = xPoints;
         this.yPoints = yPoints;
         this.nPoints = nPoints;
      }
   }
   
   /**
    * This is the (immutable) command object for
    * {@link java.awt.Graphics#drawPolygon <TT>java.awt.Graphics.drawPolygon</TT>}.
    */
   static public final class DrawPolygon extends GraphicsCommand
   {
      final int polytag;
      final int[] xPoints;
      final int[] yPoints;
      final int nPoints;
      final Polygon p;
      
      public DrawPolygon(final int[] xPoints, final int[] yPoints, final int nPoints)
      {
         super(DRAW_POLYGON);
         this.polytag = 0;
         this.xPoints = xPoints;
         this.yPoints = yPoints;
         this.nPoints = nPoints;
         this.p = null;
      }
      
      public DrawPolygon(final Polygon p)
      {
         super(DRAW_POLYGON);
         this.polytag = 1;
         this.p = p;
         this.xPoints = null;
         this.yPoints = null;
         this.nPoints = 0;
      }
   }
   
   /**
    * This is the (immutable) command object for
    * {@link java.awt.Graphics#fillPolygon <TT>java.awt.Graphics.fillPolygon</TT>}.
    */
   static public final class FillPolygon extends GraphicsCommand
   {
      final int polytag;
      final int[] xPoints;
      final int[] yPoints;
      final int nPoints;
      final Polygon p;
      
      public FillPolygon(final int[] xPoints, final int[] yPoints, final int nPoints)
      {
         super(FILL_POLYGON);
         this.polytag = 0;
         this.xPoints = xPoints;
         this.yPoints = yPoints;
         this.nPoints = nPoints;
         this.p = null;
      }
      
      public FillPolygon(final Polygon p)
      {
         super(FILL_POLYGON);
         this.polytag = 1;
         this.p = p;
         this.xPoints = null;
         this.yPoints = null;
         this.nPoints = 0;
      }
   }
   
   /**
    * This is the (immutable) command object for
    * {@link java.awt.Graphics#drawString <TT>java.awt.Graphics.drawString</TT>}.
    */
   static public final class DrawString extends GraphicsCommand
   {
      final String string;
      final int x;
      final int y;
      
      public DrawString(final String string, final int x, final int y)
      {
         super(DRAW_STRING);
         this.string = string;
         this.x = x;
         this.y = y;
      }
   }
   
   /**
    * This is the (immutable) command object for
    * {@link java.awt.Graphics#drawChars <TT>java.awt.Graphics.drawChars</TT>}.
    */
   static public final class DrawChars extends GraphicsCommand
   {
      final char[] data;
      final int offset;
      final int length;
      final int x;
      final int y;
      
      public DrawChars(final char[] data, final int offset, final int length,
              final int x, final int y)
      {
         super(DRAW_CHARS);
         this.data = data;
         this.offset = offset;
         this.length = length;
         this.x = x;
         this.y = y;
      }
   }
   
   /**
    * This is the (immutable) command object for
    * {@link java.awt.Graphics#drawBytes <TT>java.awt.Graphics.drawBytes</TT>}.
    */
   static public final class DrawBytes extends GraphicsCommand
   {
      final byte[] data;
      final int offset;
      final int length;
      final int x;
      final int y;
      
      public DrawBytes(final byte[] data, final int offset, final int length,
              final int x, final int y)
      {
         super(DRAW_BYTES);
         this.data = data;
         this.offset = offset;
         this.length = length;
         this.x = x;
         this.y = y;
      }
   }
   
   /**
    * This is the (immutable) command object for
    * {@link java.awt.Graphics#drawImage <TT>java.awt.Graphics.drawImage</TT>}.
    */
   static public final class DrawImage extends GraphicsCommand
   {
      final int drawtag;
      final Image image;
      final int x;
      final int y;
      final int width;
      final int height;
      final Color bgcolor;
      final int dx1, dy1, dx2, dy2;
      final int sx1, sy1, sx2, sy2;
      
      public DrawImage(final Image image, final int x, final int y)
      {
         super(DRAW_IMAGE);
         this.drawtag = 0;
         this.image = image;
         this.x = x;
         this.y = y;
         this.width = 0;
         this.height = 0;
         this.bgcolor = null;
         this.dx1 = 0;  this.dy1 = 0;  this.dx2 = 0;  this.dy2 = 0;
         this.sx1 = 0;  this.sy1 = 0;  this.sx2 = 0;  this.sy2 = 0;
      }
      
      public DrawImage(final Image image, final int x, final int y, final int width, final int height)
      {
         super(DRAW_IMAGE);
         this.drawtag = 1;
         this.image = image;
         this.x = x;
         this.y = y;
         this.width = width;
         this.height = height;
         this.bgcolor = null;
         this.dx1 = 0;  this.dy1 = 0;  this.dx2 = 0;  this.dy2 = 0;
         this.sx1 = 0;  this.sy1 = 0;  this.sx2 = 0;  this.sy2 = 0;
      }
      
      public DrawImage(final Image image, final int x, final int y, final Color bgcolor)
      {
         super(DRAW_IMAGE);
         this.drawtag = 2;
         this.image = image;
         this.x = x;
         this.y = y;
         this.bgcolor = bgcolor;
         this.width = 0;
         this.height = 0;
         this.dx1 = 0;  this.dy1 = 0;  this.dx2 = 0;  this.dy2 = 0;
         this.sx1 = 0;  this.sy1 = 0;  this.sx2 = 0;  this.sy2 = 0;
      }
      
      public DrawImage(final Image image, final int x, final int y, final int width, final int height, final Color bgcolor)
      {
         super(DRAW_IMAGE);
         this.drawtag = 3;
         this.image = image;
         this.x = x;
         this.y = y;
         this.width = width;
         this.height = height;
         this.bgcolor = bgcolor;
         this.dx1 = 0;  this.dy1 = 0;  this.dx2 = 0;  this.dy2 = 0;
         this.sx1 = 0;  this.sy1 = 0;  this.sx2 = 0;  this.sy2 = 0;
      }
      
      public DrawImage(final Image image,
              final int dx1, final int dy1, final int dx2, final int dy2,
              final int sx1, final int sy1, final int sx2, final int sy2)
      {
         super(DRAW_IMAGE);
         this.drawtag = 4;
         this.image = image;
         this.dx1 = dx1;  this.dy1 = dy1;  this.dx2 = dx2;  this.dy2 = dy2;
         this.sx1 = sx1;  this.sy1 = sy1;  this.sx2 = sx2;  this.sy2 = sy2;
         this.x = 0;
         this.y = 0;
         this.width = 0;
         this.height = 0;
         this.bgcolor = null;
      }
      
      public DrawImage(final Image image,
              final int dx1, final int dy1, final int dx2, final int dy2,
              final int sx1, final int sy1, final int sx2, final int sy2,
              final Color bgcolor)
      {
         super(DRAW_IMAGE);
         this.drawtag = 5;
         this.image = image;
         this.dx1 = dx1;  this.dy1 = dy1;  this.dx2 = dx2;  this.dy2 = dy2;
         this.sx1 = sx1;  this.sy1 = sy1;  this.sx2 = sx2;  this.sy2 = sy2;
         this.bgcolor = bgcolor;
         this.x = 0;
         this.y = 0;
         this.width = 0;
         this.height = 0;
      }
   }
   
   /**
    * This is the interface allowing <I>any</I> set of {@link java.awt.Graphics}
    * operations to be wrapped up in an object and sent down a {@link Display}
    * channel to an <I>Active</I> graphics component.  See {@link GraphicsCommand.General}
    */
   static public interface Graphic
   {
      public void doGraphic(final Graphics g, final Component c);
   }
   
   /**
    * This is the command object holding a {@link GraphicsCommand.Graphic} object
    * implementing an arbitrary set of graphics operations.
    */
   static public final class General extends GraphicsCommand
   {
      final Graphic g;

      public General(final Graphic g)
      {
         super(GENERAL);
         this.g = g;
      }
   }
}