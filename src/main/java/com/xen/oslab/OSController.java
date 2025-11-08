package com.xen.oslab;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import com.xen.oslab.managers.DesktopMenuManager;
import com.xen.oslab.managers.FileManager;
import com.xen.oslab.managers.FolderManager;
import com.xen.oslab.managers.GridManager;
import com.xen.oslab.managers.storage.FileStorageManager;
import com.xen.oslab.managers.storage.FolderStorageManager;
import com.xen.oslab.objects.File;
import com.xen.oslab.objects.Folder;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.util.Duration;


public class OSController {
    @FXML
    private Pane desktopPane;
    @FXML
    private HBox taskbar;

    private GridManager grid;
    private FileManager fileManager;
    private SnapOnGrid snapper;
    private FileStorageManager fileStorage;
    private FolderStorageManager folderStorage;
    private FolderManager folderManager;

    private final int cols = 10;
    private final int rows = 5;
    private final double cellW = 80;
    private final double cellH = 100;
    private final boolean[][] occupied = new boolean[rows][cols];

    @FXML
    public void initialize() {

        grid = new GridManager(rows, cols, cellW, cellH, occupied);
        snapper = new SnapOnGrid(grid);
        fileStorage = new FileStorageManager();
        folderStorage = new FolderStorageManager();
        fileManager = new FileManager(desktopPane, snapper, occupied, cellW, cellH);
        folderManager = new FolderManager(desktopPane, snapper, occupied, cellW, cellH, fileManager, folderStorage);

        Label clockLabel = new Label();
        clockLabel.setStyle("-fx-text-fill: white; -fx-font-size: 11px; -fx-padding: 4 10 4 10;");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE, MMM d yyyy - hh:mm a");

        Timeline clock = new Timeline(
            new KeyFrame(Duration.ZERO, e -> {
                LocalDateTime now = LocalDateTime.now();
                clockLabel.setText(now.format(formatter));
            }),
            new KeyFrame(Duration.seconds(1))
        );
        clock.setCycleCount(Timeline.INDEFINITE);
        clock.play();

        taskbar.getChildren().add(0, clockLabel);


        new DesktopMenuManager(desktopPane, Map.of(
            "New File", this::addNewFile,
            "New Folder", this::addNewFolder
        ));
        fileStorage.loadAll(desktopPane, fileManager);
        folderStorage.loadAll(folderManager);
    }

    private int getNextFileNumber() {
        long count = desktopPane.getChildren().stream()
            .filter(n -> n instanceof File)
            .count();
        return (int) count + 1;
    }

    private int getNextFolderNumber() {
        long count = desktopPane.getChildren().stream()
            .filter(n -> n instanceof Folder)
            .count();
        return (int) count + 1;
    }

    private void addNewFile() {
        int[] free = grid.findNearestFree(0, 0);
        if (free.length == 0) return;
        int num = getNextFileNumber();
        fileManager.createFile("New File " + num, free[0], free[1]);
    }

    private void addNewFolder() {
        int[] free = grid.findNearestFree(0, 0);
        if (free.length == 0) return;
        int num = getNextFolderNumber();
        folderManager.createFolder("New Folder " + num, free[0], free[1]);
    }
}
