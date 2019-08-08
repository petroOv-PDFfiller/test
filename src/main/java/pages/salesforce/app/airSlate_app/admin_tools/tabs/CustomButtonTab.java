package pages.salesforce.app.airSlate_app.admin_tools.tabs;

import core.check.SoftCheck;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import pages.salesforce.app.airSlate_app.admin_tools.ASAppAdminToolsPage;
import pages.salesforce.app.airSlate_app.admin_tools.entities.CustomButton;
import pages.salesforce.app.airSlate_app.admin_tools.popups.DeleteCustomButtonPopUp;
import pages.salesforce.app.airSlate_app.admin_tools.wizards.custom_button.CustomButtonInfoWizardPage;
import pages.salesforce.enums.admin_tools.ASAppCustomButtonField;
import utils.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static core.check.Check.*;
import static data.salesforce.SalesforceTestData.ASAppPageTexts.*;
import static org.awaitility.Awaitility.await;
import static pages.salesforce.enums.admin_tools.ASAppAdminToolTabs.CUSTOM_BUTTONS;

public class CustomButtonTab extends ASAppAdminToolsPage {

    private By customButton = By.xpath("//*[contains(@class, 'tableRow__')]");
    private By btnCreateButton = By.xpath("//button[.='Create button']");
    private By inputSearch = By.xpath("//*[contains(@class, 'isOpened__')]//input");
    private By btnHelper = By.xpath("//*[contains(@class, 'helper__')]");
    private By containerTitle = By.xpath("//*[contains(@class, 'tableBodyWrapper__')]//*[contains(@class, 'title__')]");
    private By containerSubtitle = By.xpath("//*[contains(@class, 'tableBodyWrapper__')]//*[contains(@class, 'subtitle__')]");

    public CustomButtonTab(WebDriver driver) {
        super(driver);
    }

    @Override
    public void isOpened() {
        skipAdminToolLoader(20);
        checkEquals(getActiveTabName(), CUSTOM_BUTTONS.getName(), "Custom button tab is not opened");
    }

    @Step
    public void checkIsCustomButtonPageReadOnly() {
        By searchButton = By.xpath("//*[contains(@class, 'searchWrapper__')]//button[.='Create button']");
        checkFalse(isElementEnabled(searchButton, 2), "Search Button is enabled");
    }

    @Step
    public void checkLabelsWhenNoOneCustomButtonIsAdded() {
        if (getNumberOfElements(customButton) == 0) {
            checkIsElementDisplayed(containerTitle, 4, "Page content title");
            checkIsElementDisplayed(containerSubtitle, 4, "Page content subtitle");
            SoftCheck softCheck = new SoftCheck();
            softCheck.checkEquals(getAttribute(containerTitle, "textContent"), SET_UP_YOUR_FIRST_CUSTOM_BUTTON,
                    "Incorrect content title");
            softCheck.checkEquals(getAttribute(containerSubtitle, "textContent"), YOU_CAN_USE_SALESFORCE_RECORDS_TO,
                    "Incorrect content subtitle");
            softCheck.checkAll();
        }
    }

    @Step
    public CustomButtonInfoWizardPage setUpNow() {
        By btnSetUpNow = By.xpath("//button[.='Set up Now']");

        checkIsElementDisplayed(btnSetUpNow, 4, "Set up now button");
        click(btnSetUpNow);
        return initExpectedPage(CustomButtonInfoWizardPage.class);
    }

    public int getNumberOfAddedButtons() {
        return getNumberOfElements(customButton);
    }

    @Step
    public CustomButtonTab waitForCustomButtonEditable(String buttonDescription, int seconds) {
        await().atMost(seconds, TimeUnit.SECONDS).until(() -> {
            refreshPage();
            initExpectedPage(ASAppAdminToolsPage.class).openTab(CUSTOM_BUTTONS);
            return openEditButtonMenu(buttonDescription).isButtonEditable();
        });
        return this;
    }

    @Step
    public boolean isButtonPresent(String buttonDescription) {
        By customButton = By.xpath("//*[@title='" + buttonDescription + "']");
        return isElementPresent(customButton);
    }

    @Step
    public CustomButtonTab deleteAllButtons() {
        while (getNumberOfAddedButtons() > 0) {
            deleteFirstButton().remove();
        }
        return this;
    }

    @Step
    private DeleteCustomButtonPopUp deleteFirstButton() {
        By btnMore = By.xpath("//*[contains(@class, 'tableRow__')]//*[contains(@class, 'editCol__')]");
        By btnRemoveButton = By.xpath("//*[contains(@class, 'tableRow__')]//*[contains(@class, 'editCol__')]//button[.='Remove button']");

        checkIsElementDisplayed(customButton, 2, "First custom button");
        checkIsElementDisplayed(btnMore, 2, "More action button");
        waitForNotificationDisappered();
        click(btnMore);
        checkIsElementDisplayed(btnRemoveButton, 4, "\"Remove custom button\" button");
        click(btnRemoveButton);
        return initExpectedPage(DeleteCustomButtonPopUp.class);
    }

    @Step
    public DeleteCustomButtonPopUp deleteButton(String buttonDescription) {
        By btnRemoveButton = By.xpath("//*[contains(@class, 'tableRow__')]//*[contains(@class, 'editCol__')]//button[.='Remove button']");

        openEditButtonMenu(buttonDescription);
        checkIsElementDisplayed(btnRemoveButton, 4, "remove button");
        click(btnRemoveButton);
        return initExpectedPage(DeleteCustomButtonPopUp.class);
    }

    @Step
    private CustomButtonTab selectButton(String buttonDescription) {
        By button = By.xpath("//*[@title='" + buttonDescription + "']/ancestor::*[contains(@class, 'tableRow__')]");

        checkIsElementDisplayed(button, 2, buttonDescription + " button");
        click(button);
        checkTrue(isElementContainsStringInClass(button, "isActive__"), "custom button is not being active");
        return this;
    }

    @Step
    public CustomButtonTab openEditButtonMenu(String buttonDescription) {
        By btnMore = By.xpath("//*[contains(@class, 'tableRow__') and contains(@class,'isActive__')]//*[contains(@class, 'editCol__')]");

        selectButton(buttonDescription);
        checkIsElementDisplayed(btnMore, 2, "Button more action for active button");
        click(btnMore);
        return this;
    }

    public boolean isButtonEditable() {
        By btnOption = By.xpath("//*[contains(@class, 'tableRow__')]//*[contains(@class, 'editCol__')]//button[.='Edit button']");
        return getAttribute(btnOption, "disabled") == null;
    }

    @Step
    public CustomButtonInfoWizardPage editButton(String buttonDescription) {
        By btnEditButton = By.xpath("//*[contains(@class, 'editCol__')]//*[.='Edit button']");

        openEditButtonMenu(buttonDescription);
        checkTrue(isButtonEditable(), "Edit option is not active");
        click(btnEditButton);
        return initExpectedPage(CustomButtonInfoWizardPage.class);
    }

    @Step
    public CustomButtonTab sortBy(ASAppCustomButtonField field, String order) {
        By columnHeader = By.xpath("//*[contains(@class, 'tableHeader__')]//*[text()='" + field.getName() + "']");

        Logger.info("Sort buttons by " + field + " in " + order + " order");
        checkIsElementDisplayed(columnHeader, 2, field.getName() + " table header");
        click(columnHeader);
        if (!isElementContainsStringInClass(columnHeader, order)) {
            click(columnHeader);
        }
        skipAdminToolLoader();
        checkTrue(isElementContainsStringInClass(columnHeader, order), "Custom button sort attribute is not added");
        return this;
    }

    @Step
    public List<CustomButton> initCustomButtons() {
        List<WebElement> customButtonElements = driver.findElements(customButton);
        List<CustomButton> customButtons = new ArrayList<>();
        customButtonElements.forEach(element -> customButtons.add(getCustomButton(element)));

        return customButtons;
    }

    private CustomButton getCustomButton(WebElement element) {
        By labelColumn = By.xpath(".//*[contains(@class, 'titleCol__')]");
        By descriptionColumn = By.xpath(".//*[contains(@class, 'descriptionCol__')]");
        By flowColumn = By.xpath(".//*[contains(@class, 'flowCol__')]");
        By dateColumn = By.xpath(".//*[contains(@class, 'dateCol__')]");

        SoftCheck softCheck = new SoftCheck();
        softCheck.checkTrue(element.findElements(labelColumn).size() > 0, "label column is not present");
        softCheck.checkTrue(element.findElements(descriptionColumn).size() > 0, "description column is not present");
        softCheck.checkTrue(element.findElements(flowColumn).size() > 0, "flow column is not present");
        softCheck.checkTrue(element.findElements(dateColumn).size() > 0, "date column is not present");
        softCheck.checkAll();

        String label = element.findElement(labelColumn).getAttribute("title");
        String description = element.findElement(descriptionColumn).getAttribute("title");
        String flow = element.findElement(flowColumn).getAttribute("title");
        String date = element.findElement(dateColumn).getText();
        return new CustomButton(label, description, flow, date);
    }

    @Step
    private CustomButtonTab openSearch() {
        By btnSearch = By.xpath("//*[contains(@class, 'search__')]");

        checkIsElementDisplayed(btnSearch, 3, "Search bar");
        click(btnSearch);
        checkIsElementDisplayed(inputSearch, 2, "Search input field");
        checkEquals(getAttribute(inputSearch, "placeholder"), "Search", "Incorrect search bar placeholder");
        return this;
    }

    @Step
    public CustomButtonTab enterSearchRequest(String request) {
        openSearch();
        checkIsElementDisplayed(inputSearch, 2, "Search input field");
        typeV2(inputSearch, request);
        return this;
    }

    @Step
    public CustomButtonTab viewResultsFor() {
        checkIsElementDisplayed(btnHelper, 2, "View results for button");
        click(btnHelper);
        skipAdminToolLoader();
        return this;
    }

    @Step
    public CustomButtonTab pressEnter() {
        type(Keys.ENTER);
        skipAdminToolLoader();
        return this;
    }

    @Step
    public CustomButtonTab clearSearch() {
        By btnClearSearch = By.xpath("//button[contains(@class, 'btn__iconClear__')]");

        checkIsElementDisplayed(btnClearSearch, 4, "'Clear search result' button");
        click(btnClearSearch);
        skipAdminToolLoader();
        return this;
    }

    @Step
    public String getHelperText() {
        return getAttribute(btnHelper, "innerText");
    }

    @Step
    public boolean isNothingFoundDisplayed() {
        return getAttribute(containerTitle, "textContent").equals(SORRY_NOTHING_CAME_UP)
                && getAttribute(containerSubtitle, "textContent").equals(TRY_CLEANING_THE_SEARCH);
    }

    public String getSearchText() {
        By searchMessage = By.xpath("//*[contains(@class, 'searchResult__')]");
        checkIsElementDisplayed(searchMessage, 3, "Search message");
        return getAttribute(searchMessage, "textContent");
    }
}
