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

import java.util.*;

//based upon AbstractFactory

/**
 * <p>
 * This class does not need to be used by normal JCSP users.
 * </p>
 * <p>
 * The <code>Builder</code> inner class needs to be sub-classed
 * by JCSP.NET protocol implementations.
 * </p>
 *
 * @author Quickstone Technologies Limited
 */
public class LinkFactory
{
    /*-------------------Singleton Class Instance---------------------------------*/
    
    private static LinkFactory instance = new LinkFactory();
    
    /*-------------------Private Constructor--------------------------------------*/
    
    private LinkFactory()
    {       
    }
    
    /*----------------------Methods-----------------------------------------------*/
    
    static LinkFactory getInstance()
    {
        return instance;
    }
    
    synchronized Link makeLink(NodeID target, Profile linkProfile)
    {   
        NodeAddressID[] targetAddressIDs = (NodeAddressID[])target.getAddresses();
        
        Node.info.log(this, "MakeLink " + target + " " + linkProfile);
        ProtocolComparator protocolComparator = new ProtocolComparator();
        Arrays.sort(targetAddressIDs, protocolComparator);
        
        //this should contain no duplicates
        ProtocolID[] txProtocols = getTxProtocols();
        Arrays.sort(txProtocols, protocolComparator);
        
        ArrayList matchingProtocolAddresses = new ArrayList();
        
        NodeAddressID bestExactMatch = null;
        
        int txIndex = 0;
        int targetRxIndex = 0;
        Link linkToReturn = null;
        
        while (txIndex < txProtocols.length && targetRxIndex < targetAddressIDs.length)
        {
            int comparison = protocolComparator.compare(txProtocols[txIndex], targetAddressIDs[targetRxIndex]);
            if (comparison == 0)
            {
                //found some matching tx and rx protocols
                //check that protocol meets requirements
                ProtocolID pID = targetAddressIDs[targetRxIndex].getProtocolID();
                int specMatch = 0;
                if (linkProfile != null)
                    specMatch = linkProfile.matches(ProtocolManager.getInstance().getProtocolSpecifications(pID));
                if (linkProfile == null || specMatch == 1)
                {
                    if (bestExactMatch == null)
                    {
                        bestExactMatch = targetAddressIDs[targetRxIndex];
                        bestExactMatch.getProtocolID().getPosition();
                    }
                    else if (bestExactMatch.getProtocolID().getPosition() > 
                             targetAddressIDs[targetRxIndex].getProtocolID().getPosition())
                        bestExactMatch = targetAddressIDs[targetRxIndex];
                }
                else if (bestExactMatch == null)
                    matchingProtocolAddresses.add(targetAddressIDs[targetRxIndex]);
                targetRxIndex++;
            }
            else if (comparison < 0)
                //txProtocol is less
                txIndex++;
            else if (comparison > 0)
                //rx protocol is less
                targetRxIndex++;
        }
        if (bestExactMatch != null)
        {
            ProtocolID pID = bestExactMatch.getProtocolID();
            Builder builder = (Builder) builders.get(pID);
            if (builder != null)
                linkToReturn = builder.testAndBuild(bestExactMatch);
        }
        else if ((linkProfile == null || !linkProfile.requiresExactMatch()) && matchingProtocolAddresses.size() > 0)
        {
            Collections.sort(matchingProtocolAddresses, new ProtocolPerformanceComparator());
            
            //iterate through the protocols until a Link is built
            ListIterator li = matchingProtocolAddresses.listIterator();
            while (li.hasNext() && linkToReturn == null)
            {
                NodeAddressID nadID = (NodeAddressID) li.next();
                ProtocolID pID = nadID.getProtocolID();
                Builder builder = (Builder) builders.get(pID);
                if (builder != null)
                    linkToReturn = builder.testAndBuild(nadID);
            }
        }
        if (linkToReturn != null)
            linkToReturn.setProfile(linkProfile);
        return linkToReturn;
    }
    
    synchronized Link makeLink(NodeAddressID targetAddress)
    {
        if (targetAddress == null)
            return null;
        Builder builder = (Builder) builders.get(targetAddress.getProtocolID());
        if (builder != null)
            return builder.testAndBuild(targetAddress);
        return null;
    }
    
    synchronized boolean installBuilder(Builder builder)
    {
        if (builder == null)
            throw new NullPointerException();
        ProtocolID protocolID = builder.getProtocolID();
        if (builders.containsKey(protocolID))
            return false;
        builders.put(protocolID, builder);
        return true;
    }
    
    synchronized boolean removeBuilder(ProtocolID protocolID, Builder builder)
    {
        if (protocolID == null)
            return false;
        //perform a security check here?
        if (builders.get(protocolID) == builder)
        {
            builders.remove(protocolID);
            return true;
        }
        return false;
    }
    
    /** Used to find out the communication protocols currently installed in the <CODE>LinkFactory</CODE> instance.
     * Returns an array of <CODE>ProtocolID</CODE> objects which identify the protocols.
     * @return an array of <CODE>ProtocolID</CODE> objects which identify the currently installed protocols.
     */
    synchronized ProtocolID[] getTxProtocols()
    {
        Set tmp = builders.keySet();
        return (ProtocolID[]) tmp.toArray(new ProtocolID[tmp.size()]).clone();
    }
    
    /** An abstract inner class which should be implemented by
     * comunication protocol implementations.
     *
     * The <CODE>Builder</CODE> class provides an abstract method which
     * should be implemented to take a <CODE>NodeAddressID</CODE> object
     * which should be used to construct a <CODE>Link</CODE> which is then
     * returned by the method to the factory.
     */
    public static abstract class Builder
    {
        /** A constructor which takes the <CODE>ProtocolID</CODE> identifying the
         * protocol that this <CODE>Builder</CODE> supports. This must be called
         * by sub-classes.
         * @param protocolID the <CODE>ProtocolID</CODE> for the protocol that the implementation supports.
         */
        public Builder(ProtocolID protocolID)
        {
            this.protocolID = protocolID;
        }
        
        /** Takes a <CODE>NodeAddressID</CODE> and constructs and returns
         * a <CODE>Link</CODE> object.
         * @param addressID the <CODE>AddressID</CODE> for the remote Node.
         * @throws IllegalArgumentException if the <CODE>AddressID</CODE> object is invalid.
         * @return the constructed <CODE>Link</CODE>.
         */
        public abstract Link testAndBuild(NodeAddressID addressID)
        throws IllegalArgumentException;
        
        /** Gets the <CODE>ProtocolID</CODE> that this <CODE>Builder</CODE> supports.
         * @return the <CODE>ProtocolID</CODE> that this <CODE>Builder</CODE> supports.
         */
        public final ProtocolID getProtocolID()
        {
            return protocolID;
        }
        
        private final ProtocolID protocolID;
        
    }
    
    /*----------------------Attributes--------------------------------------------*/
    /**
     * This Hashtable stores ProtocolID's as it keys and map these to
     * Builders for those Protocols.
     */
    private Hashtable builders = new Hashtable();
    
    /*----------------------Inner Classes-----------------------------------------*/
    
    private static class ProtocolComparator implements Comparator
    {
        
        /**
         * @param o1
         * @param o2
         * @return  */
        public int compare(Object o1, Object o2)
        {
            if (o1 instanceof NodeAddressID)
            {
                if (o2 instanceof NodeAddressID)
                    return compare((NodeAddressID) o1, (NodeAddressID) o2);
                else if (o2 instanceof ProtocolID)
                    return compare((NodeAddressID) o1, (ProtocolID) o2);
            }
            else if (o1 instanceof ProtocolID)
            {
                if (o2 instanceof NodeAddressID)
                    return compare((ProtocolID) o1, (NodeAddressID) o2);
                else if (o2 instanceof ProtocolID)
                    return compare((ProtocolID) o1, (ProtocolID) o2);
            }
            throw new ClassCastException();
        }
        
        /**
         * @param naID
         * @param pID
         * @return  */
        public int compare(NodeAddressID naID, ProtocolID pID)
        {
            ProtocolID pID1 = naID.getProtocolID();
            return compare(pID1, pID);
        }
        
        /**
         * @param pID
         * @param naID
         * @return  */
        public int compare(ProtocolID pID, NodeAddressID naID)
        {
            ProtocolID pID2 = naID.getProtocolID();
            return compare(pID, pID2);
        }
        
        /**
         * @param naID1
         * @param naID2
         * @return  */
        public int compare(NodeAddressID naID1, NodeAddressID naID2)
        {
            ProtocolID pID1 = naID1.getProtocolID();
            ProtocolID pID2 = naID2.getProtocolID();
            return compare(pID1, pID2);
        }
        
        /**
         * @param pID1
         * @param pID2
         * @return  */
        public int compare(ProtocolID pID1, ProtocolID pID2)
        {
            return pID1.getClass().getName().compareTo(pID2.getClass().getName());
        }
    }
    
    private static class ProtocolPerformanceComparator implements Comparator
    {
        /**
         * @param o1
         * @param o2
         * @return  */
        public int compare(Object o1, Object o2)
        {
            if (o1 instanceof ProtocolID)
            {
                if (o2 instanceof ProtocolID)
                    return compare((ProtocolID) o1, (ProtocolID) o2);
            }
            throw new ClassCastException();
        }
        
        /**
         * @param pID1
         * @param pID2
         * @return  */
        public int compare(ProtocolID pID1, ProtocolID pID2)
        {
            if (pID1.getPosition() == pID2.getPosition())
                return 0;
            else if (pID1.getPosition() < pID2.getPosition())
                return -1;
            else
                return 1;
        }
    }
}