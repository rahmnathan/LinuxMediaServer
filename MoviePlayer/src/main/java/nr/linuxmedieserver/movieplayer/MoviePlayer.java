package nr.linuxmedieserver.movieplayer;

import nr.linuxmedieserver.device.Device;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class MoviePlayer extends Thread {

    private final Device connectedDevice;
    
    public MoviePlayer(Device connectedDevice){
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

        }catch(Exception e){
            e.printStackTrace();
        }
    }
}