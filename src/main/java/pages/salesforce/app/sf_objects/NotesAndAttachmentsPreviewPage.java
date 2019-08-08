package pages.salesforce.app.sf_objects;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import pages.salesforce.app.SalesAppBasePage;

import static core.check.Check.checkTrue;

public class NotesAndAttachmentsPreviewPage extends SalesAppBasePage {

    private By filePreviewTitle = By.xpath("//*[@class = 'file-preview-title']");

    public NotesAndAttachmentsPreviewPage(WebDriver driver) {
        super(driver);
    }


    @Override
    public void isOpened() {
        checkTrue(isElementPresent(filePreviewTitle, 10), "File preview Page is not displayed");
    }

    @Step
    public NotesAndAttachmentsPreviewPage downloadFile() {
        By btnDownload = By.xpath("//button[@title = 'Download']");

        scrollPage(0);
        checkTrue(isElementPresentAndDisplayed(btnDownload, 5), "Download button is not displayed");
        clickJS(btnDownload);
        isOpened();
        return this;
    }
}
