package org.example.clientsevermsgexample;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerView {

    @FXML
    private TextField tf_message;

    @FXML
    private VBox vbox_messages;

    private ServerSocket serverSocket;
    private Socket clientSocket;
    private DataOutputStream dos;

    public ServerView() {
        try {
            // Server connection
            serverSocket = new ServerSocket(6666);
            updateChat("Server is running and waiting for clients...");

            new Thread(() -> {
                try {
                    clientSocket = serverSocket.accept();
                    updateChat("Client is connected");

                    dos = new DataOutputStream(clientSocket.getOutputStream());

                    new Thread(() -> {
                        try (var dis = new java.io.DataInputStream(clientSocket.getInputStream())) {
                            String message;
                            while ((message = dis.readUTF()) != null) {
                                updateChat("Client: " + message);
                            }
                        } catch (Exception e) {
                            updateChat("Connection closed: " + e.getMessage());
                        }
                    }).start();
                } catch (Exception e) {
                    updateChat("Error accepting client: " + e.getMessage());
                }
            }).start();
        } catch (Exception e) {
            updateChat("Error starting server: " + e.getMessage());
        }
    }

    @FXML
    public void sendMessage(ActionEvent actionEvent) {
        String message = tf_message.getText();
        if (message.isEmpty())  {
            return;
        }

        try {
            dos.writeUTF(message);
            updateChat("You: " + message);
            tf_message.clear();
        } catch (Exception e) {
            updateChat("Error sending message: " + e.getMessage());
        }
    }

    private void updateChat(String message) {
        Platform.runLater(() -> vbox_messages.getChildren().add(new Text(message)));
    }
}
