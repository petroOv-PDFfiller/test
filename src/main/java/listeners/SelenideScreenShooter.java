package listeners;

import com.codeborne.selenide.Screenshots;
import com.codeborne.selenide.WebDriverRunner;
import com.codeborne.selenide.ex.UIAssertionError;
import io.qameta.allure.Attachment;
import org.testng.ITestResult;
import org.testng.reporters.ExitCodeListener;

import java.io.IOException;
import java.nio.file.Files;
import java.util.logging.Logger;

public class SelenideScreenShooter extends ExitCodeListener {

    private final Logger log = Logger.getLogger(this.getClass().getName());

    @Attachment(value = "failScreenshot", type = "image/png")
    public static byte[] failScreenshot() throws IOException {
        return Files.readAllBytes(Screenshots.takeScreenShotAsFile().toPath());
    }

    public void onTestStart(ITestResult result) {
        super.onTestStart(result);
        String className = result.getMethod().getTestClass().getName();
        String methodName = result.getMethod().getMethodName();
        Screenshots.startContext(className, methodName);
    }

    public void onTestFailure(ITestResult result) {
        super.onTestFailure(result);
        if (!(result.getThrowable() instanceof UIAssertionError) && WebDriverRunner.driver() != null) {
            try {
                failScreenshot();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Screenshots.finishContext();
    }
}
