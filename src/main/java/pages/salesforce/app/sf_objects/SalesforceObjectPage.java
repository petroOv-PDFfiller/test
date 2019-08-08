package pages.salesforce.app.sf_objects;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import pages.salesforce.app.SalesAppBasePage;
import pages.salesforce.enums.SalesTab;
import utils.Logger;

import java.util.concurrent.TimeUnit;

import static core.check.Check.checkFail;
import static core.check.Check.checkTrue;
import static org.awaitility.Awaitility.await;

public abstract class SalesforceObjectPage extends SalesAppBasePage {

    public String selectedObject = "";
    protected String pageName = "";
    private By objectsList = By.xpath("//table[contains(@class, 'forceRecordLayout ')]//tr");
    private By dropdownOneActions = By.xpath("//*[@data-aura-class='oneActionsDropDown']//a");

    public SalesforceObjectPage(WebDriver driver) {
        super(driver);
    }

    @Override
    public void isOpened() {
        switchToDefaultContent();
        checkTrue(waitUntilPageLoaded(), pageName + " page is not loaded");
        waitForSalesforceLoading();
        By objectIcon = By.xpath("//img[@title='" + pageName + "']");
        checkTrue(isElementPresent(objectIcon, 60), "Sales app page is not opened");
        checkTrue(isElementNotDisplayed(salesPageModalLoader, 10), "Sales app modal loader is still displayed");
    }

    @Step
    private void showAllRecords() {
        By btnSelectView = By.xpath("//a[@title ='Select List View']");
        By btnSelectAllRecords = By.xpath("//a[@role='option' and contains(., 'All')]");

        Logger.info("Show all records");
        checkTrue(isElementPresentAndDisplayed(btnSelectView, 5), "Select view button is not present");
        click(btnSelectView);
        checkTrue(isElementPresentAndDisplayed(btnSelectAllRecords, 5), "Select all records button is not present");
        click(btnSelectAllRecords);
        checkTrue(isElementNotDisplayed(salesPageModalLoader, 10), "Sales app modal loader is still displayed");
        checkTrue(getNumberOfElements(objectsList) > 0, "Still no records are present");
    }

    @Step
    public <T extends ConcreteRecordPage> T openObjectByName(String objectName) {
        By singleObjectLink = getObjectLocator(objectName);

        ConcreteRecordPage concretePage;
        checkTrue(isElementDisplayed(singleObjectLink, 5), "Single " + pageName + " link " + objectName + " is not presented");
        click(singleObjectLink);
        selectedObject = objectName;
        concretePage = PageFactory.initElements(driver, selectObject());
        concretePage.isOpened();
        return (T) concretePage;
    }

    @Step("Open Object by number: {0}")
    public <T extends ConcreteRecordPage> T openObjectByNumber(int number) {
        By objectName = By.xpath("(//*[@class='slds-truncate outputLookupLink slds-truncate forceOutputLookup'])[" + number + "]");

        ConcreteRecordPage concretePage = null;
        checkTrue(isElementPresent(objectName, 4), pageName + "object number " + number + " is not present");
        selectedObject = getText(objectName).replaceAll("<.*?>", "");
        Logger.info("Object (" + selectedObject + ") was selected");
        click(objectName);
        concretePage = PageFactory.initElements(driver, selectObject());
        concretePage.isOpened();
        return (T) concretePage;
    }

    private <T extends Object> T selectObject() {
        for (SalesTab object : SalesTab.values()) {
            if (object.getNameNewLayout().equals(pageName)) {
                return object.getConcretePage();
            }
        }
        return null;
    }

    public boolean isObjectPresent(String objectName) {
        By singleObjectLink = getObjectLocator(objectName);
        return isElementPresentAndDisplayed(singleObjectLink, 5);
    }

    public By getObjectLocator(String objectName) {
        return By.xpath("//a[@title='" + objectName + "']");
    }

    @Step("Delete Object by name")
    public void deleteObjectByName(String objectName) {
        By btnObjectEditActions = By.xpath("//a[@title='" + objectName + "']/ancestor::tr//a[@role='button']");
        By btnDeleteObject = By.xpath("//body/div/div[@role='menu']//a[@title='Delete']");
        By btnConfirmDeleteObject = By.xpath("//button[contains(@class, 'forceActionButton') and (@title='Delete')]");
        checkTrue(isElementPresentAndDisplayed(btnObjectEditActions, 10), "Single Object link " + objectName + " is not presented");
        click(btnObjectEditActions);
        checkTrue(isElementPresentAndDisplayed(btnDeleteObject, 3), "Menu in`t displayed");
        click(btnDeleteObject);
        checkTrue(isElementPresentAndDisplayed(btnConfirmDeleteObject, 3), "Confirm Menu in`t displayed");
        click(btnConfirmDeleteObject);
        checkTrue(isElementNotDisplayed(btnConfirmDeleteObject, 3), "Confirm Menu in`t displayed");
        isOpened();
    }

    @Step
    public SalesforceObjectPage selectRecords(int count) {
        for (int i = 1; i <= count; i++) {
            By btnCheckbox = By.xpath("(//input[@type='checkbox'])[" + (i + 1) + "]/parent::label");
            scrollTo(btnCheckbox);
            checkIsElementDisplayed(btnCheckbox, 4, i + " element");
            click(btnCheckbox);
        }
        isOpened();
        return this;
    }

    @Step
    public <T extends SalesforceObjectPage> T selectListView(String listViewName) {
        By btnOpenSelector = By.xpath("//a[@title='Select List View']");
        By listView = By.xpath("//*[contains(@id, 'virtualAutocompleteListbox_')]//a/span[. ='" + listViewName + "']");
        By pinned = By.xpath("//*[@data-key='pinned']");
        By btnPin = By.xpath("//force-list-view-manager-pin-button");

        checkIsElementDisplayed(btnOpenSelector, 4, "List view selector button");
        click(btnOpenSelector);
        checkIsElementDisplayed(listView, 10, listViewName + " list view");
        clickJS(listView);
        isOpened();
        if (!isElementPresent(pinned)) {
            checkIsElementDisplayed(btnPin, 4, "Pin button");
            click(btnPin);
        }
        isOpened();
        return (T) this;
    }

    @Step
    public void waitForCustomButton(String buttonLabel, int seconds) {
        try {
            await().atMost(seconds, TimeUnit.SECONDS)
                    .until(() -> {
                        refreshPage();
                        isOpened();
                        return isCustomButtonAdded(buttonLabel);
                    });
        } catch (org.awaitility.core.ConditionTimeoutException e) {
            checkFail(buttonLabel + " button is not created");
        }
    }

    private boolean isCustomButtonAdded(String buttonLabel) {
        By button = By.xpath("//*[@title = '" + buttonLabel + "']");

        if (isElementPresent(button)) {
            return true;
        } else {
            if (isElementPresent(dropdownOneActions)) {
                click(dropdownOneActions);
            }
        }
        return isElementPresent(button);
    }

    @Step
    public <T extends SalesAppBasePage> T clickOnCustomButton(String buttonLabel, Class<T> expectedPage) {
        By button = By.xpath("//*[@title = '" + buttonLabel + "']");

        if (!isElementDisplayed(button, 4)) {
            checkIsElementDisplayed(dropdownOneActions, 5, "More actions button");
            click(dropdownOneActions);
        }
        clickJS(button);
        return initExpectedPage(expectedPage);
    }
}
