package ServerClasses;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.locks.ReentrantLock;

public class RunnableServerUDP implements Runnable {
    int port;
    static ReentrantLock lock = new ReentrantLock();
    
RunnableServerUDP(int port){
        this.port=port;
    }

    @Override
    public void run() {
        try {
            DatagramSocket datagramSocket = new DatagramSocket(port);
            Server.logUDP("opened", port);
            byte[] data1 = new byte[1];
            byte[] data2 = new byte[1];
            byte[] data3 = new byte[1];
            DatagramPacket dp1 = new DatagramPacket(data1, data1.length);
            DatagramPacket dp2 = new DatagramPacket(data2, data2.length);
            DatagramPacket dp3 = new DatagramPacket(data3, data3.length);
            while(true) {
                Server.logUDP("Waiting for messages", port);
                datagramSocket.receive(dp1);
                Server.logUDP("first message received: ", port);
                datagramSocket.receive(dp2);
                Server.logUDP("second message received", port);
                datagramSocket.receive(dp3);
                Server.logUDP("third message received", port);
                if(!Server.checkIfClientIsValid(new DatagramPacket[]{dp1, dp2, dp3})) {
                    Server.logUDP("Client not valid, resetting...", port);
                    continue;
                }
                else {
                    Server.logUDP("Client valid, sending datagram with TCP port", port);
                    byte[] bytesToSend = Integer.toString(Server.portTCP_forCommunicationWithValidClients).getBytes(StandardCharsets.UTF_8);
                    DatagramPacket toSend = new DatagramPacket(bytesToSend, bytesToSend.length, dp1.getAddress(), dp1.getPort());
                    Server.logUDP("Sending TCP port", port);
                    datagramSocket.send(toSend);
                    Server.logUDP("TCP port sent", port);
                    Server.logTCP("Accepting TCP connection", port, 0);
			synchronized (lock) {
                    new Thread(new RunnableServerTCP(Server.serverTCP_ForCommunicationWithValidClients.accept(), port)).start();
			}
                }
            }
        } catch (BindException e1){
            Server.logUDP("Address already in use: Cannot bind", port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
