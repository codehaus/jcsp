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

/**
 * <p>
 * An interface that should be implemented by classes that
 * are intended to be Node level JCSP.NET services.
 * </p>
 * <p>
 * Services should be initialized, then started and then stopped.
 * </p>
 *
 * @author Quickstone Technologies Limited
 */
public interface Service
{
   /**
    * This should start the service when called and return.
    * @return <CODE>true</CODE> iff the service has successfully started.
    */
   public boolean start();
   
   /** Should stop the service and then return.
    * @return <CODE>true</CODE> iff the service has successfully stopped.
    */
   public boolean stop();
   
   /** Initialize the service with the specified service settings.
    * @param settings The settings used by the service.
    * @return <CODE>true</CODE> iff the service has been initialized.
    */
   public boolean init(ServiceSettings settings);
   
   /**
    * Indicates whether or not a service is running.
    * @return <CODE>true</CODE> iff the service is currently running.
    */
   public boolean isRunning();
   
   /**
    * Obtains a <code>ServiceUserObject</code> from a Service.
    * This allows Services to expose functionality to users that
    * it does not want to be able to access admin features.
    *
    * @return a <code>ServiceUserObject</code>.
    * @throws SecurityException if the calling Thread does not have
    *                            access to the object.
    */
   public ServiceUserObject getUserObject() throws SecurityException;
}