package com.chatlan.client.network;

import java.io.*;
import java.net.Socket;
import java.util.function.Consumer;

public class ChatClient {

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    public ChatClient(String serverIp, int port, String username, Consumer<String> onMessage) {
        try {
            socket = new Socket(serverIp, port);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            out.println(username);

            new Thread(() -> {
                try {
                    String msg;
                    while ((msg = in.readLine()) != null) {
                        onMessage.accept(msg);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String msg) {
        out.println(msg);
    }
}
