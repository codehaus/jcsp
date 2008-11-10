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
import java.awt.image.*;
import org.jcsp.lang.TaggedProtocol;

/**
 * This is the tagged protocol for use over the <TT>toGraphics</TT>
 * configuration channel of an <I>active</I> graphics component.
 * <H2>Description</H2>
 * <TT>GraphicsProtocol</TT> is the tagged protocol for use over the <TT>toGraphics</TT>
 * configuration channel of an <I>active</I> graphics component (for example, see
 * {@link ActiveCanvas#setGraphicsChannels <TT>ActiveCanvas.setGraphicsChannels</TT>}).
 * <P>
 * It is a collection of static immutable <I>enquiry/configuration</I>
 * objects and static <I>configuration</I> classes.  An enquiry sent down the <TT>toGraphics</TT>
 * channel generates an appropriate reply on the <TT>fromGraphics</TT> channel.
 * A configuration class may be used to construct an immutable configuration object
 * that may be sent down the <TT>toGraphics</TT> channel to configure some property of the graphics
 * component.  A configuration message is always acknowledged with a <TT>Boolean.TRUE</TT>
 * returned on the <TT>fromGraphics</TT> channel (once the configuration has happened).
 *
 * @see org.jcsp.awt.ActiveCanvas
 * @see org.jcsp.lang.TaggedProtocol
 *
 * @author P.H. Welch
 */

public abstract class GraphicsProtocol extends TaggedProtocol
{
   
   GraphicsProtocol(final int tag)
   {
      super(tag);
   }
   
   static final int GET_DIMENSION_TAG = 0;
   static final int GET_COMPONENT_TAG = 1;
   static final int GET_BACKGROUND_TAG = 2;
   static final int SET_BACKGROUND_TAG = 3;
   static final int REQUEST_FOCUS_TAG = 4;
   static final int MAKE_MIS_IMAGE_TAG = 5;
   static final int SET_PAINTABLE_TAG = 6;
   static final int GENERAL_TAG = 7;
   
   static private final class GetDimension extends GraphicsProtocol
   {
      public GetDimension()
      {
         super(GET_DIMENSION_TAG);
      }
   }
   
   /**
    * This is an enquiry object to find the size of the active graphics component.
    * The <TT>java.awt.Dimension</TT>
    * will be returned down the <TT>fromGraphics</TT> channel.
    */
   static public final GraphicsProtocol GET_DIMENSION = new GetDimension();
   
   static private final class GetComponent extends GraphicsProtocol
   {
      public GetComponent()
      {
         super(GET_COMPONENT_TAG);
      }
   }
   
   /**
    * This is an enquiry object to obtain the active graphics component.
    * The <TT>java.awt.Component</TT>
    * will be returned down the <TT>fromGraphics</TT> channel.
    */
   static public final GraphicsProtocol GET_COMPONENT = new GetComponent();
   
   static private final class GetBackground extends GraphicsProtocol
   {
      public GetBackground()
      {
         super(GET_BACKGROUND_TAG);
      }
   }
   
   /**
    * This is an enquiry object to find the background colour of the active graphics component.
    * The background <TT>java.awt.Color</TT>
    * will be returned down the <TT>fromGraphics</TT> channel.
    */
   static public final GraphicsProtocol GET_BACKGROUND = new GetBackground();
   
   /**
    * This is a configuration class for setting the background <TT>java.awt.Color</TT>
    * of the active graphics component.  A <TT>Boolean.TRUE</TT> will be returned down
    * the <TT>fromGraphics</TT> channel.
    */
   static public final class SetBackground extends GraphicsProtocol
   {
      final Color color;
      
      /**
       *
       * @param color the new background colour for the graphics component.
       */
      public SetBackground(final Color color)
      {
         super(SET_BACKGROUND_TAG);
         this.color = color;
      }
   }
   
   static private final class RequestFocus extends GraphicsProtocol
   {
      public RequestFocus()
      {
         super(REQUEST_FOCUS_TAG);
      }
   }
   
   /**
    * This is a configuration object to request input focus on the graphics component
    * for keyboard and mouse evnts.  A <TT>Boolean.TRUE</TT> will be returned down
    * the <TT>fromGraphics</TT> channel.
    */
   static public final GraphicsProtocol REQUEST_FOCUS = new RequestFocus();
   
   /**
    * This is a configuration class to associate a <TT>java.awt.image.MemoryImageSource</TT>
    * with the graphics component.  The component uses this to create a <TT>java.awt.Image</TT>,
    * which is returned down the <TT>fromGraphics</TT> channel.
    */
   static public final class MakeMISImage extends GraphicsProtocol
   {
      final MemoryImageSource mis;
      
      /**
       * @param mis the MemoryImageSource for the graphics component.
       */
      public MakeMISImage(final MemoryImageSource mis)
      {
         super(MAKE_MIS_IMAGE_TAG);
         this.mis = mis;
      }
   }
   
   /**
    * This is a configuration class to register a <TT>org.jcsp.awt.Paintable</TT> object
    * with the graphics component.  A <TT>Boolean.TRUE</TT> will be returned down
    * the <TT>fromGraphics</TT> channel.
    */
   static public final class SetPaintable extends GraphicsProtocol
   {
      final Paintable paintable;
      
      /**
       * @param paintable the Paintable object to which the graphics component
       * will delegate its paint/update methods.
       */
      public SetPaintable(final Paintable paintable)
      {
         super(SET_PAINTABLE_TAG);
         this.paintable = paintable;
      }
   }
   
   /**
    * This is the interface for general configuration of the graphics component.
    * See the {@link GraphicsProtocol.General <TT>General</TT>} class.
    */
   static public interface Configure
   {
      /**
       * @param c the Component being configured.
       */
      public Object configure(final Component c);
   }
   
   /**
    * This is a general configuration class for the graphics component.
    * The user constructs an instance of this class, supplying an object
    * implementing the {@link GraphicsProtocol.Configure <TT>Configure</TT>}
    * interface, and writes it down the <TT>toGraphics</TT> channel.
    * The graphics component invokes the <TT>configure</TT> <I>method</I>
    * of the enclosed <TT>Configure</TT> <I>object</I> and
    * returns the <TT>Object</TT> result down the <TT>fromGraphics</TT> channel.
    */
   static public final class General extends GraphicsProtocol
   {
      final Configure c;
      
      /**
       * @param c the object implementing the user's configuration requirements
       * for the graphics component.
       */
      public General(final Configure c)
      {
         super(GENERAL_TAG);
         this.c = c;
      }
   } 
}