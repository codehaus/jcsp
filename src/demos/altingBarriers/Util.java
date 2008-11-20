
import org.jcsp.lang.Any2OneChannel;
import org.jcsp.lang.ChannelInput;
import org.jcsp.lang.ChannelOutput;
import org.jcsp.lang.One2OneChannel;

public class Util {

	static ChannelInput[][] get2DInputArray(One2OneChannel[][] channels) {
		ChannelInput[][] ret = new ChannelInput[channels.length][];
		
		for (int i = 0;i < channels.length;i++) {
			ret[i] = new ChannelInput[channels[i].length];
			for (int j = 0;j < channels[i].length;j++) {
				ret[i][j] = channels[i][j].in(); 
			}
		}
		
		return ret;
	}
	
	static ChannelOutput[][] get2DOutputArray(One2OneChannel[][] channels) {
		ChannelOutput[][] ret = new ChannelOutput[channels.length][];
		
		for (int i = 0;i < channels.length;i++) {
			ret[i] = new ChannelOutput[channels[i].length];
			for (int j = 0;j < channels[i].length;j++) {
				ret[i][j] = channels[i][j].out(); 
			}
		}
		
		return ret;
	}
	
	static ChannelOutput[][] get2DOutputArray(Any2OneChannel[][] channels) {
		ChannelOutput[][] ret = new ChannelOutput[channels.length][];
		
		for (int i = 0;i < channels.length;i++) {
			ret[i] = new ChannelOutput[channels[i].length];
			for (int j = 0;j < channels[i].length;j++) {
				ret[i][j] = channels[i][j].out(); 
			}
		}
		
		return ret;
	}
}
