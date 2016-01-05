package core;
import core.drivers.ChromeDriverSetter;
import core.drivers.FirefoxDriverSetter;
import core.drivers.IEDriverSetter;
import data.TestData;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.*;
import pages.ChromeCookiesPage;
import ru.yandex.qatools.allure.annotations.Attachment;
import ru.yandex.qatools.allure.annotations.Step;
import utils.Logger;

import java.util.concurrent.TimeUnit;

/**
 * Created by Vladyslav on 26.06.2015.
 */
public class TestBase {

    private WebDriver driver;

    public WebDriver getDriverInstance() {
        return driver;
    }

    protected WebDriver getDriver() {
        if (driver == null) {
            driver = ChromeDriverSetter.get().getDefaultChromeDriver();
            driver.manage().timeouts().pageLoadTimeout(90, TimeUnit.SECONDS);
            driver.manage().timeouts().implicitlyWait(90, TimeUnit.SECONDS);
            driver.manage().timeouts().setScriptTimeout(90, TimeUnit.SECONDS);
            driver.manage().window().maximize();
        }
        return driver;
    }

    protected WebDriver getDriver(Browser browser, boolean disableFlash) {
        if (driver == null) {
            switch (browser) {
                case CHROME:
                    driver = ChromeDriverSetter.get().getDefaultChromeDriver(disableFlash);
                    break;
                case CHROME_OPT:
                    driver = ChromeDriverSetter.get().getOptionalChromeDriver(disableFlash);
                    break;
                case MOBILE_CHROME:
                    driver = ChromeDriverSetter.get().getMobileChromeDriver();
                    break;
                case OLD_MOBILE_CHROME:
                    driver = ChromeDriverSetter.get().getOldMobileChromeDriver();
                    break;
                case FIREFOX:
                    driver = FirefoxDriverSetter.get().getDefaultFirefoxDriver(disableFlash);
                    break;
                case FIREFOX_OPT:
                    driver = FirefoxDriverSetter.get().getOptionalFirefoxDriver(disableFlash);
                    break;
                case IE:
                    driver = IEDriverSetter.get().getIEDriver();
                    break;
            }
            driver.manage().timeouts().pageLoadTimeout(90, TimeUnit.SECONDS);
            driver.manage().timeouts().implicitlyWait(90, TimeUnit.SECONDS);
            driver.manage().timeouts().setScriptTimeout(90, TimeUnit.SECONDS);
            driver.manage().window().maximize();
        }
        return driver;
    }

    @Attachment(value = "{0}", type = "image/png")
    public byte[] attachScreenshot(String name) {
        return ((TakesScreenshot)getDriverInstance()).getScreenshotAs(OutputType.BYTES);
    }

    @Step("Delete cookies")
    protected void deleteChromeCookies() {
        Logger.info("Deleting cookies...");
        if (driver instanceof ChromeDriver) {
            ChromeCookiesPage ccp = new ChromeCookiesPage(driver);
            ccp.deleteCookies();
            Logger.info("..Success.");
        }
        else {
            driver.manage().deleteAllCookies();
            Logger.info("..Success.");
        }
    }

    @Step("Get url [{0}]")
    public void getUrl(String url) {
        Logger.info("Getting url [" + url + "]");
        driver.get(url);
    }

//    @AfterMethod
    protected void tearDown() {
        if (driver != null) {
            driver.quit();
            driver = null;
        }
    }

    @AfterSuite
    @Parameters({ "before" })
    protected void afterSuite(@Optional("Test")String before) {
        if(before.equalsIgnoreCase("Suite")){
            tearDown();
        }
        else {
            Logger.info("Skip AfterSuite");
        }
    }

    @AfterTest
    @Parameters({ "before" })
    protected void afterTest(@Optional("Test")String before) {
        if(before.equalsIgnoreCase("Test")){
            tearDown();
        }
        else {
            Logger.info("Skip AfterTest");
        }
    }

    @AfterClass
    @Parameters({ "before" })
    protected void afterClass(@Optional("Test")String before) {
        if(before.equalsIgnoreCase("Class")){
            tearDown();
        }
        else {
            Logger.info("Skip AfterClass");
        }
    }

    @AfterMethod
    @Parameters({ "before" })
    protected void afterMethod(@Optional("Test")String before) {
        if(before.equalsIgnoreCase("Method")){
            tearDown();
        }
        else {
            Logger.info("Skip AfterMethod");
        }
    }
}
