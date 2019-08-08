package pages.salesforce.app.DaDaDocs.admin_tools.popups;

import core.AssertionException;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import pages.salesforce.app.DaDaDocs.admin_tools.tabs.SettingsTab;
import pages.salesforce.app.sf_objects.SalesforceBasePopUp;
import utils.Logger;

import static core.check.Check.checkEquals;
import static core.check.Check.checkTrue;

public class CloudStorageIntegrationPopUp extends SalesforceBasePopUp {

    private By title = By.xpath("//div[contains(@class, 'cloudStorageModal__')]//span[contains(@class, 'popup__title__')]");

    public CloudStorageIntegrationPopUp(WebDriver driver) {
        super(driver);
    }

    @Override
    public void isOpened() {
        popUpBody = By.xpath("//div[contains(@class, 'cloudStorageModal__')]");
        checkTrue(isElementPresentAndDisplayed(popUpBody, 10), "Cloud storage integration pop up is not opened");
        checkEquals(getText(title), "Cloud Storage Integration", "Wrong title for cloud storage pop up");
    }

    @Step
    public CloudStorageIntegrationPopUp setSecretAndAccessKey(String secretKey, String accessKey) {
        By inputSecretKey = By.name("secretKey");
        By inputAccessKey = By.name("accessKeyId");

        checkTrue(isElementPresentAndDisplayed(inputSecretKey, 4), "Secret key input is not displayed");
        type(inputSecretKey, secretKey);
        checkTrue(isElementPresentAndDisplayed(inputAccessKey, 4), "Access key input is not displayed");
        type(inputAccessKey, accessKey);
        return this;
    }

    @Step
    public CloudStorageIntegrationPopUp selectRegion(String region) {
        By regionSelector = By.xpath("//*[contains(@class, 'regionSelect__')]");
        By regionOption = By.xpath("//*[@aria-label='" + region + "']");

        checkTrue(isElementPresentAndDisplayed(regionSelector, 4), "Region selector is not displayed");
        click(regionSelector);
        checkTrue(isElementPresentAndDisplayed(regionOption, 4), region + " region name option is not displayed");
        click(regionOption);
        checkTrue(isElementNotDisplayed(regionOption, 4), "region option is still present");
        isOpened();
        return this;
    }

    @Step
    public CloudStorageIntegrationPopUp authorizeInAmazon() {
        By btnAuthorize = By.xpath("//*[contains(@class, 'authorizeToStorageButton__')]");

        checkTrue(isElementPresentAndDisplayed(btnAuthorize, 4), "Authorize btn is not displayed");
        click(btnAuthorize);
        isOpened();
        return this;
    }

    @Step
    public CloudStorageIntegrationPopUp setBucketAndPath(String bucketName, String bucketKey) {
        By bucketSelector = By.xpath("//*[contains(@class,'bucketsSelect__')]");
        By bucketOption = By.xpath("//*[@aria-label='" + bucketName + "']");
        By bucketPath = By.name("bucketPath");

        try {
            checkTrue(isElementPresentAndDisplayed(bucketSelector, 4), "Bucket selector is not displayed");
            click(bucketSelector);
            checkTrue(isElementPresentAndDisplayed(bucketOption, 4), "Bucket option is not displayed");
            click(bucketOption);
        } catch (AssertionException e) {
            bucketSelector = By.name("bucketName");
            Logger.error("Cannot authorize in s3");
            checkTrue(isElementPresentAndDisplayed(bucketSelector, 4), "Bucket selector is not displayed");
            type(bucketSelector, bucketName);
        }
        checkTrue(isElementPresentAndDisplayed(bucketPath, 4), "Bucket path input is not displayed");
        type(bucketPath, bucketKey);
        isOpened();
        return this;
    }

    @Step
    public SettingsTab saveSettings() {
        By btnSaveSettings = By.xpath("//*[contains(@class, 'popup__body__')]//*[contains(@class, 'saveChangesButton__')]");

        checkTrue(isElementPresentAndDisplayed(btnSaveSettings, 4), "Save settings button is not displayed");
        click(btnSaveSettings);
        SettingsTab settingsTab = new SettingsTab(driver);
        settingsTab.isOpened();
        return settingsTab;
    }
}
