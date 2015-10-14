package udp_ping;

import java.net.*;
import java.io.*;

/**
 *
 * @author  cli
 * @version 
 */
public class DatagramSender extends java.lang.Thread {
    InetAddress     _targetAddress;
    int             _targetPort;
    DatagramSocket  _socket;
    long            _pingerID;
    int             _datagramSize;
    int             _datagramNumber;

    /** Creates new DatagramReflector */
    public DatagramSender() {
    }
    
    public void initialize(long id, String targetAddress, String targetPort, int datagramSize) throws UnknownHostException, SocketException {
        _pingerID       = id;
        _targetPort     = Integer.parseInt(targetPort);
        _targetAddress  = InetAddress.getByName(targetAddress);
        _datagramSize   = datagramSize;
        _datagramNumber = 0;
        _socket = new DatagramSocket();
        _socket.setSoTimeout(1000);
    }
    
    public void run(){
        boolean loop = true;
        boolean gotDatagram = false;
        byte[] buffer = new byte[_datagramSize];
        DatagramPacket receiveDatagram = new DatagramPacket(buffer, _datagramSize);
        while(loop){
            try{
                DatagramPacket p = nextPacket();
                //System.out.println("sending " + _datagramNumber + " " + p.getLength() + " " + p.getAddress().toString() + ":" + p.getPort());
                _socket.send(p);
            } catch(IOException ioe){
                //loop = false;
                System.out.println("DatagramSender::run() " + ioe.toString());
                //ioe.printStackTrace();
            }
            try{
                receiveDatagram.setData(buffer, 0, _datagramSize);
                _socket.receive(receiveDatagram);
                if(receiveDatagram.getLength() > 0){
                    gotDatagram = true;
                }
            } catch(IOException ioe){
                //loop = false;
                gotDatagram = false;
                if(ioe instanceof InterruptedIOException){
                    // OK: timeout
                }else{
                    System.out.println("DatagramSender::run() " + ioe.getMessage());
                    //loop = false;
                }
            }
            if(loop && gotDatagram){
                handlePacket(receiveDatagram);
            }
            sleepABit();
            yield();
        }
    }
    
    protected DatagramPacket nextPacket(){
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream(_datagramSize);
        DataOutputStream dataStream = new DataOutputStream(byteStream);
        try{
            dataStream.writeLong(_pingerID);
            dataStream.writeLong(System.currentTimeMillis());
            dataStream.writeInt(_datagramNumber++);
            dataStream.writeChars("UDPPing");
            byte[] tail = new byte[50000];
            for(int i=0;i<tail.length; i++){
                tail[i] = (byte)((i%64) + 30);
            }
            dataStream.write(tail,0,tail.length);
        } catch(IOException ioe){
            System.out.println("DatagramSender::nextPacket()");
            ioe.printStackTrace();
        }
        byte[] datagramBuffer = byteStream.toByteArray();
        DatagramPacket datagram = new DatagramPacket(datagramBuffer, datagramBuffer.length, _targetAddress, _targetPort);
        return datagram;
    }

    protected void handlePacket(DatagramPacket packet){
        long currentTime = System.currentTimeMillis();
        long packetID = 0;
        long packetTransmitTimestamp = 0;
        int  packetNumber = 0;
        String packetMessage;
        byte[] buffer = packet.getData();

        //System.out.println("handlePacket" + " containing " + packet.getLength() + " bytes from " + packet.getAddress().toString() + ":" + packet.getPort());
        
        ByteArrayInputStream byteStream = new ByteArrayInputStream(buffer, 0, packet.getLength());
        DataInputStream dataStream = new DataInputStream(byteStream);
        try{
            packetID = dataStream.readLong();
            packetTransmitTimestamp = dataStream.readLong();
            packetNumber = dataStream.readInt();
        } catch(IOException ioe){
            System.out.println("DatagramSender::handlePacket() 1");
            ioe.printStackTrace();
        }
        if(packetID == _pingerID){
            System.out.println("received packet #" + packetNumber + " roundtrip time: " + (currentTime-packetTransmitTimestamp) + "ms");
        }else{
            System.out.println("received unknown packet!");
        }
    }
    protected void sleepABit(){
        try{
            Thread.sleep(1000);
        } catch (Exception e){
            System.out.println("DatagramSender::sleepABit()");
            e.printStackTrace();
        }
    }
}
