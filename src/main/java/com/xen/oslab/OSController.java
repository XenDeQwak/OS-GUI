package com.xen.oslab;

import javafx.fxml.FXML;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;

public class OSController {

    @FXML
    private Pane desktopPane;

    private final SnapOnGrid snapper = new SnapOnGrid();

    @FXML
    public void initialize() {
        ContextMenu desktopMenu = new ContextMenu();
        MenuItem newFolder = new MenuItem("New Folder");

        desktopMenu.getItems().addAll(newFolder);

        desktopPane.setOnMouseClicked(e-> {
            if (e.getButton() == MouseButton.SECONDARY) {
                desktopMenu.show(desktopPane, e.getScreenX(), e.getScreenY());
            }
            else desktopMenu.hide();
        });

        newFolder.setOnAction(e -> {
            int files = desktopPane.getChildren().size();
            int cols = 10;
            int rows = 5;
            double cellW = 80;
            double cellH = 100;

            int col = files % cols;
            int row = files / cols;

            if (row < rows) {
                File file = new File("File " + (files + 1));
                file.setLayoutX(col * cellW);
                file.setLayoutY(row * cellH);
                file.setOnMouseReleased(ev -> snapper.snap(file));
                desktopPane.getChildren().add(file);
            } else {
                System.out.println("Desktop full");
            }
        });

    }



}