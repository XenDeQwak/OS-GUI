package com.xen.oslab.managers.storage;

import com.google.gson.*;
import com.xen.oslab.managers.FolderManager;
import com.xen.oslab.objects.File;
import com.xen.oslab.objects.Folder;
import java.io.IOException;
import java.nio.file.*;
import java.util.stream.Stream;

public class FolderStorageManager extends DesktopStorageManager {

    private boolean isLoading = false;
    private static final String JSON = ".json";
    private static final String FOLDERID = "folderId";
    private static final String PARENTID = "parentId";
    private static final String SUBFOLDERS = "subfolders";

    public void saveFolder(Folder folder) {
        if (isLoading) return;

        if (folder.getParentFolder() != null) {
            saveFolder(folder.getParentFolder());
            return;
        }

        JsonObject folderObj = buildFolderJson(folder);
        Path path = baseDir.resolve(folder.getFolderId() + JSON);
        write(path, gson.toJson(folderObj));
    }




    private JsonObject buildFolderJson(Folder folder) {
        JsonObject folderObj = new JsonObject();
        folderObj.addProperty("name", folder.getFolderName());
        folderObj.addProperty(FOLDERID, folder.getFolderId());
        folderObj.addProperty(PARENTID, folder.getParentFolder() != null ? folder.getParentFolder().getFolderId() : null);
        folderObj.addProperty("x", folder.getLayoutX());
        folderObj.addProperty("y", folder.getLayoutY());

        JsonArray filesArray = new JsonArray();
        folder.getFiles().stream()
            .distinct()
            .forEach(f -> {
                JsonObject fileObj = new JsonObject();
                fileObj.addProperty("name", f.getFileName());
                fileObj.addProperty("x", f.getLayoutX());
                fileObj.addProperty("y", f.getLayoutY());
                fileObj.addProperty("content", f.getContent());
                filesArray.add(fileObj);
            });
        folderObj.add("files", filesArray);


        JsonArray subFoldersArray = new JsonArray();
        for (Folder subFolder : folder.getSubFolders()) {
            subFoldersArray.add(buildFolderJson(subFolder));
        }
        folderObj.add(SUBFOLDERS, subFoldersArray);

        return folderObj;
    }

    public void loadFolder(Folder folder) {
        Path path = baseDir.resolve(folder.getFolderId() + JSON);
        if (!Files.exists(path)) return;

        folder.getFiles().clear();
        folder.getSubFolders().clear();
        
        String content = read(path);
        JsonObject folderObj = JsonParser.parseString(content).getAsJsonObject();
        loadFolderData(folder, folderObj);
    }

    private void loadFolderData(Folder folder, JsonObject folderObj) {
        folder.setFolderName(folderObj.get("name").getAsString());
        folder.setLayoutX(folderObj.get("x").getAsDouble());
        folder.setLayoutY(folderObj.get("y").getAsDouble());

        JsonArray filesArray = folderObj.getAsJsonArray("files");
        JsonArray subFoldersArray = folderObj.getAsJsonArray(SUBFOLDERS);

        folder.getFiles().clear();
        folder.getSubFolders().clear();

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
            subFolder.setParentFolder(folder);
            subFolder.setFolderId(subFolderObj.get(FOLDERID).getAsString());
            loadFolderData(subFolder, subFolderObj);
            folder.addFolder(subFolder);
        }
    }

    public void storeInFolder(String parentId, String childId) {
        Path foldersDir = baseDir.resolve("folders");
        Path parentPath = foldersDir.resolve(parentId + JSON);
        Path childPath = foldersDir.resolve(childId + JSON);
        if (!Files.exists(parentPath) || !Files.exists(childPath)) return;

        try {
            String parentContent = Files.readString(parentPath);
            String childContent = Files.readString(childPath);

            JsonObject parentObj = JsonParser.parseString(parentContent).getAsJsonObject();
            JsonObject childObj = JsonParser.parseString(childContent).getAsJsonObject();

            JsonArray subFolders = parentObj.has(SUBFOLDERS) && parentObj.get(SUBFOLDERS).isJsonArray()
                    ? parentObj.getAsJsonArray(SUBFOLDERS)
                    : new JsonArray();

            subFolders.add(childObj);
            parentObj.add(SUBFOLDERS, subFolders);

            Files.writeString(parentPath, gson.toJson(parentObj),
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

            Files.deleteIfExists(childPath);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadAll(FolderManager folderManager) {
        isLoading = true;
        try (Stream<Path> paths = Files.list(baseDir)) {
            paths.filter(p -> p.toString().endsWith(JSON)).forEach(path -> {
                try {
                    String content = Files.readString(path);
                    JsonElement root = JsonParser.parseString(content);
                    if (!root.isJsonObject()) return;

                    JsonObject folderObj = root.getAsJsonObject();
                    if (!folderObj.has(FOLDERID)) return;

                    if (folderObj.has(PARENTID) && !folderObj.get(PARENTID).isJsonNull()) return;

                    String folderName = folderObj.get("name").getAsString();
                    double x = folderObj.get("x").getAsDouble();
                    double y = folderObj.get("y").getAsDouble();

                    Folder folder = folderManager.createFolderAt(folderName, x, y);
                    if (folderObj.has(FOLDERID)) folder.setFolderId(folderObj.get(FOLDERID).getAsString());
                    loadFolder(folder);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            isLoading = false;
        }
    }


    public void renameFolder(String oldId, String newId) {
        Path oldPath = baseDir.resolve(oldId + JSON);
        Path newPath = baseDir.resolve(newId + JSON);
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

    public boolean isLoading() {
        return isLoading;
    }

    public void deleteFolder(Folder folder) {
        if (folder == null) return;

        if (!folder.getSubFolders().isEmpty()) {
            for (Folder sub : folder.getSubFolders()) {
                deleteFolder(sub);
            }
        }

        Folder parent = folder.getParentFolder();
        if (parent != null) {
            parent.getSubFolders().remove(folder);
        }

        Path path = baseDir.resolve(folder.getFolderId() + JSON);
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (parent != null) {
            saveFolder(parent);
        }
    }
    
}
