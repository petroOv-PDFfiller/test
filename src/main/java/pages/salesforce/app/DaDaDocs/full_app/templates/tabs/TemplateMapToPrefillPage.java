package pages.salesforce.app.DaDaDocs.full_app.templates.tabs;

import io.qameta.allure.Step;
import org.openqa.selenium.WebDriver;
import pages.salesforce.app.SalesAppBasePage;
import utils.Logger;

import static core.check.Check.checkTrue;

public class TemplateMapToPrefillPage extends TemplateMappingPage {

    public TemplateMapToPrefillPage(WebDriver driver) {
        super(driver);
        descriptionText = "To prefill fields with the Salesforce data, please select the object and specify the " +
                "relationship between Salesforce fields and document fields to be filled";
        headingText = "Prefill fields with Salesforce data";
        pageName = "Map to prefill";
    }

    @Override
    @Step
    public <T extends SalesAppBasePage> T navigateToPreviousTab() {
        Logger.info("Navigate to previous tab");
        checkTrue(isElementPresentAndDisplayed(btnPreviousTab, 5), "Previous tab button is not displayed");
        click(btnPreviousTab);
        TemplateAddFieldsPage templateAddFieldsPage = new TemplateAddFieldsPage(driver);
        templateAddFieldsPage.isOpened();
        return (T) templateAddFieldsPage;
    }

    @Override
    @Step
    public <T extends SalesAppBasePage> T navigateToNextTab() {
        Logger.info("Navigate to next tab");
        checkTrue(isElementPresentAndDisplayed(btnNextTab, 5), "Next tab button is not displayed");
        click(btnNextTab);
        TemplateMapToUpdatePage templateMapToUpdatePage = new TemplateMapToUpdatePage(driver);
        templateMapToUpdatePage.isOpened();
        return (T) templateMapToUpdatePage;
    }
}
