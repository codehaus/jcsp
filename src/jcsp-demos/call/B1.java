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
class B1 implements CSProcess, Foo {

  private final ChannelAccept in;

  public B1 (final ChannelAccept in) {
    this.in = in;
  }

  public int calculate (int a, double b, long c) {
    int result = a + (int) b + (int) c;
    System.out.println ("B.calculate: " + a + ", " + b + ", " + c + " ==> " + result);
    return result;
  }

  public void  processQuery (int a, double b, long c) {
    System.out.println ("B1.processQuery: " + a + ", " + b + ", " + c);
  }

  public double closeValve (int a, double b, long c) {
    int result = a + (int) b + (int) c;
    System.out.println ("B1.closeValve: " + a + ", " + b + ", " + c + " ==> " + result);
    return result;
  }

  public void run () {
    in.accept (this);
    in.accept (this);
    in.accept (this);
  }

}
