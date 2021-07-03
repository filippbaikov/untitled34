package ClientPack;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;


public class Controller  { //описывает работу клиента
    @FXML
    TextField textField, userField; // привязывает к chat в папке resources
    @FXML
    TextArea textArea; // привязывает к chat в папке resources
    @FXML
    HBox authPanel, mesPanel;
    @FXML
    ListView<String>ClientsListView;
    @FXML
    TextField MyName;

    private Socket socket; // соединение
    private DataInputStream in; // входящий поток данных
    private DataOutputStream out; //исходящий поток данных


    public void setAuthorized( boolean authorized){
        mesPanel.setVisible(authorized);
        mesPanel.setManaged(authorized);
        authPanel.setVisible(!authorized);
        authPanel.setManaged(!authorized);
        ClientsListView.setVisible(authorized);
        ClientsListView.setManaged(authorized);
        MyName.setVisible(authorized);
        MyName.setManaged(authorized);

        }




    public void sendMessage(ActionEvent actionEvent) { // отправляльщик сообщений по кнопке отправить или ентер
        try {
            out.writeUTF(textField.getText()); //исходящий потокб берущий текст из поля ввода
            textField.clear(); //очистить поле ввода, после написания и отправки из него текста
            textField.requestFocus(); //выделение поля или кнопки, в котором работаешь
        } catch (IOException e) {

            showError("отправить сообщение на сервер не получилось");

        }
    }
    public void sendCloseRequest(){
        try {
            if (out!=null) {
                out.writeUTF("/exit");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void authUser() {
        connect(); //метод соединение с сервером
        try {
            out.writeUTF("/auth " +userField.getText()); //поле авторизации
            MyName.appendText(userField.getText());

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
             new Thread(() -> Logic()).start(); //запуск потока
        } catch (IOException e) {
             showError("подключиться не получилось");
        }
    }
    public void showError(String message){
        new Alert(Alert.AlertType.ERROR,message, ButtonType.OK).showAndWait();
    }
    private void closeConnection(){
        setAuthorized(false);
        try {
            if (in !=null){
                in.close();}
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            if (out !=null){
                out.close();}
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            if (socket !=null){
                socket.close();}
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

   private void Logic(){
       try {
           while (true) {
               String inPutMessage = in.readUTF();
               if (inPutMessage.equals("/exit")) {
                   closeConnection();
               }
               if (inPutMessage.equals("/authok")){
                   setAuthorized(true);
                   break;
               }
               textArea.appendText(inPutMessage + "\n"); //поле чата принимает текст
           }

           while (true) {
               String inPutMessage = in.readUTF();
               if (inPutMessage.startsWith("/")){
                   if (inPutMessage.equals("/exit")){
                       break;
                   }
                   if (inPutMessage.startsWith("/clients_list")){
                       Platform.runLater(()-> {
                           String[] tokens = inPutMessage.split("\\s+");
                           ClientsListView.getItems().clear();
                           for (int i = 1; i < tokens.length; i++) {
                               ClientsListView.getItems().add(tokens[i]);


                           }
                       });
                   }
                   continue;
               }
               textArea.appendText(inPutMessage + "\n"); // перенос строки
           }
       } catch (IOException e) {
           e.printStackTrace();
       }finally {
           closeConnection();
       }

   }


    public void ClientsListDoubleClick(MouseEvent mouseEvent) {
        if (mouseEvent.getClickCount()==2){
            String selectedUser= ClientsListView.getSelectionModel().getSelectedItem();
            textField.setText("/w "+ selectedUser+ " ");
            textField.requestFocus();
            textField.selectEnd();
        }

    }





        }






