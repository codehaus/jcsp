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
import java.net.*;
import java.util.*;
import java.util.jar.*;
import java.util.zip.*;
import org.jcsp.lang.*;
import org.jcsp.net.*;

/**
 * <p>A server process for dispatching binary images of classes to nodes that do not hold suitable
 * definitions locally. An instance of this process is started for a node by the <code>DynamicClassLoader</code>
 * service. A request channel is connected to this process through which remote nodes can request
 * images of classes.</p>
 *
 * <p>Classes can be retrieved from individual </code>.class</code> files in the class path or from a
 * Java archive. The current implementation does not support compressed archives. If a file from an
 * archive is requested, the remote node will be sent a manifest listing all of the other classes
 * available in the archive. This allows the remote node to better determine where to request classes
 * from in the case of objects being passed through a lengthy pipeline of nodes.</p>
 *
 * <p>Once a remote node has requested a file from an archive the rest of the archive will be queued
 * for dispatch to that node. This preemptive forwarding of information can give more reliable
 * performance by increasing the likelihood of a node passing on all of the definitions its peers
 * might require before it terminates.</p>
 *
 * @author Quickstone Technologies Limited
 */
class JFTP implements CSProcess
{
   /**
    * Constructs a new JFTP process.
    *
    * @param classLoader the class loader to obtain resources held locally from.
    * @param req the request channel for communication with other nodes.
    * @param cm the local class manager responsible for classes dynamically loaded by this node.
    */
   public JFTP(ClassLoader classLoader, AltingChannelInput req, ClassManager cm)
   {
      this.classLoader = classLoader;
      this.req = req;
      this.cm = cm;
   }
   
   /**
    * Main process thread, servicing requests sent on the <code>req</code> channel. Once a request is
    * received, a process will be spawned to complete the operation and allow another node to be
    * serviced. Where possible, requests for the same class are combined so that it is only loaded
    * once.
    */
   public void run()
   {
      final One2OneChannel toQueueFlusher = Channel.one2one(), fromQueueFlusher = Channel.one2one();
      final AltingChannelInput notifyIn = classNotify.in(), queueIn = classQueue.in(), flushNotifyIn = fromQueueFlusher.in();
      final ChannelOutput notifyOut = classNotify.out(), queueOut = classQueue.out();
      // This process will be sent class names from the output queue. It will look up the
      // class file and pass the result back on the notify channel. Any nodes that are marked
      // as needing this file will then be sent it.
      new ProcessManager(new QueuedClassLoaderProcess(toQueueFlusher.in(), notifyOut, queueOut, fromQueueFlusher.out())).start();
      final Alternative alt = new Alternative(new Guard[] { queueIn, notifyIn, req, flushNotifyIn, new Skip() });
      final boolean cond[] = new boolean[] { true, true, true, true, true };
      boolean queueEnable = true;
      int queueCount = 0;
      while(true)
      {
         try
         {
            cond[4] = queueEnable && (queueCount > 0);
            switch (alt.priSelect(cond))
            {
               case 0 :
               {
                  // Request added to queue. The class request has not actually been issued by
                  // a node yet. It has been generated by the loader as the node has requested
                  // another class from the same archive.
                  ClassRequest cr = (ClassRequest)queueIn.read();
                  ChanSet cs = (ChanSet)outputQueue.get(cr.className);
                  if (cs == null)
                  {
                     cs = new ChanSet(cr.replyChan);
                     outputQueue.put(cr.className, cs);
                     queueCount++;
                  }
                  else
                     cs.addRequest(cr.replyChan);
                  break;
               } 
               case 1 :
               { 
                  // Notification of class loaded
                  // Send the class to all waiting processes. The class has now been loaded.
                  // All nodes that requested the class (or were marked as wanting it as a
                  // result of a manifest check) are sent it and the class is removed from the
                  // output queue.
                  ClassReply cr = (ClassReply)notifyIn.read();
                  ChanSet cs = (ChanSet)outputQueue.remove(cr.className);
                  cs.writeToAll(cr);
                  break;
               } 
               case 2 :
               { 
                  // Request for a class
                  // Tag the node onto an existing request if there is one. Start a new thread
                  // to load the class. Starting a new thread solves the priority problem. A
                  // thread is not started if the class is already being processed by another
                  // unless a manifest is requested. If a thread is not started, the requesting
                  // node will receive the data when the other thread sends it on the notify
                  // channel.
                  final ClassRequest cr = (ClassRequest) req.read();
                  Node.info.log(this, "JFTP Received a request for " + cr.className);
                  ChanSet cs = (ChanSet)outputQueue.get(cr.className);
                  final boolean loadManifest, loadClass;
                  if (cs == null)
                  {
                     // No one else is waiting for the class, so create a response object
                     // and start a process to go and get the class
                     cs = new ChanSet(cr.replyChan);
                     loadClass = true;
                     loadManifest = ((cr.flags & CR_WANT_MANIFEST) != 0);
                     outputQueue.put(cr.className, cs);
                  }
                  else
                  {
                     cs.addRequest(cr.replyChan);
                     if (cs.isBeingProcessed())
                     {
                        if ((cr.flags & CR_WANT_MANIFEST) != 0)
                        {
                           loadClass = false;
                           loadManifest = true;
                        }
                        else
                           loadClass = loadManifest = false;
                     }
                     else
                     {
                        loadClass = true;
                        loadManifest = ((cr.flags & CR_WANT_MANIFEST) != 0);
                     }
                  }
                  if (loadClass || loadManifest)
                  {
                     cs.nowBeingProcessed();
                     new ProcessManager
                             (new CSProcess()
                              {
                                 public void run()
                                 {
                                    findAndLoadClass(cr.className, loadClass, loadManifest, cr.replyChan, notifyOut, queueOut);
                                 }
                              }).start();
                  }
                  break;
               } 
               case 3 :
               { 
                  // Previous queued retrieval has completed; allow another
                  flushNotifyIn.read();
                  queueEnable = true;
                  break;
               } 
               case 4 :
               {
                  // Try and push some data
                  queueEnable = false; // suppress this guard until operation completed
                  // by process sending a message on notify channel
                  Enumeration e = outputQueue.keys();
                  String className = null;
                  ChanSet cs = null;
                  // Find a class that isn't being loaded for anyone
                  do
                  {
                     if (!e.hasMoreElements())
                     {
                        cs = null;
                        break;
                     }
                     className = (String)e.nextElement();
                     cs = (ChanSet)outputQueue.get(className);
                  } while (cs.isBeingProcessed());
                  if (cs == null)
                  {
                     queueCount = 0;
                     break;
                  }
                  else
                     queueCount--;
                  cs.nowBeingProcessed();
                  toQueueFlusher.out().write(className);
                  // When the queued class loader process finishes this class it will send a
                  // value on the flushNotify channel which will reenable this guard.
                  break;
               }
            }
         }
         catch (Exception e)
         {
            Node.err.log(this, "Error ignored");
            Node.err.log(this, e);
         }
      }
   }
   
   /**
    * Child process spawned by the <code>run()</code> method of <code>JFTP</code> to retrieve a class
    * that has been queued for output to another node.
    */
   private class QueuedClassLoaderProcess implements CSProcess
   {
      private final ChannelInput classIn;
      private final ChannelOutput notifyOut;
      private final ChannelOutput queueOut;
      private final ChannelOutput flushOut;
      public QueuedClassLoaderProcess(ChannelInput classIn, ChannelOutput notifyOut, 
                                      ChannelOutput queueOut, ChannelOutput flushOut)
      {
         this.classIn = classIn;
         this.notifyOut = notifyOut;
         this.queueOut = queueOut;
         this.flushOut = flushOut;
      }
      
      public void run()
      {
         while (true)
         {
            String cn = (String)classIn.read();
            findAndLoadClass(cn, true, false, null, notifyOut, queueOut);
            flushOut.write(null);
         }
      }
   }
   
   /**
    * <p>Attempts to load a class using the local class loader. If the class was not found locally, the
    * class manager is queried - the class might have been dynamically loaded. If the class manager has
    * an image for the file it is sent. If the class manager has marked the class as pending then it
    * will issue a request to the originating node's JFTP process.</p>
    *
    * <p>If the class is loaded locally from a Java archive, a manifest is generated and returned with
    * the class to the client. The other contents of the archive are then queued for transmission to
    * the client. If the class was dynamically loaded by this node and a manifest was received, this
    * manifest is forwarded to the client.</p>
    *
    * @param className name of the class to find.
    * @param wantClass true iff the binary image for the class is required.
    * @param wantManifest true iff a manifest reply is required.
    * @param toRequestingClient manifest replies to the client are sent on this channel.
    * @param notifyOthers the <code>ClassReply</code> is sent on this channel for dispatch to all clients needing it.
    * @param queueFurtherRequest if loading from an archive, new class requests are generated for the client on this channel.
    */
   private void findAndLoadClass(String className, boolean wantClass, boolean wantManifest, ChannelOutput toRequestingClient, 
                                 ChannelOutput notifyOthers, ChannelOutput queueFurtherRequest)
   {
      Node.info.log(this, "Beginning " + ((toRequestingClient == null) ? "preemptive " : "") + "load for " + className);
      try
      {
         String filename = className.replace('.', File.separatorChar) + ".class";
         URL url = classLoader.getResource(filename);
         if (url != null)
         {
            if (wantManifest)
            {
               String uStr = url.toString();
               if (uStr.startsWith("jar:file:"))
               {
                  // This is a JAR file - create and send a manifest
                  try
                  {
                     uStr = uStr.substring(9, uStr.indexOf('!'));
                     JarFile jf = new JarFile(uStr);
                     Enumeration e = jf.entries();
                     String[] manifest = new String[jf.size()];
                     int index = 0;
                     for (int i = 0; i < manifest.length; i++)
                     {
                        String n = ((ZipEntry)e.nextElement()).getName();
                        if (n.endsWith(".class"))
                        {
                           String cn = n.substring(0, n.length() - 6);
                           queueFurtherRequest.write(new ClassRequest(cn, toRequestingClient, CR_WANT_CLASS));
                           manifest[index++] = cn;
                        }
                     }
                     toRequestingClient.write(new JarManifestReply(manifest));
                  }
                  catch (IOException e)
                  {
                  }
               }
            }
            if (wantClass)
            {
               // Get the single file requested
               InputStream fileFound = url.openStream();
               int size = fileFound.available();
               byte[] fileBytes = new byte[size];
               fileFound.read(fileBytes, 0, size);
               // Dispatch the file to all waiting processes
               notifyOthers.write(new ClassReply(className, fileBytes));
            }
         }
         else
         {
            // Not held locally; perhaps our class manager has it ...
            JarManifestReply jmr = cm.checkForPendingClass(className);
            byte[] fileBytes = cm.getClassBytes(className);
            if (fileBytes != null)
            {
               if ((jmr != null) && (wantManifest))
               {
                  // Forward manifest of other files from the same source
                  for (int i = 0; i < jmr.elements.length; i++)
                     if (jmr.elements[i] != null)
                        queueFurtherRequest.write(new ClassRequest(jmr.elements[i], toRequestingClient, CR_WANT_CLASS));
                  toRequestingClient.write(jmr);
               }
               if (wantClass)
                  // Dispatch the file to all waiting processes
                  notifyOthers.write(new ClassReply(className, fileBytes));
            }
            else
            {
               if (wantClass)
                  // Dispatch the error to all waiting processes
                  notifyOthers.write(new ClassReply(className, null));
            }
         }
      }
      catch (Throwable e)
      {
         if (wantClass)
            // Dispatch the error to all waiting processes
            notifyOthers.write(new ClassReply(className, null));
      }
   }
   
   /**
    * Child processes spawned to load classes from disk or from another node pass the loaded data on
    * this channel to the main process fror forwarding to the requesting clients.
    */
   private final Any2OneChannel classNotify = Channel.any2one();
   
   /**
    * Child processes spawned to load classes from Java archives will create additional requests on
    * this channel to queue other files from the archive to be sent to a client.
    */
   private final Any2OneChannel classQueue = Channel.any2one();
   
   /**
    * Incoming requests from the clients.
    */
   private final AltingChannelInput req;
   
   /**
    * The default class loader to get locally held classes from.
    */
   private final ClassLoader classLoader;
   
   /**
    * The local class manager for tracking classes that were dynamically loaded by this node.
    */
   private final ClassManager cm;
   
   /**
    * Queues (and combines) requests for classes by clients. Associates <code>String</code> class
    * names with <code>ChanSet</code> objects that contain the <code>ClassRequest</code> objects.
    */
   private final Hashtable outputQueue = new Hashtable();
   
   /**
    * Flag for indicating in a <code>ClassRequest</code> that a manifest is required.
    */
   public static final int CR_WANT_MANIFEST = 0x0001;
   
   /**
    * Flag for indicating in a <code>ClassRequest</code> that the class image is required.
    */
   public static final int CR_WANT_CLASS    = 0x0002;
   
   /**
    * Implementation of a set type structure for holding <code>ChannelOutput</code> objects with
    * a flag to indicate that the request set is currently being processed.
    */
   private static class ChanSet
   {
      /**
       * Holds the <code>ChannelOutput</code> objects.
       */
      private Vector set;
      
      /**
       * <code>true</code> if a class is currently being loaded.
       */
      private boolean beingProcessed;
      
      /**
       * Constructs a new <code>ChanSet</code> with an initial entry in the set.
       */
      public ChanSet(ChannelOutput out)
      {
         set = new Vector(1);
         set.addElement(out);
         beingProcessed = false;
      }
      
      /**
       * Sets the flag to indicate that a class is currently being loaded.
       */
      public void nowBeingProcessed()
      {
         beingProcessed = true;
      }
      
      /**
       * Returns <code>true</code> if a class is being loaded by another thread.
       */
      public boolean isBeingProcessed()
      {
         return beingProcessed;
      }
      
      /**
       * Adds a request (the channel to reply on) to the set.
       *
       * @param out channel to send the reply on.
       */
      public void addRequest(ChannelOutput out)
      {
         for (int i = set.size() - 1; i >= 0; i--)
            if (set.elementAt(i) == out) 
               return;
         set.addElement(out);
      }
      
      /**
       * Broadcast the reply to all of the clients in the set.
       *
       * @param reply the message to send.
       */
      public void writeToAll(ClassReply reply)
      {
         for (Enumeration e = set.elements(); e.hasMoreElements(); )
         {
            ChannelOutput out = (ChannelOutput)e.nextElement();
            out.write(reply);
         }
      }
   }
   
   /**
    * Represents a class request, indicating the class required, the channel to reply to the client
    * on and the flags to indicate whether a manifest is also wanted.
    */
   static class ClassRequest implements Serializable
   {
      public ClassRequest(String className, ChannelOutput replyChan, int flags)
      {
         this.className = className;
         this.replyChan = replyChan;
         this.flags = flags;
      }
      
      public final String className;
      public final ChannelOutput replyChan;
      public final int flags;
   }
   
   /**
    * Represents a reply to a client for a loaded class, giving the class name and binary image.
    */
   static class ClassReply implements Serializable
   {
      public ClassReply(String className, byte[] fileBytes)
      {
         this.className = className;
         this.fileBytes = fileBytes;
      }
      
      public final String className;
      public final byte[] fileBytes;
   }
   
   /**
    * Represents a reply to a client detailing a manifest of an archive.
    */
   static class JarManifestReply implements Serializable
   {
      public JarManifestReply(String[] elements)
      {
         this.elements = elements;
      }
      
      public final String[] elements;
   }
}