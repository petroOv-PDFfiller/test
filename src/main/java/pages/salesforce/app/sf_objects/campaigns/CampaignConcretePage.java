package pages.salesforce.app.sf_objects.campaigns;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import pages.salesforce.app.sf_objects.ConcreteRecordPage;

import static core.check.Check.checkTrue;

public class CampaignConcretePage extends ConcreteRecordPage {

    private By orderFormButton = By.xpath("//div[text()='Order form']");

    public CampaignConcretePage(WebDriver driver) {
        super(driver);
    }

    @Override
    public void isOpened() {
        switchToDefaultContent();
        checkTrue(waitUntilPageLoaded(), "Campaign concrete page is not loaded");
        waitForSalesforceLoading();
        checkTrue(isElementPresent(detailsTab, 60), "Page for a contact is not opened");
    }

    @Step
    public boolean isObjectRecordPresent(String name, int quantity) {
        By record = By.xpath("//span[@class='uiOutputTextArea' and text()='" + name + "']/ancestor::tr//span[@class='uiOutputNumber' and text()='" + quantity + "']");
        return isElementPresentAndDisplayed(record, 10);
    }

    @Step
    public void deleteObjectRecord(String name, int quantity) {
        By btnObjectEditActions = By.xpath("//span[@class='uiOutputTextArea' and text()='" + name + "']/ancestor::tr//span[@class='uiOutputNumber' and text()='" + quantity + "']" +
                "/ancestor::tr//a[@role='button']");
        By btnDeleteObject = By.xpath("//body/div[contains(@class, 'visible')]/div[@role='menu']//a[@title='Delete']");
        By btnConfirmDeleteObject = By.xpath("//button[contains(@class, 'forceActionButton') and (@title='Delete')]");
        checkTrue(isElementPresentAndDisplayed(btnObjectEditActions, 10), "Object  " + name + " is not presented");
        click(btnObjectEditActions);
        checkTrue(isElementPresentAndDisplayed(btnDeleteObject, 3), "Menu in`t displayed");
        click(btnDeleteObject);
        checkTrue(isElementPresentAndDisplayed(btnConfirmDeleteObject, 3), "Confirm Menu in`t displayed");
        click(btnConfirmDeleteObject);
        checkTrue(isElementNotDisplayed(btnConfirmDeleteObject, 3), "Confirm Menu in`t displayed");
        isOpened();
    }
}
