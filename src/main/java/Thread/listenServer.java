package Thread;

import Phone.Phone;
import Server.Server;

/**
 * Created by nathan on 6/9/16.
 */
public class listenServer extends Thread {
    private static Phone connectedPhone;

    public listenServer(Phone connectedPhone){
        listenServer.connectedPhone = connectedPhone;
    }

    public void run(){
        new Server().send(connectedPhone);
    }
}
