package com.xen.oslab.modules;

import java.time.LocalDateTime;

import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

public class CLIWindow {

    private VBox root = new VBox();
    private TextArea console = new TextArea();
    private String prompt = "> ";

    public CLIWindow() {
        root.getChildren().add(console);

        root.setStyle("-fx-background-color: black;");
        console.setStyle("-fx-control-inner-background: black; -fx-text-fill: white; -fx-font-family: 'Consolas'; -fx-font-size: 14;");
        console.setWrapText(true);
        console.setFont(Font.font("Consolas", 14));
        console.setEditable(true);

        console.setText(prompt);
        console.positionCaret(console.getText().length());

        console.prefWidthProperty().bind(root.widthProperty());
        console.prefHeightProperty().bind(root.heightProperty());

       console.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                e.consume();

                int promptIndex = console.getText().lastIndexOf(prompt);
                String cmd = "";
                if (promptIndex != -1) {
                    cmd = console.getText().substring(promptIndex + prompt.length()).trim();
                }

                if (!cmd.isEmpty()) {
                    String result = runCommand(cmd);
                    if (!result.isBlank()) console.appendText("\n" + result);
                }

                console.appendText("\n" + prompt);
                console.positionCaret(console.getText().length());
            }

            if (e.getCode() == KeyCode.BACK_SPACE) {
                if (isDeletingPrompt()) e.consume();
            }
        });



        console.setOnKeyTyped(e -> {
            if (e.getCharacter().equals("\n")) e.consume();
        });
    }

    private boolean isDeletingPrompt() {
        int caret = console.getCaretPosition();
        int promptIndex = console.getText().lastIndexOf(prompt);
        return caret <= promptIndex + prompt.length();
    }

    private String runCommand(String cmd) {
        cmd = cmd.trim();
        String command = cmd.split("\\s+")[0].toLowerCase(); 
        
        switch (command) { 
            case "help": return "Commands: help, time, clear, exit, ping";
            case "time": return LocalDateTime.now().toString();
            case "clear": 
                console.setText(prompt); 
                return "";
            case "exit": 
                root.getScene().getWindow().hide(); 
                return "";
            case "ping":
                return handlePing(cmd);
            default: return "Unknown command: " + cmd;
        }
    }

    private String handlePing(String cmd) {
        String[] parts = cmd.split("\\s+");
        if (parts.length != 2) return "Usage: ping <IP>";
        
        String ipToPing = parts[1];
        String currentIP = SettingsWindow.currentIP;

        new Thread(() -> {
            for (int i = 1; i <= 4; i++) {
                String reply;
                if (ipToPing.equals(currentIP)) {
                    reply = "Reply from " + ipToPing + ": bytes=32 time=" + (10 + i*5) + "ms TTL=64";
                } else {
                    reply = "Request timed out.";
                }

                String finalReply = reply;
                javafx.application.Platform.runLater(() -> {
                    console.appendText("\n" + finalReply);
                });

                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            javafx.application.Platform.runLater(() -> console.appendText("\n" + prompt));
        }).start();

        return "";
    }


    public VBox getNode() {
        return root;
    }
}
