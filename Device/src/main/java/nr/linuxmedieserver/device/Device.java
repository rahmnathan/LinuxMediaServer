package nr.linuxmedieserver.device;

public class Device {
    private final String castIP;
    private final String phoneName;
    private final String computerIP;
    private final String currentPath;

    private Device(String chromecastIP, String phoneName, String currentPath, String computerIP) {
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

    public String getCurrentPath() {
        return currentPath;
    }

    public static class Builder{

        private String castIP;
        private String phoneName;
        private String computerIP;
        private String currentPath;

        public static Builder newInstance(){
            return new Builder();
        }

        public Builder chromecastIP(String chromeCastIP){
            this.castIP = chromeCastIP;
            return this;
        }
        public Builder phoneName(String phoneName){
            this.phoneName = phoneName;
            return this;
        }
        public Builder computerIP(String computerIP){
            this.computerIP = computerIP;
            return this;
        }
        public Builder currentPath(String currentPath){
            this.currentPath = currentPath;
            return this;
        }
        public Device build(){
            return new Device(castIP, phoneName, currentPath, computerIP);
        }

    }
}