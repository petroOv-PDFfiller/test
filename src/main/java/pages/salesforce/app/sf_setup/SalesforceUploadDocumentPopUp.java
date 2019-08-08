package pages.salesforce.app.sf_setup;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import pages.salesforce.app.SalesAppBasePage;
import pages.salesforce.app.sf_objects.SalesforceBasePopUp;

import static core.check.Check.checkEquals;
import static core.check.Check.checkTrue;

public class SalesforceUploadDocumentPopUp extends SalesforceBasePopUp {

    public SalesforceUploadDocumentPopUp(WebDriver driver) {
        super(driver);
        this.popUpBody = By.xpath("//*[@class='DESKTOP uiModal open active']");
    }

    @Override
    public void isOpened() {
        By modalHeader = By.xpath("//*[@class='DESKTOP uiModal open active']//h2[@class= 'title slds-text-heading--medium slds-hyphenate']");

        switchToDefaultContent();
        checkTrue(isElementPresentAndDisplayed(popUpBody, 15), "Upload document to salesforce popup is not displayed");
        checkEquals(getText(modalHeader), "Upload Files", "Wrong modal popup opened");
    }

    @Step
    public <T extends SalesAppBasePage> T clickOnDoneButton(Class<T> expectedPage) {
        By btnDone = By.xpath("//*[@class='DESKTOP uiModal open active']//button[.='Done']");

        checkTrue(isElementPresentAndDisplayed(btnDone, 5), "Done button is not displayed");
        checkTrue(isElementEnabled(btnDone, 15), "Done button is not displayed");
        clickJS(btnDone);
        checkTrue(isElementNotDisplayed(popUpBody, 10), "PopUp still displayed");
        SalesAppBasePage resultPage = PageFactory.initElements(driver, expectedPage);
        resultPage.isOpened();
        return (T) resultPage;
    }
}
