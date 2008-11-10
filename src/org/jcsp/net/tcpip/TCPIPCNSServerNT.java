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

import org.jcsp.win32.*;

/**
 * Wraps up the TCPIPCNSServer as a Windows NT service. To install the service, register the
 * path <code>java org.jcsp.net.tcpip.TCPIPCNSServerNT</code> with the service name
 * <code>JCSP.NET:TCPIPCNSServer</code>.</p>
 *
 * @author Quickstone Technologies Limited
 */
public class TCPIPCNSServerNT extends NTService
{
   /**
    * Starts the service by calling <code>TCPIPCNSServer.main</code>.
    */
   protected void startService()
   {
      TCPIPCNSServer2.main(new String[0]);
      // will block
   }
   
   /**
    * Stops the service.
    */
   protected void stopService()
   {
      TCPIPCNSServer2.terminate.out().write(null);
   }
   
   /**
    * Creates a new service instance.
    */
   private TCPIPCNSServerNT()
   {
      super("JCSP.NET:TCPIPCNSServer");
   }
   
   /**
    * Creates a new service instance and sets it running.
    */
   public static void main(String[] args)
   {
      new TCPIPCNSServerNT().run();
   }
}