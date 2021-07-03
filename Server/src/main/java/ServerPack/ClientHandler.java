package ServerPack;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler {
    private Server server;
    private Socket socket;
    private DataOutputStream out;
    private DataInputStream in;
    private String userName;
    public String getUserName(){

         return userName;
    }


    public ClientHandler(Server server, Socket socket ) {

        try {
            this.server=server;
            this.socket = socket;
            this.in = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());
            new Thread(() -> logic() ).start();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public void sendMessage(String message){ // исходящее сообщение
        try {
            out.writeUTF(message);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
    private void logic(){
        try {
            while (consumeAuthorizeMessage(in.readUTF()));
            while (consumeRegularMessage(in.readUTF()));
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            System.out.println(userName+" от сервера отключился ");
            server.subscribe(this); //отключение клиента
            closeConnection();
        }
    }
    private boolean consumeRegularMessage(String inPutMessage) {

        if (inPutMessage.startsWith("/")) {
            if (inPutMessage.equals("/exit")) {
                sendMessage("/exit");
                return false;
            }
            if (inPutMessage.startsWith("/w ")) {
                String[] tokens = inPutMessage.split("\\s+", 3);
                server.sendPersonalMessage(this, tokens[1], tokens[2]);
            }
            return true;
        }
        server.broadcastMessage(userName + ": " + inPutMessage);
        return true;
    }
    private boolean consumeAuthorizeMessage(String message){
        if (message.startsWith("/auth")) {
            String[] tokens=message.split("\\s+");
            if (tokens.length==1){
                sendMessage("SERVER: вы не указали имя пользователя");
                return false;
            }
            if (tokens.length>2){
                sendMessage("SERVER: имя пользователя не может состоять из нескольких слов");
                return false;
            }
            String selectedUserName=tokens[1];
            if (server.isUserNameUsed(selectedUserName)){
                sendMessage("SERVER: имя занято");
                return false;
            }
            userName=selectedUserName;

            sendMessage("/authok");
            server.subscribe(this);

            return true;
        } else {
            sendMessage("SERVER: ты должен авторизоваться");
            return false;
        }
    }

    private void closeConnection(){

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


    }



