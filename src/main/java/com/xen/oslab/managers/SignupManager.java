package com.xen.oslab.managers;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.xen.oslab.managers.storage.BackgroundStorageManager;
import com.xen.oslab.managers.storage.UserStorageManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;

public class SignupManager {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private ProgressBar pwdStrengthBar;
    @FXML private Label pwdStrengthLabel;
    @FXML private StackPane desktopPane;
    @FXML private ImageView bgImageView;

    private Stage stage;
    private final BackgroundStorageManager bgStorage = new BackgroundStorageManager();
    private static final Path USERS_FILE = Paths.get("desktop_files/users.json");

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    public void initialize() {
        // Load OS background
        String bgFile = bgStorage.loadBackground();
        if (bgFile != null && !bgFile.isEmpty()) {
            try {
                Image img = new Image(getClass().getResourceAsStream("/com/xen/oslab/backgrounds/" + bgFile));
                bgImageView.setImage(img);
                bgImageView.fitWidthProperty().bind(desktopPane.widthProperty());
                bgImageView.fitHeightProperty().bind(desktopPane.heightProperty());
                bgImageView.setOpacity(0.45);
            } catch (Exception e) {
                System.out.println("Failed to load background: " + bgFile);
            }
        }

        // Password strength listener
        passwordField.textProperty().addListener((obs, oldVal, newVal) -> updatePasswordStrength(newVal));

        styleField(usernameField);
        styleField(passwordField);
        styleField(confirmPasswordField);
    }

    private void styleField(TextField field) {
        field.setStyle("""
            -fx-background-color: rgba(255,255,255,0.1);
            -fx-text-fill: white;
            -fx-prompt-text-fill: #b0b0b0;
            -fx-background-radius: 10;
            -fx-border-radius: 10;
            -fx-border-color: transparent;
            -fx-padding: 6;
            """);
        field.focusedProperty().addListener((obs, old, focused) -> {
            if (focused) {
                field.setStyle("""
                    -fx-background-color: rgba(255,255,255,0.15);
                    -fx-text-fill: white;
                    -fx-prompt-text-fill: #b0b0b0;
                    -fx-background-radius: 10;
                    -fx-border-radius: 10;
                    -fx-border-color: #2893ff;
                    -fx-padding: 6;
                    """);
            } else {
                field.setStyle("""
                    -fx-background-color: rgba(255,255,255,0.1);
                    -fx-text-fill: white;
                    -fx-prompt-text-fill: #b0b0b0;
                    -fx-background-radius: 10;
                    -fx-border-radius: 10;
                    -fx-border-color: transparent;
                    -fx-padding: 6;
                    """);
            }
        });
    }

    @FXML
    private void handleSignUp() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        String confirm = confirmPasswordField.getText();

        if (username.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
            showError("Please fill in all fields.");
            return;
        }

        if (!password.equals(confirm)) {
            showError("Passwords do not match.");
            return;
        }

        if (password.length() < 4) {
            showError("Password must be at least 4 characters long.");
            return;
        }

        try {
            JsonObject users = loadUsers();

            if (users.has(username)) {
                showError("Username already exists.");
                return;
            }

            users.addProperty(username, hashPassword(password));

            Files.createDirectories(USERS_FILE.getParent());
            try (FileWriter writer = new FileWriter(USERS_FILE.toFile())) {
                writer.write(users.toString());
            }

            // Refresh in-memory user storage
            UserStorageManager.reloadUsers();

            showInfo("Account created successfully!");
            goToLogin();

        } catch (Exception e) {
            e.printStackTrace();
            showError("Failed to save new user.");
        }
    }

    private JsonObject loadUsers() {
        if (!Files.exists(USERS_FILE)) return new JsonObject();
        try {
            return JsonParser.parseString(Files.readString(USERS_FILE)).getAsJsonObject();
        } catch (IOException e) {
            e.printStackTrace();
            return new JsonObject();
        }
    }

    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    private void goToLogin() {
        Platform.runLater(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/xen/oslab/log-in.fxml"));
                Parent root = loader.load();
                LoginManager controller = loader.getController();
                controller.setStage(stage);

                stage.setScene(new Scene(root));
                stage.setTitle("Login");
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
                showError("Failed to return to login screen.");
            }
        });
    }

    private void updatePasswordStrength(String pwd) {
        int score = getStrengthScore(pwd);
        double progress = score / 4.0;
        pwdStrengthBar.setProgress(progress);

        switch (score) {
            case 0, 1 -> {
                pwdStrengthLabel.setText("Weak");
                pwdStrengthLabel.setStyle("-fx-text-fill: #ff5c5c; -fx-font-weight: bold;");
                pwdStrengthBar.setStyle("-fx-accent: #ff5c5c;");
            }
            case 2, 3 -> {
                pwdStrengthLabel.setText("Moderate");
                pwdStrengthLabel.setStyle("-fx-text-fill: #ffb74d; -fx-font-weight: bold;");
                pwdStrengthBar.setStyle("-fx-accent: #ffb74d;");
            }
            case 4 -> {
                pwdStrengthLabel.setText("Strong");
                pwdStrengthLabel.setStyle("-fx-text-fill: #61e786; -fx-font-weight: bold;");
                pwdStrengthBar.setStyle("-fx-accent: #61e786;");
            }
            default -> {
                pwdStrengthLabel.setText("");
                pwdStrengthBar.setStyle("");
            }
        }
    }

    private int getStrengthScore(String pwd) {
        int score = 0;
        if (pwd == null || pwd.isEmpty()) return 0;
        if (pwd.length() >= 8) score++;
        if (pwd.matches(".*[A-Z].*") && pwd.matches(".*[a-z].*")) score++;
        if (pwd.matches(".*[0-9].*")) score++;
        if (pwd.matches(".*[!@#$%^&*(),.?\":{}|<>].*")) score++;
        return score;
    }

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR, msg);
        alert.initOwner(stage);
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    private void showInfo(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, msg);
        alert.initOwner(stage);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}
