package com.xen.oslab.utils;

import javafx.scene.control.Button;

public class SettingsNavUtils {
    private static final String DEFAULT_STYLE = 
        "-fx-background-color: #3c3c3c;" +
        "-fx-text-fill: white;" +
        "-fx-font-size: 14px;" +
        "-fx-border-width: 0;" +
        "-fx-background-radius: 0;";
    
    private static final String ACTIVE_STYLE = 
        "-fx-background-color: #505050;" +
        "-fx-text-fill: white;" +
        "-fx-font-size: 14px;" +
        "-fx-border-width: 0;" +
        "-fx-background-radius: 0;";
    
    public static void setActiveButton(Button activeBtn, Button... allButtons) {
        for (Button btn : allButtons) {
            btn.setStyle(DEFAULT_STYLE);
        }
        activeBtn.setStyle(ACTIVE_STYLE);
    }
    
    public static String getDefaultButtonStyle() {
        return DEFAULT_STYLE;
    }
    
    public static String getActiveButtonStyle() {
        return ACTIVE_STYLE;
    }
}