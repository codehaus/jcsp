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

package org.jcsp.net.dynamic;

import java.io.IOException;
import java.util.Vector;

import org.jcsp.net.NetChannelLocation;
import org.jcsp.net.NetChannelOutput;
import org.jcsp.util.filter.Filter;
import org.jcsp.util.filter.FilteredChannelOutput;

/**
 * Implements of a migratable networked channel output end.
 *
 * @author Quickstone Technologies Limited
 */
class MigratableChannelOutputImpl implements MigratableChannelOutput
{
   /**
    * The output reconnection manager for the channel.
    */
   private OutputReconnectionManager mgr;
   
   /**
    * The actual networked output channel end.
    */
   private transient NetChannelOutput actualOut;
   
   /**
    * The filtered channel end.
    */
   private transient FilteredChannelOutput filteredOut;
   
   /**
    * The filters applied to the channel.
    */
   private Vector filters = null;
   
   /**
    * <code>MigratableChannelOutputImpl</code> objects constructed with
    * this constructor make use of the default channel name service.
    *
    * @param out the underlying networked channel output.
    */
   public MigratableChannelOutputImpl(NetChannelOutput out)
   {
      this(new OutputReconnectionManagerCNSImpl(out));
   }
   
   /**
    * Constructs a new <code>MigratableChannelOutputImpl</code> with the given reconnection manager.
    *
    * @param mgr the reconnection manager to use for the channel.
    */
   public MigratableChannelOutputImpl(OutputReconnectionManager mgr)
   {
      super();
      this.mgr = mgr;
      actualOut = mgr.getOutputChannel();
      if (actualOut == null)
         throw new NullPointerException("Output channel provided is null");
      filteredOut = MigratableAltingChannelInputImpl.FACTORY.createFiltered(actualOut);
   }
   
   /**
    * @see org.jcsp.net.dynamic.MigratableChannelOutput#prepareToMove()
    */
   public void prepareToMove()
   {
      mgr.prepareToMove();
   }
   
   /**
    * @see org.jcsp.net.NetChannelOutput#recreate()
    */
   public void recreate()
   {
      actualOut.recreate();
   }
   
   /**
    * @see org.jcsp.net.NetChannelOutput#recreate(NetChannelLocation)
    */
   public void recreate(NetChannelLocation newLoc)
   {
      actualOut.recreate(newLoc);
   }
   
   /**
    * @see org.jcsp.net.NetChannelOutput#destroyWriter()
    */
   public void destroyWriter()
   {
      actualOut.destroyWriter();
   }
   
   /**
    * @see org.jcsp.lang.ChannelOutput#write(Object)
    */
   public void write(Object object)
   {
      if(actualOut == null)
      {
         actualOut = mgr.getOutputChannel();
         if (actualOut == null)
            throw new NullPointerException("Output channel provided is null");
         filteredOut = MigratableAltingChannelInputImpl.FACTORY.createFiltered(actualOut);
      }
      filteredOut.write(object);
   }
   
   /**
    * @see org.jcsp.net.Networked#getChannelLocation()
    */
   public NetChannelLocation getChannelLocation()
   {
      return actualOut.getChannelLocation();
   }
   
   public Class getFactoryClass()
   {
      return null;
   }
   
   /**
    * @see org.jcsp.util.filter.WriteFiltered#addWriteFilter(Filter)
    */
   public void addWriteFilter(Filter filter)
   {
      if (filteredOut == null && filters != null)
         filters.addElement(filter);
      filteredOut.addWriteFilter(filter);
   }
   
   /**
    * @see org.jcsp.util.filter.WriteFiltered#addWriteFilter(Filter, int)
    */
   public void addWriteFilter(Filter filter, int index)
   {
      if (filteredOut == null && filters != null)
         filters.add(index, filter);
      filteredOut.addWriteFilter(filter, index);
   }
   
   /**
    * @see org.jcsp.util.filter.WriteFiltered#removeWriteFilter(Filter)
    */
   public void removeWriteFilter(Filter filter)
   {
      if (filteredOut == null && filters != null)
         filters.remove(filter);
      filteredOut.removeWriteFilter(filter);
   }
   
   /**
    * @see org.jcsp.util.filter.WriteFiltered#removeWriteFilter(int)
    */
   public void removeWriteFilter(int index)
   {
      if (filteredOut == null && filters != null)
         filters.remove(index);
      filteredOut.removeWriteFilter(index);
   }
   
   /**
    * @see org.jcsp.util.filter.WriteFiltered#getWriteFilter(int)
    */
   public Filter getWriteFilter(int index)
   {
      if (filteredOut == null && filters != null)
         return (Filter)filters.elementAt(index);
      return filteredOut.getWriteFilter(index);
   }
   
   /**
    * @see org.jcsp.util.filter.WriteFiltered#getWriteFilterCount()
    */
   public int getWriteFilterCount()
   {
      if (filteredOut == null && filters != null)
         return filters.size();
      return filteredOut.getWriteFilterCount();
   }
   
   private void writeObject(java.io.ObjectOutputStream out) throws IOException
   {
      //the filtered channel input object used by this class is a
      //transient field. This code extracts the filters from the
      //filter channel input and stores them into a Filter[] field.
      //They are resinserted into a new filtered channel input on the
      //first call to read()
      if (filteredOut != null)
      {
         filters = new Vector(filteredOut.getWriteFilterCount());
         for (int i=0; i < filteredOut.getWriteFilterCount(); i++)
            filters.addElement(filteredOut.getWriteFilter(i));
      }
      
      //writes all non-transient variables to the output stream
      out.defaultWriteObject();
   }
   
   /**
    * Currently, network channels are unpoisonable so this method has no effect.
    */
   public void poison(int strength) {   
   }   
}