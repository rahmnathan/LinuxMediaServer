package player;

import networking.DeviceConnection;

import java.io.IOException;

public class MoviePlayer extends Thread {

    private final DeviceConnection connectedDeviceConnection;
    
    public MoviePlayer(DeviceConnection connectedDeviceConnection){
        this.connectedDeviceConnection = connectedDeviceConnection;
    }
    
    public void run(){
        cast();
    }
    
    private void cast(){
        
        try{
            // Play movie via castnow

            new ProcessBuilder("xterm", "-e", "castnow \"" + connectedDeviceConnection.getCurrentPath() +  "\" --myip " +
                    connectedDeviceConnection.getComputerIP() + " --address " + connectedDeviceConnection.getChromecastIP()).start();
            Thread.sleep(3000);

            // Rename window so we can find it later

            new ProcessBuilder("/bin/bash", "-c", "xdotool search --name " +
                    connectedDeviceConnection.getCurrentPath().substring(connectedDeviceConnection.getCurrentPath().length() - 6) +
                    " set_window --name " + connectedDeviceConnection.getPhoneName()).start();

        }catch(IOException | InterruptedException e){
            e.printStackTrace();
        }
    }
}