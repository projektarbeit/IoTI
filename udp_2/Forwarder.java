package udp_2;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedList;

public class Forwarder {

	// private ArrayList<Client> clientlist;
	private boolean running;

	private DatagramSocket serversocket;

	// Output packets
	private ByteArrayOutputStream outputByteBuffer;
	private ObjectOutputStream outputStream;
	private DatagramPacket outgoingPacket;
	private byte[] outgoingPacketData;

	// Input packets
	private ByteArrayInputStream inputByteBuffer;
	private DataInputStream inputStream;
	private DatagramPacket incomingPacket;
	private byte[] incomingPacketData;

	// Sessions
	private HashMap<Integer, String[]> list;
	private LinkedList<Integer> sessionlist;
	private long TIMEOUT = 60000;

	Forwarder() {
		list = new HashMap<>();
		sessionlist = new LinkedList<>();
	}

	public void checkClientList() {

		for (Integer i : sessionlist) {

			String[] tmp = list.get(i);

			if (System.currentTimeMillis() - Long.parseLong(tmp[2]) > TIMEOUT) {
				list.remove(i);
				System.out.println("Session " + i + " removed.");
			}
		}
	}

	public void closeSocket() {
		serversocket.close();
	}

	public void addToList(int session) {

		if (!list.containsKey(session)) {
			String[] tmp = new String[4];
			tmp[0] = incomingPacket.getAddress().getHostAddress();
			tmp[1] = Integer.toString(incomingPacket.getPort());
			tmp[2] = String.valueOf(System.currentTimeMillis());
			list.put(session, tmp);
			sessionlist.add(session);
			System.out.println("Session " + session + " created.");
		} else if (list.containsKey(session)) {
			String[] tmp = list.get(session);
			tmp[0] = incomingPacket.getAddress().getHostAddress();
			tmp[1] = Integer.toString(incomingPacket.getPort());
			tmp[2] = String.valueOf(System.currentTimeMillis());
			list.put(session, tmp);
			System.out.println("Session " + session + " renewed.");
		}

	}

	public void createAndListenSocket() {
		try {
			serversocket = new DatagramSocket(9876);
			incomingPacketData = new byte[2048];

			while (true) {

				checkClientList();
				incomingPacket = new DatagramPacket(incomingPacketData, incomingPacketData.length);

				System.out.println("Warte auf Daten...");
				serversocket.receive(incomingPacket);
				System.out.println(System.currentTimeMillis()+" - Paket angekommen.");

				// Daten auswerten
				byte dataIn[] = incomingPacket.getData();
				inputByteBuffer = new ByteArrayInputStream(dataIn);
				inputStream = new DataInputStream(inputByteBuffer);

				boolean isRequest = inputStream.readBoolean();
				int session = inputStream.readInt();

				if (isRequest) {
					System.out.println("Request");
					addToList(session);
				} else {

					if (list.containsKey(session)) {

						System.out.println("Ping");
						String ip = list.get(session)[0];
						String port = list.get(session)[1];
					
						outputByteBuffer = new ByteArrayOutputStream();
						outputStream = new ObjectOutputStream(outputByteBuffer);
						outgoingPacketData = outputByteBuffer.toByteArray();

						DatagramPacket outgoingPacket = new DatagramPacket(outgoingPacketData,
								outgoingPacketData.length, InetAddress.getByName(ip), Integer.parseInt(port));
					
						serversocket.send(outgoingPacket);
					}
					else {
						
						System.out.println("Session doesn't exist.");
						
					}
					
				}

			}
		} catch (SocketException e) {
			System.out.println("ERROR"+e.getMessage());
		} catch (IOException e) {
			System.out.println("ERROR2"+e.getMessage());
		}
	}

}
