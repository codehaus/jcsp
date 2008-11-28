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
    //////////////////////////////////////////////////////////////////////

package org.jcsp.lang;

class Any2OneIntImpl implements ChannelInternalsInt, Any2OneChannelInt {

	private ChannelInternalsInt channel;
	private final Object writeMonitor = new Object();
	
	Any2OneIntImpl(ChannelInternalsInt _channel) {
		channel = _channel;
	}

	//Begin never used:
	public void endRead() {
		channel.endRead();
	}

	public int read() {
		return channel.read();
	}

	public boolean readerDisable() {
		return channel.readerDisable();
	}

	public boolean readerEnable(Alternative alt) {
		return channel.readerEnable(alt);
	}

	public boolean readerPending() {
		return channel.readerPending();
	}

	public void readerPoison(int strength) {
		channel.readerPoison(strength);

	}

	public int startRead() {
		return channel.startRead();
	}
	//End never used

	public void write(int n) {
		synchronized (writeMonitor) {
			channel.write(n);
		}

	}

	public void writerPoison(int strength) {
		synchronized (writeMonitor) {
			channel.writerPoison(strength);
		}

	}

	public AltingChannelInputInt in() {
		return new AltingChannelInputIntImpl(channel,0);
	}

	public SharedChannelOutputInt out() {
		return new SharedChannelOutputIntImpl(this,0);
	}

}
