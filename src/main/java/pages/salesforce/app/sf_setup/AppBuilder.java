package pages.salesforce.app.sf_setup;

import com.google.common.collect.ImmutableMap;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import pages.salesforce.SalesforceBasePage;
import pages.salesforce.app.SalesAppBasePage;
import utils.Logger;

import java.util.Map;

import static core.check.Check.checkTrue;

public class AppBuilder extends SalesforceBasePage {
    public String airSlateComponent = "airSlate";
    public String daDaDocsComponent = "DaDaDocs";
    private By surfaceFrame = By.xpath("//iframe[@class = 'surfaceFrame']");
    private By editor = By.xpath("//div[@class = 'editor']");
    private By dadadocsElement = By.xpath("//a[contains(@title, 'DaDaDocs')]");
    private By airSlateElement = By.xpath("//a[contains(@title, 'airSlate')]");
    private Map<String, By> components = ImmutableMap.of(daDaDocsComponent, dadadocsElement, airSlateComponent, airSlateElement);
    private By addComponentPanel = By.xpath("//a[.='Add Component(s) Here']");

    public AppBuilder(WebDriver driver) {
        super(driver);
    }

    @Override
    public void isOpened() {
        switchToDefaultContent();
        checkTrue(waitUntilPageLoaded(), "App builder is not loaded");
        checkTrue(isElementPresentAndDisplayed(editor, 60), "app builder page is not opened");
        checkTrue(isElementPresentAndDisplayed(surfaceFrame, 5), "Preview page is not opened");
    }

    @Step("Add {0} component to page")
    public AppBuilder addComponent(String componentName) {
        By rightTab = By.xpath("//*[contains(@class, 'region-header')]");
        By btnAddComponentBefore = By.xpath("//div[contains(@class,'sf-interactions-proxyAddComponentAfter')]//a");

        switchToFrame(surfaceFrame);
        checkTrue(isElementPresentAndDisplayed(rightTab, 40), "Add " + componentName + " component panel in not displayed");
        scrollTo(rightTab);
        click(rightTab);
        clickJS(btnAddComponentBefore);
        switchToDefaultContent();
        checkTrue(isElementPresentAndDisplayed(components.get(componentName), 10), componentName + " element in not displayed");
        scrollTo(components.get(componentName));
        click(components.get(componentName));
        Logger.info("Component " + componentName + " added in editor");

        isOpened();
        return this;
    }

    @Step("Save page with component")
    public AppBuilder save() {
        By btnSave = By.xpath("//div[@class='toolbarRight']//*[.='Save']");
        By modal = By.xpath("//div[contains(@class,'modal-container')]");

        checkTrue(isElementEnabled(btnSave, 10), "Btn save is not present");
        click(btnSave);
        checkTrue(isElementPresentAndDisplayed(modal, 20), "Save element modal window is not opened");
        Logger.info("component saved on page");
        isOpened();
        return this;
    }

    @Step("Activate page with component")
    public SalesforceBasePage activate() {
        By btnActivate = By.xpath("//*[text()='Activate']");
        By btnAssign = By.xpath("//*[text()='Assign as Org Default']");
        By btnSavePage = By.xpath("//div[@class='right']//*[.='Save']");
        By btnNextPage = By.xpath("//div[@class='right']//*[.='Next']");
        By btnDesktop = By.xpath("//input[@value='ALL']/parent::*");
        By modal = By.xpath("//div[contains(@class,'modal-container')]");

        checkTrue(isElementPresentAndDisplayed(modal, 20), "Save element modal window is not opened");
        checkTrue(isElementPresentAndDisplayed(btnActivate, 20), "Activate button is not displayed");
        click(btnActivate);
        checkTrue(isElementPresentAndDisplayed(btnAssign, 45), "Assign button is not displayed");
        click(btnAssign);
        checkTrue(isElementPresentAndDisplayed(btnDesktop, 20), "Desktop form factor  is not displayed");
        click(btnDesktop);
        checkTrue(isElementPresentAndDisplayed(btnNextPage, 20), "Next page btn is not displayed");
        click(btnNextPage);
        checkTrue(isElementPresentAndDisplayed(btnSavePage, 20), "Save page btn is not displayed");
        click(btnSavePage);
        checkTrue(isElementNotDisplayed(modal, 10), "Save element modal window is still opened");
        Logger.info("Component activated");

        isOpened();
        return this;
    }

    @Step("Return to salesforce page")
    public SalesAppBasePage backToSalesForce() {
        By btnBack = By.xpath("//div[@class='right']//*[text()='Back']");

        checkTrue(isElementPresentAndDisplayed(btnBack, 10), "Back button is not displayed");
        click(btnBack);

        SalesAppBasePage salesAppBasePage = new SalesAppBasePage(driver);
        salesAppBasePage.isOpened();
        return salesAppBasePage;
    }
}
