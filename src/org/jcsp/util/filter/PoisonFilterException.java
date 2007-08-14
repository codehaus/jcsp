package org.jcsp.util.filter;

import org.jcsp.lang.PoisonException;

/**
 * 
 * @deprecated Use poison directly instead
 */
public class PoisonFilterException extends PoisonException {
  public PoisonFilterException(String message) {
	  //In lieu of knowing a specific poison strength,
	  //we supply the maximum:
	  super(Integer.MAX_VALUE);
  }
}
