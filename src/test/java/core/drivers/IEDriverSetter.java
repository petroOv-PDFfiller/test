package core.drivers;

import data.TestData;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.annotations.Optional;
import utils.Logger;

/**
 * Created by Pyrozhok on 08.12.2015.
 */
public class IEDriverSetter {

    private static IEDriverSetter driverSetter;

    private IEDriverSetter() {}

    public static IEDriverSetter get() {
        if (driverSetter == null) {
            driverSetter = new IEDriverSetter();
        }
        return driverSetter;
    }

    public WebDriver getIEDriver() {
        DesiredCapabilities cap = new DesiredCapabilities().internetExplorer();
        //cap.setCapability(CapabilityType.ForSeleniumServer.ENSURING_CLEAN_SESSION, true);
        //cap.setCapability("nativeEvents", false);
        cap.setCapability("ignoreZoomSetting", true);
        cap.setCapability("ignoreProtectedModeSettings", true);
        Logger.info("Starting IE driver");
        System.setProperty("webdriver.ie.driver", TestData.pathToIEDriver);
        //cap.setCapability(CapabilityType.PAGE_LOAD_STRATEGY, "eager"); // Ожидание загрузки страницы
        return new InternetExplorerDriver(cap);
    }
}
