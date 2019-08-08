package pages.salesforce.app.sf_objects.contacts;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import pages.salesforce.app.sf_objects.SalesforceObjectPage;
import pages.salesforce.enums.SalesTab;
import utils.Logger;

import static core.check.Check.checkTrue;

public class ContactsPage extends SalesforceObjectPage {

    private By btnEditContact = By.xpath("//body/div/div[@role='menu']//a[@title='Edit']");
    private By btnConfirmChangesContact = By.xpath("//button[contains(@class, 'forceActionButton') and (@title='Save')]");
    private By inputContactFirstName = By.xpath("//input[contains(@class, 'firstName')]");

    public ContactsPage(WebDriver driver) {
        super(driver);
        pageName = SalesTab.CONTACTS.getNameNewLayout();
    }

    @Step("Edit contact by name")
    public void editContactFirstName(String contactFullName, String newName) {
        By btnContactEditActions = By.xpath("//a[@title='" + contactFullName + "']/ancestor::tr//a[@role='button']");

        checkTrue(isElementPresentAndDisplayed(btnContactEditActions, 10), "Single contact link " + contactFullName + " is not presented");
        click(btnContactEditActions);
        checkTrue(isElementPresentAndDisplayed(btnEditContact, 3), "Menu in`t displayed");
        click(btnEditContact);
        checkTrue(isElementPresentAndDisplayed(btnConfirmChangesContact, 5), "Confirm Menu in`t displayed");
        checkTrue(isElementPresentAndDisplayed(inputContactFirstName, 5), "Confirm Menu in`t displayed");
        type(inputContactFirstName, newName);
        click(btnConfirmChangesContact);
        checkTrue(isElementNotDisplayed(btnConfirmChangesContact, 3), "Confirm Menu in`t displayed");
        isOpened();
    }

    @Step("Get {0} contact first name")
    public String getContactFirstName(int contactNumber) {
        By objectName = By.xpath("(//*[@class='slds-truncate outputLookupLink slds-truncate forceOutputLookup'])[" + contactNumber + "]");
        By btnContactEditActions = By.xpath("(//a[@title]/ancestor::tr//a[@role='button'])[" + contactNumber + "]");

        Logger.info("Get " + contactNumber + "contact first name");
        checkTrue(isElementPresentAndDisplayed(btnContactEditActions, 10), "Single contact link for contact number " + contactNumber + " is not presented");
        selectedObject = getText(objectName).replaceAll("<.*?>", "");
        click(btnContactEditActions);
        checkTrue(isElementPresentAndDisplayed(btnEditContact, 5), "Menu in`t displayed");
        click(btnEditContact);
        checkTrue(isElementPresentAndDisplayed(btnConfirmChangesContact, 10), "Confirm Menu isn`t displayed");
        checkTrue(isElementPresentAndDisplayed(inputContactFirstName, 5), "Confirm Menu isn`t displayed");
        String result = getAttribute(inputContactFirstName, "value");

        clickJS(btnConfirmChangesContact);
        checkTrue(isElementNotDisplayed(btnConfirmChangesContact, 10), "Confirm Menu still displayed");
        isOpened();
        return result;
    }
}
