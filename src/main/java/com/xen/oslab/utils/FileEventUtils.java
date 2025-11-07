package com.xen.oslab.utils;

import com.xen.oslab.objects.File;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.Node;

public class FileEventUtils {

    private static ContextMenu openMenu = null;

    public static ContextMenu createRenameContextMenu(File file, Runnable saveAction) {
        MenuItem renameItem = new MenuItem("Rename");
        renameItem.setOnAction(e -> file.startRename());

        ContextMenu menu = new ContextMenu(renameItem);

        file.getRenameFile().setOnAction(e -> {
            file.finishRename();
            saveAction.run();
        });
        file.getRenameFile().focusedProperty().addListener((obs, oldFocus, newFocus) -> {
            if (!newFocus) {
                file.finishRename();
                saveAction.run();
            }
        });

        return menu;
    }

    public static void showMenu(ContextMenu menu, double x, double y, Node owner) {
        if (openMenu != null && openMenu.isShowing()) openMenu.hide();
        openMenu = menu;
        menu.show(owner, x, y);
        menu.setOnHidden(e -> openMenu = null);
    }
}
