package pages.salesforce.app.DaDaDocs.full_app.templates.tabs.x_templates;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import pages.salesforce.app.DaDaDocs.full_app.templates.CreateTemplateWizardPage;
import pages.salesforce.app.DaDaDocs.full_app.templates.components.FooterNavigation;
import pages.salesforce.app.DaDaDocs.full_app.templates.popups.PreviewUploadedFilePopUp;
import pages.salesforce.app.SalesAppBasePage;
import utils.Logger;

import static core.check.Check.checkEquals;
import static core.check.Check.checkTrue;

public class TemplateUploadPage extends CreateTemplateWizardPage implements FooterNavigation {

    public TemplateUploadPage(WebDriver driver) {
        super(driver);
        vMappingLoader = By.xpath("//*[contains(@class, 'loader__')]");
        heading = By.xpath("//*[contains(@class, 'title__')]");
        descriptionText = "Upload your file with tags";
        description = By.xpath("//*[contains(@class, 'text__')]/p");
        pageName = "Template upload";
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
        CopyTagsPage copyTagsPage = new CopyTagsPage(driver);
        copyTagsPage.isOpened();
        return (T) copyTagsPage;
    }

    @Override
    @Step
    public <T extends SalesAppBasePage> T navigateToNextTab() {
        Logger.info("Navigate to next tab");
        checkTrue(isElementPresentAndDisplayed(btnNextTab, 5), "Next tab button is not displayed");
        click(btnNextTab);
        XTemplateAccessSettingsPage accessSettingsPage = new XTemplateAccessSettingsPage(driver);
        accessSettingsPage.isOpened();
        return (T) accessSettingsPage;
    }

    @Override
    public void checkTexts() {
        checkEquals(getText(description).trim(), descriptionText, "Wrong heading text for " + pageName + " page");
    }

    @Step
    public PreviewUploadedFilePopUp uploadTemplate(String pathToFile) {
        By inputUpload = By.id("upload");

        checkTrue(isElementPresent(inputUpload), "Upload input is not present");
        sendFileInput(inputUpload, pathToFile);
        skipMappingLoader();
        PreviewUploadedFilePopUp uploadedFilePopUp = new PreviewUploadedFilePopUp(driver);
        uploadedFilePopUp.isOpened();
        return uploadedFilePopUp;
    }
}
