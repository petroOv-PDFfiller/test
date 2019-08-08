package pages.salesforce.app.sf_setup.object_manager;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import pages.salesforce.app.SalesAppBasePage;

import static core.check.Check.checkTrue;

public class NewCustomObjectPage extends SalesAppBasePage {
    private By titleNewCustomObjectIframe =
            By.xpath("//*[@class='noSecondHeader pageType' and contains(text(),'New Custom Object')]");
    private By inputLabel = By.id("MasterLabel");
    private By titlePluralLabel = By.id("PluralLabel");
    private By btnSave = By.xpath("//td[@id='bottomButtonRow']/input[@name='save']");
    private By inputObjectName = By.id("DeveloperName");

    public NewCustomObjectPage(WebDriver driver) {
        super(driver);
    }

    @Override
    public void isOpened() {
        checkTrue(waitUntilPageLoaded(), "SetupSalesForcePage is not loaded");
        waitForSalesforceLoading();
        switchToDefaultContent();
        checkTrue(switchToFrame(iframe), "Iframe not present");
        checkTrue(isElementPresent(titleNewCustomObjectIframe, 60), "Setup app page is not opened");
    }

    @Step("Create new object. Parametes: label - {0}, pluralLabelText - {1}")
    public EditCustomObjectPage createNewObject(String label, String pluralLabelText, String objectName) {
        checkTrue(isElementPresentAndDisplayed(inputLabel, 5), "Create new object page not opened");
        hoverOverAndClick(inputLabel);
        type(inputLabel, label);
        hoverOverAndClick(titlePluralLabel);
        type(titlePluralLabel, pluralLabelText);
        if (!objectName.isEmpty()) {
            hoverOverAndClick(inputObjectName);
            type(inputObjectName, objectName);
        }
        click(btnSave);
        EditCustomObjectPage editCustomObjectPage = new EditCustomObjectPage(driver);
        editCustomObjectPage.isOpened();
        return editCustomObjectPage;
    }
}
