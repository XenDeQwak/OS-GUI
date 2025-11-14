package com.xen.oslab.modules;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Taskbar {
    private HBox bar = new HBox();

    public Taskbar(Runnable onSettings, Runnable onPower) {
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        bar.getChildren().addAll(addSettingsBtn(onSettings), addPowerBtn(onPower), addCLIButton(), spacer, addClock());
    }

    private Button addSettingsBtn(Runnable onSettings) {
        Button settingsBtn = new Button();
        ImageView settingsIcon = new ImageView(new Image(getClass().getResource("/com/xen/oslab/icons/settings.png").toExternalForm()));
        settingsIcon.setFitWidth(24);
        settingsIcon.setFitHeight(24);
        settingsBtn.setGraphic(settingsIcon);
        settingsBtn.setStyle("-fx-background-color: transparent;");
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
        powerBtn.setOnAction(e -> onPower.run());

        return powerBtn;
    }

    private Label addClock() {
        Label clockLabel = new Label();
        clockLabel.setStyle("-fx-text-fill: white; -fx-font-size: 11px; -fx-padding: 4 10 4 10;");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE, MMM d yyyy - hh:mm a");

        Timeline clock = new Timeline(
            new KeyFrame(Duration.ZERO, e -> clockLabel.setText(LocalDateTime.now().format(formatter))),
            new KeyFrame(Duration.seconds(1))
        );
        clock.setCycleCount(Timeline.INDEFINITE);
        clock.play();

        return clockLabel;
    }

    public Button addCLIButton() {
        // Image icon = new Image(getClass().getResource("/com/xen/oslab/icons/terminal.png").toExternalForm());
        // ImageView iconView = new ImageView(icon);
        // iconView.setFitWidth(24);
        // iconView.setFitHeight(24);

        Button cliBtn = new Button();
        //cliBtn.setGraphic(iconView);
        cliBtn.setText("CLI");
        //cliBtn.setStyle("-fx-background-color: transparent;");

        cliBtn.setOnAction(e -> openCLI());

        return cliBtn;
    }

    private void openCLI() {
        CLIWindow cli = new CLIWindow();
        Stage stage = new Stage();
        stage.setScene(new Scene(cli.getNode(), 600, 400));
        stage.setTitle("Terminal");
        stage.show();
    }


    public HBox getNode() {
        return bar;
    }
}

