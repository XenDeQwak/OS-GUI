package com.xen.oslab.managers;

import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;

import java.util.Map;

public class DesktopMenuManager {
    private final ContextMenu menu = new ContextMenu();

    public DesktopMenuManager(Pane desktopPane, Map<String, Runnable> actions) {
        this(desktopPane, actions, new Menu[0]);
    }

    public DesktopMenuManager(Pane desktopPane, Map<String, Runnable> actions, Menu... extraMenus) {
        actions.forEach((label, action) -> {
            MenuItem item = new MenuItem(label);
            item.setOnAction(e -> action.run());
            menu.getItems().add(item);
        });

        if (extraMenus != null && extraMenus.length > 0) {
            menu.getItems().add(new SeparatorMenuItem());
            menu.getItems().addAll(extraMenus);
        }

        desktopPane.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.SECONDARY) {
                if (e.getTarget() == desktopPane) {
                    menu.show(desktopPane, e.getScreenX(), e.getScreenY());
                }
            } 
            else menu.hide();
        });
    }
}
