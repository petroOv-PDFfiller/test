package pages.salesforce.app.sf_objects.opportunities;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import pages.salesforce.app.sf_objects.SalesforceBasePopUp;
import pages.salesforce.app.sf_objects.contacts.AddNewContactPopUp;
import utils.Logger;

import static core.check.Check.checkEquals;
import static core.check.Check.checkTrue;

public class ManageContactRolesPopUp extends SalesforceBasePopUp {

    private By modalContainer = By.xpath("//div[contains(@class , 'modal-container')]");
    private By modalTitle = By.xpath("//div[contains(@class , 'modal-container')]//div[contains(@class , 'title')]");

    public ManageContactRolesPopUp(WebDriver driver) {
        super(driver);
    }

    @Override
    public void isOpened() {
        checkTrue(isElementPresentAndDisplayed(modalContainer, 15), "Modal is not opened");
        checkEquals(getAttribute(modalTitle, "textContent"), "Manage Contact Roles", "Wrong modal is opened");
    }

    @Step
    public AddNewContactPopUp openAddNewContactPopUp() {
        By contactField = By.xpath("//div[contains(@class , 'modal-container')]//span[@class='triggerContainer']/button");
        By btnNewContact = By.xpath("//span[@title='New Contact']");

        checkTrue(isElementPresentAndDisplayed(contactField, 5), "Contact filed is not displayed");
        click(contactField);
        checkTrue(isElementPresentAndDisplayed(btnNewContact, 10), "New contactButton is not displayed");
        click(btnNewContact);
        AddNewContactPopUp addNewContactPopUp = new AddNewContactPopUp(driver);
        addNewContactPopUp.isOpened();
        return addNewContactPopUp;
    }

    @Step
    public ManageContactRolesPopUp setPrimaryContact(String contactName) {
        By primaryField = By.xpath("//*[@class = 'primaryLookup']//input[contains(@id, 'input-')]");
        By contact = By.xpath("//span[@title='" + contactName + "']");

        Logger.info("Set primary contact role: " + contactName);
        checkTrue(isElementPresentAndDisplayed(primaryField, 10), "primary contact input field is not displayed");
        type(primaryField, contactName);
        checkTrue(isElementPresentAndDisplayed(contact, 10), "Needful contact is not present");
        click(contact);
        isOpened();
        return this;
    }

    @Step
    public OpportunitiesConcretePage saveContactRoles() {
        By btnSave = By.xpath("//button[@title='Save']");

        Logger.info("Save contact roles");
        checkTrue(isElementPresentAndDisplayed(btnSave, 10), "Save button is not displayed");
        click(btnSave);
        checkTrue(isElementNotDisplayed(btnSave, 10), "Save button is still present");
        OpportunitiesConcretePage opportunitiesConcretePage = new OpportunitiesConcretePage(driver);
        opportunitiesConcretePage.isOpened();
        return opportunitiesConcretePage;
    }
}
