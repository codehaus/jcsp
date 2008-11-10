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
import org.jcsp.net.*;
import org.jcsp.net.cns.*;
import org.jcsp.lang.*;

/**
 * <p>Program to run a Channel Name Service. The service will listen on all locally available
 * addresses using either a default port of 7890 or a port specified in the XML file.</p>
 *
 * @author Quickstone Technologies Limited
 */
public class TCPIPCNSServer2
{
   /**
    * For use by other sub classes of this class to terminate the server. To terminate the server write a null
    * to this channel. This gets used in the NT service implementation to response to the STOP event.
    */
   static final One2OneChannel terminate = Channel.one2one();
   
   private TCPIPCNSServer2()
   {
   }
   
   /**
    * Main method, running the service. This will never terminate if the service can be started.
    */
   public static void main(String[] args)
   {
      Node.info.log(TCPIPCNSServer2.class, "Starting CNS server");
      try
      {
         NodeKey key = Node.getInstance().init(new XMLNodeFactory("JCSPNetCNSService.xml"));
         ServiceManager sm = Node.getInstance().getServiceManager(key);
         CNS cns = new CNS(key);
         if (sm.installService(cns, "Channel Name Server") && sm.startService("Channel Name Server"))
            Node.info.log(TCPIPCNSServer2.class, "CNS Started");
         else
            Node.info.log(TCPIPCNSServer2.class, "CNS failed to start");
      }
      catch (NodeInitFailedException e)
      {
         e.printStackTrace();
         return;
      }
      catch (IOException e)
      {
         Node.info.log(TCPIPCNSServer2.class,"XML file not found");
         return;
      }
      Node.info.log(TCPIPCNSServer2.class,"CNS server running on " + Node.getInstance().getNodeID());
      // sleep forever (or until terminated)
      terminate.in().read();
   }
}