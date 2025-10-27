package com.xen.oslab.managers;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.input.MouseButton;

public class DesktopMenuManager {
    private final ContextMenu menu = new ContextMenu();

    public DesktopMenuManager(Pane desktopPane, Runnable onNewFile) {
        MenuItem newFile = new MenuItem("New File");
        newFile.setOnAction(e -> onNewFile.run());

        Menu changeBg = new Menu("Change Background");
        MenuItem bg1 = new MenuItem("Sky");
        MenuItem bg2 = new MenuItem("Mountains");
        MenuItem bg3 = new MenuItem("City");

        bg1.setOnAction(e -> setBackground(desktopPane, "/com/xen/oslab/backgrounds/sky.jpg"));
        bg2.setOnAction(e -> setBackground(desktopPane, "/com/xen/oslab/backgrounds/mountain.jpg"));
        bg3.setOnAction(e -> setBackground(desktopPane, "/com/xen/oslab/backgrounds/city.jpg"));

        changeBg.getItems().addAll(bg1, bg2, bg3);
        menu.getItems().addAll(newFile, changeBg);

        desktopPane.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.SECONDARY)
                menu.show(desktopPane, e.getScreenX(), e.getScreenY());
            else menu.hide();
        });
    }

    private void setBackground(Pane pane, String path) {
        var url = getClass().getResource(path);
        if (url == null) return;

        var img = new Image(url.toExternalForm());
        var bg = new BackgroundImage(
                img,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, true, true)
        );
        pane.setBackground(new Background(bg));
    }
}
