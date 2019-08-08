package pages.salesforce.app.sf_setup.object_manager;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import pages.salesforce.app.SalesAppBasePage;
import pages.salesforce.app.sf_setup.SetupSalesforcePage;

import static core.check.Check.checkTrue;

public class ObjectManagerPage extends SalesAppBasePage {
    private By titleObjectManager = By.xpath("//*[@class='breadcrumbDetail uiOutputText' and contains(text(),'Object Manager')]");
    private By btnCreate = By.xpath("(//*[@title='Create Menu'])[2]");
    private By linkCreateCustomObject = By.xpath("//*[@title ='Custom Object']");

    public ObjectManagerPage(WebDriver driver) {
        super(driver);
    }

    @Override
    public void isOpened() {
        checkTrue(waitUntilPageLoaded(), "Object manager Page is not loaded");
        waitForSalesforceLoading();
        switchToDefaultContent();
        checkTrue(isElementPresent(titleObjectManager, 60), "Setup app page is not opened");
    }

    @Step("Open custom object create page.")
    public NewCustomObjectPage createObjectPage() {
        checkTrue(isElementPresentAndDisplayed(btnCreate, 5), "CreateButton not present on page");
        click(btnCreate);
        checkTrue((isElementPresentAndDisplayed(linkCreateCustomObject, 5)), "CreateCustomObject option not presented on page");
        click(linkCreateCustomObject);
        NewCustomObjectPage newCustomObjectPage = new NewCustomObjectPage(driver);
        newCustomObjectPage.isOpened();
        return newCustomObjectPage;
    }

    @Step("Open object manager page")
    public ObjectManagerPage openListOfObjects() {
        switchToDefaultContent();
        SetupSalesforcePage setupSalesforcePage = new SetupSalesforcePage(driver);
        ObjectManagerPage objectManagerPage = setupSalesforcePage.openObjectManagerPage();
        return objectManagerPage;
    }
}
