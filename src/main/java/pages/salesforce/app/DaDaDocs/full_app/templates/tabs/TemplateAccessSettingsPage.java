package pages.salesforce.app.DaDaDocs.full_app.templates.tabs;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import pages.salesforce.app.DaDaDocs.full_app.DaDaDocsFullApp;
import pages.salesforce.app.DaDaDocs.full_app.V3.DaDaDocsFullAppV3;
import pages.salesforce.app.DaDaDocs.full_app.templates.CreateTemplateWizardPage;
import pages.salesforce.app.DaDaDocs.full_app.templates.components.FooterNavigation;
import pages.salesforce.app.DaDaDocs.full_app.templates.entities.Permission;
import pages.salesforce.app.DaDaDocs.full_app.templates.entities.PermissionGroup;
import pages.salesforce.app.SalesAppBasePage;
import utils.Logger;

import java.util.ArrayList;
import java.util.List;

import static core.check.Check.checkEquals;
import static core.check.Check.checkTrue;

public class TemplateAccessSettingsPage extends CreateTemplateWizardPage implements FooterNavigation {

    public List<PermissionGroup> permissionGroups;

    public TemplateAccessSettingsPage(WebDriver driver) {
        super(driver);
        descriptionText = "Select users who will be able to create documents using this template";
        headingText = "Access settings";
        pageName = "Access settings";
    }

    @Override
    public void isOpened() {
        skipMappingLoader();
        checkTrue(isElementPresentAndDisplayed(heading, 5), "Heading is not displayed");
        checkTrue(isElementPresentAndDisplayed(footer, 5), "Footer is not displayed");
        checkTexts();
    }

    @Override
    @Step
    public <T extends SalesAppBasePage> T navigateToPreviousTab() {
        Logger.info("Navigate to previous tab");
        checkTrue(isElementPresentAndDisplayed(btnPreviousTab, 5), "Previous tab button is not displayed");
        click(btnPreviousTab);
        TemplateMapToCreatePage templateMapToCreatePage = new TemplateMapToCreatePage(driver);
        templateMapToCreatePage.isOpened();
        return (T) templateMapToCreatePage;
    }

    @Override
    @Step
    public DaDaDocsFullApp navigateToNextTab() {
        Logger.info("Navigate to next tab");
        checkTrue(isElementPresentAndDisplayed(btnNextTab, 5), "Next tab button is not displayed");
        skipMappingLoader();
        click(btnNextTab);
        skipMappingLoader();
        DaDaDocsFullApp daDaDocsFullApp = new DaDaDocsFullApp(driver);
        daDaDocsFullApp.isOpened();
        return daDaDocsFullApp;
    }

    @Step
    public List<PermissionGroup> initPermissionGroups() {
        By container = By.xpath("//div[contains(@class, 'container__')]");
        By groupTitle = By.xpath("//span[contains(@class, 'groupTitle__')]");
        By groupContainer = By.xpath("//div[contains(@class, 'groupContainer__')]");
        checkTrue(isElementPresentAndDisplayed(container, 10), "container is displayed");

        permissionGroups = new ArrayList<>();
        for (int i = 0; i < getNumberOfElements(groupTitle); i++) {
            List<Permission> permissions = new ArrayList<>();
            String groupName = getText(driver.findElements(groupTitle).get(i));
            By settingLabel = By.xpath("(//div[contains(@class, 'groupContainer__')])[" + (i + 1) + "]//span[contains(@class, 'settingLabel__')]");
            By settingStatus = By.xpath("(//div[contains(@class, 'groupContainer__')])[" + (i + 1) + "]//input[contains(@class, 'checkbox__input__')]");
            checkEquals(getNumberOfElements(groupTitle), getNumberOfElements(groupContainer), "Wrong number of permission groups");
            checkEquals(getNumberOfElements(settingLabel), getNumberOfElements(settingStatus), "Wrong number of permissions");
            for (int j = 0; j < driver.findElements(settingLabel).size(); j++) {
                String permissionName = getText(driver.findElements(settingLabel).get(j));
                boolean permissionStatus = driver.findElements(settingStatus).get(j).isSelected();
                permissions.add(new Permission(permissionName, permissionStatus));
            }
            permissionGroups.add(new PermissionGroup(groupName.trim(), permissions));
        }
        Logger.info("Init permission groups");
        return permissionGroups;
    }

    @Step
    public DaDaDocsFullAppV3 navigateToNextTabV3() {
        Logger.info("Navigate to next tab");
        checkTrue(isElementPresentAndDisplayed(btnNextTab, 5), "Next tab button is not displayed");
        skipMappingLoader();
        click(btnNextTab);
        skipMappingLoader();
        DaDaDocsFullAppV3 daDaDocsFullApp = new DaDaDocsFullAppV3(driver);
        daDaDocsFullApp.isOpened();
        return daDaDocsFullApp;
    }
}
