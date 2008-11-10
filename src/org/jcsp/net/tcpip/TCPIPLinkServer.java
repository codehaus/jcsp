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

import java.net.*;
import java.io.*;
import org.jcsp.lang.*;
import org.jcsp.net.*;

/**
 * A process to accept links to a ServerSocket, create appropriate
 * TCPIPLink objects, and register them with the LinkManager.
 * <p>
 *
 * This is a package-private implementation class.
 *
 * @author Quickstone Technologies Limited
 */
// package-private.
class TCPIPLinkServer extends LinkServer implements CSProcess
{
   /*----------------------Constructors------------------------------------------*/
   
   /**
    * Private constructor.  To make life easier for you, you don't have to
    * call this.  Just use the static start() method in this class and a
    * process will be created and executed for you.
    *
    * @see #start(ServerSocket)
    *
    * @param socket The ServerSocket to accept from
    */
   private TCPIPLinkServer(ServerSocket serverSocket, boolean uniqueAddress)
   {
      super(new TCPIPProtocolID(), new TCPIPAddressID(serverSocket.getInetAddress(), serverSocket.getLocalPort(),uniqueAddress));
      this.serverSocket = serverSocket;
   }
   
   /*----------------------Public Methods----------------------------------------*/
   
   /**
    * Start accepting links and dealing with them.
    * This method runs forever.
    */
   public void run()
   {
      try
      {
         Node.info.log(this, "TCP/IP V4 LinkServer listening on " + getLinkServerAddressID() + " Started");
         while (true)
         {
            // Accept an incoming link
            Socket incoming = serverSocket.accept();
            // Create a Link object to represent it.
            TCPIPLink link = new TCPIPLink(incoming, false);
            // spawn off the Link object to deal with it, but
            // let us continue in parallel.
            new ProcessManager(link).start();
         }
      }
      catch (Exception ex)
      {
         // Will be IOException
         // warn but otherwise ignore
         Node.err.log(this, ex);
      }
      try
      {
         serverSocket.close();
      }
      catch (Exception ignored)
      {
      }
      Node.info.log(this, "TCP/IP V4 LinkServer listening on " + getLinkServerAddressID() + " Ended");
   }
   
   /*----------------------Non-public Methods------------------------------------*/
   
   /**
    * Create a server on a specified NodeAddressID, and start it.  The server
    * is spawned off in parallel, so this call returns immediately.
    *
    * This NEEDS to be overridden.
    *
    * @param addressID The NodeAddressID to accept from
    */
   // package-private
   protected static LinkServer create(NodeAddressID addressID)
   {
      if(!(addressID instanceof TCPIPAddressID))
         throw new IllegalArgumentException("Unable to start TCPIPLinkServer, wrong type of address.");
      TCPIPAddressID add = (TCPIPAddressID) addressID;
      InetAddress ipAdd = add.getHost();
      int port = add.getPort();
      TCPIPLinkServer ls;
      try
      {
         ServerSocket serverSocket = new ServerSocket(port, QUEUE_LENGTH, ipAdd);
         ls = new TCPIPLinkServer(serverSocket, addressID.isGloballyUnique());
         new ProcessManager(ls).start(ProcessManager.PRIORITY_MAX);
      }
      catch(IOException e)
      {
         Node.info.log(TCPIPLinkServer.class, e.getMessage());
         ls = null;
      }
      return ls;
   }
   
   /**
    * Stops the LinkServer.
    *
    * This NEEDS to be overridden.
    */
   protected boolean stop()
   {
      try
      {
         Node.info.log(this, "Trying to stop TCP/IP V4 LinkServer listening on " + getLinkServerAddressID());
         serverSocket.close();
      }
      catch(IOException e)
      {
         e.printStackTrace();
      }
      return true;
   }
   
   /*----------------------Attributes--------------------------------------------*/
   
   /**
    * The socket to accept from.
    */
   private final ServerSocket serverSocket;
   private static int QUEUE_LENGTH = 10;
}