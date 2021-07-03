package ServerPack;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server { //работа сервера
    private List<ClientHandler> clients; // список клиентов

    public Server() {
        try {
            this.clients=new ArrayList<>(); //новый клиент
            ServerSocket serverSocket = new ServerSocket(8189); //порт сервера
            System.out.println("Сервер запущен, ждем подключения клиентов");
            while (true) {
                Socket socket = serverSocket.accept(); //сервер ждет клиента
                System.out.println("новый клиент подключился");
                new ClientHandler(this,socket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public synchronized void subscribe(ClientHandler c){
        clients.add(c);
        broadcastMessage("присоединился пользователь: "+ c.getUserName());
        broadcastClientList();


    }
    public synchronized void unsubscribe(ClientHandler c){ //отключение клиента, не смог прописать вывод в текстЭреа

        clients.remove(c);
        broadcastMessage("отсоединился пользователь: "+ c.getUserName());
        broadcastClientList();

    }
    public synchronized void broadcastMessage(String message){ //рассылка сообщения всем от клиента
        for (ClientHandler c:clients){
            c.sendMessage(message);
        }
    }
    public synchronized boolean isUserNameUsed(String userName){
        for (ClientHandler c: clients){
            if (c.getUserName().equalsIgnoreCase(userName)){
            return true;
            }
        } return false;
    }
    public synchronized void broadcastClientList(){
        StringBuilder builder = new StringBuilder(clients.size()*10);
        builder.append("/clients_list ");
        for (ClientHandler c:clients){
            builder.append(c.getUserName()).append(" ");
                    }
        String clientsListStr=builder.toString();
        broadcastMessage(clientsListStr);
    }
    public synchronized void sendPersonalMessage(ClientHandler sender,String receiverUsername, String message){
        if (sender.getUserName().equalsIgnoreCase(receiverUsername)){
            sender.sendMessage("нельзя сам себе отправлять сообщения, баран!!!");
            return;
        }
        for (ClientHandler c:clients){
            if(c.getUserName().equalsIgnoreCase(receiverUsername)){
                c.sendMessage("от "+ sender.getUserName()+ ": "+ message);
                sender.sendMessage("пользователю "+ receiverUsername + ": "+message);
                return;
            }
        }
        sender.sendMessage("пользователь"+ receiverUsername+ "не в сети");
    }
}
