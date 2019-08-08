package pages.salesforce.app.sf_objects.contacts;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import pages.salesforce.SalesforceBasePage;
import pages.salesforce.app.sf_objects.SalesAppHomePage;
import pages.salesforce.app.sf_objects.SalesforceBasePopUp;
import utils.Logger;

import static core.check.Check.checkTrue;

public class AddNewContactPopUp extends SalesforceBasePopUp {

    private By modalContainer = By.xpath("//div[contains(@class , 'modal-container')]");
    private By modalHeader = By.xpath("//*[contains(@id , 'title') and text()='New Contact']");

    public AddNewContactPopUp(WebDriver driver) {
        super(driver);
    }

    @Override
    public void isOpened() {
        checkTrue(isElementPresentAndDisplayed(modalContainer, 10), "AddNewContactPopUp Modal is not opened");
        checkTrue(isElementPresentAndDisplayed(modalHeader, 10), "Wrong modal is opened");
    }

    @Step
    public AddNewContactPopUp setFirstName(String name) {
        By firstNameField = By.xpath("//span[text()='First Name']/ancestor::div/input");

        Logger.info("Set new contact first name: " + name);
        checkTrue(isElementPresentAndDisplayed(firstNameField, 5), "Field for first name is not displayed");
        type(firstNameField, name);
        isOpened();
        return this;
    }

    @Step
    public AddNewContactPopUp setLastName(String name) {
        By lastNameField = By.xpath("//span[text()='Last Name']/ancestor::div/input");

        Logger.info("Set new contact last name: " + name);
        checkTrue(isElementPresentAndDisplayed(lastNameField, 5), "Field for last name is not displayed");
        type(lastNameField, name);
        isOpened();
        return this;
    }

    @Step
    public AddNewContactPopUp setEmail(String email) {
        By emailField = By.xpath("//input[@type ='email']");

        Logger.info("Set new contact email: " + email);
        checkTrue(isElementPresentAndDisplayed(emailField, 5), "Field for last name is not displayed");
        type(emailField, email);
        isOpened();
        return this;
    }

    @Step
    public SalesforceBasePage saveContact() {
        By btnSave = By.xpath("//*[contains(@id , 'title') and text()='New Contact']/ancestor::div[contains(@class, 'modal-container')]//span[@dir='ltr' and .='Save']");

        Logger.info("Save contact");
        checkTrue(isElementPresentAndDisplayed(btnSave, 5), "Save button is not displayed");
        click(btnSave);
        checkTrue(isElementNotDisplayed(btnSave, 5), "save button is still displayed");
        SalesAppHomePage concretePage = new SalesAppHomePage(driver);
        concretePage.isOpened();
        return concretePage;
    }
}
