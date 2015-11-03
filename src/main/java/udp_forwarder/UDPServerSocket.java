package main.java.udp_forwarder;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

/**
 * UDPServer Klassen
 */
public class UDPServerSocket {
	
	private byte[] buffer = new byte[1024];
	private static DatagramSocket ds = null;
	private DatagramPacket packet = null;
	private InetSocketAddress socketAddress = null;
	
	/**
	 * TestMethode
	 */
	public static void main(String[] args) throws Exception {
		String serverHost = "127.0.0.1";
		int serverPort = 3344;
		UDPServerSocket udpServerSocket = new UDPServerSocket(serverHost,
				serverPort);
		while (true) {
			udpServerSocket.receive();
			udpServerSocket.response("Hallo,wie geht's?");
		}		
	}

	/**
	 * Konstruktor,die Host und Port zu verbinden   (鏋勯�犲嚱鏁帮紝缁戝畾涓绘満鍜岀鍙�)
	 */
	public UDPServerSocket(String host, int port) throws Exception {
		socketAddress = new InetSocketAddress(host, port);
		ds = new DatagramSocket(socketAddress);
		System.out.println("Der Server verbunden!");
	}

	/**
	 * Packet aufnehmen.  鎺ユ敹鏁版嵁鍖咃紝璇ユ柟娉曚細閫犳垚绾跨▼闃诲顢�
	 */
	public final String receive() throws IOException {
		packet = new DatagramPacket(buffer, buffer.length);
		ds.receive(packet);
		String info = new String(packet.getData(), 0, packet.getLength());
		System.out.println("aufgenommende Information:" + info);
		return info;
	}

	/**
	 * Der Server sendet den Req. an Client  灏嗗搷搴斿寘鍙戦�佺粰璇锋眰绔�
	 */
	public final void response(String info) throws IOException {
		System.out.println("ClientAdresse : " + packet.getAddress().getHostAddress()
				+ ",Port:" + packet.getPort());
		DatagramPacket dp = new DatagramPacket(buffer, buffer.length, packet
				.getAddress(), packet.getPort());
		dp.setData(info.getBytes());
		ds.send(dp);
	}
}

