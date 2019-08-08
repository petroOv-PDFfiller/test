package pages.salesforce.app.airSlate_app.admin_tools.wizards.custom_button;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import pages.salesforce.app.SalesAppBasePage;
import pages.salesforce.app.airSlate_app.admin_tools.wizards.LeaveWizardPopUp;
import pages.salesforce.app.airSlate_app.admin_tools.wizards.WizardPage;

import static core.check.Check.checkTrue;

public class CustomButtonInfoWizardPage extends WizardPage {

    private By inputButtonLabel = By.name("label");
    private By inputButtonDescription = By.name("description");

    public CustomButtonInfoWizardPage(WebDriver driver) {
        super(driver);
        title = By.xpath("//*[contains(@class, 'title__') and text()='Custom button info']");
    }

    @Override
    public void isOpened() {
        skipLoader();
        checkTrue(isElementPresentAndDisplayed(title, 20), "Custom button info page title is not displayed");
    }

    public void skipLoader() {
        checkTrue(isElementDisappeared(loader, 90), "Loader is still present");
    }

    public String getButtonLabel() {
        checkIsElementDisplayed(inputButtonLabel, 4, "Button label input field");
        return getAttribute(inputButtonLabel, "value");
    }

    @Step
    public CustomButtonInfoWizardPage setButtonLabel(String buttonLabel) {
        checkIsElementDisplayed(inputButtonLabel, 2, "Button label input field");
        type(inputButtonLabel, buttonLabel);
        return this;
    }

    public String getButtonDescription() {
        checkIsElementDisplayed(inputButtonDescription, 4, "Button description input field");
        return getAttribute(inputButtonDescription, "value");
    }

    @Step
    public CustomButtonInfoWizardPage setButtonDescription(String buttonDescription) {
        checkIsElementDisplayed(inputButtonDescription, 2, "Button description input field");
        type(inputButtonDescription, buttonDescription);
        return this;
    }

    @Step
    public boolean isButtonDescriptionErrorIsShown() {
        By nameError = By.xpath("//*[@name='description']/following::*[contains(@class, 'error__') and text()='Maximum 255 symbols']");
        return isElementDisplayed(nameError, 2);
    }

    @Step
    public boolean isButtonLabelErrorIsShown() {
        By labelError = By.xpath("//*[@name='label']/following::*[contains(@class, 'error__') and text()='Maximum 240 symbols']");
        return isElementDisplayed(labelError, 2);
    }

    @Override
    public <T extends SalesAppBasePage> T navigateToPreviousTab() {
        super.navigateToPreviousTab();
        return (T) initExpectedPage(LeaveWizardPopUp.class);
    }

    @Override
    public <T extends SalesAppBasePage> T navigateToNextTab() {
        super.navigateToNextTab();
        return (T) initExpectedPage(ActionSettingsWizardPage.class);
    }
}
