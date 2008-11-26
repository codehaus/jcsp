
import org.jcsp.lang.*;

import java.awt.Color;

/**
 * This contains the variables shared by each (synchronised) playing group.
 * Safe access is assured by <i>phased barrier synchronisation</i> honoured
 * by the group, ensuring <i>Concurrent-Read-Exclusive-Write</i> (CREW)
 * behaviour for each phase.  In fact, a <i>relaxed</i>, but still safe,
 * extension to CREW is honoured: concurrent <i>writes of the same value</i>
 * are allowed to one of the fields (the game termination flag, {@link ok})
 * during the update phases.
 * <p>
 * {@link #toLeader} gives a channel back to the leader, used by the other
 * group members to cancel the leader's timeout should they want to end
 * the game -- it is set just the once by the constructer.  It must be an
 * <i>any-1</i> channel and <i>overwriting buffered</i>.  It is used only
 * during an update phase.
 * </p>
 * <p>
 * The other field values may be used during read phases by all players.
 * {@link #colour} and {@link #brighter} are defined by the leader at
 * start of each game.  {@link label} is changed by the leader during
 * update phases.  {@link ok} is originally set <code>true</code> by
 * the leader, but may be set <code>false</code> by <i>any</i> group member
 * during any update phase.
 * </p>
 */
 public class Shared {

  /** 
   * This is set <code>true</code> by the group leader
   * at the start of a new game.  It may be safely set
   * <code>false</code> by <i>any</i> member of the group
   * (so long as this is during a parcel update phase).
   */
  public boolean ok = false;

  /**
   * This is set (randomly) by the group leader at the start
   * of a new game.
   */
  public Color colour = null;

  /**
   * This is set by the group leader at the start
   * of a new game.
   */
  public Color brighter = null;

  /**
   * This is set by the group leader at the start of a new game
   * and updated during the game (in parcel update phases).
   */
  public String label = null;

  /**
   * This is set once by the (group leader) gadget
   * when this parcel is constructed.
   */
  public final ChannelOutput toLeader;

  /**
   * This parcel is constructed by the (group leader) gadget.
   */
  public Shared (ChannelOutput toLeader) {
    this.toLeader = toLeader;
  }

}
