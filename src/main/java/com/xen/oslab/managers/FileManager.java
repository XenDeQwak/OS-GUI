package com.xen.oslab.managers;

import com.xen.oslab.SnapOnGrid;
import com.xen.oslab.objects.File;

import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class FileManager {
    private final Pane desktopPane;
    private final SnapOnGrid snapper;
    private final double cellW;
    private final double cellH;
    private final boolean[][] occupied;

    public FileManager(Pane desktopPane, SnapOnGrid snapper, boolean[][] occupied, double cellW, double cellH) {
        this.desktopPane = desktopPane;
        this.snapper = snapper;
        this.occupied = occupied;
        this.cellW = cellW;
        this.cellH = cellH;
    }

    public void createFile(String name, int row, int col) {
        File file = new File(name);
        file.setLayoutX(col * cellW);
        file.setLayoutY(row * cellH);
        file.setUserData(new int[]{row, col});
        occupied[row][col] = true;

        file.setOnMousePressed(e -> {
            int[] pos = (int[]) file.getUserData();
            if (pos != null) occupied[pos[0]][pos[1]] = false;
            file.setUserData(null);
            file.toFront();
        });

        file.setOnMouseDragged(e -> {
            file.setLayoutX(e.getSceneX() - file.getWidth() / 2);
            file.setLayoutY(e.getSceneY() - file.getHeight() / 2);
        });

        file.setOnMouseReleased(e -> snapper.snap(file));

        file.setOnMouseClicked(e -> {
        if (e.getClickCount() == 2) {
            openFile(file);
        }
        });

        desktopPane.getChildren().add(file);
    }

    private void openFile(File file) {
        Stage stage = new Stage();
        TextArea textArea = new TextArea();
        textArea.setPromptText("blah blah blah");
        Scene scene = new Scene(textArea, 500, 400);
        stage.setTitle(file.getFileName());
        stage.setScene(scene);
        stage.show();
    }
}
