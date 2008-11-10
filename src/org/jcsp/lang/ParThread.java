    //////////////////////////////////////////////////////////////////////
    //                                                                  //
    //  JCSP ("CSP for Java") Libraries                                 //
    //  Copyright (C) 1996-2008 Peter Welch and Paul Austin.            //
    //                2001-2004 Quickstone Technologies Limited.        //
    //                                                                  //
    //  This library is free software; you can redistribute it and/or   //
    //  modify it under the terms of the GNU Lesser General Public      //
    //  License as published by the Free Software Foundation; either    //
    //  version 2.1 of the License, or (at your option) any later       //
    //  version.                                                        //
    //                                                                  //
    //  This library is distributed in the hope that it will be         //
    //  useful, but WITHOUT ANY WARRANTY; without even the implied      //
    //  warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR         //
    //  PURPOSE. See the GNU Lesser General Public License for more     //
    //  details.                                                        //
    //                                                                  //
    //  You should have received a copy of the GNU Lesser General       //
    //  Public License along with this library; if not, write to the    //
    //  Free Software Foundation, Inc., 59 Temple Place, Suite 330,     //
    //  Boston, MA 02111-1307, USA.                                     //
    //                                                                  //
    //  Author contact: P.H.Welch@kent.ac.uk                             //
    //                                                                  //
    //                                                                  //
    //////////////////////////////////////////////////////////////////////

package org.jcsp.lang;

/**
 * This is the <TT>Thread</TT> class used by {@link Parallel} to run all but
 * one of its given processes.
 *
 * <H2>Description</H2>
 * A <TT>ParThread</TT> is a <TT>Thread</TT> used by {@link Parallel} to run
 * all but one of its given processes.
 * <P>
 * The <TT>CSProcess</TT> to be executed can be changed using the
 * <TT>setProcess</TT> method providing the <TT>ParThread</TT> is not active.
 *
 * @see org.jcsp.lang.CSProcess
 * @see org.jcsp.lang.ProcessManager
 * @see org.jcsp.lang.Parallel
 *
 * @author P.D. Austin
 * @author P.H. Welch
 */
//}}}

class ParThread extends Thread
{
    /** the process to be executed */
    private CSProcess process;

    /** the barrier at the end of a PAR */
    private Barrier barrier;

    private boolean running = true;

    /** parking barrier for this thread */
    private Barrier park = new Barrier(2);

    /**
     * Construct a new ParThread.
     *
     * @param process the process to be executed
     * @param barrier the barrier for then end of the PAR
     */
    public ParThread(CSProcess process, Barrier barrier)
    {
        setDaemon(true);
        this.process = process;
        this.barrier = barrier;
        setName(process.toString());
    }

    /**
     * reset the ParThread.
     *
     * @param process the process to be executed
     * @param barrier the barrier for then end of the PAR
     */
    public void reset(CSProcess process, Barrier barrier)
    {
        this.process = process;
        this.barrier = barrier;
        setName(process.toString());
    }

    /**
     * Sets the ParThread to terminate next time it's unparked.
     *
     */
    public void terminate()
    {
        running = false;
        park.sync();
    }

    /**
     * Releases the ParThread to do some more work.
     */
    public void release()
    {
        park.sync();
    }

    /**
     * The main body of this process.
     * above.
     */
    public void run()
    {
        try
        {
            Parallel.addToAllParThreads(this);
            while (running)
            {
                try
                {
                    process.run();
                }
                catch (Throwable e)
                {
                    Parallel.uncaughtException("org.jcsp.lang.Parallel", e);
                }
                barrier.resign();
                park.sync();
            }
        }
        catch (Throwable t)
        {
            Parallel.uncaughtException("org.jcsp.lang.Parallel", t);
        }
        finally
        {
            Parallel.removeFromAllParThreads(this);
        }
    }
}
