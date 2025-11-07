package com.xen.oslab.managers.storage;

import com.google.gson.*;
import com.xen.oslab.managers.FileManager;
import com.xen.oslab.objects.File;
import com.xen.oslab.objects.Folder;

import javafx.scene.layout.Pane;

import java.io.IOException;
import java.nio.file.*;
import java.util.stream.Stream;

public class FileStorageManager extends DesktopStorageManager {
    private final Path filesDir = baseDir.resolve("files");
    private static final String FIELDID = "fieldId";
    private static final String JSON = ".json";

    public FileStorageManager() {
        try {
            if (!Files.exists(filesDir)) Files.createDirectories(filesDir);
        } catch (IOException e) {
            throw new NullPointerException();
        }
    }

    public void saveFile(File file) {
        if (file.getParentFolder() != null) {
            Folder parent = file.getParentFolder();
            FolderStorageManager folderStorage = parent.getStorage();
            folderStorage.saveFolder(parent);
            return;
        }

        Path path = baseDir.resolve(file.getFileId() + JSON);
        JsonObject fileObj = new JsonObject();
        fileObj.addProperty(FIELDID, file.getFileId());
        fileObj.addProperty("name", file.getFileName());
        fileObj.addProperty("x", file.getLayoutX());
        fileObj.addProperty("y", file.getLayoutY());
        fileObj.addProperty("content", file.getContent());
        write(path, gson.toJson(fileObj));
    }



    public File loadFile(Path path) {
        String content = read(path);
        JsonObject obj = JsonParser.parseString(content).getAsJsonObject();

        File file = new File(obj.get("name").getAsString());
        file.setLayoutX(obj.get("x").getAsDouble());
        file.setLayoutY(obj.get("y").getAsDouble());
        file.setContent(obj.get("content").getAsString());
        file.setFileId(obj.get(FIELDID).getAsString());
        return file;
    }

   public void loadAll(Pane desktop, FileManager fm) {
        try (Stream<Path> paths = Files.list(filesDir)) {
            paths.filter(p -> p.toString().endsWith(JSON)).forEach(path -> {
                try {
                    File file = loadFile(path);
                    fm.attachEvents(file, true);
                    desktop.getChildren().add(file);
                    fm.markOccupied(file);
                    int row = (int) (file.getLayoutY() / fm.getCellH());
                    int col = (int) (file.getLayoutX() / fm.getCellW());
                    file.setUserData(new int[]{row, col});
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void renameFile(String oldId, String newId) {
        Path oldPath = filesDir.resolve(oldId + JSON);
        Path newPath = filesDir.resolve(newId + JSON);
        try {
            if (Files.exists(oldPath)) {
                Files.move(oldPath, newPath, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteFromDesktop(String fileId) {
        try (Stream<Path> paths = Files.list(filesDir)) {
            paths.filter(p -> p.toString().endsWith(JSON)).forEach(path -> {
                String content = read(path);
                JsonObject obj = JsonParser.parseString(content).getAsJsonObject();
                if (obj.get(FIELDID).getAsString().equals(fileId)) {
                    try { Files.delete(path); } catch (IOException e) { e.printStackTrace(); }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
