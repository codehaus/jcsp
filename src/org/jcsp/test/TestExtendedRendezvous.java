package org.jcsp.test;

import java.util.Arrays;
import java.util.Collections;

import junit.framework.TestCase;

import org.jcsp.lang.AltingBarrier;
import org.jcsp.lang.Any2AnyChannel;
import org.jcsp.lang.Any2AnyChannelInt;
import org.jcsp.lang.Any2OneChannel;
import org.jcsp.lang.Any2OneChannelInt;
import org.jcsp.lang.CSProcess;
import org.jcsp.lang.Channel;
import org.jcsp.lang.ChannelInput;
import org.jcsp.lang.ChannelInputInt;
import org.jcsp.lang.ChannelInt;
import org.jcsp.lang.ChannelOutput;
import org.jcsp.lang.ChannelOutputInt;
import org.jcsp.lang.Guard;
import org.jcsp.lang.One2AnyChannel;
import org.jcsp.lang.One2AnyChannelInt;
import org.jcsp.lang.One2OneChannel;
import org.jcsp.lang.One2OneChannelInt;
import org.jcsp.lang.Parallel;
import org.jcsp.lang.Sequence;
import org.jcsp.util.Buffer;
import org.jcsp.util.OverFlowingBuffer;
import org.jcsp.util.ints.BufferInt;

public class TestExtendedRendezvous extends TestCase {

  /**
   * Asserts that the two arrays are equal.  The arrays are equal iff
   * they are the same length, and the Object references in them
   * are the same references (and in the same order)
   */
  public void assertArraysEqual(Object[] a,Object[] b) {
    assertEquals(a.length,b.length);
    
    for (int i = 0;i < a.length;i++) {
      assertSame(a[i],b[i]);
    }     
  }
  
  public void assertArraysEqual(int[] a,int[] b) {
	    assertEquals(a.length,b.length);
	    
	    for (int i = 0;i < a.length;i++) {
	      assertEquals(a[i],b[i]);
	    }     
	  }
  
  public void helper_testChannelOneWriter(ChannelInput in,ChannelOutput out) {
    
    /*
     * This test checks that the reads and writes occur in the right order during an extended rendezvous
     * 
     *  Specifically , it checks that the extended action always occurs before the write has finished
     *
     * The event-recorder offers a read event, a write event and a terminate event
     * The terminate event is only done (by the BarrierSyncer) once the other processes have finished
     * 
     * The writer offers the write event *after* each write.
     * The reader offers the single read event during each extended rendezvous.
     * 
     * Therefore the events should be in lock-step:
     * read,write,read,write...
     */

    AltingBarrier[] terminateEvent = AltingBarrier.create(2);
    
    AltingBarrier[] writeEvent = AltingBarrier.create(2);
    AltingBarrier[] readEvent = AltingBarrier.create(2);
        
    EventRecorder recorder;
    
    new Parallel( new CSProcess[] {
        recorder = new EventRecorder(new Guard[] {writeEvent[0],readEvent[0],terminateEvent[0]},2),
        new Sequence(new CSProcess[] {
            new Parallel( new CSProcess[] {
                new WriterProcess(out,Collections.nCopies(100,null),writeEvent[1]),
                new ExtendedReaderSync(readEvent[1],in,100)
            }),
            new BarrierSyncer(terminateEvent[1])
        })
    }).run();
    
    Guard[] expectedEvents = new Guard[201];
    
    for (int i = 0;i < 100;i++) {
      expectedEvents[i*2 + 0] = readEvent[0];
      expectedEvents[i*2 + 1] = writeEvent[0];      
    }
    
    expectedEvents[200] = terminateEvent[0];
    
    assertArraysEqual(expectedEvents,recorder.getObservedEvents());
  }
  
  public void testNormalOne2OneChannel() {
	  One2OneChannel chan = Channel.one2one();
	  helper_testChannelOneWriter(chan.in(), chan.out());  
  }
  
  public void testNormalAny2OneChannel() {
	  Any2OneChannel chan = Channel.any2one();
	  helper_testChannelOneWriter(chan.in(), chan.out());  
  }
  
  public void testNormalOne2AnyChannel() {
	  One2AnyChannel chan = Channel.one2any();
	  helper_testChannelOneWriter(chan.in(), chan.out());  
  }
  
  public void testNormalAny2AnyChannel() {
	  Any2AnyChannel chan = Channel.any2any();
	  helper_testChannelOneWriter(chan.in(), chan.out());
  }
  
  public void helper_testIntChannelOneWriter(ChannelInputInt in,ChannelOutputInt out) {
	    
	    /*
	     * This test checks that the reads and writes occur in the right order during an extended rendezvous
	     * 
	     *  Specifically , it checks that the extended action always occurs before the write has finished
	     *
	     * The event-recorder offers a read event, a write event and a terminate event
	     * The terminate event is only done (by the BarrierSyncer) once the other processes have finished
	     * 
	     * The writer offers the write event *after* each write.
	     * The reader offers the single read event during each extended rendezvous.
	     * 
	     * Therefore the events should be in lock-step:
	     * read,write,read,write...
	     */

	    AltingBarrier[] terminateEvent = AltingBarrier.create(2);
	    
	    AltingBarrier[] writeEvent = AltingBarrier.create(2);
	    AltingBarrier[] readEvent = AltingBarrier.create(2);
	        
	    EventRecorder recorder;
	    
	    new Parallel( new CSProcess[] {
	        recorder = new EventRecorder(new Guard[] {writeEvent[0],readEvent[0],terminateEvent[0]},2),
	        new Sequence(new CSProcess[] {
	            new Parallel( new CSProcess[] {
	                new WriterProcessInt(out,new int[100],writeEvent[1]),
	                new ExtendedReaderSyncInt(readEvent[1],in,100)
	            }),
	            new BarrierSyncer(terminateEvent[1])
	        })
	    }).run();
	    
	    Guard[] expectedEvents = new Guard[201];
	    
	    for (int i = 0;i < 100;i++) {
	      expectedEvents[i*2 + 0] = readEvent[0];
	      expectedEvents[i*2 + 1] = writeEvent[0];      
	    }
	    
	    expectedEvents[200] = terminateEvent[0];
	    
	    assertArraysEqual(expectedEvents,recorder.getObservedEvents());
	  }
	  
	  public void testNormalOne2OneChannelInt() {
		  One2OneChannelInt chan = Channel.one2oneInt();
		  helper_testIntChannelOneWriter(chan.in(), chan.out());  
	  }
	  
	  public void testNormalAny2OneChannelInt() {
		  Any2OneChannelInt chan = ChannelInt.any2one();
		  helper_testIntChannelOneWriter(chan.in(), chan.out());  
	  }
	  
	  public void testNormalOne2AnyChannelInt() {
		  One2AnyChannelInt chan = ChannelInt.one2any();
		  helper_testIntChannelOneWriter(chan.in(), chan.out());  
	  }
	  
	  public void testNormalAny2AnyChannelInt() {
		  Any2AnyChannelInt chan = ChannelInt.any2any();
		  helper_testIntChannelOneWriter(chan.in(), chan.out());
	  }
  
  
  
  
  
  
  
  
  public void helper_testFIFOChannelOneWriter(ChannelInput in,ChannelOutput out) {
    
    /*
     * Since the reads and writes are fairly independent, this test instead checks
     * that all the data flows in the correct write order without getting lost, even when the
     * reader is slower than the writer
     * 
     */

    AltingBarrier[] delayEvent = AltingBarrier.create(2);
            
    ExtendedReaderSync reader;
    
    Object[] values = new Object[100];
    
    for (int i = 0;i < 100;i++) {
      values[i] = new Object(); 
    }
    
    new Parallel( new CSProcess[] {
        new DelaySyncer(delayEvent[0],5,100),
        new WriterProcess(out,Arrays.asList(values),(AltingBarrier)null),
        reader = new ExtendedReaderSync(delayEvent[1],in,100)
    }).run();
            
    assertArraysEqual(values,reader.getValuesRead());
  }
  
  public void testFIFO_One2OneChannel() {  
    One2OneChannel chan4 = Channel.one2one(new Buffer(4));
    helper_testFIFOChannelOneWriter(chan4.in(), chan4.out());
    
    One2OneChannel chan1 = Channel.one2one(new Buffer(1));
    helper_testFIFOChannelOneWriter(chan1.in(), chan1.out());
  }

  public void testFIFO_Any2OneChannel() {  
	    Any2OneChannel chan4 = Channel.any2one(new Buffer(4));
	    helper_testFIFOChannelOneWriter(chan4.in(), chan4.out());
	    
	    Any2OneChannel chan1 = Channel.any2one(new Buffer(1));
	    helper_testFIFOChannelOneWriter(chan1.in(), chan1.out());
	  }
  
  public void testFIFO_One2AnyChannel() {  
	    One2AnyChannel chan4 = Channel.one2any(new Buffer(4));
	    helper_testFIFOChannelOneWriter(chan4.in(), chan4.out());
	    
	    One2AnyChannel chan1 = Channel.one2any(new Buffer(1));
	    helper_testFIFOChannelOneWriter(chan1.in(), chan1.out());
	  }
  
  public void testFIFO_Any2AnyChannel() {  
	    Any2AnyChannel chan4 = Channel.any2any(new Buffer(4));
	    helper_testFIFOChannelOneWriter(chan4.in(), chan4.out());
	    
	    Any2AnyChannel chan1 = Channel.any2any(new Buffer(1));
	    helper_testFIFOChannelOneWriter(chan1.in(), chan1.out());
	  }
  
  //The given channel should be using a FIFO buffer (of any size >= 1)
  public void helper_testFIFOChannelOneWriterInt(ChannelInputInt in,ChannelOutputInt out) {
	    
	    /*
	     * Since the reads and writes are fairly independent, this test instead checks
	     * that all the data flows in the correct write order without getting lost, even when the
	     * reader is slower than the writer
	     * 
	     */

	    AltingBarrier[] delayEvent = AltingBarrier.create(2);
	            
	    ExtendedReaderSyncInt reader;
	    
	    int[] values = new int[100];
	    
	    for (int i = 0;i < 100;i++) {
	      values[i] = i; 
	    }
	    
	    new Parallel( new CSProcess[] {
	        new DelaySyncer(delayEvent[0],5,100),
	        new WriterProcessInt(out,values,(AltingBarrier)null),
	        reader = new ExtendedReaderSyncInt(delayEvent[1],in,100)
	    }).run();
	            	    
	    assertArraysEqual(values,reader.getValuesRead());
	  }
	  
	  public void testFIFO_One2OneChannelInt() {  
	    One2OneChannelInt chan4 = Channel.one2oneInt(new BufferInt(4));
	    helper_testFIFOChannelOneWriterInt(chan4.in(), chan4.out());
	    
	    One2OneChannelInt chan1 = Channel.one2oneInt(new BufferInt(1));
	    helper_testFIFOChannelOneWriterInt(chan1.in(), chan1.out());
	  }

	  public void testFIFO_Any2OneChannelInt() {  
		    Any2OneChannelInt chan4 = Channel.any2oneInt(new BufferInt(4));
		    helper_testFIFOChannelOneWriterInt(chan4.in(), chan4.out());
		    
		    Any2OneChannelInt chan1 = Channel.any2oneInt(new BufferInt(1));
		    helper_testFIFOChannelOneWriterInt(chan1.in(), chan1.out());
		  }
	  
	  public void testFIFO_One2AnyChannelInt() {  
		    One2AnyChannelInt chan4 = Channel.one2anyInt(new BufferInt(4));
		    helper_testFIFOChannelOneWriterInt(chan4.in(), chan4.out());
		    
		    One2AnyChannelInt chan1 = Channel.one2anyInt(new BufferInt(1));
		    helper_testFIFOChannelOneWriterInt(chan1.in(), chan1.out());
		  }
	  
	  public void testFIFO_Any2AnyChannelInt() {  
		    Any2AnyChannelInt chan4 = Channel.any2anyInt(new BufferInt(4));
		    helper_testFIFOChannelOneWriterInt(chan4.in(), chan4.out());
		    
		    Any2AnyChannelInt chan1 = ChannelInt.any2anyInt(new BufferInt(1));
		    helper_testFIFOChannelOneWriterInt(chan1.in(), chan1.out());
		  }
  
  
  public void testOverflowingFIFOOne2OneChannel() {
    /*
     * This test checks that the buffer does overflow if  the reader is in the middle
     * of an extended rendezvous
     * 
     * The buffer is size 4.  We let the writer send 4 times, then send our fifth item before syncing
     * (the fifth should overflow and be lost) with the reader's sync during the 1st item.
     * Repeat until 100 items have been sent (80 received)
     */
    
    One2OneChannel chan = Channel.one2one(new OverFlowingBuffer(4));
    
    AltingBarrier[] syncEvent = AltingBarrier.create(2);
    
    Object[] sendValues = new Object[100];
    Object[] expValues = new Object[80];
    
    AltingBarrier[][] readerSync = new AltingBarrier[80][];
    AltingBarrier[][] writerSync = new AltingBarrier[100][];
    
    /*
     * The syncs are confusing.  We need this to happen:
     * 
     * WRITE}
     * WRITE}
     * WRITE}begin READ
     * WRITE}
     * WRITE}
     *       end READ
     *       READ
     *       READ
     *       READ
     * WRITE}
     * WRITE}
     * WRITE}begin READ
     * WRITE}
     * WRITE}
     *       end READ
     *       READ
     *       READ
     *       READ
     *
     * Therefore we use these syncs:
     *
     * WRITE}
     * WRITE}
     * WRITE}begin READ
     * WRITE}
     * WRITE}
     * sync  sync
     *       end READ
     *       READ
     *       READ
     *       READ
     * sync  sync
     * WRITE}
     * WRITE}
     * WRITE}begin READ
     * WRITE}
     * WRITE}
     * sync  sync        
     *       READ
     *       READ
     *       READ
     *       READ
     * ...
     * WRITE}
     * WRITE}
     * WRITE}begin READ
     * WRITE}
     * WRITE}
     * sync  sync
     *       READ
     *       READ
     *       READ
     *       READ
     * sync  sync
     * DONE  DONE
     * 
     */
    
    for (int i = 0;i < 100;i++) {
      sendValues[i] = new Object();
                  
      if (i % 5 == 4) {
        writerSync[i] = new AltingBarrier[] {syncEvent[1] , syncEvent[1]};        
      } else {
        writerSync[i] = new AltingBarrier[0];
      }
    }
    
    for (int i = 0;i < 80;i++) {
      if (i % 4 == 0) {
        readerSync[i] = new AltingBarrier[] {syncEvent[0]};
      } else if (i % 4 == 3) {
        readerSync[i] = new AltingBarrier[] {null,syncEvent[0]};        
      } else {
        readerSync[i] = new AltingBarrier[0];
      }
      
      expValues[i] = sendValues[((i / 4) * 5) + (i % 4)];
    }
    
    ExtendedReaderSync reader;
    
    new Parallel( new CSProcess[] {        
        new WriterProcess(chan.out(),Arrays.asList(sendValues),writerSync),
        reader = new ExtendedReaderSync(readerSync,chan.in(),80)
    }).run();
            
    assertArraysEqual(expValues,reader.getValuesRead());
  }
  
  public void testOverwritingOldestOne2OneChannel() {
	//This buffer should over-write the oldest item
	  //If a write is made during an extended rendezvous that would over-write the
	  //most recent item, it should succeed but the item should not be removed by the
	  //reader at the end of the sync
	  
    //Imagine a buffer of size 3.  If the writer writes 0, and then the reader starts an
	  //extended rendezvous, if the writer then writes 1,2,3 and then the reader finishes
	  //the rendezvous, the buffer should still contain 1,2,3.
	  
	  
    //TODO work out what the expected behaviour is!
  }
  
}

  
