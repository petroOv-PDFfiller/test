package pages.salesforce.app.DaDaDocs.full_app.templates.tabs.x_templates;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import pages.salesforce.app.DaDaDocs.full_app.templates.CreateTemplateWizardPage;
import pages.salesforce.app.DaDaDocs.full_app.templates.components.FooterNavigation;
import pages.salesforce.app.SalesAppBasePage;
import utils.Logger;
import utils.SystemMan;

import static core.check.Check.checkTrue;

public class CopyTagsPage extends CreateTemplateWizardPage implements FooterNavigation {

    public CopyTagsPage(WebDriver driver) {
        super(driver);
        vMappingLoader = By.xpath("//*[contains(@class, 'loader__')]");
        heading = By.xpath("//*[contains(@class, 'title__')]");
        descriptionText = "1) Mark all tags you'd like to use <br>2) Click \"Copy used tags\", " +
                "or copy tags one by one<br>3) Paste tags from clipboard into your template file <br>";
        description = By.xpath("//*[contains(@class, 'text__')]/p");
        headingText = "Copy and paste tags into your template";
        pageName = "Copy tags";
    }

    @Override
    public void isOpened() {
        skipMappingLoader();
        checkTrue(isElementPresentAndDisplayed(heading, 5), "Heading is not displayed");
        checkTexts();
        checkTrue(isElementPresentAndDisplayed(footer, 5), "Footer is not displayed");
    }

    @Override
    @Step
    public <T extends SalesAppBasePage> T navigateToPreviousTab() {
        Logger.info("Navigate to previous tab");
        checkTrue(isElementPresentAndDisplayed(btnPreviousDefaultTab, 5), "Previous tab button is not displayed");
        click(btnPreviousDefaultTab);
        RelatedChildPage relatedChildPage = new RelatedChildPage(driver);
        relatedChildPage.isOpened();
        return (T) relatedChildPage;
    }

    @Override
    @Step
    public <T extends SalesAppBasePage> T navigateToNextTab() {
        Logger.info("Navigate to next tab");
        checkTrue(isElementPresentAndDisplayed(btnNextTab, 5), "Next tab button is not displayed");
        click(btnNextTab);
        TemplateUploadPage templateUploadPage = new TemplateUploadPage(driver);
        templateUploadPage.isOpened();
        return (T) templateUploadPage;
    }

    @Step
    public String copySelectedTags() {
        By btnCopySelectedTags = By.xpath("//*[@aria-label = 'Copied to clipboard']");

        checkTrue(isElementPresent(btnCopySelectedTags, 5), "Copy selected tags button is not present");
        scrollTo(description);
        click(btnCopySelectedTags);
        return SystemMan.getClipboardValue();
    }

    @Step
    public void selectTag(String tagName) {
        By checkboxSelectTag = By.xpath("//*[contains(@class, 'fieldCell__') and text()='" + tagName + "']/parent::div//label");
        By selectedTag = By.xpath("//*[contains(@class, 'fieldCell__') and text()='" + tagName + "']/parent::div");

        checkTrue(isElementPresent(checkboxSelectTag, 5), tagName + " tag is not present");
        scrollIntoView(selectedTag, true);
        scrollPage(-100);
        click(checkboxSelectTag);
        checkTrue(isElementContainsStringInAttribute(selectedTag, "class", "tableRowSelected__", 5),
                tagName + "tag is not marked as selected");
    }

    @Step
    public CopyTagsPage selectObjeсtFields(String objectName) {
        String modifiedObjectName = objectName.replace(" ", "_");
        By selectObject = By.xpath("//*[contains(@class, 'selectObject__')]");
        By objectRow = By.xpath("//*[@data-test='listItem' and contains(.,'" + modifiedObjectName + "')]");

        checkTrue(isElementDisplayed(selectObject, 5), "object Select is not displayed");
        click(selectObject);
        checkTrue(isElementPresentAndDisplayed(objectRow, 6), objectName + " field row is not displayed");
        click(objectRow);
        checkTrue(getText(selectObject).contains(modifiedObjectName), "Object is not selected");
        isOpened();
        return this;
    }
}
