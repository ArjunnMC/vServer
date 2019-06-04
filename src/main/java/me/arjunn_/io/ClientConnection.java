package me.arjunn_.io;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.UUID;

public class ClientConnection extends Thread {

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    public ClientConnection(Socket clientSocket) {
        this.socket = clientSocket;
    }

    public void run() {
        System.out.println("New ClientConnection initialized for " + socket.getInetAddress() + ":" + socket.getPort());
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

    public Request<JSONObject> sendRequest(JSONObject request) {

        Request<JSONObject> response = new Request<>();
        request.put("type", "request");
        request.put("responseID", UUID.randomUUID().toString());
        Server.futuresToResolve.put(request.getString("responseID"), response);

        sendData(request);

        return response;

    }

    public void sendResponse(JSONObject received, JSONObject response) {

        response.put("type", "response");
        response.put("responseID", received.getString("responseID"));

        sendData(response);

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
