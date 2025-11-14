package com.xen.oslab.modules;

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
    private Scene scene; 
    private StackPane contentArea;
    private Button deviceBtn, networkBtn, personalizeBtn;

    public SettingsWindow() {
        stage = new Stage();
        stage.setTitle("Settings");
        stage.setWidth(800);
        stage.setHeight(800);

        BorderPane root = new BorderPane();

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

        deviceBtn = new Button("Device");
        networkBtn = new Button("Network");
        personalizeBtn = new Button("Personalize");

        deviceBtn.setMaxWidth(Double.MAX_VALUE);
        networkBtn.setMaxWidth(Double.MAX_VALUE);
        personalizeBtn.setMaxWidth(Double.MAX_VALUE);

        deviceBtn.setOnAction(e -> showDeviceSettings());

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
        btn.setStyle(
            "-fx-background-color: #3c3c3c;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 14px;" +
            "-fx-border-width: 0;" +
            "-fx-background-radius: 0;"
        );

        btn.setOnMouseEntered(e -> {
            if(!btn.getStyle().contains("#505050")){
                btn.setStyle(btn.getStyle() + "-fx-background-color: #454545;");
            }
        });
        return btn;
    }
    private void highlightButton(Button activeBtn) {
        deviceBtn.setStyle(
            "-fx-background-color: #3c3c3c;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 14px;" +
            "-fx-border-width: 0;" +
            "-fx-background-radius: 0;"
        );
        networkBtn.setStyle(
            "-fx-background-color: #3c3c3c;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 14px;" +
            "-fx-border-width: 0;" +
            "-fx-background-radius: 0;"
        );
        personalizeBtn.setStyle(
            "-fx-background-color: #3c3c3c;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 14px;" +
            "-fx-border-width: 0;" +
            "-fx-background-radius: 0;"
        );
        
        activeBtn.setStyle(
            "-fx-background-color: #505050;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 14px;" +
            "-fx-border-width: 0;" +
            "-fx-background-radius: 0;"
        );
    }
    private void showDeviceSettings() {
        VBox panel = new VBox(15);
        panel.setPadding(new Insets(20));
        
        Label title = new Label("Device Settings");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        
        Label display = new Label("Display");
        Label storage = new Label("Storage");
        Label battery = new Label("Battery");
        Label sound = new Label("Sound");
        
        panel.getChildren().addAll(title, display, storage, battery, sound);
        
        contentArea.getChildren().clear();
        contentArea.getChildren().add(panel);
    }
    
    //network settings here

    //personalization settings here

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
