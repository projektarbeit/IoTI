package udpforwarder;

public class RunForwarder {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		Forwarder fwd = new Forwarder();
		fwd.createAndListenSocket();
		fwd.closeSocket();
		
	}

}
