package com.xen.oslab.modules;

import com.xen.oslab.managers.FileManager;
import com.xen.oslab.objects.File;
import com.xen.oslab.objects.Folder;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class FolderWindow {
    public FolderWindow(Folder folder, FileManager fileManager) {
        FlowPane contentPane = new FlowPane(20, 20);

        for (File dataFile : folder.getFiles()) {
            File fileNode = new File(dataFile.getFileName());
            fileNode.setContent(dataFile.getContent());
            fileNode.setLayoutX(dataFile.getLayoutX());
            fileNode.setLayoutY(dataFile.getLayoutY());
            fileManager.attachEvents(fileNode);
            
            fileNode.setOnMousePressed(event -> event.consume());
            
            contentPane.getChildren().add(fileNode);
        }

        ScrollPane scroll = new ScrollPane(contentPane);
        scroll.setFitToWidth(true);

        Stage stage = new Stage();
        stage.setTitle(folder.getFolderName());
        stage.initModality(Modality.NONE);
        stage.setScene(new Scene(scroll, 500, 400));
        stage.show();
    }

}


