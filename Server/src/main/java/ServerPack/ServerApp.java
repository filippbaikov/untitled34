package ServerPack;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerApp {
    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(8189);
            System.out.println("Сервер запущен, ждем подключения клиентов");

            Socket socket = serverSocket.accept();
            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            System.out.println("клиент подключился");
            while (true) {
                String inPutMes = in.readUTF();
                System.out.println(inPutMes);
                out.writeUTF("echo: " + inPutMes);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
