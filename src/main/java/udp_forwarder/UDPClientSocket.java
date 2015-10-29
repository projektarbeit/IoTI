package udp_forwarder;

import java.io.*;
import java.net.*;

/**
 * UDPClient, Um die Daten an Server zu senden und die Response aus Server zu bekommen
 * UDP客户端程序，用于对服务端发送数据，并接收服务端的回应信息
 */
public class UDPClientSocket {
	private byte[] buffer = new byte[1024];

	private static DatagramSocket ds = null;
	
	/**
	 * TestMetode für ClientPacket Senden und Response Aufnahme
	 * 测试客户端发包和接收回应信息的方法
	 */
	public static void main(String[] args) throws Exception {
		UDPClientSocket client = new UDPClientSocket();
		String serverHost = "127.0.0.1";
		int serverPort = 3344;
		client.send(serverHost, serverPort, ("Hallo!").getBytes());
		byte[] bt = client.receive();
		System.out.println("Server antwortet:" + new String(bt));
		// schliessen Verbunden 关闭连接
		try {
			ds.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Konstruktor   构造函数，创建UDP客户端
	 */
	public UDPClientSocket() throws Exception {
		ds = new DatagramSocket(8899); // hostport als Client  邦定本地端口作为客户端
	}
	
	/**
	 * senden Daten an Server
	 * 向指定的服务端发送数据信息
	 */
	public final void send(final String host, final int port,
			final byte[] bytes) throws IOException {
		DatagramPacket dp = new DatagramPacket(bytes, bytes.length, InetAddress.getByName(host), port);
		ds.send(dp);
	}

	/**
	 * nehmen die Daten aus Server auf
	 * 接收从指定的服务端发回的数据
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
