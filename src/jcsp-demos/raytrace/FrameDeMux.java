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
 * @author Quickstone Technologies Limited
 */
class FrameDeMux implements CSProcess {
	
	private final ChannelInput in;
	private final ChannelOutput[] out;
	
	public FrameDeMux (final ChannelInput in, final ChannelOutput[] out) {
		this.in = in;
		this.out = out;
	}
	
	public void run () {
		System.out.println ("Frame DeMux: started");
		while (true) {
			//System.out.println ("Frame DeMux: waiting for data");
			ResultPacket rp = (ResultPacket)in.read ();
			//System.out.println ("Frame DeMux: frame " + rp.frame + " data received");
			out[rp.frame % out.length].write (rp);
		}
	}
	
}
