
package udpforwarder;
import java.io.Serializable;

public class LongPoll implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private int sessionID;

	LongPoll(int id)
	{
		sessionID = id;
	}
	
	public int getSessionID()
	{
		return sessionID;
	}
	
	public void setSessionID(int id)
	{
		sessionID = id;
	}
}
