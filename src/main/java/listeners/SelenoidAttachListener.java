package listeners;

import core.AllureAttachments;
import core.TestBase;
import io.qameta.allure.Step;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.SessionId;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;
import utils.Logger;

import java.net.MalformedURLException;
import java.net.URL;

import static core.TestBase.sVideoHub;

public class SelenoidAttachListener extends TestListenerAdapter {

    @Override
    public void onTestFailure(ITestResult result) {
        WebDriver driver = ((TestBase) result.getInstance()).getDriver();
        if (driver != null) {
            AllureAttachments.selenoidHtmlAttachment(generateHtmlVideoReport(getVideoUrl(result)));
        }
        Logger.warning("Attach video report to failed test: " + result.getMethod().getMethodName());
        Logger.warning("URL to video: " + getVideoUrl(result));
    }

    @Step
    private String generateHtmlVideoReport(URL videoUrl) {
        Document doc = Jsoup.parseBodyFragment("<video><source></video>");
        doc.getElementsByTag("video").get(0)
                .attr("width", "1080")
                .attr("height", "720")
                .attr("controls", "controls");

        doc.getElementsByTag("source").get(0)
                .attr("src", videoUrl.toString())
                .attr("type", "video/mp4");
        return doc.toString();
    }

    @Step
    public URL getVideoUrl(ITestResult result) {
        SessionId sessionId = ((RemoteWebDriver) ((TestBase) result.getInstance()).getDriver()).getSessionId();
        URL videourl = null;
        try {
            videourl = new URL("https", sVideoHub, "/video/" + sessionId + ".mp4");
        } catch (MalformedURLException e) {
            Logger.error("Can't get video url for session: " + sessionId + " " + e.getMessage());
        }
        return videourl;
    }
}
