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

/**
 * Node initialisation factory interface. A specific initialisation can be written by implementing
 * this interface and passing an instance to the <code>factoryInit</code> method of
 * <code>Node</code>.
 *
 * @author Quickstone Technologies Limited
 */
public interface NodeFactory extends Serializable
{
   /*
    * Initialises the node passed and returns the resulting <code>NodeKey</code> for the caller
    * to subsequently use.
    *
    * @return the <code>NodeKey</code> of the Node or <code>null</code>
    *          if initialization failed.
    */
   public NodeKey initNode(Node node, Node.Attributes attribs) throws NodeInitFailedException;
}