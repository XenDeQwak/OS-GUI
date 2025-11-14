package com.xen.oslab.modules;

import com.xen.oslab.utils.SettingsNavUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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

        Label title = new Label("Device Settings");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #ffffff;");

        Label display = new Label("Display");
        display.setStyle("-fx-text-fill: white;");
        Label storage = new Label("Storage");
        storage.setStyle("-fx-text-fill: white;");
        Label battery = new Label("Battery");
        battery.setStyle("-fx-text-fill: white;");
        Label sound = new Label("Sound");
        sound.setStyle("-fx-text-fill: white;");

        panel.getChildren().addAll(title, display, storage, battery, sound);

        contentArea.getChildren().clear();
        contentArea.getChildren().add(panel);
    }

    private void showNetworkSettings() {
        SettingsNavUtils.setActiveButton(networkBtn, deviceBtn, networkBtn, personalizeBtn);

        VBox panel = new VBox(15);
        panel.setPadding(new Insets(20));

        Label title = new Label("Network Settings");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #ffffff;");

        Label wifi = new Label("Wi-Fi");
        wifi.setStyle("-fx-text-fill: white;");
        Label ethernet = new Label("Ethernet");
        ethernet.setStyle("-fx-text-fill: white;");
        Label proxy = new Label("Proxy");
        proxy.setStyle("-fx-text-fill: white;");

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

        Label theme = new Label("Theme");
        theme.setStyle("-fx-text-fill: white;");
        Label wallpaper = new Label("Wallpaper");
        wallpaper.setStyle("-fx-text-fill: white;");
        Label colors = new Label("Colors");
        colors.setStyle("-fx-text-fill: white;");

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