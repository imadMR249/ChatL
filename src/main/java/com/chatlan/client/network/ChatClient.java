package com.chatlan.client.network;

import java.io.*;
import java.net.Socket;
import java.util.function.Consumer;

public class ChatClient {

    private String host;
    private int port;
    private String username;
    private Consumer<String> messageHandler;

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    public ChatClient(String host, int port, String username, Consumer<String> messageHandler) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.messageHandler = messageHandler;

        connect();
    }

    private void connect() {
        try {
            socket = new Socket(host, port);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            // Envoyer le nom d'utilisateur au serveur
            out.println(username);

            // Thread pour écouter les messages du serveur
            new Thread(() -> {
                String line;
                try {
                    while ((line = in.readLine()) != null) {
                        if (messageHandler != null) {
                            messageHandler.accept(line);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();

            System.out.println("Client connecté en tant que " + username);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Envoyer un message de groupe
    public void sendMessage(String message) {
        out.println(message);
    }

    // Envoyer un message privé
    public void sendPrivateMessage(String to, String message) {
        out.println("PM:" + to + ":" + message);
    }

    public void disconnect() {
        try {
            if (socket != null) socket.close();
        } catch (IOException ignored) {}
    }
}
