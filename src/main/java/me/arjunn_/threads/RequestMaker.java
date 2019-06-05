package me.arjunn_.threads;

import me.arjunn_.Server;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class RequestMaker extends Thread {

    static BufferedReader in;
    Server server;

    public RequestMaker(Server server) {
        in = new BufferedReader(new InputStreamReader(System.in));
        this.server = server;
    }

    @Override
    public void run() {

        while (true) {
            try {
                String send = in.readLine();
                System.out.println("Got input " + send);
                JSONObject request = new JSONObject();
                request.put("event", "echo");
                request.put("data", send);

                try {
                    System.out.println("Sending messages to " + Server.servers.keySet().toString());
                    for (String serverName : Server.servers.keySet()) {
                        long start = System.currentTimeMillis();
                        System.out.println("SENDING TO " + serverName);
                        JSONObject response = Server.getClientConnection(serverName).sendRequest(request).getResponse();
                        System.out.println("Response time: " + (System.currentTimeMillis() - start) + " milliseconds.");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
