package com.xen.oslab.modules;

import com.xen.oslab.utils.SettingsNavUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class SettingsWindow {
    private Stage stage;
    private StackPane contentArea;
    private Button deviceBtn, networkBtn, personalizeBtn;
    public static boolean wifiConnected = true;
    private final java.util.List<String[]> deviceListData = new java.util.ArrayList<>();
    private String selectedNetwork = "Home_WiFi_5G";
    public static String currentIP = "192.168.1.14";


    public SettingsWindow() {
        stage = new Stage();
        stage.setTitle("Settings");
        stage.setWidth(800);
        stage.setHeight(800);

        deviceListData.add(new String[]{"USB Flash Drive", "Storage", "Enabled"});
        deviceListData.add(new String[]{"Bluetooth Mouse", "Input", "Enabled"});
        deviceListData.add(new String[]{"Wireless Keyboard", "Input", "Disabled"});
        deviceListData.add(new String[]{"Printer HP 2055", "Peripheral", "Enabled"});
        deviceListData.add(new String[]{"Webcam 1080p", "Camera", "Enabled"});

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

        Label devices = new Label("Devices");
        devices.setStyle(basicStyle);
        devices.setOnMouseClicked(e -> showDevicesManager());
        devices.setMaxWidth(Double.MAX_VALUE);
        panel.getChildren().add(devices);
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
    
    private void showDevicesManager() {
    VBox panel = new VBox(20);
    panel.setPadding(new Insets(20));

    Label title = new Label("Connected Devices");
    title.setStyle("-fx-font-size: 50px; -fx-font-weight: bold; -fx-text-fill: white;");

    VBox deviceList = new VBox(15);

    for (String[] dev : deviceListData) {
        String devName = dev[0];
        String devType = dev[1];
        String devStatus = dev[2];

        Button btn = new Button("🖥  " + devName + "  —  " + devType + "  (" + devStatus + ")");
        btn.setPrefWidth(500);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setStyle("""
            -fx-background-color: #3a3a3a;
            -fx-text-fill: white;
            -fx-font-size: 20;
            -fx-padding: 10;
            -fx-background-radius: 8;
        """);

        btn.setOnAction(evt -> showDeviceDetails(devName, devType, devStatus));

        deviceList.getChildren().add(btn);
    }

    ScrollPane scroll = new ScrollPane(deviceList);
    scroll.setFitToWidth(true);
    scroll.setStyle("-fx-background: #505050; -fx-border-color: transparent;");

    panel.getChildren().addAll(title, scroll);

    contentArea.getChildren().clear();
    contentArea.getChildren().add(panel);
}


    private void showDeviceDetails(String name, String type, String status) {
        VBox panel = new VBox(20);
        panel.setPadding(new Insets(20));

        Label title = new Label(name);
        title.setStyle("-fx-font-size: 45px; -fx-font-weight: bold; -fx-text-fill: white;");

        Label typeLbl = new Label("Type: " + type);
        typeLbl.setStyle("-fx-text-fill: white; -fx-font-size: 24px;");

        Label statusLbl = new Label("Status: " + status);
        statusLbl.setStyle("-fx-text-fill: white; -fx-font-size: 24px;");

        Button toggleBtn = new Button(status.equals("Enabled") ? "Disable" : "Enable");
        toggleBtn.setStyle("""
            -fx-background-color: #3b82f6;
            -fx-text-fill: white;
            -fx-font-size: 20;
            -fx-padding: 10 20;
        """);

        toggleBtn.setOnAction(e -> {
            boolean isEnabled = statusLbl.getText().contains("Enabled");
            statusLbl.setText("Status: " + (isEnabled ? "Disabled" : "Enabled"));
            toggleBtn.setText(isEnabled ? "Enable" : "Disable");
        });

        Button removeBtn = new Button("Remove Device");
        removeBtn.setStyle("""
            -fx-background-color: #d9534f;
            -fx-text-fill: white;
            -fx-font-size: 20;
            -fx-padding: 10 20;
        """);

        removeBtn.setOnAction(e -> {
        deviceListData.removeIf(d -> d[0].equals(name));
        showDevicesManager();
        });

        panel.getChildren().addAll(title, typeLbl, statusLbl, toggleBtn, removeBtn);

        contentArea.getChildren().clear();
        contentArea.getChildren().add(panel);
    }

    private void showNetworkSettings() {
        SettingsNavUtils.setActiveButton(networkBtn, deviceBtn, networkBtn, personalizeBtn);

        VBox panel = new VBox(20);
        panel.setPadding(new Insets(20));

        Label title = new Label("Network Settings");
        title.setStyle("-fx-font-size: 50px; -fx-font-weight: bold; -fx-text-fill: white;");

        VBox wifiSection = new VBox(10);
        Label wifiTitle = new Label("Wi-Fi");
        wifiTitle.setStyle("-fx-font-size: 28px; -fx-text-fill: white;");

        Label wifiStatus = new Label();
        updateWifiStatusLabel(wifiStatus);

        Button toggleWifiBtn = new Button(SettingsWindow.wifiConnected ? "Turn Off" : "Turn On");
        toggleWifiBtn.setStyle("""
            -fx-background-color: #3b82f6;
            -fx-text-fill: white;
            -fx-font-size: 18;
            -fx-padding: 10 20;
        """);
        toggleWifiBtn.setOnAction(e -> {
        SettingsWindow.wifiConnected = !SettingsWindow.wifiConnected;
        toggleWifiBtn.setText(SettingsWindow.wifiConnected ? "Turn Off" : "Turn On");
        updateWifiStatusLabel(wifiStatus);
      });


        Label nearbyTitle = new Label("Available Networks");
        nearbyTitle.setStyle("-fx-font-size: 24px; -fx-text-fill: white;");

        VBox networksList = new VBox(10);
        String[] fakeNetworks = {
                "Home_WiFi_5G",
                "CoffeeShop_Free",
                "LibraryNet",
                "XEN-OS-Dev-AP"
        };

        for (String network : fakeNetworks) {
            Button networkBtn = new Button("📶  " + network);
            networkBtn.setPrefWidth(300);
            networkBtn.setAlignment(Pos.CENTER_LEFT);

            networkBtn.setStyle(getNetworkButtonStyle(network.equals(selectedNetwork)));

            networkBtn.setOnAction(e -> {
                selectedNetwork = network;
                refreshNetworkList(networksList, fakeNetworks);
                updateWifiStatusLabel(wifiStatus);
            });

            networksList.getChildren().add(networkBtn);
        }

        wifiSection.getChildren().addAll(wifiTitle, wifiStatus, toggleWifiBtn, nearbyTitle, networksList);

        VBox ipSection = new VBox(10);
        Label ipTitle = new Label("IP Address");
        ipTitle.setStyle("-fx-font-size: 28px; -fx-text-fill: white;");
        TextField ipField = new TextField("192.168.1.14");
        ipField.textProperty().addListener((obs, oldVal, newVal) -> {
        currentIP = newVal;
     });

        styleNetworkField(ipField);
        ipSection.getChildren().addAll(ipTitle, ipField);

        VBox dnsSection = new VBox(10);
        Label dnsTitle = new Label("DNS Server");
        dnsTitle.setStyle("-fx-font-size: 28px; -fx-text-fill: white;");
        TextField dnsField = new TextField("8.8.8.8");
        styleNetworkField(dnsField);
        dnsSection.getChildren().addAll(dnsTitle, dnsField);

        VBox gatewaySection = new VBox(10);
        Label gatewayTitle = new Label("Gateway");
        gatewayTitle.setStyle("-fx-font-size: 28px; -fx-text-fill: white;");
        TextField gatewayField = new TextField("192.168.1.1");
        styleNetworkField(gatewayField);
        gatewaySection.getChildren().addAll(gatewayTitle, gatewayField);

        panel.getChildren().addAll(
                title,
                wifiSection,
                ipSection,
                dnsSection,
                gatewaySection
        );

        ScrollPane scroll = new ScrollPane(panel);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: #505050; -fx-border-color: transparent;");

        contentArea.getChildren().clear();
        contentArea.getChildren().add(scroll);
    }

    private void refreshNetworkList(VBox networksList, String[] networks) {
        networksList.getChildren().clear();
        for (String net : networks) {
            Button btn = new Button("📶  " + net);
            btn.setPrefWidth(300);
            btn.setAlignment(Pos.CENTER_LEFT);
            btn.setStyle(getNetworkButtonStyle(net.equals(selectedNetwork)));
            btn.setOnAction(e -> {
                selectedNetwork = net;
                refreshNetworkList(networksList, networks);
            });
            networksList.getChildren().add(btn);
        }
    }

    private String getNetworkButtonStyle(boolean selected) {
        if (selected) {
            return """
                -fx-background-color: #1e90ff;
                -fx-text-fill: white;
                -fx-font-size: 20;
                -fx-padding: 10;
                -fx-background-radius: 8;
                -fx-border-radius: 8;
            """;
        }
        return """
            -fx-background-color: #3a3a3a;
            -fx-text-fill: white;
            -fx-font-size: 20;
            -fx-padding: 10;
            -fx-background-radius: 8;
            -fx-border-radius: 8;
        """;
    }

    private void updateWifiStatusLabel(Label label) {
        String icon = SettingsWindow.wifiConnected
        ? "📶 (Connected)"
        : "📡 (Disconnected)";
        label.setText("Status: " + icon + " — " + selectedNetwork);
        label.setStyle("-fx-text-fill: white; -fx-font-size: 22px;");
    }

    private void styleNetworkField(TextField field) {
        field.setStyle("""
            -fx-background-color: #3a3a3a;
            -fx-text-fill: white;
            -fx-font-size: 20;
            -fx-padding: 8;
            -fx-background-radius: 8;
            -fx-border-radius: 8;
            -fx-border-color: #555;
        """);
        field.setPrefWidth(300);
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