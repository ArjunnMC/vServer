package me.arjunn_.io;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.UUID;

public class ServerThread extends Thread {

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    public ServerThread(Socket clientSocket) {
        this.socket = clientSocket;
    }

    public void run() {
        System.out.println("New me.arjunn_.io.ServerThread initialized for " + socket.getInetAddress() + ":" + socket.getPort());
        try {
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        JSONObject received;

        while (true) {
            try {
                String s = in.readLine();
                if (s == null) {
                    disconnect();
                    return;
                }
                received = new JSONObject(s);
                Server.handle(received, this);
            } catch (IOException e) {
                disconnect();
                return;
            }
        }
    }

    public void sendData(JSONObject object) {
        this.out.println(object.toString());
    }

    public void disconnect() {
        try {
            JSONObject disconnect = new JSONObject();
            disconnect.put("event", "DISCONNECT");
            disconnect.put("responseID", UUID.randomUUID().toString());
            sendData(disconnect);
            socket.close();
            Server.removeClient(this);
            this.interrupt();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
