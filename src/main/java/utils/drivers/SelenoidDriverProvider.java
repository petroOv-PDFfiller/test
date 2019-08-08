package utils.drivers;

import com.codeborne.selenide.WebDriverProvider;
import org.aeonbits.owner.ConfigFactory;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.BrowserType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.LocalFileDetector;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.configs.DefaultUITestConfig;
import utils.configs.baseconfigs.BrowserConfig;

import java.net.MalformedURLException;
import java.net.URI;
import java.util.logging.Level;


public class SelenoidDriverProvider implements WebDriverProvider {
    private static final Logger logger = LoggerFactory.getLogger(SelenoidDriverProvider.class);
    private static final String userAgent = " pdffiller-autotest";

    private static ChromeOptions overrideUserAgentChrome() {
        return new ChromeOptions().addArguments("--user-agent=" + new UserAgentInfo().toString() + userAgent);
    }

    private static FirefoxProfile overrideUserAgentFirefox() {
        FirefoxProfile profile = new FirefoxProfile();
        profile.setPreference("general.useragent.override", new UserAgentInfo().toString() +
                userAgent);
        return profile;
    }

    /**
     * Create new {@link WebDriver} instance. The instance will be bound to current thread, so there is no need to cache
     * this instance in method implementation. Also don't cache the instance in static variable, as <a
     * href="http://code.google.com/p/selenium/wiki/FrequentlyAskedQuestions#Q:_Is_WebDriver_thread-safe?">WebDriver
     * instance is not thread-safe</a>.
     *
     * @param capabilities set of desired capabilities as suggested by Selenide framework; method implementation is
     *                     recommended to pass this variable to {@link WebDriver}, probably modifying it according to specific needs
     * @return new {@link WebDriver} instance
     */
    @Override
    public WebDriver createDriver(DesiredCapabilities capabilities) {
        BrowserConfig selenoidConfig = ConfigFactory.create(DefaultUITestConfig.class,
                System.getProperties(),
                System.getenv());
        capabilities.setBrowserName(selenoidConfig.browserName());
        capabilities.setVersion(selenoidConfig.browserVersion());
        capabilities.setCapability("enableVNC", selenoidConfig.enableVNC());
        capabilities.setCapability("enableVideo", selenoidConfig.enableVideo());
        capabilities.setCapability("sessionTimeout", selenoidConfig.sessionTimeout());

        switch (capabilities.getBrowserName()) {
            case BrowserType.CHROME:
                LoggingPreferences logPrefs = new LoggingPreferences();
                logPrefs.enable(LogType.PERFORMANCE, Level.ALL);
                logPrefs.enable(LogType.BROWSER, Level.ALL);
                ChromeOptions options = overrideUserAgentChrome();
                options.setCapability("goog:loggingPrefs", logPrefs);
                capabilities.setCapability(ChromeOptions.CAPABILITY, options);
                options.merge(capabilities);
                break;
            case BrowserType.FIREFOX:
                capabilities.setCapability(FirefoxDriver.PROFILE, overrideUserAgentFirefox());
                break;
            default:
                break;
        }

        RemoteWebDriver driver;
        try {
            driver = new RemoteWebDriver(
                    URI.create("http://" + selenoidConfig.host() + ":" + selenoidConfig.port() + "/wd/hub").toURL(),
                    capabilities
            );
        } catch (MalformedURLException e) {
            throw new RuntimeException("Can't start selenoid driver " + e);
        }
        driver.setFileDetector(new LocalFileDetector());
        return driver;
    }
}
