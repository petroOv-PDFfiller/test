package pages.salesforce.app.sf_objects.opportunities;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import pages.salesforce.app.sf_objects.SalesforceBasePopUp;
import utils.Logger;

import static core.check.Check.checkTrue;

public class AddNewOpportunityPopUp extends SalesforceBasePopUp {

    private By modalContainer = By.xpath("//div[contains(@class , 'modal-container')]");
    private By modalHeader = By.xpath("//*[contains(@id , 'title') and text()='New Opportunity']");

    public AddNewOpportunityPopUp(WebDriver driver) {
        super(driver);
    }

    @Override
    public void isOpened() {
        checkTrue(isElementPresentAndDisplayed(modalContainer, 10), "AddNewOpportunityPopUp Modal is not opened");
        checkTrue(isElementPresentAndDisplayed(modalHeader, 10), "Wrong modal is opened");
    }

    @Step
    public AddNewOpportunityPopUp setOpportunityName(String name) {
        By opportunityName = By.xpath("//span[text()='Opportunity Name']/ancestor::div/input");

        Logger.info("Set new opportunity name: " + name);
        checkTrue(isElementPresentAndDisplayed(opportunityName, 5), "Field for opportunityName is not displayed");
        type(opportunityName, name);
        isOpened();
        return this;
    }

    @Step
    public AddNewOpportunityPopUp setTodayAsCloseDate() {
        By datepicker = By.xpath("//a[contains(@class, 'datePicker-openIcon ')]");
        By todayDay = By.xpath("//span[contains(@class, 'today ')]");

        Logger.info("Set new opportunity close day: today");
        checkTrue(isElementPresentAndDisplayed(datepicker, 5), "close date datepicker is not displayed");
        click(datepicker);
        checkTrue(isElementPresentAndDisplayed(todayDay, 5), "today is not dislayed");
        click(todayDay);
        checkTrue(isElementNotDisplayed(todayDay, 5), "today option is still displayed");
        isOpened();
        return this;
    }

    @Step
    public AddNewOpportunityPopUp setStage(String stageName) {
        By opportunityStageField = By.xpath("//*[contains(@class, 'label')]/*[text()='Stage']/ancestor::*[@class='slds-form-element__control']//a[@class='select']");
        By stage = By.xpath("//a[@title='" + stageName + "' and @role='menuitemradio']");

        Logger.info("Set new opportunity stage: " + stageName);
        checkTrue(isElementPresentAndDisplayed(opportunityStageField, 5), "Stage field is not displayed");
        click(opportunityStageField);
        checkTrue(isElementPresentAndDisplayed(stage, 5), "Stage " + stageName + " is not displayed");
        click(stage);
        isOpened();
        return this;
    }

    @Step
    public OpportunitiesConcretePage saveOpportunity() {
        By btnSave = By.xpath("//*[contains(@class, 'modal-footer')]//span[@dir='ltr' and .='Save']");

        Logger.info("Save new opportunity");
        checkTrue(isElementPresentAndDisplayed(btnSave, 5), "Save button is not displayed");
        click(btnSave);
        checkTrue(isElementNotDisplayed(btnSave, 5), "save button is still displayed");
        OpportunitiesConcretePage concretePage = new OpportunitiesConcretePage(driver);
        concretePage.isOpened();
        return concretePage;
    }
}
