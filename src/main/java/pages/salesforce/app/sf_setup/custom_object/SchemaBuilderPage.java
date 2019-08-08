package pages.salesforce.app.sf_setup.custom_object;

import core.check.SoftCheck;
import data.salesforce.SalesforceTestData;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.PageFactory;
import pages.salesforce.SalesforceBasePage;
import pages.salesforce.app.sf_setup.SetupSalesforcePage;
import pages.salesforce.app.sf_setup.custom_object.entities.SchemaBuilderCanvasObject;
import pages.salesforce.app.sf_setup.custom_object.entities.SchemaBuilderEditorElement;
import pages.salesforce.app.sf_setup.custom_object.entities.SchemaBuilderEditorObject;
import pages.salesforce.app.sf_setup.custom_object.popups.NewObjectFieldsPopUp;
import utils.Logger;

import java.util.ArrayList;
import java.util.List;

import static core.check.Check.checkTrue;
import static data.salesforce.SalesforceTestData.SchemaBuilderTabs.OBJECTS;

public class SchemaBuilderPage extends SetupSalesforcePage {

    private By canvas = By.id("viewport");
    private List<SchemaBuilderEditorObject> builderObjects = new ArrayList<>();
    private List<SchemaBuilderEditorElement> builderElements = new ArrayList<>();
    private List<SchemaBuilderCanvasObject> canvasObjects = new ArrayList<>();
    private By title = By.xpath("//h1[text() = 'Schema Builder']");
    private By spinnerMask = By.id("spinnerMask");

    public SchemaBuilderPage(WebDriver driver) {
        super(driver);
    }

    @Override
    public void isOpened() {
        switchToDefaultContent();
        checkTrue(waitUntilPageLoaded(), "Schema builder page is not loaded");
        checkTrue(isElementPresentAndDisplayed(iframe, 20), "vFrame is not present");
        checkTrue(switchToFrame(iframe, 5), "Cannot switch to vframe");
        checkTrue(isElementPresentAndDisplayed(title, 10), "Schema builder page is not opened");
        checkTrue(isElementNotDisplayed(spinnerMask, 5), "Loader is still displayed");
    }

    @Step
    public SchemaBuilderPage clearAllObjects() {
        By btnClearAll = By.id("clearAll");

        Logger.info("Hide all objects from canvas");
        openTab(OBJECTS);
        checkTrue(isElementPresentAndDisplayed(btnClearAll, 5), "Clear all button is not displayed");
        click(btnClearAll);
        initSchemaBuilderObjects();

        SoftCheck softCheck = new SoftCheck();
        for (SchemaBuilderEditorObject builderObject : builderObjects) {
            softCheck.checkFalse(builderObject.isActive(), "Wrong " + builderObject.getName() + " object status");
        }
        softCheck.checkAll();
        isOpened();
        return this;
    }

    @Step
    public SchemaBuilderPage openTab(SalesforceTestData.SchemaBuilderTabs tabName) {
        By btnTab = By.id(tabName.getName());
        Logger.info("Open tab " + tabName);
        checkTrue(isElementPresentAndDisplayed(btnTab, 5), tabName + " tab is not displayed");
        checkTrue(isElementNotDisplayed(spinnerMask, 5), "Loader is still displayed");
        click(btnTab);
        checkTrue(isElementContainsStringInAttribute(btnTab, "class", "selectedTab", 5), "Tab is not active");
        isOpened();
        return this;
    }

    @Step
    public List<SchemaBuilderEditorObject> initSchemaBuilderObjects() {
        By objects = By.xpath("//li[@class = 'std']");

        Logger.info("init schema builder objects");
        builderObjects = new ArrayList<>();
        int objectsCount = getNumberOfElements(objects);
        for (int i = 0; i < objectsCount; i++) {
            By status = By.xpath("(//li[@class = 'std'])[" + (i + 1) + "]//input");
            By name = By.xpath("(//li[@class = 'std'])[" + (i + 1) + "]//label");

            builderObjects.add(new SchemaBuilderEditorObject(getText(name), driver.findElement(status).isSelected()));
        }
        return builderObjects;
    }

    @Step
    public List<SchemaBuilderEditorElement> initSchemaBuilderElements() {
        By elements = By.xpath("//li[contains(@class,'schemaElement')]");

        Logger.info("Init schema builder elements");
        builderElements = new ArrayList<>();
        int elementsCount = getNumberOfElements(elements);
        for (int i = 0; i < elementsCount; i++) {
            By name = By.xpath("((//li[contains(@class,'schemaElement')])[" + (i + 1) + "]/div)[2]");
            By locator = By.xpath("(//li[contains(@class,'schemaElement')])[" + (i + 1) + "]");

            builderElements.add(new SchemaBuilderEditorElement(getText(name), locator));
        }
        return builderElements;
    }

    @Step
    public List<SchemaBuilderCanvasObject> initAddedObjects() {
        By canvasObject = By.xpath("//div[@id = 'schemaBuilderCanvas']//div[contains(@class ,'object')]");

        Logger.info("init added to canvas objects");
        checkTrue(isElementNotDisplayed(spinnerMask, 10), "Loader is still present");
        int addedObjects = getNumberOfElements(canvasObject);
        canvasObjects = new ArrayList<>();
        for (int i = 0; i < addedObjects; i++) {
            By selectedObject = By.xpath("(//div[@id = 'schemaBuilderCanvas']//div[contains(@class ,'object')])[" + (i + 1) + "]");
            By title = By.xpath("(//div[@id = 'schemaBuilderCanvas']//div[contains(@class ,'object')])[" + (i + 1) + "]//h3");
            if (!getAttribute(selectedObject, "class").contains("hidden")) {
                canvasObjects.add(new SchemaBuilderCanvasObject(getAttribute(title, "innerText"), selectedObject, getAttribute(selectedObject, "id")));
            }
        }
        return canvasObjects;
    }

    @Step
    public NewObjectFieldsPopUp addElementToCanvas(SalesforceTestData.SchemaBuilderElements element, Dimension coordinates) {
        initSchemaBuilderElements();
        By elementLocator = getEditorElementLocatorByName(element.getName());

        Logger.info("Add element to canvas");
        Actions actions = new Actions(driver);
        actions.clickAndHold(driver.findElement(elementLocator)).pause(1000).moveToElement(driver.findElement(canvas),
                coordinates.width, coordinates.height).pause(1000).release().build().perform();
        NewObjectFieldsPopUp newObjectFieldsPopUp = new NewObjectFieldsPopUp(driver);
        newObjectFieldsPopUp.isOpened();
        return newObjectFieldsPopUp;
    }

    public <T extends SalesforceBasePage> T addElementToCanvasObject(SalesforceTestData.SchemaBuilderElements element, String objectName) {
        initSchemaBuilderElements();
        By elementLocator = getEditorElementLocatorByName(element.getName());
        By objectLocator = getCanvasElementByName(objectName).getLocator();

        Logger.info("Add element to canvas object");
        Actions actions = new Actions(driver);
        actions.clickAndHold(driver.findElement(elementLocator)).pause(1000).moveToElement(driver.findElement(objectLocator))
                .pause(1000).release().build().perform();
        SalesforceBasePage page = PageFactory.initElements(driver, element.getExpectedPage());
        page.isOpened();
        return (T) page;
    }

    public By getEditorElementLocatorByName(String name) {
        By result;

        for (SchemaBuilderEditorElement builderElement : builderElements) {
            if (builderElement.getName().equals(name)) {
                result = builderElement.getLocator();
                return result;
            }
        }
        throw new NullPointerException("element is not found");
    }

    public SchemaBuilderCanvasObject getCanvasElementByName(String name) {
        SchemaBuilderCanvasObject result;

        for (SchemaBuilderCanvasObject canvasElement : canvasObjects) {
            if (canvasElement.getName().equals(name)) {
                result = canvasElement;
                return result;
            }
        }
        return null;
    }
}
