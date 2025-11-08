package com.xen.oslab.managers;

import com.xen.oslab.managers.storage.UserStorageManager;
import com.xen.oslab.managers.storage.BackgroundStorageManager;
import com.google.gson.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class LoginManager {

    @FXML private PasswordField passwordField;
    @FXML private Label userLabel;
    @FXML private StackPane desktopPane;
    @FXML private ImageView bgImageView;

    private Stage stage;
    private String currentUser;
    private int failedAttempts = 0;
    private static final int MAX_ATTEMPTS = 5;

    private final BackgroundStorageManager bgStorage = new BackgroundStorageManager();
    private static final Path USERS_FILE = Paths.get("desktop_files/users.json");

    public void setStage(Stage stage) {
        this.stage = stage;
        if (currentUser != null) {
            userLabel.setText("Welcome, " + currentUser);
        }
    }

    @FXML
    public void initialize() {
        String bgFile = bgStorage.loadBackground();
        if (bgFile != null && !bgFile.isEmpty()) {
            String path = "/com/xen/oslab/backgrounds/" + bgFile;
            try {
                Image img = new Image(getClass().getResourceAsStream(path));
                bgImageView.setImage(img);
                bgImageView.fitWidthProperty().bind(desktopPane.widthProperty());
                bgImageView.fitHeightProperty().bind(desktopPane.heightProperty());
            } catch (Exception e) {
                System.out.println("Failed to load background: " + path);
            }
        }

        try {
            String content = Files.readString(USERS_FILE);
            JsonObject obj = JsonParser.parseString(content).getAsJsonObject();
            if (!obj.keySet().isEmpty()) {
                currentUser = obj.keySet().iterator().next(); 
                userLabel.setText("Welcome, " + currentUser);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

       

        if (passwordField != null) {
            passwordField.setStyle("-fx-font-size: 18;");
            passwordField.setPrefHeight(40);
        }
    }

    @FXML
    private void handleLogin() {
        String password = passwordField.getText().trim();
        if (password.isEmpty()) {
            showError("Please enter a password.");
            return;
        }

        Platform.runLater(() -> {
            if (UserStorageManager.validateUser(currentUser, password)) {
                failedAttempts = 0;
                launchOS();
            } else {
                failedAttempts++;
                checkAttempts();
            }
        });
    }

    private void launchOS() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/xen/oslab/os-view.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);

            stage.setScene(scene);
            stage.sizeToScene();
            stage.setMaximized(true);
            stage.centerOnScreen(); 
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showError("Failed to launch OS.");
        }
    }

    private void checkAttempts() {
        if (failedAttempts >= MAX_ATTEMPTS) {
            showError("Too many failed attempts. Exiting...");
            Platform.exit();
        } else {
            showError("Incorrect password. Attempt " + failedAttempts + " of " + MAX_ATTEMPTS + ".");
        }
    }

    @FXML
    private void handlePower() {
        Platform.exit();
    }

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR, msg);
        alert.initOwner(stage);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}
