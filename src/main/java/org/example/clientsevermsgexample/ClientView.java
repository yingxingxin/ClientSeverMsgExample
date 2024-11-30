package org.example.clientsevermsgexample;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import java.io.DataOutputStream;
import java.net.Socket;

public class ClientView {

    @FXML
    private TextField tf_message;

    @FXML
    private VBox vbox_messages;

    private Socket socket;
    private DataOutputStream dos;

    public ClientView() {

        try {
            // Server connection
            socket = new Socket("localhost", 6666);
            dos = new DataOutputStream(socket.getOutputStream());

            new Thread(() -> {
                try (var dis = new java.io.DataInputStream(socket.getInputStream())) {

                    String message;

                    while ((message = dis.readUTF()) != null) {
                        updateChat("Server: " + message);
                    }
                } catch (Exception e) {
                    updateChat("Connection closed: " + e.getMessage());
                }

            }).start();
        } catch (Exception e) {
            updateChat("Error connecting to server: " + e.getMessage());
        }
    }

    @FXML
    public void sendMessage(ActionEvent actionEvent) {
        String message = tf_message.getText();
        if (message.isEmpty()) {
            return;
        }

        try {
            dos.writeUTF(message);
            updateChat("You: " + message);
            tf_message.clear();

        } catch (Exception e) {
            updateChat("Error sending a message: " + e.getMessage());
        }
    }

    private void updateChat(String message) {
        Platform.runLater(() -> vbox_messages.getChildren().add(new Text(message)));
    }
}
