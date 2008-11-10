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
import java.util.*;

/**
 * <p>Manages the output of diagnostic messages to one or more devices. Instances of this class represent
 * a log output device to which diagnostic or error messages can be written. The instance can be tied to
 * an actual device such as a file or the standard I/O streams provided by the <code>System</code> class.</p>
 *
 * <p>All messages must be signed by an application class and may also contain a severity level. The higher
 * the number the less severe the problem. The output generated can be controlled by setting the maximum level
 * that should be reported. Thus, essential output should be written at level 1. Less severe and verbose
 * output should be written at higher levels depending on the granularity of information required when
 * testing a program.</p>
 *
 * <p>The default logging level is 5. That is, for all classes only messages up to level 5 by default will
 * be displayed.</p>
 *
 * <p>Messages from the infrastructure classes are written to the loggers <code>Node.info</code> and
 * <code>Node.err</code> which are by default connected to <code>System.out</code> and <code>System.in</code>
 * respectively.</p>
 *
 * <p>Alternative implementations of this class may instead write to a system event log or format the
 * information in manners tailored to particular debugging environments.</p>
 *
 * @author Quickstone Technologies Limited
 */

public class Logger
{
   //------------------------- Constructors -----------------------------------------------//
   
   /**
    * <p>Creates a new <code>Logger</code> with a given name. The name assigned is arbitrary but allows the
    * logger to be uniquely identified by name. For example instead of assigning a static reference
    * somewhere to always refer to a logger, one can be named and resolved dynamically:</p>
    *
    * <pre>
    * public static void main (String[] args) {
    *   new Logger ("errors", "stderr");
    * }
    * </pre>
    *
    * <p>This will allocate a logger with the name <Code>errors</code> connected to <code>System.err</code>.
    * Elsewhere in the program one can write:</p>
    *
    * <pre>
    * Logger.getLogger ("errors")
    * </pre>
    *
    * <p>To obtain a reference to this logger.</p>
    *
    * <p>The default device can take one of three reserved values:</p>
    * <ul>
    *   <li><em>null</em> - no output device; all logged events are discarded.</li>
    *   <li><em>stdout</em> - write to <code>System.out</code>.</li>
    *   <li><em>stderr</em> - write to <code>System.err</code>.</li>
    * </ul>
    *
    * <p>If none of these values match it is assumed to be a filename.</p>
    *
    * @param name the system unique name of the logger.
    * @param defaultDevice the output device to use.
    */
   public Logger(String name, String defaultDevice)
   {
      this.name = name;
      synchronized (all)
      {
         all.put(name, this);
      }
      // code to get device settings from preferences or system properties perhaps
      setDevice(defaultDevice);
      // load the default logging levels from somewhere
      setLevel("java.lang.Object", DEFAULT_LOGGING_LEVEL);
   }
   
   //------------------------- Public Methods ---------------------------------------------//
   
   /**
    * <p>Returns a named logger within the system. Refer to the constructor for an example of its use.</p>
    *
    * @param name the name of the logger
    * @return the logger
    * @throws InvalidLoggerException if the logger specified doesn't exist.
    */
   public static Logger getLogger(String name)
   {
      synchronized (all)
      {
         Logger s = (Logger)all.get(name);
         if (s == null) throw new InvalidLoggerException(name);
         return s;
      }
   }
   
   /**
    * <p>Sets the current output device for this logger.  For example, to suppress infrastructure messages:</p>
    *
    * <pre>
    *   Node.info.setDevice (null);
    * </pre>
    *
    * @param device the new device to use.
    */
   public void setDevice(String device)
   {
      output = null;
      if (device == null) 
         output = null;
      else if (device.equals("stderr")) 
         output = System.err;
      else if (device.equals("stdout")) 
         output = System.out;
      else
         try
         {
            output = new PrintStream(new FileOutputStream(device));
         }
         catch (IOException e)
         {
            // Not really appropriate if the error log fails :(
            Node.err.log(this, "Unable to open device '" + device + "' for " + name + " log - " + e.getMessage());
         }
   }
   
   /**
    * <p>Sets the current logging level for a given class (and its subclasses). Only messages generated by
    * that class (or its subclasses) with a lesser level will be output.</p>
    *
    * @param clazz the name of the class.
    * @param level the maximum level to display.
    */
   public synchronized void setLevel(String clazz, int level)
   {
      // clear cached data (i.e. anything with a positive value)
      if (levelsCached)
      {
         Hashtable temp = new Hashtable();
         for (Enumeration e = levels.keys(); e.hasMoreElements(); )
         {
            Object key = e.nextElement();
            Integer i = (Integer)levels.get(key);
            if (i.intValue() < 0)
               temp.put(key, i);
         }
         levels = temp;
         levelsCached = false;
      }
      // set the actual data
      if (level <= 0)
         levels.remove(clazz);
      else
         levels.put(clazz, new Integer(-level));
   }
   
   //------------------------- Logging constants ------------------------------------------//
   
   /** The logging level for <em>really</em> important messages. */
   public static final int MAX_LOGGING = 1;
   /** The default logging level (currently 5) */
   public static final int DEFAULT_LOGGING_LEVEL = 5;
   /** The default string if the class name is omitted. */
   public static final String DEFAULT_CLASS_NAME = "Application";
   
   //------------------------- Methods to do the logging ----------------------------------//
   
        /* TODO:
         *
         * Need to get settings from somewhere
         * Need to configure what gets displayed; eg show class names ?
         * Need to configure calls throughout the infrastructure
         */
   
   /**
    * Log a message at the default level with the default class name.
    *
    * @param message the message to output.
    */
   public void log(Object message)
   {
      if (output == null) 
         return;
      log(DEFAULT_LOGGING_LEVEL, message);
   }
   
   /**
    * Log a message at a specific level with the default class name.
    *
    * @param level the logging level.
    * @param message the message.
    */
   public void log(int level, Object message)
   {
      if (output == null) 
         return;
      log(DEFAULT_CLASS_NAME, level, message);
   }
   
   private synchronized void logImpl(String className, int level, Object message)
   {
      if (output == null) 
         return;
      if (message instanceof String)
      {
         String lineEnd = "";
         if (!lastClass.equals(className))
         {
            lastClass = className;
            output.println(className);
         }
         String str = (String)message + lineEnd;
         if (str.indexOf('\n') >= 0)
         {
            output.print('\t');
            for (int i = 0; i < str.length(); i++)
            {
               if (str.charAt(i) == '\n')
                  output.print("\n\t");
               else
                  output.print(str.charAt(i));
            }
            output.println();
         }
         else
            output.println("\t" + message + lineEnd);
      }
      else if (message instanceof Exception)
      {
         Exception e = (Exception)message;
         output.println("(" + level + ")\tException caught in " + className);
         e.printStackTrace(output);
      }
      else
      {
         logImpl(className, level, message.toString());
      }
   }
   
   /**
    * Log a message at the specified level with the specific class name. Note that this does not have to
    * match a real class name so could be used to describe the subsystem generating the message more
    * meaningfully.
    *
    * @param className the class or component name.
    * @param level the logging level.
    * @param message the message.
    */
   public void log(String className, int level, Object message)
   {
      if (output == null) return;
      try
      {
         log(Class.forName(className), level, message);
      }
      catch (ClassNotFoundException e)
      {
         // see if logging is enabled or disabled for the name ...
         int maxLevel;
         Integer lvl = (Integer)levels.get(className);
         if (lvl == null)
            maxLevel = DEFAULT_LOGGING_LEVEL;
         else
            maxLevel = lvl.intValue();
         if (level <= maxLevel)
            logImpl(className, level, message);
      }
   }
   
   // Find level this class
   private synchronized int findMaxLevel(Class clazz)
   {
      int l, max = 0;
      
      // Check if there is an entry for this class (and use it)
      Integer lvl = (Integer)levels.get(clazz.getName());
      if (lvl != null)
      {
         l = lvl.intValue();
         return (l < 0) ? -l : l;
      }
      // Check the interfaces
      Class[] ifaces = clazz.getInterfaces();
      for (int i = 0; i < ifaces.length; i++)
      {
         l = findMaxLevel(ifaces[i]);
         if (l > max) 
            max = l;
      }
      // Check the superclass
      Class sup = clazz.getSuperclass();
      if (sup != null)
      {
         l = findMaxLevel(sup);
         if (l > max) 
            max = l;
      }
      // Update the entry for this class to speed up subsequent operations
      if (max > 0)
      {
         //System.out.println("Updating " + clazz.getName() + " from hierarchy to " + max);
         levelsCached = true;
         levels.put(clazz.getName(), new Integer(max));
      }
      return max;
   }
   
   /**
    * Log a message at the specified level with the specific class.
    *
    * @param class the class generating the message.
    * @param level the logging level.
    * @param message the message.
    */
   public void log(Class clazz, int level, Object message)
   {
      if (output == null) 
         return;
      // go through the classes to determine the maximum level
      if (level <= findMaxLevel(clazz))
         logImpl(clazz.getName(), level, message.toString());
   }
   
   /**
    * Log a message at the specified level with the class of the given object.
    *
    * @param class the object whose class has generated the message.
    * @param level the logging level.
    * @param message the message.
    */
   public void log(Object object, int level, Object message)
   {
      if (output == null) 
         return;
      log(object.getClass(), level, message);
   }
   
   /**
    * Log a message with the specific class.
    *
    * @param class the class generating the message.
    * @param message the message.
    */
   public void log(String className, Object message)
   {
      if (output == null) 
         return;
      log(className, DEFAULT_LOGGING_LEVEL, message);
   }
   
   /**
    * Log a message with the specific class.
    *
    * @param class the class generating the message.
    * @param message the message.
    */
   public void log(Class clazz, Object message)
   {
      if (output == null) 
         return;
      log(clazz, DEFAULT_LOGGING_LEVEL, message);
   }
   
   /**
    * Log a message with the specific class.
    *
    * @param class the class generating the message.
    * @param message the message.
    */
   public void log(Object object, Object message)
   {
      if (output == null) 
         return;
      log(object, DEFAULT_LOGGING_LEVEL, message);
   }
   
   //------------------------- Attributes -------------------------------------------------//
   
   private String name;
   private PrintStream output = null; // underlying output stream
   private String lastClass = "";
   private Hashtable levels = new Hashtable();
   private boolean levelsCached = false;
   private static final Hashtable all = new Hashtable();
   
   //------------------------- Inner Classes ----------------------------------------------//
   
   private static class InvalidLoggerException extends RuntimeException
   {
      public InvalidLoggerException(String name)
      {
         super("Invalid logger - " + name);
      }
   }
}