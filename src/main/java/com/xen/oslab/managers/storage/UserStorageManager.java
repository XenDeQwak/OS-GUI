package com.xen.oslab.managers.storage;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class UserStorageManager {
    private static final String FILE_PATH = "desktop_files/users.json";
    private static Map<String, String> users = new HashMap<>();

    static {
        loadUsers();
    }

    private static void loadUsers() {
        File file = new File(FILE_PATH);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            JsonObject obj = JsonParser.parseReader(reader).getAsJsonObject();
            for (String key : obj.keySet()) {
                users.put(key, obj.get(key).getAsString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static boolean validateUser(String username, String password) {
        if (!users.containsKey(username)) return false;
        return users.get(username).equals(hashPassword(password));
    }

    private static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (byte b : hash) hex.append(String.format("%02x", b));
            return hex.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}
