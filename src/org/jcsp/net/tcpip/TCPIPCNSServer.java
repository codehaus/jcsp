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

package org.jcsp.net.tcpip;

import java.io.*;
import java.net.*;
import org.jcsp.net.*;
import org.jcsp.net.cns.*;
import org.jcsp.lang.*;

/**
 * <p>Program to run a Channel Name Service. The service will listen on all locally available
 * addresses using either a default port of 7890 or a port specified by the first parameter on the
 * command line.</p>
 *
 * @author Quickstone Technologies Limited
 */
public class TCPIPCNSServer
{
   /**
    * For use by other sub classes of this class to terminate the server. To terminate the server write a null
    * to this channel. This gets used in the NT service implementation to response to the STOP event.
    */
   static final One2OneChannel terminate = Channel.one2one();
   
   private TCPIPCNSServer()
   {
   }
   
   /**
    * Main method, running the service. This will never terminate if the service can be started.
    */
   public static void main(String[] args)
   {
      // Parse args
      int port = DEFAULT_CNS_PORT;
      if (args.length == 1)
      {
         try
         {
            port = Integer.parseInt(args[0]);
            if ((port > 0) && (port <= 0xFFFF))
               // Valid port
               args = new String[0];
         }
         catch (NumberFormatException ex)
         {
            // do nothing
         }
      }
      if (args.length > 0)
      {
         System.err.println("Usage: java org.jcsp.net.tcpip.TCPIPCNSServer [port]\n" + 
                            "where 0<port<65536, default port=" + DEFAULT_CNS_PORT);
         return;
      }
      
      Node.info.log(TCPIPCNSServer.class, "Starting CNS server on port " + port + ".");
      try
      {
         InetAddress[] allLocal = InetAddress.getAllByName(InetAddress.getLocalHost().getHostName());
         TCPIPAddressID[] localAddressIDs = new TCPIPAddressID[allLocal.length];
         int nullCount = 0;
         for (int i = 0; i < allLocal.length; i++)
         {
            try
            {
               localAddressIDs[i] =
                       new TCPIPAddressID(allLocal[i], port, true);
            }
            catch (Exception e)
            {
               Node.err.log(TCPIPCNSServer.class, "Cannot listen on " + allLocal[i] + ":" + port);
               nullCount++;
            }
         }
         
         if (nullCount > 0)
         {
            TCPIPAddressID[] localAddressIDsCopy = new TCPIPAddressID[localAddressIDs.length - nullCount];
            int copyPointer = 0;
            for (int i = 0; i < localAddressIDs.length; i++)
            {
               if (localAddressIDs[i] != null)
               {
                  localAddressIDsCopy[copyPointer] = localAddressIDs[i];
                  copyPointer++;
               }
            }
            localAddressIDs = localAddressIDsCopy;
         }
         
         try
         {
            NodeKey key = Node.getInstance().init(localAddressIDs);
            ServiceManager sm = Node.getInstance().getServiceManager(key);
            CNS cns = new CNS(key);
            if (sm.installService(cns, "Channel Name Server") && sm.startService("Channel Name Server"))
               Node.info.log(TCPIPCNSServer.class, "CNS Started");
            else
               Node.info.log(TCPIPCNSServer.class, "CNS failed to start");
         }
         catch (NodeInitFailedException e)
         {
            e.printStackTrace();
         }
      }
      catch (IOException e)
      {
         Node.err.log(TCPIPCNSServer.class, "Cannot start CNS server - port is probably in use.");
         Node.err.log(TCPIPCNSServer.class, e);
         System.exit(1);
      }
      Node.info.log(TCPIPCNSServer.class, "CNS server running on " + Node.getInstance().getNodeID());
      // sleep forever (or until terminated)
      terminate.in().read();
   }
   
   /**
    * The default port number for a Channel Name Server. This value will be used by default if none
    * is specified on the command line. When locating a CNS the TCPIPNodeFactory will also use this
    * default value if none is supplied to it.
    */
   public static final int DEFAULT_CNS_PORT = 7890;
}