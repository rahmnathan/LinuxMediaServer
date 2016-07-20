package Server;

/*
 * @author Nathan Rahm
 * Created 12/2015
 */

import MovieData.DirectoryExplorer;
import Phone.Phone;
import PlayMovie.MoviePlayer;
import Thread.PhoneConnection;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    
    public static void main(String[] args){
        new Server().listenForPhoneConnection();
    }
    
    private void listenForPhoneConnection() {

        try {
            ServerSocket serverSocket = new ServerSocket(3999);

            while (true) {
                // Checking the stream for input
                Socket socket = serverSocket.accept();
                try {
                    ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
                    Phone connectedPhone = (Phone) objectInputStream.readObject();
                    new PhoneConnection(connectedPhone).start();
                    objectInputStream.close();
                } catch (EOFException e){
                    e.printStackTrace();
                }
                socket.close();
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    
    public void sendTitles(Phone connectedPhone){
        
        // This method sends current title list to the Android app
        
        try {
            Socket socket = new Socket(connectedPhone.getPhoneIP(), 3998);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectOutputStream.writeObject(new DirectoryExplorer(connectedPhone.getPath()).getTitles());
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
            // Play Movie at received Index

            new MoviePlayer(connectedPhone).start();

            listenForCommand();
        }
        else {
            sendTitles(connectedPhone);
        }
    }
}