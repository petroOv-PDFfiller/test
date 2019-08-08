package pages.salesforce.app.airSlate_app.admin_tools.wizards.custom_button;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import pages.salesforce.app.SalesAppBasePage;
import pages.salesforce.app.airSlate_app.admin_tools.tabs.CustomButtonTab;
import pages.salesforce.app.airSlate_app.admin_tools.wizards.WizardPage;

import static core.check.Check.checkTrue;

public class SalesforceLayoutsAndListsWizardPage extends WizardPage {

    public SalesforceLayoutsAndListsWizardPage(WebDriver driver) {
        super(driver);
        title = By.xpath("//*[contains(@class, 'title__') and text()='Salesforce layouts and lists']");
    }

    @Override
    public void isOpened() {
        skipLoader();
        checkTrue(isElementPresentAndDisplayed(title, 20), "Salesforce layouts and lists page title is not displayed");
    }

    public void skipLoader() {
        checkTrue(isElementDisappeared(loader, 90), "Loader is still present");
    }

    @Step
    public SalesforceLayoutsAndListsWizardPage selectLayouts(String[] layoutNames) {
        for (String layoutName : layoutNames) {
            By layout = By.xpath("//*[contains(@class, 'rowItem__') and .='" + layoutName + "']");
            checkTrue(isElementPresent(layout), layoutName + " is not present");
            scrollTo(layout);
            click(layout);
        }
        return this;
    }

    @Override
    public <T extends SalesAppBasePage> T navigateToPreviousTab() {
        super.navigateToPreviousTab();
        return (T) initExpectedPage(ActionSettingsWizardPage.class);
    }

    @Override
    public <T extends SalesAppBasePage> T navigateToNextTab() {
        super.navigateToNextTab();
        return (T) initExpectedPage(CustomButtonTab.class);
    }
}
