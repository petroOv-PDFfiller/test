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

public class DaDaDocsFlashEditor extends SalesAppBasePage {

    public DaDaDocsFlashEditor(WebDriver driver) {
        super(driver);
    }

    @Override
    public void isOpened() {
        By iframe = By.xpath("//iframe[contains(@id, 'vfFrame')]");
        checkTrue(waitUntilPageLoaded(), "DaDaDocsFlashEditor page isn`t loaded");
        driver.switchTo().defaultContent();
        waitForSalesforceLoading();
        TimeMan.sleep(5);
        click(By.xpath("//body"));
        switchToFrame(iframe);
        TimeMan.sleep(10);
        switchToFrame(0, 30);
        TimeMan.sleep(5);

        By loader = By.id("jsloader-container");
        checkTrue(isElementPresent(loader, 60), "Loader wasn't dissappear");
        checkTrue(isElementContainsStringInAttribute(loader, "style", "hidden", 60), "Flash editor is not opened");
        TimeMan.sleep(2);
    }

    @Step
    public Screenshot takeScreenShotEditor() {
        AShotMan aShot = new AShotMan(driver);
        WebElement element = driver.findElement(By.xpath("//body"));

        return aShot.takeScreenshotWithOutJquery(element);
    }
}
