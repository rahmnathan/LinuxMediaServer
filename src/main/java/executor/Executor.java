package executor;

import networking.ClientInput;
import networking.ServerOutput;
import networking.Phone;
import player.MoviePlayer;

class Executor {

    public static void main(String[] args) {

        ClientInput input = new ClientInput();
        ServerOutput output = new ServerOutput();

        while(true) {

            Phone connectedPhone = input.receive();

            if (connectedPhone.isCasting()) {
                new MoviePlayer(connectedPhone).start();
            } else {
                output.send(connectedPhone);
            }
        }
    }
}
