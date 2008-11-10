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

import java.io.*;
import org.jcsp.lang.*;
import org.jcsp.net.*;
import org.jcsp.util.filter.*;

/**
 * A service implementation for supporting dynamic class transfer at a node. When started, JFTP server
 * and <code>ClassMananger</code> processes will be spawned and send/receive filters created. To support
 * dynamic class loading over a given channel, plug the TX filter into the sending end and the RX filter
 * into the receiving end. The channel can then be used normally.
 *
 * @author Quickstone Technologies Limited
 */
public class DynamicClassLoader implements Service
{
   /**
    * Starts the dynamic loader service by spawning <code>ClassManager</code> and <code>JFTP</code>
    * processes.
    *
    * @return true if the service started successfully.
    */
   public boolean start()
   {
      cm = new ClassManager();
      new ProcessManager(cm).start();
      channelRxFilter = new DeserializeChannelFilter(cm);
      NetAltingChannelInput requests = NetChannelEnd.createNet2One();
      JFTP localFileService = new JFTP(ClassLoader.getSystemClassLoader(), requests, cm);
      new ProcessManager(localFileService).start();
      this.channelTxFilter = new DataSerializationFilter(requests.getChannelLocation());
      running = true;
      Node.info.log(this, "Dynamic Class Loading Service Started");
      return running;
   }
   
   /**
    * Stops the dynamic loader service.
    */
   public boolean stop()
   {
      return false;
   }
   
   /**
    * Initializes the dynamic loader service.
    */
   public boolean init(ServiceSettings settings)
   {
      return true;
   }
   
   /**
    * Returns true iff the service is running.
    */
   public boolean isRunning()
   {
      return running;
   }
   
   /**
    * Returns the service user object.
    */
   public ServiceUserObject getUserObject() throws SecurityException
   {
      return new DynamicClassLoaderUserObject();
   }
   
   /**
    * Returns the TX filter to be used at the sending end of a channel if dynamic class loading is to
    * be supported over that channel. If there is a TX filter at the sending end, there must be a RX
    * filter at the receiving end.
    */
   public Filter getChannelTxFilter()
   {
      return channelTxFilter;
   }
   
   /**
    * Returns the RX filter to be used at the receiving end of a channel if dynamic class loading is to
    * be supported over that channel. The filter returned is safe to use if there is no TX filter at
    * the sending end.
    */
   public Filter getChannelRxFilter()
   {
      return channelRxFilter;
   }
   
   /**
    * An alternative RX filter that does not support dynamic class loading but will properly
    * unmarshal objects wrapped up by a TX filter.
    */
   public static Filter getNonDynamicClassLoadingRxFilter()
   {
      return nonDynamicClassLoadingRxFilter;
   }
   
   /**
    * User interface to obtain the TX and RX filters from the service once it has been started.
    */
   public class DynamicClassLoaderUserObject implements ServiceUserObject
   {
      /**
       * Constructs a new <code>DynamicClassLoaderUserObject</code>.
       */
      private DynamicClassLoaderUserObject()
      {
      }
      
      /**
       * Returns the TX filter to be used at the sending end of a channel if dynamic class laoding is
       * to be supported over that channel. If there is a TX filter at the sending end, there must be
       * a RX filter at the receiving end.
       */
      public Filter getChannelTxFilter()
      {
         return channelTxFilter;
      }
      
      /**
       * Returns the RX filter to be used at the receiving end of a channel if dynamic class loading
       * is to be supported over that channel. The filter returned is safe to use if there is no
       * TX filter at the sending end.
       */
      public Filter getChannelRxFilter()
      {
         return channelRxFilter;
      }
   }
   
   /**
    * Stores the current state of the service.
    */
   private boolean running = false;
   
   /**
    * The TX filter created when the service started, returned by <code>getChannelTxFilter</code>.
    */
   private DataSerializationFilter channelTxFilter = null;
   
   /**
    * The RX filter created when the service started, returned by <code>getChannelRxFilter</code>.
    */
   private DeserializeChannelFilter channelRxFilter = null;
   
   /**
    * The class manager process started by the service.
    */
   private ClassManager cm;
   
   /**
    * Default service name.
    */
   public static final String name = "dynamic_class_loading";
   
   /**
    * The alternative RX filter that does not support dynamic class loading, returned by
    * <code>getNonDynamicClassLoadingRxFilter</code>.
    */
   private static Filter nonDynamicClassLoadingRxFilter = new Filter()
                                                         {
                                                            public Object filter(Object data)
                                                            {
                                                               if (data instanceof DynamicClassLoaderMessage)
                                                               {
                                                                  try
                                                                  {
                                                                     return ((DynamicClassLoaderMessage) data).get(null);
                                                                  }
                                                                  catch (ClassNotFoundException e)
                                                                  {
                                                                     Node.err.log(this, e);
                                                                  }
                                                                  catch (IOException e)
                                                                  {
                                                                     Node.err.log(this, e);
                                                                  }
                                                               }
                                                               return data;
                                                            }
                                                         };
}