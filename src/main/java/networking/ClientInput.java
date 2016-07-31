package networking;

import player.MoviePlayer;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ClientInput {

    public void receive() {

        ServerOutput server = new ServerOutput();

        // Listening for movie choice from android app

        try {

            ServerSocket serverSocket = new ServerSocket(3998);
            Socket socket;

            while (true) {
                Phone connectedPhone;
                socket = serverSocket.accept();

                try {
                    ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());

                    connectedPhone = (Phone) objectInputStream.readObject();

                    socket.close();

                    if (connectedPhone.isCasting()) {
                        new MoviePlayer(connectedPhone).start();
                    } else {
                        server.send(connectedPhone);
                    }

                } catch (EOFException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}