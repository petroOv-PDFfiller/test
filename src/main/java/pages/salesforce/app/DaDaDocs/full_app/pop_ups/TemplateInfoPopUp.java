package pages.salesforce.app.DaDaDocs.full_app.pop_ups;

import core.check.SoftCheck;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import pages.salesforce.app.DaDaDocs.full_app.DaDaDocsFullApp;
import pages.salesforce.app.SalesAppBasePage;
import pages.salesforce.app.sf_objects.SalesforceBasePopUp;

import static core.check.Check.checkEquals;
import static core.check.Check.checkTrue;

public class TemplateInfoPopUp extends SalesforceBasePopUp {

    protected By popUpContainer = By.xpath("//div[@class = 'popup__inner-content']");
    protected By popUpHeader = By.xpath("//*[@class = 'popup__header']//*[@class = 'popup__title']");
    protected By templateName = By.xpath("(//span[contains(@class, 'popup__column')])[1]");
    protected By templateDescription = By.xpath("(//span[contains(@class, 'popup__column')])[2]");
    protected By templateId = By.xpath("(//span[contains(@class, 'popup__column')])[3]");
    protected By templateOwnerNameEmail = By.xpath("(//span[contains(@class, 'popup__column')])[4]");
    protected By templateCreatedAt = By.xpath("(//span[contains(@class, 'popup__column')])[5]");

    public TemplateInfoPopUp(WebDriver driver) {
        super(driver);
    }

    @Override
    public void isOpened() {
        checkTrue(isElementPresentAndDisplayed(popUpContainer, 5), "Pop up is not displayed");
        checkEquals(getText(popUpHeader), "Template Info", "Wrong header for template info popup");
    }

    @Step
    public String getTemplateName() {
        checkTrue(isElementPresentAndDisplayed(templateName, 5), "Template Name is not present");
        return getText(templateName);
    }

    @Step
    public String getTemplateDescription() {
        checkTrue(isElementPresentAndDisplayed(templateDescription, 5), "Template Description is not present");
        return getText(templateDescription);
    }

    @Step
    public void checkPopUpFields() {
        SoftCheck softCheck = new SoftCheck();
        softCheck.checkTrue(isElementDisplayed(templateName, 2), "Template name field is not displayed");
        softCheck.checkTrue(getText(templateName) != null && !getText(templateName).isEmpty(),
                "Template name field is empty");

        softCheck.checkTrue(isElementDisplayed(templateDescription, 2), "Template description field is not displayed");
        softCheck.checkTrue(getText(templateDescription) != null,
                "Template description field is empty");

        softCheck.checkTrue(isElementDisplayed(templateId, 2), "Template Id field is not displayed");
        softCheck.checkTrue(getText(templateId) != null && !getText(templateId).isEmpty(),
                "Template Id field is empty");

        softCheck.checkTrue(isElementDisplayed(templateOwnerNameEmail, 2),
                "Template owner name email field is not displayed");
        softCheck.checkTrue(getText(templateOwnerNameEmail) != null && !getText(templateOwnerNameEmail).isEmpty(),
                "Template owner name email field is empty");

        softCheck.checkTrue(isElementDisplayed(templateCreatedAt, 2), "Template created at field is not displayed");
        softCheck.checkTrue(getText(templateCreatedAt) != null && !getText(templateCreatedAt).isEmpty(),
                "Template created at field is empty");
        softCheck.checkAll();
    }

    @Step
    public <T extends SalesAppBasePage> T closePopUp() {
        By btnCloseInfo = By.xpath("//button[contains(@class, 'popup--btn-close-info')]");
        checkTrue(isElementPresentAndDisplayed(btnCloseInfo, 5), "close button is not displayed");
        click(btnCloseInfo);
        DaDaDocsFullApp daDaDocsFullApp = new DaDaDocsFullApp(driver);
        daDaDocsFullApp.isOpened();
        return (T) daDaDocsFullApp;
    }
}
