package com.xen.oslab;

import javafx.fxml.FXML;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;

public class OSController {

    @FXML
    private GridPane desktopGrid;

    @FXML
    public void initialize() {
        ContextMenu desktopMenu = new ContextMenu();
        MenuItem newFolder = new MenuItem("New Folder");

        desktopMenu.getItems().addAll(newFolder);

        desktopGrid.setOnMouseClicked(e-> {
            if (e.getButton() == MouseButton.SECONDARY) {
                desktopMenu.show(desktopGrid, e.getScreenX(), e.getScreenY());
            }
            else desktopMenu.hide();
        });

        newFolder.setOnAction(e-> {
            int files = desktopGrid.getChildren().size();
            int cols = 10;
            int row = files / cols;
            int col = files % cols;

            if (row < 5) desktopGrid.add(new File("New File" + (files + 1)), col, row);
            desktopMenu.hide();
        });
    }



}