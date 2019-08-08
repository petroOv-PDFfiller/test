package pages.salesforce;

import core.PageBase;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import pages.salesforce.app.AppLauncherPopUp;
import pages.salesforce.app.SalesAppBasePage;
import utils.Logger;
import utils.TimeMan;

import static core.check.Check.checkTrue;

public abstract class SalesforceBasePage extends PageBase {
    protected By iframe = By.xpath("//iframe[contains(@name, 'vfFrameId_')]");
    protected By fillerFrame = By.xpath("//iframe[@id='pdffillerIframe']");
    protected By editorFrame = By.xpath("//iframe[contains(@src,'pdffiller')]");
    protected By orangeLoader = By.cssSelector("#orangeLoader");
    protected By loader = By.xpath("//*[contains(@class, 'g-loader__label')]");
    protected By loadingCompleted = By.xpath("//*[@class='auraLoadingBox oneLoadingBox loadingHide']");
    protected By vMappingLoader = By.xpath("//*[contains(@class,'loaderWrap__')]");
    protected By salesPageModalLoader = By.cssSelector("div.slds-spinner_container");
    protected By dadadocsFrame = By.xpath("//iframe[contains(@src,'LightningDaDaDocs?id')]");
    protected By btnAppLauncher = By.xpath("//nav[contains(@class, 'appLauncher')]");
    protected By salesForceBody = By.xpath("//div[@id='brandBand_1']");
    protected By setupGear = By.xpath("//div[@class = 'setupGear']");
    protected By setupGearMenu = By.xpath("//div[@class = 'setupGear']//div[contains(@class, 'popupTargetContainer')]");
    private By salesForceGlobalLoader = By.cssSelector("div.g-wrap-loading.g-wrap-loading_global");

    public SalesforceBasePage(WebDriver driver) {
        super(driver);
    }

    public void waitForSalesforceLoading() {
        checkTrue(isElementPresent(loadingCompleted, 60), "Page wasn't loaded");
    }

    @Step
    public AppLauncherPopUp openAppLauncher() {
        checkTrue(isElementPresentAndDisplayed(btnAppLauncher, 10), "App launcher button is not presented");
        checkTrue(isElementPresentAndDisplayed(salesForceBody, 25), "Salesforce Body is not loaded");
        TimeMan.sleep(3);
        click(btnAppLauncher);
        AppLauncherPopUp appLauncherPopUp = new AppLauncherPopUp(driver);
        appLauncherPopUp.isOpened();
        return appLauncherPopUp;
    }

    @Step
    public ClassicSalesforcePage switchToClassicInterface() {
        By profileIcon = By.xpath("//div[contains(@class, 'profileTrigger')]");
        By btnSwitchToClassic = By.xpath("//a[contains(@class, 'switch-to-aloha')]");

        Logger.info("Switch to classic design");
        switchToDefaultContent();
        checkTrue(isElementPresentAndDisplayed(profileIcon, 10), "Profile icon is not displayed");
        click(profileIcon);
        checkTrue(isElementPresentAndDisplayed(btnSwitchToClassic, 10), "Switch to classic design button is not displayed");
        clickJS(btnSwitchToClassic);
        checkTrue(isElementNotDisplayed(btnSwitchToClassic, 20), "Switch to lightning button still displayed");
        ClassicSalesforcePage salesforcePage = new ClassicSalesforcePage(driver);
        salesforcePage.isOpened();
        return salesforcePage;
    }

    @Step
    public SalesAppBasePage returnToLightningDesign() {
        By btnSwitchToLighting = By.xpath("//a[contains(@class, 'switch-to-lightning')]");

        Logger.info("Return to lightning design");
        checkTrue(isElementPresentAndDisplayed(btnSwitchToLighting, 5), "Switch to lightning button is not displayed");
        click(btnSwitchToLighting);
        checkTrue(isElementNotDisplayed(btnSwitchToLighting, 20), "Switch to lightning button still displayed");
        SalesAppBasePage salesAppBasePage = new SalesAppBasePage(driver);
        salesAppBasePage.isOpened();
        return salesAppBasePage;
    }

    protected boolean isFrameDisappeared(By elementInFrame, int seconds) {
        try {
            return isElementDisappeared(elementInFrame, seconds);
        } catch (WebDriverException e) {
            Logger.info("iframe switch error");//temporary chrome v72 fix
            return true;
        }
    }

    public void skipLoader() {
        if (isElementPresentAndDisplayed(loader, 1)) {
            checkTrue(isElementNotDisplayed(loader, 60), "Page is still loading");
        }
    }

    public void skipGlobalLoader() {
        checkTrue(isElementDisappeared(salesForceGlobalLoader, 90), "Global loader is still displayed");
    }

    public void skipGlobalLoaderInMyDaDaDocs() {
        checkTrue(isElementNotDisplayed(salesForceGlobalLoader, 90), "Global loader is still displayed");
    }

    public boolean isElementPresentAndDisplayed(By locator, int seconds) {
        int startTime = (int) (System.currentTimeMillis() / 1000);
        if (isElementPresent(locator, seconds)) {
            int currentTime = (int) (System.currentTimeMillis() / 1000);
            int timeLeft = seconds - (currentTime - startTime);
            if (timeLeft > 0) {
                return isElementDisplayed(locator, timeLeft);
            } else {
                return false;
            }
        }
        return false;
    }

    public void checkIsElementDisplayed(By locator, int seconds, String elementName) {
        checkTrue(isElementPresentAndDisplayed(locator, seconds), elementName + " is not displayed");
    }

    public String getGeneratedFileName(String[] fileNames, String email) {
        StringBuilder modifiedName = new StringBuilder();
        for (int i = 0; i < fileNames.length; i++) {
            modifiedName.append(fileNames[i]);
            if (i < fileNames.length - 1)
                modifiedName.append(",");
        }
        modifiedName.append("-").append(email.replace('@', '_').replace('.', '_'));
        if (modifiedName.length() > 66) {
            modifiedName.setLength(66);
            modifiedName.trimToSize();
        }
        return modifiedName.toString();
    }

    public String getGeneratedSignedDocumentNameV3(String documentName, String recipientEmail) {
        return documentName +
                '-' +
                recipientEmail.replaceAll("[.@]", "_");
    }

    protected void switchToIfWindowsNumberIs(int windowsNumber, String windowsUrl) {
        try {
            if (new WebDriverWait(driver, 3).ignoring(Exception.class)
                    .until(ExpectedConditions.numberOfWindowsToBe(windowsNumber + 1))) {
                driverWinMan.switchToWindow(windowsUrl);
            }
        } catch (org.openqa.selenium.TimeoutException e) {
            Logger.info("Incorrect number of windows");
        }
    }
}