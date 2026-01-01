package com.chatlan.client.controller;

import com.chatlan.client.network.ChatClient;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class PrivateChatController {

    @FXML private VBox messagesBox;
    @FXML private TextField messageField;
    @FXML private ScrollPane scrollPane;

    private ChatClient client;
    private String chatUser;
    private String myUsername;

    public void init(ChatClient client, String chatUser, String myUsername) {
        this.client = client;
        this.chatUser = chatUser;
        this.myUsername = myUsername;
    }

    @FXML
    private void sendPrivateMessage() {
        String message = messageField.getText().trim();
        if (message.isEmpty()) return;

        client.sendPrivateMessage(chatUser, message);
        addMessage(myUsername, message);
        messageField.clear();
    }

    public void addMessage(String sender, String message) {
        HBox box = new HBox();
        Text text = new Text(message);
        text.setWrappingWidth(250);

        if (sender.equals(myUsername)) {
            box.setAlignment(Pos.CENTER_RIGHT);
            text.setStyle("-fx-background-color:#DCF8C6; -fx-padding:8; -fx-background-radius:10;");
        } else {
            box.setAlignment(Pos.CENTER_LEFT);
            text.setStyle("-fx-background-color:#EAEAEA; -fx-padding:8; -fx-background-radius:10;");
        }

        box.getChildren().add(text);
        messagesBox.getChildren().add(box);

        scrollPane.layout();
        scrollPane.setVvalue(1.0);
    }
}
