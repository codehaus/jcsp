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

package org.jcsp.win32;

import java.io.*;

/**
 * Common file to ensure that the DLL gets loaded. All files in the Win32 package that declare native
 * methods from this DLL should make a call to <code>LoadDLL.go ()</code> from their static initializers.
 *
 * @author Quickstone Technologies Limited
 */
class LoadDLL
{
    /**
     * Indicates whether the library has already been loaded.
     */
    private static boolean loaded = false;

    /**
     * Loads the library if it hasn't already been loaded.
     */
    static synchronized void go()
    {
        if (loaded)return;
        File f = new File("./org.win32.dll");
        System.load(f.getAbsolutePath());
        loaded = true;
    }
}
