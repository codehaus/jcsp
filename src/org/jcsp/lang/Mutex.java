package org.jcsp.lang;

/**
 * A package-visible class that implements a straightforward mutex, for use by 
 * One2AnyChannel and Any2AnyChannel
 * 
 * @author nccb
 *
 */
class Mutex {
  private boolean claimed = false;
  
  public void claim() {
    synchronized (this) {
      while (claimed) {
        try {
          wait();
        } catch (InterruptedException e) {
          throw new ProcessInterruptedException (
              "*** Thrown from Mutex.claim()\n" + e.toString ()
            );
        }        
      }
      claimed = true;
    } 
  }
  
  public void release() {
    synchronized (this) {
      claimed = false;
      notify();
    }
  }
}
