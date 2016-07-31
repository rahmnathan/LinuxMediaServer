package networking;

import player.MoviePlayer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ClientInput {

    public void receive() {

        ServerOutput server = new ServerOutput();

        // Listening for movie choice from android app

        while(true) {
            try {
                ServerSocket serverSocket = new ServerSocket(3998);
                Socket socket = serverSocket.accept();
                ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());

                Phone connectedPhone = (Phone) objectInputStream.readObject();

                serverSocket.close();

                if (connectedPhone.isCasting()) {

                    new MoviePlayer(connectedPhone).start();

                } else {
                    server.send(connectedPhone);
                }

            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}