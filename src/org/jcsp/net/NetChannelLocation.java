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

import java.io.Serializable;

/**
 * <p>
 * Instances of this class hold location information for a networked
 * <code>ChannelInput</code> object. Instances of the class hold sufficient
 * information for a networked <code>ChannelOutput</code> object to establish
 * a connection to the networked <code>ChannelInput</code> object.
 * </p>
 * <p>
 * Instances of this class may either be constructed by the
 * <code>jcsp.net</code> user or by the networking infrastructure.
 * </p>
 * @author Quickstone Technologies Limited
 */
public class NetChannelLocation implements Cloneable, Serializable
{
   /*----------------Constructors-----------------------------------------------*/
   /**
    * <p>
    * Constructor which takes the <code>NodeID</code> of the Node hosting
    * the networked <code>ChannelInput</code> object and the label assigned
    * to the channel's Virtual Channel Number (VCN).
    * </p>
    *
    * @param channelNode   the <CODE>NodeID</CODE> of the Node hosting the
    *                       read end of the channel.
    * @param channelLabel  the label assigned to the channel's Virtual
    *                       Channel Number (VCN).
    */
   public NetChannelLocation(NodeID channelNode, String channelLabel) throws IllegalArgumentException
   {
      if (channelNode == null || channelLabel == null)
         throw new IllegalArgumentException ("A null parameter supplied");
      this.channelLabel = channelLabel;
      this.vcn = IndexManager.getInvalidVCN();
      this.channelNode = channelNode;
      this.channelAddress = null;
   }
   
   /**
    * <p>
    * Constructor which takes a <CODE>NodeAddressID</CODE> on which the
    * channel's Node is listening and the label assigned to the channel's
    * Virtual Channel Number (VCN).
    * </p>
    * <p>
    * This constructor is intended to be used by code that wishes to connect
    * to a <CODE>ChannelInput</CODE> running on a Node to which a
    * <CODE>Link</CODE> has not yet been established. An address of the Node
    * must be known by some means. For example, with a JCSP network running
    * over TCP/IP, a program may prompt the user for an IP address to which
    * to connect.
    * </p>
    * @param channelAddress a <CODE>NodeAddressID</CODE> on which the channel's
    *                        Node is listening.
    * @param channelLabel   the label assigned to the channel's VCN.
    */
   public NetChannelLocation(NodeAddressID channelAddress, String channelLabel) throws IllegalArgumentException
   {
      if (channelAddress == null || channelLabel == null)
         throw new IllegalArgumentException("A null parameter supplied");
      this.channelLabel = channelLabel;
      this.vcn = IndexManager.getInvalidVCN();
      this.channelNode = null;
      this.channelAddress = channelAddress;
   }
   
   /**
    * <p>
    * Package private constructor which takes the <code>NodeID</code> of the
    * Node hosting the networked <code>ChannelInput</code> object and a
    * <code>long</code> holding the VCN of the channel.
    * </p>
    * <p>
    * Users of the <code>jcsp.net</code> package cannot use this constructor.
    * It is intended to be used by the infrastructure for creating an instance
    * of the class in order to return it to the user.
    * </p>
    * <p>
    * When used like this, the class may seem to be the same as the
    * <code>ChannelID</code> class, however, this class is publicly
    * visible outside the <code>jcsp.net</code>.
    * </p>
    *
    * @param channelNode    the <CODE>NodeID</CODE> of the Node hosting the
    *                        read end of the channel.
    * @param vcn            the VCN of the channel
    *
    */
   NetChannelLocation(NodeID channelNode, long vcn) throws IllegalArgumentException
   {
      if (channelNode == null || !IndexManager.checkIndexIsValid(vcn))
         throw new IllegalArgumentException("A null NodeID or invalid VCN was supplied");
      this.channelLabel = null;
      this.vcn = vcn;
      this.channelNode = channelNode;
      this.channelAddress = null;
   }
   
   /**
    * <p>
    * This is a protected constructor which takes another
    * <code>NetChannelLocation</code> object and "clones" it into this one.
    * This allows sub-classes to adopt the field values of an instance of
    * this class by passing a reference of it to this constructor.
    * </p>
    *
    * @param   other   Another <code>NetChannelLocation</code> to "clone"
    *                  into this one.
    * @throws IllegalArgumentException iff the parameter is <code>null</code>.
    */
   protected NetChannelLocation(NetChannelLocation other) throws IllegalArgumentException
   {
      refreshFrom(other);
   }
   
   /*----------------Fields-----------------------------------------------------*/
   
   /**
    * The label, if any, assigned to a channel's VCN.
    */
   private String channelLabel;
   
   /**
    * The VCN of a channel, if known.
    */
   private long vcn;
   
   /**
    * The <code>NodeID</code> of the channel's Node.
    * This should be <code>null</code> if <code>channelAddress</code> is
    * not <code>null</code>.
    */
   private NodeID channelNode;
   
   /**
    * The <code>NodeAddressID</code> on which the channel's Node is listening.
    * This should be <code>null</code> if <code>channelNode</code> is
    * not <code>null</code>.
    */
   private NodeAddressID channelAddress;
   
   
   /*----------------Public Methods---------------------------------------------*/
   
   /**
    * <p>
    * Public accessor for the channel label property.
    * </p>
    *
    * @return the channel label held by the instance of this object,
    *          if exists or else <code>null</code>.
    *
    */
   public final String getChannelLabel()
   {
      return channelLabel;
   }
   
   /**
    * <p>
    * Public accessor for the channel's address, if held.
    * </p>
    *
    * @return a <code>NodeAddressID</code> on which the channel's Node is
    *          listening. If the channel's Node's <code>NodeID</code> is
    *          known, then this will return <code>null</code>.
    *
    */
   public final NodeAddressID getChannelAddress()
   {
      return channelAddress;
   }
   
   /**
    * <p>
    * Public accessor for the channel label property.
    * </p>
    *
    * @return the <code>NodeID</code> of the channel's Node, if known,
    *          else <code>null</code>.
    *
    */
   public final NodeID getChannelNodeID()
   {
      return channelNode;
   }
   
   /**
    * <p>
    * This method requests that the instance of this class refresh
    * its information. This class does not actually do anything when
    * this method is called, however, instances of subclasses may take the
    * opportunity to refresh their data.
    * </p>
    * <p>
    * An example, of when this might be useful is if this class were
    * extended as part of a name service that resolves names to
    * <code>NetChannelLocation</code> objects. If a user of an instance
    * of this extended version wanted to check that the data were still
    * up to date, it would call this method which would then check the
    * information with a name service.
    * </p>
    *
    * @return  <code>true</code> if any information has changed, otherwise
    *           <code>false</code>.
    *
    *
    */
   public boolean refresh()
   {
      //does not do anything - this is for subclasses to implement
      return false;
   }
   
    /* It is not necessary to be able to create a labelled VCN for a channel that will migrate. Labelled
     * VCNs should only be used as a bootstrapping mechanism (eg for an alternative CNS service). If there
     * is a possibility of a channel end moving then the CNS should be used.
     */
   
   /**
    * <p>
    * Returns a String ID for this
    * <code>NetChannelLocation</code> object.
    * </p>
    * <p>
    * This method does not need to be used by normal JCSP users.
    * </p>
    *
    * @return the id.
    */
   public final String getStringID()
   {
      if (channelNode == null)
         throw new IllegalStateException("Unable to provide an ID for a " + "NetChannelLocation with null NodeID");
      if (channelLabel != null)
         throw new IllegalStateException("Unable to provide an ID for a NetChannelLocation with an unresolved label");
      return channelNode + " " + Long.toString(vcn);
   }
   
   /*----------------Public Methods from Cloneable Interface--------------------*/
   
   /**
    * <p>
    * Returns a clone of the instance of this class. All mutable fields
    * are also cloned.
    * </p>
    *
    * @return a clone of the instance of this class.
    *
    */
   public Object clone() throws CloneNotSupportedException
   {
      NetChannelLocation clone = (NetChannelLocation)super.clone();
      
      //either the channelNode or channelAddress should be null
      if (channelNode != null)
      {
         clone.channelNode	 = (NodeID) channelNode.clone();
         clone.channelAddress = null;
      }
      else
      {
         clone.channelNode = null;
         clone.channelAddress = (NodeAddressID) channelAddress.clone();
      }
      return clone;
   }
   /*----------------Methods overriden from Object------------------------------*/
   
   /**
    * <p>
    * Returns whether this object is equal to another object.
    * </p>
    *
    * @return <code>true</code> iff the supplied object is equal.
    */
   public final boolean equals(Object o)
   {
      if (o == null || !(o instanceof NetChannelLocation))
         return false;
      NetChannelLocation other = (NetChannelLocation) o;
      
      boolean equal = true;
      if (this.channelAddress != null && other.channelAddress != null)
         equal = channelAddress.equals(other.channelAddress);
      else if (!(this.channelAddress == null && other.channelAddress == null))
         equal = false;
      if (equal)
      {
         if (this.channelLabel != null && other.channelLabel != null)
            equal = channelLabel.equals(other.channelLabel);
         else if (!(this.channelLabel == null && other.channelLabel == null))
            equal = false;
      }
      if (equal)
      {
         if (this.channelNode != null && other.channelNode != null)
            equal = channelNode.equals(other.channelNode);
         else if (!(this.channelNode == null && other.channelNode == null))
            equal = false;
      }
      return equal && this.vcn == other.vcn && checkEqual(other);
   }
   
   /**
    * Return a hashcode for this object.
    *
    * @return the <code>int</code> hash code.
    */
   public final int hashCode()
   {
      return (this.channelNode != null)
               ? this.channelNode.hashCode() + (int) this.vcn
               : this.channelAddress.hashCode() + this.channelLabel.hashCode();
   }
   
   /*----------------Protected Methods------------------------------------------*/
   /**
    * <p>
    * This method refreshes the fields in the instance of this object to
    * equal the fields in the supplied object. Only members of the
    * <code>NetChannelLocation</code> class are copied and not members
    * added by sub-classes.
    * </p>
    *
    * @param   other   Another <code>NetChannelLocation</code> to "clone"
    *                  into this one.
    * @throws IllegalArgumentException iff the parameter is <code>null</code>.
    */
   protected final void refreshFrom(NetChannelLocation other) throws IllegalArgumentException
   {
      this.channelAddress = other.channelAddress;
      this.channelLabel   = other.channelLabel;
      this.channelNode    = other.channelNode;
      this.vcn = other.vcn;
   }
   
   /**
    * <p>
    * This method is used by the <code>equals(Object)</code> method
    * to determine whether another object is equal to the one on which
    * it is invoked.
    * </p>
    * <p>
    * This should be used by sub-classes as they cannot override the
    * equals method.
    * </p>
    *
    * @param other	the <code>NetChannelLocation</code> object to
    *                  compare with this object.
    * @return <code>true</code> if the other object is equal to this one.
    */
   protected boolean checkEqual(NetChannelLocation other)
   {
      return true;
   }
   
   /*----------------Package level Methods--------------------------------------*/
   /**
    * <p>
    * Package level accessor for the channel's VCN if held.
    * Users of <code>jcsp.net</code> should not have any control over VCN
    * allocation so this accessor is not publicly accessible.
    * </p>
    *
    * @return the VCN value held for the channel.
    *
    */
   long getVCN()
   {
      return vcn;
   }
   /**
    * <p>
    * Package level mutator for the channel's actual location details.
    * The details can only be set if the current NodeID held is null.
    * </p>
    *
    * @param nodeID the <code>NodeID</code> of the channel.
    * @param vcn     the VCN of the channel.
    */
   void setLocationDetails(NodeID nodeID, long vcn)
   {
      if (this.channelNode == null)
      {
         this.channelNode = nodeID;
         this.vcn = vcn;
         this.channelLabel = null;
         this.channelAddress = null;
      }
   }
}