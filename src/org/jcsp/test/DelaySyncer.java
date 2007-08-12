package org.jcsp.test;

import org.jcsp.lang.AltingBarrier;
import org.jcsp.lang.CSProcess;
import org.jcsp.lang.CSTimer;

public class DelaySyncer implements CSProcess {

  AltingBarrier barrier;
  CSTimer timer;
  int milliSeconds;
  int iterations;
  
  public DelaySyncer(AltingBarrier barrier, int milliSeconds,int iterations) {
    this.barrier = barrier;
    timer = new CSTimer();
    this.milliSeconds = milliSeconds;
    this.iterations = iterations;     
  }
  
  public void run() {
    
    barrier.mark();
    
    for (int i = 0;i < iterations;i++) {
      timer.sleep(milliSeconds);
      barrier.sync();
    }

  }
  
  

}
