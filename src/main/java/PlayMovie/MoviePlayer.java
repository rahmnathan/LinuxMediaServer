package PlayMovie;

import Controls.ControlListener;
import Phone.Phone;

import java.io.IOException;

public class MoviePlayer extends Thread {

    private Phone connectedPhone;
    
    public MoviePlayer(Phone connectedPhone){
        this.connectedPhone = connectedPhone;
    }
    
    public void run(){
        cast();
    }
    
    private void cast(){
        
        try{
            new ProcessBuilder("xterm", "-e", "castnow \"" + connectedPhone.getPath() +  "\" --myip " +
                    connectedPhone.getComputerIP() + " --address " + connectedPhone.getCastIP()).start();
            Thread.sleep(3000);


            new ProcessBuilder("/bin/bash", "-c", "xdotool search --name " +
                    connectedPhone.getPath().substring(connectedPhone.getPath().length() - 6) +
                    " set_window --name " + connectedPhone.getPhoneName()).start();


            new ControlListener().start();
        }catch(IOException | InterruptedException e){
            e.printStackTrace();
        }
    }
}