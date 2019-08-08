package utils.configs;

import org.aeonbits.owner.Config;
import org.openqa.selenium.remote.BrowserType;
import utils.configs.baseconfigs.BrowserConfig;

@Config.Sources({
        "file:src/main/resources/data/default_test_data.properties"
})
public interface DefaultUITestConfig extends BrowserConfig {
    @DefaultValue("qwe1rty2")
    String password();

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
