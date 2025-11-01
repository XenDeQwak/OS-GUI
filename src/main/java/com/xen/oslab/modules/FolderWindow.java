package com.xen.oslab.modules;

import java.util.Map;
import com.xen.oslab.managers.DesktopMenuManager;
import com.xen.oslab.managers.FileManager;
import com.xen.oslab.managers.storage.FolderStorageManager;
import com.xen.oslab.objects.File;
import com.xen.oslab.objects.Folder;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.FlowPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class FolderWindow {
    public FolderWindow(Folder folder, FileManager fileManager) {
        FlowPane contentPane = new FlowPane(20, 20);
        contentPane.setPrefWidth(Double.MAX_VALUE);
        contentPane.setPrefHeight(Double.MAX_VALUE);

        FolderStorageManager storage = new FolderStorageManager();
        storage.loadFolder(folder);

        for (File dataFile : folder.getFiles()) {
            File fileNode = new File(dataFile.getFileName());
            fileNode.setContent(dataFile.getContent());
            fileNode.setLayoutX(dataFile.getLayoutX());
            fileNode.setLayoutY(dataFile.getLayoutY());
            fileManager.attachEvents(fileNode);
            fileNode.setOnMousePressed(event -> event.consume());
            contentPane.getChildren().add(fileNode);
        }

        for (Folder subFolder : folder.getSubFolders()) {
            Folder subFolderNode = new Folder(subFolder.getFolderName());
            attachFolderEvents(subFolderNode, fileManager);
            contentPane.getChildren().add(subFolderNode);
        }

        ScrollPane scroll = new ScrollPane(contentPane);
        scroll.setFitToWidth(true);
        scroll.setFitToHeight(true);

        Stage stage = new Stage();
        stage.setTitle(folder.getFolderName());
        stage.initModality(Modality.NONE);
        stage.setScene(new Scene(scroll, 500, 400));
        stage.show();

        DesktopMenuManager menuManager = new DesktopMenuManager(contentPane, Map.of(
            "New File", () -> fileManager.createFileInFolder(folder, contentPane, "New File", 0, 0),
            "New Folder", () -> {
                Folder newSub = new Folder("New Folder");
                attachFolderEvents(newSub, fileManager);
                folder.addFolder(newSub);
                contentPane.getChildren().add(newSub);
                new FolderStorageManager().saveFolder(folder);
            }
        ));

        contentPane.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.SECONDARY)
                menuManager.getMenu().show(contentPane, e.getScreenX(), e.getScreenY());
            else
                menuManager.getMenu().hide();
        });
    }

    private void attachFolderEvents(Folder folder, FileManager fileManager) {
        folder.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2)
                new FolderWindow(folder, fileManager);
        });
    }
}
