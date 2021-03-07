package ClientClasses;

import Interfaces.ThrowingConsumer;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Scanner;

public class RunnableClient implements Runnable {
    InetAddress serverAddress;
    int serverPort;
    DatagramSocket datagramSocket;

    RunnableClient(String serverAddress, int serverPort, DatagramSocket datagramSocket) throws UnknownHostException {
        this.serverAddress = InetAddress.getByName(serverAddress);
        this.serverPort = serverPort;
        this.datagramSocket = datagramSocket;
    }

    @Override
    public void run() {
        String[] sequenceToSend = {"S", "K", "J"};
        List<DatagramPacket> datagramsToSend = Arrays.stream(sequenceToSend)
                .map(s -> s.getBytes(StandardCharsets.UTF_8))
                .map(bytes -> new DatagramPacket(bytes, bytes.length, serverAddress, serverPort))
                .collect(Collectors.toList());
        Client.log("Sent sequence of datagrams", serverPort);
        datagramsToSend.forEach((ThrowingConsumer<DatagramPacket>) datagramPacket -> datagramSocket.send(datagramPacket));
        try {
            byte[] portBytes = new byte[4];
            DatagramPacket datagramPacket = new DatagramPacket(portBytes, portBytes.length);
            datagramSocket.setSoTimeout(5000);
            datagramSocket.receive(datagramPacket);
            datagramSocket.setSoTimeout(0);
            Client.log("Received serverPort for TCP communication.. Starting TCP communication on received serverPort.", serverPort);
            int serverPort = Integer.parseInt(new String(datagramPacket.getData()));
            final Socket TCP_CONNECTION = new Socket(serverAddress, serverPort);
            Client.log("TCP connection established", this.serverPort);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(TCP_CONNECTION.getInputStream()));
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(TCP_CONNECTION.getOutputStream()));
            Client.log("Sending desired DateFormat to Server", this.serverPort);
            bufferedWriter.write("pl\r\n");
            bufferedWriter.flush();
            Client.log("Desired DateFormat sent", this.serverPort);
            Client.log("Received answer, today date in desired format: " + bufferedReader.readLine(), this.serverPort);
            Client.log("Everything went well, I know the Date now, closing connection...", this.serverPort);
            bufferedReader.close();
            bufferedWriter.close();
            TCP_CONNECTION.close();
            Client.log("Connection closed, bye!", this.serverPort);
        } catch (IOException e) {
            System.err.println("Client knocking on UDP port " + serverPort + ": Received time out");
		new Scanner(System.in).next();
	    }

    }
}
