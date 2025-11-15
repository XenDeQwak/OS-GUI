package com.xen.oslab.modules;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.xen.oslab.managers.FolderManager;
import com.xen.oslab.managers.LoginManager;
import com.xen.oslab.managers.SettingsManager;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.application.Platform;

public class Taskbar {
    private HBox bar = new HBox();
    private HBox windowSection = new HBox(5);
    private final LoginManager loginManager;
    private final FolderManager folderManager;
    private final SettingsManager settingsManager;

    public Taskbar(LoginManager loginManager, FolderManager folderManager, Runnable onSettings, Runnable onPower, SettingsManager settingsManager) {
        this.loginManager = loginManager;
        this.folderManager = folderManager;
        this.settingsManager = settingsManager;

        windowSection.setAlignment(Pos.CENTER);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        bar.getChildren().addAll(addPowerBtn(loginManager), addCLIButton(), addExplorerButton(), windowSection, spacer, addSettingsBtn(onSettings), addClock());
    }

    public void registerWindow(Stage stage, String title, String iconPath) {
        ImageView icon = new ImageView(new Image(getClass().getResource(iconPath).toExternalForm()));
        icon.setFitWidth(24);
        icon.setFitHeight(24);

        Button btn = new Button();
        btn.setGraphic(icon);
        btn.setStyle("""
            -fx-background-color: #3a3a3a;
            -fx-background-radius: 5;
        """);
        btn.setCursor(Cursor.HAND);

        btn.setOnAction(e -> {
            stage.toFront();
            stage.requestFocus();
        });

        windowSection.getChildren().add(btn);

        stage.setOnCloseRequest(e -> windowSection.getChildren().remove(btn));
        
    }

    private Button addSettingsBtn(Runnable onSettings) {
        Button settingsBtn = new Button();
        ImageView settingsIcon = new ImageView(new Image(getClass().getResource("/com/xen/oslab/icons/settings.png").toExternalForm()));
        settingsIcon.setFitWidth(24);
        settingsIcon.setFitHeight(24);
        settingsBtn.setGraphic(settingsIcon);
        settingsBtn.setStyle("-fx-background-color: transparent;");
        settingsBtn.setCursor(Cursor.HAND);

        settingsBtn.setOnAction(e -> {
        Stage s = settingsManager.openSettings();
        registerWindow(s, "Settings", "/com/xen/oslab/icons/settings.png");
        });

        return settingsBtn;
    }


    private Button addPowerBtn(LoginManager loginManager) {
        Button powerBtn = new Button();
        ImageView powerIcon = new ImageView(new Image(getClass().getResource("/com/xen/oslab/icons/power.png").toExternalForm()));
        powerIcon.setFitWidth(24);
        powerIcon.setFitHeight(24);
        powerBtn.setGraphic(powerIcon);
        powerBtn.setStyle("-fx-background-color: transparent;");
        powerBtn.setCursor(Cursor.HAND);

        ContextMenu powerMenu = new ContextMenu();

        MenuItem signOff = new MenuItem("Sign Off");
        signOff.setOnAction(e -> {
            Stage primaryStage = (Stage) powerBtn.getScene().getWindow();
            loginManager.setStage(primaryStage);
            loginManager.handleSignOff();
        });

        MenuItem shutdown = new MenuItem("Shutdown");
        shutdown.setOnAction(e -> Platform.exit());

        powerMenu.getItems().addAll(signOff, shutdown);

        powerBtn.setOnAction(e -> powerMenu.show(powerBtn, Side.BOTTOM, 0, 5));

        return powerBtn;
    }

    private VBox addClock() {
        Label timeLabel = new Label();
        Label dateLabel = new Label();

        timeLabel.setStyle("-fx-text-fill: white; -fx-font-size: 11px; -fx-padding: 2 10 0 10;");
        dateLabel.setStyle("-fx-text-fill: white; -fx-font-size: 11px; -fx-padding: 0 10 2 10;");

        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("EEE, MMM d yyyy");

        Timeline clock = new Timeline(
            new KeyFrame(Duration.ZERO, e -> {
                LocalDateTime now = LocalDateTime.now();
                timeLabel.setText(now.format(timeFormatter));
                dateLabel.setText(now.format(dateFormatter));
            }),
            new KeyFrame(Duration.seconds(1))
        );
        clock.setCycleCount(Timeline.INDEFINITE);
        clock.play();

        VBox clockBox = new VBox(timeLabel, dateLabel);
        clockBox.setAlignment(Pos.CENTER_RIGHT);
        return clockBox;
    }


    private Button addCLIButton() {
        Button cliBtn = new Button();
        cliBtn.setText("💻 Terminal");
        cliBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-padding: 10px 0 0 0;");
        cliBtn.setCursor(Cursor.HAND);
        cliBtn.setOnAction(e -> openCLI());

        HBox.setMargin(cliBtn, new Insets(0, 0, 0, 10));

        return cliBtn;
    }

    private Button addExplorerButton() {
        Button feBtn = new Button();
        feBtn.setText("📁 File Explorer");
        feBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-padding: 10px 0 0 0;");
        feBtn.setCursor(Cursor.HAND);
        feBtn.setOnAction (e -> openExplorer());

        HBox.setMargin(feBtn, new Insets(0, 0, 0, 10));
        return feBtn;
        
    }

    private void openCLI() {
        CLIWindow cli = new CLIWindow();
        Stage stage = new Stage();
        stage.setScene(new Scene(cli.getNode(), 600, 400));
        stage.setTitle("Terminal");
        stage.show();

        registerWindow(stage, "Terminal", "/com/xen/oslab/icons/terminal.png");
    }


    private void openExplorer() {
        FileExplorer explorer = new FileExplorer(folderManager);
        Stage stage = new Stage();
        stage.setScene(new Scene(explorer.getNode(), 800, 500));
        stage.setTitle("File Explorer");
        stage.show();

        registerWindow(stage, "Explorer", "/com/xen/oslab/icons/folder.png");
    }


    public HBox getNode() {
        return bar;
    }
}

