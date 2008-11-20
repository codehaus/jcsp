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
class CrewDisplay implements CSProcess {

  private final ChannelInputInt in, info;

  public CrewDisplay (final ChannelInputInt in, final ChannelInputInt info) {
    this.in = in;
    this.info = info;
  }

  protected void initialise () {
  }

  protected void updateBlackboard (char scribbleChar) {
    System.out.print ("BLACKBOARD = ");
    System.out.print (scribbleChar);
    System.out.print (scribbleChar);
    System.out.println (scribbleChar);
  }

  public void run () {

    final int zeroInt = 48;
    int scribbleInt;
    char scribbleChar = '?';

    initialise ();
    updateBlackboard (scribbleChar);

    while (true) {
      int state = in.read ();
      int philId = info.read ();
      switch (state) {
        case PhilState.THINKING:
          System.out.println ("\tPhil " + philId + " : thinking ...");
        break;
        case PhilState.WANNA_READ:
          System.out.println ("\t\tPhil " + philId + " : wanna read ...");
        break;
        case PhilState.READING:
          System.out.println ("\t\t\tPhil " + philId + " : reading ...");
        break;
        case PhilState.DONE_READ:
          scribbleInt = info.read ();
          if (scribbleInt != -1) scribbleChar = (char) (scribbleInt + zeroInt);
          System.out.print ("\t\t\t\tPhil " + philId + " : read ");
          System.out.print (scribbleChar);
          System.out.print (scribbleChar);
          System.out.println (scribbleChar);
        break;
        case PhilState.WANNA_WRITE:
          System.out.print ("\t\t\t\t\tPhil " + philId + " : wanna write ... ");
          System.out.print (philId);
          System.out.print (philId);
          System.out.println (philId);
        break;
        case PhilState.WRITING:
          System.out.print ("\t\t\t\t\t\tPhil " + philId + " : writing ... ");
          System.out.print (philId);
          System.out.print (philId);
          System.out.println (philId);
        break;
        case PhilState.DONE_WRITE:
          scribbleInt = info.read ();
          scribbleChar = (char) (scribbleInt + zeroInt);
          System.out.print ("\t\t\t\t\t\t\tPhil " + philId + " : wrote ... ");
          System.out.print (scribbleChar);
          System.out.print (scribbleChar);
          System.out.println (scribbleChar);
          updateBlackboard (scribbleChar);
        break;
        case PhilState.TIME:
          System.out.println ("TICK " + philId);
        break;
      }
    }

  }

}
