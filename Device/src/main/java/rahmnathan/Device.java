package rahmnathan;

public class Device {
    private final String castIP;
    private final String phoneName;
    private final String computerIP;
    private final String currentPath;

    public Device(String chromecastIP, String phoneName, String currentPath, String computerIP) {
        this.castIP = chromecastIP;
        this.phoneName = phoneName;
        this.currentPath = currentPath;
        this.computerIP = computerIP;
    }

    public String getComputerIP() {
        return computerIP;
    }

    public String getChromecastIP() {
        return castIP;
    }

    public String getPhoneName() {
        return phoneName;
    }

    public String getCurrentPath(){
        return currentPath;
    }
}