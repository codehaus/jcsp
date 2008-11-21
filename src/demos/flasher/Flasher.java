import jcsp.awt.*;

public class Flasher extends ActiveApplet {

  public static final int minPeriod = 300;       // milliseconds
  public static final int maxPeriod = 1000;      // milliseconds
  public static final int defaultPeriod = 500;   // milliseconds

  public void init () {

    final int period =
      getAppletInt ("period", minPeriod, maxPeriod, defaultPeriod);

    setProcess (new FlasherNetwork (period, this));
    // setProcess (new FlasherNetworkSSD (period, this));

  }

}
