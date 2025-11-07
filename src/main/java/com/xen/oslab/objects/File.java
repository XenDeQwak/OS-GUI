package com.xen.oslab.objects;

import java.util.UUID;

import com.xen.oslab.managers.LoadManager;
import javafx.geometry.Pos;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class File extends VBox {
    private String fileId = UUID.randomUUID().toString();
    private String fileName;
    private String content = "";
    private String filePath;
    private int row = -1;
    private int col = -1;
    private Label label;
    private TextField renameFile;
    private Folder parentFolder;

    public File(String name) {
        this.fileName = name;

        LoadManager image = new LoadManager();

        Image fileIcon = image.load("File.png");
        ImageView fileImage = new ImageView(fileIcon);
        fileImage.setFitWidth(48);
        fileImage.setFitHeight(48);

        label = new Label(name);
        label.setAlignment(Pos.CENTER);
        label.setWrapText(true);
        label.setMaxWidth(80);
        label.setTextFill(Color.WHITE);

        renameFile = new TextField(fileName);
        renameFile.setAlignment(Pos.CENTER);
        renameFile.setMaxWidth(80);
        renameFile.setVisible(false);
        
        getChildren().addAll(fileImage, label, renameFile);
        setAlignment(Pos.CENTER);
        setSpacing(5);
    }

        public void startRename(){
            renameFile.setText(fileName);
            label.setVisible(false);
            renameFile.setVisible(true);
            renameFile.requestFocus();
            renameFile.selectAll();
        }

    public void finishRename() {
        String newName = renameFile.getText().trim();
        if (!newName.isEmpty()) {
            fileName = newName;
            label.setText(newName);
        }
        renameFile.setVisible(false);
        label.setVisible(true);
    }


    public String getFileName() { return fileName; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }

    public int getRow() { return row; }
    public int getCol() { return col; }
    
    public void setPosition(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public Folder getParentFolder() { return parentFolder; }
    public void setParentFolder(Folder folder) { this.parentFolder = folder; }

    public TextField getRenameFile() {
        return renameFile;
    }

    
}

