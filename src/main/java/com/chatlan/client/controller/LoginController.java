package com.chatlan.client;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Parent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label messageLabel;

    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Veuillez remplir tous les champs");
            return;
        }

        try (Connection conn = DBUtil.getConnection()) {
            String sql = "SELECT * FROM users WHERE username=? AND password=?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                messageLabel.setText("Connexion réussie !");

                // Charger le FXML du chat
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/chatlan/client/view/chat.fxml"));
                Parent root = loader.load();

                // Récupérer le controller du chat et passer le username
                ChatController chatController = loader.getController();
                chatController.initChat(username);

                // Changer la scène
                Stage stage = (Stage) usernameField.getScene().getWindow();
                stage.setScene(new Scene(root, 600, 400));
                stage.setTitle("Chat LAN - " + username);

            } else {
                messageLabel.setText("Nom d'utilisateur ou mot de passe incorrect");
            }

        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Erreur : " + e.getMessage());
        }
    }

    @FXML
    private void handleSignup() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/chatlan/client/view/signup.fxml"));
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(loader.load(), 400, 300));
            stage.setTitle("Chat LAN - Inscription");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
