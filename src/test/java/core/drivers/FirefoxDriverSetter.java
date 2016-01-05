package core.drivers;

import data.TestData;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.annotations.Optional;
import utils.Logger;

import java.io.File;

/**
 * Created by Pyrozhok on 08.12.2015.
 */
public class FirefoxDriverSetter {

    private static FirefoxDriverSetter driverSetter;

    private FirefoxDriverSetter() {}

    public static FirefoxDriverSetter get() {
        if (driverSetter == null) {
            driverSetter = new FirefoxDriverSetter();
        }
        return driverSetter;
    }

    public WebDriver getDefaultFirefoxDriver(boolean disableFlash) {
        FirefoxProfile profile = new FirefoxProfile();
        //downloading
        profile.setPreference("browser.download.folderList", 2);
        profile.setPreference("browser.download.manager.showWhenStarting", false);
        profile.setPreference("browser.download.dir", TestData.pathToDownloadsFolder);
        //

        //do not ask open or save file
        profile.setPreference("browser.helperApps.neverAsk.openFile",
                "text/csv,application/x-msexcel,application/excel,application/x-excel,application/vnd.ms-excel,image/png,image/jpeg,text/html,text/plain,application/msword,application/xml,application/pdf,application/vnd.openxmlformats-officedocument.wordprocessingml.document,application/vnd.openxmlformats-officedocument.spreadsheetml.sheet,application/vnd.openxmlformats-officedocument.presentationml.presentation");
        profile.setPreference("browser.helperApps.neverAsk.saveToDisk",
                "text/csv,application/x-msexcel,application/excel,application/x-excel,application/vnd.ms-excel,image/png,image/jpeg,text/html,text/plain,application/msword,application/xml,application/pdf,application/vnd.openxmlformats-officedocument.wordprocessingml.document,application/vnd.openxmlformats-officedocument.spreadsheetml.sheet,application/vnd.openxmlformats-officedocument.presentationml.presentation");
        //pdf-preview disabling
        profile.setPreference("pdfjs.disabled", true);

        if(disableFlash){
            profile.setPreference("plugin.state.flash", 0); //disable flash
        }
        //profile.setPreference("webdriver.load.strategy", "fast");
        DesiredCapabilities desiredCapabilities = new DesiredCapabilities();
        desiredCapabilities.setCapability(FirefoxDriver.PROFILE, profile);
        desiredCapabilities.setCapability(CapabilityType.PAGE_LOAD_STRATEGY, "eager"); // Ожидание загрузки страницы
        Logger.info("Starting Firefox");
        return new FirefoxDriver(desiredCapabilities);
    }

    public WebDriver getOptionalFirefoxDriver(boolean disableFlash) {
        FirefoxProfile profile = new FirefoxProfile(new File("C:\\testResources\\firefoxProfiles\\61dqp4ok.TestProfile"));
        profile.setPreference("browser.download.folderList", 2);
        profile.setPreference("browser.download.manager.showWhenStarting", false);
        profile.setPreference("browser.download.dir", TestData.pathToDownloadsFolder);
        profile.setPreference("browser.helperApps.neverAsk.openFile",
                "text/csv,application/x-msexcel,application/excel,application/x-excel,application/vnd.ms-excel,image/png,image/jpeg,text/html,text/plain,application/msword,application/xml,application/pdf,application/vnd.openxmlformats-officedocument.wordprocessingml.document,application/vnd.openxmlformats-officedocument.spreadsheetml.sheet,application/vnd.openxmlformats-officedocument.presentationml.presentation");
        profile.setPreference("browser.helperApps.neverAsk.saveToDisk",
                "text/csv,application/x-msexcel,application/excel,application/x-excel,application/vnd.ms-excel,image/png,image/jpeg,text/html,text/plain,application/msword,application/xml,application/pdf,application/vnd.openxmlformats-officedocument.wordprocessingml.document,application/vnd.openxmlformats-officedocument.spreadsheetml.sheet,application/vnd.openxmlformats-officedocument.presentationml.presentation");
        profile.setPreference("pdfjs.disabled", true);
        //profile.setPreference("webdriver.load.strategy", "fast");
        DesiredCapabilities desiredCapabilities = new DesiredCapabilities();
        desiredCapabilities.setCapability(FirefoxDriver.PROFILE, profile);
        //cap.setCapability(CapabilityType.PAGE_LOAD_STRATEGY, "eager"); // Ожидание загрузки страницы

        Logger.info("Starting Firefox driver (Profile)");
        return new FirefoxDriver(profile);
    }
}
