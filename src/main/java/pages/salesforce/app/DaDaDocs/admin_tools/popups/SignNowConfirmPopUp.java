package pages.salesforce.app.DaDaDocs.admin_tools.popups;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import pages.salesforce.app.DaDaDocs.admin_tools.tabs.SettingsTab;
import pages.salesforce.app.sf_objects.SalesforceBasePopUp;
import utils.Logger;

import static core.check.Check.checkEquals;
import static core.check.Check.checkTrue;

public class SignNowConfirmPopUp extends SalesforceBasePopUp {

    private By title = By.xpath("//div[contains(@class, 'signNowConfirm__')]//span[contains(@class, 'popup__title__')]");
    private By body = By.xpath("//div[contains(@class, 'signNowConfirm__')]//div[contains(@class, 'popup__text__')]/div");

    public SignNowConfirmPopUp(WebDriver driver) {
        super(driver);
    }

    @Override
    public void isOpened() {
        popUpBody = By.xpath("//div[contains(@class, 'signNowConfirm__')]");
        checkTrue(isElementPresentAndDisplayed(popUpBody, 10), "SignNow confirm pop up is not opened");
        checkEquals(getText(title), "Oops! The SignNow package hasn't been installed yet.", "Wrong pop up text");
        checkEquals(getText(body), "Install SignNow package for your Salesforce organization in a few clicks." +
                        " Remember to enable SignNow by checking the box in settings after the package has been installed.",
                "Wrong pop up body text");
    }

    @Step
    public SettingsTab cancelSignNowInstallation() {
        By btnCancel = By.xpath("//div[contains(@class, 'signNowConfirm__')]//button[.='Cancel']");

        Logger.info("Cancel SignNow package installation");
        checkTrue(isElementPresentAndDisplayed(btnCancel, 2), "SignNow package installation Cancel " +
                "button is not displayed");
        click(btnCancel);
        SettingsTab settingsTab = new SettingsTab(driver);
        settingsTab.isOpened();
        return settingsTab;
    }
}
