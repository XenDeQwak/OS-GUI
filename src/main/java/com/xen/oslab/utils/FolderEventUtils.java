package com.xen.oslab.utils;

import com.xen.oslab.objects.Folder;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.Node;

public class FolderEventUtils {

    private static ContextMenu openMenu = null;
    public static ContextMenu createRenameContextMenu(Folder folder, Runnable saveAction) {
        MenuItem renameItem = new MenuItem("Rename");
        renameItem.setOnAction(e -> folder.startRename());

        ContextMenu menu = new ContextMenu(renameItem);

        folder.getRenameField().setOnAction(e -> {
            folder.finishRename();
            saveAction.run();
        });
        folder.getRenameField().focusedProperty().addListener((obs, oldFocus, newFocus) -> {
            if (!newFocus) {
                folder.finishRename();
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
