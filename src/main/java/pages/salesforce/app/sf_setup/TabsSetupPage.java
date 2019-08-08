package pages.salesforce.app.sf_setup;

import core.DriverWindow;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.Select;
import pages.salesforce.SalesforceBasePage;
import utils.Logger;

import static core.check.Check.checkTrue;

public class TabsSetupPage extends SalesforceBasePage {

    private By title = By.xpath("//h1[contains(@class, 'pageType')]");

    public TabsSetupPage(WebDriver driver) {
        super(driver);
    }

    @Override
    public void isOpened() {
        switchToDefaultContent();
        checkTrue(waitUntilPageLoaded(), "Tabs setup page is not loaded");
        checkTrue(isElementPresentAndDisplayed(iframe, 20), "vFrame is not present");
        checkTrue(switchToFrame(iframe, 5), "Cannot switch to vframe");
        checkTrue(isElementPresentAndDisplayed(title, 10), "Tabs setup page is not opened");
    }

    @Step("Add new custom object tab with name {1}")
    public TabsSetupPage addNewCustomObjectTab(String tabName, String pluralName) {
        By btnNewTab = By.xpath("//div[@id='CustomObjectTabList']//input[@value=' New ']");
        By tabStyle = By.xpath("//div[@id='motifElement_desc_p2motifElement']");
        By tabIcon = By.xpath("//div[contains(@id, 'motifElement_desc_Custom40')]");
        By btnNext = By.xpath("//input[@value = ' Next ']");
        By btnSave = By.xpath("//input[@value = ' Save ']");
        By expectedTab = By.xpath("//div[@id='CustomObjectTabList']//a[text()='" + pluralName + "']");
        By object = By.id("p1");
        String imgPicker = "pages/MotifPicker";

        Logger.info("Add " + tabName + " custom tab");
        if (isElementPresent(expectedTab)) {
            Logger.info("Tab is already added");
        } else {
            checkTrue(isElementPresentAndDisplayed(btnNewTab, 10), "New tab btn is not displayed");
            click(btnNewTab);
            isOpened();
            checkTrue(isElementPresentAndDisplayed(object, 5), "Object select is not displayed");
            Select objectSelect = new Select(driver.findElement(object));
            objectSelect.selectByVisibleText(tabName);
            checkTrue(isElementPresent(tabStyle, 5), "Tab style select is not displayed");
            DriverWindow tabSettings = driverWinMan.getCurrentWindow();
            setStyle("Custom50");
            driverWinMan.switchToWindow(tabSettings);
            isOpened();
            checkTrue(isElementPresentAndDisplayed(btnSave, 5), "Next button is displayed");
            click(btnSave);
            isOpened();
            checkTrue(isElementPresentAndDisplayed(btnSave, 5), "Next button is displayed");
            click(btnSave);
            checkTrue(isElementPresentAndDisplayed(btnSave, 5), "Save button is displayed");
            click(btnSave);
            checkTrue(isElementDisappeared(btnSave, 10), "Save button is still present");
            isOpened();
            checkTrue(isElementPresentAndDisplayed(expectedTab, 5), "tab is not added");
        }

        isOpened();
        return this;
    }

    private void setStyle(String styleName) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("document.getElementById('p2motifName').setAttribute('value', '" + styleName + "')");

    }
}
