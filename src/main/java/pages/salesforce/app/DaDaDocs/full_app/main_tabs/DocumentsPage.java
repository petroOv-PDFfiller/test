package pages.salesforce.app.DaDaDocs.full_app.main_tabs;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import pages.salesforce.SalesforceBasePage;
import pages.salesforce.app.DaDaDocs.full_app.DaDaDocsFullApp;
import pages.salesforce.enums.SaleforceMyDocsTab;

import static core.check.Check.checkTrue;

public class DocumentsPage extends DaDaDocsFullApp {

    public DocumentsPage(WebDriver driver) {
        super(driver);
    }

    @Override
    public void isOpened() {
        By tab = By.xpath("//*[@id='" + SaleforceMyDocsTab.DOCUMENTS.getName() + "']/ancestor-or-self::li");
        skipTabLoader();
        if (!isElementPresent(tab)) {
            driver.switchTo().defaultContent();
            switchToFrame(iframe, 4);
        }
        checkTrue(isElementPresentAndDisplayed(tab, 12), "Document tab is not present");
        skipTabLoader();
        checkTrue(isElementContainsStringInAttribute(tab, "class", "active", 10), "Document tab is not active");
    }

    @Override
    public <T extends SalesforceBasePage> T selectAnAction(String actionName) {
        // TODO refactoring
        return super.selectAnAction(actionName);
    }
}
