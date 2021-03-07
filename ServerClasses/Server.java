package ServerClasses;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

public class Server {
    protected static ServerSocket serverTCP_ForCommunicationWithValidClients;
    protected static int portTCP_forCommunicationWithValidClients;

    public static void main(String[] args) throws IOException {
        Set<Integer> UDP_PORTS_TO_OPEN = new HashSet<>();
        for(int i=0; i<args.length;i++){
            int portToOpen = Integer.parseInt(args[i]);
            if(portToOpen<=1024){
                System.out.println("Choose ports bigger than 1024");
                return;
            }
            UDP_PORTS_TO_OPEN.add(portToOpen);
        }
        portTCP_forCommunicationWithValidClients = new Random().nextInt(9999)+1050;
        serverTCP_ForCommunicationWithValidClients = new ServerSocket(portTCP_forCommunicationWithValidClients);
        openPorts(UDP_PORTS_TO_OPEN);
    }

    private static void openPorts(Collection<Integer> portsToOpen) {
        portsToOpen.forEach(port -> new Thread(new RunnableServerUDP(port)).start());
    }

    private static boolean checkIfAllMessageWereSentBySameClient(DatagramPacket[] datagramsReceived) {
        int firstMessagePort = datagramsReceived[0].getPort();
        InetAddress firstMessageAddress = datagramsReceived[0].getAddress();
        return Arrays.stream(datagramsReceived)
                .allMatch(d -> d.getPort() == firstMessagePort && d.getAddress().equals(firstMessageAddress));
    }

    private static boolean checkIfSequenceOfMsgsIsValid(DatagramPacket[] datagramsReceived) {
        final String validSequence = "SKJ";
        String sequence = Arrays.stream(datagramsReceived)
                .map(datagramPacket -> datagramPacket.getData())
                .map(data -> new String(data, StandardCharsets.UTF_8))
                .collect(Collectors.joining(""));
        return validSequence.equals(sequence);
    }

    protected static boolean checkIfClientIsValid(DatagramPacket[] datagramsReceived) {
        return checkIfAllMessageWereSentBySameClient(datagramsReceived) && checkIfSequenceOfMsgsIsValid(datagramsReceived);
    }

    protected static void logUDP(String s, int port) {
        System.out.println("ServerUDP:"+port+" "+s);
    }
    protected static void logTCP(String s, int UDPport, int TCPport){
        System.out.println("ServerTCP:"+TCPport+"serverUDP:"+UDPport+" "+s);
    }



}

