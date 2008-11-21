import jcsp.lang.*;

public class PongScorer implements CSProcess {

  private final AltingChannelInputInt[] fromPaddle;
  private final ChannelOutput[] configureLabel;

  public PongScorer (final AltingChannelInputInt[] fromPaddle,
                     final ChannelOutput[] configureLabel) {
    this.fromPaddle = fromPaddle;
    this.configureLabel = configureLabel;
  }

  public void run () {

    final Alternative alt = new Alternative (fromPaddle);
  
    int left = 0;
    int right = 0;

    configureLabel[0].write ("0");
    configureLabel[1].write ("0");

    while (true) {
      switch (alt.fairSelect ()) {
        case 0:
          final int scoreLeft = fromPaddle[0].read ();
          left = (scoreLeft == 0) ? 0 : left + scoreLeft;
          configureLabel[0].write ((new Integer (left)).toString ());
        break;    
        case 1:
          final int scoreRight = fromPaddle[1].read ();
          right = (scoreRight == 0) ? 0 : right + scoreRight;
          configureLabel[1].write ((new Integer (right)).toString ());
        break;
      }
    }

  }

}
    
