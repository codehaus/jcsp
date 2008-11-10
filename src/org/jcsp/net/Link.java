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

package org.jcsp.net;

import java.io.*;
import org.jcsp.lang.*;
import org.jcsp.net.settings.*;
import org.jcsp.util.filter.*;
import org.jcsp.net.security.*;
import java.util.*;

// package-private
/**
 * <p>
 * This class is an abstract class that all JCSP.NET protocol implementations
 * must implement. Concrete implementations of the <CODE>Link</CODE> class
 * must provide the mechanism for sending data to remote Nodes and receiving
 * data back. When a concrete implementation of link is initiated by calling
 * the <CODE>run()</CODE> method, it must inititate a handshaking procedure
 * and call certain protected methods in this abstract class.
 * </p>
 * <p>
 * During handshaking, the
 * <CODE>runTests(ChannelInput, ChannelOutput, boolean)</CODE> should be called.
 * See the documentation for this method for a full explanation.
 * </p>
 * <p>
 * When a <CODE>Link</CODE> receives an object from over the network
 * it should deliver the object to its destination by calling the
 * <CODE>deliverReceivedObject(Object)</CODE> method.
 * </p>
 * <p>
 * <CODE>Link</CODE> implementations obtain objects to send to the remote
 * Node by reading from the protected <CODE>txChannel</CODE> object.
 * </p>
 *
 * @author Quickstone Technologies Limited
 */
public abstract class Link implements CSProcess
{
   /*----------------------Constructors------------------------------------------*/
   /**
    * <p>
    * A constructor that must be called by sub-classes.
    * </p>
    * @param protocolID A <CODE>ProtocolID</CODE> object for identifying the protocol that is implementing the Link.
    * @param client Indicates whether this is the client (true) or server (false) end of the connection.
    * @param connected true if a connection is already established; otherwise <code>connect()</code> will later be called
    */
   public Link(ProtocolID protocolID, boolean client, boolean connected)
   {
      this.protocolID = protocolID;
      this.client = client;
      this.connected = connected;
      Service svc = Node.getInstance().getServiceManager().getService("security");
      if (svc != null)
         securityAuthority = (SecurityAuthority)svc.getUserObject();
   }
   /*----------------------Abstract Methods--------------------------------------*/
   
   /**
    * <p>
    * Establishes a connection to the peer node. Called when the process is started unless
    * <code>connected</code> was true in the constructor. This is called internally from this
    * class' <code>run</code> method - do not call it.
    * </p>
    *
    * @return true on success, false on failure
    */
   protected boolean connect() throws Exception
   {
      throw new NoSuchMethodException("connect");
   }
   
   /**
    * <p>
    * Allocates the resources necessary for the actual connection. Called as the first part
    * of the handshaking process. This is called internally from the class' <code>handshake</code>
    * method - do not call it.
    * </p>
    *
    * @return true on success, false on failure
    */
   protected boolean createResources() throws Exception
   {
      throw new NoSuchMethodException("createResources");
   }
   
   /**
    * <p>
    * Deallocates any resources allocated by <code>createResources</code>. Called in the event of
    * an error during handshaking or when the link is dropped. An implementation of this method
    * must cope with being called multiple times without an intervening call to <code>createResources</code>.
    * </p>
    */
   protected void destroyResources() throws Exception
   {
      throw new NoSuchMethodException("destroyResources");
   }
   
   /**
    * <p>
    * Sends the ID of this node to the peer process and receives its ID. It is only necessary to
    * send this node's ID if <code>sendNodeID</code> is true. The <code>remoteNodeID</code>
    * attribute should be set to the ID of the remote node if received. This is called internally
    * during handshaking - do not call it.
    * </p>
    *
    * @return true on success, false on failure.
    */
   protected boolean exchangeNodeIDs() throws Exception
   {
      throw new NoSuchMethodException("exchangeNodeIDs");
   }
   
   /**
    * Performs send and receive actions for the link exchanging data with the peer node. This is
    * called from this class' <code>run</code> method - do not call it directly.
    *
    *
    */
   protected void runTxRxLoop() throws Exception
   {
      throw new NoSuchMethodException("runTxRxLoop");
   }
   
   /**
    * Waits for <code>numRepliesOutstanding</code> instances of <code>LinkLost</code>
    * to arrive from the <code>txChannel</code>. This is called internally from this class'
    * <code>run</code> method - do not call it directly.
    *
    * @param numRepliesOutstanding <code>LinkLost</code> instances to wait for.
    */
   protected void waitForReplies(int numAcknowledgements) throws Exception
   {
      throw new NoSuchMethodException("waitForReplies");
   }
   
   /**
    * Writes a test object to the underlying connection. Called internally during handshaking
    * - do not call it directly. The value written must be delivered by <code>readTestObject</code>
    * at the remote node.
    *
    * @param obj object to be written
    */
   protected void writeTestObject(Object obj) throws Exception
   {
      throw new NoSuchMethodException("writeTestObject");
   }
   
   /**
    * Reads a test object from the underlying connection. Called internally during handshaking
    * - do not call it directly. It must deliver the value passed to <code>writeTestObject</code>
    * at the peer node.
    *
    * @return the object received.
    */
   protected Object readTestObject() throws Exception
   {
      throw new NoSuchMethodException("readTestObject");
   }
   
   /**
    * Writes a boolean link decision as to whether the other node has the option to keep or discard
    * the link. Called internally during handshaking - do not call it directly. The value written
    * must be delivered by <code>readLinkDecision</code> at the peer node.
    *
    * @param use decision result
    */
   protected void writeLinkDecision(boolean use) throws Exception
   {
      throw new NoSuchMethodException("writeLinkDecision");
   }
   
   /**
    * Reads a boolean link decision as to whether this node can keep or discard the link. Called
    * internally during handshaking - do not call it directly. It must return the value passed to
    * <code>writeLinkDecision</code> at the peer node.
    *
    * @return decision result
    */
   protected boolean readLinkDecision() throws Exception
   {
      throw new NoSuchMethodException("readLinkDecision");
   }
   
   /*----------------------Concrete Methods--------------------------------------*/
   
   /**
    * Adds a transmission filter. This filter will run in the thread that is
    * trying to send something over this Link.
    *
    * @param	filter	the filter.
    * @param	index	the index position of the filter.
    */
   void addTxFilter(Filter filter, int index)
   {
      txChannel.outFilter().addWriteFilter(filter, index);
   }
   
   /**
    * Removes a transmission filter.
    *
    * @param	filter	the filter to remove.
    */
   void removeTxFilter(Filter filter)
   {
      txChannel.outFilter().removeWriteFilter(filter);
   }
   
   /**
    * Gets the number of installed transmission filters.
    *
    * @return	the number of transmission filters installed.
    */
   int getTxFilterCount()
   {
      return txChannel.outFilter().getWriteFilterCount();
   }
   
   /**
    * Returns the other computer's ID.
    *
    * This method is safe to call while the process is running, however
    * it will return null if the other computer has not yet identified
    * itself.
    *
    * @return ID of connected computer.
    */
   // package-private
   protected NodeID getRemoteNodeID()
   {
      return this.remoteNodeID;
   }
   
   /** A protected accessor for obtaining the identifier of the protocol implementing this Link object.
    * @return the <CODE>ProtocolID</CODE> of the protocol implementing this Link.
    */
   protected ProtocolID getProtocolID()
   {
      return this.protocolID;
   }
   
   void setProfile(Profile profile)
   {
      this.profile = profile;
   }
   
   Profile getProfile()
   {
      return this.profile;
   }
   
   /**
    * Returns channel to use for transmitting.
    *
    * This method is safe to call while the process is running.  May block
    * for if handshaking is still in progress.
    *
    * When written to, if the object supplied is not Serializable then an
    * IllegalArgumentException Runtime exception.
    *
    * @return Channel you should send data to if you want to transmit on
    *         this Connection.
    */
   // protected - to be used by this package and sub-classes
   protected ChannelOutput getTxChannel()
   {
      return txChannel.out();
   }
   
   /**
    * A protected method for concrete implementations of this class to call when they received a an object from the remote Node. This method delivers the message to its destination (a Channel, Connection etc.).
    * @param obj the object to deliver.
    */
   protected void deliverReceivedObject(Object obj)
   {
      
      if (!(obj instanceof Message))
      {
         if (obj instanceof Challenge)
            // the other end is using security and we aren't
            txChannel.out().write(null);
         else
            Node.err.log(this, "call to deliverReceivedObject without a valid Message");
         return;
      }
      Message msg = (Message) obj;
      
      if (msg instanceof Message.PingMessage)
         txChannel.out().write(Message.PING_REPLY_MESSAGE);
      else if (msg instanceof Message.PingReplyMessage)
         pingReplyChan.out().write(msg);
      else
      {
         // this is a little hack so that LoopbackLinks deliver the correct node IDs
         if ((msg.sourceID = remoteNodeID) == null)
            msg.sourceID = remoteNodeID = getRemoteNodeID();
         msg.txReplyChannel = txChannel.out();
         
         ChannelOutput out = null;
         if (IndexManager.checkIndexIsValid(msg.destIndex))
            out = im.getRxChannel(msg.destIndex);
         else if (msg.destVCNLabel != null)
            out = im.getRxChannel(msg.destVCNLabel);
         if (out != null)
         {
            try
            {
               out.write(msg);
            }
            catch (PoisonException e)
            {
               Node.info.log(this, "BOUNCING Message");
               msg.bounce(getTxChannel());
               //log this properly
               Node.info.log(this, "Unable to deliver message " + msg + " Channel was poisoned: " + e);
            }
         }
         else if (!(msg instanceof Message.BounceMessage))
         {
            Node.info.log(this, "BOUNCING Message");
            
            //Do not do this if recieved an InvalidChannelIndexMessage
            //there could be continually bounced back replies
            msg.bounce(getTxChannel());
         }
         else
            Node.err.log(this, "BOUNCED message with invalid destination index");
      }
   }
   
   Specification[] getSpecifications()
   {
      return specifications;
   }
   
   void setSpecifications(Specification[] specifications)
   {
      Comparator c = new Comparator()
      {
         public int compare(Object o1, Object o2)
         {
            Specification s1 = (Specification) o1;
            Specification s2 = (Specification) o2;
            return s1.name.compareTo(s2.name);
         }
      };
    
      Arrays.sort(specifications, c);
      this.specifications = specifications;
   }
   
   /**
    * A public accessor for obtaining the ping time between the local Node and this Link's remote Node.
    * @return the ping time as a <CODE>long</CODE>.
    */
   public long getPingTime()
   {
      return pingTime;
   }
   
   /**
    * Performs a ping on the link.
    *
    * @return the ping time in ms.
    */
   public synchronized long ping()
   {
      long startTime = System.currentTimeMillis();
      txChannel.out().write(Message.PING_MESSAGE);
      pingReplyChan.in().read();
      long endTime = System.currentTimeMillis();
      this.pingTime = endTime - startTime;
      return pingTime;
   }
   
   /**
    * <p>
    * A public accessor for enquiring as to whether this <CODE>Link</CODE> object has performed a ping test.
    * </p>
    *
    * @return <CODE>true</CODE> iff a ping test has been performed.
    */
   public boolean performedPingTest()
   {
      return performedPingTest;
   }
   
   /**
    * <p>
    * This is used by concrete <CODE>Link</CODE> implementations before calling the <CODE>runTests</CODE> method.
    * When tests are run, a series of messages are exchanged between both sides of the link. This allows
    * the link to perform its tests using a single thread.
    * </p>
    *
    * @param client a <CODE>boolean</CODE> indicating whether this <CODE>Link</CODE> object is the client side of the link.
    * If this object is the client then the parameter should be <CODE>true</CODE>.
    * @return an array of <CODE>boolean</CODE> values indicating the sequence of when to read and when to transmit.
    */
   protected boolean[] getReadSequence(boolean client)
   {
      if (client)
         return new boolean[] { false, true, false, true, false };
      return new boolean[] { true, false, true, false, true };
   }
   
   /**
    * <p>
    * This should be called during the handshaking process. The <CODE>in</CODE> parameter should be
    * a <CODE>ChannelInput</CODE> that reads from a process that receives object from the remote Node.
    * The <CODE>out</CODE> parameter is a <CODE>ChannelOutput</CODE> that, when written
    * to, will transmit the object to the remote Node. The <CODE>client</CODE> parameter should be <CODE>true</CODE>
    * iff the current <CODE>Link</CODE> is acting as a client side link (if it establishes the initial connection).
    * </p>
    * <p>
    * The method will start a testing process and then return. The concrete implementation
    * of <CODE>Link</CODE> that calls this method should call the <CODE>getReadSequence(boolean)</CODE> method
    * before calling this method. Once this method has returned, the calling process should loop through the
    * returned array of <CODE>boolean</CODE> values. If the value is true, the process should wait to recieve
    * an object from over the network and then send it down the channel supplied as the <CODE>in</CODE> parameter.
    * If the value is <CODE>false</CODE>, the process should read from the channel supplied as the <CODE>out</CODE>
    * parameter and then send the object it receives over the network.
    * </p>
    * <p>
    * Once the calling process has finished looping through the array, it should perform a read from the channel
    * supplied as the <CODE>out</CODE> channel.
    * This is some example code for what concrete implementations of <CODE>Link</CODE> need to do:
    * </p>
    * <PRE>
    *    One2OneChannel fromTestProcess = Channel.one2one();
    *    One2OneChannel toTestProcess = Channel.one2one();
    *    boolean[] readSequence = super.getReadSequence(client);
    *    super.runTests(toTestProcess.in(), fromTestProcess.out(), client);
    *    try {
    *        for(int i=0; i<readSequence.length; i++) {
    *            if(readSequence[i] == true) {
    *                Object obj = rxStream.readObject();
    *                toTestProcess.out().write(obj);
    *            }else {
    *                Object obj = fromTestProcess.in().read();
    *                txStream.writeObject(obj);
    *                txStream.flush();
    *                txStream.reset();
    *            }
    *        }
    *    }catch (Exception e) {
    *        //Handle Error
    *    }
    *    fromTestProcess.read();
    * </PRE>
    *
    * @param in a <CODE>ChannelInput</CODE> that the test process can use
    * to receive objects from over the network.
    * @param out a <CODE>ChannelOutput</CODE> that the test process
    * should use to send objects over the network.
    * @param client a <CODE>boolean</CODE> that is <CODE>true</CODE> iff this <CODE>Link</CODE> object
    * is the object that established the connection to the remote Node.
    */
   private void runTestProcess(final ChannelInput in, final ChannelOutput out, final boolean client)
   {
      new ProcessManager(new CSProcess()
                        {
                           public void run()
                           {
                              if (client)
                              {
                                 //check other side is ready
                                 out.write(new LinkTest());
                                 LinkTest returned = (LinkTest) in.read();
                                 if ((returned != null) && (returned.counter == 1))
                                 {
                                    returned.counter++;
                                    long txTime = System.currentTimeMillis();
                                    out.write(returned);
                                    returned = (LinkTest) in.read();
                                    long rxTime = System.currentTimeMillis();
                                    if ((returned != null) && (returned.counter == 3))
                                    {
                                       returned.counter++;
                                       out.write(returned);
                                       performedPingTest = true;
                                       pingTime = rxTime - txTime;
                                    }
                                 }
                              }
                              else
                              {
                                 LinkTest received = (LinkTest) in.read();
                                 if ((received != null) && (received.counter == 0))
                                 {
                                    received.counter++;
                                    out.write(received);
                                    //both sides at this stage - now perform test
                                    received = (LinkTest) in.read();
                                    if ((received != null) && (received.counter == 2))
                                    {
                                       received.counter++;
                                       //reply straight away
                                       long txTime = System.currentTimeMillis();
                                       out.write(received);
                                       received = (LinkTest) in.read();
                                       long rxTime = System.currentTimeMillis();
                                       if ((received != null)
                                       && (received.counter == 4))
                                       {
                                          performedPingTest = true;
                                          pingTime = rxTime - txTime;
                                       }
                                    }
                                 }
                              }
                              out.write(null);
                           }
                        }).start();
   }
   
   /**
    * <p>
    * This is called during handshaking in order to
    * register the link with the local <CODE>LinkManager</CODE>. It returns <CODE>true</CODE>
    * if the link is successfully registered.
    * </p>
    * @return <CODE>true</CODE> iff the link is successfully registered.
    */
   private boolean registerLink()
   {
      Specification[] specifications = ProtocolManager.getInstance().getProtocolSpecifications(protocolID);
      if (specifications != null)
      {
         Specification[] specificationsClone = (Specification[])specifications.clone();
         //update the ping value
         for (int i = 0; i < specificationsClone.length; i++)
         {
            if (specificationsClone[i].name.equals(XMLConfigConstants.SPEC_NAME_PING))
            {
               specificationsClone[i] = new Specification(specificationsClone[i].name, (int)getPingTime());
               break;
            }
         }
         setSpecifications(specificationsClone);
      }
      return LinkManager.getInstance().registerLink(this);
   }
   
   /**
    * <p>
    * Called to inform the local <CODE>LinkManager</CODE> that
    * this link has failed.
    * </p>
    * <p>
    * Currently, this should be called if the link fails once
    * it has been registered but before the handshaking is complete.
    * </p>
    */
   private void registerFailure()
   {
      LinkManager.getInstance().registerFailure(this);
   }
   
   /**
    * <p>
    * This should be called once to notify users of the link that the link has been dropped.
    * The method returns the number of channels that are using the link that has been dropped.
    * Implementations should read from <CODE>txChannel</CODE> the number of times returned
    * by this method.
    * </p>
    * <p>
    * This is an example:
    * </p>
    * <PRE>
    *    int numRepliesOutstanding = super.lostLink();
    *    while (numRepliesOutstanding > 0) {
    *        Object obj = txChannel.read();
    *        if (obj instanceof LinkLost) {
    *            numRepliesOutstanding--;
    *        }
    *    }
    * </PRE>
    * @return an <CODE>int</CODE> indicating the number of channels that are using the link.
    */
   private int lostLink()
   {
      return LinkManager.getInstance().lostLink(this);
   }
   
   /**
    * <p>
    * Compares another object with this object. This implementation
    * returns <CODE>true</CODE> iff the supplied object is the same
    * instance object as this object.
    * </p>
    * @param o an object to compare with this object.
    * @return <CODE>true</CODE> iff the parameter equals this object.
    */
   public final boolean equals(Object o)
   {
      return o == this;
   }
   
   /**
    * <p>
    * Returns an <CODE>int</CODE> hash code for this <CODE>Link</CODE>.
    * The current implementation just uses the instance from <CODE>Object</CODE>.
    * </p>
    *
    * @return an <CODE>int</CODE> hash code.
    */
   public final int hashCode()
   {
      return super.hashCode();
   }
   
   /**
    * <p>
    * Main process for the link, containing generic code that makes calls on
    * the abstract methods that should be implemented by a concrete subclass.
    * The subclass will be required to <code>connect()</code> if it hasn't
    * done so, perform handshaking, <code>runTxRxLoop()</code> and then
    * <code>waitForReplies()</code> to acknowledge link termination.
    * </p>
    */
   public final void run()
   {
      if (!connected)
      {
         boolean result;
         try
         {
            result = connect();
         }
         catch (Exception e)
         {
            e.printStackTrace();
            result = false;
         }
         if (!result)
         {
            // error.
            Node.err.log(this, "Connect Error");
            registerFailure();
            return;
         }
         connected = true;
      }
      
      if (protocolID != null)
      {
         switch (handshake())
         {
            case HS_OK :
               break;
            default :
            case HS_ERROR :
               // Print an error message
               // Negotiated that since we have another link we'll not use
               // this duplicate.  (Or there was an error & we gave up).
               Node.err.log(this, "Handshake Error");
               registerFailure();
               // Fall through
            case HS_TEMPORARY :
               try
               {
                  destroyResources();
               }
               catch (Exception e)
               {
                  Node.err.log(this, e);
               }
               return;
         }
      
         Node.info.log(this, "Link handshaking finished");
         
         // Security ...
         if (securityAuthority != null)
         {
            final boolean okay[] = new boolean[2];
            final One2OneChannelInt sync = Channel.one2oneInt();
            Parallel par = 
                    new Parallel
                     (new CSProcess[] 
                     {
                        new CSProcess()
                        {
                           public void run()
                           {
                              boolean read = false;
                              try
                              {
                                 Challenge ch = securityAuthority.createChallenge();
                                 writeTestObject(ch);
                                 sync.in().read();
                                 read = true;
                                 Response r = (Response)readTestObject();
                                 if (securityAuthority.validateResponse(ch, r))
                                    okay[0] = true;
                              }
                              catch (Exception e)
                              {
                                 Node.err.log(this, "error in making challenge - " + e.toString());
                              }
                              finally
                              {
                                 if (!read)
                                    sync.in().read();
                              }
                           }
                        },
                        new CSProcess()
                        {
                           public void run()
                           {
                              boolean written = false;
                              try
                              {
                                 Challenge ch = (Challenge)readTestObject();
                                 sync.out().write(1);
                                 written = true;
                                 Response r = securityAuthority.createResponse(ch);
                                 writeTestObject(r);
                                 okay[1] = true;
                              }
                              catch (Exception e)
                              {
                                 Node.info.log(this, "error in responding to challenge - " + e.toString());
                              }
                              finally
                              {
                                 if (!written)
                                    sync.out().write(0);
                              }
                           }
                        }
                     });
            par.run();
            par.releaseAllThreads();
            if ((!okay[0]) || (!okay[1]))
            {
               Node.info.log(this, "link rejected by security manager");
               try
               {
                  destroyResources();
               }
               catch (Exception e)
               {
                  Node.err.log(this, e);
               }
               return;
            }
         }
      }
      
      try
      {
         runTxRxLoop();
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
      
      if (protocolID != null)
      {
         
         try
         {
            waitForReplies(lostLink());
         }
         catch (Exception e)
         {
            Node.err.log(this, e);
         }
         
      }
      
   }
   
   /**
    * <p>
    * Does handshaking.  Checks that the other side is a JCSP.net server
    * of the same version, then decides whether or not this link is a
    * duplicate of an existing link.  If the versions match and there is
    * no link to the other server, then the link is registered with the
    * LinkManager, true is returned, and the link is ready for real
    * data.  Otherwise, the link is closed and false is returned.
    * </p>
    * <p>
    * This method contains generic code. Calls are made on abstract methods
    * that a concrete subclass must implement to provide link functionality.
    * </p>
    * @return HS_ERROR if a problem occurs, HS_REMPORARY if the link is to be discarded or
    *         HS_OK for a permanent link.
    */
   private int handshake()
   {
      // Establish the connecting resources
      try
      {
         if (!createResources())
            return HS_ERROR;
      }
      catch (Exception e)
      {
         Node.err.log(this, e);
         return HS_ERROR;
      }
      
      // Exchange node IDs
      try
      {
         if (!exchangeNodeIDs())
            return HS_ERROR;
      }
      catch (Exception e)
      {
         Node.err.log(this, e);
         return HS_ERROR;
      }
      
      // Verify the result and abort if just exchanging IDs
      if (remoteNodeID == null && sendNodeID)
      {
         //The other side was not trying to establish a full link
         //they just wanted our NodeID
         Node.info.log(this, "Request for NodeID received");
         return HS_TEMPORARY;
      }
      else if (remoteNodeID != null && !sendNodeID)
      {
         //We just wanted to obtain the other side's NodeID
         //Didn't want a permenent connection
         return HS_TEMPORARY;
      }
      else if (remoteNodeID == null && !sendNodeID)
      {
         //An error - we just wanted the other side's NodeID but they didn't
         //send it
         return HS_ERROR;
      }
      
      // Run the tests
      One2OneChannel fromTestProcess = Channel.one2one();
      One2OneChannel toTestProcess = Channel.one2one();
      boolean[] readSequence = getReadSequence(client);
      runTestProcess(toTestProcess.in(), fromTestProcess.out(), client);
      boolean testStatus = true, recvNull = false;
      for (int i = 0; testStatus && (i < readSequence.length); i++)
      {
         Object obj = null;
         if (readSequence[i])
         {
            //test process is going to do a read first
            try
            {
               obj = readTestObject();
               toTestProcess.out().write(obj);
            }
            catch (Exception e)
            {
               testStatus = false;
               toTestProcess.out().write(null);
            }
         }
         else
         {
            //test process is going to do a write first
            obj = fromTestProcess.in().read();
            if (obj == null)
            {
               recvNull = true;
               testStatus = false;
            }
            else
            {
               try
               {
                  writeTestObject(obj);
               }
               catch (Exception e)
               {
                  testStatus = false;
                  toTestProcess.out().write(null);
               }
            }
         }
      }
      if (!recvNull)
         fromTestProcess.in().read();
      if (!testStatus)
         return HS_ERROR;
      
      // Decide whether to use the link or not
      int nodeCompare = getRemoteNodeID().compareToLocalNode();
      if (nodeCompare == 0)
      {
         Node.err.log(this, "Error: Should be using Loopback");
         //should be using LoopBackLink
         //Networking.getInstance().log("ERROR in Link handshaking: loopback error");
         return HS_ERROR;
      }
      else if (nodeCompare < 0)
      {
         // We decide whether we want to keep this link.
         boolean use = registerLink();
         try
         {
            writeLinkDecision(use);
         }
         catch (Exception e)
         {
            Node.err.log(this, "Error during handshaking whilst writing link decision.");
            return HS_ERROR;
         }
         if (use)
            return HS_OK;
         else
            return HS_ERROR;
      }
      else
      {
         // other side can choose to drop the link.
         boolean use;
         try
         {
            use = readLinkDecision();
         }
         catch (Exception ex)
         {
            Node.err.log(this, "Error during handshaking whilst reading link decision.");
            return HS_ERROR;
         }
         
         if (use)
         {
            // Other side wants to use this.
            if (!registerLink())
            {
               // This is *NOT GOOD*.
               // In fact, this should *never* happen.
               
               // I think I've already got a link to another computer,
               // but that other computer doesn't seem to know about it!
               Node.err.log(this, "ERROR in Link handshaking: Remote and local link lists " + 
                       "are\ndifferent!  Try resetting everything...");
               return HS_ERROR;
            }
            return HS_OK;
         }
         else
         {
            // Other side does not want to use this.
            Node.err.log(this, "Other side does not want to use this");
            return HS_ERROR;
         }
      }
   }
   
   /**
    * <p>
    * This returns the <CODE>NodeID</CODE> of the remote Node to which this
    * link is connected. If a connection has not already been established,
    * this method may connect to the remote Node and request its
    * <CODE>NodeID</CODE> and then drop the connection.
    * </p>
    *
    * @return the remote Node's <CODE>NodeID</CODE> object.
    */
   public final NodeID obtainNodeID()
   {
      if (getRemoteNodeID() != null)
         return getRemoteNodeID();
      if (client)
      {
         sendNodeID = false;
         try
         {
            run();
         }
         catch (Exception e)
         { /*ignored*/
         }
         //set sendNodeID back to true just in case this Link is later used
         //in the normal way
         sendNodeID = true;
         return remoteNodeID;
      }
      return null;
   }
   
   /*----------------------Attributes--------------------------------------------*/
   
   /** Handshake result code - a problem occurred */
   private static final int HS_ERROR = -1;
   /** Handshake result code - temporary link */
   private static final int HS_TEMPORARY = 0;
   /** Handshake result code - permanant link ok */
   private static final int HS_OK = 1;
   
   /** True if <code>connect()</code> has been called or the underlying connection is already
    * open. False otherwise.
    */
   private boolean connected = false;
   
   /** True if this is a client during handshaking, false if it is the server. */
   private boolean client;
   
   private IndexManager im = IndexManager.getInstance();
   
   /**
    * The channel used for TX over the link.
    *
    * In the future, this might be made private.
    * It is recommended that implementations use the
    * <CODE>getTxChannel()</CODE> to obtain the channel.
    *
    * Protocol implementations should NOT alter any of the
    * filters on this channel.
    */
   protected FilteredAny2OneChannel txChannel = FilteredChannel.createAny2One();
   
   /**
    * This indicates whether to send the local Node's address during the
    * handshaking process. If not then the link is only for obtaining the
    * remote Node's NodeID and will be shutdown.
    */
   //private boolean sendNodeID = true;
   
   /**
    * The remote NodeID. The subclass must set this during <code>exchangeNodeIDs</code>.
    */
   protected NodeID remoteNodeID;
   
   /**
    * True if the subclass must pass the ID of this node to the peer node.
    */
   protected boolean sendNodeID = true;
   
   private ProtocolID protocolID;
   
   private Specification[] specifications;
   
   private boolean performedPingTest = false;
   
   private long pingTime = -1;
   
   private Profile profile = null;
   
   private One2OneChannel pingReplyChan = Channel.one2one();
   
   private SecurityAuthority securityAuthority = null;
   
   private static class LinkTest implements Serializable
   {
      int counter = 0;
      
      private byte[] bytes = new byte[4096];
   }
}
