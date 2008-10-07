package org.jcsp.net2;

import org.jcsp.lang.ProcessInterruptedException;

/**
 * A package-visible class that implements a straightforward mutex, for use by Net2AnyChannel
 * 
 * @author Neil Brown
 */
class Mutex
{
    /**
     * Flag to mark the mutex as claimed
     */
    private boolean claimed = false;

    /**
     * Claims the mutex for exclusive access
     */
    void claim()
    {
        synchronized (this)
        {
            while (this.claimed)
            {
                try
                {
                    wait();
                }
                catch (InterruptedException e)
                {
                    throw new ProcessInterruptedException("*** Thrown from Mutex.claim()\n" + e.toString());
                }
            }
            this.claimed = true;
        }
    }

    /**
     * Releases the mutex for exclusive access
     */
    void release()
    {
        synchronized (this)
        {
            this.claimed = false;
            notify();
        }
    }
}
