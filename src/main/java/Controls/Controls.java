package Controls;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author nathan
 */
public class Controls {
    
    private enum controls {
        VOLUME_UP, VOLUME_DOWN, SEEK_FORWARD, SEEK_BACK, PLAY_PAUSE, STOP
    }
    
    public void castControls() {
        
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
                        new ProcessBuilder("/bin/bash", "-c", "xdotool key space").start();
                        break;

                    case SEEK_BACK:
                        new ProcessBuilder("/bin/bash", "-c", "xdotool key Left").start();
                        break;

                    case SEEK_FORWARD:
                        new ProcessBuilder("/bin/bash", "-c", "xdotool key Right").start();
                        break;

                    case VOLUME_UP:
                        new ProcessBuilder("/bin/bash", "-c", "xdotool key Up").start();
                        break;

                    case VOLUME_DOWN:
                        new ProcessBuilder("/bin/bash", "-c", "xdotool key Down").start();
                        break;

                    case STOP:
                        new ProcessBuilder("/bin/bash", "-c", "xdotool key space").start();
                        new ProcessBuilder("/bin/bash", "-c", "xdotool type Q").start();                
                        serverSocket.close();
                        socket.close();
                        bufferedReader.close();
                        break;
                }
            }
        }catch(IOException | InterruptedException e){
            e.printStackTrace();
        }
    }
}
