package executor;

import networking.Phone;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

class Executor {

    public static void main(String[] args){
        listenForPhoneConnection();
    }

    private static void listenForPhoneConnection() {

        try {
            ServerSocket serverSocket = new ServerSocket(3999);


            while (true) {
                Socket socket = serverSocket.accept();

                // Checking the stream for input
                try {
                    ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());

                    Phone connectedPhone = (Phone) objectInputStream.readObject();
                    new PhoneConnection(connectedPhone).start();

                    socket.close();
                } catch (EOFException e){
                    e.printStackTrace();
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
