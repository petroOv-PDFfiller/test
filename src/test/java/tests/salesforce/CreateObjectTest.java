package tests.salesforce;

import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import listeners.WebTestListener;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import pages.salesforce.app.SalesAppBasePage;
import pages.salesforce.app.sf_setup.SetupSalesforcePage;
import pages.salesforce.app.sf_setup.object_manager.EditCustomObjectPage;
import pages.salesforce.app.sf_setup.object_manager.NewCustomObjectPage;
import pages.salesforce.app.sf_setup.object_manager.ObjectManagerPage;
import pages.salesforce.enums.SalesforceFields;

import java.io.IOException;
import java.net.URISyntaxException;

@Feature("Create new custom object")
@Listeners({WebTestListener.class})
public class CreateObjectTest extends SalesforceBaseTest {
    private WebDriver driver;
    private SalesAppBasePage salesAppBasePage;

    @BeforeTest
    public void setUp() throws IOException, URISyntaxException {
        driver = getDriver();
        salesAppBasePage = loginWithDefaultCredentials();
    }

    @Story("Create structure for demo test")
    @Test
    public void createNewObject() {
        SetupSalesforcePage setupSalesforcePage = salesAppBasePage.openSetupPage();
        ObjectManagerPage objectManagerPage = setupSalesforcePage.openObjectManagerPage();
        NewCustomObjectPage newCustomObjectPage = objectManagerPage.createObjectPage();
        newCustomObjectPage.createNewObject("Corporate order", "Corporate orders", "Order_Product");
        EditCustomObjectPage editCustomObjectPage = new EditCustomObjectPage(driver);
        editCustomObjectPage.createNewFieldWithLength(SalesforceFields.TEXT, "Billing City", "", "150", false);
        editCustomObjectPage.createNewFieldWithLength(SalesforceFields.TEXT, "Billing Country", "", "150", false);
        editCustomObjectPage.createNewFieldWithLength(SalesforceFields.TEXT, "Billing State/Province", "", "150", false);
        editCustomObjectPage.createNewFieldWithLength(SalesforceFields.TEXT_AREA, "Description", "", "", false);
        editCustomObjectPage.createNewFieldWithLength(SalesforceFields.NUMBER, "Quantity", "", "", false);
        editCustomObjectPage.createNewFieldWithRelation(SalesforceFields.LOOKUP_RELATIONSHIP, "Contact", "Contact", false);

        objectManagerPage.openListOfObjects();
        editCustomObjectPage.openEditObjectPage("Opportunity");
        editCustomObjectPage.createNewFieldWithRelation(SalesforceFields.LOOKUP_RELATIONSHIP, "Primary Contact", "Contact", false);
        editCustomObjectPage.createNewCurrencyField("Maintenance", "Amount_1", "16", "2", false);
        editCustomObjectPage.createNewCurrencyField("Professional Services", "Amount_2", "16", "2", false);
        editCustomObjectPage.createNewFieldWithLength(SalesforceFields.TEXT_AREA_RICH, "Project Scope", "Rich_Text", "", false);
        editCustomObjectPage.createNewFormulaField(SalesforceFields.CURRENCY, "Total Amount", "Total_Amount", "Amount + Amount_1__c + Amount_2__c", "", "2", false);
    }
}
