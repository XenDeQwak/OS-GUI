package com.xen.oslab.managers;

import com.xen.oslab.modules.SettingsWindow;
import javafx.stage.Stage;

public class SettingsManager {
    private SettingsWindow settingsWindow;

    public Stage openSettings() {
        if (settingsWindow == null) {
            settingsWindow = new SettingsWindow();
        }

        settingsWindow.show();
        return settingsWindow.getStage();
    }

    public void closeSettings() {
        if (settingsWindow != null) {
            settingsWindow.close();
        }
    }
}
