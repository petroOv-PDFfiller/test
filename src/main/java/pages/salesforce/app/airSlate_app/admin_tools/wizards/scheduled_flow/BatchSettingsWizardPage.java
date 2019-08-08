package pages.salesforce.app.airSlate_app.admin_tools.wizards.scheduled_flow;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import pages.salesforce.app.SalesAppBasePage;
import pages.salesforce.app.airSlate_app.admin_tools.tabs.ScheduledFlowsTab;
import pages.salesforce.app.airSlate_app.admin_tools.wizards.WizardPage;

import static core.check.Check.checkTrue;

public class BatchSettingsWizardPage extends WizardPage {

    public BatchSettingsWizardPage(WebDriver driver) {
        super(driver);
        title = By.xpath("//*[contains(@class, 'title__') and text()='Batch settings']");
    }

    @Override
    public void isOpened() {
        skipLoader();
        checkTrue(isElementPresentAndDisplayed(title, 20), "Batch settings page title is not displayed");
    }

    @Override
    public <T extends SalesAppBasePage> T navigateToPreviousTab() {
        super.navigateToPreviousTab();
        return (T) initExpectedPage(FrequencyWizardPage.class);
    }

    @Override
    public <T extends SalesAppBasePage> T navigateToNextTab() {
        super.navigateToNextTab();
        return (T) initExpectedPage(ScheduledFlowsTab.class);
    }

    @Step
    public BatchSettingsWizardPage enterSoqlQuery(String query) {
        By soqlOption = By.xpath("//*[contains(@class, 'radioLabel_') and text() = 'SOQL Query']");
        By queryInput = By.name("query");

        checkIsElementDisplayed(soqlOption, 3, "Soql option");
        click(soqlOption);
        checkIsElementDisplayed(queryInput, 2, "SOQL query input");
        type(queryInput, query);
        return this;
    }
}