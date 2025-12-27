package com.chatlan.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class ChatServer {

    private static final int PORT = 5000;
    public static Set<ClientHandler> clients = Collections.synchronizedSet(new HashSet<>());

    public static void main(String[] args) {
        System.out.println("Serveur Chat LAN démarré sur le port " + PORT);

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket socket = serverSocket.accept();

                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);

                String username = reader.readLine(); // username envoyé par ChatClient
                if (username == null || username.trim().isEmpty()) {
                    writer.println("Erreur : username invalide");
                    socket.close();
                    continue;
                }

                ClientHandler client = new ClientHandler(socket, username);
                clients.add(client);
                new Thread(client).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void broadcast(String message) {
        for (ClientHandler client : clients) {
            client.sendMessage(message);
        }
    }

    public static void removeClient(ClientHandler client) {
        clients.remove(client);
    }

    public static void listUsers() {
        StringBuilder users = new StringBuilder("💻 Utilisateurs connectés : ");
        for (ClientHandler client : clients) {
            users.append(client.getUsername()).append(", ");
        }
        if (users.length() > 25) users.setLength(users.length() - 2);
        broadcast(users.toString());
    }
}
