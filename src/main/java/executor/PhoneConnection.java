package executor;

import networking.Phone;
import networking.Server;

class PhoneConnection extends Thread {
    private final Phone connectedPhone;

    public PhoneConnection(Phone connectedPhone){
        this.connectedPhone = connectedPhone;
    }

    public void run(){
        new Server().sendTitles(connectedPhone);
    }
}
