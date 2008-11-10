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
 * Used internally within the JCSP network infrastructure to represent a single link profile.
 *
 * @author Quickstone Technologies Limited
 */
public class LinkProfile
{
   public LinkProfile(String name, boolean exactMatchRequired)
   {
      this.name = name;
      this.exactMatchRequired = exactMatchRequired;
   }
   
   public void addReq(Req req)
   {
      if(req instanceof MinSpeed && minSpeed == null)
         minSpeed = (MinSpeed) req;
      else if(req instanceof Wireless && wireless == null)
      {
      }
      else if(req instanceof ReqProtocol && reqProtocol == null)
         reqProtocol = (ReqProtocol) req;
      else if(req instanceof Reliable && reliable == null)
         reliable = (Reliable) req;
      else if(req instanceof ConnectionOriented && connectionOriented == null)
         connectionOriented = (ConnectionOriented) req;
      else if(req instanceof MaxPing && maxPing == null)
         maxPing = (MaxPing) req;
      else if(req instanceof OtherReq && !reqs.contains(req))
         reqs.put(req, req);
      else
         throw new ReqAlreadyExistsException("Already have a req named " + req.getName());
   }
   
   public void removeReq(Req req)
   {
      if(minSpeed == req)
         minSpeed = null;
      else if(reqProtocol == req)
         reqProtocol = null;
      else if(wireless == req)
         wireless = null;
      else if(reliable == req)
         reliable = null;
      else if(connectionOriented == req)
         connectionOriented = null;
      else if(maxPing == req)
         maxPing = null;
      else if(req instanceof OtherReq && reqs.contains(req))
         reqs.remove(req);
   }
   
   public Req[] getReqs()
   {
      int count=0;
      if (minSpeed != null) 
         count++;
      if (reqProtocol != null) 
         count++;
      if (wireless != null) 
         count++;
      if (reliable != null) 
         count++;
      if (maxPing != null) 
         count++;
      if (connectionOriented != null) 
         count++;
      count += reqs.size();
      Req[] toReturn = new Req[count];
      int pos = 0;
     
      if(minSpeed != null)
      {
         toReturn[pos] = minSpeed;
         pos++;
      }
      if(reqProtocol != null)
      {
         toReturn[pos] = reqProtocol;
         pos++;
      }
      if(wireless != null)
      {
         toReturn[pos] = wireless;
         pos++;
      }
      if(reliable != null)
      {
         toReturn[pos] = reliable;
         pos++;
      }
      if(connectionOriented != null)
      {
         toReturn[pos] = connectionOriented;
         pos++;
      }
      if(maxPing != null)
      {
         toReturn[pos] = maxPing;
         pos++;
      }
      
      for (Enumeration it = reqs.keys(); it.hasMoreElements(); )
      {
         toReturn[pos] = (Req) it.nextElement();
         pos++;
      }
      return toReturn;
   }
   
   public String getName()
   {
      return name;
   }
   
   public boolean getExactMatchRequired()
   {
      return exactMatchRequired;
   }
   
   public boolean equals(Object o)
   {
      if(o!= null && o instanceof LinkProfile)
      {
         LinkProfile other = (LinkProfile) o;
         if((minSpeed == other.minSpeed || (minSpeed != null && minSpeed.equals(other.minSpeed))) 
            && (reqProtocol == other.reqProtocol || (reqProtocol != null && reqProtocol.equals(other.reqProtocol)))
            && (wireless == other.wireless || (wireless != null && wireless.equals(other.wireless)))
            && (reliable == other.reliable || (reliable != null && reliable.equals(other.reliable)))
            && (connectionOriented == other.connectionOriented || (connectionOriented != null 
                                                                  && connectionOriented.equals(other.connectionOriented)))
            && (maxPing == other.maxPing || (maxPing != null && maxPing.equals(other.maxPing)))
            && reqs.equals(other.reqs))
         {
            return true;
         }
      }
      return false;
   }
   
   public String toString()
   {
      StringBuffer sb = new StringBuffer();
      
      Req[] reqs = getReqs();
      sb.append("<LinkProfile name=\"" + name + "\" exactMatchRequired=\"" + exactMatchRequired + "\">\n");
      if(reqs.length > 0)
         sb.append(JCSPConfig.tabIn("<Reqs>")).append("\n");
      for(int i=0; i<reqs.length; i++)
         sb.append(JCSPConfig.tabIn(JCSPConfig.tabIn(reqs[i].toString()))).append("\n");
      if(reqs.length > 0) 
         sb.append(JCSPConfig.tabIn("</Reqs>")).append("\n");
      sb.append("</LinkProfile>");
      return sb.toString();
   }
   
   public int hashCode()
   {
      return reqs.hashCode();
   }
   
   private MinSpeed minSpeed = null;
   private ReqProtocol reqProtocol = null;
   private Wireless wireless = null;
   private Reliable reliable = null;
   private ConnectionOriented connectionOriented = null;
   private MaxPing maxPing = null;
   private Hashtable reqs = new Hashtable();
   private String name;
   private boolean exactMatchRequired;
   
   static class ReqAlreadyExistsException extends RuntimeException
   {
      private ReqAlreadyExistsException(String msg)
      {
         super(msg);
      }
   }
}