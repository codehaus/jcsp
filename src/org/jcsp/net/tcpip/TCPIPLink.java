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
 * Implements a link running over TCP/IP sockets. See the definition of <code>Link</code> for
 * full details.
 *
 * @see org.jcsp.net.Link
 *
 * @author Quickstone Technologies Limited
 */
class TCPIPLink extends Link
{
   /*----------------------Constructors------------------------------------------*/
   
   /**
    * Constructor for using an existing socket.
    *
    * @param socket The socket to the remote computer, which must already be
    *               open and active.  You must not have transmitted anything
    *               over the socket.
    */
   // package-private
   TCPIPLink(Socket socket)
   {
      this(socket, true);
   }
   /**
    * Constructor for using an existing socket.
    *
    * @param socket The socket to the remote computer, which must already be
    *               open and active.  You must not have transmitted anything
    *               over the socket.
    *
    * @param client Tells the link whether to act as a client or a server
    *        during the handshake process
    */
   
   // package-private - called from LinkServer with client false
   TCPIPLink(Socket socket, boolean client)
   {
      super(new TCPIPProtocolID(), client, true);
      this.socket = socket;
   }
   
   /**
    * Constructor for connecting to a remote computer.  The actual connect
    * happens when you run the process using start().
    *
    * @param remoteTCPIPAddress The remote computer to connect to.
    */
   // package-private - called from LinkManager
   TCPIPLink(TCPIPAddressID remoteTCPIPAddress)
   {
      super(new TCPIPProtocolID(), true, false);
      this.remoteTCPIPAddress = remoteTCPIPAddress;
   }
   
   /*-------------Implementations of Methods from Link---------------------------*/
   
   /**
    * Run the send and receive threads to marshall and unmarshall objects.
    */
   protected void runTxRxLoop()
   {
      Parallel par = new Parallel( new CSProcess[] {new RxLoop(), new TxLoop()});
      par.run();
      par.releaseAllThreads();
   }
   
   /**
    * Waits for <code>numRepliesOutstanding</code> instances of <code>LinkLost</code>
    * to arrive from the <code>txChannel</code>.
    *
    * @param numRepliesOutstanding <code>LinkLost</code> instances to wait for.
    */
   protected void waitForReplies(int numRepliesOutstanding)
   {
      ChannelInput in = txChannel.in();
      while (numRepliesOutstanding > 0)
      {
         Object obj = in.read();
         if (obj instanceof LinkLost)
            numRepliesOutstanding--;
      }
   }
   
   /**
    * Start this Link but allow the caller to continue in parallel.
    * This is the similar to "new ProcessManager(this).start()" - except
    * that Link no longer implements CSProcess, so you can't do
    * that.
    */
   // package-private - called from LinkManager & LinkServer
   protected void start(boolean newProcess)
   {
      ProcessManager pm = new ProcessManager(this);
      pm.setPriority(ProcessManager.PRIORITY_MAX);
      if(newProcess)
         pm.start();
      else
         pm.run();
   }
   
   /**
    * Connect to remote host.  Should only be called for client-side
    * Links which have not yet been connected.  (i.e. where
    * socket==null and remoteAddress != null).
    *
    * @return true on success, false on failure.
    */
   protected boolean connect()
   {
      try
      {
         socket = new Socket(remoteTCPIPAddress.getHost(), remoteTCPIPAddress.getPort());
      }
      catch (SecurityException ex)
      {
         ex.printStackTrace();
         return false; // Give up
      }
      catch (ConnectException ex)
      {
         ex.printStackTrace();
         return false; // Give up
      }
      catch (NoRouteToHostException ex)
      {
         ex.printStackTrace();
         return false; // Give up
      }
      catch (IOException ex)
      {
         ex.printStackTrace();
         return false; // Give up
      }
      
      return true;
   }
   
   /**
    * Create the object streams used to communicate with the peer system. Called internally by
    * the superclass.
    *
    * @return true on success, false on failure
    */
   protected boolean createResources()
   {
      // Create the object streams and do the first part of the handshaking.
      Parallel par = new Parallel( new CSProcess[] {
         new CreateRxStream(),
         new CreateTxStream(),
      });
      par.run();
      par.removeAllProcesses();
      par.releaseAllThreads();
      // check whether it worked.
      if ((rxStream == null) || (txStream == null))
      {
         // Object Stream creation failed.
         Node.err.log(this, "Object Stream creation failed.");
         destroyResources();
         
         // We've already displayed an error message (or two), so just exit
         return false;
      }
      // Ok
      return true;
   }
   
   /**
    * Performs the node exchange part of the handshaking process. Creates a process to
    * send this <code>NodeID</code> and a process to receive the peer node ID. Called internally
    * by the superclass.
    *
    * @return true on success, false on failure
    */
   protected boolean exchangeNodeIDs()
   {
      // Do the second part of the handshaking.
      Parallel par = new Parallel(new CSProcess[] { new RxId(), new TxId() });
      par.run();
      par.releaseAllThreads();
      // check whether it worked.
      if ((rxStream == null) || (txStream == null))
      {
         Node.err.log(this, "Error during handshaking (stage2)");
         return false;
      }
      return true;
   }
   
   /**
    * Writes a test object to the output stream, flushing and resetting the stream afterwards.
    * Called internally by the superclass during the testing stage to determine link speed. The parameter passed
    * to this method must be returned by the <code>readTestObject</code> method at the peer
    * node.
    *
    * @param obj Object to be sent
    */
   protected void writeTestObject(Object obj) throws Exception
   {
      txStream.writeObject(obj);
      txStream.flush();
      txStream.reset();
   }
   
   /**
    * Reads a test object from the input stream. Called internally by the superclass during the testing stage to
    * determine link speed. Returns the parameter passed to <code>writeTestObject</code> at the
    * peer node.
    *
    * @return the object received.
    */
   protected Object readTestObject() throws Exception
   {
      return rxStream.readObject();
   }
   
   /**
    * Writes a boolean link decision value to the output stream. Called internally by the superclass during the
    * handshaking sequence. The parameter passed to this method must be returned by the
    * <code>readLinkDecision</code> method at the peer node.
    *
    * @param use boolean decision value to send.
    */
   protected void writeLinkDecision(boolean use) throws Exception
   {
      txStream.writeObject(use ? Boolean.TRUE : Boolean.FALSE);
      txStream.flush();
   }
   
   /**
    * Reads a boolean link decision from the input stream. Called internally during the
    * handshaking sequence by the superclass. Returns the parameter passed to <code>writeLinkDecision</code>
    * at the peer node.
    *
    * @return the boolean decision.
    */
   protected boolean readLinkDecision() throws Exception
   {
      return ((Boolean)rxStream.readObject()).booleanValue();
   }
   
   /**
    * Closes the streams and the socket, if needed.  Surpresses errors.
    */
   protected void destroyResources()
   {
      if (txStream != null)
      {
         try
         {
            txStream.close();
         }
         catch (Exception ignored)
         {
         }
         txStream = null;
      }
      closeRx();
   }
   
   /*-------------------Private Methods------------------------------------------*/
   
   /**
    * Closes the Rx stream and the socket, if needed.  Surpresses errors.
    */
   private void closeRx()
   {
      if (rxStream != null)
      {
         try
         {
            rxStream.close();
         }
         catch (Exception ignored)
         {
         }
         rxStream = null;
      }
      closeSocket();
   }
   
   /**
    * Closes the Tx stream and the socket, if needed.  Surpresses errors.
    */
   private void closeTx()
   {
      if (txStream != null)
      {
         try
         {
            txStream.close();
         }
         catch (Exception ignored)
         {
         }
         txStream = null;
      }
      closeSocket();
   }
   
   /**
    * Closes the socket, if needed.  Surpresses errors.
    */
   private void closeSocket()
   {
      if (socket != null)
      {
         try
         {
            socket.close();
         }
         catch (Exception ignored)
         {
         }
         socket = null;
      }
   }
   
   /*----------------------Attributes--------------------------------------------*/
   
   /**
    * The other computer's IP address and port.
    */
   private TCPIPAddressID remoteTCPIPAddress;
   
   /**
    * The actual socket.
    */
   private Socket socket;
   
   /**
    * The stream for reading from the socket.
    */
   private ObjectInputStream rxStream;
   
   /**
    * The stream for reading from the socket.
    */
   private ObjectOutputStream txStream;
   
   /**
    * Handshaking string.
    */
   private static String PROTOCOL_IDENTIFIER = "JCSP.net version 0.1";
   
   /**
    * Size of Java buffers.  Note that there are also OS buffers,
    * set using {@link java.net.Socket#setReceiveBufferSize(int)} and
    * {@link java.net.Socket#setSendBufferSize(int)}.
    */
   private static final int BUFFER_SIZE = 8192;
   
   /*----------------------Private Inner Classes---------------------------------*/
   
   /**
    * The process which creates txStream.  It creates an object stream
    * associated with the socket, and sends the Java object stream header
    * and the PROTOCOL_IDENTIFIER.  On success, it sets txStream in the
    * outer class to a non-null value.  If there is an exception, it
    * closes the socket and returns without modifying txStream.
    *
    * @see #txStream
    */
   private class CreateTxStream implements CSProcess
   {
      /**
       * The run method.
       */
      public void run()
      {
         // This is what we close() if we catch an exception.
         OutputStream rawTxStream = null;
         try
         {
            rawTxStream = socket.getOutputStream();
            socket.setTcpNoDelay(true);
            rawTxStream = new BufferedOutputStream(rawTxStream,BUFFER_SIZE);
            ObjectOutputStream objTxStream = new ObjectOutputStream(rawTxStream);
            objTxStream.writeObject(PROTOCOL_IDENTIFIER);
            objTxStream.flush();
            objTxStream.reset();
            txStream = objTxStream;
            return;
         }
         catch (Exception ex)
         {
            ex.printStackTrace();
            if (rawTxStream != null)
            {
               try
               {
                  rawTxStream.close();
               }
               catch (Exception ignored)
               {
               }
            }
            closeSocket();
         }
      }
   }
   
   /**
    * The process which creates rxStream.  It creates an object stream
    * associated with the socket, and listens for the Java object stream
    * header and the PROTOCOL_IDENTIFIER.  On success, it sets rxStream in
    * the outer class to a non-null value.  If there is an exception, or
    * if the object recieved is not PROTOCOL_IDENTIFIER, it closes the
    * socket and returns without modifying rxStream.
    *
    * @see #rxStream
    */
   private class CreateRxStream implements CSProcess
   {
      /**
       * The run method.
       */
      public void run()
      {
         // This is what we close() if we catch an exception.
         InputStream rawRxStream = null;
         try
         {
            rawRxStream = socket.getInputStream();
            rawRxStream = new BufferedInputStream(rawRxStream, BUFFER_SIZE);
            ObjectInputStream objRxStream = new ObjectInputStream(rawRxStream);
            
            if (!PROTOCOL_IDENTIFIER.equals(objRxStream.readObject()))
            {
               // Wrong PROTOCOL_IDENTIFIER
            }
            else
            {
               rxStream = objRxStream;
               return;
            }
         }
         catch (Exception ex)
         {
         }
         if (rawRxStream != null)
         {
            try
            {
               rawRxStream.close();
            }
            catch (Exception ignored)
            {
            }
         }
         closeSocket();
      }
   }
   
   /**
    * The process which sends our Id.  On success, it doesn't change anything.
    * If there is an exception, it closes the socket, sets txStream=null, and
    * returns.
    */
   private class TxId implements CSProcess
   {
      /**
       * The run method.
       */
      public void run()
      {
         NodeID localAddress = null;
         if(sendNodeID) 
            localAddress = Node.getInstance().getNodeID();
         try
         {
            txStream.writeObject(localAddress);
            txStream.flush();
            txStream.reset();
         }
         catch (Exception ex)
         {
            ex.printStackTrace();
            closeTx();
         }
      }
   }
   
   /**
    * The process which recieves the remote Id.  On success, it sets
    * remoteAddress.  If there is an exception, it closes the socket, sets
    * rxStream=null, and returns.
    */
   private class RxId implements CSProcess
   {
      /**
       * The run method.
       */
      public void run()
      {
         try
         {
            remoteNodeID = (NodeID)rxStream.readObject();
         }
         catch (Exception ex)
         {
            ex.printStackTrace();
            closeRx();
         }
      }
   }
   
   /**
    * The process which does transmission to a socket.
    */
   private class TxLoop implements CSProcess
   {
      /**
       * The run method.
       */
      public void run()
      {
         ChannelInput in = txChannel.in();
         try
         {
            Object obj = in.read();
            while (!(obj instanceof TxLoopPoison))
            {
               txStream.writeObject(obj);
               txStream.flush();
               txStream.reset();
               obj = in.read();
            }
            closeTx();
         }
         catch (Exception ex)
         {
            if ((ex instanceof SocketException) || (ex instanceof EOFException))
            {
               // Probably because the other side closed it.
            }
            else
            {
               synchronized(System.err)
               {
                  Node.err.log(this, "Error in TX:");
                  ex.printStackTrace();
               }
            }
            closeTx();
            
            // Need to wait for RxLoop to die (which it will do because we
            // just closed the socket it's trying to read from) and then
            // get the TxLoopPoison it sends us.  Meanwhile, black-hole
            // any incoming data.
            Object obj = in.read();
            while (!(obj instanceof TxLoopPoison))
               obj = in.read();
         }
      }
   }
   
   /**
    * The process which recieves from a socket.
    */
   private class RxLoop implements CSProcess
   {
      /**
       * The run method.
       */
      public void run()
      {
         try
         {
            // Now enter demux loop
            while (true)
               deliverReceivedObject(rxStream.readObject());
         }
         catch (Exception ex)
         {
            if ((ex instanceof SocketException) || (ex instanceof EOFException))
            {
               // Probably because the other side closed it.
            }
            else
            {
               synchronized(System.err)
               {
                  Node.err.log(this, "Error in RX:");
                  Node.err.log(this, ex);
               }
            }
            closeRx();
            
            // Now, need to carefully close TxLoop, making sure we don't
            // deadlock.
            txChannel.out().write(new TxLoopPoison());
         }
      }
   }
   
   /**
    * An object of this type is used by RxLoop to poison TxLoop.
    */
   private class TxLoopPoison
   {
   }
}