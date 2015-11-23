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
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedList;

public class Forwarder {
	
	//private ArrayList<Client> clientlist;
	private boolean running;
	
	private DatagramSocket serversocket;
	byte[] incomingPacketData;
	byte[] outgoingPacketData;
	
	//Output packets
	private ByteArrayOutputStream outputByteBuffer;
	private ObjectOutputStream outputStream;
	private DatagramPacket outgoingPacket;
	
	//Input packets
	private ByteArrayInputStream inputByteBuffer;
	private ObjectInputStream inputStream;
	private DatagramPacket incomingPacket;
	
	private HashMap<Integer, String[]> list;				// Liste mit ID und dazugehoerige Clients
	private HashMap<Integer, GregorianCalendar> timelist;	// Liste mit ID und Gueltigkeit der Session (5 Minuten)
	private LinkedList<Integer> sessionlist;				// Liste mit gueltigen Sessions
	
	Forwarder()
	{
		list = new HashMap<>();
		timelist = new HashMap<>();
		sessionlist = new LinkedList<>();
	}
	
	
	public void checkClientList()
	{
		int minutes;
		long difference;
		GregorianCalendar now = new GregorianCalendar();
		for(Integer i : sessionlist)
		{
			difference = timelist.get(i).getTimeInMillis() - now.getTimeInMillis();
			minutes = (int) (difference / (1000 * 60) % 60);
			if(minutes >= 5)
			{
				list.remove(i);
				timelist.remove(i);
				sessionlist.remove(i);
			}
		}
	}

	
	public void closeSocket()
	{
		serversocket.close();
	}

	public void addToList(LongPoll lp)
	{
		if(!list.containsKey(lp.getSessionID()))
		{
			String[] tmp = new String[4];
			tmp[0] = incomingPacket.getAddress().toString();
			tmp[1] = Integer.toString(incomingPacket.getPort());
			tmp[2] = "";
			tmp[3] = "";
			list.put(lp.getSessionID(), tmp);
			timelist.put(lp.getSessionID(), new GregorianCalendar());
			sessionlist.add(lp.getSessionID());
		}
		else if(list.containsKey(lp.getSessionID())) 
		{
			String[] tmp = list.get(lp.getSessionID());
			tmp[2] = incomingPacket.getAddress().toString();
			tmp[3] = Integer.toString(incomingPacket.getPort());
			list.put(lp.getSessionID(), tmp);
		}
	}
	
	public void createAndListenSocket() 
	{
		try 
		{
			serversocket = new DatagramSocket(9876);
			incomingPacketData = new byte[2048];
			
			while(true)
			{
				//checkClientList();
				incomingPacket = new DatagramPacket(incomingPacketData, incomingPacketData.length);
				
				System.out.println("Warte auf Daten...");
				serversocket.receive(incomingPacket);
				System.out.println("Paket angekommen.");
				
				//Daten auswerten
				byte dataIn[] = incomingPacket.getData();
				inputByteBuffer = new ByteArrayInputStream(dataIn);
				inputStream = new ObjectInputStream(inputByteBuffer);
				
				/* 
				 * Der nachfolgende Block ist dafuer gedacht um spaeter
				 * Objekte zu serialisieren. Durch die Kapselung von Daten
				 * in Form eines Objektes ist es einfacher die vorliegenden
				 * Daten auszulesen, da die Datenfelder auch unterschiedlich 
				 * lang sein koennen. Dadurch kann die Benutzung von Trennzeichen
				 * zwischen den Datenfeldern im ByteArray umgangen werden. Es
				 * werden lediglich die gewuenschten Attribute des Objektes
				 * ausgelesen.
				 * 
				 */
				try 
				{
					outputByteBuffer = new ByteArrayOutputStream();
					outputStream = new ObjectOutputStream(outputByteBuffer);
					
					Object obj = inputStream.readObject();
					if(obj instanceof LongPoll)
					{
						LongPoll lp = (LongPoll) obj;
						addToList(lp);
					}
					else if(obj instanceof ClientData)
					{
						ClientData cd = (ClientData) obj;
						
						outputStream.flush();
						outputStream.writeObject(cd);
						outputStream.flush();
						
						outgoingPacketData = outputByteBuffer.toByteArray();

						String tmp[] = list.get(cd.getSessionID());
						
						outgoingPacket = new DatagramPacket(outgoingPacketData, outgoingPacketData.length, InetAddress.getByName(tmp[2]), Integer.parseInt(tmp[3]));
						serversocket.send(outgoingPacket);
					}					
					
										
				}
				catch(ClassNotFoundException e)
				{
					System.out.println(e.getMessage());
				}				
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

}
