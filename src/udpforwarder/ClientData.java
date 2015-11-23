package udpforwarder;
import java.io.Serializable;
import java.net.InetAddress;
import java.util.Scanner;

public class ClientData implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private int SessionID;
	private String data;
	
	
	ClientData()
	{
		//do nothing
	}

	public void setData(String s)
	{
		data = s;
	}
	
	public void setSessionID(int sessionID)
	{
		SessionID = sessionID;
	}
	
	public int getSessionID()
	{
		return SessionID;
	}
	
	public String getData()
	{
		return data;
	}

}
