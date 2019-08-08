package utils.drivers;

import com.codeborne.selenide.WebDriverProvider;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChromeDriverProvider implements WebDriverProvider {
    private static final Logger logger = LoggerFactory.getLogger(ChromeDriverProvider.class);

    /**
     * Create new {@link WebDriver} instance. The instance will be bound to current thread, so there is no need to cache
     * this instance in method implementation. Also don't cache the instance in static variable, as <a
     * href="http://code.google.com/p/selenium/wiki/FrequentlyAskedQuestions#Q:_Is_WebDriver_thread-safe?">WebDriver
     * instance is not thread-safe</a>.
     *
     * @param desiredCapabilities set of desired capabilities as suggested by Selenide framework; method implementation is
     *                            recommended to pass this variable to {@link WebDriver}, probably modifying it according to specific needs
     * @return new {@link WebDriver} instance
     */
    @Override
    public WebDriver createDriver(DesiredCapabilities desiredCapabilities) {
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--user-agent=" + new UserAgentInfo().toString() + " pdffiller-autotest");

        return new ChromeDriver(options);
    }
}
