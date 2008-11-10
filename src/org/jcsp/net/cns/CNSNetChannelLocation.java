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

package org.jcsp.net.cns;

import org.jcsp.net.*;

/**
 * <p>
 * Instances of this class are returned by the resolve methods
 * of <code>{@link CNSService}</code>. JCSP.NET users cannot create
 * the objects directly.
 * </p>
 * <p>
 * The objects returned by the <code>{@link CNSService}</code> can be used
 * in place of normal <code>NetChannelLocation</code> objects.
 * </p>
 * @author Quickstone Technologies Limited
 */
public class CNSNetChannelLocation extends NetChannelLocation
{
   
   /*-----------Constructors----------------------------------------------------*/
   
   CNSNetChannelLocation(NetChannelLocation locToClone, String name, NameAccessLevel accessLevel, 
                         CNSService cnsService, String cnsServiceName)
   {
      super(locToClone);
      this.name = name;
      this.accessLevel = accessLevel;
      this.cnsResolver = cnsService;
      this.cnsServiceName = cnsServiceName;
   }
   
   /*-----------Private fields--------------------------------------------------*/
   
   private String name;
   
   private NameAccessLevel accessLevel;
   
   private transient CNSUser cnsResolver = null;
   
   private String cnsServiceName = null;
   
   /*-----------Overriden methods from NetChannelLocation-----------------------*/
   
   /**
    * This method requests that the instance of this class refresh
    * its information.
    *
    * The method will re-resolve the location of the
    * <code>NetChannelInput</code> from the channel name server.
    *
    * @return  <code>true</code> if any information has changed, otherwise
    *           <code>false</code>.
    *
    */
   public boolean refresh()
   {
      NetChannelLocation refreshed = null;
      if (cnsResolver == null)
         //obtain a reference to the installed CNS
         this.cnsResolver = (CNSUser) Node.getInstance().getServiceUserObject(this.cnsServiceName);
      if (accessLevel == null)
         refreshed = cnsResolver.resolve(name);
      else
         refreshed = cnsResolver.resolve(name, accessLevel);
      if (!this.equals(refreshed))
      {
         //CNS has updated the location object
         refreshFrom(refreshed);
         return true;
      }
      else
         //nothing has changed
         return false;
   }
   /*-----------Other public methods--------------------------------------------*/
}