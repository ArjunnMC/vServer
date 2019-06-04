package me.arjunn_.io;

import org.json.JSONObject;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class Server {

    protected static HashMap<String, ClientConnection> servers = new HashMap<>();
    public static HashMap<String, Request<JSONObject>> futuresToResolve = new HashMap<>();

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

    public Request<JSONObject> getStringFromClient(String client, JSONObject request) {
        Request<JSONObject> response = new Request<>();
        futuresToResolve.put(request.getString("responseID"), response);

        servers.get(client).sendData(request);

        return response;

    }

    public static ClientConnection getClientConnection(String name) {
        return servers.get(name);
    }

    public static Collection<ClientConnection> getClientConnections() {
        return servers.values();
    }

    public static void handle(JSONObject received, ClientConnection thread) {

        if (!received.has("type")) {
            System.out.println("Received data with no type - ignoring. " + received);
            return;
        }

        String type = received.getString("type");

        if (type.equals("response")) {
            if (!received.has("responseID")) {
                System.out.println("Received response without a responseID - ignoring. " + received);
            } else if (futuresToResolve.containsKey(received.getString("responseID"))) {
                futuresToResolve.get(received.getString("responseID")).setResponse(received);
                futuresToResolve.remove(received.getString("responseID"));
            } else {
                System.out.println("Got a response for a non-existant request! Request either never existed or has timed out.");
            }
            return;
        }

        if (!received.has("event")) {
            System.out.println("Received a request without an event: " + received);
            return;
        }

        // Received a valid request
        String event = received.getString("event");

        // Begin handling based on event name

        if (event.equalsIgnoreCase("connect")) {
            addServer(received.getString("name"), thread);
            JSONObject response = new JSONObject();
            response.put("event", "connect");
            response.put("status", "connected");
            thread.sendResponse(received, response);
        } else if (event.equalsIgnoreCase("disconnect")) {
            thread.disconnect();
        } else if (event.equalsIgnoreCase("echo" )) {
            JSONObject response = new JSONObject();
            response.put("event", "echo");
            response.put("data", received.getString("data"));
            thread.sendResponse(received, response);
        }

    }

}
