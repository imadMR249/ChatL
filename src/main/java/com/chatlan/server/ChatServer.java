package com.chatlan.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class ChatServer {

    private static final int PORT = 5000;

    // Liste des clients connectés
    private static final Set<ClientHandler> clients =
            Collections.synchronizedSet(new HashSet<>());

    public static void main(String[] args) {
        System.out.println("🟢 Serveur démarré sur le port " + PORT);

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket socket = serverSocket.accept();
                new ClientHandler(socket).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Ajouter un client et envoyer la liste mise à jour
    public static void addClient(ClientHandler client) {
        clients.add(client);
        broadcast("JOIN:" + client.getUsername());
        sendUserList();
    }

    // Supprimer un client
    public static void removeClient(ClientHandler client) {
        clients.remove(client);
        broadcast("LEFT:" + client.getUsername());
        sendUserList();
    }

    // Diffuser un message de groupe
    public static void broadcast(String message) {
        for (ClientHandler client : clients) {
            client.send(message);
        }
    }

    // Envoyer un message privé
    public static void sendPrivate(String to, String from, String message) {
        for (ClientHandler client : clients) {
            if (client.getUsername().equals(to)) {
                client.send("PM:" + from + ":" + message);
                break;
            }
        }
    }

    // Mettre à jour la liste des utilisateurs connectés
    public static void sendUserList() {
        String users = clients.stream()
                .map(ClientHandler::getUsername)
                .reduce((a, b) -> a + "," + b)
                .orElse("");
        broadcast("USERS:" + users);
    }
}
