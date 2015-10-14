package udp_ping;

import java.net.*;
import java.io.*;

/**
 *
 * @author  cli
 * @version 
 */
public class DatagramReflector extends Thread {
    InetAddress     _localAddress;
    int             _localPort;
//    InetAddress     _targetAddress;
//    int             _targetPort;
    DatagramSocket  _socket;
    long            _pingerID;
    int             _datagramSize;
//    int             _datagramNumber;
    

    /** Creates new DatagramReflector */
    public DatagramReflector() {
    }
    
    public void initialize(long id, String localIP, String localPort, int datagramSize) throws UnknownHostException, SocketException {
        _pingerID       = id;
        _localPort      = Integer.parseInt(localPort);
        _localAddress   = InetAddress.getByName(localIP);
//        _targetPort     = Integer.parseInt(targetPort);
//        _targetAddress  = InetAddress.getByName(targetIP);
        _datagramSize   = datagramSize;
//        _datagramNumber = 0;
        _socket = new DatagramSocket(_localPort, _localAddress);
//        _socket.setSoTimeout(1000);
    }
    
    public void run(){
        boolean loop = true;
        boolean gotDatagram = false;
        byte[] buffer = new byte[_datagramSize];
        DatagramPacket receiveDatagram = new DatagramPacket(buffer, _datagramSize);
        while(loop){
            try{
                _socket.receive(receiveDatagram);
                //gotDatagram = true;
            } catch(IOException ioe){
                loop = false;
                System.out.println("DatagramReflector::run()");
                ioe.printStackTrace();
                //gotDatagram = false;
                /*
                if(ioe instanceof InterruptedIOException){
                    // OK: timeout
                }else{
                    ioe.printStackTrace();
                    loop = false;
                }*/
            }
            if(loop){
                handlePacket(receiveDatagram);
            }
            yield();
        }
    }

    protected void handlePacket(DatagramPacket packet){
        long currentTime = System.currentTimeMillis();
        long packetID = 0;
        long packetTransmitTimestamp = 0;
        int  packetNumber = 0;
        //int  targetPort = 0;
        String packetMessage;
        byte[] buffer = packet.getData();
        ByteArrayInputStream byteStream = new ByteArrayInputStream(buffer, 0, packet.getLength());
        DataInputStream dataStream = new DataInputStream(byteStream);
        try{
            packetID = dataStream.readLong();
            packetTransmitTimestamp = dataStream.readLong();
            packetNumber = dataStream.readInt();
            //targetPort = dataStream.readInt();
            //packetMessage = dataStream.readChars();
        } catch(IOException ioe){
            System.out.println("DatagramReflector::handlePacket() 1");
            ioe.printStackTrace();
        }
        if(packetID != _pingerID){
            try{
                //DatagramPacket echoPacket = new DatagramPacket(buffer, buffer.length, packet.getAddress(), packet.getPort());
                DatagramPacket echoPacket = packet;
                System.out.println("echoing #" + packetNumber + " containing " + echoPacket.getLength() + " bytes to " + echoPacket.getAddress().toString() + ":" + echoPacket.getPort());
                _socket.send(echoPacket);
            } catch(IOException ioe){
                System.out.println("DatagramReflector::handlePacket() 2");
                ioe.printStackTrace();
            }
        }else{
            System.out.println("received packet #" + packetNumber + " roundtrip time: " + (currentTime-packetTransmitTimestamp) + "ms");
        }
    }
}
