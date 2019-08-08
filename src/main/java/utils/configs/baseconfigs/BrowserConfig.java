package utils.configs.baseconfigs;

import org.aeonbits.owner.Config;
import org.openqa.selenium.remote.BrowserType;

public interface BrowserConfig extends Config {

    @DefaultValue("remote")
    String browserType();

    @Key("host")
    @DefaultValue("localhost")
    String host();

    @DefaultValue("4444")
    String port();

    @DefaultValue(BrowserType.CHROME)
    String browserName();

    @DefaultValue("75.0")
    String browserVersion();

    @DefaultValue("true")
    boolean enableVNC();

    @DefaultValue("false")
    boolean enableVideo();

    @DefaultValue("4m")
    String sessionTimeout();
}
