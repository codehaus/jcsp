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
 * Message sent from <code>RemoteProcess</code> to <code>SpawnerService</code> listing the details of
 * the process that should be started and a network channel address for replying on.
 *
 * @author Quickstone Technologies Limited
 */
class SpawnerMessage implements Serializable
{
   public final CSProcess process;
   public final NetChannelLocation caller;
   public final NodeFactory factory;
   public final ApplicationID applicationID;
   public final String classPath;
   
   /**
    * Constructs a new message.
    *
    * @param process the process to be spawned.
    * @param caller the location of the <code>RemoteProcess</code>'s channel for replies.
    * @param factory the optional factory for initializing the remote node.
    * @param applicationID the application ID that the remote node should adopt.
    * @param classPath the class path the remote JVM should use.
    */
   public SpawnerMessage(CSProcess process, NetChannelLocation caller, NodeFactory factory, 
                         ApplicationID applicationID, String classPath)
   {
      this.process = process;
      this.caller = caller;
      this.factory = factory;
      this.applicationID = applicationID;
      this.classPath = classPath;
   }
}