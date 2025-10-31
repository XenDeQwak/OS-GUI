package com.xen.oslab.objects;

import java.util.ArrayList;
import java.util.List;

import com.xen.oslab.managers.LoadManager;
import com.xen.oslab.managers.storage.FolderStorageManager;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

public class Folder extends VBox {
    private String folderName;
    private final List<File> files = new ArrayList<>();
    private final FolderStorageManager storage;

    public Folder(String name) {
        this.folderName = name;
        this.storage = new FolderStorageManager();

        LoadManager image = new LoadManager();
        Image folderIcon = image.load("folder.png");
        ImageView folderImage = new ImageView(folderIcon);
        folderImage.setFitWidth(48);
        folderImage.setFitHeight(48);
        Label label = new Label(name);
        label.setAlignment(Pos.CENTER);
        label.setWrapText(true);
        label.setMaxWidth(80);
        setAlignment(Pos.CENTER);
        setSpacing(5);
        getChildren().addAll(folderImage, label);
    }

    public void addFile(File f) { files.add(f); }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public List<File> getFiles() {
        return files;
    }

    public FolderStorageManager getStorage() {
        return storage;
    }

    
    

}
