package Thread;

import Phone.Phone;
import Server.Server;

/**
 * Created by nathan on 6/9/16.
 */
public class PhoneConnection extends Thread {
    private static Phone connectedPhone;

    public PhoneConnection(Phone connectedPhone){
        PhoneConnection.connectedPhone = connectedPhone;
    }

    public void run(){
        new Server().sendTitles(connectedPhone);
    }
}
