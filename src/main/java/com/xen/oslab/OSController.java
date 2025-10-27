package com.xen.oslab;

import com.xen.oslab.managers.DesktopMenuManager;
import com.xen.oslab.managers.FileManager;
import com.xen.oslab.managers.GridManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class OSController {

    @FXML
    private Pane desktopPane;

    private final int cols = 10;
    private final int rows = 5;
    private final double cellW = 80;
    private final double cellH = 100;
    private final boolean[][] occupied = new boolean[rows][cols];

    private GridManager grid;
    private FileManager fileManager;
    private SnapOnGrid snapper;

    @FXML
    public void initialize() {
        grid = new GridManager(rows, cols, cellW, cellH, occupied);
        snapper = new SnapOnGrid(grid);
        fileManager = new FileManager(desktopPane, snapper, occupied, cellW, cellH);
        
        new DesktopMenuManager(desktopPane, this::addNewFile);

        addTaskbar();
    }

    private void addTaskbar() {
        HBox taskbar = new HBox();
        taskbar.setPrefHeight(40);
        taskbar.setStyle("-fx-background-color: rgba(30,30,30,0.8);");
        taskbar.setAlignment(Pos.CENTER_RIGHT);
        taskbar.setPadding(new Insets(0, 15, 0, 15));

        ImageView powerIcon = new ImageView(
                getClass().getResource("/com/xen/oslab/icons/power.png").toExternalForm()
        );
        powerIcon.setFitWidth(20);
        powerIcon.setFitHeight(20);

        Button powerButton = new Button();
        powerButton.setGraphic(powerIcon);
        powerButton.setStyle("-fx-background-color: transparent;");
        powerButton.setOnAction(e -> Platform.exit());

        taskbar.getChildren().add(powerButton);

        if (desktopPane.getParent() instanceof BorderPane borderPane) {
            borderPane.setBottom(taskbar);
        } else {
            BorderPane wrapper = new BorderPane();
            wrapper.setCenter(desktopPane);
            wrapper.setBottom(taskbar);

            Stage stage = (Stage) desktopPane.getScene().getWindow();
            Scene scene = new Scene(wrapper, desktopPane.getWidth(), desktopPane.getHeight());
            stage.setScene(scene);
        }
    }

    private void addNewFile() {
        int[] free = grid.findNearestFree(0, 0);
        if (free.length == 0) {
            System.out.println("Desktop full");
            return;
        }
        fileManager.createFile("New File " + (desktopPane.getChildren().size() + 1), free[0], free[1]);
    }

}
