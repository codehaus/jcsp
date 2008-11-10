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
import java.lang.reflect.*;
import java.util.*;

/**
 * <p>
 * A Class whose instances represent a unique identifier for a JCSP.NET Node.
 * </p>
 * <p>
 * For an explanation of this class, see <code>{@link AbstractID}</code>.
 * </p>
 * @author Quickstone Technologies Limited
 */
public final class NodeID extends AbstractID implements Serializable, Cloneable, Comparable
{
   /*----------------------Constructors------------------------------------------*/
   
   NodeID(NodeUI nodeUI)
   {
      if (nodeUI == null)
         throw new NullPointerException("nodeUI is null");
      this.nodeUI = nodeUI;
      domainID = DomainID.getNullDomainID();
   }
   
   NodeID(NodeUI nodeUI, NodeAddressID addressID, DomainID domainID)
   {
      this(nodeUI, new NodeAddressID[] { addressID }, domainID);
   }
   
   NodeID(NodeUI nodeUI, NodeAddressID[] addressIDs, DomainID domainID)
   {
      //copy the supplied array so that contents cannot be modified externally
      if (nodeUI == null)
         throw new NullPointerException("nodeUI is null");
      if (domainID == null)
         throw new NullPointerException("domainID is null");
      this.nodeUI = nodeUI;
      this.domainID = domainID;
      this.addressIDs = new NodeAddressID[addressIDs.length];
      for (int i = 0; i < addressIDs.length; i++)
      {
         try
         {
            this.addressIDs[i] = (NodeAddressID) addressIDs[i].clone();
         }
         catch (CloneNotSupportedException e)
         {
            //will make do without cloning
            this.addressIDs[i] = addressIDs[i];
         }
         catch (NullPointerException e)
         {
            throw new NullPointerException("An addressID is null");
         }
      }
   }
   
   private NodeID(String stringRep) throws IllegalArgumentException
   {
      if (stringRep == null)
         throw new IllegalArgumentException("No String supplied.");
      String[] strings = Utils.stringToArray(stringRep);
      if (strings.length < 2)
         throw new IllegalArgumentException("No NodeAddressID supplied.");
      domainID = new DomainID(strings[0]);
      nodeUI = NodeUI.createFromStringForm(strings[1]);
      boolean addressCreated = false;
      this.addressIDs = new NodeAddressID[strings.length - 2];
      for (int i = 2; i < strings.length; i++)
      {
         int pos = strings[i].indexOf(":");
         if (pos > -1)
         {
            String className = strings[i].substring(0, pos);
            String addStringRep = strings[i].substring(pos + 1);
            try
            {
               Class addressClass = Class.forName(className);
               Method createMethod = addressClass.getMethod("getAddressIDFromString", new Class[] { String.class });
               NodeAddressID naddID = (NodeAddressID)createMethod.invoke(null, new Object[] { addStringRep });
               this.addressIDs[i - 2] = naddID;
            }
            catch (Exception e)
            {
               e.printStackTrace();
               if (!addressCreated && i == strings.length - 1)
                  throw new IllegalArgumentException("No NodeAddressID created.");
            }
         }
      }
   }
   
   /*----------------------Public Methods----------------------------------------*/
   
   /**
    * <p>
    * Returns a clone of the instance of <CODE>NodeID</CODE> on which
    * this method is being called.
    * </p>
    * @throws CloneNotSupportedException if the <CODE>NodeID</CODE> cannot be cloned.
    * @return a clone of the current instance of <CODE>NodeID</CODE>.
    */
   public Object clone() throws CloneNotSupportedException
   {
      NodeID clone = (NodeID)super.clone();
      clone.unrecognisedAddressIDs = (HashSet) unrecognisedAddressIDs.clone();
      return clone;
   }
   
   /**
    * <p>
    * Compares this <CODE>NodeID</CODE> with another object. This will only return
    * true if the other object is a <CODE>NodeID</CODE> representing the same Node as
    * this <CODE>NodeID</CODE>. The comparison is performed by using a Node's unique
    * identifier. This was introduced for efficiency reasons as comparing two
    * <CODE>NodeID</CODE> objects' sets of addresses could be quite slow.
    * </p>
    * @param o another object to compare with this <CODE>NodeID</CODE>.
    * @return <CODE>true</CODE> iff the other object is a <CODE>NodeID</CODE> representing the same
    * Node as this <CODE>NodeID</CODE>.
    */
   public boolean equals(Object o)
   {
      if (o == null || !(o instanceof NodeID))
         return false;
      NodeID other = (NodeID) o;
      if (!(domainID.equals(other.getDomainID())))
         return false;
      return nodeUI.equals(other.nodeUI);
   }
   
   /**
    * <p>
    * Returns a hash code for this <CODE>NodeID</CODE> object.
    * </p>
    * @return an <CODE>int</CODE> hash code.
    */
   public int hashCode()
   {
      return nodeUI.hashCode();
   }
   
   /**
    * <p>
    * Compares this <CODE>NodeID</CODE> object with another
    * <CODE>NodeID</CODE> object. Returns 0 if this <CODE>NodeID</CODE> is equal
    * to the other <CODE>NodeID</CODE>, a negative <CODE>int</CODE> if this
    * <CODE>NodeID</CODE> is less than the supplied <CODE>NodeID</CODE> or a positive
    * <CODE>int</CODE> if this <CODE>NodeID</CODE> is greater than the supplied
    * <CODE>NodeID</CODE>.
    * </p>
    * <p>
    * This comparison is based upon the implementation of <CODE>NodeUI</CODE> used.
    * </p>
    * @param o An object to compare with this <CODE>NodeID</CODE>.
    * @throws ClassCastException if the parameter supplied is not a <CODE>NodeID</CODE>.
    * @return 0 if this <CODE>NodeID</CODE> is equal
    * to the other <CODE>NodeID</CODE>, a negative <CODE>int</CODE> if this
    * <CODE>NodeID</CODE> is less than the supplied <CODE>NodeID</CODE> or a positive
    * <CODE>int</CODE> if this <CODE>NodeID</CODE> is greater than the supplied
    * <CODE>NodeID</CODE>.
    */
   public int compareTo(Object o) throws ClassCastException
   {
      NodeID other = (NodeID) o;
      if (equals(o))
         return 0;
      int domCompare = domainID.compareTo(other.domainID);
      if (domCompare != 0)
         return domCompare;
      return nodeUI.compareTo(other.nodeUI);
   }
   
   /**
    * <p>
    * This method is equivalent to calling the <CODE>compareTo(Object)</CODE>
    * and supplying the local Node's <CODE>NodeID</CODE> as a parameter.
    * </p>
    *
    * @return an <CODE>int</CODE> following the rules of the
    * 			<CODE>compareTo(Object)</CODE> method.
    */
   public int compareToLocalNode()
   {
      return compareTo(Node.getInstance().getActualNode());
   }
   
   boolean onSameBranch(AbstractID abstractID)
   {
      if (abstractID == null)
         return false;
      if (abstractID instanceof NodeID)
         return this.equals(abstractID);
      if (abstractID instanceof ApplicationID)
         return abstractID.onSameBranch(this);
      return domainID.onSameBranch(abstractID);
   }
   
   /**
    * <p>
    * Returns a clone of the set of <CODE>NodeAddressID</CODE> objects that
    * this <CODE>NodeID</CODE> holds.
    * </p>
    * <p>
    * If any held <code>NodeAddressID</code> objects do not support
    * cloning, then the actual object is returned but this should
    * never be the case.
    * </p>
    * @return an array of <CODE>NodeAddressID</CODE> objects.
    */
   public synchronized NodeAddressID[] getAddresses()
   {
      NodeAddressID[] addressIDs = new NodeAddressID[this.addressIDs.length];
      for (int i = 0; i < addressIDs.length; i++)
      {
         try
         {
            addressIDs[i] = (NodeAddressID) this.addressIDs[i].clone();
         }
         catch (CloneNotSupportedException e)
         {
            //will make do without cloning
            addressIDs[i] = this.addressIDs[i];
         }
      }
      return addressIDs;
   }
   
   synchronized void addAddress(NodeAddressID addressID)
   {
      final NodeAddressID[] newArray = new NodeAddressID[this.addressIDs.length + 1];
      System.arraycopy(this.addressIDs, 0, newArray, 0, this.addressIDs.length);
      newArray[newArray.length - 1] = addressID;
      this.addressIDs = newArray;
   }
   
   synchronized void removeAddress(NodeAddressID addressID)
   {
      int position = -1;
      for (int i = 0; i < this.addressIDs.length; i++)
      {
         if (this.addressIDs[i].equals(addressID))
            position = i;
      }
      if (position < 0)
         // The address was not in our list. Maybe raise an exception as this indicates a logical error elsewhere.
         return;
      final NodeAddressID[] newArray = new NodeAddressID[this.addressIDs.length - 1];
      System.arraycopy(this.addressIDs, 0, newArray, 0, position);
      int remainingLength = this.addressIDs.length - position - 1;
      if (remainingLength > 0)
         System.arraycopy(this.addressIDs, position + 1, newArray, position, remainingLength);
      this.addressIDs = newArray;
   }
   
   /**
    *
    * @deprecated Not needed now channel names have been abstracted
    */
   static NodeID createFromStringForm(String stringForm) throws IllegalArgumentException
   {
      return new NodeID(stringForm);
   }
   
   /**
    * This gets a String form of this NodeID. This is only intended to be
    * called for NodeID's representing the current JVM as any
    * NodeAddressIDs in serialized form in the unrecognisedAddressIDs
    * HashSet will not be saved. This HashSet should be empty for NodeID's
    * representing the current JVM.
    *
    * @deprecated Not needed now channel names have been abstracted
    *
    * @return	the String form that can be used to reconstruct this NodeID.
    */
   synchronized String getStringForm()
   {
      String[] strings = new String[addressIDs.length + 2];
      strings[0] = domainID.getStringForm();
      strings[1] = nodeUI.getStringForm();
      
      for (int i = 0; i < addressIDs.length; i++)
      {
         NodeAddressID naddID = addressIDs[i];
         strings[2 + i] = naddID.getClass().getName() + ":" + naddID.getStringForm();
      }
      return Utils.arrayToString(strings);
   }
   
   synchronized void setDomainID(DomainID domainID)
   {
      this.domainID = domainID;
   }
   
   public AbstractID getParentID()
   {
      return domainID;
   }
   
   /** Get the <CODE>DomainID</CODE> of the domain to which the Node represented by
    * this <CODE>NodeID</CODE> object belongs.
    * If the Node is not a member of a domain then a <CODE>DomainID</CODE> object
    * will be returned that will return <CODE>true</CODE> when its
    * <CODE>isNullDomain()</CODE> method is called.
    * @return this <CODE>NodeID</CODE> object's <CODE>DomainID</CODE> object.
    */
   public DomainID getDomainID()
   {
      return domainID;
   }
   
   /** Returns a name that has been assigned to the Node
    * represented by this <CODE>NodeID</CODE> object.
    * If no name has been assigned, then an empty <CODE>String</CODE>
    * is returned.
    *
    * The Node naming feature is not currently implemented.
    * @return the name assigned to this <CODE>NodeID</CODE> object's Node.
    */
   public String getName()
   {
      return name;
   }
   
   /** Returns a human readable <CODE>String</CODE> that
    * represents this <CODE>NodeID</CODE> object.
    * This will either include the name of the Node, if assigned, or else
    * a list of the Node's addresses.
    *
    * The <CODE>String</CODE> returned by this method is only really intended as a
    * way of supply debugging information to users. The contents is not guaranteed
    * and should not relied upon by a program.
    * @return a human readable <CODE>String</CODE>.
    */
   public String toString()
   {
      if (!name.equals(""))
         return getDomainID() + "\\Node: " + name;
      StringBuffer stringToReturn = new StringBuffer(getDomainID() + "\\Node(");
      for (int i = 0; i < addressIDs.length; i++)
         stringToReturn.append(addressIDs[i] + ")");
      return stringToReturn.toString();
   }
   
   void setName(String name)
   {
      if (name != null)
         this.name = name;
      else
         name = "";
   }
   
   private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
   {
      try
      {
         domainID = (DomainID) in.readObject();
         nodeUI = (NodeUI) in.readObject();
         name = (String) in.readObject();
         int numAddresses = in.readInt();
         addressIDs = new NodeAddressID[numAddresses];
         int unrecCount = 0;
         for (int i = 0; i < numAddresses; i++)
         {
            SerializedObject s = (SerializedObject) in.readObject();
            try
            {
               NodeAddressID naddID = (NodeAddressID) s.get();
               addressIDs[i - unrecCount] = naddID;
            }
            catch (ClassNotFoundException e)
            {
               unrecCount++;
               unrecognisedAddressIDs.add(s);
               //put s into HashSet of addresses which this JVM
               //does not recognise - means they won't be lost
            }
            catch (Exception e)
            {
               e.printStackTrace();
            }
         }
         unrecognisedAddressIDs = new HashSet();
         if (unrecCount > 0)
         {
            NodeAddressID[] addressIDs = new NodeAddressID[numAddresses - unrecCount];
            System.arraycopy(this.addressIDs, 0, addressIDs, 0, addressIDs.length);
            this.addressIDs = addressIDs;
         }
         
      }
      catch (Exception e)
      {
         e.printStackTrace();
         throw new IOException("An error occured during deserialization");
      }
   }
   
   private void writeObject(ObjectOutputStream out) throws IOException
   {
      synchronized (this)
      {
         out.writeObject(domainID);
         out.writeObject(nodeUI);
         out.writeObject(name);
         out.writeInt(addressIDs.length + unrecognisedAddressIDs.size());
         for (int i = 0; i < addressIDs.length; i++)
         {
            try
            {
               out.writeObject(new SerializedObject(addressIDs[i], true));
            }
            catch (Exception e)
            {
               //ignored - could print out a message
               e.printStackTrace();
            }
         }
         for (Iterator it = unrecognisedAddressIDs.iterator(); it.hasNext(); )
         {
            try
            {
               out.writeObject(it.next());
            }
            catch (Exception ex)
            {
               //ignored - could print out a message
               ex.printStackTrace();
            }
         }
      }
   }
   
   /*----------------------Attributes--------------------------------------------*/
   
   /**
    * This is a set of addresses of LinkServers that the Node, represented by
    * this NodeID, is running.
    */
   private NodeAddressID[] addressIDs = new NodeAddressID[0];
   
   /**
    * This is the set of addresses of LinkServers that the Node, represented by
    * this NodeID, is running but this JVM does not recognise. The serialized
    * forms of the addresses are stored here so that
    */
   private HashSet unrecognisedAddressIDs = new HashSet();

   /**
    * The Domain of which the Node represented by this NodeID is a member.
    */
   private DomainID domainID;
   
   /**
    * A unique identifier for this node.
    */
   private NodeUI nodeUI;
   
   /**
    * A name assigned to this Node.
    */
   private String name = "";
}