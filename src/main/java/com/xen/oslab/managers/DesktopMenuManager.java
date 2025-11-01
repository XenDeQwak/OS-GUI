package com.xen.oslab.managers;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.stage.Window;

import java.util.Map;

public class DesktopMenuManager {
    private final ContextMenu menu = new ContextMenu();

    public DesktopMenuManager(Pane desktopPane, Map<String, Runnable> actions) {
        actions.forEach((label, action) -> {
            MenuItem item = new MenuItem(label);
            item.setOnAction(e -> action.run());
            menu.getItems().add(item);
        });

        desktopPane.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.SECONDARY)
                menu.show(desktopPane, e.getScreenX(), e.getScreenY());
            else menu.hide();
        });
    }

    public ContextMenu getMenu() { return menu; }
}
