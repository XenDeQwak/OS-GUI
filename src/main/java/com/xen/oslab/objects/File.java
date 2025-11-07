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
    private final String fileId = UUID.randomUUID().toString();
    private String fileName;
    private String content = "";
    private String filePath;
    private int row = -1;
    private int col = -1;
    private Label label;
    private TextField renameFile;

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

        ContextMenu contextMenu = new ContextMenu();
        MenuItem renameItem = new MenuItem("Rename File");
        renameItem.setOnAction(e -> startRename());
        contextMenu.getItems().add(renameItem);
        label.setOnContextMenuRequested(e -> {
            contextMenu.show(label, e.getScreenX(), e.getScreenY());
            e.consume();
        });

        renameFile.setOnAction(e -> finishRename());
        renameFile.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (!isNowFocused) finishRename();
        });

        setAlignment(Pos.CENTER);
        setSpacing(5);
    }

        private void startRename(){
            renameFile.setText(fileName);
            label.setVisible(false);
            renameFile.setVisible(true);
            renameFile.requestFocus();
            renameFile.selectAll();
        }

    private void finishRename() {
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

    
}

