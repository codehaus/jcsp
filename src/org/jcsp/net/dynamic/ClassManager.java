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
 * <p>A process for retrieving and registering classes that cannot be found locally. A single instance
 * of this process runs, with other components in the dynamic class loading infrastructure invoking
 * the public methods to request the loading of a class.</p>
 *
 * <p>When a class is requested, the system class loader will be tried first. If this fails, a cache
 * of classes received dynamically will be tried. If this fails, and a {@link NodeClassLoader} has
 * already been started for communication with the remote node, it will be used to retrieve the class
 * file from that node. If a <code>NodeClassLoader</code> has not been started, one will be created
 * and used for all subsequent requests to be issued to the remote node.</p>
 *
 * @author Quickstone Technologies Limited
 */
class ClassManager implements CSProcess
{
   /**
    * Process execution method. Requests are issued to the process by calling the other methods. These
    * will synchronize with the process thread and transfer information to it via internal channels.
    */
   public void run()
   {
      Alternative alt = new Alternative(new Guard[] { classRegChan.in(), classReqs.in()});
      RegisteredClass lookupReg = new RegisteredClass();
      while (true)
      {
         Class regClass;
         switch (alt.priSelect())
         {
            case 0 :
               //This registers a class. If a class already exists
               //with the same name and SerializedVersion number then
               //the existing class will be returned.
               try
               {
                  regClass = (Class) classRegChan.in().read();
                  lookupReg.name = regClass.getName();
                  classNoLongerPending(lookupReg.name);
                  Class classToReturn = (Class) classes.get(lookupReg);
                  if (classToReturn == null)
                  {
                     classToReturn = regClass;
                     classes.put(lookupReg.clone(), regClass);
                  }
                  classRegReplyChan.out().write(classToReturn);
               }
               catch (Exception e)
               {
                  e.printStackTrace();
               }
               break;
            case 1 :
               final ClassRequest cr = (ClassRequest)classReqs.in().read();
              
               //check to see if the system class loader can get the class
               try
               {
                  regClass = systemClassLoader.loadClass(cr.name);
                  cr.replyChan.write(regClass);
                  break;
               }
               catch (ClassNotFoundException e)
               {
               }
               lookupReg.name = cr.name;
               
               regClass = (Class) classes.get(lookupReg);
               if (regClass != null)
               {
                  cr.replyChan.write(regClass);
                  break;
               }
               ClassLoader cl = (ClassLoader)classLoaders.get(cr.classSourceChannelLoc);
               if (cl == null)
               {
                  NodeClassLoader ncl = new NodeClassLoader(cr.classSourceChannelLoc, this);
                  classLoaders.put(cr.classSourceChannelLoc, ncl);
                  cl = ncl;
               }
               final ClassLoader clToUse = cl;
               new ProcessManager(new CSProcess()
                                 {
                                    public void run()
                                    {
                                       try
                                       {
                                          Class foundClass = clToUse.loadClass(cr.name);
                                          cr.replyChan.write(foundClass);
                                       }
                                       catch (ClassNotFoundException e)
                                       {
                                          cr.replyChan.write(e);
                                       }
                                    }
                                 }).start();
         }
      }
   }
   
   /**
    * <p>Marks a class as scheduled for later arrival. This is called when a <code>NodeClassLoader</code>
    * receives a <code>JarManifestReply</code> from a remote node to indicate that requests for the
    * class should be directed to that class loader. If a JFTP process receives a request for a
    * class that is not held locally it will check the set of pending classes to determine the class
    * loader that it should invoke to retrieve the class.</p>
    *
    * <p>The original manifest reply is also associated with the class so that this can be forwarded to
    * other class loaders replicating the behaviour of the original JFTP process that loaded the class
    * from disk.</p>
    *
    * @param name the name of the class.
    * @param clToUse the class loader that can retrieve the class.
    * @param jmr the manifest reply to associate with the class.
    */
   public void classPending(String name, ClassLoader clToUse, JFTP.JarManifestReply jmr)
   {
      synchronized (classPendings)
      {
         if (classManifests.get(name) == null)
         {
            classPendings.put(name, clToUse);
            classManifests.put(name, jmr);
         }
      }
   }
   
   /**
    * <p>Marks a class as arrived after it was previously marked as pending. This is called when a
    * <code>NodeClassLoader</code> has received a class as a result of an issued request or a JFTP
    * process push.</p>
    *
    * @param name the name of the class that has arrived.
    */
   private void classNoLongerPending(String name)
   {
      synchronized (classPendings)
      {
         classPendings.remove(name);
      }
   }
   
   /**
    * Returns true iff a manifest reply has been stored for the specified class. To retrieve the
    * manifest reply, call <code>checkForPendingClass</code> which will also issue a request to load
    * the class from the remote node.
    *
    * @param className the name of the class to check.
    */
   public boolean pendingClassManifestAvailable(String className)
   {
      synchronized (classPendings)
      {
         return classManifests.get(className) != null;
      }
   }
   
   /**
    * <p>Attempts to load the class from a remote node if it has previously been marked as pending. The
    * class loader associated with the class by a call to <code>classPending</code> will be used. If
    * there is no associated class loader, no action is taken. If there is a manifest reply associated
    * with the class this will be returned for possible forwarding to another node.</p>
    *
    * <p>If this method issues a call to a class loader this may block until the class has arrived. This
    * may take a large amount of time if the class has to be routed through a chain of JFTP processes
    * and class loaders.</p>
    *
    * @param name the name of the class to check.
    */
   public JFTP.JarManifestReply checkForPendingClass(String name)
   {
      ClassLoader cl;
      synchronized (classPendings)
      {
         cl = (ClassLoader) classPendings.get(name);
      }
      if (cl != null)
      {
         try
         {
            cl.loadClass(name);
         }
         catch (ClassNotFoundException e)
         {
         }
      }
      synchronized (classPendings)
      {
         return (JFTP.JarManifestReply)classManifests.get(name);
      }
   }
   
   /**
    * Requests that the class manager process attempt to load the given class. This method will return
    * once synchronization with the main thread has occurred. The caller will be notified of completion
    * of the operation on the reply channel passed.
    *
    * @param name the name of the class to retrieve.
    * @param classSourceChannelLoc location of the channel to request from
    * @param replyChan reply channel to notify the caller of operation completion
    */
   public void getClass(String name, NetChannelLocation classSourceChannelLoc, ChannelOutput replyChan)
   {
      ClassRequest cr = new ClassRequest(name, classSourceChannelLoc, replyChan);
      classReqs.out().write(cr);
   }
   
   /**
    * Stores the binary image for the class to optimize any further requests for the same class.
    * This method will synchronize with the main class manager thread which will update the
    * hash tables to associate the image with the class and mark the class as having arrived if it
    * was previously marked as pending.
    *
    * @param cls class that has been loaded.
    * @param fileBytes the binary image defining the class.
    * @return the class - typically the <Code>cls</code> parameter but possibly a different class if
    *         one has already been registered by another class loader.
    */
   public Class registerClass(Class cls, byte[] fileBytes)
   {
      classRegChan.out().write(cls);
      Class classToReturn = (Class) classRegReplyChan.in().read();
      if (cls == classToReturn && fileBytes != null)
      {
         RegisteredClass rc = new RegisteredClass();
         rc.name = classToReturn.getName();
         classFiles.put(rc, fileBytes);
      }
      return classToReturn;
   }
   
   /**
    * Gets the binary image for a given class.
    *
    * @param name name of the class.
    * @return the binary image registered by a call to <code>registerClass</code>.
    */
   public byte[] getClassBytes(String name)
   {
      RegisteredClass rc = new RegisteredClass();
      rc.name = name;
      byte[] fileBytes = (byte[]) classFiles.get(rc);
      return fileBytes;
   }
   
   /**
    * Used to pass requests to the main process thread from calls to the <code>getClass</code> method.
    */
   private Any2OneChannel classReqs = Channel.any2one();
   
   /**
    * Used to pass requests to the main process thread from calls to the <code>registerClass</code>
    * method.
    */
   private Any2OneChannel classRegChan = Channel.any2one();
   
   /**
    * Used to pass replies from the main process thread to callers of the <code>registerClass</code>
    * method.
    */
   private One2OneChannel classRegReplyChan = Channel.one2one();
   
   /**
    * A local reference to the system class loader. This is held as an attribute so that it would be
    * possible to change the class loader that this process tries by default when resolving classes.
    */
   private ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();
   
   /**
    * Maps <code>RegisteredClass</code> objects to <code>byte[]</code> binary images.
    */
   private Hashtable classes = new Hashtable();
   
   /**
    * Maps <code>NetChannelLocation</code> objects to <code>NodeClassLoader</code> objects to use to
    * retrieve classes from that node.
    */
   private Hashtable classLoaders = new Hashtable();
   
   // source chan -> class loader
   /**
    * Maps <code>String</code> class names to <code>byte[]</code> binary images.
    */
   private Hashtable classFiles = new Hashtable(); // name -> binary
   
   /**
    * Maps <code>String</code> class names to <code>NodeClassLoader</code> objects to use to retrieve
    * them.
    */
   private Hashtable classPendings = new Hashtable(); // name -> class loader
   
   /**
    * Maps <code>String</code> class names to <code>JarManifestReply</code> objects.
    */
   private Hashtable classManifests = new Hashtable();
   // name -> manifest reply
   
   /**
    * Marshals a set of parameters for passing information between <code>getClass</code> and the
    * main process thread.
    */
   private static class ClassRequest
   {
      public ClassRequest(String name, NetChannelLocation classSourceChannelLoc, ChannelOutput replyChan)
      {
         this.name = name;
         this.classSourceChannelLoc = classSourceChannelLoc;
         this.replyChan = replyChan;
      }
      //public final long serialVersionID;
      public final String name;
      public final NetChannelLocation classSourceChannelLoc;
      public final ChannelOutput replyChan;
   }
   
   /**
    * Used in the <code>classes</code> hash table to represent class names. The current implementation
    * just represents a class name, however it could be modified to include version numbers and
    * distinguish classes with the same name but different implementations.
    */
   private static class RegisteredClass implements Cloneable
   {
      public boolean equals(Object o)
      {
         if (o == null || !(o instanceof RegisteredClass))
            return false;
         RegisteredClass other = (RegisteredClass) o;
         return name.equals(other.name);
      }
      
      public int hashCode()
      {
         return (int) /*version +*/
         name.hashCode();
      }
      
      public Object clone() throws CloneNotSupportedException
      {
         return super.clone();
      }
      
      public String name;
   }
}