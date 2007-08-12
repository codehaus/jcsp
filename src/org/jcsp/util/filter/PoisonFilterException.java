package org.jcsp.util.filter;

import org.jcsp.lang.PoisonException;

public class PoisonFilterException extends PoisonException {
  public PoisonFilterException(String message) { 
    super(message);
  }
}
