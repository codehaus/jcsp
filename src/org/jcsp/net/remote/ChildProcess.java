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

package org.jcsp.net.remote;

import java.io.*;
import org.jcsp.lang.*;
import org.jcsp.net.*;

/**
 * <p>For use by the infrastructure only.</p>
 *
 * <p>Process launcher for starting a child JVM. This class should not be used directly. On loading the
 * process to be started will be retrieved from the main <code>SpawnerService</code> JVM and run. The
 * .NET node will be initialized using the factory supplied by the spawner. This may be either the
 * platform's default factory or one supplied to the proxy <code>ChildProcess</code> processes.</p>
 *
 * <p>The process started should not call <code>System.exit</code> to terminate. If it does a
 * <code>RemoteProcessFailedException</code> will be raised as an abrupt exit cannot be distinguished
 * from a .NET infrastructure failure.</p>
 *
 * <p>If the process raises an exception, this will be forwarded to the caller if possible.</p>
 *
 * @author Quickstone Technologies Limited
 */
public class ChildProcess
{
   /**
    * Program entry point. An argument will give the name of a temporary file that contains serialized
    * representations of the process and other information required to locate the originating host.
    *
    * @param args program command line arguments
    */
   public static void main(String[] args)
   {
      Throwable eToThrow = null;
      NetChannelLocation ncl = null;
      try
      {
         
         NodeFactory nf = null;
         CSProcess p = null;
         
         File f = new File(args[0]);
         try
         {
            ObjectInputStream os = new ObjectInputStream(new FileInputStream(f));
            nf = (NodeFactory)os.readObject();
            p = (CSProcess)os.readObject();
            RemoteProcess.applicationID = (ApplicationID)os.readObject();
            ncl = (NetChannelLocation)os.readObject();
            os.close();
         }
         finally
         {
            f.delete();
         }
         
         if (nf != null) 
            Node.setNodeFactory(nf);
         Node.getInstance().init();
         p.run();
      }
      catch (Throwable e)
      {
         eToThrow = e;
      }
      // Try and route the exception back (or send the null for an OK signal)
      try
      {
         NetChannelOutput out = NetChannelEnd.createOne2Net(ncl);
         out.write(eToThrow);
         System.exit(0);
      }
      catch (Throwable e)
      {
         System.err.println("\n");
         System.err.println("Unable to route exception back to calling application");
         System.err.println("Reason for not being able to route:");
         e.printStackTrace();
         System.err.println("Exception:");
         eToThrow.printStackTrace();
      }
      finally
      {
         System.exit(1);
      }
   }
}