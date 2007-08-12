package org.jcsp.test;

import org.jcsp.lang.AltingBarrier;
import org.jcsp.lang.CSProcess;

/**
 * A process that syncs on one alting barrier and finishes
 * 
 * 
 * @author N.C.C. Brown
 *
 */
public class BarrierSyncer implements CSProcess {

  private AltingBarrier barrier;    
  
  public BarrierSyncer(AltingBarrier barrier) {
    super();
    this.barrier = barrier;
  }



  public void run() {
    
    barrier.mark();
    
    barrier.sync();
    
  }

}
