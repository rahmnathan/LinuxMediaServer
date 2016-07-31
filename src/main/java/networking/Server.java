package networking;

import player.MoviePlayer;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    
    public void send(Phone connectedPhone){
        
        // This method sends current title list to the Android app
        
        try {
            Socket socket = new Socket(connectedPhone.getPhoneIP(), 3998);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());

            objectOutputStream.writeObject(new DirectoryExplorer(connectedPhone.getPath()).getTitleList());

            socket.close();
            
            receive();
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }
    
    private void receive() {
                            
        // Listening for movie choice from android app

        try {
            ServerSocket serverSocket = new ServerSocket(3998);
            Socket socket = serverSocket.accept();
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());

            Phone connectedPhone = (Phone) objectInputStream.readObject();

            serverSocket.close();


            if (connectedPhone.isCasting()){

                new MoviePlayer(connectedPhone).start();

                receive();
            }
            else {
                send(connectedPhone);
            }
        } catch (IOException | ClassNotFoundException e){
            e.printStackTrace();
        }
    }
}