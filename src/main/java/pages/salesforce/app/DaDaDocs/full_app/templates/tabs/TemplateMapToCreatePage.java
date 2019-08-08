package pages.salesforce.app.DaDaDocs.full_app.templates.tabs;

import io.qameta.allure.Step;
import org.openqa.selenium.WebDriver;
import pages.salesforce.app.SalesAppBasePage;
import utils.Logger;

import static core.check.Check.checkTrue;

public class TemplateMapToCreatePage extends TemplateMappingPage {

    public TemplateMapToCreatePage(WebDriver driver) {
        super(driver);
        descriptionText = "To create new records using data from a filled document, please select the object and specify the relationship between Salesforce fields and document fields to be filled";
        headingText = "Create records using data from a filled document";
        pageName = "Map to create";
    }

    @Override
    @Step
    public <T extends SalesAppBasePage> T navigateToPreviousTab() {
        Logger.info("Navigate to previous tab");
        checkTrue(isElementPresentAndDisplayed(btnPreviousTab, 5), "Previous tab button is not displayed");
        click(btnPreviousTab);
        TemplateMapToUpdatePage templateMapToUpdatePage = new TemplateMapToUpdatePage(driver);
        templateMapToUpdatePage.isOpened();
        return (T) templateMapToUpdatePage;
    }

    @Override
    @Step
    public <T extends SalesAppBasePage> T navigateToNextTab() {
        Logger.info("Navigate to next tab");
        checkTrue(isElementPresentAndDisplayed(btnNextTab, 5), "Next tab button is not displayed");
        click(btnNextTab);
        TemplateAccessSettingsPage templateAccessSettingsPage = new TemplateAccessSettingsPage(driver);
        templateAccessSettingsPage.isOpened();
        return (T) templateAccessSettingsPage;
    }
}
