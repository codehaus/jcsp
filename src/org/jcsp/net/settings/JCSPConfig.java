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

/**
 * Used internally within the JCSP network infrastructure to represent the overall configuration.
 *
 * @author Quickstone Technologies Limited
 */
public class JCSPConfig
{
   public void setSettings(Settings settings)
   {
      this.settings = settings;
   }
   
   public Settings getSettings()
   {
      return settings;
   }
   
   public void setServices(Services services)
   {
      this.services = services;
   }
   
   public Services getServices()
   {
      return services;
   }
   
   public void setPlugins(Plugins plugins)
   {
      this.plugins = plugins;
   }
   
   public Plugins getPlugins()
   {
      return plugins;
   }
   
   public void setProtocols(Protocols protocols)
   {
      this.protocols = protocols;
   }
   
   public Protocols getProtocols()
   {
      return protocols;
   }
   
   public void setAddresses(Addresses addresses)
   {
      this.addresses = addresses;
   }
   
   public Addresses getAddresses()
   {
      return addresses;
   }
   
   public void setNodeSpecs(Specs specs)
   {
      this.nodeSpecs = specs;
   }
   
   public Specs getNodeSpecs()
   {
      return nodeSpecs;
   }
   
   public void setLinkProfiles(LinkProfiles linkProfiles)
   {
      this.linkProfiles = linkProfiles;
   }
   
   public LinkProfiles getLinkProfiles()
   {
      return linkProfiles;
   }
   
   public void setNodeProfiles(NodeProfiles nodeProfiles)
   {
      this.nodeProfiles = nodeProfiles;
   }
   
   public NodeProfiles getNodeProfiles()
   {
      return nodeProfiles;
   }
   
   public String toString()
   {
      StringBuffer sb = new StringBuffer();
      sb.append("<JCSPConfig>\n");
      sb.append(tabIn(settings.toString())).append("\n");
      sb.append(tabIn(services.toString())).append("\n");
      sb.append(tabIn(plugins.toString())).append("\n");
      sb.append(tabIn(protocols.toString())).append("\n");
      sb.append(tabIn(addresses.toString())).append("\n");
      sb.append(tabIn(nodeSpecs.toString())).append("\n");
      sb.append(tabIn(linkProfiles.toString())).append("\n");
      sb.append(tabIn(nodeProfiles.toString())).append("\n");
      sb.append("</JCSPConfig>");
      return sb.toString();
   }
   
   public static String tabIn(String string)
   {
      StringBuffer sb = new StringBuffer(string);
      //tab in first line
      sb.insert(0, "   ");
      for(int i=0; i<sb.length(); i++)
         if(sb.charAt(i) == '\n')
            sb.insert(i+1, "   ");
      return sb.toString();
   }
   
   private Settings settings = new Settings();
   private Services services = new Services();
   private Plugins plugins = new Plugins();
   private Protocols protocols = new Protocols();
   private Addresses addresses = new Addresses();
   private Specs nodeSpecs = new Specs();
   private LinkProfiles linkProfiles = new LinkProfiles();
   private NodeProfiles nodeProfiles = new NodeProfiles();
}