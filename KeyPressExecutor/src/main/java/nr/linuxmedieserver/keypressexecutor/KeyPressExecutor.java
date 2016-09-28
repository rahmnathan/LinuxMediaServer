package nr.linuxmedieserver.keypressexecutor;

import java.io.IOException;

public class KeyPressExecutor {
    
    public enum Controls {
        VOLUME_UP, VOLUME_DOWN, SEEK_FORWARD, SEEK_BACK, PLAY_PAUSE, STOP
    }

    public void executeCommand(Controls keyPress, String name){

        try {
            new ProcessBuilder("/bin/bash", "-c", "xdotool search --name " + name +
                    " windowfocus").start();
            Thread.sleep(100);
        } catch (InterruptedException | IOException e){
            e.printStackTrace();
        }

        switch (keyPress) {
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
                break;
        }
    }

    private void executeControl(String key) {
        try {
            new ProcessBuilder("/bin/bash", "-c", "xdotool key " + key).start();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    private void stopStream() {
        try {
            new ProcessBuilder("/bin/bash", "-c", "xdotool type Q").start();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

}
