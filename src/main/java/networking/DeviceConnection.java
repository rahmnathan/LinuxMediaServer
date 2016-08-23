package networking;

import java.io.Serializable;

public class DeviceConnection implements Serializable {
    private final String castIP;
    private final String phoneName;
    private String computerIP;
    private String currentPath;

    public DeviceConnection(String chromecastIP, String phoneName, String currentPath, String computerIP) {
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