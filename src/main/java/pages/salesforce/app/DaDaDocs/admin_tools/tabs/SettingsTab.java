package pages.salesforce.app.DaDaDocs.admin_tools.tabs;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import pages.salesforce.app.DaDaDocs.admin_tools.AdminToolsPage;
import pages.salesforce.app.DaDaDocs.admin_tools.popups.CloudStorageIntegrationPopUp;
import pages.salesforce.app.DaDaDocs.admin_tools.popups.DeleteCloudStoragePopUp;
import pages.salesforce.app.DaDaDocs.admin_tools.popups.WipeSettingsPopUp;
import pages.salesforce.enums.admin_tools.AdminToolFeatureSettings;
import utils.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static core.check.Check.checkEquals;
import static core.check.Check.checkTrue;

public class SettingsTab extends AdminToolsPage {

    private By header = By.xpath("//*[contains(@class, 'header__') and text()='DaDaDocs settings']");
    private By settingsBlock = By.xpath("//*[contains(@class, 'specificSettings__')]");
    private By btnSaveChanges = By.xpath("//*[contains(@class, 'saveChangesButton__')]");
    private String featuresUpdated = "Your Feature Specific Settings were updated!";
    private Map<String, Boolean> settings;

    public SettingsTab(WebDriver driver) {
        super(driver);
    }

    @Override
    public void isOpened() {
        checkTrue(isElementPresentAndDisplayed(header, 20), "Settings tab header is not displayed");
        checkTrue(isElementPresentAndDisplayed(settingsBlock, 20), "Settings list is not loaded");
        checkTrue(isElementNotDisplayed(newLoader, 20), "New loader is not hide");
    }

    public boolean isSaveButtonActive() {
        checkTrue(isElementPresentAndDisplayed(btnSaveChanges, 5), "Save changes button isn`t displayed");
        return isElementEnabled(btnSaveChanges);
    }

    @Step
    public SettingsTab saveChanges() {
        Logger.info("Save settings changes");
        checkTrue(isSaveButtonActive(), "Save changes button is not active");
        click(btnSaveChanges);
        checkTrue(notificationMessageContainsText(featuresUpdated), "Wrong notification message is displayed");
        isOpened();
        return this;
    }

    @Step
    public Map<String, Boolean> initSettings() {
        By settingNames = By.xpath("//*[contains(@class, 'specificSettings__')]//label[contains(@class, 'checkbox__')]");
        By settingStatuses = By.xpath("//*[contains(@class, 'specificSettings__')]//label[contains(@class, 'checkbox__')]/input");
        settings = new HashMap<>();

        Logger.info("Init settings");
        checkEquals(getNumberOfElements(settingNames), getNumberOfElements(settingStatuses), "Settings names and statuses count is not equals");
        List<WebElement> elements = driver.findElements(settingNames);
        List<WebElement> statuses = driver.findElements(settingStatuses);
        checkEquals(elements.size(), statuses.size(), "Setting name and status elements count is not equals");
        for (int i = 0; i < elements.size(); i++) {
            String settingName = elements.get(i).getText();
            boolean status = statuses.get(i).isSelected();
            settings.put(settingName, status);
        }
        return settings;
    }

    @Step("Switch setting status: {0}")
    public SettingsTab switchSettingStatus(AdminToolFeatureSettings settingObject) {
        String settingName = settingObject.getName();
        By setting = By.xpath("//*[contains(@class, 'specificSettings__')]//label[contains(@class, 'checkbox__') and .='" + settingName + "']");

        Logger.info("Switch status for " + settingName);
        checkTrue(isElementPresentAndDisplayed(setting, 2), "Setting " + settingName + " is not displayed");
        click(setting);
        isOpened();
        return this;
    }

    @Step
    public CloudStorageIntegrationPopUp addCloudStorage() {
        By btnAddCloudStorage = By.xpath("//button[.='Add cloud storage']");

        Logger.info("Add cloud storage");
        checkTrue(isElementPresentAndDisplayed(btnAddCloudStorage, 2), "Add cloud storage button is not present");
        click(btnAddCloudStorage);
        CloudStorageIntegrationPopUp integrationPopUp = new CloudStorageIntegrationPopUp(driver);
        isOpened();
        return integrationPopUp;
    }

    @Step
    public WipeSettingsPopUp wipeSettings() {
        By btnWipeSettings = By.xpath("//button[contains(@class,'wipeSettingsButton__')]");

        Logger.info("Wipe settings");
        checkTrue(isElementPresentAndDisplayed(btnWipeSettings, 2), "Add cloud storage button is not present");
        click(btnWipeSettings);
        WipeSettingsPopUp popUp = new WipeSettingsPopUp(driver);
        isOpened();
        return popUp;
    }

    public boolean isStorageAdded() {
        By storage = By.xpath("//*[contains(@class, 'storage__')]");
        return isElementPresentAndDisplayed(storage, 4);
    }

    @Step
    public DeleteCloudStoragePopUp deleteCloudStorage() {
        By btnDeleteStorage = By.xpath("//*[contains(@class, 'removeStorageButton__')]");

        checkTrue(isElementPresentAndDisplayed(btnDeleteStorage, 4), "Delete external storage button " +
                "is not displayed");
        click(btnDeleteStorage);
        DeleteCloudStoragePopUp popUp = new DeleteCloudStoragePopUp(driver);
        popUp.isOpened();
        return popUp;
    }

    @Step
    public CloudStorageIntegrationPopUp changeStorageSettings() {
        By btnChangeSettings = By.xpath("//*[contains(@class, 'changeSettingsButton__')]");

        checkTrue(isElementPresentAndDisplayed(btnChangeSettings, 4), "Change settings button is not" +
                " present");
        click(btnChangeSettings);
        CloudStorageIntegrationPopUp popUp = new CloudStorageIntegrationPopUp(driver);
        popUp.isOpened();
        return popUp;
    }

    @Step
    public void setSettingsTo(Map<String, Boolean> defaultSettings) {
        initSettings();

        for (String key : settings.keySet()) {
            if (defaultSettings.containsKey(key) && settings.get(key) != defaultSettings.get(key)) {
                switchSettingStatus(AdminToolFeatureSettings.valueOf(key));
            }
        }
    }
}
