package udp_2;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {

	private DatagramSocket socket;

	private InetAddress forwarderIP;
	private int forwarderPort;

	private int sessionID, mySessionID;

	private ByteArrayOutputStream outputByteBuffer;
	private ObjectOutputStream outputStream;
	private DatagramPacket outgoingPacket;
	private byte[] outgoingPacketData;

	// Input packets
	private ByteArrayInputStream inputByteBuffer;
	private DataInputStream inputStream;
	private DatagramPacket incomingPacket;
	private byte[] incomingPacketData;

	private int TIMEOUT = 20000;

	public Client(String myID, String otherID) {

		try {
			socket = new DatagramSocket();
			socket.setSoTimeout(TIMEOUT);
		} catch (SocketException e1) {
			e1.printStackTrace();
		}

		sessionID = Integer.parseInt(otherID);
		mySessionID = Integer.parseInt(myID);

		try {
			forwarderIP = InetAddress.getByName("85.16.151.69");
			forwarderPort = 9876;
		} catch (UnknownHostException e) {
			System.out.println(e.getMessage());
		}

		/*
		 * Startet einen seperaten Thread (anonyme Klasse) die einen long
		 * polling betreibt
		 */
		longPoll();
	}

	public void longPoll() {

		new Thread(new Runnable() {
			@Override
			public void run() {

				ByteArrayOutputStream streambuffer;
				DataOutputStream outstream;

				while (true) {
					System.out.println("Initiere long poll...");

					try {

						// Ping senden
						streambuffer = new ByteArrayOutputStream();
						outstream = new DataOutputStream(streambuffer);
						outstream.writeBoolean(false);// Request?
						outstream.writeInt(sessionID);
						byte[] packetData = streambuffer.toByteArray();
						DatagramPacket pingPacket = new DatagramPacket(packetData, packetData.length, forwarderIP,
								forwarderPort);
						socket.send(pingPacket);
						System.out.println("Ping versendet.");

						// Request senden
						streambuffer = new ByteArrayOutputStream();
						outstream = new DataOutputStream(streambuffer);
						outstream.writeBoolean(true);// Request?
						outstream.writeInt(mySessionID);
						packetData = streambuffer.toByteArray();

						DatagramPacket longPollPacket = new DatagramPacket(packetData, packetData.length, forwarderIP,
								forwarderPort);
						socket.send(longPollPacket);
						System.out.println(
								"Long poll Paket abgeschickt. Waiting.. (Timeout: " + (TIMEOUT / 1000) + " Sekunden)");

						// Auf Antwort warten
						incomingPacketData = new byte[2048];
						incomingPacket = new DatagramPacket(incomingPacketData, incomingPacketData.length);
						socket.receive(incomingPacket);
						System.out.println("Antwort empfangen.");

						// Antwort auswerten
						byte[] dataIn = incomingPacket.getData();
						inputByteBuffer = new ByteArrayInputStream(dataIn);
						inputStream = new DataInputStream(inputByteBuffer);
						
						Thread.sleep(2000);

					} catch (IOException | InterruptedException e) {
						System.out.println("Timeout");
					}
				}

			}
		}).start();

	}

}
