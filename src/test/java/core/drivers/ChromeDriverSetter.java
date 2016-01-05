package core.drivers;

import data.TestData;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.annotations.Optional;
import utils.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Pyrozhok on 08.12.2015.
 */
public class ChromeDriverSetter {

    private static ChromeDriverSetter driverSetter;

    private ChromeDriverSetter() {}

    public static ChromeDriverSetter get() {
        if (driverSetter == null) {
            driverSetter = new ChromeDriverSetter();
        }
        return driverSetter;
    }

    public WebDriver getDefaultChromeDriver() {
        // LoggingPreferences loggingprefs = new LoggingPreferences();
        //loggingprefs.enable(LogType.BROWSER, Level.ALL);
        // cap.setCapability(CapabilityType.LOGGING_PREFS, loggingprefs);
        //ChromeOptions options = new ChromeOptions();

        //cap.setCapability(ChromeOptions.CAPABILITY, options);
        HashMap<String, Object> chromePrefs = new HashMap<String, Object>();
        chromePrefs.put("profile.default_content_settings.popups", 0);
        chromePrefs.put("download.default_directory", TestData.pathToDownloadsFolder);
        //	chromePrefs.put("plugins.plugins_disabled", "Chrome PDF Viewer"); //////

        // disable Chrome PDF Viewer // TODO
			/*
			HashMap<String, Object> plugin = new HashMap<String, Object>();
			plugin.put("enabled", false);
			plugin.put("name", "Chrome PDF Viewer");

			chromePrefs.put("plugins.plugins_list", Arrays.asList(plugin));
			*/

        //chromePrefs.put("plugins.plugins_disabled", "pepperflash"); //////
        ChromeOptions options = new ChromeOptions();
        HashMap<String, Object> chromeOptionsMap = new HashMap<String, Object>();
        options.addArguments("--disable-popup-blocking"); // disable popup blocking

        //options.addArguments("--disable-extensions");

        //options.addArguments("--disable-plugins-discovery");
        //options.addArguments("--disable-internal-flash");

        options.setExperimentalOption("prefs", chromePrefs);
        options.addArguments("--test-type");
        //options.addArguments("--disable-extensions"); //disable developer mode extensions
        //options.addArguments("--disable-plugins");
        //options.addArguments("--disable-print-preview"); // Отключить
        //	DesiredCapabilities cap = DesiredCapabilities.chrome();
        DesiredCapabilities cap = new DesiredCapabilities();
        cap.setCapability(ChromeOptions.CAPABILITY, chromeOptionsMap);
        cap.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
        cap.setCapability(ChromeOptions.CAPABILITY, options);
        System.setProperty("webdriver.chrome.driver", TestData.pathToChromeDriver);
        Logger.info("Starting Chrome driver");
        return new ChromeDriver(cap);
    }

    public WebDriver getDefaultChromeDriver(boolean disableFlash) {
        // LoggingPreferences loggingprefs = new LoggingPreferences();
        //loggingprefs.enable(LogType.BROWSER, Level.ALL);
        // cap.setCapability(CapabilityType.LOGGING_PREFS, loggingprefs);
        //ChromeOptions options = new ChromeOptions();

        //cap.setCapability(ChromeOptions.CAPABILITY, options);
        HashMap<String, Object> chromePrefs = new HashMap<String, Object>();
        chromePrefs.put("profile.default_content_settings.popups", 0);
        chromePrefs.put("download.default_directory", TestData.pathToDownloadsFolder);
        //	chromePrefs.put("plugins.plugins_disabled", "Chrome PDF Viewer"); //////

        // disable Chrome PDF Viewer // TODO
			/*
			HashMap<String, Object> plugin = new HashMap<String, Object>();
			plugin.put("enabled", false);
			plugin.put("name", "Chrome PDF Viewer");

			chromePrefs.put("plugins.plugins_list", Arrays.asList(plugin));
			*/

        //chromePrefs.put("plugins.plugins_disabled", "pepperflash"); //////
        ChromeOptions options = new ChromeOptions();
        HashMap<String, Object> chromeOptionsMap = new HashMap<String, Object>();
        options.addArguments("--disable-popup-blocking"); // disable popup blocking

        //options.addArguments("--disable-extensions");
        if(disableFlash){
            options.addArguments("--disable-bundled-ppapi-flash"); //TODO DISABLE FLASH
        }

        //options.addArguments("--disable-plugins-discovery");
        //options.addArguments("--disable-internal-flash");

        options.setExperimentalOption("prefs", chromePrefs);
        options.addArguments("--test-type");
        //options.addArguments("--disable-extensions"); //disable developer mode extensions
        //options.addArguments("--disable-plugins");
        //options.addArguments("--disable-print-preview"); // Отключить
        //	DesiredCapabilities cap = DesiredCapabilities.chrome();
        DesiredCapabilities cap = new DesiredCapabilities();
        cap.setCapability(ChromeOptions.CAPABILITY, chromeOptionsMap);
        cap.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
        cap.setCapability(ChromeOptions.CAPABILITY, options);
        System.setProperty("webdriver.chrome.driver", TestData.pathToChromeDriver);
        Logger.info("Starting Chrome driver");
        return new ChromeDriver(cap);
    }

    public WebDriver getOptionalChromeDriver(boolean disableFlash) {
        HashMap<String, Object> chromePrefs = new HashMap<String, Object>();
        chromePrefs.put("profile.default_content_settings.popups", 0);
        chromePrefs.put("download.default_directory", TestData.pathToDownloadsFolder);
        ChromeOptions options = new ChromeOptions();
        HashMap<String, Object> chromeOptionsMap = new HashMap<String, Object>();
        options.setExperimentalOption("prefs", chromePrefs);
        options.addArguments("--test-type");
        DesiredCapabilities cap = DesiredCapabilities.chrome();
        cap.setCapability(ChromeOptions.CAPABILITY, chromeOptionsMap);
        cap.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
        cap.setCapability(ChromeOptions.CAPABILITY, options);
        System.setProperty("webdriver.chrome.driver", TestData.pathToChromeDriver);
        Logger.info("Starting Chrome driver (download dir)");
        return new ChromeDriver(cap);
    }

    public WebDriver getMobileChromeDriver() {
        /*
			log.log("Эмуляция мобильного телефона");
			ChromeOptions options = new ChromeOptions();
			if(!proxy.equalsIgnoreCase("no")){ options.addArguments("--proxy-server=http://" + proxyForOptions); }
			options.addArguments("--user-agent= Mozilla/5.0 (Linux; U; Android 2.2; ru-ru; GT-I9000 Build/FROYO) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1");
			*/
        Map<String, Object> deviceMetrics = new HashMap<String, Object>();

        deviceMetrics.put("width", 800);
        deviceMetrics.put("height", 640);
        deviceMetrics.put("pixelRatio", 3.0);

        Map<String, Object> mobileEmulation = new HashMap<String, Object>();

        mobileEmulation.put("deviceMetrics", deviceMetrics);

        mobileEmulation.put("userAgent", "Mozilla/5.0 (iPhone; CPU iPhone OS 8_0 like Mac OS X) AppleWebKit/600.1.3 (KHTML, like Gecko) Version/8.0 Mobile/12A4345d Safari/600.1.4");
        //mobileEmulation.put("deviceName", "Apple iPhone 6");
        //mobileEmulation.put("deviceMetrics", deviceMetrics);
        Map<String, Object> chromeOptions = new HashMap<String, Object>();
        chromeOptions.put("mobileEmulation", mobileEmulation);
        DesiredCapabilities capabilities = DesiredCapabilities.chrome();
        //capabilities.setCapability(CapabilityType.UNEXPECTED_ALERT_BEHAVIOUR, org.openqa.selenium.UnexpectedAlertBehaviour.ACCEPT);
        capabilities.setCapability(ChromeOptions.CAPABILITY, chromeOptions);
        System.setProperty("webdriver.chrome.driver", TestData.pathToChromeDriver);
        Logger.info("Starting Mobile Chrome driver");
        return new ChromeDriver(capabilities);
    }

    public WebDriver getOldMobileChromeDriver() {
        Map<String, Object> deviceMetrics = new HashMap<String, Object>();

        deviceMetrics.put("width", 360);
        deviceMetrics.put("height", 640);
        deviceMetrics.put("pixelRatio", 3.0);

        Map<String, Object> mobileEmulation = new HashMap<String, Object>();

        mobileEmulation.put("deviceMetrics", deviceMetrics);
        mobileEmulation.put("userAgent", "Mozilla/5.0 (MeeGo; NokiaN9) AppleWebKit/534.13 (KHTML, like Gecko) NokiaBrowser/8.5.0 Mobile Safari/534.13");

        Map<String, Object> chromeOptions = new HashMap<String, Object>();
        chromeOptions.put("mobileEmulation", mobileEmulation);
        DesiredCapabilities capabilities = DesiredCapabilities.chrome();
        capabilities.setCapability(ChromeOptions.CAPABILITY, chromeOptions);
        System.setProperty("webdriver.chrome.driver", TestData.pathToChromeDriver);
        Logger.info("Starting Old Mobile Chrome driver");
        return new ChromeDriver(capabilities);
    }
}
