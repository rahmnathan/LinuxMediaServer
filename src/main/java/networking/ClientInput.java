package networking;

import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ClientInput {

    public Phone receive() {

        try {

            ServerSocket serverSocket = new ServerSocket(3998);
            Socket socket = serverSocket.accept();

            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());

            Phone connectedPhone = (Phone) objectInputStream.readObject();

            socket.close();
            serverSocket.close();

            return connectedPhone;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}