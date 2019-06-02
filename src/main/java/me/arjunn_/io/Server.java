package me.arjunn_.io;

import org.json.JSONObject;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class Server {

    protected static HashMap<String, ClientConnection> servers = new HashMap<>();
    private static HashMap<String, Request<JSONObject>> futuresToResolve = new HashMap<>();

    static final int PORT = 9093;

    public Server() {
        ServerSocket serverSocket = null;
        Socket socket = null;

        try {
            serverSocket = new ServerSocket(PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // takes user console input and sends it as echos.
        new RequestMaker(this).start();

        while (true) {
            try {
                socket = serverSocket.accept();
                System.out.println("Got a new connection on " + socket.getInetAddress());
            } catch (IOException e) {
                System.out.println("I/O error: " + e);
            }
            // new thread for the client
            new ClientConnection(socket).start();
        }
    }

    public static void main(String args[]) {
        new Server();
    }

    public static void addServer(String server, ClientConnection thread) {
        servers.put(server, thread);
        System.out.println("Client has connected: " + servers.keySet().toString());
    }

    public static void removeClient(ClientConnection thread) {
        servers.values().remove(thread);
        System.out.println("Client has disconnected: " + servers.toString());
    }

    public Request<String> getStringFromClient(String client, JSONObject request) {
        Request<String> response = new Request<>();
        futuresToResolve.put(request.getString("responseID"), response);

        servers.get(client).sendData(request);

        return response;

    }

    public Request<JSONObject>

    public static void handle(JSONObject sent, ClientConnection thread) {

        System.out.println(sent);
        String type = sent.getString("type");
        String event = sent.getString("event");
        String responseID = sent.getString("responseID");


        if (sent.has("responseID") && type.equals("response")) {
            if (futuresToResolve.containsKey(sent.getString("responseID"))) {
                futuresToResolve.get(sent.getString("responseID")).setResponse(sent);
            }
            return;
        }

        if (event.equalsIgnoreCase("connect")) {
            addServer(sent.getString("name"), thread);
            JSONObject ack = new JSONObject();
            ack.put("type", "response");
            ack.put("event", "connect");
            ack.put("status", "connected");
            ack.put("responseID", "000");
            thread.sendData(ack);
        } else if (event.equalsIgnoreCase("disconnect")) {
            thread.disconnect();
        } else if (event.equalsIgnoreCase("echo" ) && type.equals("request")) {
            JSONObject response = new JSONObject();
            response.put("type", "response");
            response.put("event", "echo");
            response.put("data", sent.getString("data"));
            response.put("responseID", sent.getString("responseID"));
            thread.sendData(response);
        }

    }

}
