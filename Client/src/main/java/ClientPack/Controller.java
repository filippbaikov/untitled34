package ClientPack;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.ResourceBundle;


public class Controller  { //описывает работу клиента
    @FXML
    TextField textField, userField; // привязывает к chat в папке resources
    @FXML
    TextArea textArea; // привязывает к chat в папке resources
    @FXML
    HBox authPanel, mesPanel; // привязывает к chat в папке resources
    private Socket socket; // соединение
    private DataInputStream in; // входящий поток данных
    private DataOutputStream out; //исходящий поток данных




    public void sendMessage(ActionEvent actionEvent) { // отправляльщик сообщений по кнопке отправить или ентер
        try {
            out.writeUTF(textField.getText()); //исходящий потокб берущий текст из поля ввода
            textField.clear(); //очистить поле ввода, после написания и отправки из него текста
            textField.requestFocus(); //выделение поля или кнопки, в котором работаешь
        } catch (IOException e) {
            e.printStackTrace(); //лови исключение входящий исходящий поток

        }
    }

    public void authUser() {
        connect(); //метод соединение с сервером
        try {
            out.writeUTF("/auth " +userField.getText()); //поле авторизации
        } catch (IOException e) {
            showError("невозможно отправить зпрос авторизации на сервер");
        }
    }
    public void connect() {
        if (socket != null && !socket.isClosed()){ //если уже соединился, пропускаем этот цикл
            return;
        }
        try {
            socket = new Socket("localhost", 8189); //соединение с портом с сервером
            in = new DataInputStream(socket.getInputStream()); //входящий поток, принимающий данные из соединения
            out = new DataOutputStream(socket.getOutputStream()); //изходящий, отдающий данные
            Thread thread = new Thread(() -> { //обертка в отдельный поток
                try {
                    while (true) {
                        String inPutMessage = in.readUTF(); //входящее сообщение
                        if (inPutMessage.equals("/authok")){ //если авторизовался
                            mesPanel.setVisible(true); //панель ввода сообщений вкл
                            mesPanel.setManaged(true);
                            authPanel.setVisible(false); //панель авторизации выкл
                            authPanel.setVisible(false);
                            break;
                        }
                        textArea.appendText(inPutMessage + "\n"); //поле чата принимает текст
                    }

                while (true) {
                    String inPutMessage = in.readUTF();
                    textArea.appendText(inPutMessage + "\n"); // перенос строки
                }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            });
            thread.start(); //запуск потока
        } catch (IOException e) {
             showError("подключиться не получилось");
        }
    }
    public void showError(String message){
        new Alert(Alert.AlertType.ERROR,message, ButtonType.OK).showAndWait();
    }
}  //метод позволяет прописать возможные ошибки, включает кнопку ОК