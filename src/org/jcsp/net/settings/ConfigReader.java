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

import java.io.*;

/**
 * <p>Used internally by the JCSP network infrastructure to load a configuration from an XML file.</p>
 *
 * <p>This is not a full XML reader, and is capable of reading only a subset of XML.</p>
 *
 * @author Quickstone Technologies Limited
 */
public class ConfigReader implements XMLConfigConstants
{
   /**
    * Diagnostic routine. This can load an XML configuration file and then display the configuration
    * structure constructed. Specify the name of the file as the first command line parameter.
    */
   static public void main(String[] args)
   {
      // Parse arguments
      if (args.length != 1)
         System.err.println("Usage: XMLReader <filename>");
      else
      {
         String filename = args[0];
         try
         {
            ConfigReader cr = new ConfigReader(new FileInputStream(filename));
            JCSPConfig config = cr.getConfig();
            System.out.println(config);
         }
         catch (Exception e)
         {
            System.err.println("Error while reading config.");
            e.printStackTrace();
         }
      }
   }
   
   /** The config built up. */
   private JCSPConfig config = new JCSPConfig();
   
   /**
    * Constructs a new configuration from the given source stream. This will attempt to parse the file
    * using recursive-descent approach.
    *
    * @param instream source of the XML configuration.
    * @throws IOException if there is a problem with the stream or the file is improperly formatted.
    * @throws XMLValidationException if there is a symantic problem with the configuration.
    */
   public ConfigReader(InputStream instream) throws IOException
   {
      BufferedReader in = new BufferedReader(new InputStreamReader(instream));
      Tag t;
      while ((t = nextTag(in)) != null)
      {
         if (t.name.equals("JCSP-CONFIG"))
         {
            doJCSP_Config(in);
            continue;
         }
         if (t.name.equals("?xml"))
            continue;
         t.bad();
      }
   }
   
   private void do_template(Reader in) throws IOException
   {
      Tag t;
      while (((t = nextTag(in)) != null) && (!t.name.equals("/" + "")))
         // put clauses here
         t.bad();
   }
   
   private void doJCSP_Config(Reader in) throws IOException
   {
      Tag t;
      while (((t = nextTag(in)) != null) && (!t.name.equals("/JCSP-CONFIG")))
      {
         if (t.name.equals(ELEMENT_SETTINGS))
         {
            if (!t.terminated) 
               doSettings(in);
            continue;
         }
         if (t.name.equals(ELEMENT_SERVICES))
         {
            if (!t.terminated) 
               doServices(in);
            continue;
         }
         if (t.name.equals(ELEMENT_PLUGINS))
         {
            if (!t.terminated) 
               doPlugins(in);
            continue;
         }
         if (t.name.equals(ELEMENT_PROTOCOLS))
         {
            if (!t.terminated) 
               doProtocols(in);
            continue;
         }
         if (t.name.equals(ELEMENT_ADDRESSES))
         {
            if (!t.terminated) 
               doAddresses(in);
            continue;
         }
         if (t.name.equals(ELEMENT_NODE_SPECS))
         {
            if (!t.terminated) 
               doNodeSpecs(in);
            continue;
         }
         if (t.name.equals(ELEMENT_LINK_PROFILES))
         {
            if (!t.terminated) 
               doLinkProfiles(in);
            continue;
         }
         if (t.name.equals(ELEMENT_NODE_PROFILES))
         {
            if (!t.terminated) 
               doNodeProfiles(in);
            continue;
         }
         t.bad();
      }
   }
   
   private void doSettings(Reader in) throws IOException
   {
      Tag t;
      while (((t = nextTag(in)) != null) && (!t.name.equals("/" + ELEMENT_SETTINGS)))
      {
         if (t.name.equals(ELEMENT_SETTING))
         {
            String name = t.getAttrib("name");
            String value = t.getAttrib("value");
            Setting setting = config.getSettings().getSetting(name);
            if (setting == null)
            {
               setting = new Setting(name, value);
               config.getSettings().addSetting(setting);
            }
            else
               throw new XMLValidationException("Setting \"" + name + "\" already exists with value " + setting.getValue());
            continue;
         }
         t.bad();
      }
   }
   
   private int servicePos = 0;
   private int protocolPos = 0;
   
   private void doServices(Reader in) throws IOException
   {
      Tag t;
      while (((t = nextTag(in)) != null) && (!t.name.equals("/" + ELEMENT_SERVICES)))
      {
         if (t.name.equals(ELEMENT_SERVICE))
         {
            String name = t.getAttrib("name");
            String className = t.getAttrib("class");
            boolean run = Tag.getBooleanValue(t.getAttrib("run"));
            try
            {
               Class serviceClass = Class.forName(className);
               config.getServices().addService(new Service(name, serviceClass, run, servicePos++));
            }
            catch (ClassNotFoundException e)
            {
               throw new XMLValidationException("Unable to load class " + className);
            }
            if (!t.terminated) 
               doService(in);
            continue;
         }
         t.bad();
      }
   }
   
   private void doService(Reader in) throws IOException
   {
      Tag t;
      while (((t = nextTag(in)) != null) && (!t.name.equals("/" + ELEMENT_SERVICE)))
      {
         if (t.name.equals(ELEMENT_ADDRESS_SETTING))
         {
            String name = t.getAttrib("name");
            String value = t.getAttrib("value");
            String pID = t.getAttrib("protocolid");
            Service current = config.getServices().getLastService();
            AddressSetting addrSetting = (AddressSetting) current.getAddressSetting(name);
            if (addrSetting == null)
            {
               addrSetting = new AddressSetting(name, value, pID);
               current.addAddressSetting(addrSetting);
            }
            else
               addrSetting.addAlternate(new AddressSetting(name, value, pID));
            continue;
         }
         if (t.name.equals(ELEMENT_SETTING))
         {
            String name = t.getAttrib("name");
            String value = t.getAttrib("value");
            Setting setting = config.getServices().getLastService().getSetting(name);
            if (setting == null)
            {
               setting = new Setting(name, value);
               config.getServices().getLastService().addSetting(setting);
            }
            else
               throw new XMLValidationException("Setting \"" + name + "\" already exists with value " + setting.getValue());
            continue;
         }
         t.bad();
      }
   }
   
   private void doPlugins(Reader in) throws IOException
   {
      Tag t;
      while (((t = nextTag(in)) != null) && (!t.name.equals("/" + ELEMENT_PLUGINS)))
      {
         if (t.name.equals(ELEMENT_PLUGIN))
         {
            String name = t.getAttrib("name");
            String className = t.getAttrib("classname");
            try
            {
               Class classToUse = Class.forName(className);
               Plugin plugin = new Plugin(name, classToUse);
               config.getPlugins().addPlugin(plugin);
            }
            catch (Exception e)
            {
               throw new XMLValidationException("Unable to load class " + className);
            }
            if (!t.terminated) 
               doPlugin(in);
            continue;
         }
         t.bad();
      }
   }
   
   private void doPlugin(Reader in) throws IOException
   {
      Tag t;
      while (((t = nextTag(in)) != null) && (!t.name.equals("/" + ELEMENT_PLUGIN)))
         // put clauses here
         t.bad();
   }
   
   private void doProtocols(Reader in) throws IOException
   {
      Tag t;
      while (((t = nextTag(in)) != null) && (!t.name.equals("/" + ELEMENT_PROTOCOLS)))
      {
         if (t.name.equals(ELEMENT_PROTOCOL))
         {
            String id = t.getAttrib("id");
            String name = t.getAttrib("name");
            String idClass = t.getAttrib("idclass");
            try
            {
               Class classToUse = Class.forName(idClass);
               Protocol protocol = new Protocol(id, name, classToUse, protocolPos++);
               config.getProtocols().addProtocol(protocol);
            }
            catch (ClassNotFoundException e)
            {
               throw new XMLValidationException("Unable to load class " + idClass);
            }
            if (!t.terminated) 
               doProtocol(in);
            continue;
         }
         t.bad();
      }
   }
   
   private void doProtocol(Reader in) throws IOException
   {
      Tag t;
      while (((t = nextTag(in)) != null) && (!t.name.equals("/" + ELEMENT_PROTOCOL)))
      {
         if (t.name.equals(ELEMENT_SPECS))
         {
            if (!t.terminated) 
               doProtocolSpecs(in);
            continue;
         }
         if (t.name.equals(ELEMENT_PROTOCOL_SETTINGS))
         {
            if (!t.terminated) 
               doProtocolSettings(in);
            continue;
         }
         t.bad();
      }
   }
   
   private void doProtocolSpecs(Reader in) throws IOException
   {
      Tag t;
      while (((t = nextTag(in)) != null)
      && (!t.name.equals("/" + ELEMENT_SPECS)))
      {
         if (t.name.equals(ELEMENT_MAXSPEED))
         {
            int value = Tag.getIntValue(t.getAttrib("value"));
            MaxSpeed m = new MaxSpeed(value);
            Protocol p = config.getProtocols().getLastProtocol();
            p.addSpec(m);
            continue;
         }
         if (t.name.equals(ELEMENT_WIRELESS))
         {
            String value = t.getAttrib("value");
            Wireless w = null;
            
            if (value.equals(XML_TRISTATE_TRUE))
               w = new Wireless(1, false);
            else if (value.equals(XML_TRISTATE_FALSE))
               w = new Wireless(-1, false);
            else if (value.equals(XML_TRISTATE_CANBE))
               w = new Wireless(0, false);
            else
               //this validation should be done by the DTD
               throw new XMLValidationException("Invalid Wireless value " + "\"" + value + "\"" + "\nUse either \"" + 
                                                XML_TRISTATE_TRUE + "\", \"" + XML_TRISTATE_FALSE + "\" or \"" + 
                                                XML_TRISTATE_CANBE + "\".");
            Protocol p = config.getProtocols().getLastProtocol();
            p.addSpec(w);
            continue;
         }
         if (t.name.equals(ELEMENT_RELIABLE))
         {
            boolean value = Tag.getBooleanValue(t.getAttrib("value"));
            Reliable r = new Reliable(value, false);
            Protocol p = config.getProtocols().getLastProtocol();
            p.addSpec(r);
            continue;
         }
         if (t.name.equals(ELEMENT_CONNECTION_ORIENTED))
         {
            boolean value = Tag.getBooleanValue(t.getAttrib("value"));
            ConnectionOriented co = new ConnectionOriented(value, false);
            Protocol p = config.getProtocols().getLastProtocol();
            p.addSpec(co);
            continue;
         }
         if (t.name.equals(ELEMENT_OTHERSPEC))
         {
            String name = t.getAttrib("name");
            String value = t.getAttrib("value");
            String type = t.getAttrib("type");
            OtherSpec os = null;
            if (type.equals(DATA_TYPE_INDICATOR_INT))
            {
               int intVal = Tag.getIntValue(value);
               os = new OtherSpec(name, intVal, false);
            }
            else if (type.equals(DATA_TYPE_INDICATOR_DOUBLE))
            {
               double dblVal = Tag.getDoubleValue(value);
               os = new OtherSpec(name, dblVal, false);
            }
            else if (type.equals(DATA_TYPE_INDICATOR_STRING))
               os = new OtherSpec(name, value, false);
            else if (type.equals(DATA_TYPE_INDICATOR_BOOLEAN))
            {
               boolean booVal = Tag.getBooleanValue(value);
               os = new OtherSpec(name, booVal, false);
            }
            Protocol p = config.getProtocols().getLastProtocol();
            p.addSpec(os);
            continue;
         }
         t.bad();
      }
   }
   
   private void doProtocolSettings(Reader in) throws IOException
   {
      Tag t;
      while (((t = nextTag(in)) != null) && (!t.name.equals("/" + ELEMENT_PROTOCOL_SETTINGS)))
      {
         if (t.name.equals(ELEMENT_PROTOCOL_SETTING))
         {
            Protocol p = config.getProtocols().getLastProtocol();
            String name = t.getAttrib("name");
            String value = t.getAttrib("value");
            Setting setting = p.getSetting(name);
            if (setting == null)
            {
               setting = new Setting(name, value);
               p.addSetting(setting);
            }
            else
               throw new XMLValidationException("Protocol Setting \"" + name + "\" already exists with value " + 
                                                setting.getValue());
            continue;
         }
         t.bad();
      }
   }
   
   private void doAddresses(Reader in) throws IOException
   {
      Tag t;
      while (((t = nextTag(in)) != null) && (!t.name.equals("/" + ELEMENT_ADDRESSES)))
      {
         if (t.name.equals(ELEMENT_ADDRESS))
         {
            String protocolid = t.getAttrib("protocolid");
            String value = t.getAttrib("value");
            boolean unique = Tag.getBooleanValue(t.getAttrib("unique"));
            Address add = new Address(protocolid, value, unique);
            config.getAddresses().addAddress(add);
            if (!t.terminated) 
               doAddress(in);
            continue;
         }
         t.bad();
      }
   }
   
   private void doAddress(Reader in) throws IOException
   {
      Tag t;
      while (((t = nextTag(in)) != null) && (!t.name.equals("/" + ELEMENT_ADDRESS)))
      {
         if (t.name.equals(ELEMENT_SPECS))
         {
            if (!t.terminated) doAddressSpecs(in);
            continue;
         }
         t.bad();
      }
   }
   
   private void doAddressSpecs(Reader in) throws IOException
   {
      Tag t;
      while (((t = nextTag(in)) != null) && (!t.name.equals("/" + ELEMENT_SPECS)))
      {
         if (t.name.equals(ELEMENT_MAXSPEED))
         {
            int value = Tag.getIntValue(t.getAttrib("value"));
            MaxSpeed m = new MaxSpeed(value);
            Address a = config.getAddresses().getLastAddress();
            a.addSpec(m);
            continue;
         }
         if (t.name.equals(ELEMENT_WIRELESS))
         {
            String value = t.getAttrib("value");
            Wireless w = null;
            if (value.equals(XML_TRISTATE_TRUE))
               w = new Wireless(1, false);
            else if (value.equals(XML_TRISTATE_FALSE))
               w = new Wireless(-1, false);
            else if (value.equals(XML_TRISTATE_CANBE))
               w = new Wireless(0, false);
            else
               //this validation should be done by the DTD
               throw new XMLValidationException("Invalid Wireless value \"" + value + "\"" + "\nUse either \"" + 
                                                XML_TRISTATE_TRUE + "\", \"" + XML_TRISTATE_FALSE + "\" or \"" + 
                                                XML_TRISTATE_CANBE + "\".");
            Address a = config.getAddresses().getLastAddress();
            a.addSpec(w);
            continue;
         }
         if (t.name.equals(ELEMENT_RELIABLE))
         {
            boolean value = Tag.getBooleanValue(t.getAttrib("value"));
            Reliable r = new Reliable(value, false);
            Address a = config.getAddresses().getLastAddress();
            a.addSpec(r);
            continue;
         }
         if (t.name.equals(ELEMENT_CONNECTION_ORIENTED))
         {
            boolean value = Tag.getBooleanValue(t.getAttrib("value"));
            ConnectionOriented co = new ConnectionOriented(value, false);
            Address a = config.getAddresses().getLastAddress();
            a.addSpec(co);
            continue;
         }
         if (t.name.equals(ELEMENT_OTHERSPEC))
         {
            String name = t.getAttrib("name");
            String value = t.getAttrib("value");
            String type = t.getAttrib("type");
            OtherSpec os = null;
            if (type.equals(DATA_TYPE_INDICATOR_INT))
            {
               int intVal = Tag.getIntValue(value);
               os = new OtherSpec(name, intVal, false);
            }
            else if (type.equals(DATA_TYPE_INDICATOR_DOUBLE))
            {
               double dblVal = Tag.getDoubleValue(value);
               os = new OtherSpec(name, dblVal, false);
            }
            else if (type.equals(DATA_TYPE_INDICATOR_STRING))
               os = new OtherSpec(name, value, false);
            else if (type.equals(DATA_TYPE_INDICATOR_BOOLEAN))
            {
               boolean booVal = Tag.getBooleanValue(value);
               os = new OtherSpec(name, booVal, false);
            }
            Address a = config.getAddresses().getLastAddress();
            a.addSpec(os);
            continue;
         }
         t.bad();
      }
   }
   
   private void doNodeSpecs(Reader in) throws IOException
   {
      Tag t;
      while (((t = nextTag(in)) != null) && (!t.name.equals("/" + ELEMENT_NODE_SPECS)))
      {
         if (t.name.equals(ELEMENT_MAXSPEED))
         {
            int value = Tag.getIntValue(t.getAttrib("value"));
            MaxSpeed m = new MaxSpeed(value);
            config.getNodeSpecs().addSpec(m);
            continue;
         }
         if (t.name.equals(ELEMENT_MEMORY))
         {
            int value = Tag.getIntValue(t.getAttrib("value"));
            Memory m = new Memory(value);
            config.getNodeSpecs().addSpec(m);
            continue;
         }
         if (t.name.equals(ELEMENT_OTHERSPEC))
         {
            String name = t.getAttrib("name");
            String value = t.getAttrib("value");
            String type = t.getAttrib("type");
            OtherSpec os = null;
            if (type.equals(DATA_TYPE_INDICATOR_INT))
            {
               int intVal = Tag.getIntValue(value);
               os = new OtherSpec(name, intVal, false);
            }
            else if (type.equals(DATA_TYPE_INDICATOR_DOUBLE))
            {
               double dblVal = Tag.getDoubleValue(value);
               os = new OtherSpec(name, dblVal, false);
            }
            else if (type.equals(DATA_TYPE_INDICATOR_STRING))
               os = new OtherSpec(name, value, false);
            else if (type.equals(DATA_TYPE_INDICATOR_BOOLEAN))
            {
               boolean booVal = Tag.getBooleanValue(value);
               os = new OtherSpec(name, booVal, false);
            }
            config.getNodeSpecs().addSpec(os);
            continue;
         }
         t.bad();
      }
   }
   
   private void doLinkProfiles(Reader in) throws IOException
   {
      Tag t;
      while (((t = nextTag(in)) != null) && (!t.name.equals("/" + ELEMENT_LINK_PROFILES)))
      {
         if (t.name.equals(ELEMENT_LINK_PROFILE))
         {
            String name = t.getAttrib("name");
            boolean exactMatchRequired = Tag.getBooleanValue(t.getAttrib("requireExactMatch"));
            LinkProfile lp = new LinkProfile(name, exactMatchRequired);
            config.getLinkProfiles().addProfile(lp);
            if (!t.terminated) 
               doLinkProfile(in);
            continue;
         }
         t.bad();
      }
   }
   
   private void doLinkProfile(Reader in) throws IOException
   {
      Tag t;
      while (((t = nextTag(in)) != null) && (!t.name.equals("/" + ELEMENT_LINK_PROFILE)))
      {
         if (t.name.equals(ELEMENT_LINK_REQS))
         {
            if (!t.terminated) 
               doLinkReqs(in);
            continue;
         }
         t.bad();
      }
   }
   
   private void doLinkReqs(Reader in) throws IOException
   {
      Tag t;
      while (((t = nextTag(in)) != null) && (!t.name.equals("/" + ELEMENT_LINK_REQS)))
      {
         if (t.name.equals(ELEMENT_LINK_REQ_MINSPEED))
         {
            int value = Tag.getIntValue(t.getAttrib("value"));
            MinSpeed ms = new MinSpeed(value);
            config.getLinkProfiles().getLastProfile().addReq(ms);
            continue;
         }
         if (t.name.equals(ELEMENT_LINK_REQ_PROTOCOL))
         {
            String protocolid = t.getAttrib("protocolid");
            ReqProtocol rp = new ReqProtocol(protocolid);
            config.getLinkProfiles().getLastProfile().addReq(rp);
            continue;
         }
         if (t.name.equals(ELEMENT_WIRELESS))
         {
            String value = t.getAttrib("value");
            Wireless w = null;
            if (value.equals(XML_TRISTATE_TRUE))
               w = new Wireless(1, true);
            else if (value.equals(XML_TRISTATE_FALSE))
               w = new Wireless(-1, true);
            else if (value.equals(XML_TRISTATE_CANBE))
               w = new Wireless(0, true);
            else
               //this validation should be done by the DTD
               throw new XMLValidationException("Invalid Wireless value \"" + value + "\"\nUse either \"" + XML_TRISTATE_TRUE + 
                                                "\", \"" + XML_TRISTATE_FALSE + "\" or \"" + XML_TRISTATE_CANBE + "\".");
            config.getLinkProfiles().getLastProfile().addReq(w);
            continue;
         }
         if (t.name.equals(ELEMENT_RELIABLE))
         {
            boolean value = Tag.getBooleanValue(t.getAttrib("value"));
            Reliable r = new Reliable(value, true);
            config.getLinkProfiles().getLastProfile().addReq(r);
            continue;
         }
         if (t.name.equals(ELEMENT_CONNECTION_ORIENTED))
         {
            boolean value = Tag.getBooleanValue(t.getAttrib("value"));
            ConnectionOriented co = new ConnectionOriented(value, true);
            config.getLinkProfiles().getLastProfile().addReq(co);
            continue;
         }
         if (t.name.equals(ELEMENT_LINK_REQ_MAXPING))
         {
            int value = Tag.getIntValue(t.getAttrib("value"));
            MaxPing mp = new MaxPing(value);
            config.getLinkProfiles().getLastProfile().addReq(mp);
            continue;
         }
         if (t.name.equals(ELEMENT_LINK_REQ_OTHER))
         {
            String name = t.getAttrib("name");
            String comparator = t.getAttrib("comparator");
            String type = t.getAttrib("type");
            String value = t.getAttrib("value");
            OtherReq or = null;
            if (type.equals(DATA_TYPE_INDICATOR_INT))
            {
               int intVal = Tag.getIntValue(value);
               or = new OtherReq(name, intVal, comparator);
            }
            else if (type.equals(DATA_TYPE_INDICATOR_DOUBLE))
            {
               double dblVal = Tag.getDoubleValue(value);
               or = new OtherReq(name, dblVal, comparator);
            }
            else if (type.equals(DATA_TYPE_INDICATOR_STRING))
               or = new OtherReq(name, value, comparator);
            else if (type.equals(DATA_TYPE_INDICATOR_BOOLEAN))
            {
               boolean booVal = Tag.getBooleanValue(value);
               or = new OtherReq(name, booVal, comparator);
            }
            config.getLinkProfiles().getLastProfile().addReq(or);
            continue;
         }
         t.bad();
      }
   }
   
   private void doNodeProfiles(Reader in) throws IOException
   {
      Tag t;
      while (((t = nextTag(in)) != null) && (!t.name.equals("/" + ELEMENT_NODE_PROFILES)))
      {
         if (t.name.equals(ELEMENT_NODE_PROFILE))
         {
            String name = t.getAttrib("name");
            boolean exactMatchRequired = Tag.getBooleanValue(t.getAttrib("requireExactMatch"));
            NodeProfile np = new NodeProfile(name, exactMatchRequired);
            config.getNodeProfiles().addProfile(np);
            if (!t.terminated) doNodeProfile(in);
            continue;
         }
         t.bad();
      }
   }
   
   private void doNodeProfile(Reader in) throws IOException
   {
      Tag t;
      while (((t = nextTag(in)) != null) && (!t.name.equals("/" + ELEMENT_NODE_PROFILE)))
      {
         if (t.name.equals(ELEMENT_NODE_REQS))
         {
            if (!t.terminated) 
               doNodeReqs(in);
            continue;
         }
         t.bad();
      }
   }
   
   private void doNodeReqs(Reader in) throws IOException
   {
      Tag t;
      while (((t = nextTag(in)) != null) && (!t.name.equals("/" + ELEMENT_NODE_REQS)))
      {
         if (t.name.equals(ELEMENT_NODE_REQ_MINSPEED))
         {
            int value = Tag.getIntValue(t.getAttrib("value"));
            MinSpeed ms = new MinSpeed(value);
            config.getNodeProfiles().getLastProfile().addReq(ms);
            continue;
         }
         if (t.name.equals(ELEMENT_NODE_REQ_MINMEMORY))
         {
            int value = Tag.getIntValue(t.getAttrib("value"));
            MinMemory mm = new MinMemory(value);
            config.getNodeProfiles().getLastProfile().addReq(mm);
            continue;
         }
         if (t.name.equals(ELEMENT_NODE_REQ_OTHER))
         {
            String name = t.getAttrib("name");
            String comparator = t.getAttrib("comparator");
            String type = t.getAttrib("type");
            String value = t.getAttrib("value");
            OtherReq or = null;
            if(type.equals(DATA_TYPE_INDICATOR_INT))
            {
               int intVal = Tag.getIntValue(value);
               or = new OtherReq(name, intVal, comparator);
            }
            else if(type.equals(DATA_TYPE_INDICATOR_DOUBLE))
            {
               double dblVal = Tag.getDoubleValue(value);
               or = new OtherReq(name, dblVal, comparator);
            }
            else if(type.equals(DATA_TYPE_INDICATOR_STRING))
               or = new OtherReq(name, value, comparator);
            else if(type.equals(DATA_TYPE_INDICATOR_BOOLEAN))
            {
               boolean booVal = Tag.getBooleanValue(value);
               or = new OtherReq(name, booVal, comparator);
            }
            config.getNodeProfiles().getLastProfile().addReq(or);
            continue;
         }
         t.bad();
      }
   }
   
   public JCSPConfig getConfig()
   {
      return config;
   }
   
   private static final int MAX_ATTRIBS = 20;
   private final String[] attribs = new String[MAX_ATTRIBS];
   private final String[] values = new String[MAX_ATTRIBS];
   
   private Tag nextTag(Reader in) throws IOException
   {
      int i;
      char c;
      String name;
      boolean terminated = false; //put in for J# compat - is always set
      do
      {
         // skip whitespace
         do
         {
            i = in.read();
            if (i < 0)
               return null;
            c = (char) i;
         } while (isSpace(c));
         // expect to find a '<' character
         if (c != '<')
            throw new IOException("Expected '<'");
         // get the tag name (up to a space)
         c = nextChar(in);
         if (c != '!') 
            break;
         // comment - skip until '>'
         do
         {
            c = nextChar(in);
         } while (c != '>');
      } while (true);
      name = "";
      do
      {
         name = name + c;
         c = nextChar(in);
      } while ((!isSpace(c)) && (c != '/') && (c != '>'));
      // get attributes
      i = 0;
      do
      {
         // skip whitespace
         while (isSpace(c))
            c = nextChar(in);
         if (c == '>')
         {
            terminated = false;
            break;
         }
         if (c == '/')
         {
            terminated = true;
            while (c != '>')
               c = nextChar(in);
            break;
         }
         // read the attribute name
         attribs[i] = "";
         while ((c != '=') && (!isSpace(c)) && (c != '>') && (c != '/'))
         {
            attribs[i] = attribs[i] + c;
            c = nextChar(in);
         }
         // read the attribute value
         c = nextChar(in); // a quote
         if (c == '\"')
         {
            c = nextChar(in); // skip the quote
            values[i] = "";
            do
            {
               values[i] = values[i] + c;
               c = nextChar(in);
            } while (c != '\"');
            c = nextChar(in);
            i++;
         }
         else
         {
            // skip to the '>' or '/' characters
            while ((c != '>') && (c != '/'))
               c = nextChar(in);
         }
      } while (true);
      // return the tag
      String[] a = new String[i], v = new String[i];
      for (int j = 0; j < i; j++)
      {
         a[j] = attribs[j];
         v[j] = values[j];
      }
      return new Tag(name, a, v, terminated);
   }
   
   private boolean isSpace(char c)
   {
      return (c == ' ') || (c == '\t') || (c == '\r') || (c == '\n');
   }
   
   private char nextChar(Reader in) throws IOException
   {
      int i = in.read();
      if (i < 0)
         throw new IOException("Unexpected end of file");
      return (char) i;
   }
   
   private static class Tag
   {
      public final String name;
      public final String[] attrib;
      public final String[] value;
      public final boolean terminated;
      public Tag(String name, String[] attrib, String[] value, boolean terminated)
      {
         this.name = name;
         this.attrib = attrib;
         this.value = value;
         this.terminated = terminated;
      }
      public String getAttrib(String attr, String def)
      {
         for (int i = 0; i < attrib.length; i++)
         {
            if (attr.equals(attrib[i]))
               return value[i];
         }
         return def;
      }
      public String getAttrib(String attr)
      {
         return getAttrib(attr, null);
      }
      public void bad() throws IOException
      {
         throw new IOException("Unexpected tag - " + name);
      }
      public static boolean getBooleanValue(String val)
      {
         if (val.equals(XML_BOOLEAN_TRUE))
            return true;
         if (val.equals(XML_BOOLEAN_FALSE))
            return false;
         throw new XMLValidationException("expected boolean tag");
      }
      public static int getIntValue(String val)
      {
         return Integer.parseInt(val);
      }
      public static double getDoubleValue(String val)
      {
         return Double.parseDouble(val);
      }
   }
   
   /**
    * Thrown in the event of a semantic error in the parsed XML file.
    *
    * @author Quickstone Technologies Limited
    */
   public static class XMLValidationException extends RuntimeException
   {
      /**
       * Creates a new exception without a detail message.
       */
      public XMLValidationException()
      {
         super();
      }
      
      /**
       * Creates a new exception with a detail message.
       *
       * @param msg the detail message.
       */
      public XMLValidationException(String msg)
      {
         super(msg);
      }
   }
}