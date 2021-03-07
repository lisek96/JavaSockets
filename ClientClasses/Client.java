package ClientClasses;

import Interfaces.ThrowingConsumer;

import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Arrays;
import java.util.Set;
import java.util.HashSet;

public class Client {
    public static void main(String[] args) {
        final String SERVER_ADDRESS = args[0];
	Set<Integer> portsToKnock = new HashSet<>();
        for(int i=1; i<args.length;i++){
            int portToKnock = Integer.parseInt(args[i]);
            portsToKnock.add(portToKnock);
        }
       knockToGivenServerPort(SERVER_ADDRESS, portsToKnock);
    }
    public static void knockToGivenServerPort(String serverAddress, Set<Integer> ports){
        ports.forEach((ThrowingConsumer<Integer>) port -> {
            new Thread(new RunnableClient(serverAddress, port, new DatagramSocket())).start();
        });
    }
    public static void log(String s, int port){
        System.out.println("Client knocking on UDP port " + port + ": " + s);
    }

}
