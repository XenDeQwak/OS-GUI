package com.xen.oslab.modules;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.util.Duration;

public class Taskbar {
    private HBox bar = new HBox();

    public Taskbar(Runnable onSettings, Runnable onPower) {
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button settingsBtn = new Button();
        ImageView settingsIcon = new ImageView(new Image(getClass().getResource("/com/xen/oslab/icons/settings.png").toExternalForm()));
        settingsIcon.setFitWidth(24);
        settingsIcon.setFitHeight(24);
        settingsBtn.setGraphic(settingsIcon);
        settingsBtn.setStyle("-fx-background-color: transparent;");
        settingsBtn.setOnAction(e -> onSettings.run());

        Button powerBtn = new Button();
        ImageView powerIcon = new ImageView(new Image(getClass().getResource("/com/xen/oslab/icons/power.png").toExternalForm()));
        powerIcon.setFitWidth(24);
        powerIcon.setFitHeight(24);
        powerBtn.setGraphic(powerIcon);
        powerBtn.setStyle("-fx-background-color: transparent;");
        powerBtn.setOnAction(e -> onPower.run());

        Label clockLabel = new Label();
        clockLabel.setStyle("-fx-text-fill: white; -fx-font-size: 11px; -fx-padding: 4 10 4 10;");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE, MMM d yyyy - hh:mm a");

        Timeline clock = new Timeline(
            new KeyFrame(Duration.ZERO, e -> clockLabel.setText(LocalDateTime.now().format(formatter))),
            new KeyFrame(Duration.seconds(1))
        );
        clock.setCycleCount(Timeline.INDEFINITE);
        clock.play();

        bar.getChildren().addAll(settingsBtn, powerBtn, spacer, clockLabel);
    }

    public HBox getNode() {
        return bar;
    }
}

