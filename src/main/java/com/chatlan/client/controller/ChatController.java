package com.chatlan.client;

import com.chatlan.client.network.ChatClient;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;

public class ChatController {

    @FXML private TextArea chatArea;
    @FXML private TextField messageField;
    @FXML private Button sendButton;

    private ChatClient client;
    private String username;

    // Méthode publique appelée depuis LoginController
    public void initChat(String username) {
        this.username = username;
        client = new ChatClient("localhost", 5000, username, this::displayMessage);
        messageField.requestFocus();
    }

    @FXML
    private void sendMessage() {
        String message = messageField.getText().trim();
        if (!message.isEmpty() && client != null) {
            client.sendMessage(message);
            messageField.clear();
        }
    }

    private void displayMessage(String msg) {
        chatArea.appendText(msg + "\n");
    }

    @FXML
    private void handleEnterPressed() {
        sendMessage();
    }
}
