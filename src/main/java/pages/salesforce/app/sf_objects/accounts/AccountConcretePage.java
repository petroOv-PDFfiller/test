package pages.salesforce.app.sf_objects.accounts;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import pages.salesforce.app.DaDaDocs.full_app.DaDaDocsFullApp;
import pages.salesforce.app.sf_objects.ConcreteRecordPage;
import pages.salesforce.app.sf_objects.LeftTabs;

import static core.check.Check.checkTrue;

public class AccountConcretePage extends ConcreteRecordPage {

    private By btnUseDaDaDocs = By.xpath("//a[@title='Use DaDaDocs']");
    private By tableContent = By.xpath("(//*[@class='tabset slds-tabs_card uiTabset--base uiTabset--default uiTabset--dense uiTabset flexipageTabset'])[1]");

    public AccountConcretePage(WebDriver driver) {
        super(driver);
    }

    @Override
    public void isOpened() {
        checkTrue(isElementPresent(tableContent, 16), "tableContent is not present");
    }


    @Step("Click Use DaDaDocs")
    public <T extends DaDaDocsFullApp> T clickUseDaDaDocs() {
        openLeftTab(LeftTabs.DETAILS);
        By arrowDropDown = By.xpath("//*[@class='slds-grid slds-grid--vertical-align-center slds-grid--align-center sldsButtonHeightFix']");
        checkTrue(isElementPresent(arrowDropDown, 4), "arrowDropDown is not present");
        hoverOverAndClick(arrowDropDown);
        checkTrue(isElementPresent(btnUseDaDaDocs, 4), "btnUseDaDaDocs is not present");
        hoverOverAndClick(btnUseDaDaDocs);
        DaDaDocsFullApp daDaDocsFullApp = new DaDaDocsFullApp(driver);
        daDaDocsFullApp.isOpened();

        return daDaDocsFullApp.getActiveTab();
    }
}