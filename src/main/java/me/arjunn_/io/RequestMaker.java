package me.arjunn_.io;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

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
                request.put("type", "request");
                request.put("event", "echo");
                request.put("data", send);
                request.put("responseID", UUID.randomUUID().toString());
                System.out.println(request.getString("responseID"));

                try {
                    System.out.println("Sending messages to " + Server.servers.keySet().toString());
                    for (String serverName : Server.servers.keySet()) {
                        long start = System.currentTimeMillis();
                        System.out.println("SENDING TO " + serverName);
                        String response = server.getStringFromClient(serverName, request).getResponse();
                        System.out.println(response);
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
