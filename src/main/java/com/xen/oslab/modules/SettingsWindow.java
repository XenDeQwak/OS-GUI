package com.xen.oslab.modules;

import com.xen.oslab.utils.SettingsNavUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class SettingsWindow {
    private Stage stage;
    private StackPane contentArea;
    private Button deviceBtn, networkBtn, personalizeBtn;

    public SettingsWindow() {
        stage = new Stage();
        stage.setTitle("Settings");
        stage.setWidth(800);
        stage.setHeight(800);


        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #505050");

        VBox sidebar = createSidebar();
        root.setLeft(sidebar);

        contentArea = new StackPane();
        contentArea.setPadding(new Insets(20));
        root.setCenter(contentArea);

        showDeviceSettings();

        Scene scene = new Scene(root);
        stage.setScene(scene);
    }

    private VBox createSidebar() {
        VBox sidebar = new VBox(20);
        sidebar.setPadding(new Insets(10));
        sidebar.setPrefHeight(180);
        sidebar.setMinWidth(180);
        sidebar.setStyle("-fx-background-color: #454545");

        deviceBtn = createNavButton("Device");
        networkBtn = createNavButton("Network");
        personalizeBtn = createNavButton("Personalize");

        deviceBtn.setOnAction(e -> showDeviceSettings());
        networkBtn.setOnAction(e -> showNetworkSettings());
        personalizeBtn.setOnAction(e -> showPersonalizeSettings());

        sidebar.getChildren().addAll(deviceBtn, networkBtn, personalizeBtn);
        return sidebar;
    }

    private Button createNavButton(String text){
        Button btn = new Button(text);
        btn.setPrefWidth(100);
        btn.setPrefHeight(50);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setPadding(new Insets(10,20,10,20));
        btn.setStyle(SettingsNavUtils.getDefaultButtonStyle());

        btn.setOnMouseEntered(e -> {
            if(!btn.getStyle().contains("#505050")){
                btn.setStyle(btn.getStyle() + "-fx-background-color: #454545;");
            }
        });

        btn.setOnMouseExited(e -> {
            if(!btn.getStyle().contains("#505050")){
                btn.setStyle(SettingsNavUtils.getDefaultButtonStyle());
            }
        });

        return btn;
    }

    private void showDeviceSettings() {
        SettingsNavUtils.setActiveButton(deviceBtn, deviceBtn, networkBtn, personalizeBtn);

        VBox panel = new VBox(15);
        panel.setPadding(new Insets(20));
        panel.setStyle("-fx-text-fill: white; -fx-font-size: 25px");

        Label title = new Label("Device Settings");
        title.setStyle("-fx-font-size: 50px; -fx-font-weight: bold; -fx-text-fill: #ffffff;");

        Label display = new Label("Display");
        Label storage = new Label("Storage");
        Label battery = new Label("Battery");
        Label sound = new Label("Sound");

        String basicStyle = "-fx-text-fill: #ffffff; -fx-cursor: hand; -fx-padding: 14px;";

        display.setStyle(basicStyle);
        storage.setStyle(basicStyle);
        battery.setStyle(basicStyle);
        sound.setStyle(basicStyle);

        display.setOnMouseClicked(e -> showDisplaySettings());
        storage.setOnMouseClicked(e -> showStorageSettings());
        battery.setOnMouseClicked(e -> showBatterySettings());
        sound.setOnMouseClicked(e -> showSoundSettings());

        display.setMaxWidth(Double.MAX_VALUE);
        storage.setMaxWidth(Double.MAX_VALUE);
        battery.setMaxWidth(Double.MAX_VALUE);
        sound.setMaxWidth(Double.MAX_VALUE);

        panel.getChildren().addAll(title, display, storage, battery, sound);

        contentArea.getChildren().clear();
        contentArea.getChildren().add(panel);
    }

    private void showDisplaySettings() {
        VBox panel = new VBox(15);
        panel.setPadding(new Insets(20));

        Label title = new Label("Display Settings");
        title.setStyle("-fx-font-size: 50px; -fx-font-weight: bold; -fx-text-fill: white;");

        Label brightness = new Label("Brightness: 50%");
        VBox brightnessSection = new VBox(8);
        brightness.setStyle("-fx-text-fill: white; -fx-font-size: 25px; -fx-font-family: 'Arial'");

        Slider brightnessSlider = new Slider(0, 100, 50);
        brightnessSlider.setShowTickLabels(true);
        brightnessSlider.setShowTickMarks(true);
        brightnessSlider.setMajorTickUnit(25);
        brightnessSlider.setBlockIncrement(5);

        brightnessSlider.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.addPostLayoutPulseListener(() -> {
                    brightnessSlider.lookupAll(".axis").forEach(axis -> {
                        axis.setStyle("-fx-tick-label-fill: white;");
                    });
                });
            }
        });

        brightnessSlider.setStyle("""
            .track {
                -fx-background-color: #1e1e1e;
                -fx-pref-height: 8px;
            }
            .colored-track {
                -fx-background-color: linear-gradient(to right, #00ff88, #00ccff);
                -fx-pref-height: 8px;
            }
            .thumb {
                -fx-background-color: white;
                -fx-effect: dropshadow(three-pass-box, #00ccff, 8, 0.7, 0, 0);
            }
        """);

        brightnessSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            int value = newVal.intValue();
            brightness.setText("Brightness: " + value + "%");
        });

        brightnessSection.getChildren().addAll(brightness, brightnessSlider);

        Label resolution = new Label("Resolution: 1920x1080");
        resolution.setStyle("-fx-text-fill: white; -fx-font-size: 25px; -fx-font-family: 'Arial'");

        panel.getChildren().addAll(title, brightnessSection, resolution);

        contentArea.getChildren().clear();
        contentArea.getChildren().add(panel);
    }

    private void showStorageSettings() {
        VBox panel = new VBox(15);
        panel.setPadding(new Insets(20));

        Label title = new Label("Storage Settings");
        title.setStyle("-fx-font-size: 50px; -fx-font-weight: bold; -fx-text-fill: white;");

        Label space = new Label("Available Space: 256 GB");
        space.setStyle("-fx-text-fill: white; -fx-font-size: 25px; -fx-font-family: 'Arial'");

        Label apps = new Label("Installed apps Space: 25 GB");
        apps.setStyle("-fx-text-fill: white; -fx-font-size: 25px; -fx-font-family: 'Arial'");

        Label others = new Label("Others: 5 GB");
        others.setStyle("-fx-text-fill: white; -fx-font-size: 25px; -fx-font-family: 'Arial'");

        Label photos = new Label("Photos: 5 GB");
        photos.setStyle("-fx-text-fill: white; -fx-font-size: 25px; -fx-font-family: 'Arial'");

        Label videos = new Label("Videos: 35 GB");
        videos.setStyle("-fx-text-fill: white; -fx-font-size: 25px; -fx-font-family: 'Arial'");

        Label temp = new Label("Temporary : 6 GB");
        temp.setStyle("-fx-text-fill: white; -fx-font-size: 25px; -fx-font-family: 'Arial'");

        panel.getChildren().addAll(title, space, apps, others, photos, videos, temp);

        contentArea.getChildren().clear();
        contentArea.getChildren().add(panel);
    }

    private void showBatterySettings() {
        VBox panel = new VBox(15);
        panel.setPadding(new Insets(20));

        Label title = new Label("Battery Settings");
        title.setStyle("-fx-font-size: 50px; -fx-font-weight: bold; -fx-text-fill: white;");

        Label level = new Label("Battery Level: 85%");
        level.setStyle("-fx-text-fill: white; -fx-font-size: 25px; -fx-font-family: 'Arial'");

        Label energy = new Label("Energy Saver");
        energy.setStyle("-fx-text-fill: white; -fx-font-size: 25px; -fx-font-family: 'Arial'");

        Label usage = new Label("Battery Usage");
        usage.setStyle("-fx-text-fill: white; -fx-font-size: 25px; -fx-font-family: 'Arial'");

        panel.getChildren().addAll(title, level, energy, usage);

        contentArea.getChildren().clear();
        contentArea.getChildren().add(panel);
    }

    private void showSoundSettings() {
        VBox panel = new VBox(15);
        panel.setPadding(new Insets(20));

        Label title = new Label("Sound Settings");
        title.setStyle("-fx-font-size: 50px; -fx-font-weight: bold; -fx-text-fill: white;");

        Label volume = new Label("Volume: 70%");
        volume.setStyle("-fx-text-fill: white;-fx-font-size: 25px;");

//        VBox SoundSection = new VBox(8);
        volume.setStyle("-fx-text-fill: white; -fx-font-size: 25px; -fx-font-family: 'Arial'");

        Slider volumeSlider = new Slider(0, 100, 50);
        volumeSlider.setShowTickLabels(true);
        volumeSlider.setShowTickMarks(true);
        volumeSlider.setMajorTickUnit(25);
        volumeSlider.setBlockIncrement(5);
        volumeSlider.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.addPostLayoutPulseListener(() -> {
                    volumeSlider.lookupAll(".axis").forEach(axis -> {
                        axis.setStyle("-fx-tick-label-fill: white;");
                    });
                });
            }
        });
        volumeSlider.setStyle("""
            .track {
                -fx-background-color: #1e1e1e;
                -fx-pref-height: 8px;
            }
            .colored-track {
                -fx-background-color: linear-gradient(to right, #00ff88, #00ccff);
                -fx-pref-height: 8px;
            }
            .thumb {
                -fx-background-color: white;
                -fx-effect: dropshadow(three-pass-box, #00ccff, 8, 0.7, 0, 0);
            }
        """);

        volumeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            int value = newVal.intValue();
            volume.setText("Volume: " + value + "%");
        });

        panel.getChildren().addAll(title, volume, volumeSlider);

        contentArea.getChildren().clear();
        contentArea.getChildren().add(panel);
    }

    private void showNetworkSettings() {
        SettingsNavUtils.setActiveButton(networkBtn, deviceBtn, networkBtn, personalizeBtn);

        VBox panel = new VBox(15);
        panel.setPadding(new Insets(20));
        panel.setStyle("-fx-text-fill: white;");

        Label title = new Label("Network Settings");
        title.setStyle("-fx-font-size: 50px; -fx-font-weight: bold; -fx-text-fill: #ffffff;");

        Label wifi = new Label("Wi-Fi");
        Label ethernet = new Label("Ethernet");
        Label proxy = new Label("Proxy");

        panel.getChildren().addAll(title, wifi, ethernet, proxy);

        contentArea.getChildren().clear();
        contentArea.getChildren().add(panel);
    }

    private void showPersonalizeSettings() {
        SettingsNavUtils.setActiveButton(personalizeBtn, deviceBtn, networkBtn, personalizeBtn);

        VBox panel = new VBox(15);
        panel.setPadding(new Insets(20));

        Label title = new Label("Personalization Settings");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #ffffff;");
        panel.setStyle("-fx-text-fill: white;");

        Label theme = new Label("Theme");
        Label wallpaper = new Label("Wallpaper");
        Label colors = new Label("Colors");

        panel.getChildren().addAll(title, theme, wallpaper, colors);

        contentArea.getChildren().clear();
        contentArea.getChildren().add(panel);
    }

    public void show() {
        stage.show();
    }

    public void close() {
        stage.close();
    }

    public Stage getStage() {
        return stage;
    }
}