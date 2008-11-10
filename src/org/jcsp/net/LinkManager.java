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

import org.jcsp.lang.*;
import org.jcsp.util.*;
import java.util.*;

/**
 * This class manages the list of open links.  It ensures that there is
 * only ever one link between two given JCSP servers.  <p>
 *
 * @author Quickstone Technologies Limited.
 */
// package-private
class LinkManager implements CSProcess
{
   /*-------------------Singleton Class Instance---------------------------------*/
   
   private static LinkManager instance = new LinkManager();
   
   /*-------------------Private Constructor--------------------------------------*/
   
   LinkManager()
   {
      ProcessManager pm = new ProcessManager(loopbackLink);
      pm.setPriority(ProcessManager.PRIORITY_MAX);
      pm.start();
   }
   
   /*----------------------Attributes--------------------------------------------*/
   
   /**
    * The active links.  This is a hashtable where the key is a
    * NodeID and the value is a Link.
    *
    * @see NodeID
    * @see Link
    */
   private Hashtable linkHolders = new Hashtable();
   
   /**
    * This maps Link objects to a channel that is used to receive notification
    * that the link has been established.  This is necessary for when
    * links are created to Nodes where the NodeID is not known. This means that
    * a link holder cannnot be created as they are identified by the remote
    * NodeID of the links they hold.
    *
    * This would typically be used when establishing a connection to the
    * CNS Server as the NodeID would not be known until after the connection is
    * made.
    */
   //private Hashtable synchChannels = new Hashtable();
   
   /**
    * The LoopBackLink used for local connections.
    *
    */
   private LoopbackLink loopbackLink = new LoopbackLink();
   
   private final Any2OneConnection registerConn = Connection.createAny2One();
   
   private final Any2OneChannel requestLink = Channel.any2one();
   
   private final Any2OneChannel lostLinkChan = Channel.any2one();
   
   private final Any2OneChannel linkFailureChan = Channel.any2one();
   
   private final Any2OneChannel checkForLink = Channel.any2one();
   
   private final Any2OneChannel getNodeIDChan = Channel.any2one();
   
   private final Any2OneChannel registerEventChannel = Channel.any2one();
   
   private static final int ALT_LOST_LINK = 0;
   private static final int ALT_LINK_FAIL = 1;
   private static final int ALT_REG_CHAN = 2;
   private static final int ALT_REQ_LINK = 3;
   private static final int ALT_CHECK_FOR_LINK = 4;
   
   /*----------------------Methods-----------------------------------------------*/
   
   /** Used to run the <CODE>LinkManager</CODE>.
    */
   public void run()
   {
      Node.info.log(this, "Running");
      
      //Start a process to hand event channel registration and
      //distribution of events
      
      final Any2OneChannel sendEvent = Channel.any2one(new Buffer(10));
      
      CSProcess eventProc = new CSProcess()
      {
         public void run()
         {
            Alternative alt = new Alternative(new Guard[] {registerEventChannel.in(), sendEvent.in()});
            Vector eventChans = new Vector();
            while (true)
            {
               switch(alt.fairSelect())
               {
                  case 0:
                     eventChans.add(registerEventChannel.in().read());
                     break;
                  case 1:
                     final ChannelOutput[] chans = new ChannelOutput[eventChans.size()];
                     final Object eventToSend = sendEvent.in().read();
                     eventChans.toArray(chans);
                     new ProcessManager(new CSProcess()
                                       {
                                          public void run()
                                          {
                                             //the channels have infinite buffers
                                             //so ok to just use one thread.
                                             for (int i=0; i<chans.length; i++)
                                                chans[i].write(eventToSend);
                                          }
                                       }).start();
                     break;
               }
            }
         }
      };
      new ProcessManager(eventProc).start();
      AltingConnectionServer registerServer = registerConn.server();
      
      Alternative alt = new Alternative(new Guard[] 
                                       {
                                          lostLinkChan.in(),
                                          linkFailureChan.in(),
                                          registerServer,
                                          requestLink.in(),
                                          checkForLink.in()
                                       });
      boolean run = true;
      while(run)
      {
         try
         {
            switch(alt.priSelect())
            {
               case ALT_LOST_LINK:
                  //Link Lost
                  Link linkLost = (Link) lostLinkChan.in().read();
                  LinksToNodeHolder lostLH = (LinksToNodeHolder)linkHolders.get(linkLost.getRemoteNodeID());
                  lostLH.removeLink(linkLost);
                  //send the NodeID of the link that has been lost to the
                  //event channel
                  sendEvent.out().write(linkLost.getRemoteNodeID());
                  Node.info.log(this, "Link to " + linkLost.getRemoteNodeID() + " lost.");
                  break;
               case ALT_LINK_FAIL:
                  //link failure
                  Object o = linkFailureChan.in().read();
                  if(o instanceof LinkEstablishmentException)
                  {
                     LinkEstablishmentException exc = (LinkEstablishmentException) o;
                     exc.lh.notifyQueuedProcesses(exc.profile, exc);
                  }
                  else if(o instanceof Link)
                  {
                     //what shall we do?
                     //Link was created but failed
                     //Need to determine if there are other links being
                     //established that could service the queued
                     //processes
                     
                     //Do this later
                  }
                  Node.info.log(this, "Link failed");
                  break;
               case ALT_REG_CHAN:
               {
                  //Link Registration
                  Link conn = (Link) registerServer.request();
                  //Register Link Code
                  NodeID id = conn.getRemoteNodeID();
                  boolean success = false;
                  if (linkHolders.containsKey(id))
                  {
                     //There might already a link to the remote Node of
                     //this link try and add this link anyway,
                     //if there were
                     //some queued processes then this link must have
                     //different facilities.
                     LinksToNodeHolder lh = (LinksToNodeHolder) linkHolders.get(id);
                     if(lh != null)
                     {
                        if(lh.addLink(conn, false) > 0 )
                        {
                           registerServer.replyAndClose(Boolean.TRUE);
                           success = true;
                        }
                     }
                  }
                  else
                  {
                     //This Node was connected to from another Node.
                     //create a linkHolder for the target
                     LinksToNodeHolder lh = new LinksToNodeHolder(id);
                     linkHolders.put(id, lh);
                     lh.addLink(conn, true);
                     registerServer.replyAndClose(Boolean.TRUE);
                     success = true;
                  }
                  if (!success) 
                     registerServer.replyAndClose(Boolean.FALSE);
                  if (success)
                     Node.info.log(this, "Link established to " + id);
                  break;
               }
               case ALT_REQ_LINK:
               {   
                  //Link Request
                  //create a new scope
                  LinkRequest lr = (LinkRequest)requestLink.in().read();
                  final ChannelOutput channel = lr.replyChan;
                  final NodeID target = lr.target;
                  final Profile linkProfile = lr.linkProfile;
                  LinksToNodeHolder lhTemp = (LinksToNodeHolder) linkHolders.get(target);
                  if(lhTemp == null)
                  {
                     //create a linkHolder for the target
                     lhTemp = new LinksToNodeHolder(target);
                     linkHolders.put(target, lhTemp);
                  }
                  final LinksToNodeHolder lh = lhTemp;
                  //join a queue for the link, if one already exists then
                  //the Link will be sent straight down the channel
                  if(lh.joinQueue(channel, linkProfile))
                  {
                     //Need to initiate the link
                     new ProcessManager(new CSProcess()
                     {
                        public void run()
                        {
                           Link conn = LinkFactory.getInstance().makeLink(target, linkProfile);
                           if(conn != null)
                           {
                              //run the connection in the same process
                              //as this
                              ProcessManager pm = new ProcessManager(conn);
                              pm.setPriority(ProcessManager.PRIORITY_MAX);
                              pm.run();
                           }
                           else
                           {
                              //notify all those waiting that Link
                              //establishment has failed
                              LinkEstablishmentException exc = new LinkEstablishmentException(
                                      "No matching can be " + "established.", lh, linkProfile);
                              linkFailureChan.out().write(exc);
                           }
                        }
                     }).start();
                  }
               }
               break;
               case ALT_CHECK_FOR_LINK:
               {
                  LinkCheck lc = (LinkCheck) checkForLink.in().read();
                  final ChannelOutput channel = lc.replyChan;
                  final NodeID target = lc.target;
                  channel.write(linkHolders.get(target));
               }
               break;
            }
         }
         catch (Exception e)
         {
            Node.err.log(this, e);
         }
      }
   }
   
   /**
    * Starts this instance of the <CODE>LinkManager</CODE> and returns.
    */
   public void start()
   {
      new ProcessManager(this).start();
   }
   
   /**
    * Get the singleton instance of LinkManager.
    *
    * @return The singleton LinkManager object.
    */
   // package-private
   static LinkManager getInstance()
   {
      return instance;
   }
   
   /**
    * Register a Link in the database.  Prevents duplicates.
    * Returns true if there was no prior entry for that NodeID, and one
    * was added.  (In that case it has also notified any waiting processes).
    * Returns false (and does not change the database) if there is an
    * existing entry.
    *
    * @return true iff there was no prior entry for that NodeID
    */
   //package-private - only called from Link.
   boolean registerLink(Link conn)
   {
      Node.info.log(this, "register link " + conn);
      ConnectionClient cc = registerConn.client();
      cc.request(conn);
      return ((Boolean)cc.reply()).booleanValue();
   }
   
   void registerFailure(Link conn)
   {
      Node.err.log(this, "Link failed to be established: " + conn);
      linkFailureChan.out().write(conn);
   }
   
   /**
    * Get a channel to use to send to a remote PC.  Will block (possibly
    * forever) while the link is being established.
    *
    * @param target The system to connect to.
    * @return The Channel to use to send to the requested computer.
    * @throws IllegalStateException If init() has not been called.
    */
   // package-private
   ChannelOutput getTxChannel(NodeID target)
   {
      return getLink(target, null).getTxChannel();
   }
   
   ChannelOutput getTxChannel(NodeID target, Profile linkProfile)
   {
      return getLink(target, linkProfile).getTxChannel();
   }
   
   /**
    * This is used to get a Link when no details are known about the other
    * Node except for one of its addresses. A typical situation when this
    * would be used is to connect to the CNS.
    *
    * @param	targetAddress	The address to which to create a link.
    * @return	the ChannelOuput that writes to the link.
    */
   ChannelOutput getTxChannel(NodeAddressID targetAddress)
   {
      Node.getInstance().checkInitialized();
      Link link = LinkFactory.getInstance().makeLink(targetAddress);
      if(link != null)
      {
         NodeID remoteNodeID = link.obtainNodeID();
         if(remoteNodeID != null)
         {
            return getTxChannel(remoteNodeID);
         }
      }
      return null;
   }
   
   /**
    * Get a Link to a remote PC.  Will block (possibly forever) while
    * the link is being established.
    *
    * @param target The system to connect to.
    * @return The Link to the requested computer.
    * @throws IllegalStateException If init() has not been called.
    */
   private Link getLink(NodeID target, Profile linkProfile)
   {
      Node.getInstance().checkInitialized();
      if(Node.getInstance().isThisNode(target))
         //attempt to get a link to the local Node - return the LoopbackLink
         return loopbackLink;
      One2OneChannel channel = Channel.one2one(new InfiniteBuffer());
     
      requestLink.out().write(new LinkRequest(channel.out(), target, linkProfile));
      
      Link conn = null;
      Object obj = channel.in().read();
      if(obj instanceof Link)
         conn = (Link) obj;
      else if(obj instanceof ProfileMatchFailureException)
         throw new IllegalStateException("No matching link exists.");
      else
         throw new IllegalStateException("No matching link exists.");
      return conn;
   }
   
   /**
    * Called by a Link to indicate the link broke.
    *
    * @param conn The broken link.
    */
   int lostLink(Link conn)
   {
      lostLinkChan.out().write(conn);
      return IndexManager.getInstance().broadcastLinkLost(new LinkLost(conn));
   }
   
   /**
    * This method tests whether a link exists to
    * a specified remote Node in this <code>LinkManager</code>.
    *
    * @param  otherNode  The <code>NodeID</code> of a remote Node to check
    *                     the existance
    * @return <code>true</code> iff a link currently exists to
    *          the specified remote Node.
    */
   boolean linkExists(NodeID otherNode)
   {
      Node.getInstance().checkInitialized();
      
      One2OneChannel channel = Channel.one2one(new InfiniteBuffer());
      checkForLink.out().write(new LinkCheck(channel.out(), otherNode));
      return (channel.in().read() != null);
   }
   
   AltingChannelInput getLinkLostEventChannel()
   {
      Node.getInstance().checkInitialized();
      
      final One2OneChannel eventChan = Channel.one2one(new InfiniteBuffer());
      registerEventChannel.out().write(eventChan.out());
      //this should be a Safe channel
      return eventChan.in();
   }
   
   /*----------------------Inner Classes-----------------------------------------*/
   
   private static class LinksToNodeHolder
   {
      public LinksToNodeHolder(NodeID target)
      {
         this.target = target;
      }
      
      public Link getLink(Profile profile)
      {
         if(profile == null)
         {
            if(linkWithNoSpecifiedFacilities != null)
               return linkWithNoSpecifiedFacilities;
         }
         else
         {
            Iterator it = links.iterator();
            while(it.hasNext())
            {
               Link l = (Link) it.next();

               //test whether the link meets the profile's requirements
               Specification[] linkSpecs = l.getSpecifications();
               int matches = profile.matches(linkSpecs);
               //if so, return it
               if(matches == 1)
                  return l;
               else if(!profile.requiresExactMatch() && matches == 0)
                  return l;
            }
         }
         return null;
      }
      
      public NodeID getTarget()
      {
         return target;
      }
      
      /**
       * This sends the supplied link to any processes that are waiting for
       * matching link. If there were some processes waiting or the
       * <code>addIfNoQueue</code> parameter was true, then the link is
       * added to this link manager.
       *
       * @param	link	the Link to add.
       * @param	addIfNoQueue	boolean inidicating whether to add the link
       *							if there were no queued processes.
       *
       * @return	the number of processes to which this link was sent.
       */
      public int addLink(Link link, boolean addIfNoQueue)
      {
         Specification[] specs = link.getSpecifications();
         
         int noNotified = notifyQueuedProcesses(specs, link);
         if(noNotified > 0 || addIfNoQueue)
         {
            //if link has not specs or it uses the favoured protocol
            //set it as the the link with no specified facilities
            if(specs == null || specs.length == 0 || link.getProtocolID().getPosition() == 0)
               linkWithNoSpecifiedFacilities = link;
            links.add(link);
         }
         return noNotified;
      }
      
      public int notifyQueuedProcesses(Profile profile, Object toSend)
      {
         if(profile == null)
            profile = Profile.getAlwaysMatchProfile();
         Object queue = waiting.get(profile);
         int notifyCount = 0;
         if (queue != null)
         {
            if (queue instanceof Vector)
            {
               Vector v = (Vector) queue;
               for (int i = 0; i < v.size(); i++)
               {
                  ChannelOutput ch = ((ChannelOutput)v.elementAt(i));
                  ch.write(toSend);
               }
            }
            else
            {
               ChannelOutput ch = ((ChannelOutput)queue);
               ch.write(toSend);
            }
            notifyCount++;
            waiting.remove(profile);
         }
         return notifyCount;
      }
      
      /**
       * This takes a set of link specifications, looks through the set of
       * queues, checks whether the specifications meet requirements of
       * the profile of a queue, and if so, sends the supplied Link to the
       * processes in that queue.
       *
       * The method returns the number of queues that were notified.
       *
       * @param	specs	an array of Specifcation objects that
       *					the link provides.
       * @param	linkToSend	the link to send to the queued processes.
       *
       * @return	the number of queues that were notified.
       */
      public int notifyQueuedProcesses(Specification[] specs, Link linkToSend)
      {
         int notifyCount = 0;
         Iterator it = waiting.keySet().iterator();
         while(it.hasNext())
         {
            Profile profile = (Profile) it.next();

            //check whether the specs match the profile
            int matchValue = 0;
            if (profile != null) 
               matchValue = profile.matches(specs);
            
            Object toSend = null;
       
            if(profile == null || matchValue == 1 ||(matchValue == 0 && !profile.requiresExactMatch()))
            {
               notifyCount++;
               toSend = linkToSend;
            }
            else if(profile != null)
            {
               if(linkToSend.getProfile().equals(profile))
                  toSend = new ProfileMatchFailureException();
            }
            if(toSend != null)
            {
               //the profile matches the specs
               if(profile == null)
                  profile = Profile.getAlwaysMatchProfile();
               Object queue = waiting.get(profile);
               if (queue != null)
               {
                  if (queue instanceof Vector)
                  {
                     Vector v = (Vector) queue;
                     for (int i = 0; i < v.size(); i++)
                     {
                        ChannelOutput ch = ((ChannelOutput)v.elementAt(i));
                        ch.write(toSend);
                     }
                  }
                  else
                  {
                     ChannelOutput ch = ((ChannelOutput)queue);
                     ch.write(toSend);
                  }
               }
               it.remove();
            }
         }
         return notifyCount;
      }
      
      public void removeLink(Link conn)
      {
         links.remove(conn);
      }
      
      /**
       * This method allows processes to join a queue waiting for a link
       * to a Node (with a known NodeID) to be established. An infinite
       * buffered channel is supplied. When it is known that a link has
       * been created or failed to be created, notification can be sent down
       * the supplied channel to the waiting process.
       *
       * There is one queue per link required matching matching a given
       * profile.  When a link is established, the profile of each queue
       * is tested against the link and, if the profile matches, then all
       * queued processes are notified.  This can mean that multiple links to
       * a remote Node might be established but by the time a link has been
       * established, there may be no processes waiting for it as other links
       * may have met their requirements.
       *
       * @param	channel	the channel to put in the queue.
       * @param	profile	the profile that the required link must meet.
       *
       * @return	a boolean inidicating whether the channel is the first
       *			in the queue and hence the link needs to be initiated.
       */
      public boolean joinQueue(ChannelOutput channel, Profile profile)
      {
         boolean initiate = false;
         Link link = getLink(profile);
         if(profile == null)
            profile = Profile.getAlwaysMatchProfile();
         
         if(link != null)
            channel.write(link);
         else if (waiting.containsKey(profile))
         {
            Object o = waiting.get(profile);
            Vector v;
            if (o instanceof Vector)
               v = (Vector) o;
            else
            {
               v = new Vector();
               v.add(o);
            }
            v.add(channel);
            waiting.put(profile, v);
            initiate =  false;
         }
         else
         {
            waiting.put(profile, channel);
            initiate =  true;
         }
         return initiate;
      }
      
      private NodeID target;
      
      private HashSet links = new HashSet();
      
      /**
       * A link with no special facilities. This is so that if no features
       * are required then no iteration will be needed.
       *
       */
      
      private Link linkWithNoSpecifiedFacilities = null;
      
      /**
       * The processes waiting for a link.
       * The key is the FacilitiesRequired  of the link we're waiting for,
       * the value is the channel to the waiting process
       * (or a Vector of these).  When the link is available, we notify the
       * process(es) by sending the Link down the channel(s).
       */
      private Hashtable waiting = new Hashtable();
      
   }
   
   private static class ProfileMatchFailureException extends Exception
   {
      public ProfileMatchFailureException()
      {
      }
      
      public ProfileMatchFailureException(String msg)
      {
         super(msg);
      }
   }
   
   public static class LinkEstablishmentException extends RuntimeException
   {
      private LinkEstablishmentException(String msg, LinksToNodeHolder lh,
              Profile profile)
      {
         super(msg);
         this.lh = lh;
         this.profile = profile;
      }
      
      private LinksToNodeHolder lh;
      private Profile profile;
   }
   
   private static class LinkRequest
   {
      LinkRequest(ChannelOutput replyChan, NodeID target, Profile linkProfile)
      {
         this.replyChan = replyChan;
         this.target = target;
         this.linkProfile = linkProfile;
      }
      final ChannelOutput replyChan;
      final NodeID target;
      final Profile linkProfile;
   }
   
   private static class LinkCheck
   {
      LinkCheck(ChannelOutput replyChan, NodeID target)
      {
         this.replyChan = replyChan;
         this.target = target;
      }
      
      final ChannelOutput replyChan;
      final NodeID target;
   }
}