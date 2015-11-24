package udp_2;

public class Main {

	public static void main(String[] args) {

		if (args.length == 0) {

			Forwarder fwd = new Forwarder();
			fwd.createAndListenSocket();

		}

		else {

			if (args.length < 2) {
				
				System.out.println("Too few arguments.");
				return;
				
			}
			
			Client c = new Client(args[0], args[1]);

		}
	}

}
