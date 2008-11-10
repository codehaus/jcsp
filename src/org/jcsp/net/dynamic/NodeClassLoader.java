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

import java.util.*;
import org.jcsp.lang.*;
import org.jcsp.net.*;

/**
 * Custom class loader for retrieving class files from a JFTP process on another node. Each instance
 * will spawn a child process that will keep running. When a class is requested from the loader, it
 * will synchronize with the child process which will contact a JFTP process and wait for data. When
 * the class arrives the original caller will resume and register the class. Using a separate thread
 * allows the JFTP process to push other classes to the class loader. Any classes pushed in this manner
 * will be registered when the next class is requested from the class loader.
 *
 * @author Quickstone Technologies Limited
 */
class NodeClassLoader extends ClassLoader
{
  /* loadClass in ClassLoader is synchronized so there can only ever be one pending class
   * request. If further classes are pushed to the loader thread it will keep them in a collection
   * until another request arrives. The next request will return an array of JFTP replies for
   * classes to be registered. This array will include the actual one requested if it is available.
   */

   /**
    * Child process to communicate with the JFTP process, issuing requests to it and waiting for
    * replies.
    */
   private static class LoaderThread implements CSProcess
   {
      
      /**
       * Constructs a new <code>LoaderThread</code>.
       *
       * @param sourceChannelLocation location of a JFTP process request channel.
       * @param classRequest requests for classes will be received on this channel.
       * @param classResponse when classes have arrived they will be passed out on this channel.
       * @param deferredLoader the class loader that owns this process which will be associated with
       *                       pending classes found in a manifest response.
       * @param classManager the class manager for managing classes dynamically loaded by this node.
       */
      public LoaderThread(NetChannelLocation sourceChannelLocation, AltingChannelInput classRequest, 
                          ChannelOutput classResponse, ClassLoader deferredLoader, ClassManager classManager)
      {
         this.sourceChannelLocation = sourceChannelLocation;
         this.classRequest = classRequest;
         this.classResponse = classResponse;
         this.deferredLoader = deferredLoader;
         this.classManager = classManager;
      }
      
      /**
       * Main process loop, servicing requests for classes from this node and receiving class replies
       * from the JFTP process of a remote node.
       */
      public void run()
      {
         try
         {
            NetChannelOutput out = NetChannelEnd.createOne2Net(sourceChannelLocation);
            NetAltingChannelInput classIn = NetChannelEnd.createNet2One();
            NetChannelOutput classOut = NetChannelEnd.createOne2Net(classIn.getChannelLocation());
            Alternative alt = new Alternative(new Guard[] { classIn, classRequest });
            LinkedList classesToRegister = new LinkedList();
            String classPending = null;
            Node.info.log(this, "Node class loader for " + sourceChannelLocation + " started");
            while (true)
            {
               if (alt.priSelect() == 0)
               {
                  Object objReply = classIn.read();
                  if (objReply instanceof JFTP.ClassReply)
                  {
                     JFTP.ClassReply cr = (JFTP.ClassReply)objReply;
                     classesToRegister.add(cr);
                     Node.info.log(this, "Definition for " + cr.className + " has arrived");
                     // Notify a waiting thread
                     if (classPending != null)
                     {
                        if (classPending.equals(cr.className))
                        {
                           classResponse.write(classesToRegister.toArray());
                           classesToRegister.clear();
                           classPending = null;
                        }
                     }
                  }
                  else if (objReply instanceof JFTP.JarManifestReply)
                  {
                     // Mark classes as pending ...
                     JFTP.JarManifestReply jmr = (JFTP.JarManifestReply)objReply;
                     for (int i = 0; i < jmr.elements.length; i++)
                        if (jmr.elements[i] != null)
                           classManager.classPending(jmr.elements[i], deferredLoader, jmr);
                  }
               }
               else
               {
                  // A request to retrieve a class has arrived
                  classPending = (String)classRequest.read();
                  int mode = JFTP.CR_WANT_CLASS;
                  if (!classManager.pendingClassManifestAvailable(classPending))
                     mode |= JFTP.CR_WANT_MANIFEST;
                  // Issue request to source node
                  out.write(new JFTP.ClassRequest(classPending, classOut, mode));
               }
            }
         }
         catch (Exception e)
         {
            Node.err.log(this, e);
         }
         finally
         {
            Node.info.log(this, "Node class loader for " + sourceChannelLocation + " terminated");
         }
      }
      
      /**
       * Location of a JFTP process request channel.
       */
      private final NetChannelLocation sourceChannelLocation;
      
      /**
       * Requests for classes will be received on this channel.
       */
      private final AltingChannelInput classRequest;
      
      /**
       * When classes have arrived they will be passed out on this channel.
       */
      private final ChannelOutput classResponse;
      
      /**
       * The class loader owning this process which should be associated with classes marked as
       * pending.
       */
      private final ClassLoader deferredLoader;
      
      /**
       * The class manager for managing classes dynamically loaded by this node.
       */
      private final ClassManager classManager;
   }
   
   /**
    * Constructs a new <code>NodeClassLoader</code> for loading classes held by a JFTP process at
    * another node. On construction a child thread is spawned.
    *
    * @param sourceChannelLocation location of the request channel of a remote JFTP process.
    * @param cm class manager responsible for dynamically loaded classes at this node.
    */
   public NodeClassLoader(NetChannelLocation sourceChannelLocation, ClassManager cm)
   {
      new ProcessManager(new LoaderThread(sourceChannelLocation, classReq.in(), classResp.out(), this, cm)).start();
      classManager = cm;
   }
   
   /**
    * Issues a request to the child process to get a class definition from the remote JFTP process.
    * If the JFTP process has been pushing class definitions to this node the child process may return
    * multiple results. One of them will be the requested class. All will be registered and a note
    * is taken of the one requested so that it can be returned.
    *
    * @param name name of the class to load.
    * @return the class instance.
    * @throws ClassNotFoundException if the remote node did not have the class.
    */
   public Class findClass(String name) throws ClassNotFoundException
   {
      // Request the class from the loader thread
      classReq.out().write(name);
      Object[] replies = (Object[])classResp.in().read();
   
      // Register all of the classes returned by the loader
      Class found = null;
      for (int i = 0; i < replies.length; i++)
      {
         JFTP.ClassReply reply = (JFTP.ClassReply)replies[i];
         if (reply.fileBytes != null)
         {
            Class cls;
            try
            {
               cls = defineClass(reply.className, reply.fileBytes, 0, reply.fileBytes.length);
               classManager.registerClass(cls, reply.fileBytes);
            }
            catch (LinkageError e)
            {
               cls = loadClass(reply.className);
            }
            // Take note of the one actually requested
            if (reply.className.equals(name)) found = cls;
         }
         else
            Node.info.log(this, "Class " + reply.className + " not available at source node");
      }
      if (found == null)
         throw new ClassNotFoundException("Class " + name + " not found.");
      return found;
   }
   
   /**
    * Channel for passing requests from the <code>findClass</code> method to the child process.
    */
   private final One2OneChannel classReq = Channel.one2one();
   
   /**
    * Channel for returning data from the child process to the <code>findClass</code> method.
    */
   private final One2OneChannel classResp = Channel.one2one();
   
   /**
    * The class manager responsible for managing dynamically loaded classes at this node.
    */
   private final ClassManager classManager;
}