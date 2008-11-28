
=============================================================================
              CSP for Java : JCSP 1.1 Release Candidate 1 (rc4)
=============================================================================


This file contains:

  o the manifest list of files/directories in this release;
  o installation notes;
  o incremental change list since JCSP 0.5.

Please read the GNU-LGPL-2.1.txt file.  Retention of these files is
assumed to imply acceptance of the terms in that license.

This release is licenced under the Lesser GNU Public Licence, version 2.1
(LGPL 2.1).  A copy is included with the distribution, or you can read an online
version at:

  http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html

There are rather a lot of deprecated items now in the library.  Most of
these will be removed in JCSP 1.2.  This vew version will contain
a completely re-written networking support (org.jcsp.lang.channel).

Peter Welch and Neil Brown.
(28th. November, 2008)


=============================================================================
                            JCSP 1.1 (rc4) Manifest
=============================================================================


  jcsp.jar                  (class library containing all features, both net
                             and core - add either this, or jcsp-core.jar,
                             to your CLASSPATH)

  jcsp-core.jar             (class library containing just the core features
                             - add either this or jcsp.jar to your CLASSPATH)

Note: jcsp-core excludes the networking mechanisms (and some other things)
contained within jcsp.jar.  It is a smaller library for applications running
in a single JVM.  It efficiently exploits multicore processors.

  jcsp-demos/               (directory with sources for lots of demos)
  jcsp-demos-util.jar       (addtional class library - needed for the demos)  

  jcsp-doc/                 (javadoc HTML tree for jcsp.jar)
  jcsp-core-doc/            (javadoc HTML tree for jcsp-core.jar)

Note: the jcsp-core-doc folder is included so that jcsp-core users are not
overloaded with materials they do not use.  For either of the above trees,
point your browser at the index.html file at their top level.

  GNU-LGPL-2.1              (open source licence agreement)
  README-JCSP-1.1-rc4.txt   (this file)
  
L-GPL open sources are available from the subversion repository:

  svn checkout http://projects.cs.kent.ac.uk/projects/jcsp/svn/jcsp/tags/jcsp-1.1-rc4/

You will need the JDK1.3 libraries (or any later JDK) installed to work
with the JCSP library.


=============================================================================
                              Installation Notes
=============================================================================


Put the file jcsp.jar (or jcsp-core.jar) in your CLASSPATH.
You do *not* need to unpack these files.

Point your web browser at (and, maybe, bookmark):

  jcsp-doc/index.html    or    jcsp-core-doc/index.html

for the documentation to the JCSP library.

The jcsp-demos directory contains many JCSP demonstration applications
and applets.  These are not embedded in any Java packages.  Each demo
is contained in a single sub-directory.  To run one, change to that
directory, compile (javac *java) and run the main program (which
usually has the word "Main" as part of its name).


=============================================================================
                                 Change List
=============================================================================


Changes since JCSP 1.1 (rc3) release
------------------------------------

At last, the documentation has been changed to conform to the API changes
introduced in version 1.1.  Many minor bugs fixed.  Some new demos added.

The documentation of org.jcsp.lang.Channel (from which all channels should
be made) and org.jcsp.lang.Poisonable (for simplifying network shut-down)
has been finished.

There are rather a lot of deprecated items now in the library.  Most of
these will be removed in JCSP 1.2.  This vew version will contain
a completely re-written networking support (org.jcsp.lang.channel).

Peter Welch and Neil Brown.
(28th. November, 2008)


Changes since JCSP 1.1 (rc2) release
------------------------------------

Deprecated the remaining old methods of channel creation and fixed some
public visibility mistakes.

Neil Brown and Peter Welch.
(3rd. March, 2008).


Changes since JCSP 1.1 (rc1) release
------------------------------------

Deprecated some of the channel factories, and updated the overview of the
documentation and this README file.

Neil Brown and Peter Welch.
(9th. October, 2007).


Changes since JCSP 1.0 (rc8) release
------------------------------------

The library has been merged with the old Quickstone version, which includes
a great many changes.  The channels are now interfaces with hidden 
implementations, and must be created using methods in the Channel class.
The networking aspects from Quickstone's JCSP.NET have been included.
Poisonable channels are now included.  Many Quickstone classes have been 
removed (from JCSP.NET; JCSP core users will not notice any difference) or
deprecated ready for future removal.  Symmetric Object channels have been
added.

Neil Brown and Peter Welch.
(5th. October, 2007).


Changes since JCSP 1.0 (rc7) release
------------------------------------

Symmetric channels (One2OneChannelSymmetricInt) added, with demo added to
CommsTime (to run with symmetric channels) and a new output-guards demo
folder.

Peter Welch.
(16th. August, 2007)


Changes since JCSP 1.0 (rc6) release
------------------------------------

One correction to AltingBarrier.  Lots more documentation and examples for
AltingBarrier.  Minor documentation improvements elsewhere.

Some Framed* classes added to jcsp.plugNplay.  These wrap some jcsp.awt
Active* widgets (buttoms and scrollbars) in frames for immediate use.
They are useful for quick demonstrations (and used in the documentation
of AltingBarrier).

The Parallel class has a new contructor taking a 2D-array of processes.

Spurious wakeup protection/logging has been added to the Bucket and Stop
classes.  These has been overlooked when preparing the rc6 version.

Peter Welch.
(19th. December, 2006)


Changes since JCSP 1.0 (rc5) release
------------------------------------

The introduction of an (experimental) AltingBarrier class.  This is a Guard
useable for Alternative selections.  It implements multiway synchronisation
(i.e. a CSP 'event'), allowing all parties to back off choice operations.
One party to the AltingBarrier will select it if and only if *all* parties
select it.  Prioritised choice is not allowed if the choice includes an
AltingBarrier, since conflicting priorities would be easy to set up and
impossible to resolve.  Fair choice is allowed, but only on the understanding
that a completed AltingBarrier takes precedence over other typse of Guard
(because of the necessity for all parties to the choice making the same one).

The Alternative class has had to be modified to support AltingBarriers.  This
should have negligeable overhead on its operation without AltingBarriers.

An AlternativeError class has been added, which is thrown in the attempt to
make a prioritised choice over a set of Guards including an AltingBarrier.

A JCSP_InternalError class has been added.  This is thrown if an internal
inconsistency is detected.  Currently, this is used for defensive programming
only of the AltingBarrier (which, we stress, is experimental).

Spurious wakeup protection has been added to all blocking methods (channel
communications, barrier synchronisation, alting, etc.).  These are implemented
with invocations of "Object.wait()".  The Java specification has always
included a footnote that those calls may "spuriously" return for no proper
reason (where "proper" means being released through an "Object.notify()",
"Object.notifyAll()", an "InterruptedException" or a timeout)!  There was
no mention of this danger in the (javadoc) documentation in JDK 1.3 and
earlier versions.  The danger is heavily emphasised in the documentation
for JDK 1.5, so we thought we should take it seriously.

Spurious wakeup protection requires a few extra fields in some of the classes
and extra work (and ugly code) at run-time -- but these overheads are pretty
slight.  Optional logging of detected spurious wakeups is provided (see the
"SpuriousLog").  Running stress tests on Sun's JVMs, with logging switched
on, has *never* shown any!

It has long been known that "early" timeouts (from "Object.wait(long)") occur
and JCSP has always allowed them.  The above logging mechanism also reports
counts of these -- and they occur frequently.  The acceptable margin for
"early" timeouts may be set by "SpuriousLog.setEarlyTimeout(long)"; by default,
that margin is set to 9 milliseconds (which covers most of those encountered
using Sun's JVMs).  Timeouts returning earlier than the acceptable margin are
treated as spurious wakeups and the timeout is reset.  A margin of zero may be
defined, but that would lead to many re-timeouts that are not really needed
-- note that JCSP running on standard JVMs should not be used for critical
("hard") real-time applications.

Peter Welch.
(6th. January, 2006) and (19th. December, 2006)


Changes since JCSP 1.0 (rc4) release
------------------------------------

First, many apologies for the long interval sine the last release.  We are very
positive about this work, but have been concentrating our time on developing
the occam-pi language and supporting infrastructure.  occam-pi and JCSP are
closely related - common goals, differing technologies.  Anyone interested in
finding more about occam-pi, please check out:

  http://www.cs.kent.ac.uk/projects/ofa/kroc/    (motivation and download)
  http://frmb.org/occ21-extensions.html          (summary of extensions over
                                                  occam2.1)
  http://rmox.net/prelude/                       (experimental OS)

Back to JCSP!  This release is mostly just maintenance over rc4, with several
sets of classes re-implemented for better efficiency and security (i.e. no
semantic change).

Some changes have been made though:

  o several more items have been deprecated:

      interfaces: jcsp.lang.Channel, jcsp.lang.ChannelInt.
      
        Note: these are just the union of their separate input/output classes:
        
          jcsp.lang.Channel = jcsp.lang.ChannelInput union
                              jcsp.lang.ChannelOutput

          jcsp.lang.ChannelInt = jcsp.lang.ChannelInputInt union
                                 jcsp.lang.ChannelOutputInt

        They are never needed!  Such interfaces are used for the parameters
        of CSProcess class constructors and should only hold one end (either
        input ot output, but not both!) of the channel plugged in to any given
        instance.  Any other use is misconceived.

        We are afraid that some pieces of JCSP documentation incorrectly used
        these interfaces.  This have been corrected in this release.

      abstract classes: jcsp.lang.AltingChannel, jcsp.lang.AltingChannelInt.

        Note: these are deprecated for exactly the same reasons as above.  Use
        AltingChannelInput, respectively AltingChannelInputInt, instead for input
        channels that need to support ALTing - or ChannelOutput, respectively
        ChannelOutputInt, for output channels.

General note about the above deprecated items: THEY WILL BE REMOVED from the
final 1.0 release!  That will be a merger with the full JCSP Network Edition i
from Quickstone (http://www.quickstone.com/xcsp/jcspnetworkedition/).  This has 
a cleaner (more systematic) way for creating the wide variety of channels
supported and we want the name "Channel" as the right name for the class of
static `factory' methods.

Other changes: 

  o a bunch of deprecated items have now been removed:

      methods: jcsp.lang.Alternative.select (AltingChannelInput[], ...) etc..

        Note: Alternative classes *must* now be constructed bound to their Guard
        arrays - so there is no need to suppy those to their select methods.

      constructors: jcsp.lang.Alternative ().
      
        Note: use jcsp.lang.Alternative (Guard[] guard) instead - see above.

      constructors: jcsp.lang.Any2AnyChannel(ChannelDataStore) etc..

        Note: all deprecated channel constructors taking ChannelDataStore or
        ChannelDataStoreInt have been removed.  Use the static create methods
        instead.  This removal was needed for technical reasons for improving
        the efiiciency of default channels (i.e. those without ChannelDataStore
        buffers).  As mentioned in the "General note" above, all channel
        creation in future releases will be via the Channel static create
        methods.

  o a bug fix:

      class: jcsp.lang.Stop.

        Note: this simple class was included for completeness - it is one of
        the primitives of CSP.  It never occurred to us that anyone would need
        to use it and it was never tested!  But someone did use it.  It carried
        a very stupid bug - now exterminated.

Peter Welch
(5th. September, 2005)


Changes since JCSP 1.0 (rc3) release
------------------------------------

  o Minor corrections and changes to a (very) few of the documentation files.

  o A new demo - MultiPong :-)

  o A modified demo - Infection.  This has more controls and interaction.  See
    how hard it is fighting (say) Foot-and-Mouth by culling neighbouring animals
    when the effectiveness of the cull is only 99% ...

Peter Welch.
(13th. July, 2001)


Changes since JCSP 1.0 (rc2) release
------------------------------------

  o Fixed bug in jcsp.util.Buffer and jcsp.util.ints.BufferInt.  When we tried
    to create a buffered channel of size n, we used to get one of size (n+1).
    The channel buffer is now created correctly with the requested size.

  o Fixed bug in jcsp.awt.GraphicsCommand.DrawArc and jcsp.awt.GraphicsCommand.FillArc.
    A typing error had omitted startAngle and arcAngle from the constructor's
    parameter list.  It compiled OK under JDK1.2 (but couldn't work correctly).
    The JDK1.3 compiler was more thorough and, quite properly, rejected it.
    All is well now.

  o Several additions to the documentation.  In particular, the top-level javadoc
    page points new users to the documentation in the jcsp.lang.CSProcess interface
    - where there is an overview of (Communicating Sequential) Process Oriented Design
    and the implementation pattern for a JCSP process.

  o Modified Binary Code License (that explicitly allows redistribution of JCSP
    classes as part of users' applications/applets).

Peter Welch.
(7th. February, 2001)


Changes since JCSP 1.0 (rc1) release
------------------------------------

  o Change of name of the "Timer" class in jcsp.lang to "CSTimer".

    This has been caused by the JDK1.3 release from Sun introducing their own
    classes called "Timer" in packages java.util and javax.swing.  Without this
    name change, applications importing both jcsp.lang.* and java.util.* (or
    javax.swing*) would have to qualify declarations and constructions of
    "Timer" variables and objects - e.g:

      jcsp.lang.Timer tim = new jcsp.lang.Timer ();

    With this name change, even if both jcsp.lang.* and java.util.* are imported,
    we may declare JCSP timers without qualification - e.g:

      CSTimer tim = new CSTimer tim ();

    Note that the JDK1.3 java.util.Timer is a very different class from CSTimer.

    Warning: the old jcsp.lang.Timer has been deleted from this release - not
             deprecated.  If it had just been deprecated, importing java.util.*
             would still require full qualification for references to its Timers.
             This means existing JCSP applications using JCSP timers will have
             to change all occurrences of "Timer" to "CSTimer" - many apologies.

    Aside: avoidance of a name clash was the reason why JCSP has the "CSProcess"
    interface, rather than just "Process".  There has always been a "Process"
    class defined in java.lang (again with a very different purpose from ours).

  o Some improvements to various items in the documentation.

  o Re-packaging of the release as a single JAR file.

  o A new applet/application demo - Fireworks!

Peter Welch.
(11th. August, 2000) 


Changes since JCSP 0.95 release
-------------------------------

  o Change of name of the "Many2..." and "...2Many" channel classes.  These
    have been changed to "Any2..." and "...2Any" channels.  The "Any" names
    better reflect the semantics of these channels.  ["Many" implied some kind
    of broadcast - which is not what these channels do.]

    This has been an outstanding request for some time.  The old class names
    have not simply been deprecated - they have been deleted!  The jcsp.lang
    section of the library would have become embarassingly top heavy with
    deprecated classes if they had been retained.  Apologies to those who
    may need to make a lot of edits.  I have a simple Unix cshell script that
    will make these edits to all .java files in, or below, a named directory.
    Please mail me (P.H.Welch@ukc.ac.uk) if you need it!

  o The bucket synchronisation primitive;

  o Generation of a jcsp.lang.ProcessInterruptedError if any process, blocked
    on a JCSP synchonisation primitive, is interrupted.  This is not allowed
    (and is always a panic-induced design error).  The error does not need to
    be caught (and, indeed, should *not* be caught) with a try-catch clutter
    surrounding the JCSP synchonisation.

  o Minor tweaks to the documentation (as ever).

Peter Welch.
(3rd. November, 1999)
    

Changes since JCSP 0.94 release
-------------------------------

  o Links to Sun's JDK1.2 documentation (from the JCSP documentation)
    are now in place;

  o Minor tightening up of the ChannelAccept interface (for CALL channels)
    to enable the compiler to trap erroneous accepts - no effect on correct
    programs;

  o Minor tweaks to the documentation.

Peter Welch.
(28th. October, 1999)


Changes since JCSP 0.5 release
------------------------------

  o A more flexible API for ALTing;

  o Numerous new facilities (and some deprecated old ones);

  o More and better (javadoc)umentation - including several mini-tutorials.
    JCSP documentation is now produced using the JDK 1.2 javadoc tool;

  o More demonstration applications and applets;

  o (occam3) CALL channels;

  o Barrier and CREW synchronisation primitives;

  o Various pieces of maintenance.

A source release will be made available shortly under either the GPL
or Artistic open source licences.  Please contact me (P.H.Welch@ukc.ac.uk).

I *hope* to freeze this into a JCSP 1.0 release *shortly* and update
the JCSP web pages suitably - at present, these offer only JCSP 0.5.

Peter Welch.
(28th. October, 1999)



