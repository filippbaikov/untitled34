package ClientPack;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.ResourceBundle;


public class Controller implements Initializable{
    @FXML
    TextField textField;
    @FXML
    TextArea textArea;
    private Socket socket;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            socket=new Socket("localhost",8189);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("подключиться не получилось");
            System.exit(0);
        }
    }
    public void sendMessage(ActionEvent actionEvent) {
        try {
            socket.getOutputStream().write(textField.getText().getBytes(StandardCharsets.UTF_8));
            textArea.appendText(textField.getText()+ "\n");
            textField.clear();
        } catch (IOException e) {
            e.printStackTrace();
            
        }
    }
}