package com.chatlan.server;

import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {

    private final Socket socket;       // 'final' conseillé
    private final String username;     // 'final' conseillé
    private BufferedReader reader;
    private PrintWriter writer;

    public ClientHandler(Socket socket, String username) {
        this.socket = socket;
        this.username = username;

        try {
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream(), true);

            // Message de connexion automatique
            ChatServer.broadcast("🟢 " + username + " connecté");
            ChatServer.listUsers();

        } catch (IOException e) {
            System.err.println("Erreur lors de l'initialisation du client : " + e.getMessage());
        }
    }

    @Override
    public void run() {
        String message;
        try {
            while ((message = reader.readLine()) != null) {
                if (!message.trim().isEmpty()) {
                    ChatServer.broadcast(username + " : " + message);
                }
            }
        } catch (IOException e) {
            System.err.println("Erreur lecture message client : " + e.getMessage());
        } finally {
            closeConnection();
        }
    }

    public void sendMessage(String message) {
        writer.println(message);
    }

    public String getUsername() {
        return username;
    }

    private void closeConnection() {
        try {
            if (reader != null) reader.close();
            if (writer != null) writer.close();
            if (socket != null && !socket.isClosed()) socket.close();
            ChatServer.removeClient(this);
            ChatServer.broadcast("🔴 " + username + " déconnecté");
            ChatServer.listUsers();
        } catch (IOException e) {
            System.err.println("Erreur fermeture connexion : " + e.getMessage());
        }
    }
}
