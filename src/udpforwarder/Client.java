package udpforwarder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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
	
	private int sessionID;
	private ClientData cd;
	private Scanner sc;
	
	private ByteArrayOutputStream outputByteBuffer;
	private ObjectOutputStream outputStream;
	private DatagramPacket outgoingPacket;
	private byte[] outgoingPacketData;
	
	//Input packets
	private ByteArrayInputStream inputByteBuffer;
	private ObjectInputStream inputStream;
	private DatagramPacket incomingPacket;
	private byte[] incomingPacketData;

	public Client()
	{		
		cd = new ClientData();
		
		System.out.println("SessionID eingeben: ");
		//sessionID = sc.nextInt();
		sessionID = 4711;
		
		cd.setSessionID(sessionID);
		
		try 
		{
			forwarderIP = InetAddress.getByName("139.13.81.104");
			forwarderPort = 9876;
		} 
		catch (UnknownHostException e) 
		{
			System.out.println(e.getMessage());
		}
		
		/* Startet einen seperaten Thread (anonyme Klasse) die einen long polling betreibt*/
		longPoll();
	}
	
	public void longPoll()
	{
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				ByteArrayOutputStream streambuffer;
				ObjectOutputStream outstream;
				
				while(true)
				{
					System.out.println("Initiere long poll...");
					
					try
					{
						streambuffer = new ByteArrayOutputStream();
						outstream = new ObjectOutputStream(streambuffer);
						byte[] packetData;
						outstream.flush();
						outstream.writeObject(new LongPoll(sessionID));
						outstream.flush();
						packetData = streambuffer.toByteArray();
						DatagramPacket longPollPacket = new DatagramPacket(packetData, packetData.length, forwarderIP, forwarderPort);
						socket.send(longPollPacket);
						System.out.println("long poll Paket abgeschickt, warte nun 15 Sekunden...");
						Thread.sleep(15000);	//Zeit spaeter anpassen
					}
					catch (IOException e) 
					{
						System.out.println("IOException");
						System.out.println(e.getMessage());
					} 
					catch (InterruptedException e) 
					{
						System.out.println("InterruptedException");
						System.out.println(e.getMessage());
					}
					
					
				}				
			}
		}).start();
	}
	
	public void createSocket()
	{
		try 
		{
			socket = new DatagramSocket();
			
			outputByteBuffer = new ByteArrayOutputStream();
			outputStream = new ObjectOutputStream(outputByteBuffer);
			
			outputStream.flush();
			outputStream.writeObject(cd);
			outputStream.flush();
			
			outgoingPacketData = outputByteBuffer.toByteArray();
			outgoingPacket = new DatagramPacket(outgoingPacketData, outgoingPacketData.length, forwarderIP, forwarderPort);
			
			socket.send(outgoingPacket);
			
			
			//Auf Antwort warten
			incomingPacketData = new byte[2048];
			incomingPacket = new DatagramPacket(incomingPacketData, incomingPacketData.length);
			
			byte[] dataIn = incomingPacket.getData();
			inputByteBuffer = new ByteArrayInputStream(dataIn);
			inputStream = new ObjectInputStream(inputByteBuffer);
			
			try 
			{
				ClientData _cd = (ClientData) inputStream.readObject();
				System.out.println(_cd.getData());
			}
			catch(ClassNotFoundException e)
			{
				System.out.println(e.getMessage());
			}
		}
		catch (SocketException e) 
		{
			System.out.println(e.getMessage());
		}
		catch (IOException e) 
		{
			System.out.println(e.getMessage());
		}
	}
	
	
	
	public static void main(String args[])
	{		
		Client c;
		try 
		{
			c = new Client();
			c.createSocket();
			
		}
		catch (NumberFormatException e) 
		{
			System.out.println(e.getMessage());
		}
		
		
	}

}
