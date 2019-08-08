package pages.salesforce.app.DaDaDocs.admin_tools.popups;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import pages.salesforce.app.DaDaDocs.admin_tools.tabs.SettingsTab;
import pages.salesforce.app.sf_objects.SalesforceBasePopUp;

import static core.check.Check.checkEquals;
import static core.check.Check.checkTrue;

public class DeleteCloudStoragePopUp extends SalesforceBasePopUp {

    private By title = By.xpath("//div[contains(@class, 'popup_opened__')]//span[contains(@class, 'popup__title__')]");

    public DeleteCloudStoragePopUp(WebDriver driver) {
        super(driver);
    }


    @Override
    public void isOpened() {
        popUpBody = By.xpath("//div[contains(@class, 'popup_opened__')]");
        checkTrue(isElementPresentAndDisplayed(popUpBody, 10), "Cloud storage delete pop up is not opened");
        checkEquals(getText(title), "Delete cloud storage?", "Wrong title for cloud storage delete pop up");
    }

    @Step
    public SettingsTab deleteStorage() {
        By btnDelete = By.xpath("//div[contains(@class, 'popup_opened__')]//button[.='Delete']");

        checkTrue(isElementPresentAndDisplayed(btnDelete, 4), "Delete button is not displayed");
        click(btnDelete);
        SettingsTab tab = new SettingsTab(driver);
        tab.isOpened();
        return tab;
    }
}
