package com.xen.oslab.managers;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;

public class DesktopMenuManager {
    private final ContextMenu menu = new ContextMenu();

    public DesktopMenuManager(Pane desktopPane, Runnable onNewFile) {
        MenuItem newFile = new MenuItem("New File");
        newFile.setOnAction(e -> onNewFile.run());
        menu.getItems().add(newFile);

        desktopPane.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.SECONDARY)
                menu.show(desktopPane, e.getScreenX(), e.getScreenY());
            else menu.hide();
        });
    }
}
