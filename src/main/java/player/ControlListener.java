package player;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ControlListener {
    
    private enum Controls {
        VOLUME_UP, VOLUME_DOWN, SEEK_FORWARD, SEEK_BACK, PLAY_PAUSE, STOP
    }
    
    public void start() {
        
        // Listening for Controls from the app
        try{
            ServerSocket serverSocket = new ServerSocket(3995);

            while (serverSocket.isBound()) {

                Socket socket = serverSocket.accept();
                ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());

                String[] command = (String[]) objectInputStream.readObject();

                socket.close();

                Controls control = Controls.valueOf(command[0]);
                String phoneName = command[1];

                // Selecting window and executing control
                
                new ProcessBuilder("/bin/bash", "-c", "xdotool search --name " + phoneName +
                        " windowfocus").start();
                Thread.sleep(100);

                switch(control){
                    case PLAY_PAUSE: 
                        executeControl("space");
                        break;

                    case SEEK_BACK:
                        executeControl("Left");
                        break;

                    case SEEK_FORWARD:
                        executeControl("Right");
                        break;

                    case VOLUME_UP:
                        executeControl("Up");
                        break;

                    case VOLUME_DOWN:
                        executeControl("Down");
                        break;

                    case STOP:
                        executeControl("space");
                        stopStream();

                        serverSocket.close();
                        break;
                }
            }
        }catch(IOException | InterruptedException | ClassNotFoundException e){
            e.printStackTrace();
        }
    }

    private void executeControl(String key) throws IOException {
        new ProcessBuilder("/bin/bash", "-c", "xdotool key " + key).start();
    }

    private void stopStream() throws IOException {
        new ProcessBuilder("/bin/bash", "-c", "xdotool type Q").start();
    }

}
