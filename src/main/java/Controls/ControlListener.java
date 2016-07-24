package Controls;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class ControlListener {
    
    private enum controls {
        VOLUME_UP, VOLUME_DOWN, SEEK_FORWARD, SEEK_BACK, PLAY_PAUSE, STOP
    }
    
    public void start() {
        
        // Listening for controls from the app
        try{
            ServerSocket serverSocket = new ServerSocket(3995);

            while (serverSocket.isBound()) {

                Socket socket = serverSocket.accept();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
                String[] sort = bufferedReader.readLine().split("splithere159");
                controls control = controls.valueOf(sort[0]);
                
                new ProcessBuilder("/bin/bash", "-c", "xdotool search --name " + sort[1] +
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

                        socket.close();
                        serverSocket.close();
                        break;
                }
            }
        }catch(IOException | InterruptedException e){
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
