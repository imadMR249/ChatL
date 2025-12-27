package com.chatlan.client;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class SignupController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label messageLabel;

    @FXML
    private void handleSignup() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        String confirmPassword = confirmPasswordField.getText().trim();

        if (!password.equals(confirmPassword)) {
            messageLabel.setText("Les mots de passe ne correspondent pas !");
            return;
        }

        try (Connection conn = DBUtil.getConnection()) {
            // Vérifier si l'utilisateur existe déjà
            String checkSql = "SELECT * FROM users WHERE username=?";
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setString(1, username);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next()) {
                messageLabel.setText("Nom d'utilisateur déjà utilisé !");
                return;
            }

            // Ajouter l'utilisateur dans la base
            String insertSql = "INSERT INTO users (username, password) VALUES (?, ?)";
            PreparedStatement insertStmt = conn.prepareStatement(insertSql);
            insertStmt.setString(1, username);
            insertStmt.setString(2, password);
            insertStmt.executeUpdate();

            messageLabel.setText("Inscription réussie ! Redirection vers login...");

            // Redirection vers la page de login après inscription
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/chatlan/client/view/login.fxml"));
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(loader.load(), 400, 250));
            stage.setTitle("Chat LAN - Login");

        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Erreur : " + e.getMessage());
        }
    }
}
