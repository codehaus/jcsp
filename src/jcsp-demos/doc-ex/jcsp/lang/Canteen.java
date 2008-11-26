import org.jcsp.lang.*;

public class Canteen implements CSProcess {

  private final AltingChannelInput supply;    // from the cook
  private final AltingChannelInput request;   // from a philosopher
  private final ChannelOutput deliver;        // to a philosopher

  public Canteen (final AltingChannelInput supply,
                  final AltingChannelInput request, final ChannelOutput deliver) {
    this.supply = supply;
    this.request = request;
    this.deliver = deliver;
  }

  public void run() {

    final Guard[] guard = {supply, request};
    final boolean[] preCondition = new boolean[guard.length];
    final int SUPPLY = 0;
    final int REQUEST = 1;

    final Alternative alt = new Alternative (guard);

    final int maxChickens = 20;
    final int maxSupply = 4;
    final int limitChickens = maxChickens - maxSupply;

    final Integer oneChicken = new Integer (1);                 // ready to go!

    int nChickens = 0;             // invariant : 0 <= nChickens <= maxChickens

    while (true) {
      preCondition[SUPPLY] = (nChickens <= limitChickens);
      preCondition[REQUEST] = (nChickens > 0);
      switch (alt.priSelect (preCondition)) {
        case SUPPLY:
          nChickens += ((Integer) supply.read ()).intValue ();  // <= maxSupply
        break;
        case REQUEST:
          Object dummy = request.read ();  // we have to still input the signal
          deliver.write (oneChicken);      // preCondition ==> (nChickens > 0)
          nChickens--;
        break;
      }
    }

  }

}
