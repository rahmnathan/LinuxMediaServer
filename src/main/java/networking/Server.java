package networking;

import player.MoviePlayer;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    
    public void sendTitles(Phone connectedPhone){
        
        // This method sends current title list to the Android app
        
        try {
            Socket socket = new Socket(connectedPhone.getPhoneIP(), 3998);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());

            objectOutputStream.writeObject(new DirectoryExplorer(connectedPhone.getPath()).getTitleList());

            socket.close();
            
            listenForCommand();
        }
        catch(IOException | ClassNotFoundException e){
            e.printStackTrace();
        }
    }
    
    private void listenForCommand() throws IOException, ClassNotFoundException {
                            
        // Listening for movie choice from android app

        ServerSocket serverSocket = new ServerSocket(3998);
        Socket socket = serverSocket.accept();
        ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());

        Phone connectedPhone = (Phone) objectInputStream.readObject();

        serverSocket.close();

        if (connectedPhone.isCasting()){

            new MoviePlayer(connectedPhone).start();

            listenForCommand();
        }
        else {
            sendTitles(connectedPhone);
        }
    }
}