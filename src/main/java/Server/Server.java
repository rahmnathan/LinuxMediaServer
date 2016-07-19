package Server;

/*
 * @author Nathan Rahm
 * Created 12/2015
 */

import MovieData.MainFile;
import Phone.Phone;
import PlayMovie.PlayMovie;
import Thread.listenServer;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    
    public static void main(String[] args){
        new Server().phoneListen();
    }
    
    private void phoneListen() {

        try {
            ServerSocket serverSocket = new ServerSocket(3999);

            while (true) {
                // Checking the stream for input
                Socket socket = serverSocket.accept();
                try {
                    ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
                    Phone connectedPhone = (Phone) objectInputStream.readObject();
                    new listenServer(connectedPhone).start();
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
    
    public void send(Phone connectedPhone){
        
        // This method sends current title list to the Android app
        
        try {
            Socket socket = new Socket(connectedPhone.getPhoneIP(), 3998);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectOutputStream.writeObject(new MainFile(connectedPhone.getPath()).getTitles());
            socket.close();
            
            server();
        }
        catch(IOException | ClassNotFoundException e){
            e.printStackTrace();
        }
    }
    
    private void server() throws IOException, ClassNotFoundException {
                            
        // Listening for movie choice from android app

        ServerSocket serverSocket = new ServerSocket(3998);
        Socket socket = serverSocket.accept();
        ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
        Phone connectedPhone = (Phone) objectInputStream.readObject();
        objectInputStream.close();
        serverSocket.close();
        socket.close();

        if (connectedPhone.isCasting()){
            // Play Movie at received Index

            new PlayMovie(connectedPhone).start();

            server();
        }
        else {
            send(connectedPhone);
        }
    }
}