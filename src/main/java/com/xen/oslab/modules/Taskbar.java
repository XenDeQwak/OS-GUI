package com.xen.oslab.modules;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.xen.oslab.managers.FolderManager;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
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

public class Taskbar {
    private HBox bar = new HBox();
    private final FolderManager folderManager;

    public Taskbar(FolderManager folderManager, Runnable onSettings, Runnable onPower) {
        this.folderManager = folderManager;
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        bar.getChildren().addAll(addPowerBtn(onPower), addCLIButton(), addExplorerButton(), spacer, addSettingsBtn(onSettings), addClock());
    }

    private Button addSettingsBtn(Runnable onSettings) {
        Button settingsBtn = new Button();
        ImageView settingsIcon = new ImageView(new Image(getClass().getResource("/com/xen/oslab/icons/settings.png").toExternalForm()));
        settingsIcon.setFitWidth(24);
        settingsIcon.setFitHeight(24);
        settingsBtn.setGraphic(settingsIcon);
        settingsBtn.setStyle("-fx-background-color: transparent;");
        settingsBtn.setCursor(Cursor.HAND);
        settingsBtn.setOnAction(e -> onSettings.run());

        return settingsBtn;
    }

    private Button addPowerBtn(Runnable onPower) {
        Button powerBtn = new Button();
        ImageView powerIcon = new ImageView(new Image(getClass().getResource("/com/xen/oslab/icons/power.png").toExternalForm()));
        powerIcon.setFitWidth(24);
        powerIcon.setFitHeight(24);
        powerBtn.setGraphic(powerIcon);
        powerBtn.setStyle("-fx-background-color: transparent;");
        powerBtn.setCursor(Cursor.HAND);
        powerBtn.setOnAction(e -> onPower.run());


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
    }

    private void openExplorer() {
        FileExplorer explorer = new FileExplorer(folderManager);
        Stage stage = new Stage();
        stage.setScene(new Scene(explorer.getNode(), 800, 500));
        stage.setTitle("File Explorer");
        stage.show();

    }


    public HBox getNode() {
        return bar;
    }
}

