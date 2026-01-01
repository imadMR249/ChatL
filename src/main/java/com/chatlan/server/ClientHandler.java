package com.chatlan.server;

import java.io.*;
import java.net.Socket;

public class ClientHandler extends Thread {

    private final Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private String username;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    public String getUsername() {
        return username;
    }

    public void send(String message) {
        out.println(message);
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            // Lire le nom d'utilisateur envoyé par le client
            username = in.readLine();
            System.out.println("✔ Connexion : " + username);

            ChatServer.addClient(this);

            String message;
            while ((message = in.readLine()) != null) {

                if (message.startsWith("PM:")) {
                    // Format PM:destinataire:message
                    String[] parts = message.split(":", 3);
                    if (parts.length == 3) {
                        ChatServer.sendPrivate(parts[1], username, parts[2]);
                    }
                } else {
                    // Message de groupe
                    ChatServer.broadcast("MSG:" + username + ":" + message);
                }
            }

        } catch (IOException e) {
            System.out.println("❌ Déconnexion : " + username);
        } finally {
            ChatServer.removeClient(this);
            try {
                socket.close();
            } catch (IOException ignored) {}
        }
    }
}
