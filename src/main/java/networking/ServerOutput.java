package networking;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ServerOutput {
    
    public void send(Phone connectedPhone){
        
        // This method sends current title list to the Android app
        
        try {
            Socket socket = new Socket(connectedPhone.getPhoneIP(), 3998);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());

            objectOutputStream.writeObject(new DirectoryExplorer(connectedPhone.getPath()).getTitleList());

            socket.close();
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }
    

}