package pages.salesforce.app.DaDaDocs.full_app.templates.tabs;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import pages.salesforce.app.DaDaDocs.full_app.templates.CreateTemplateWizardPage;
import pages.salesforce.app.DaDaDocs.full_app.templates.components.FooterNavigation;
import pages.salesforce.app.DaDaDocs.full_app.templates.popups.DiscardTemplateCreationPopUp;
import pages.salesforce.app.SalesAppBasePage;
import utils.Logger;

import static core.check.Check.checkEquals;
import static core.check.Check.checkTrue;

public class TemplateInfoPage extends CreateTemplateWizardPage implements FooterNavigation {

    private DiscardTemplateCreationPopUp discardTemplateCreationPopUp;

    public TemplateInfoPage(WebDriver driver) {
        super(driver);
        descriptionText = "Enter a name for your new template and add a description";
        headingText = "Template info";
        pageName = "Template info";
    }

    @Override
    public void isOpened() {
        skipMappingLoader();
        checkTrue(isElementPresentAndDisplayed(heading, 5), "Heading is not displayed");
        checkTexts();
        checkTrue(isElementPresentAndDisplayed(footer, 5), "Footer is not displayed");
    }

    @Step
    public TemplateInfoPage setTemplateName(String templateName) {
        By inputTemplateName = By.id("templateName");

        String expectedName;
        if (templateName.length() > 100) {
            expectedName = templateName.substring(0, 100);
        } else {
            expectedName = templateName;
        }
        checkTrue(isElementPresentAndDisplayed(inputTemplateName, 5), "Template name input field is not displayed");
        type(inputTemplateName, templateName);
        checkEquals(getAttribute(inputTemplateName, "value"), expectedName, "Wrong template name input value");

        isOpened();
        return this;
    }

    @Step
    public TemplateInfoPage setTemplateDescription(String templateDescription) {
        By taTemplateDescription = By.id("templateDescription");

        String expectedName;
        if (templateDescription.length() > 400) {
            expectedName = templateDescription.substring(0, 400);
        } else {
            expectedName = templateDescription;
        }
        checkTrue(isElementPresentAndDisplayed(taTemplateDescription, 5),
                "Template description area is not displayed");
        type(taTemplateDescription, templateDescription);
        checkEquals(getAttribute(taTemplateDescription, "textContent"), expectedName,
                "Wrong template description value");

        isOpened();
        return this;
    }

    @Override
    @Step
    public <T extends SalesAppBasePage> T navigateToPreviousTab() {
        Logger.info("Navigate to previous tab");
        checkTrue(isElementPresentAndDisplayed(btnPreviousTab, 5), "Previous tab button is not displayed");
        click(btnPreviousTab);
        discardTemplateCreationPopUp = new DiscardTemplateCreationPopUp(driver);
        discardTemplateCreationPopUp.isOpened();
        return (T) discardTemplateCreationPopUp;
    }

    @Override
    @Step
    public <T extends SalesAppBasePage> T navigateToNextTab() {
        Logger.info("Navigate to next tab");
        checkTrue(isElementPresentAndDisplayed(btnNextTab, 5), "Next tab button is not displayed");
        click(btnNextTab);
        TemplateAddFieldsPage templateAddFieldsPage = new TemplateAddFieldsPage(driver);
        templateAddFieldsPage.isOpened();
        return (T) templateAddFieldsPage;
    }
}
