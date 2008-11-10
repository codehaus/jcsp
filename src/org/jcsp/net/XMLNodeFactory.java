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

import java.net.*;
import java.io.*;
import java.util.*;
import java.lang.reflect.*;
import org.jcsp.net.settings.*;

/**
 * <p>
 * Factory for node instantiation based on an XML-like file.
 * </p>
 * <p>
 * The factory uses its own XML parser that supports a sub-set of
 * XML. The current implementation will parse a file compatible
 * with jcsp-config.dtd. The parser does not current test conformance
 * to this DTD. It does not currently support XML comments.
 * </p>
 * <p>
 * An example of using this class can be seen in
 * <code>{@link org.jcsp.net.cns.CNS}</code>.
 * </p>
 *
 * @author Quickstone Technologies Limited
 */
public class XMLNodeFactory implements NodeFactory
{
   private final JCSPConfig config;
   
   /**
    * Constructs an <code>XMLNodeFactory</code> that uses the
    * specified file.
    *
    * @param filename  the file name of the config file to use.
    */
   public XMLNodeFactory(String filename) throws IOException
   {
      this(new File(filename));
   }
   
   /**
    * Constructs an <code>XMLNodeFactory</code> that uses the
    * specified file.
    *
    * @param xmlFile  a <code>File</code> object pointing to the
    *                  XML file to use.
    *
    */
   public XMLNodeFactory(File xmlFile) throws IOException
   {
      config = new ConfigReader(new FileInputStream(xmlFile)).getConfig();
   }
   
   /**
    * Constructs an <code>XMLNodeFactory</code> that uses the
    * specified file.
    *
    * @param xmlFile  a <code>URL</code> object pointing to the
    *                  XML file to use.
    */
   public XMLNodeFactory(URL xmlFile) throws IOException
   {
      config = new ConfigReader(xmlFile.openStream()).getConfig();
   }
   
   /**
    * This is called by the <code>Node</code> class.
    *
    * @param node     the Node object calling the method.
    * @param attribs  the attributes that need to be set by the method.
    * @throws NodeInitFailedException if initialization failed.
    */
   public NodeKey initNode(Node node, Node.Attributes attribs) throws NodeInitFailedException
   {
      //setup the plugins - only UIFactory at the moment
      Plugins plugins = config.getPlugins();
      
      Plugin uiFactPlugin = plugins.getPlugin("UIFactory");
      if (uiFactPlugin != null)
      {
         try
         {
            Class uiFactoryClass = uiFactPlugin.getPluginClass();
            attribs.setUIFactory((UIFactory) uiFactoryClass.newInstance());
         }
         catch (Exception e)
         {
            Node.err.log(this, "Error trying to load UIFactory: " + uiFactPlugin.getName());
            throw attribs.exception("Error trying to load UIFactory: " + uiFactPlugin.getName());
         }
      }
      else
         attribs.setUIFactory(new UIFactory());
      
      //Setup protocols
      Hashtable protocolIDMap = new Hashtable();
      Protocol[] protocols = config.getProtocols().getProtocols();
      for (int i = 0; i < protocols.length; i++)
      {
         if (protocols[i] != null)
         {
            Protocol p = protocols[i];
            try
            {
               //Get the protocol id
               Class idClass = p.getIDClass();
               ProtocolID pID = (ProtocolID) idClass.newInstance();
               
               //set the order of preference of this protocol
               pID.setPosition(p.getPosition());
               
               //get any settings that are needed
               Setting[] settingsFromConfig = p.getSettings();
               Hashtable settings = new Hashtable();
               for (int j = 0; j < settingsFromConfig.length; j++)
                  settings.put(settingsFromConfig[j].getName(), settingsFromConfig[j].getValue());
               
               //Setup specs
               Spec[] specs = p.getSpecs();
               Specification[] specifications = new Specification[specs.length + 2];
               //Have an extra specification for protocolID
               specifications[specifications.length - 1] = 
                       new Specification(XMLConfigConstants.SPEC_NAME_PROTOCOL,idClass.toString());
               specifications[specifications.length - 2] = new Specification(XMLConfigConstants.SPEC_NAME_PING, 0);
               
               for (int j = 0; j < specs.length; j++)
               {
                  int intVal = -1;
                  double dblValue = -1;
                  boolean booValue = false;
                  String strValue = "";
                  Class type = null;
                  if (specs[j] instanceof OtherSpec)
                  {
                     OtherSpec otherSpec = (OtherSpec) specs[j];
                     type = otherSpec.getType();
                     if (type.equals(String.class))
                        strValue = otherSpec.getStringValue();
                     else if (type.equals(Integer.TYPE))
                        intVal = otherSpec.getIntValue();
                     else if (type.equals(Double.TYPE))
                        dblValue = otherSpec.getDoubleValue();
                     else if (type.equals(Boolean.TYPE))
                        booValue = otherSpec.getBooleanValue();
                  }
                  else
                  {
                     Class specClass = specs[j].getClass();
                     try
                     {
                        Method valueMethod = specClass.getMethod("getValue", null);
                        type = valueMethod.getReturnType();
                        Object returned = valueMethod.invoke(specs[j], new Object[0]);
                        if (type.equals(String.class))
                           strValue = (String) returned;
                        else if (type.equals(Integer.TYPE))
                           intVal = ((Integer) returned).intValue();
                        else if (type.equals(Double.TYPE))
                           dblValue = ((Double) returned).doubleValue();
                        else if (type.equals(Boolean.TYPE))
                           booValue = ((Boolean) returned).booleanValue();
                        else
                           throw attribs.exception("Specification's value is of an unsupported type: " + type);
                     }
                     catch (NoSuchMethodException e)
                     {
                        Node.err.log(this, "Spec does not have a getValue() method.");
                        throw attribs.exception("Cannot load protcol specification: " + specs[j].getName());
                     }
                     catch (IllegalAccessException e)
                     {
                        throw attribs.exception("Unable to access specification's value: " + specs[j].getName());
                     }
                     catch (InvocationTargetException e)
                     {
                        throw attribs.exception("An exception was thrown while trying to access specification's value: " + 
                                                 e.getTargetException());
                     }
                  }
                  if (type.equals(String.class))
                     specifications[j] = new Specification(specs[j].getName(), strValue);
                  else if (type.equals(Integer.TYPE))
                     specifications[j] = new Specification(specs[j].getName(), intVal);
                  else if (type.equals(Double.TYPE))
                     specifications[j] = new Specification(specs[j].getName(), dblValue);
                  else if (type.equals(Boolean.TYPE))
                     specifications[j] = new Specification(specs[j].getName(), booValue);
               }
               
               //install the protocol
               attribs.getProtocolManager().installProtocolClient(pID, specifications, settings);
               protocolIDMap.put(p.getProtocolID(), pID);
            }
            catch (Exception e)
            {
               throw attribs.exception("Unable to instantiate protocol: " + p.getName() + "\n" + e.getMessage());
            }
         }
         else
         {
            Node.err.log(this, "Null protocol specified.");
            throw attribs.exception("Null protocol specified.");
         }
      }
      
      //Setup local addresses
      Address[] addresses = config.getAddresses().getAddresses();
      for (int i = 0; i < addresses.length; i++)
      {
         if (addresses[i] != null)
         {
            Address a = addresses[i];
            String xmlProtocolID = a.getProtocolID();
            ProtocolID pID = (ProtocolID) protocolIDMap.get(xmlProtocolID);
            if (pID != null)
            {
               NodeAddressID add;
               try
               {
                  add = pID.createAddressID(a.getValue(), a.isUnique());
               }
               catch (IllegalArgumentException e)
               {
                  throw attribs.exception("Address specified in invalid format: " + a.getValue());
                  
               }
               
               //Setup specs
               Spec[] specs = a.getSpecs();
               Specification[] specifications = new Specification[specs.length];
               for (int j = 0; j < specs.length; j++)
               {
                  int intVal = -1;
                  double dblValue = -1;
                  boolean booValue = false;
                  String strValue = "";
                  Class type = null;
                  if (specs[j] instanceof OtherSpec)
                  {
                     OtherSpec otherSpec = (OtherSpec) specs[j];
                     type = otherSpec.getType();
                     if (type.equals(String.class))
                        strValue = otherSpec.getStringValue();
                     else if (type.equals(Integer.TYPE))
                        intVal = otherSpec.getIntValue();
                     else if (type.equals(Double.TYPE))
                        dblValue = otherSpec.getDoubleValue();
                     else if (type.equals(Boolean.TYPE))
                        booValue = otherSpec.getBooleanValue();
                  }
                  else
                  {
                     Class specClass = specs[j].getClass();
                     try
                     {
                        Method valueMethod = specClass.getMethod("getValue", null);
                        type = valueMethod.getReturnType();
                        Object returned = valueMethod.invoke(specs[j], new Object[0]);
                        if (type.equals(String.class))
                           strValue = (String) returned;
                        else if (type.equals(Integer.TYPE))
                           intVal = ((Integer) returned).intValue();
                        else if (type.equals(Double.TYPE))
                           dblValue = ((Double) returned).doubleValue();
                        else if (type.equals(Boolean.TYPE))
                           booValue = ((Boolean) returned).booleanValue();
                        else
                           throw attribs.exception("Specification's value is of an unsupported type: " + type);
                     }
                     catch (NoSuchMethodException e)
                     {
                        Node.err.log(this, "Spec does not have a getValue() method.");
                        throw attribs.exception("Cannot load protcol specification: " + specs[j].getName());
                     }
                     catch (IllegalAccessException e)
                     {
                        throw attribs.exception("Unable to access specification's value: " + specs[j].getName());
                     }
                     catch (InvocationTargetException e)
                     {
                        throw attribs.exception("An exception was thrown while trying to access specification's value: " + 
                                                 e.getTargetException());
                     }
                  }
                  if (type.equals(String.class))
                     specifications[j] = new Specification(specs[j].getName(), strValue);
                  else if (type.equals(Integer.TYPE))
                     specifications[j] = new Specification(specs[j].getName(), intVal);
                  else if (type.equals(Double.TYPE))
                     specifications[j] = new Specification(specs[j].getName(), dblValue);
                  else if (type.equals(Boolean.TYPE))
                     specifications[j] = new Specification(specs[j].getName(), booValue);
               }
               attribs.getProtocolManager().installProtocolServer(add, specifications);
            }
            else
               throw attribs.exception("Unable to set address " + a.getValue() + ", " + "unknown protocol specified.");
         }
         else
         {
            Node.err.log(this, "Null address specified.");
            throw attribs.exception("Null address specified.");
         }
      }
      
      //Setup Node specifications
      Spec[] nodeSpecs = config.getNodeSpecs().getSpecs();
      Specification[] specifications = new Specification[nodeSpecs.length];
      for (int j = 0; j < nodeSpecs.length; j++)
      {
         int intVal = -1;
         double dblValue = -1;
         boolean booValue = false;
         String strValue = "";
         Class type = null;
         if (nodeSpecs[j] instanceof OtherSpec)
         {
            OtherSpec otherSpec = (OtherSpec) nodeSpecs[j];
            type = otherSpec.getType();
            if (type.equals(String.class))
               strValue = otherSpec.getStringValue();
            else if (type.equals(Integer.TYPE))
               intVal = otherSpec.getIntValue();
            else if (type.equals(Double.TYPE))
               dblValue = otherSpec.getDoubleValue();
            else if (type.equals(Boolean.TYPE))
               booValue = otherSpec.getBooleanValue();
         }
         else
         {
            Class specClass = nodeSpecs[j].getClass();
            try
            {
               Method valueMethod = specClass.getMethod("getValue", null);
               type = valueMethod.getReturnType();
               Object returned = valueMethod.invoke(nodeSpecs[j], new Object[0]);
               if (type.equals(String.class))
                  strValue = (String) returned;
               else if (type.equals(Integer.TYPE))
                  intVal = ((Integer) returned).intValue();
               else if (type.equals(Double.TYPE))
                  dblValue = ((Double) returned).doubleValue();
               else if (type.equals(Boolean.TYPE))
                  booValue = ((Boolean) returned).booleanValue();
               else
                  throw attribs.exception("Specification's value is of an unsupported type: " + type);
            }
            catch (NoSuchMethodException e)
            {
               Node.err.log(this, "Spec does not have a getValue() method.");
               throw attribs.exception("Cannot load protcol specification: " + nodeSpecs[j].getName());
            }
            catch (IllegalAccessException e)
            {
               throw attribs.exception("Unable to access specification's value: " + nodeSpecs[j].getName());
            }
            catch (InvocationTargetException e)
            {
               throw attribs.exception("An exception was thrown while trying to access specification's value: " + 
                                        e.getTargetException());
            }
            
         }
         if (type.equals(String.class))
            specifications[j] = new Specification(nodeSpecs[j].getName(), strValue);
         else if (type.equals(Integer.TYPE))
            specifications[j] = new Specification(nodeSpecs[j].getName(), intVal);
         else if (type.equals(Double.TYPE))
            specifications[j] = new Specification(nodeSpecs[j].getName(), dblValue);
         else if (type.equals(Boolean.TYPE))
            specifications[j] = new Specification(nodeSpecs[j].getName(), booValue);
      }
      attribs.setSpecifications(specifications);
      
      //Setup profiles
      LinkProfile[] linkProfiles = config.getLinkProfiles().getProfiles();
      for (int i = 0; i < linkProfiles.length; i++)
      {
         //Setup reqs
         Req[] reqs = linkProfiles[i].getReqs();
         Requirement[] requirements = new Requirement[reqs.length];
         for (int j = 0; j < reqs.length; j++)
         {
            String matchingSpecName = reqs[j].getName();
            String value = null;
            if (matchingSpecName.equals(XMLConfigConstants.REQ_NAME_MINSPEED))
               matchingSpecName = XMLConfigConstants.SPEC_NAME_MAXSPEED;
            else if (matchingSpecName.equals(XMLConfigConstants.REQ_NAME_PROTOCOL))
            {
               matchingSpecName = XMLConfigConstants.SPEC_NAME_PROTOCOL;
               value = protocolIDMap.get(reqs[j].getStringValue()).getClass().toString();
            }
            else if (matchingSpecName.equals(XMLConfigConstants.REQ_NAME_MAXPING))
               matchingSpecName = XMLConfigConstants.SPEC_NAME_PING;
            if (reqs[j] instanceof MaxPing)
               requirements[j] = new PingRequirement(reqs[j].getIntValue(), 50);
            else if (reqs[j].getType().equals(String.class))
            {
               if (value == null)
                  value = reqs[j].getStringValue();
               requirements[j] = new Requirement(reqs[j].getName(), matchingSpecName, reqs[j].getComparator(), value);
            }
            else if (reqs[j].getType().equals(Integer.TYPE))
               requirements[j] =
                       new Requirement(reqs[j].getName(), matchingSpecName, reqs[j].getComparator(), reqs[j].getIntValue());
            else if (reqs[j].getType().equals(Double.TYPE))
               requirements[j] = 
                       new Requirement(reqs[j].getName(), matchingSpecName, reqs[j].getComparator(), reqs[j].getDoubleValue());
            else if (reqs[j].getType().equals(Boolean.TYPE))
               requirements[j] =
                       new Requirement(reqs[j].getName(), matchingSpecName, reqs[j].getComparator(), reqs[j].getBooleanValue());
         }
         Profile.createNewLinkProfile(linkProfiles[i].getName(), requirements, linkProfiles[i].getExactMatchRequired());
      }
      
      //Node Profiles
      NodeProfile[] nodeProfiles = config.getNodeProfiles().getProfiles();
      for (int i = 0; i < nodeProfiles.length; i++)
      {
         //Setup reqs
         Req[] reqs = nodeProfiles[i].getReqs();
         Requirement[] requirements = new Requirement[reqs.length];
         for (int j = 0; j < reqs.length; j++)
         {
            String matchingSpecName = reqs[j].getName();
            if (matchingSpecName.equals(XMLConfigConstants.REQ_NAME_MINSPEED))
               matchingSpecName = XMLConfigConstants.SPEC_NAME_MAXSPEED;
            else if (matchingSpecName.equals(XMLConfigConstants.REQ_NAME_MINMEMORY))
               matchingSpecName = XMLConfigConstants.SPEC_NAME_MEMORY;
            else if (matchingSpecName.equals(XMLConfigConstants.REQ_NAME_MAXPING))
               matchingSpecName = XMLConfigConstants.SPEC_NAME_PING;
            if (reqs[j].getType().equals(String.class))
               requirements[j] =
                       new Requirement(reqs[j].getName(), matchingSpecName, reqs[j].getComparator(), reqs[j].getStringValue());
            else if (reqs[j].getType().equals(Integer.TYPE))
               requirements[j] =
                       new Requirement(reqs[j].getName(), matchingSpecName, reqs[j].getComparator(), reqs[j].getIntValue());
            else if (reqs[j].getType().equals(Double.TYPE))
               requirements[j] =
                       new Requirement(reqs[j].getName(), matchingSpecName, reqs[j].getComparator(), reqs[j].getDoubleValue());
            else if (reqs[j].getType().equals(Boolean.TYPE))
               requirements[j] = 
                       new Requirement(reqs[j].getName(), matchingSpecName, reqs[j].getComparator(), reqs[j].getBooleanValue());
         }
         Profile.createNewNodeProfile(nodeProfiles[i].getName(), requirements, nodeProfiles[i].getExactMatchRequired());
      }
      
      //start Link Manager
      LinkManager.getInstance().start();
      attribs.setInitialized();
      
      //Setup CNS      
      ServiceManager sm = attribs.getServiceManager();
      org.jcsp.net.settings.Service[] services = config.getServices().getServices();
      
      //shouldn't be that many services so just loop through array each time
      int servicePos = 0;
      for (int i = 0; i < services.length; i++)
      {
         int actualPos = -1;
         for (int j = 0; j < services.length && actualPos < 0; j++)
            if (services[j].getPosition() == servicePos)
               actualPos = j;
         
         ServiceSettings serviceSettings = new ServiceSettings(services[actualPos].getName());
         
         //get the array of addresses for each setting name
         AddressSetting[] addressSettings =  services[actualPos].getAddressSettings();
         for (int j = 0; j < addressSettings.length; j++)
         {
            //loop through the alternate addresses for the setting name
            AddressSetting addrSetting = addressSettings[j];
            while (addrSetting != null)
            {
               ProtocolID pID = (ProtocolID) protocolIDMap.get(addrSetting.getProtocolID());
               NodeAddressID addID = pID.createAddressID(addrSetting.getValue(), false);
               
               //add the address to the ServiceSettings object
               serviceSettings.addAddress(addrSetting.getName(), addID);
               
               //get the next alternate address for the current name
               addrSetting = addrSetting.getAlternate();
            }
         }
         
         Setting[] otherSettings = services[actualPos].getSettings();
         for (int j = 0; j < otherSettings.length; j++)
            serviceSettings.addSetting(otherSettings[j].getName(), otherSettings[j].getValue());
         
         if (sm.installService(serviceSettings, services[actualPos].getServiceClass()))
            //service has installed
            if (services[actualPos].getRun())
               if (!sm.startService(services[actualPos].getName()))
                  Node.info.log(this, "Service " + services[actualPos].getName() + " failed to start.");
         servicePos++;
      }
      return attribs.getNodeKey();
   }
}