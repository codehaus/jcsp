/*******************************************************************************
 *
 * $Archive: /jcsp/src/test/jcsp/PriorityTest.java $
 *
 * $Date: 1998/07/21 14:17:14 $
 *
 * $Revision: 1.1 $
 *
 * (C) Copyright 1997/8 Paul Austin <pda1@ukc.ac.uk>
 * University of Kent Canterbury
 ******************************************************************************/

import org.jcsp.lang.*;

/**
 */
public class PriorityTest implements CSProcess {

  public void run() {
    noProcesses();
    //staticProcesses();
    //dynamicPrccesses();
    staticPriority();
    //dynamicPriority();
  }
  
  void noProcesses() {
    System.out.println("Sarting Test: noProcesses()");
    new Parallel().run();
    System.out.println("Finished Test: noProcesses()");
  }
  
  public class StaticProcesses implements CSProcess {
    private int index;
    StaticProcesses(int index) {
      this.index = index;
    }
    
    public void run() {
      int priority = Thread.currentThread().getPriority();
      System.out.println("Process: " + index);
      long count = 1;
      for (int i = 0; i < index; i++) {
        count *= priority;
      }
      System.out.println("Process: " + index + ", count = " + count);
    }
  }
    
  void staticProcesses() {
    System.out.println("Sarting Test: staticProcesses()");
    new Parallel().run();
    System.out.println("Finished Test: staticProcesses()");
  }
  
  void dynamicPrccesses() {
    System.out.println("Sarting Test: dynamicPrccesses()");
    new Parallel().run();
    System.out.println("Finished Test: dynamicPrccesses()");
  }
  
  public class StaticPriority implements CSProcess {
    private int index;
    StaticPriority(int index) {
      this.index = index;
    }
    
    public void run() {
      int priority = Thread.currentThread().getPriority();
      System.out.println("Process: " + index + ", priority=" + priority);
      long count = 1;
      for (int i = 0; i < index; i++) {
        count *= priority;
      }
      System.out.println("Process: " + index + ", count=" + count);
    }
  }
    
  /**
   * Note: All this test shows is that each of the processes priority is set
   * to the correct value. The last process will have the priority equal to
   * Thread.NORM_PRIORITY. 
   */
  void staticPriority() {
    System.out.println("Sarting Test: staticPriority()");
    CSProcess[][] procs = new CSProcess[4][];
    
    procs[0] = new CSProcess[1];
    procs[1] = new CSProcess[Thread.MAX_PRIORITY - Thread.currentThread().getPriority() + 1];
    procs[2] = new CSProcess[Thread.MAX_PRIORITY - Thread.currentThread().getPriority()];
    procs[3] = new CSProcess[Thread.MAX_PRIORITY - Thread.currentThread().getPriority() + 2];
    
    for (int i = 0; i < procs.length; i++) {
      for (int j = 0; j < procs[i].length; j++) {
        procs[i][j] = new StaticPriority(procs[i].length - j);
      }
      System.out.println("  Start Data: " + i);
      new PriParallel(procs[i]).run();
      System.out.println("  End Data: " + i);
    }
    System.out.println("Finished Test: staticPriority()");
  }
  
  void dynamicPriority() {
    System.out.println("Sarting Test: dynamicPriority()");
    new Parallel().run();
    System.out.println("Finished Test: dynamicPriority()");
  }
  
  public static void main(String[] argv) {
    new PriorityTest().run();
  }
}
