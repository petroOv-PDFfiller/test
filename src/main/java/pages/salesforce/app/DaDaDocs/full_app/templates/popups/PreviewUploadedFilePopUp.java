package pages.salesforce.app.DaDaDocs.full_app.templates.popups;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import pages.salesforce.app.DaDaDocs.full_app.templates.tabs.x_templates.TemplateUploadPage;
import pages.salesforce.app.sf_objects.SalesforceBasePopUp;
import utils.Logger;

import static core.check.Check.checkEquals;
import static core.check.Check.checkTrue;

public class PreviewUploadedFilePopUp extends SalesforceBasePopUp {

    private By popUpHeader = By.xpath("//div[contains(@class, 'popup__header__')]//*[contains(@class, 'popup__title__')]");

    public PreviewUploadedFilePopUp(WebDriver driver) {
        super(driver);
    }

    @Override
    public void isOpened() {
        popUpBody = By.xpath("//div[contains(@class, 'popup__container__')]");
        checkTrue(isElementPresentAndDisplayed(popUpBody, 4), "Preview upload file popup is not displayed");
        checkEquals(getText(popUpHeader), "Preview settings", "Wrong header for Preview upload file popup");
    }

    @Step
    public TemplateUploadPage skipPreview() {
        By btnSkip = By.xpath("//*[contains(@class, 'buttonsContainer__')]//button[contains(@class, 'btn__secondary__') and .='Skip']");

        Logger.info("Skip uploaded file preview");
        checkTrue(isElementEnabled(btnSkip), "Skip button is not enable");
        click(btnSkip);
        TemplateUploadPage uploadPage = new TemplateUploadPage(driver);
        uploadPage.isOpened();
        return uploadPage;
    }

    @Step
    public PreviewUploadedFilePopUp selectRecordForPreview(String recordName) {
        By selectRecord = By.xpath("//*[contains(@class, 'select__')]");
        By record = By.xpath("//*[contains(@class,'Select-option') and @aria-label]");

        checkTrue(isElementPresentAndDisplayed(selectRecord, 5), "select record for preview field is not displayed");
        click(selectRecord);
        checkTrue(isElementPresentAndDisplayed(record, 5), recordName + " is not displayed");
        click(record);
        isOpened();
        return this;
    }

    @Step
    public TemplateUploadPage generatePreview() {
        By btnGeneratePreview = By.xpath("//button[contains(@class, 'btn__accent__') and .='Generate preview']");

        checkTrue(isElementPresentAndDisplayed(btnGeneratePreview, 5), "Button generate is not displayed");
        click(btnGeneratePreview);
        TemplateUploadPage templateUploadPage = new TemplateUploadPage(driver);
        templateUploadPage.isOpened();
        return templateUploadPage;
    }
}
