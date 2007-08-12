package org.jcsp.test;

import java.util.LinkedList;
import java.util.List;

import org.jcsp.lang.Alternative;
import org.jcsp.lang.CSProcess;
import org.jcsp.lang.Guard;

/**
 * A class that listens out for many guards, and records the order in which they occur
 * 
 *  Note: do not pass in channel guards, as the process will not perform the necessary
 *  input after the guard is selected
 * 
 * @author nccb
 *
 */
class EventRecorder implements CSProcess {
  private Guard originalGuards[];
  
  private int stopOnGuard;
  
  private List observedGuards = new LinkedList();
  
  public EventRecorder(Guard[] guards, int terminateEvent) {
    originalGuards = guards;
    stopOnGuard = terminateEvent;
  }
  
  public Guard[] getObservedEvents() {
    return (Guard[])observedGuards.toArray(new Guard[observedGuards.size()]);
  }
  
  public void run() {
    Alternative alt = new Alternative(originalGuards);
    int selected;
    
    do {
      selected = alt.select();
      
      observedGuards.add(originalGuards[selected]);      
      
    } while (selected != stopOnGuard);
    
  }
   
  
}
