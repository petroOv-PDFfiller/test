package pages.salesforce.app.DaDaDocs.full_app.templates.tabs.x_templates;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.Select;
import pages.salesforce.app.DaDaDocs.full_app.templates.popups.CancelTemplateCreationPopUp;
import pages.salesforce.app.DaDaDocs.full_app.templates.popups.DiscardTemplateCreationPopUp;
import pages.salesforce.app.DaDaDocs.full_app.templates.tabs.TemplateInfoPage;
import pages.salesforce.app.SalesAppBasePage;
import utils.Logger;

import static core.check.Check.checkEquals;
import static core.check.Check.checkTrue;

public class XTemplateInfoPage extends TemplateInfoPage {

    public XTemplateInfoPage(WebDriver driver) {
        super(driver);
        vMappingLoader = By.xpath("//*[contains(@class, 'loader__')]");
        heading = By.xpath("//*[contains(@class, 'title__')]");
        descriptionText = "Enter a name for your new template, select starting object and add a description";
        description = By.xpath("//*[contains(@class, 'text__')]/p");
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
    public XTemplateInfoPage setTemplateName(String templateName) {
        By inputTemplateName = By.name("templateName");

        String expectedName;
        if (templateName.length() > 80) {
            expectedName = templateName.substring(0, 80);
        } else {
            expectedName = templateName;
        }
        checkTrue(isElementPresentAndDisplayed(inputTemplateName, 5), "Template name input field is not displayed");
        type(inputTemplateName, templateName);
        checkEquals(getAttribute(inputTemplateName, "value"), expectedName, "Wrong template name input value");
        return this;
    }

    @Step
    public XTemplateInfoPage setTemplateDescription(String templateDescription) {
        By taTemplateDescription = By.name("templateDescription");

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
        return this;
    }

    public boolean isStartingObjectErrorDisplayed() {
        By error = By.xpath("//*[contains(@class, 'validationText__') and text()='Please select starting object']");
        return isElementDisplayed(error, 4);
    }

    public boolean isStartingObjectEditable() {
        By startingObject = By.id("startingObject");
        return isElementEnabled(startingObject);
    }

    @Step
    public CancelTemplateCreationPopUp cancelTemplateCreation() {
        Logger.info("Close wizard");
        checkTrue(isElementPresentAndDisplayed(btnPreviousDefaultTab, 5), "Previous tab button is not displayed");
        click(btnPreviousDefaultTab);
        CancelTemplateCreationPopUp cancelTemplateCreationPopUp = new CancelTemplateCreationPopUp(driver);
        cancelTemplateCreationPopUp.isOpened();
        return cancelTemplateCreationPopUp;
    }

    @Override
    @Step
    public <T extends SalesAppBasePage> T navigateToPreviousTab() {
        Logger.info("Navigate to previous tab");
        checkTrue(isElementPresentAndDisplayed(btnPreviousDefaultTab, 5), "Previous tab button is not displayed");
        click(btnPreviousDefaultTab);
        DiscardTemplateCreationPopUp discardTemplateCreationPopUp = new DiscardTemplateCreationPopUp(driver);
        discardTemplateCreationPopUp.isOpened();
        return (T) discardTemplateCreationPopUp;
    }

    @Override
    @Step
    public <T extends SalesAppBasePage> T navigateToNextTab() {
        Logger.info("Navigate to next tab");
        checkTrue(isElementPresentAndDisplayed(btnNextTab, 5), "Next tab button is not displayed");
        click(btnNextTab);
        RelatedParentPage relatedParentPage = new RelatedParentPage(driver);
        relatedParentPage.isOpened();
        return (T) relatedParentPage;
    }

    @Step("Select starting object: {0}")
    public XTemplateInfoPage selectStartingObject(String objectName) {
        By selectStartingObject = By.id("startingObject");

        Logger.info("Select starting object");
        checkTrue(isElementDisplayed(selectStartingObject, 2), "Starting object select is not displayed");
        Select startingObject = new Select(driver.findElement(selectStartingObject));
        startingObject.selectByValue(objectName);
        checkEquals(startingObject.getFirstSelectedOption().getAttribute("value"),
                objectName, "Starting object value is nt set");
        return this;
    }
}
