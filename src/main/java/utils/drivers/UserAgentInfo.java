package utils.drivers;

public class UserAgentInfo {

    private final String MAC = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_3) Chrome/75.0.3770.100 Safari/537.36"; //"Macintosh; Intel Mac OS";
    private final String WIN = "Mozilla/5.0 (Windows NT 6.1; Win64; x64) Chrome/75.0.3770.100 Safari/537.36";//"Windows NT 6.0";
    private final String LINUX = "Mozilla/5.0 (X11; Linux armv7l)  Chrome/75.0.3770.100 Safari/537.36"; //X11; Linux";

    public String toString() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("mac")) {
            return MAC;
        } else if (os.contains("win")) {
            return WIN;
        } else if (os.contains("linux")) {
            return LINUX;
        } else {
            return "undefined UserAgentInfo";
        }
    }
}