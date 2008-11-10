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

package org.jcsp.net.tcpip;

import java.net.*;
import org.jcsp.net.*;

/**
 * <p>Provides a concrete implementation of the abstract <code>NodeAddressID</code> for use with the
 * TCP/IP link protocol. A TCP/IP node address consists of an internet host address (IP address)
 * and 16bit IP port number.</p>
 *
 * @author Quickstone Technologies Limited
 */
public class TCPIPAddressID extends NodeAddressID
{
   
   /*----------------------Constructors------------------------------------------*/
   /**
    * Creates a TCPIPAddressID for a computer, given it's IP address
    * and port number.
    *
    * @param host The computer's IP address.
    * @param port The computer's port number, in the range 0-65535 inclusive.
    * @param isUnique True if the address ID is globally unique, false otherwise.
    *
    * @throws IllegalArgumentException If the port is invalid.
    */
   public TCPIPAddressID(InetAddress host, int port, boolean isUnique) throws IllegalArgumentException
   {
      if ((port > 0xFFFF) || (port < 0))
         throw new IllegalArgumentException("new ComputerID(): Port must be < 65536 and >= 0");
      
      if (host == null)
         throw new NullPointerException("new ComputerID: null host InetAddress!");
      
      this.host = host;
      this.port = port;
      
      //Store a String represenation of host and port
      //the port stored as hex
      StringBuffer buf = new StringBuffer(19);
      buf.append(Integer.toHexString(0xFFFF & port));
      while (buf.length() < 4)
         buf.insert(0, '0');
      
      StringBuffer ipBuf = new StringBuffer(17);
      ipBuf.insert(0, host.getHostAddress());
      while (ipBuf.length() < 15)
         ipBuf.append(' ');
      buf.insert(0, ipBuf);
      
      if (isUnique)
         buf.append("T");
      else
         buf.append("F");
      stringForm = buf.toString();
   }
   
   /**
    * Creates a TCPIPAddressID for a computer, given it's host name
    * and port number.
    *
    * @param host The computer's IP address, as a string
    * @param port The computer's port number.
    *
    * @throws IllegalArgumentException If the port is invalid.
    * @throws UnknownHostException If the host name cannot be resolved.
    */
   public TCPIPAddressID(String host, int port, boolean isUnique) throws IllegalArgumentException, UnknownHostException
   {
      this(InetAddress.getByName(host), port, isUnique);
   }
   
   /*----------------------Public Methods----------------------------------------*/
   
   /**
    * Constructs and returns a TCPIPv4 NodeAddressID from a String.
    * The String MUST be in the form of that returned from the
    * getStringForm method.
    *
    * @param stringForm	The String form representing a NodeAddressID.
    * @throws IllegalArgumentException if the string is incorrectly formatted.
    */
   public static NodeAddressID getAddressIDFromString(String stringForm) throws  IllegalArgumentException
   {
      if(stringForm == null || stringForm.length() < 20)
         throw new IllegalArgumentException("Illegal String");
      String ipString = stringForm.substring(0,15).trim();
      String portString = stringForm.substring(15,19);
      int port;
      try
      {
         port = Integer.parseInt(portString,16);
      }
      catch (NumberFormatException ex)
      {
         // oops
         throw new IllegalArgumentException("Unable to create TCPIPv4 AddressID - Bad Port");
      }
      
      String sUnique = stringForm.substring(19);
      boolean isUnique = sUnique.equals("T");
      //this method used to thrown the UnknownHostException, however
      //this does not compile under jdk 1.4 due to superclass' static
      //method not throwing this
      try
      {
         return new TCPIPAddressID(ipString, port, isUnique);
      }
      catch (UnknownHostException e)
      {
         throw new RuntimeException(e.getMessage());
      }
   }
   
   /**
    * Returns a string representation of the address suitable for use in the <code>createAddressID</code>
    * method of <code>TCPIPProtocolID</code>.
    */
   protected String getStringForm()
   {
      return stringForm;
   }
   
   /**
    * Returns the ProtocolID for this address
    */
   public ProtocolID getProtocolID()
   {
      return protocolID;
   }
   
   /**
    * Returns the computer's IP address
    *
    * @return IP address of server.
    */
   public final InetAddress getHost()
   {
      return host;
   }
   
   /**
    * Returns the computer's port number for incoming link requests.
    *
    * @return Port number of server.
    */
   public final int getPort()
   {
      return port;
   }
   
   /**
    * Compares two TCPIPAddressID for equality.
    *
    * @return true iff obj is a non-null TCPIPAddressID for the same port &
    *         host, false otherwise.
    */
   public final boolean equals(Object obj)
   {
      if ((obj == null) || !(obj instanceof TCPIPAddressID))
         return false;
      TCPIPAddressID other = (TCPIPAddressID)obj;
      return (other.port == port) && (host.equals(other.host));
   }
   
   /**
    * Returns a hashCode for this TCPIPAddressID
    *
    */
   public final int hashCode()
   {
      return host.hashCode() + (port << 16);
   }
   
   public boolean isGloballyUnique()
   {
      return isUnique;
   }
   
   /**
    * Returns a string representation of this TCPIPAddressID, in the form
    * "123.45.67.234:5678".
    *
    * @return A string representation of this ComputerID.
    */
   public final String toString()
   {
      return host.getHostAddress() + ":" + port;
   }
   
   /*----------------------Attributes--------------------------------------------*/
   
   private InetAddress host;
   private final int port;
   private ProtocolID protocolID = new TCPIPProtocolID();
   private String stringForm;
   private boolean isUnique = false;
}