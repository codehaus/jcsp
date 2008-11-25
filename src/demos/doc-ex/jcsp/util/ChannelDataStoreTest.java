/*******************************************************************************
 *
 * $Archive: /doc-ex/jcsp/util/ints/ChannelDataStoreTest.java $
 *
 * $Date: 1998/07/21 14:17:11 $
 *
 * $Revision: 1.1 $
 *
 * (C) Copyright 1997/8 Paul Austin <pda1@ukc.ac.uk>
 * University of Kent Canterbury
 ******************************************************************************/

// package test.jcsp;

import java.text.*;
import java.util.*;

import org.jcsp.lang.*;
import org.jcsp.util.*;
import org.jcsp.plugNplay.Printer;

/**
 * <H2>Description</H2>
 * The ChannelDataStoreTest process is designed to test the ChannelDataStore
 * implementations provided by the jcsp library.
 *
 * @see org.jcsp.util.ZeroBuffer
 * @see org.jcsp.util.Buffer
 * @see org.jcsp.util.OverWritingBuffer
 * @see org.jcsp.util.OverWriteOldestBuffer
 * @see org.jcsp.util.InifiniteBuffer
 * @see org.jcsp.util.BlackHole
 * @see org.jcsp.util.CSTimer
 * @author P.D. Austin
 */

public class ChannelDataStoreTest implements CSProcess {

  /**
   * The main body of this process.
   */
  public void run() {
    final Any2OneChannel a2o = Channel.any2one();
    final One2OneChannel b = Channel.one2one (new Buffer(4));
    final One2OneChannel owb = Channel.one2one (new OverWritingBuffer(4));
    final One2OneChannel owob = Channel.one2one (new OverWriteOldestBuffer(4));
    final One2OneChannel oflb = Channel.one2one (new OverFlowingBuffer(4));
    final One2OneChannel ib = Channel.one2one (new InfiniteBuffer(1));
    final BlackHoleChannel bh = new BlackHoleChannel ();
    final CSTimer tim = new CSTimer ();
    final One2OneChannel prompt = Channel.one2one();
    final One2OneChannel next = Channel.one2one();
    
    new Parallel(new CSProcess[] {
      new CSProcess() {
        public void run() {
          testZeroBuffer();
          testBuffer();
          testOverWritingBuffer();
          testOverWriteOldestBuffer();
          testOverFlowingBuffer();
          testInfiniteBuffer();
          testBlackHole();
          testCSTimer();
        }
        
        void testZeroBuffer() {
          a2o.out ().write("Starting Test (ZeroBuffer)");
          for (int i = 0; i < 5; i++) {
            b.out ().write(new Integer (i));
          }
          a2o.out ().write("Finished outputing (ZeroBuffer)");
          prompt.out ().write(Boolean.TRUE);
        }
        
        void testBuffer() {
          next.in ().read();
          a2o.out ().write("Starting Test (Buffer)");
          for (int i = 0; i < 4; i++) {
            b.out ().write(new Integer (i));
          }
          prompt.out ().write(Boolean.TRUE);
          b.out ().write(new Integer (4));
          a2o.out ().write("Finished outputing (Buffer)");
          prompt.out ().write(Boolean.TRUE);
        }
        
        void testOverWritingBuffer() {
          next.in ().read();
          a2o.out ().write("Starting Test (OverWritingBuffer)");
          for (int i = 0; i < 6; i++) {
            owb.out ().write(new Integer (i));
          }
          a2o.out ().write("Finished outputing (OverWritingBuffer)");
          prompt.out ().write(Boolean.TRUE);
        }
        
        void testOverWriteOldestBuffer() {
          next.in ().read();
          a2o.out ().write("Starting Test (OverWriteOldestBuffer)");
          for (int i = 0; i < 10; i++) {
            owob.out ().write(new Integer (i));
          }
          a2o.out ().write("Finished outputing (OverWriteOldestBuffer)");
          prompt.out ().write(Boolean.TRUE);
        }
        
        void testOverFlowingBuffer() {
          next.in ().read();
          a2o.out ().write("Starting Test (OverFlowingBuffer)");
          for (int i = 0; i < 10; i++) {
            oflb.out ().write(new Integer (i));
          }
          a2o.out ().write("Finished outputing (OverFlowingBuffer)");
          prompt.out ().write(Boolean.TRUE);
        }
        
        void testInfiniteBuffer() {
          next.in ().read();
          a2o.out ().write("Starting Test (InfiniteBuffer)");
          for (int i = 0; i < 5; i++) {
            ib.out ().write(new Integer (i));
          }
          prompt.out ().write(Boolean.TRUE);
          for (int i = 0; i < 5; i++) {
            ib.out ().write(new Integer (i + 5));
          }
          a2o.out ().write("Finished outputing (InfiniteBuffer)");
          prompt.out ().write(Boolean.TRUE);
        }
        
        void testBlackHole() {
          next.in ().read();
          a2o.out ().write("Starting Test (BlackHole)");
          for (int i = 0; i < 5; i++) {
            bh.write(new Integer (i));
          }
          a2o.out ().write("Finished outputing (BlackHole)");
          prompt.out ().write(Boolean.TRUE);
        }
        
        void testCSTimer() {
          next.in ().read();
          a2o.out ().write("Starting Test (CSTimer)");
          a2o.out ().write("Finished outputing (CSTimer)");
          prompt.out ().write(Boolean.TRUE);
        }
      },
      
      new CSProcess() {
        boolean finished;
        
        public void run() {
          testZeroBuffer();
          testBuffer();
          testOverWritingBuffer();
          testOverWriteOldestBuffer();
          testOverFlowingBuffer();
          testInfiniteBuffer();
          testBlackHole();
          testCSTimer();
        }
        
        void testZeroBuffer() {
          finished = false;
          while (!finished) {
            Integer i = (Integer) b.in ().read();
            a2o.out ().write(i);
            finished = (i.intValue() == 4);
          }
          prompt.in ().read();
          a2o.out ().write("Finished Test (ZeroBuffer)");
        }
        
        void testBuffer() {
          next.out ().write(Boolean.TRUE);
          finished = false;
          prompt.in ().read();
          while (!finished) {
            Integer i = (Integer) b.in ().read();
            a2o.out ().write(i);
            finished = (i.intValue() == 4);
          }
          prompt.in ().read();
          a2o.out ().write("Finished Test (Buffer)");
        }
        
        void testOverWritingBuffer() {
          next.out ().write(Boolean.TRUE);
          finished = false;
          prompt.in ().read();
          while (!finished) {
            Integer i = (Integer) owb.in ().read();
            a2o.out ().write(i);
            finished = (i.intValue() == 5);
          }
          a2o.out ().write("Finished Test (OverWritingBuffer)");
        }
        
        void testOverWriteOldestBuffer() {
          next.out ().write(Boolean.TRUE);
          finished = false;
          prompt.in ().read();
          while (!finished) {
            Integer i = (Integer) owob.in ().read();
            a2o.out ().write(i);
            finished = (i.intValue() == 9);
          }
          a2o.out ().write("Finished Test (OverWriteOldestBuffer)");
        }
        
        void testOverFlowingBuffer() {
          next.out ().write(Boolean.TRUE);
          prompt.in ().read();
          while (oflb.in ().pending ()) {
            Integer i = (Integer) oflb.in ().read();
            a2o.out ().write(i);
          }
          a2o.out ().write("Finished Test (OverFlowingBuffer)");
        }
        
        void testInfiniteBuffer() {
          next.out ().write(Boolean.TRUE);
          finished = false;
          prompt.in ().read();
          while (!finished) {
            Integer i = (Integer) ib.in ().read();
            a2o.out ().write(i);
            if (i.intValue() == 4) {
              prompt.in ().read();
            }
            finished = (i.intValue() == 9);
          }
          a2o.out ().write("Finished Test (InfiniteBuffer)");
        }
        
        void testBlackHole() {
          next.out ().write(Boolean.TRUE);
          prompt.in ().read();
          a2o.out ().write("Finished Test (BlackHole)");
        }
        
        void testCSTimer() {
          next.out ().write(Boolean.TRUE);
          prompt.in ().read();
          for (int i = 0; i < 5; i++) {
            long time = tim.read();
            a2o.out ().write(DateFormat.getInstance().format(new Date(time)) + " " + time % 1000);
          }
          a2o.out ().write("Finished Test (CSTimer)");

          System.exit(0);
        }
      },
      new Printer(a2o.in (), "", "\n")
    }).run();
  }
  
  /**
   * Executes the process.
   *
   * @param argv The parameters to the application.
   */
  public static void main(String[] argv) {
    new ChannelDataStoreTest().run();
  }
}
