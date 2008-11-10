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

package org.jcsp.net.settings;

import java.util.*;

/**
 * Used internally within the JCSP network infrastructure to represent a set of protocols.
 *
 * @author Quickstone Technologies Limited
 */
public class Protocols
{
   public void addProtocol(Protocol p)
   {
      if(p != null)
      {
         if(!protocols.contains(p) && !protocolIDMap.containsKey(p.getProtocolID()))
         {
            protocols.put(p, p);
            protocolIDMap.put(p.getProtocolID(), p);
            lastProtocol = p;
         }
      }
      else
         throw new ProtocolAlreadyExistsException("Already have a protocold named " + p.getName());
   }
   
   public void removeProtocol(Protocol p)
   {
      if (protocols.contains(p))
      {
         protocols.remove(p);
         protocolIDMap.remove(p.getProtocolID());
      }
   }
   
   public Protocol getProtocol(String protocolID)
   {
      return (Protocol) protocolIDMap.get(protocolID);
   }
   
   public Protocol getLastProtocol()
   {
      return lastProtocol;
   }
   
   public Protocol[] getProtocols()
   {
      Protocol[] toReturn = new Protocol[protocols.size()];
      return (Protocol[])protocols.keySet().toArray(toReturn);
   }
   
   public String toString()
   {
      StringBuffer sb = new StringBuffer();
      sb.append("<Protocols>\n");
      Protocol[] protocols = getProtocols();
      for(int i=0; i<protocols.length; i++)
         sb.append(JCSPConfig.tabIn(protocols[i].toString())).append("\n");
      sb.append("</Protocols>");
      return sb.toString();
   }
   
   private Hashtable protocols = new Hashtable();
   private Hashtable protocolIDMap = new Hashtable();
   private Protocol lastProtocol = null;
   
   static class ProtocolAlreadyExistsException extends RuntimeException
   {
      private ProtocolAlreadyExistsException(String msg)
      {
         super(msg);
      }
   }
}