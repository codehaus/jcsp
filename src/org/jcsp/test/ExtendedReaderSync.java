package org.jcsp.test;

import java.security.InvalidParameterException;
import java.util.Arrays;

import org.jcsp.lang.AltingBarrier;
import org.jcsp.lang.CSProcess;
import org.jcsp.lang.ChannelInput;

/**
 * A process that performs a set number of extended inputs, syncing on a barrier
 * as its extended action for each
 * 
 * @author N.C.C. Brown
 *
 */
public class ExtendedReaderSync implements CSProcess {

  private AltingBarrier[][] events;
  
  private ChannelInput input;
  
  private int iterations;
  
  private Object[] valuesRead;
  
  public ExtendedReaderSync(AltingBarrier[][] barriers, ChannelInput in, int iterations) {
    if (barriers.length != iterations) {
      throw new InvalidParameterException("Barriers must be the same length as iterations");
    }
    
    this.events = barriers;
    this.input = in;
    this.iterations = iterations;
    valuesRead = new Object[iterations];
  }
  
  public ExtendedReaderSync(AltingBarrier barrier, ChannelInput in, int iterations) {
    this.events = new AltingBarrier[iterations][];
    Arrays.fill(this.events,new AltingBarrier[] {barrier});
    
    this.input = in;
    this.iterations = iterations;
    valuesRead = new Object[iterations];
  }
  
  public void run() {

    for (int i = 0;i < events.length;i++) {
      AltingBarrier[] barriers = events[i];
      for (int j = 0;j < barriers.length;j++) {
        AltingBarrier barrier = barriers[j];
        if (barrier != null) {
          barrier.mark();
        }
      }
    }
    
    for (int i = 0;i < iterations;i++) {
      valuesRead[i] = input.startRead();
      
      AltingBarrier[] barriers = events[i];
      if (barriers.length > 0) {
        AltingBarrier barrier = barriers[0];
        if (barrier != null) {
          barrier.sync();
        }
      }      
      
      input.endRead();
      
      if (barriers.length > 1) {
        AltingBarrier barrier = barriers[1];
        if (barrier != null) {
          barrier.sync();
        }
      }
    }

  }

  public Object[] getValuesRead() {
    return valuesRead;
  }

  
}
