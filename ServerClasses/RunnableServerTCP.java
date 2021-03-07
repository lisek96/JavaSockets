package ServerClasses;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Locale;

public class RunnableServerTCP implements Runnable {
    private final Socket TCP_CONNECTION;
    private final int UDP_PORT;

    RunnableServerTCP(Socket SOCKET, int UDP_PORT){
       this.TCP_CONNECTION = SOCKET;
       this.UDP_PORT = UDP_PORT;
    }

    @Override
    public void run() {
        int TCP_PORT = 0;
        try {
            TCP_PORT = TCP_CONNECTION.getPort();
            Server.logTCP("TCP connection with client established", UDP_PORT, TCP_PORT);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(TCP_CONNECTION.getInputStream()));
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(TCP_CONNECTION.getOutputStream()));
            Server.logTCP("Waiting for dateformat from client", UDP_PORT, TCP_PORT);
            TCP_CONNECTION.setSoTimeout(5000);
            String lineFromClient = bufferedReader.readLine();
            Server.logTCP("Desired date format from client: " + lineFromClient, UDP_PORT, TCP_PORT);
            Locale.setDefault(new Locale(lineFromClient));
            bufferedWriter.write(DateFormat.getDateInstance().format(Calendar.getInstance().getTime()));
            bufferedWriter.flush();
            Server.logTCP("Client served, closing TCP connection...", UDP_PORT, TCP_PORT);
            bufferedWriter.close();
            bufferedReader.close();
            TCP_CONNECTION.close();
            Server.logTCP("TCP connection for served client closed.", UDP_PORT, TCP_PORT);
        } catch (IOException ioException) {
            Server.logTCP("Time out, no response from client after successful connection establishment", UDP_PORT, TCP_PORT);
            ioException.printStackTrace();
        }

    }
}
