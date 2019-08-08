package pages.salesforce.app.sf_setup;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import pages.salesforce.SalesforceBasePage;
import pages.salesforce.app.sf_setup.custom_object.SchemaBuilderPage;
import pages.salesforce.app.sf_setup.object_manager.ObjectManagerPage;
import pages.salesforce.app.sf_setup.process_builder.ProcessBuilderPage;

import static core.check.Check.checkTrue;
import static data.salesforce.SalesforceTestData.SalesforceSetupOptions.*;

public class SetupSalesforcePage extends SalesforceBasePage {
    private By setupTitle = By.xpath("//span[@title='Setup']");
    private By quickFind = By.xpath("//input[@type='search' and contains(@class, 'filter-box')]");
    private By objectManager = By.cssSelector("[title='Object Manager']");

    public SetupSalesforcePage(WebDriver driver) {
        super(driver);
    }

    @Override
    public void isOpened() {
        driver.switchTo().defaultContent();
        checkTrue(waitUntilPageLoaded(), "SetupSalesForcePage is not loaded");
        waitForSalesforceLoading();
        checkTrue(isElementPresent(setupTitle, 60), "Setup app page is not opened");
    }

    @Step
    public InstalledPackagesPage openInstalledPackages() {
        By installedPackagesApp = By.xpath("//div[@title='" + INSTALLED_PACKAGES.getName() + "']");
        checkTrue(isElementPresentAndDisplayed(quickFind, 10), "Quick find is not displayed");
        hoverOverAndClick(quickFind);
        type(quickFind, INSTALLED_PACKAGES.getName());
        checkTrue(isElementPresentAndDisplayed(installedPackagesApp, 5), "Installed packages tab is not displayed");
        click(installedPackagesApp);
        InstalledPackagesPage installedPackagesPage = new InstalledPackagesPage(driver);
        installedPackagesPage.isOpened();
        return installedPackagesPage;
    }

    @Step
    public SchemaBuilderPage openSchemaBuilder() {
        By schemaBuildersApp = By.xpath("//div[@title='" + SCHEMA_BUILDER.getName() + "']");
        checkTrue(isElementPresentAndDisplayed(quickFind, 5), "Quick find is not displayed");
        hoverOverAndClick(quickFind);
        type(quickFind, SCHEMA_BUILDER.getName());
        checkTrue(isElementPresentAndDisplayed(schemaBuildersApp, 5), "Schema builder is not displayed");
        click(schemaBuildersApp);
        SchemaBuilderPage schemaBuilderPage = new SchemaBuilderPage(driver);
        schemaBuilderPage.isOpened();
        return schemaBuilderPage;
    }

    @Step
    public TabsSetupPage openTabsSetup() {
        By tabsApp = By.xpath("//div[@title='" + TABS.getName() + "']");
        checkTrue(isElementPresentAndDisplayed(quickFind, 5), "Quick find is not displayed");
        hoverOverAndClick(quickFind);
        type(quickFind, TABS.getName());
        checkTrue(isElementPresentAndDisplayed(tabsApp, 5), "Tabs setup is not displayed");
        click(tabsApp);
        TabsSetupPage tabsSetupPage = new TabsSetupPage(driver);
        tabsSetupPage.isOpened();
        return tabsSetupPage;
    }

    @Step
    public ProcessBuilderPage openProcessBuilder() {
        By processBuilder = By.xpath("//div[@title='" + PROCESS_BUILDER.getName() + "']");
        checkTrue(isElementPresentAndDisplayed(quickFind, 5), "Quick find is not displayed");
        hoverOverAndClick(quickFind);
        type(quickFind, PROCESS_BUILDER.getName());
        checkTrue(isElementPresentAndDisplayed(processBuilder, 5), "Tabs setup is not displayed");
        click(processBuilder);
        ProcessBuilderPage processBuilderPage = new ProcessBuilderPage(driver);
        processBuilderPage.isOpened();
        return processBuilderPage;
    }

    @Step
    public ObjectManagerPage openObjectManagerPage() {
        checkTrue(isElementPresentAndDisplayed(objectManager, 5), "Object manager button not present");
        click(objectManager);
        ObjectManagerPage objectManagerPage = new ObjectManagerPage(driver);
        objectManagerPage.isOpened();
        return objectManagerPage;
    }
}
