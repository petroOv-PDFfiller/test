package listeners;

import core.TestBase;
import org.apache.poi.util.IOUtils;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;
import utils.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import static java.util.Optional.ofNullable;

/**
 * Created by Vladyslav on 07.07.2015.
 */
public class WebTestListener extends TestListenerAdapter {

    @Override
    public void onTestStart(ITestResult result) {
        Logger.info("------------------------ TEST START ------------------------");
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        Logger.info(result.getTestClass().getName() + " TEST: " + result.getName() + "........SUCCESS");
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        if (result != null && result.getInstance() != null) {
            ((TestBase) result.getInstance()).tearDown();
        }
        Logger.warning(result.getTestClass().getName() + " TEST: " + result.getName() + "........SKIPPED");
    }

    @Override
    public void onFinish(ITestContext result) {
        Properties allureConfig = new Properties();
        try {
            allureConfig.load(this.getClass().getClassLoader().getResourceAsStream("allure.properties"));
            setBuildEnvironment(allureConfig.getProperty("allure.results.directory"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Logger.info(result.getClass().getName() + " TEST: " + result.getName() +
                "\n------------------------ TEST FINISH ------------------------");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        WebDriver driver = ((TestBase) result.getInstance()).getDriver();
        if (driver != null) {
            ((TestBase) result.getInstance()).makeScreenshot(result);
        }
        Logger.error(result.getTestClass().getName() + " TEST: " + result.getName() + "........FAILED");
    }

    @Override
    public void onConfigurationFailure(ITestResult result) {
        WebDriver driver = ((TestBase) result.getInstance()).getDriver();
        if (driver != null) {
            ((TestBase) result.getInstance()).makeScreenshot(result);
        }
        Logger.error(result.getTestClass().getName() + " TEST: " + result.getName() + "........FAILED");
    }

    public void setBuildEnvironment(String path) {

        FileOutputStream fos = null;

        try {
            Properties props = new Properties();
            fos = new FileOutputStream(path + File.separator + "environment.properties");
            ofNullable(System.getProperty("environment")).ifPresent(s -> props.setProperty("environment", s));
            ofNullable(System.getProperty("browser")).ifPresent(s -> props.setProperty("browser", s));
            ofNullable(System.getProperty("screenSize")).ifPresent(s -> props.setProperty("screenSize", s));
            ofNullable(System.getProperty("dxOwnerEmail")).ifPresent(s -> props.setProperty("DX org", s));
            ofNullable(System.getProperty("email")).ifPresent(s -> props.setProperty("Admin user username", s));
            ofNullable(System.getProperty("testPassword")).ifPresent(s -> props.setProperty("Admin user password", s));
            ofNullable(System.getProperty("stUserUsername")).ifPresent(s -> props.setProperty("Standard user username", s));
            ofNullable(System.getProperty("stUserPassword")).ifPresent(s -> props.setProperty("Standard user password", s));
            ofNullable(System.getProperty("airslate.admin")).ifPresent(s -> props.setProperty("airSlate admin", s));
            ofNullable(System.getProperty("airslate.user")).ifPresent(s -> props.setProperty("airSlate user", s));
            ofNullable(System.getProperty("orgDomain")).ifPresent(s -> props.setProperty("Organization domain", s));
            ofNullable(System.getProperty("testRunId")).ifPresent(s -> props.setProperty("testRunId", s));
            ofNullable(System.getProperty("testRailProject")).ifPresent(s -> props.setProperty("testRailProject", s));

            props.store(fos, "See https://github.com/allure-framework/allure-app/wiki/Environment");
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(fos);
        }
    }
}
