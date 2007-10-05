import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;


public class GenerateBatchFiles {

	public static void main(String[] args) {
		try
		{
			BufferedReader br = new BufferedReader(new FileReader(args[0]));
			
			String line;
			while (null != (line = br.readLine()))
			{
				line = line.trim();
				
				if (line.length() > 0)
				{
				
					final String demosPkg = "org.jcsp.demos.";
					String batchName;
					if (line.startsWith(demosPkg))
					{
						batchName = line.substring(demosPkg.length());
					}
					else
					{
						batchName = line;
					}
					batchName = batchName.replace('.','_') + ".bat";
					
					FileWriter out = new FileWriter(batchName);
					out.write("java -classpath \"jcsp-demos.jar;jcsp-demos-util.jar;jcsp.jar\" " + line + "\r\n");
					out.close();
				}
			}
			
			br.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

}
