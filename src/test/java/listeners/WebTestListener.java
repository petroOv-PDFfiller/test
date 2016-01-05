package listeners;

import core.AllureAttachments;
import core.TestBase;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;
import ru.yandex.qatools.allure.annotations.Attachment;
import utils.Logger;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Vladyslav on 07.07.2015.
 */
public class WebTestListener extends TestListenerAdapter {

    private SimpleDateFormat date = new SimpleDateFormat("dd-MM-yyyy");
    private SimpleDateFormat time = new SimpleDateFormat("HH-mm-ss");

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
        Logger.warning(result.getTestClass().getName() + " TEST: " + result.getName() + "........SKIPPED");
    }

    @Override
    public void onFinish(ITestContext result) {
        Logger.info(result.getClass().getName() + " TEST: " + result.getName() +
                "------------------------ TEST FINISH ------------------------");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        WebDriver driver = ((TestBase) result.getInstance()).getDriverInstance();
        if (driver != null) {
            ((TestBase) result.getInstance()).attachScreenshot("screenshotOnFailure");
            File file = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            String destDir = ("surefire-reports/html/fail-screenshots/"); //path of new directory of screenshot
            new File(destDir).mkdirs(); //creating this directory
            String destFile = date.format(Calendar.getInstance().getTime()) + "/"
                    + result.getTestClass().getName() + "_" + result.getName()
                    + "_" + time.format(Calendar.getInstance().getTime()) + ".png"; // info for name of screenshot
            //copying screenshot from temporary directory to our created directory (name of dir=name of dest dir+file name)
            try {
                FileUtils.copyFile(file, new File(destDir + "/" + destFile));
            } catch (IOException e) {
                e.printStackTrace();
            }
            String openedUrl = driver.getCurrentUrl();
            Logger.info("Opened url: [" + openedUrl+ "]");
            AllureAttachments.textAttachment(openedUrl, "Opened url");
        }
        else {
            Logger.error("Can not make test failure screenshot, driver == null");
        }
        Logger.error(result.getTestClass().getName() + " TEST: " + result.getName() + "........FAILED");
    }
}
