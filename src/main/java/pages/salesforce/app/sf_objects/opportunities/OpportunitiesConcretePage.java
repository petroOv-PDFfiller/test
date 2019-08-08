package pages.salesforce.app.sf_objects.opportunities;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import pages.salesforce.app.DaDaDocs.CustomButtonS2SPage;
import pages.salesforce.app.DaDaDocs.editor.DaDaDocsEditor;
import pages.salesforce.app.sf_objects.ConcreteRecordPage;
import utils.Logger;

import static core.check.Check.checkTrue;

public class OpportunitiesConcretePage extends ConcreteRecordPage {

    protected By opportunity = By.xpath("//*[text()='Opportunity']");

    public OpportunitiesConcretePage(WebDriver driver) {
        super(driver);
    }

    @Override
    public void isOpened() {
        driver.switchTo().defaultContent();
        waitUntilPageLoaded();
        waitForSalesforceLoading();
        checkTrue(isElementPresent(detailsTab, 60), "Opportunity concrete page is not opened");
        checkTrue(isElementPresent(opportunity, 5), "Opportunity concrete page  is not opened");
    }

    //situation where in moment that page must be fully loaded we are redirecting to editor
    @Step
    public DaDaDocsEditor clickOrderFormButtonAndRedirectToEditor(String buttonName) {
        By orderFormButton = By.xpath("//div[text()='" + buttonName + "']");
        checkTrue(isElementPresentAndDisplayed(orderFormButton, 10), "Order form button is not presented");
        click(orderFormButton);
        CustomButtonS2SPage orderFormPage = new CustomButtonS2SPage(driver);
        orderFormPage.isOpenedBeforeRedirect();
        DaDaDocsEditor daDaDocsEditor = new DaDaDocsEditor(driver);
        daDaDocsEditor.isOpened();
        return daDaDocsEditor;
    }

    @Step
    public ManageContactRolesPopUp openContactRolesManager() {
        By contactRolesActions = By.xpath("//div[contains(@class , 'oneContent active ')]//span[@title='Contact Roles']/ancestor::article//a[@role='button']");
        By contactRolesImg = By.xpath("//div[contains(@class , 'oneContent active ')]//span[@title='Contact Roles']/ancestor::article//a[@role='button']");
        By contactRoles = By.xpath("//div[contains(@class , 'oneContent active ')]//span[@title='Contact Roles']");
        By btnManageContactRoles = By.xpath("//a[@role='menuitem' and @title = 'Manage Contact Roles']");

        Logger.info("Open contact roles manager");
        checkTrue(isElementPresentAndDisplayed(contactRoles, 40), "Contqact roles tab is not displayed");
        scrollTo(contactRoles);
        checkTrue(isElementPresentAndDisplayed(contactRolesImg, 40), "Contact roles img is not displayed");
        checkTrue(isElementPresentAndDisplayed(contactRolesActions, 40), "Contact roles action dropdown is not displayed");
        clickJS(contactRolesActions);
        checkTrue(isElementPresentAndDisplayed(btnManageContactRoles, 10), "Manage contact roles button is not displayed");
        clickJS(btnManageContactRoles);
        ManageContactRolesPopUp contactRolesPopUp = new ManageContactRolesPopUp(driver);
        contactRolesPopUp.isOpened();
        return contactRolesPopUp;
    }

    @Step
    public OpportunitiesConcretePage setStage(String stageName) {
        By stageTab = By.xpath("//a[@title='" + stageName + "']");
        By btnSetStage = By.xpath("//button[contains(@class,'stepAction') and .='Mark as Current Stage']");
        By btnMarkComplete = By.xpath("//button[contains(@class,'stepAction')]//span[ .='Mark Stage as Complete']");

        Logger.info("Set opportunity stage: " + stageName);
        checkTrue(isElementPresentAndDisplayed(stageTab, 15), "Stage " + stageName + " is not displayed");
        click(stageTab);
        checkTrue(isElementPresentAndDisplayed(btnSetStage, 15), "Set current stage btn is not displayed");
        click(btnSetStage);
        checkTrue(isElementPresentAndDisplayed(btnMarkComplete, 10), "Stage is not set");
        isOpened();
        return this;
    }
}
