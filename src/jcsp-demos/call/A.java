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


import org.jcsp.lang.*;

/**
 * @author P.H. Welch
 */
class A implements CSProcess {

  private final Foo out;

  public A (final Foo out) {
    this.out = out;
  }

  public void run () {
    System.out.println ("A: out.calculate (3, 4.0, 5)");
    int t = out.calculate (3, 4.0, 5);
    System.out.println ("A: ==> " + t);
    System.out.println ("A: out.closeValve (6, 7.0, 8)");
    double s = out.closeValve (6, 7.0, 8);
    System.out.println ("A: ==> " + s);
    System.out.println ("A: out.processQuery (0, 1.0, 2)");
    out.processQuery (0, 1.0, 2);
    System.out.println ("A: ==> returned OK");
  }

}
