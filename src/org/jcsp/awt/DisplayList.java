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

/**
 * This implements the {@link Display} and {@link Paintable} interfaces and provides
 * a channel-like connection between user processes and an active graphics component.
 * <H2>Description</H2>
 * A <TT>DisplayList</TT> is a passive object providing graphics services on behalf
 * of a <TT>CSProcess</TT>.  It provides an <I>occam3</I>-like <TT>CALL</TT>
 * channel between the application <TT>CSProcess</TT> and the active graphics component
 * (such as {@link ActiveCanvas <TT>ActiveCanvas</TT>})
 * on which it wishes to draw.
 *
 * <p><img src="doc-files\DisplayList1.gif"></p>
 *
 * The <I>user process</I> {@link #set set}s, {@link #extend extend}s or {@link #change change}s
 * a list of {@link GraphicsCommand GraphicsCommand}s maintained by the <TT>DisplayList</TT>.
 * Any such operation causes the Java <I>Event thread</I> to call back, via the active graphics
 * component, on its {@link #paint paint} or {@link #update update} methods and execute
 * those commands.
 * <P>
 * The <I>user process</I> sees the {@link Display <TT>Display</TT>} interface to the <TT>DisplayList</TT>
 * (in the same way as a writer process sees the <TT>OutputChannel</TT> interface to
 * a <TT>Channel</TT>).  The <TT>ActiveCanvas</TT> process sees the {@link Paintable <TT>Paintable</TT>}
 * interface to the <TT>DisplayList</TT> (in the same way as a reader process sees
 * the <TT>InputChannel</TT> interface to a <TT>Channel</TT>).
 * <P>
 * Unlike a <TT>Channel</TT>, however, a <TT>DisplayList</TT> should never block
 * any of its attached processes indefinitely, regardless of the behaviour of its
 * partner at the other end.
 * The <TT>DisplayList</TT> imposes mutually exclusive access to its state and
 * there are no <TT>wait/notify</TT> operations -- so any delays should be transient.
 * User process sets/extends/changes and <TT>ActiveCanvas</TT>
 * paints/updates on the <TT>DisplayList</TT>, therefore, should always succeed.
 * <P>
 * <I>Note: the cautionary note in the above paragraph is because Java
 * makes no guarantee that any invocation of a </I><TT>synchronized</TT><I> method
 * ever takes place.  This is something with which any Java application has to live</I>.
 * <P>
 * Any number of user processes may draw on the same component via a <TT>DisplayList</TT>
 * -- i.e. it is a <I>any-one</I> channel.
 * By reserving different sections of a <TT>DisplayList</TT> for control by different
 * processes, complex multiple animations can be simply managed.
 * <P>
 * <I>Note: in this release, only the </I>{@link #extend <TT>extend</TT>}<I> and
 * </I>{@link #change <TT>change</TT>}<I> methods are safe for direct use by multiple
 * user processes on the same </I><TT>DisplayList</TT><I>.  A process should only invoke
 * </I>{@link #set <TT>set</TT>}<I> at times when it knows others cannot be operating
 * on that </I><TT>DisplayList</TT><I>.  This would normally be required by an application,
 * since a </I><TT>DisplayList</TT><I> re</I><TT>set</TT><I> invalidates the result
 * returned by a previous </I><TT>extend</TT><I> and the base index used in
 * a </I><TT>change</TT><I> -- i.e. concurrent processes doing these things will need
 * to be informed before they do them again!  However, if the list really needs concurrent
 * setting without such an arrangement, this can be done within
 * a </I><TT>synchronized</TT><I> block on the </I><TT>DisplayList</TT>.
 * <P>
 * User applications will not normally be implementing new processes that are at the receiving
 * end of a <TT>DisplayList</TT>.  Users will only be responsible for connecting a <TT>DisplayList</TT>
 * to a standard <TT>org.jcsp.awt</TT> drawing component.  This can either be done statically (e.g.
 * through {@link ActiveCanvas#setPaintable <TT>setPaintable</TT>}) or dynamically (by sending
 * a {@link GraphicsProtocol.SetPaintable} object through
 * a {@link GraphicsProtocol} channel).
 *
 * @see org.jcsp.awt.GraphicsCommand
 * @see org.jcsp.awt.Display
 * @see org.jcsp.awt.Paintable
 * @see org.jcsp.awt.ActiveCanvas
 *
 * @author P.H. Welch
 */

public class DisplayList implements Paintable, Display
{
   private Component component;
   private Dimension size;                        // ***
   private Image image;                           // ***
   private Graphics imageGraphics;                // ***
   
   private final int INITIAL_MAX_COMMANDS = 10;
   private GraphicsCommand[] command = new GraphicsCommand[INITIAL_MAX_COMMANDS];
   private int maxCommands = INITIAL_MAX_COMMANDS;
   private int nCommands = 0;
   private int updateIndex = 0;  // invariant: 0 <= updateIndex <= nCommands <= maxCommands
   private boolean refresh = false;
   private boolean updated = true;
   private long minRefreshInterval = 10;  // milliseconds
   
   /**
    * Sets the array of <TT>GraphicsCommand</TT>s to be executed.  The commands will
    * be executed in ascending order of index.
    * The <TT>repaint</TT> method of the registered component is called to trigger
    * the {@link #update <TT>update</TT>} callback on this object.
    * All commands will be executed.
    * <P>
    *
    * @param c the array of GraphicsCommands to be executed.
    */
   public void set(final GraphicsCommand[] c)
   {
      if (c != null)
      {
         final int n = c.length;
         if (n > 0)
         {
            if (n <= maxCommands)
            {
               synchronized (this)
               {
                  System.arraycopy(c, 0, command, 0, n);
                  nCommands = n;
                  updateIndex = 0;
                  refresh = true;
               }
            }
            else
            {
               maxCommands = 2*n;
               final GraphicsCommand[] tmp = new GraphicsCommand[maxCommands];
               System.arraycopy(c, 0, tmp, 0, n);
               synchronized (this)
               {
                  command = tmp;
                  nCommands = n;
                  updateIndex = 0;
                  refresh = true;
               }
            }
         }
      }
      component.repaint(minRefreshInterval);
   }
   
   /**
    * Sets the <TT>GraphicsCommand</TT> to be executed.
    * The <TT>repaint</TT> method of the registered component is called to trigger
    * the {@link #update <TT>update</TT>} callback on this object.
    * <P>
    *
    * @param c the GraphicsCommand to be executed.
    */
   public void set(final GraphicsCommand c)
   {
      if (c != null)
      {
         synchronized (this)
         {
            command[0] = c;
            nCommands = 1;
            updateIndex = 0;
            refresh = true;
         }
      }
      component.repaint(minRefreshInterval);
   }
   
   /**
    * Extends the array of <TT>GraphicsCommand</TT>s to be executed.
    * The <TT>repaint</TT> method of the registered component is called to trigger
    * the {@link #update <TT>update</TT>} callback on this object.
    * Only the new commands will be executed.
    * <P>
    *
    * @param c the extra GraphicsCommands to be executed.
    * @return the start index of the extension.
    */
   public synchronized int extend(final GraphicsCommand[] c)
   {
      final int extensionStart = nCommands;
      if (c != null)
      {
         final int n = c.length;
         if (n > 0)
         {
            final int newNcommands = extensionStart + n;
            if (newNcommands <= maxCommands)
            {
               System.arraycopy(c, 0, command, extensionStart, n);
               if (updated)
               {
                  updateIndex = extensionStart;
                  updated = false;
               }
               nCommands = newNcommands;
            }
            else
            {
               maxCommands = 2*newNcommands;
               final GraphicsCommand[] tmp = new GraphicsCommand[maxCommands];
               System.arraycopy(command, 0, tmp, 0, extensionStart);
               System.arraycopy(c, 0, tmp, extensionStart, n);
               command = tmp;
               if (updated)
               {
                  updateIndex = extensionStart;
                  updated = false;
               }
               nCommands = newNcommands;
            }
         }
      }
      component.repaint(minRefreshInterval);
      return extensionStart;
   }
   
   /**
    * Extends the array of <TT>GraphicsCommand</TT>s to be executed by one command.
    * The <TT>repaint</TT> method of the registered component is called to trigger
    * the {@link #update <TT>update</TT>} callback on this object.
    * Only the new command will be executed.
    * <P>
    *
    * @param c the extra GraphicsCommand to be executed.
    * @return the start index of the extension.
    */
   public synchronized int extend(final GraphicsCommand c)
   {
      final int extensionStart = nCommands;
      if (c != null)
      {
         final int newNcommands = extensionStart + 1;
         if (newNcommands <= maxCommands)
         {
            command[extensionStart] = c;
            if (updated)
            {
               updateIndex = extensionStart;
               updated = false;
            }
            nCommands = newNcommands;
         }
         else
         {
            maxCommands = 2*newNcommands;
            final GraphicsCommand[] tmp = new GraphicsCommand[maxCommands];
            System.arraycopy(command, 0, tmp, 0, extensionStart);
            tmp[extensionStart] = c;
            command = tmp;
            if (updated)
            {
               updateIndex = extensionStart;
               updated = false;
            }
            nCommands = newNcommands;
         }
      }
      component.repaint(minRefreshInterval);
      return extensionStart;
   }
   
   /**
    * Changes the array of <TT>GraphicsCommand</TT>s to be executed by replacing elements
    * <TT>i</TT> onwards with the new ones.  There must be at least <TT>(i + c.length)</TT>
    * elements in the original array -- else this method will not change anything and will
    * return false.
    * The <TT>repaint</TT> method of the registered component is called to trigger
    * the {@link #update <TT>update</TT>} callback on this object.
    * All commands will be executed.
    * <P>
    *
    * @param c the new GraphicsCommands to be executed.
    * @param i the start index for the replacement.
    * @return true if and only if the changes are successfully made.
    */
   public boolean change(final GraphicsCommand[] c, final int i)
   {
      if (c != null)
      {
         final int n = c.length;
         if (n > 0)
         {
            if ((i + n) <= nCommands)
            {
               synchronized (this)
               {
                  System.arraycopy(c, 0, command, i, n);
                  updateIndex = 0;
                  refresh = true;
               }
            }
            else
            {
               return false;
            }
         }
      }
      component.repaint(minRefreshInterval);
      return true;
   }
   
   /**
    * Changes the array of <TT>GraphicsCommand</TT>s to be executed by replacing element
    * <TT>i</TT> with the new one.  There must be at least <TT>(i + 1)</TT>
    * elements in the original array -- else this method will not change anything and will
    * return false.
    * The <TT>repaint</TT> method of the registered component is called to trigger
    * the {@link #update <TT>update</TT>} callback on this object.
    * All commands will be executed.
    * <P>
    *
    * @param c the new GraphicsCommand to be executed.
    * @param i the index for the replacement.
    * @return true if and only if the change is successfully made.
    */
   public boolean change(final GraphicsCommand c, final int i)
   {
      if (c != null)
      {
         if (i < nCommands)
         {
            synchronized (this)
            {
               command[i] = c;
               updateIndex = 0;
               refresh = true;
            }
         }
         else
            return false;
      }
      component.repaint(minRefreshInterval);
      return true;
   }
   
   /**
    * Returns a copy of the array of <TT>GraphicsCommand</TT>s currently held.
    * <P>
    *
    * @return a copy of the array of <TT>GraphicsCommand</TT>s currently held.
    */
   public GraphicsCommand[] get()
   {
      final GraphicsCommand[] c = new GraphicsCommand[nCommands];
      synchronized (this)
      {
         System.arraycopy(command, 0, c, 0, nCommands);
      }
      return c;
   }
   
   /**
    * Sets the <TT>repaint</TT> interval invoked by the {@link #set <TT>set</TT>},
    * {@link #extend <TT>extend</TT>} and {@link #change <TT>change</TT>} commands.
    * The default is 10 milliseconds (the normal default for the
    * {@link java.awt.Component#repaint <TT>repaint</TT>} method from {@link java.awt.Component}).
    * <P>
    *
    * @param minRefreshInterval the display commands will be executed at most once
    * per <TT>minRefreshInterval</TT> milliseconds.
    */
   public void setMinRefreshInterval(final long minRefreshInterval)
   {
      this.minRefreshInterval = minRefreshInterval;
   }
   
   private void execute(int start, Graphics g)
   {
      for (int i = start; i < nCommands; i++)
      {
         final GraphicsCommand c = command[i];
         switch (c.tag)
         {
            case GraphicsCommand.NULL_TAG:
               // do nothing
               break;
            case GraphicsCommand.TRANSLATE:
               final GraphicsCommand.Translate t = (GraphicsCommand.Translate) c;
               g.translate(t.x, t.y);
               break;
            case GraphicsCommand.SET_COLOR:
               final GraphicsCommand.SetColor sc = (GraphicsCommand.SetColor) c;
               g.setColor(sc.c);
               break;
            case GraphicsCommand.SET_PAINT_MODE_TAG:
               g.setPaintMode();
               break;
            case GraphicsCommand.SET_XOR_MODE:
               final GraphicsCommand.SetXORMode sx = (GraphicsCommand.SetXORMode) c;
               g.setXORMode(sx.c);
               break;
            case GraphicsCommand.SET_FONT:
               final GraphicsCommand.SetFont sf = (GraphicsCommand.SetFont) c;
               g.setFont(sf.f);
               break;
            case GraphicsCommand.CLIP_RECT:
               final GraphicsCommand.ClipRect clr = (GraphicsCommand.ClipRect) c;
               g.clipRect(clr.x, clr.y, clr.width, clr.height);
               break;
            case GraphicsCommand.SET_CLIP:
               final GraphicsCommand.SetClip scl = (GraphicsCommand.SetClip) c;
               switch (scl.cliptag)
               {
                  case 0:
                     g.setClip(scl.x, scl.y, scl.width, scl.height);
                     break;
                  case 1:
                     g.setClip(scl.s);
                     break;
               }
               break;
            case GraphicsCommand.COPY_AREA:
               final GraphicsCommand.CopyArea ca = (GraphicsCommand.CopyArea) c;
               g.copyArea(ca.x, ca.y, ca.width, ca.height, ca.dx, ca.dy);
               break;
            case GraphicsCommand.DRAW_LINE:
               final GraphicsCommand.DrawLine dl = (GraphicsCommand.DrawLine) c;
               g.drawLine(dl.x1, dl.y1, dl.x2, dl.y2);
               break;
            case GraphicsCommand.FILL_RECT:
               final GraphicsCommand.FillRect fr = (GraphicsCommand.FillRect) c;
               g.fillRect(fr.x, fr.y, fr.width, fr.height);
               break;
            case GraphicsCommand.DRAW_RECT:
               final GraphicsCommand.DrawRect dr = (GraphicsCommand.DrawRect) c;
               g.drawRect(dr.x, dr.y, dr.width, dr.height);
               break;
            case GraphicsCommand.CLEAR_RECT:
               final GraphicsCommand.ClearRect cr = (GraphicsCommand.ClearRect) c;
               g.clearRect(cr.x, cr.y, cr.width, cr.height);
               break;
            case GraphicsCommand.DRAW_ROUND_RECT:
               final GraphicsCommand.DrawRoundRect drr = (GraphicsCommand.DrawRoundRect) c;
               g.drawRoundRect(drr.x, drr.y, drr.width, drr.height, drr.arcWidth, drr.arcHeight);
               break;
            case GraphicsCommand.FILL_ROUND_RECT:
               final GraphicsCommand.FillRoundRect frr = (GraphicsCommand.FillRoundRect) c;
               g.fillRoundRect(frr.x, frr.y, frr.width, frr.height, frr.arcWidth, frr.arcHeight);
               break;
            case GraphicsCommand.DRAW_3D_RECT:
               final GraphicsCommand.Draw3DRect d3r = (GraphicsCommand.Draw3DRect) c;
               g.draw3DRect(d3r.x, d3r.y, d3r.width, d3r.height, d3r.raised);
               break;
            case GraphicsCommand.FILL_3D_RECT:
               final GraphicsCommand.Fill3DRect f3r = (GraphicsCommand.Fill3DRect) c;
               g.fill3DRect(f3r.x, f3r.y, f3r.width, f3r.height, f3r.raised);
               break;
            case GraphicsCommand.DRAW_OVAL:
               final GraphicsCommand.DrawOval dov = (GraphicsCommand.DrawOval) c;
               g.drawOval(dov.x, dov.y, dov.width, dov.height);
               break;
            case GraphicsCommand.FILL_OVAL:
               final GraphicsCommand.FillOval fov = (GraphicsCommand.FillOval) c;
               g.fillOval(fov.x, fov.y, fov.width, fov.height);
               break;
            case GraphicsCommand.DRAW_ARC:
               final GraphicsCommand.DrawArc da = (GraphicsCommand.DrawArc) c;
               g.drawArc(da.x, da.y, da.width, da.height, da.startAngle, da.arcAngle);
               break;
            case GraphicsCommand.FILL_ARC:
               final GraphicsCommand.FillArc fa = (GraphicsCommand.FillArc) c;
               g.fillArc(fa.x, fa.y, fa.width, fa.height, fa.startAngle, fa.arcAngle);
               break;
            case GraphicsCommand.DRAW_POLYLINE:
               final GraphicsCommand.DrawPolyline dpl = (GraphicsCommand.DrawPolyline) c;
               g.drawPolyline(dpl.xPoints, dpl.yPoints, dpl.nPoints);
               break;
            case GraphicsCommand.DRAW_POLYGON:
               final GraphicsCommand.DrawPolygon dpg = (GraphicsCommand.DrawPolygon) c;
               switch (dpg.polytag)
               {
                  case 0:
                     g.drawPolygon(dpg.xPoints, dpg.yPoints, dpg.nPoints);
                     break;
                  case 1:
                     g.drawPolygon(dpg.p);
                     break;
               }
               break;
            case GraphicsCommand.FILL_POLYGON:
               final GraphicsCommand.FillPolygon fpg = (GraphicsCommand.FillPolygon) c;
               switch (fpg.polytag)
               {
                  case 0:
                     g.fillPolygon(fpg.xPoints, fpg.yPoints, fpg.nPoints);
                     break;
                  case 1:
                     g.fillPolygon(fpg.p);
                     break;
               }
               break;
            case GraphicsCommand.DRAW_STRING:
               final GraphicsCommand.DrawString ds = (GraphicsCommand.DrawString) c;
               g.drawString(ds.string, ds.x, ds.y);
               break;
            case GraphicsCommand.DRAW_CHARS:
               final GraphicsCommand.DrawChars dc = (GraphicsCommand.DrawChars) c;
               g.drawChars(dc.data, dc.offset, dc.length, dc.x, dc.y);
               break;
            case GraphicsCommand.DRAW_BYTES:
               final GraphicsCommand.DrawBytes db = (GraphicsCommand.DrawBytes) c;
               g.drawBytes(db.data, db.offset, db.length, db.x, db.y);
               break;
            case GraphicsCommand.DRAW_IMAGE:
               final GraphicsCommand.DrawImage di = (GraphicsCommand.DrawImage) c;
               switch (di.drawtag)
               {
                  case 0:
                     g.drawImage(di.image, di.x, di.y, component);
                     break;
                  case 1:
                     g.drawImage(di.image, di.x, di.y, di.width, di.height, component);
                     break;
                  case 2:
                     g.drawImage(di.image, di.x, di.y, di.bgcolor, component);
                     break;
                  case 3:
                     g.drawImage(di.image, di.x, di.y, di.width, di.height, di.bgcolor, component);
                     break;
                  case 4:
                     g.drawImage(di.image, di.dx1, di.dy1, di.dx2, di.dy2,
                             di.sx1, di.sy1, di.sx2, di.sy2, component);
                     break;
                  case 5:
                     g.drawImage(di.image, di.dx1, di.dy1, di.dx2, di.dy2,
                             di.sx1, di.sy1, di.sx2, di.sy2, di.bgcolor, component);
                     break;
               }
               break;
            case GraphicsCommand.GENERAL:
               final GraphicsCommand.General general = (GraphicsCommand.General) c;
               general.g.doGraphic(g, component);
               break;
         }
      }
   }
   
   /**
    * Register the <TT>Component</TT> that will delegate its <TT>paint</TT> and
    * <TT>update</TT> methods here.  Only the JCSP <TT>Active</TT> component should
    * perform this registration (in response to being passed this <TT>Paintable</TT>).
    * <P>
    *
    * @param c the Component that will do the delegating.
    */
   public synchronized void register(final Component c)
   {
      component = c;
   }
   
   /**
    * This is the call-back delegated here by the registered <TT>Component</TT>.
    * It will normally be the JVM <I>event thread</I> that is making this call.
    * <P>
    *
    * @param g the graphics context for the painting.
    */
   public synchronized void paint(final Graphics g)
   {
      final Dimension newSize = component.getSize();                
      if (image == null)
      {                                          
         size = newSize;                                              
         image = component.createImage(size.width, size.height);     
         imageGraphics = image.getGraphics();                        
      }
      else if ((newSize.width != size.width) || (newSize.height != size.height))
      {   
         size = newSize;                                              
         image = component.createImage(size.width, size.height);     
         imageGraphics = image.getGraphics();                        
      }                                                                     
      execute(0, imageGraphics);                                  
      g.drawImage(image, 0, 0, component);                          
      refresh = false;
   }
   
   /**
    * This is the call-back delegated here by the registered <TT>Component</TT>.
    * It will normally be the JVM <I>event thread</I> that is making this call.
    * <P>
    *
    * @param g the graphics context for the painting.
    */
   public synchronized void update(final Graphics g)
   {
      if (refresh)
      {
         final Dimension newSize = component.getSize();              
         if (image == null)
         {                                         
            size = newSize;                                            
            image = component.createImage(size.width, size.height);   
            imageGraphics = image.getGraphics();                      
         }
         else if ((newSize.width != size.width) || (newSize.height != size.height))
         {
            size = newSize;                                            
            image = component.createImage(size.width, size.height);   
            imageGraphics = image.getGraphics();                      
         }                                                            
         execute(0, imageGraphics);                                  
         g.drawImage(image, 0, 0, component); 
         refresh = false;
         updated = true;                                            
      }
      else
      {
         final Dimension newSize = component.getSize();              
         if (image == null)
         {                                         
            size = newSize;                                            
            image = component.createImage(size.width, size.height);   
            imageGraphics = image.getGraphics();                      
         }
         else if ((newSize.width != size.width) || (newSize.height != size.height))
         {   
            size = newSize;                                            
            image = component.createImage(size.width, size.height);   
            imageGraphics = image.getGraphics();                      
         }                                                            
         execute(updateIndex, imageGraphics);                        
         g.drawImage(image, 0, 0, component);                        
         updated = true;
      }
   }
}