package pages.salesforce.app.DaDaDocs.full_app.templates.tabs.x_templates;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import pages.salesforce.app.DaDaDocs.full_app.templates.entities.PermissionGroup;
import pages.salesforce.app.DaDaDocs.full_app.templates.tabs.TemplateAccessSettingsPage;
import pages.salesforce.app.SalesAppBasePage;
import utils.Logger;

import java.util.List;

import static core.check.Check.checkTrue;

public class XTemplateAccessSettingsPage extends TemplateAccessSettingsPage {

    public List<PermissionGroup> permissionGroups;

    public XTemplateAccessSettingsPage(WebDriver driver) {
        super(driver);
        vMappingLoader = By.xpath("//*[contains(@class, 'loader__')]");
        heading = By.xpath("//*[contains(@class, 'title__')]");
        description = By.xpath("//*[contains(@class, 'text__')]/p");
    }

    @Override
    @Step
    public <T extends SalesAppBasePage> T navigateToPreviousTab() {
        Logger.info("Navigate to previous tab");
        checkTrue(isElementPresentAndDisplayed(btnPreviousTab, 5), "Previous tab button is not displayed");
        click(btnPreviousTab);
        TemplateUploadPage templateUploadPage = new TemplateUploadPage(driver);
        templateUploadPage.isOpened();
        return (T) templateUploadPage;
    }
}
