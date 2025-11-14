package com.xen.oslab.managers;

import com.xen.oslab.modules.SettingsWindow;

public class SettingsManager {
    private SettingsWindow settingsWindow;

    public void openSettings() {
        if (settingsWindow == null) {
            settingsWindow = new SettingsWindow();
        }
       settingsWindow.show();
    }

    public void closeSettings(){
        if(settingsWindow != null){
            settingsWindow.close();
        }
    }
}
