package com.xen.oslab.modules;

import com.xen.oslab.OSController;
import com.xen.oslab.utils.SettingsNavUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class SettingsWindow {
    private Stage stage;
    private StackPane contentArea;
    private Button deviceBtn, networkBtn; //personalizeBtn;
    public static boolean wifiConnected = true;
    private final java.util.List<String[]> deviceListData = new java.util.ArrayList<>();
    private String selectedNetwork = "Home_WiFi_5G";
    public static String currentIP = "192.168.1.14";

    private static final String BG_PRIMARY = "#1a1a1a";
    private static final String BG_SECONDARY = "#242424";
    private static final String BG_TERTIARY = "#2d2d2d";
    private static final String ACCENT_BLUE = "#3b82f6";
    private static final String ACCENT_CYAN = "#06b6d4";
    private static final String TEXT_PRIMARY = "#ffffff";
    private static final String TEXT_SECONDARY = "#a1a1aa";
    private static final String HOVER_BG = "#3a3a3a";

    public SettingsWindow() {
        stage = new Stage();
        stage.setTitle("Settings");
        stage.setWidth(900);
        stage.setHeight(650);

        deviceListData.add(new String[]{"USB Flash Drive", "Storage", "Enabled"});
        deviceListData.add(new String[]{"Bluetooth Mouse", "Input", "Enabled"});
        deviceListData.add(new String[]{"Wireless Keyboard", "Input", "Disabled"});
        deviceListData.add(new String[]{"Printer HP 2055", "Peripheral", "Enabled"});
        deviceListData.add(new String[]{"Webcam 1080p", "Camera", "Enabled"});

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + BG_PRIMARY + ";");

        VBox sidebar = createSidebar();
        root.setLeft(sidebar);

        contentArea = new StackPane();
        contentArea.setPadding(new Insets(30));
        contentArea.setStyle("-fx-background-color: " + BG_PRIMARY + ";");
        root.setCenter(contentArea);

        showDeviceSettings();

        Scene scene = new Scene(root);
        stage.setScene(scene);
    }

    private VBox createSidebar() {
        VBox sidebar = new VBox(10);
        sidebar.setPadding(new Insets(20));
        sidebar.setPrefWidth(200);
        sidebar.setStyle("-fx-background-color: " + BG_SECONDARY + "; -fx-border-color: #333333; -fx-border-width: 0 1 0 0;");

        deviceBtn = createNavButton("⚙️ Device");
        networkBtn = createNavButton("🌐 Network");
        //personalizeBtn = createNavButton("🎨 Personalize");

        deviceBtn.setStyle(deviceBtn.getStyle() + "-fx-background-color: transparent; -fx-border-width: 0; -fx-background-insets: 0; -fx-padding: 12 20 12 20;");
        networkBtn.setStyle(networkBtn.getStyle() + "-fx-background-color: transparent; -fx-border-width: 0; -fx-background-insets: 0; -fx-padding: 12 20 12 20;");
        //personalizeBtn.setStyle(personalizeBtn.getStyle() + "-fx-background-color: transparent; -fx-border-width: 0; -fx-background-insets: 0; -fx-padding: 12 20 12 20;");

        deviceBtn.setOnAction(e -> showDeviceSettings());
        networkBtn.setOnAction(e -> showNetworkSettings());
        //personalizeBtn.setOnAction(e -> showPersonalizeSettings());

        sidebar.getChildren().addAll(deviceBtn, networkBtn);
        return sidebar;
    }

    private Button createNavButton(String text){
        Button btn = new Button(text);
        btn.setPrefHeight(50);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setPadding(new Insets(12, 20, 12, 20));
        btn.setStyle("""
            -fx-background-color: transparent;
            -fx-text-fill: %s;
            -fx-font-size: 16px;
            -fx-font-weight: 500;
            -fx-background-radius: 8;
            -fx-cursor: hand;
            -fx-border-width: 0;
            -fx-border-color: transparent;
            -fx-background-insets: 0;
            -fx-focus-color: transparent;
            -fx-faint-focus-color: transparent;
        """.formatted(TEXT_SECONDARY));

        btn.setOnMouseEntered(e -> {
            if(!btn.getStyle().contains(ACCENT_BLUE)){
                btn.setStyle("""
                    -fx-background-color: %s;
                    -fx-text-fill: %s;
                    -fx-font-size: 16px;
                    -fx-font-weight: 500;
                    -fx-background-radius: 8;
                    -fx-cursor: hand;
                    -fx-border-width: 0;
                    -fx-scale-x: 1.02;
                    -fx-scale-y: 1.02;
                    -fx-border-color: transparent;
                    -fx-background-insets: 0;
                    -fx-focus-color: transparent;
                    -fx-faint-focus-color: transparent;
                """.formatted(HOVER_BG, TEXT_PRIMARY));
            }
        });

        btn.setOnMouseExited(e -> {
            if(!btn.getStyle().contains(ACCENT_BLUE)){
                btn.setStyle("""
                    -fx-background-color: transparent;
                    -fx-text-fill: %s;
                    -fx-font-size: 16px;
                    -fx-font-weight: 500;
                    -fx-background-radius: 8;
                    -fx-cursor: hand;
                    -fx-border-width: 0;
                    -fx-scale-x: 1.0;
                    -fx-scale-y: 1.0;
                    -fx-border-color: transparent;
                    -fx-background-insets: 0;
                    -fx-focus-color: transparent;
                    -fx-faint-focus-color: transparent;
                """.formatted(TEXT_SECONDARY));
            }
        });

        btn.setOnMousePressed(e -> {
            if(!btn.getStyle().contains(ACCENT_BLUE)){
                btn.setStyle("""
                    -fx-background-color: %s;
                    -fx-text-fill: %s;
                    -fx-font-size: 16px;
                    -fx-font-weight: 500;
                    -fx-background-radius: 8;
                    -fx-cursor: hand;
                    -fx-border-width: 0;
                    -fx-scale-x: 0.98;
                    -fx-scale-y: 0.98;
                    -fx-border-color: transparent;
                    -fx-background-insets: 0;
                    -fx-focus-color: transparent;
                    -fx-faint-focus-color: transparent;
                """.formatted(BG_TERTIARY, TEXT_PRIMARY));
            }
        });

        btn.setOnMouseReleased(e -> {
            if(!btn.getStyle().contains(ACCENT_BLUE)){
                btn.setStyle("""
                    -fx-background-color: %s;
                    -fx-text-fill: %s;
                    -fx-font-size: 16px;
                    -fx-font-weight: 500;
                    -fx-background-radius: 8;
                    -fx-cursor: hand;
                    -fx-border-width: 0;
                    -fx-scale-x: 1.02;
                    -fx-scale-y: 1.02;
                    -fx-border-color: transparent;
                    -fx-background-insets: 0;
                    -fx-focus-color: transparent;
                    -fx-faint-focus-color: transparent;
                """.formatted(HOVER_BG, TEXT_PRIMARY));
            }
        });

        return btn;
    }

    private void resetNavButton(Button btn) {
        btn.setStyle("""
            -fx-background-color: transparent;
            -fx-text-fill: %s;
            -fx-font-size: 16px;
            -fx-font-weight: 500;
            -fx-background-radius: 8;
            -fx-cursor: hand;
            -fx-border-width: 0;
            -fx-border-color: transparent;
            -fx-background-insets: 0;
            -fx-focus-color: transparent;
            -fx-faint-focus-color: transparent;
        """.formatted(TEXT_SECONDARY));
    }

    private void showDeviceSettings() {
        SettingsNavUtils.setActiveButton(deviceBtn, deviceBtn, networkBtn);
        
        // Reset all buttons to default state
        resetNavButton(deviceBtn);
        resetNavButton(networkBtn);
        //resetNavButton(personalizeBtn);
        
        // Set active button
        deviceBtn.setStyle("""
            -fx-background-color: %s;
            -fx-text-fill: white;
            -fx-font-size: 16px;
            -fx-font-weight: 600;
            -fx-background-radius: 8;
            -fx-border-width: 0;
            -fx-border-color: transparent;
            -fx-background-insets: 0;
            -fx-focus-color: transparent;
            -fx-faint-focus-color: transparent;
        """.formatted(ACCENT_BLUE));

        VBox panel = new VBox(15);
        panel.setPadding(new Insets(0));

        Label title = new Label("Device Settings");
        title.setStyle("-fx-font-size: 36px; -fx-font-weight: 700; -fx-text-fill: " + TEXT_PRIMARY + ";");

        VBox optionsBox = new VBox(12);
        optionsBox.setPadding(new Insets(20, 0, 0, 0));

        String[] options = {"💻 Display", "💾 Storage", "🔋 Battery", "🔊 Sound", "🖥️ Devices"};
        Runnable[] actions = {
            this::showDisplaySettings,
            this::showStorageSettings,
            this::showBatterySettings,
            this::showSoundSettings,
            this::showDevicesManager
        };

        for (int i = 0; i < options.length; i++) {
            Button optionBtn = createOptionButton(options[i]);
            int index = i;
            optionBtn.setOnAction(e -> actions[index].run());
            optionsBox.getChildren().add(optionBtn);
        }

        panel.getChildren().addAll(title, optionsBox);

        contentArea.getChildren().clear();
        contentArea.getChildren().add(panel);
    }

    private Button createOptionButton(String text) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setPadding(new Insets(18, 24, 18, 24));
        btn.setStyle("""
            -fx-background-color: %s;
            -fx-text-fill: %s;
            -fx-font-size: 18px;
            -fx-font-weight: 500;
            -fx-background-radius: 12;
            -fx-cursor: hand;
            -fx-border-color: #333333;
            -fx-border-width: 1;
            -fx-border-radius: 12;
        """.formatted(BG_SECONDARY, TEXT_PRIMARY));

        btn.setOnMouseEntered(e -> btn.setStyle(btn.getStyle().replace(BG_SECONDARY, BG_TERTIARY)));
        btn.setOnMouseExited(e -> btn.setStyle(btn.getStyle().replace(BG_TERTIARY, BG_SECONDARY)));

        return btn;
    }

    private void showDisplaySettings() {
        VBox panel = new VBox(25);
        panel.setPadding(new Insets(0));

        Label title = new Label("Display Settings");
        title.setStyle("-fx-font-size: 36px; -fx-font-weight: 700; -fx-text-fill: " + TEXT_PRIMARY + ";");

        // Brightness Section
        VBox brightnessSection = createSettingSection("Brightness", "50%");
        Label brightnessLabel = (Label) brightnessSection.getChildren().get(0);
        
        Slider brightnessSlider = createStyledSlider(50);
        brightnessSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            int value = newVal.intValue();
            brightnessLabel.setText("Brightness — " + value + "%");
        });

        brightnessSection.getChildren().add(brightnessSlider);

        // Resolution Section
        VBox resolutionBox = createInfoCard("Resolution", "1920 × 1080");

        panel.getChildren().addAll(title, brightnessSection, resolutionBox);

        contentArea.getChildren().clear();
        contentArea.getChildren().add(panel);
    }

    private VBox createSettingSection(String label, String value) {
        VBox section = new VBox(12);
        Label titleLabel = new Label(label + " — " + value);
        titleLabel.setStyle("-fx-text-fill: " + TEXT_PRIMARY + "; -fx-font-size: 20px; -fx-font-weight: 600;");
        section.getChildren().add(titleLabel);
        return section;
    }

    private VBox createInfoCard(String label, String value) {
        VBox card = new VBox(8);
        card.setPadding(new Insets(20));
        card.setStyle("""
            -fx-background-color: %s;
            -fx-background-radius: 12;
            -fx-border-color: #333333;
            -fx-border-width: 1;
            -fx-border-radius: 12;
        """.formatted(BG_SECONDARY));

        Label titleLabel = new Label(label);
        titleLabel.setStyle("-fx-text-fill: " + TEXT_SECONDARY + "; -fx-font-size: 14px; -fx-font-weight: 500;");

        Label valueLabel = new Label(value);
        valueLabel.setStyle("-fx-text-fill: " + TEXT_PRIMARY + "; -fx-font-size: 22px; -fx-font-weight: 600;");

        card.getChildren().addAll(titleLabel, valueLabel);
        return card;
    }

    private Slider createStyledSlider(double initialValue) {
        Slider slider = new Slider(0, 100, initialValue);
        slider.setShowTickLabels(false);
        slider.setShowTickMarks(false);
        slider.setMaxWidth(500);

        slider.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.addPostLayoutPulseListener(() -> {
                    slider.lookupAll(".axis").forEach(axis -> {
                        axis.setStyle("-fx-tick-label-fill: " + TEXT_SECONDARY + ";");
                    });
                });
            }
        });

        slider.setStyle("""
            -fx-background-color: transparent;
            .slider .track {
                -fx-background-color: %s;
                -fx-pref-height: 6px;
                -fx-background-radius: 3px;
            }
            .slider .colored-track {
                -fx-background-color: linear-gradient(to right, %s, %s);
                -fx-pref-height: 6px;
                -fx-background-radius: 3px;
            }
            .slider .thumb {
                -fx-background-color: white;
                -fx-background-radius: 8px;
                -fx-pref-width: 16px;
                -fx-pref-height: 16px;
                -fx-effect: dropshadow(gaussian, rgba(59, 130, 246, 0.5), 8, 0.3, 0, 0);
            }
            .slider .thumb:hover {
                -fx-effect: dropshadow(gaussian, rgba(59, 130, 246, 0.8), 12, 0.4, 0, 0);
            }
        """.formatted(BG_TERTIARY, ACCENT_BLUE, ACCENT_CYAN));

        return slider;
    }

    private void showStorageSettings() {
        VBox panel = new VBox(20);
        panel.setPadding(new Insets(0));

        Label title = new Label("Storage Settings");
        title.setStyle("-fx-font-size: 36px; -fx-font-weight: 700; -fx-text-fill: " + TEXT_PRIMARY + ";");

        VBox storageGrid = new VBox(12);
        storageGrid.getChildren().addAll(
            createInfoCard("Available Space", "256 GB"),
            createInfoCard("Installed Apps", "25 GB"),
            createInfoCard("Photos", "5 GB"),
            createInfoCard("Videos", "35 GB"),
            createInfoCard("Temporary Files", "6 GB"),
            createInfoCard("Others", "5 GB")
        );

        panel.getChildren().addAll(title, storageGrid);

        contentArea.getChildren().clear();
        contentArea.getChildren().add(panel);
    }

    private void showBatterySettings() {
        VBox panel = new VBox(20);
        panel.setPadding(new Insets(0));

        Label title = new Label("Battery Settings");
        title.setStyle("-fx-font-size: 36px; -fx-font-weight: 700; -fx-text-fill: " + TEXT_PRIMARY + ";");

        VBox batteryGrid = new VBox(12);
        batteryGrid.getChildren().addAll(
            createInfoCard("Battery Level", "85%"),
            createInfoCard("Power Mode", "Balanced"),
            createInfoCard("Estimated Time", "5h 23m remaining")
        );

        panel.getChildren().addAll(title, batteryGrid);

        contentArea.getChildren().clear();
        contentArea.getChildren().add(panel);
    }

    private void showSoundSettings() {
        VBox panel = new VBox(25);
        panel.setPadding(new Insets(0));

        Label title = new Label("Sound Settings");
        title.setStyle("-fx-font-size: 36px; -fx-font-weight: 700; -fx-text-fill: " + TEXT_PRIMARY + ";");

        VBox volumeSection = createSettingSection("Volume", "70%");
        Label volumeLabel = (Label) volumeSection.getChildren().get(0);
        
        Slider volumeSlider = createStyledSlider(70);
        volumeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            int value = newVal.intValue();
            volumeLabel.setText("Volume — " + value + "%");
        });

        volumeSection.getChildren().add(volumeSlider);

        panel.getChildren().addAll(title, volumeSection);

        contentArea.getChildren().clear();
        contentArea.getChildren().add(panel);
    }
    
    private void showDevicesManager() {
        VBox panel = new VBox(20);
        panel.setPadding(new Insets(0));

        Label title = new Label("Connected Devices");
        title.setStyle("-fx-font-size: 36px; -fx-font-weight: 700; -fx-text-fill: " + TEXT_PRIMARY + ";");

        VBox deviceList = new VBox(12);

        for (String[] dev : deviceListData) {
            String devName = dev[0];
            String devType = dev[1];
            String devStatus = dev[2];

            Button btn = new Button(String.format("🖥  %s  •  %s", devName, devType));
            btn.setMaxWidth(Double.MAX_VALUE);
            btn.setAlignment(Pos.CENTER_LEFT);
            btn.setPadding(new Insets(18, 24, 18, 24));
            
            String statusColor = devStatus.equals("Enabled") ? "#10b981" : TEXT_SECONDARY;
            btn.setStyle("""
                -fx-background-color: %s;
                -fx-text-fill: %s;
                -fx-font-size: 18px;
                -fx-font-weight: 500;
                -fx-background-radius: 12;
                -fx-cursor: hand;
                -fx-border-color: %s;
                -fx-border-width: 1 1 1 4;
                -fx-border-radius: 12;
            """.formatted(BG_SECONDARY, TEXT_PRIMARY, statusColor));

            btn.setOnMouseEntered(e -> btn.setStyle(btn.getStyle().replace(BG_SECONDARY, BG_TERTIARY)));
            btn.setOnMouseExited(e -> btn.setStyle(btn.getStyle().replace(BG_TERTIARY, BG_SECONDARY)));
            btn.setOnAction(evt -> showDeviceDetails(devName, devType, devStatus));

            deviceList.getChildren().add(btn);
        }

        ScrollPane scroll = new ScrollPane(deviceList);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: " + BG_PRIMARY + "; -fx-background-color: " + BG_PRIMARY + "; -fx-border-color: transparent;");
        scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        panel.getChildren().addAll(title, scroll);

        contentArea.getChildren().clear();
        contentArea.getChildren().add(panel);
    }

    private void showDeviceDetails(String name, String type, String status) {
        VBox panel = new VBox(25);
        panel.setPadding(new Insets(0));

        Label title = new Label(name);
        title.setStyle("-fx-font-size: 32px; -fx-font-weight: 700; -fx-text-fill: " + TEXT_PRIMARY + ";");

        VBox infoBox = new VBox(12);
        infoBox.getChildren().addAll(
            createInfoCard("Type", type),
            createInfoCard("Status", status)
        );

        VBox buttonBox = new VBox(12);
        buttonBox.setPadding(new Insets(10, 0, 0, 0));

        Button toggleBtn = new Button(status.equals("Enabled") ? "Disable Device" : "Enable Device");
        styleActionButton(toggleBtn, ACCENT_BLUE);
        
        Label statusLabel = (Label) ((VBox) infoBox.getChildren().get(1)).getChildren().get(1);
        toggleBtn.setOnAction(e -> {
            boolean isEnabled = statusLabel.getText().equals("Enabled");
            statusLabel.setText(isEnabled ? "Disabled" : "Enabled");
            toggleBtn.setText(isEnabled ? "Enable Device" : "Disable Device");
        });

        Button removeBtn = new Button("Remove Device");
        styleActionButton(removeBtn, "#ef4444");
        removeBtn.setOnAction(e -> {
            deviceListData.removeIf(d -> d[0].equals(name));
            showDevicesManager();
        });

        buttonBox.getChildren().addAll(toggleBtn, removeBtn);
        panel.getChildren().addAll(title, infoBox, buttonBox);

        contentArea.getChildren().clear();
        contentArea.getChildren().add(panel);
    }

    private void styleActionButton(Button btn, String color) {
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setPadding(new Insets(14, 24, 14, 24));
        btn.setStyle("""
            -fx-background-color: %s;
            -fx-text-fill: white;
            -fx-font-size: 16px;
            -fx-font-weight: 600;
            -fx-background-radius: 10;
            -fx-cursor: hand;
            -fx-border-width: 0;
        """.formatted(color));

        btn.setOnMouseEntered(e -> btn.setOpacity(0.9));
        btn.setOnMouseExited(e -> btn.setOpacity(1.0));
    }

    private void showNetworkSettings() {
        SettingsNavUtils.setActiveButton(networkBtn, deviceBtn, networkBtn);
        
        // Reset all buttons to default state
        resetNavButton(deviceBtn);
        resetNavButton(networkBtn);
       // resetNavButton(personalizeBtn);
        
        // Set active button
        networkBtn.setStyle("""
            -fx-background-color: %s;
            -fx-text-fill: white;
            -fx-font-size: 16px;
            -fx-font-weight: 600;
            -fx-background-radius: 8;
            -fx-border-width: 0;
            -fx-border-color: transparent;
            -fx-background-insets: 0;
            -fx-focus-color: transparent;
            -fx-faint-focus-color: transparent;
        """.formatted(ACCENT_BLUE));

        VBox panel = new VBox(25);
        panel.setPadding(new Insets(0));

        Label title = new Label("Network Settings");
        title.setStyle("-fx-font-size: 36px; -fx-font-weight: 700; -fx-text-fill: " + TEXT_PRIMARY + ";");

        // Wi-Fi Section
        VBox wifiSection = new VBox(15);
        Label wifiTitle = new Label("Wi-Fi");
        wifiTitle.setStyle("-fx-font-size: 24px; -fx-font-weight: 600; -fx-text-fill: " + TEXT_PRIMARY + ";");

        Label wifiStatus = new Label();
        updateWifiStatusLabel(wifiStatus);

        Button toggleWifiBtn = new Button(SettingsWindow.wifiConnected ? "Turn Off Wi-Fi" : "Turn On Wi-Fi");
        styleActionButton(toggleWifiBtn, wifiConnected ? "#ef4444" : ACCENT_BLUE);
        toggleWifiBtn.setOnAction(e -> {
            SettingsWindow.wifiConnected = !SettingsWindow.wifiConnected;
            toggleWifiBtn.setText(SettingsWindow.wifiConnected ? "Turn Off Wi-Fi" : "Turn On Wi-Fi");
            styleActionButton(toggleWifiBtn, wifiConnected ? "#ef4444" : ACCENT_BLUE);
            updateWifiStatusLabel(wifiStatus);
        });

        Label nearbyTitle = new Label("Available Networks");
        nearbyTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: 600; -fx-text-fill: " + TEXT_SECONDARY + "; -fx-padding: 10 0 5 0;");

        VBox networksList = new VBox(10);
        String[] fakeNetworks = {"Home_WiFi_5G", "CoffeeShop_Free", "LibraryNet", "XEN-OS-Dev-AP"};

        for (String network : fakeNetworks) {
            Button networkBtn = createNetworkButton(network, network.equals(selectedNetwork));
            networkBtn.setOnAction(e -> {
                selectedNetwork = network;
                refreshNetworkList(networksList, fakeNetworks, wifiStatus);
                updateWifiStatusLabel(wifiStatus);
            });
            networksList.getChildren().add(networkBtn);
        }

        wifiSection.getChildren().addAll(wifiTitle, wifiStatus, toggleWifiBtn, nearbyTitle, networksList);

        // Network Configuration
        VBox configSection = new VBox(15);
        configSection.setPadding(new Insets(20, 0, 0, 0));

        TextField ipField = createStyledTextField(currentIP, "IP Address");
        ipField.textProperty().addListener((obs, oldVal, newVal) -> currentIP = newVal);

        TextField dnsField = createStyledTextField("8.8.8.8", "DNS Server");
        TextField gatewayField = createStyledTextField("192.168.1.1", "Gateway");

        configSection.getChildren().addAll(
            createFieldWithLabel("IP Address", ipField),
            createFieldWithLabel("DNS Server", dnsField),
            createFieldWithLabel("Gateway", gatewayField)
        );

        panel.getChildren().addAll(title, wifiSection, configSection);

        ScrollPane scroll = new ScrollPane(panel);
        scroll.setFitToWidth(true);
        scroll.setStyle("""
            -fx-background: %s;
            -fx-background-color: %s;
            -fx-border-color: transparent;
        """.formatted(BG_PRIMARY, BG_PRIMARY));
        
        // Apply styling after scene is rendered
        scroll.skinProperty().addListener((obs, oldSkin, newSkin) -> {
            if (newSkin != null) {
                javafx.application.Platform.runLater(() -> {
                    var vbar = scroll.lookup(".scroll-bar:vertical");
                    if (vbar != null) {
                        vbar.setStyle("""
                            -fx-background-color: %s;
                            -fx-pref-width: 8px;
                        """.formatted(BG_PRIMARY));
                        
                        var thumb = vbar.lookup(".thumb");
                        if (thumb != null) {
                            thumb.setStyle("""
                                -fx-background-color: %s;
                                -fx-background-radius: 4px;
                            """.formatted(BG_TERTIARY));
                        }
                        
                        var track = vbar.lookup(".track");
                        if (track != null) {
                            track.setStyle("""
                                -fx-background-color: %s;
                                -fx-background-radius: 4px;
                            """.formatted(BG_SECONDARY));
                        }
                        
                        var incrBtn = vbar.lookup(".increment-button");
                        var decrBtn = vbar.lookup(".decrement-button");
                        if (incrBtn != null) incrBtn.setStyle("-fx-opacity: 0;");
                        if (decrBtn != null) decrBtn.setStyle("-fx-opacity: 0;");
                    }
                });
            }
        });

        contentArea.getChildren().clear();
        contentArea.getChildren().add(scroll);
    }

    private Button createNetworkButton(String network, boolean selected) {
        Button btn = new Button("📶  " + network);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setPadding(new Insets(16, 20, 16, 20));
        
        if (selected) {
            btn.setStyle("""
                -fx-background-color: %s;
                -fx-text-fill: white;
                -fx-font-size: 16px;
                -fx-font-weight: 600;
                -fx-background-radius: 10;
                -fx-cursor: hand;
                -fx-border-width: 0;
            """.formatted(ACCENT_BLUE));
        } else {
            btn.setStyle("""
                -fx-background-color: %s;
                -fx-text-fill: %s;
                -fx-font-size: 16px;
                -fx-font-weight: 500;
                -fx-background-radius: 10;
                -fx-cursor: hand;
                -fx-border-color: #333333;
                -fx-border-width: 1;
                -fx-border-radius: 10;
            """.formatted(BG_SECONDARY, TEXT_PRIMARY));
            
            btn.setOnMouseEntered(e -> btn.setStyle(btn.getStyle().replace(BG_SECONDARY, BG_TERTIARY)));
            btn.setOnMouseExited(e -> btn.setStyle(btn.getStyle().replace(BG_TERTIARY, BG_SECONDARY)));
        }
        
        return btn;
    }

    private void refreshNetworkList(VBox networksList, String[] networks, Label wifiStatus) {
        networksList.getChildren().clear();
        for (String net : networks) {
            Button btn = createNetworkButton(net, net.equals(selectedNetwork));
            btn.setOnAction(e -> {
                selectedNetwork = net;
                refreshNetworkList(networksList, networks, wifiStatus);
                updateWifiStatusLabel(wifiStatus);
            });
            networksList.getChildren().add(btn);
        }
    }

    private void updateWifiStatusLabel(Label label) {
        String icon = SettingsWindow.wifiConnected ? "📶" : "📡";
        String status = SettingsWindow.wifiConnected ? "Connected" : "Disconnected";
        String color = SettingsWindow.wifiConnected ? "#10b981" : TEXT_SECONDARY;
        
        label.setText(icon + "  " + status + "  •  " + selectedNetwork);
        label.setStyle("-fx-text-fill: " + color + "; -fx-font-size: 16px; -fx-font-weight: 500;");
    }

    private TextField createStyledTextField(String text, String prompt) {
        TextField field = new TextField(text);
        field.setPromptText(prompt);
        field.setStyle("""
            -fx-background-color: %s;
            -fx-text-fill: %s;
            -fx-prompt-text-fill: %s;
            -fx-font-size: 16px;
            -fx-padding: 12 16;
            -fx-background-radius: 8;
            -fx-border-color: #333333;
            -fx-border-width: 1;
            -fx-border-radius: 8;
        """.formatted(BG_SECONDARY, TEXT_PRIMARY, TEXT_SECONDARY));
        
        field.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (isNowFocused) {
                field.setStyle(field.getStyle().replace("#333333", ACCENT_BLUE));
            } else {
                field.setStyle(field.getStyle().replace(ACCENT_BLUE, "#333333"));
            }
        });
        
        return field;
    }

    private VBox createFieldWithLabel(String labelText, TextField field) {
        VBox box = new VBox(8);
        Label label = new Label(labelText);
        label.setStyle("-fx-text-fill: " + TEXT_SECONDARY + "; -fx-font-size: 14px; -fx-font-weight: 600;");
        box.getChildren().addAll(label, field);
        return box;
    }

    // private void showPersonalizeSettings() {
    //     SettingsNavUtils.setActiveButton(personalizeBtn, deviceBtn, networkBtn, personalizeBtn);
        
    //     // Reset all buttons to default state
    //     resetNavButton(deviceBtn);
    //     resetNavButton(networkBtn);
    //     resetNavButton(personalizeBtn);
        
    //     // Set active button
    //     personalizeBtn.setStyle("""
    //         -fx-background-color: %s;
    //         -fx-text-fill: white;
    //         -fx-font-size: 16px;
    //         -fx-font-weight: 600;
    //         -fx-background-radius: 8;
    //         -fx-border-width: 0;
    //         -fx-border-color: transparent;
    //         -fx-background-insets: 0;
    //         -fx-focus-color: transparent;
    //         -fx-faint-focus-color: transparent;
    //     """.formatted(ACCENT_BLUE));

    //     VBox panel = new VBox(20);
    //     panel.setPadding(new Insets(0));

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