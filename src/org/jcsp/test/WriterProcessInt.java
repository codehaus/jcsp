package org.jcsp.test;

import java.security.InvalidParameterException;
import java.util.Arrays;

import org.jcsp.lang.AltingBarrier;
import org.jcsp.lang.CSProcess;
import org.jcsp.lang.ChannelOutputInt;

/**
 * A process that writes out a list of values, synchronizing on the corresponding barrier after each. 
 * 
 * 
 * @author N.C.C. Brown
 *
 */
public class WriterProcessInt implements CSProcess {

  private ChannelOutputInt out;
  
  private int[] values;
  
  private AltingBarrier[][] events;
  
  public WriterProcessInt(ChannelOutputInt out,int[] values,AltingBarrier[][] events) {
    if (values.length != events.length) {
      throw new InvalidParameterException("Values must be the same length as Events");
    }
    
    this.out = out;
    this.values = values;
    this.events = events;
  }
  
  public WriterProcessInt(ChannelOutputInt out,int[] values,AltingBarrier event) {        
    this.out = out;
    this.values = values;
    this.events = new AltingBarrier[values.length][];
    Arrays.fill(this.events,new AltingBarrier[] {event});
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
    
    for (int i = 0;i < values.length;i++) {
      
      out.write(values[i]);
      
      AltingBarrier[] barriers = events[i];
      for (int j = 0;j < barriers.length;j++) {
        AltingBarrier barrier = barriers[j];
        if (barrier != null) {
          barrier.sync();
        }
      }
    }

  }

}
