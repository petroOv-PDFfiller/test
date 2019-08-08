package pages.salesforce.app.DaDaDocs.full_app.templates.tabs;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import pages.salesforce.app.SalesAppBasePage;
import utils.Logger;

import static core.check.Check.checkEquals;
import static core.check.Check.checkTrue;

public class TemplateMapToUpdatePage extends TemplateMappingPage {

    private By btnCopyMapping = By.xpath("//*[contains(@class, 'copyBtn__')]");
    private By btnCancelCopying = By.xpath("//div[contains(@class, 'popup__overlay')]//button[contains(@class, 'btn__secondary__')]");
    private By btnAcceptCopying = By.xpath("//div[contains(@class, 'popup__overlay')]//button[contains(@class, 'btn__accent')]");

    public TemplateMapToUpdatePage(WebDriver driver) {
        super(driver);
        descriptionText = "To update records using data from a filled document, please select the object and specify " +
                "the relationship between Salesforce fields and document fields to be filled";
        headingText = "Update records using data from a filled document";
        pageName = "Map to prefill";
    }

    @Override
    @Step
    public <T extends SalesAppBasePage> T navigateToPreviousTab() {
        Logger.info("Navigate to previous tab");
        checkTrue(isElementPresentAndDisplayed(btnPreviousTab, 5), "Previous tab button is not displayed");
        click(btnPreviousTab);
        TemplateMapToPrefillPage templateMapToPrefillPage = new TemplateMapToPrefillPage(driver);
        templateMapToPrefillPage.isOpened();
        return (T) templateMapToPrefillPage;
    }

    @Override
    @Step
    public <T extends SalesAppBasePage> T navigateToNextTab() {
        Logger.info("Navigate to next tab");
        checkTrue(isElementPresentAndDisplayed(btnNextTab, 5), "Next tab button is not displayed");
        click(btnNextTab);
        TemplateMapToCreatePage templateMapToCreatePage = new TemplateMapToCreatePage(driver);
        templateMapToCreatePage.isOpened();
        return (T) templateMapToCreatePage;
    }

    @Step
    public TemplateMapToUpdatePage clickOnCopyPrefill() {
        Logger.info("copy prefill mapping");
        By popUpText = By.xpath("//div[contains(@class, 'popup__overlay')]//div[contains(@class, 'bodyText__')]");

        int recordsBeforeCopy = getNumberOfElements(addedRecords);
        checkTrue(isElementPresentAndDisplayed(btnCopyMapping, 5), "Copy button is not displayed");
        click(btnCopyMapping);
        if (recordsBeforeCopy > 0) {
            checkTrue(isElementPresentAndDisplayed(mappingPopUp, 5), "Pop up is not opened");
            checkEquals(getText(popUpText).trim(), "Clicking 'Replace' will replace current mapping" +
                    " with mapping from the Prefill section", "Wrong replace popUp text");
        }
        isOpened();
        return this;
    }

    @Step
    public TemplateMapToUpdatePage cancelCopying() {
        Logger.info("Cancel copy prefill mapping");
        checkTrue(isElementPresentAndDisplayed(btnCancelCopying, 5), "Cancel copying button is not displayed");
        click(btnCancelCopying);
        checkTrue(isElementNotDisplayed(mappingPopUp, 5), "Pop up is still displayed");
        isOpened();
        return this;
    }

    @Step
    public TemplateMapToUpdatePage acceptCopying() {
        Logger.info("Accept copy prefill mapping");
        checkTrue(isElementPresentAndDisplayed(btnAcceptCopying, 5), "Accept copying button is not displayed");
        click(btnAcceptCopying);
        checkTrue(isElementNotDisplayed(mappingPopUp, 5), "Pop up is still displayed");
        isOpened();
        return this;
    }
}
