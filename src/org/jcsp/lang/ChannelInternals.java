package org.jcsp.lang;

interface ChannelInternals {
	public Object read();
	public void write(Object obj);
	
	public Object startRead();
	public void endRead();
	
	public boolean readerEnable(Alternative alt);
	public boolean readerDisable();
	public boolean readerPending();
	
	/*//For Symmetric channel, later:
	public boolean writerEnable(Alternative alt);
	public boolean writerDisable();
	public boolean writerPending();
	*/
	
	public void readerPoison(int strength);
	public void writerPoison(int strength);
}
