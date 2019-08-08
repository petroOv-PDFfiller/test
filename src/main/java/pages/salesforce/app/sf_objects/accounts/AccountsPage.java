package pages.salesforce.app.sf_objects.accounts;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import pages.salesforce.app.sf_objects.SalesforceObjectPage;
import pages.salesforce.enums.SalesTab;

import java.util.List;

import static core.check.Check.checkTrue;

public class AccountsPage extends SalesforceObjectPage {

    public AccountsPage(WebDriver driver) {
        super(driver);
        pageName = SalesTab.ACCOUNTS.getNameNewLayout();
    }

    @Step("Open Account Name: {0}")
    public AccountConcretePage openAccountName(int number) {
        By accountName = By.xpath("(//*[@class='slds-truncate outputLookupLink slds-truncate forceOutputLookup'])[" + number + "]");
        checkTrue(isElementPresent(accountName, 4), "accountName " + number + " is not present");
        click(accountName);
        AccountConcretePage account = new AccountConcretePage(driver);
        account.isOpened();

        return account;
    }

    public void setTextAccounts() {
        By table = By.xpath("//*[@class='slds-form-element slds-m-bottom_xx-small slds-form-element_edit slds-grow slds-hint-parent override--slds-form-element']");
        By key = By.xpath("//*[@class='slds-form-element slds-m-bottom_xx-small slds-form-element_edit slds-grow slds-hint-parent override--slds-form-element']//*[@class='test-id__field-label slds-form-element__label']");
        By value = By.xpath("");
        List<WebElement> rows = driver.findElements(key);

        int allRows = driver.findElements(key).size();
        checkTrue(isElementPresent(key, 4), "Row name is not present in table DETAILS");
        checkTrue(isElementPresent(value, 4), "Row value is not present in table DETAILS");
        for (int i = 1; i <= allRows; i++) {
            key = By.xpath("(//*[@class='slds-form-element slds-m-bottom_xx-small slds-form-element_edit slds-grow slds-hint-parent override--slds-form-element']//*[@class='test-id__field-label slds-form-element__label'])[" + i + "]");
            key = By.xpath("(//*[@class='slds-form-element slds-m-bottom_xx-small slds-form-element_edit slds-grow slds-hint-parent override--slds-form-element']//*[@class='test-id__field-label slds-form-element__label'])[" + i + "]");
//            accountParameteres.put(getText())
        }

    }
}

