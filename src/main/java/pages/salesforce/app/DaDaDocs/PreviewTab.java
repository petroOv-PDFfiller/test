package pages.salesforce.app.DaDaDocs;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import pages.salesforce.app.SalesAppBasePage;
import ru.yandex.qatools.ashot.Screenshot;
import utils.AShotMan;
import utils.TimeMan;

import static core.check.Check.checkTrue;

public class PreviewTab extends SalesAppBasePage {

    private By btnPrint = By.xpath("//button[@aria-label='Print']");
    private By btnFullScreen = By.xpath("//button[contains(@class,'full-screen')]");
    private By pagePreview = By.cssSelector(".c-preview__body");
    private By compactScreen = By.xpath("//section[@class='c-preview-wrapper']/div[contains(@class,'c-preview')]");
    private By fullScreen = By.xpath("//section[@class='c-preview-wrapper']/div[contains(@class,'c-preview is-full-screen')]");
    private By previewFrame = By.xpath("//iframe[contains(@src,'pdffiller')]");

    public PreviewTab(WebDriver driver) {
        super(driver);
    }

    @Override
    public void isOpened() {
        checkTrue(waitUntilPageLoaded(), "Preview frame was not loaded");
        skipLoader();
        checkTrue(isElementPresent(previewFrame, 5), "Preview frame is not presented");
        switchToFrame(previewFrame);
        TimeMan.sleep(5);

        checkTrue(isElementPresentAndDisplayed(btnPrint, 30), "Preview tab was not loaded");
    }

    @Step
    @Override
    public void switchToDefaultContent() {
        if (isFullScreened()) {
            switchToFullScreen(false);
        }
        driver.switchTo().defaultContent();
        TimeMan.sleep(5);
    }

    @Step
    public Screenshot takeScreenShotPagePreview() {
        switchToFullScreen(true);
        AShotMan aShot = new AShotMan(driver);
        checkTrue(isElementPresent(pagePreview, 4), "pagePreview is not present");
        WebElement element = driver.findElement(pagePreview);
        TimeMan.sleep(4);
        return aShot.takeScreenshotWithOutJquery(element);
    }

    @Step
    public void switchToFullScreen(boolean isFullscreen) {
        if (isFullscreen != isFullScreened()) {
            checkTrue(isElementDisplayed(btnFullScreen, 5), "Full screen button is not presented");
            click(btnFullScreen);
            checkTrue(isFullscreen == isFullScreened(), "Switching screen mode was not performed");
        }
    }

    @Step
    public boolean isFullScreened() {
        checkTrue(isElementPresent(compactScreen, 5), "Preview screen locator is missed");
        return isElementPresent(fullScreen, 5);
    }

    @Step
    public void print() {
        //click print
        checkTrue(isElementDisplayed(btnPrint, 5), "Print button is not enabled!");
        click(btnPrint);
        skipLoader();
    }

    @Step
    public void close() {
        checkTrue(isElementDisplayed(btnPrint, 5), "Print button is not enabled!");
        click(btnPrint);
        skipLoader();
    }
}
