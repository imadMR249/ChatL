package com.chatlan.client.controller;

import com.chatlan.client.network.ChatClient;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ChatController {

    @FXML private ListView<String> userList;
    @FXML private TextArea chatArea;
    @FXML private TextField messageField;

    private ChatClient client;
    private String username;

    private final Map<String, PrivateChatController> privateChats = new HashMap<>();

    public void initChat(String username) {
        this.username = username;
        client = new ChatClient("localhost", 5000, username, this::onMessageReceived);
    }

    // 🔥 Réception des messages depuis le serveur
    private void onMessageReceived(String msg) {
        Platform.runLater(() -> {

            // Liste des utilisateurs
            if (msg.startsWith("USERS:")) {
                String[] users = msg.substring(6).split(",");
                userList.getItems().setAll(users);
                return;
            }

            // Message de groupe
            if (msg.startsWith("MSG:")) {
                String[] parts = msg.substring(4).split(":", 2);
                String sender = parts[0];
                String message = parts[1];

                // ❌ ignorer mon propre message (déjà affiché)
                if (sender.equals(username)) return;

                chatArea.appendText(sender + " : " + message + "\n");
                return;
            }

            // Message privé
            if (msg.startsWith("PM:")) {
                String[] parts = msg.substring(3).split(":", 2);
                String sender = parts[0];
                String message = parts[1];

                openPrivateChat(sender).addMessage(sender, message);
            }
        });
    }

    // 📤 envoyer message public
    @FXML
    private void sendMessage() {
        String message = messageField.getText().trim();
        if (message.isEmpty()) return;

        client.sendMessage(message);
        chatArea.appendText("Moi : " + message + "\n");
        messageField.clear();
    }

    // 📩 ouvrir chat privé
    @FXML
    private void openPrivateChat() {
        String user = userList.getSelectionModel().getSelectedItem();
        if (user == null || user.equals(username)) return;

        openPrivateChat(user);
    }

    private PrivateChatController openPrivateChat(String user) {
        try {
            if (privateChats.containsKey(user))
                return privateChats.get(user);

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/chatlan/client/view/private_chat.fxml")
            );
            Stage stage = new Stage();
            stage.setTitle("Discussion avec " + user);
            stage.setScene(new Scene(loader.load()));
            stage.show();

            PrivateChatController controller = loader.getController();
            controller.init(client, user, username);

            privateChats.put(user, controller);
            return controller;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
