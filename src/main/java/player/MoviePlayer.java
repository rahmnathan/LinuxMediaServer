package player;

import networking.DeviceConnection;

import java.io.IOException;

public class MoviePlayer extends Thread {

    private final DeviceConnection connectedDevice;
    
    public MoviePlayer(DeviceConnection connectedDevice){
        this.connectedDevice = connectedDevice;
    }
    
    public void run(){
        cast();
    }
    
    private void cast(){
        try{
            // Play movie via castnow

            new ProcessBuilder("xterm", "-e", "castnow \"" + connectedDevice.getCurrentPath() +  "\" --myip " +
                    connectedDevice.getComputerIP() + " --address " + connectedDevice.getChromecastIP()).start();
            Thread.sleep(3000);

            // Rename window so we can find it later

            new ProcessBuilder("/bin/bash", "-c", "xdotool search --name " +
                    connectedDevice.getCurrentPath().substring(connectedDevice.getCurrentPath().length() - 6) +
                    " set_window --name " + connectedDevice.getPhoneName()).start();

        }catch(IOException | InterruptedException e){
            e.printStackTrace();
        }
    }
}