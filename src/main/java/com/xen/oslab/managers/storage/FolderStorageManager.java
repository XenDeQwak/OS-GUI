package com.xen.oslab.managers.storage;

import com.google.gson.*;
import com.xen.oslab.managers.FolderManager;
import com.xen.oslab.objects.File;
import com.xen.oslab.objects.Folder;
import javafx.scene.layout.Pane;
import java.io.IOException;
import java.nio.file.*;
import java.util.stream.Stream;

public class FolderStorageManager extends DesktopStorageManager {

    public void saveFolder(Folder folder) {
        Path path = baseDir.resolve(folder.getFolderName() + ".json");
        JsonArray filesArray = new JsonArray();
        JsonArray subFoldersArray = new JsonArray();
        
        for (File f : folder.getFiles()) {
            JsonObject fileObj = new JsonObject();
            fileObj.addProperty("name", f.getFileName());
            fileObj.addProperty("x", f.getLayoutX());
            fileObj.addProperty("y", f.getLayoutY());
            fileObj.addProperty("content", f.getContent());
            filesArray.add(fileObj);
        }

        
        for (Folder subFolder : folder.getSubFolders()) {
            JsonObject subFolderObj = new JsonObject();
            subFolderObj.addProperty("name", subFolder.getFolderName());
            saveFolderRecursive(subFolder, subFolderObj);
            subFoldersArray.add(subFolderObj);
        }

        JsonObject folderObj = new JsonObject();
        folderObj.addProperty("name", folder.getFolderName());
        folderObj.add("files", filesArray);
        folderObj.add("subfolders", subFoldersArray);
        folderObj.addProperty("x", folder.getLayoutX());
        folderObj.addProperty("y", folder.getLayoutY());
        write(path, gson.toJson(folderObj));
    }

    private void saveFolderRecursive(Folder folder, JsonObject subFolderObj) {
        JsonArray filesArray = new JsonArray();
        for (File f : folder.getFiles()) {
            JsonObject fileObj = new JsonObject();
            fileObj.addProperty("name", f.getFileName());
            fileObj.addProperty("x", f.getLayoutX());
            fileObj.addProperty("y", f.getLayoutY());
            fileObj.addProperty("content", f.getContent());
            filesArray.add(fileObj);
        }

        JsonArray subFoldersArray = new JsonArray();
        for (Folder subFolder : folder.getSubFolders()) {
            JsonObject nestedSubFolderObj = new JsonObject();
            nestedSubFolderObj.addProperty("name", subFolder.getFolderName());
            saveFolderRecursive(subFolder, nestedSubFolderObj);
            subFoldersArray.add(nestedSubFolderObj);
        }

        subFolderObj.add("files", filesArray);
        subFolderObj.add("subfolders", subFoldersArray);
    }

    public void loadFolder(Folder folder) {
        Path path = baseDir.resolve(folder.getFolderName() + ".json");
        if (!Files.exists(path)) return;

        folder.getFiles().clear();
        folder.getSubFolders().clear();

        String content = read(path);
        JsonObject folderObj = JsonParser.parseString(content).getAsJsonObject();
        JsonArray filesArray = folderObj.getAsJsonArray("files");
        JsonArray subFoldersArray = folderObj.getAsJsonArray("subfolders");

        for (JsonElement e : filesArray) {
            JsonObject fileObj = e.getAsJsonObject();
            File fileData = new File(fileObj.get("name").getAsString());
            fileData.setContent(fileObj.get("content").getAsString());
            fileData.setLayoutX(fileObj.get("x").getAsDouble());
            fileData.setLayoutY(fileObj.get("y").getAsDouble());
            folder.addFile(fileData);
        }

        for (JsonElement e : subFoldersArray) {
            JsonObject subFolderObj = e.getAsJsonObject();
            Folder subFolder = new Folder(subFolderObj.get("name").getAsString());
            loadFolderRecursive(subFolder, subFolderObj);
            folder.addFolder(subFolder);
        }
    }


    public void storeInFolder(String parentName, String childName) {
        Path foldersDir = baseDir.resolve("folders");
        Path parentPath = foldersDir.resolve(parentName + ".json");
        Path childPath = foldersDir.resolve(childName + ".json");
        if (!Files.exists(parentPath) || !Files.exists(childPath)) return;

        try {
            String parentContent = Files.readString(parentPath);
            String childContent = Files.readString(childPath);

            JsonObject parentObj = JsonParser.parseString(parentContent).getAsJsonObject();
            JsonObject childObj = JsonParser.parseString(childContent).getAsJsonObject();

            JsonArray subFolders = parentObj.has("subfolders") && parentObj.get("subfolders").isJsonArray()
                    ? parentObj.getAsJsonArray("subfolders")
                    : new JsonArray();

            subFolders.add(childObj);
            parentObj.add("subfolders", subFolders);

            Files.writeString(parentPath, gson.toJson(parentObj),
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

            Files.deleteIfExists(childPath);

            System.out.println("IT HAPPENED");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void loadFolderRecursive(Folder folder, JsonObject subFolderObj) {
        JsonArray filesArray = subFolderObj.getAsJsonArray("files");
        JsonArray subFoldersArray = subFolderObj.getAsJsonArray("subfolders");

        
        for (JsonElement e : filesArray) {
            JsonObject fileObj = e.getAsJsonObject();
            File fileData = new File(fileObj.get("name").getAsString());
            fileData.setContent(fileObj.get("content").getAsString());
            fileData.setLayoutX(fileObj.get("x").getAsDouble());
            fileData.setLayoutY(fileObj.get("y").getAsDouble());
            folder.addFile(fileData);
        }

        
        for (JsonElement e : subFoldersArray) {
            JsonObject nestedSubFolderObj = e.getAsJsonObject();
            Folder nestedSubFolder = new Folder(nestedSubFolderObj.get("name").getAsString());
            loadFolderRecursive(nestedSubFolder, nestedSubFolderObj);
            folder.addFolder(nestedSubFolder);
        }
    }

    public void loadAll(Pane desktopPane, FolderManager folderManager) {
        try (Stream<Path> paths = Files.list(baseDir)) {
            paths.filter(p -> p.toString().endsWith(".json")).forEach(path -> {
                try {
                    String content = Files.readString(path);
                    JsonElement root = JsonParser.parseString(content);
                    if (!root.isJsonObject()) return;

                    JsonObject folderObj = root.getAsJsonObject();
                    if (!folderObj.has("name") || !folderObj.has("files")) return;

                    String folderName = folderObj.get("name").getAsString();
                    double x = folderObj.get("x").getAsDouble();
                    double y = folderObj.get("y").getAsDouble();

                    Folder folder = folderManager.createFolderAt(folderName, x, y);
                    loadFolder(folder);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void renameFolder(String oldName, String newName) {
        Path oldPath = baseDir.resolve(oldName + ".json");
        Path newPath = baseDir.resolve(newName + ".json");

        try {
            if (Files.exists(oldPath)) {
                Files.move(oldPath, newPath, StandardCopyOption.REPLACE_EXISTING);
            } else {
                System.err.println("Old folder file not found: " + oldPath);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
