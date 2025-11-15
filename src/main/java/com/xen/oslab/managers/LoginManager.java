package com.xen.oslab.managers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.xen.oslab.OSController;
import com.xen.oslab.managers.storage.BackgroundStorageManager;
import com.xen.oslab.managers.storage.UserStorageManager;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class LoginManager {

    @FXML private PasswordField passwordField;
    @FXML private Label userLabel;
    @FXML private StackPane desktopPane;
    @FXML private ImageView bgImageView;
    @FXML private ListView<String> userListView;

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
            if (Files.exists(USERS_FILE)) {
                String content = Files.readString(USERS_FILE);
                JsonObject obj = JsonParser.parseString(content).getAsJsonObject();
                userListView.getItems().clear();
                obj.keySet().forEach(userListView.getItems()::add);

                if (!obj.keySet().isEmpty()) {
                    userListView.getSelectionModel().select(0);
                    currentUser = userListView.getSelectionModel().getSelectedItem();
                    userLabel.setText("Welcome, " + currentUser);
                } else {
                    userLabel.setText("No users yet");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        userListView.getSelectionModel().selectedItemProperty().addListener((obs, oldUser, newUser) -> {
            if (newUser != null) {
                currentUser = newUser;
                userLabel.setText("Welcome, " + currentUser);
            }
        });
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

   @FXML
    private void handleSignUp() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/xen/oslab/sign-up.fxml"));
            Parent root = loader.load();

            SignupManager controller = loader.getController();
            controller.setStage(stage);

            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Sign Up");
            stage.centerOnScreen();
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showError("Failed to open sign-up.");
        }
    }
    public void handleSignOff() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/xen/oslab/log-in.fxml"));
            Parent loginRoot = loader.load();

            LoginManager loginController = loader.getController();
            loginController.setStage(stage);

            Scene loginScene = new Scene(loginRoot);
            stage.setScene(loginScene);
            stage.setTitle("Log In");
            stage.setMaximized(true);
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showError("Failed to return to log-in screen.");
        }
    }

    private void launchOS() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/xen/oslab/os-view.fxml"));
            Parent root = loader.load();

            OSController controller = loader.getController();
            controller.setStage(stage);

            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setMaximized(true);
            stage.setFullScreen(false);
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

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR, msg);
        alert.initOwner(stage);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}
