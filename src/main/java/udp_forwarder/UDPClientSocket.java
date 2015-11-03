package main.java.udp_forwarder;

import java.io.*;
import java.net.*;

/**
 * UDPClient, Um die Daten an Server zu senden und die Response aus Server zu bekommen
 * UDP瀹㈡埛绔▼搴忥紝鐢ㄤ簬瀵规湇鍔＄鍙戦�佹暟鎹紝骞舵帴鏀舵湇鍔＄鐨勫洖搴斾俊鎭�
 */
public class UDPClientSocket {
	private byte[] buffer = new byte[1024];

	private static DatagramSocket ds = null;
	
	/**
	 * TestMetode f眉r ClientPacket Senden und Response Aufnahme
	 * 娴嬭瘯瀹㈡埛绔彂鍖呭拰鎺ユ敹鍥炲簲淇℃伅鐨勬柟娉�
	 */
	public static void main(String[] args) throws Exception {
		UDPClientSocket client = new UDPClientSocket();
		String serverHost = "127.0.0.1";
		int serverPort = 3344;
		client.send(serverHost, serverPort, ("Hallo!").getBytes());
		byte[] bt = client.receive();
		System.out.println("Server antwortet:" + new String(bt));
		// schliessen Verbunden 鍏抽棴杩炴帴
		try {
			ds.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Konstruktor   鏋勯�犲嚱鏁帮紝鍒涘缓UDP瀹㈡埛绔�
	 */
	public UDPClientSocket() throws Exception {
		ds = new DatagramSocket(8899); // hostport als Client  閭﹀畾鏈湴绔彛浣滀负瀹㈡埛绔�
	}
	
	/**
	 * senden Daten an Server
	 * 鍚戞寚瀹氱殑鏈嶅姟绔彂閫佹暟鎹俊鎭�
	 */
	public final void send(final String host, final int port,
			final byte[] bytes) throws IOException {
		DatagramPacket dp = new DatagramPacket(bytes, bytes.length, InetAddress.getByName(host), port);
		ds.send(dp);
	}

	/**
	 * nehmen die Daten aus Server auf
	 * 鎺ユ敹浠庢寚瀹氱殑鏈嶅姟绔彂鍥炵殑鏁版嵁
	 */
	public final byte[] receive()
			throws Exception {
		DatagramPacket dp = new DatagramPacket(buffer, buffer.length);
		ds.receive(dp);		
		byte[] data = new byte[dp.getLength()];
		System.arraycopy(dp.getData(), 0, data, 0, dp.getLength());		
		return data;
	}
}

