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


/**
 * @author Quickstone Technologies Limited
 */
final class Counter {
	
	private static final int NUM_SAMPLES = 50;
	
	private final long timeSample[] = new long[NUM_SAMPLES];
	private final long dataSample[] = new long[NUM_SAMPLES];
	private int samplePtr = 0, samples = 0;
	private long timeSum = 0, dataSum = 0;
	
	private long tag = 0;
	
	public final void click () {
		long t = System.currentTimeMillis ();
		if (tag == 0) {
			tag = t;
			return;
		}
		timeSum -= timeSample[samplePtr];
		timeSum += (timeSample[samplePtr] = (t - tag));
		if (++samplePtr == NUM_SAMPLES) samplePtr = 0;
		if (samples < NUM_SAMPLES) samples++;
		if ((samplePtr & 15) == 0) {
			System.out.println ("Count = " + (timeSum / samples) + "ms/frame (" + ((double)samples * 1000.0 / (double)timeSum) + " fps)");
		}
		tag = t;
	}
	
	public final void dataStart () {
		tag = System.currentTimeMillis ();
	}
		
	public final void dataEnd (int bytes) {
		long t = System.currentTimeMillis ();
		timeSum -= timeSample[samplePtr];
		timeSum += (timeSample[samplePtr] = (t - tag));
		dataSum -= dataSample[samplePtr];
		dataSum += (dataSample[samplePtr] = bytes);
		if (++samplePtr == NUM_SAMPLES) samplePtr = 0;
		if (samples < NUM_SAMPLES) samples++;
		if ((samplePtr & 15) == 0) {
			System.out.println ("Data = " + (dataSum / timeSum) + "pixels/ms");
		}
	}
	
}
