package pages.salesforce.app.sf_objects;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import pages.salesforce.app.DaDaDocs.CustomButtonS2SPage;
import pages.salesforce.app.DaDaDocs.DaDaDocsLightningComponent;
import pages.salesforce.app.SalesAppBasePage;
import pages.salesforce.app.airSlate_app.lightning_component.AirSlateLightningComponent;
import pages.salesforce.app.sf_setup.AppBuilder;
import utils.Logger;
import utils.TimeMan;

import java.util.concurrent.TimeUnit;

import static core.check.Check.checkFail;
import static core.check.Check.checkTrue;
import static org.awaitility.Awaitility.await;

public abstract class ConcreteRecordPage extends SalesAppBasePage {
    public DaDaDocsLightningComponent daDaDocsLightningComponent;
    protected By detailsTab = By.xpath("//a[@title='Details']");
    private By tableContent = By.xpath("(//*[@class='tabset slds-tabs_card uiTabset--base uiTabset--default uiTabset flexipageTabset'])[1]");
    private By btnEditPage = By.xpath("//a[@data-id ='edit-page']");
    private By dropdownOneActions = By.xpath("//*[@data-aura-class='oneActionsDropDown']//a");


    public ConcreteRecordPage(WebDriver driver) {
        super(driver);
    }

    @Override
    public void isOpened() {
        switchToDefaultContent();
        checkTrue(waitUntilPageLoaded(), "Salesforce page is not loaded");
        waitForSalesforceLoading();
        checkTrue(isElementPresentAndDisplayed(tableContent, 15), "tableContent is not present");
    }

    @Step("Open Left tab: {0}")
    protected <T extends ConcreteRecordPage> T openLeftTab(LeftTabs tab) {
        By concreteTab = By.xpath("(//*[@class='tabs__nav']/li)[" + tab.getName() + "]");
        checkTrue(isElementPresent(concreteTab, 8), "concreteTab is not present");
        hoverOverAndClick(concreteTab);
        this.isOpened();
        return (T) this;
    }

    @Step
    public DaDaDocsLightningComponent switchToDaDaDocsLightningComponent() {
        Logger.info("Switching frame to dadadocs tab");

        if (!isElementPresent(dadadocsFrame, 10)) {
            Logger.warning("DaDaDocs component is not added to page");
            AppBuilder appBuilder = this.openAppBuilder();
            appBuilder.addComponent(appBuilder.daDaDocsComponent).save().activate();
            appBuilder.backToSalesForce();

            isOpened();
        }
        checkTrue(isElementPresentAndDisplayed(dadadocsFrame, 15), "DaDaDocs lightning component is not displayed");
        TimeMan.sleep(1);
        checkTrue(switchToFrame(dadadocsFrame), "Can not switch to frame");
        TimeMan.sleep(1);
        daDaDocsLightningComponent = new DaDaDocsLightningComponent(driver);
        daDaDocsLightningComponent.isOpened();

        return daDaDocsLightningComponent;
    }

    @Step
    public AirSlateLightningComponent switchToAirSlateLightning() {
        Logger.info("Switching frame to airSlate lightning component");
        By airSlateFrame = By.xpath("//*[contains(@data-component-id, 'airSlateWidget')]//iframe");

        if (!isElementPresent(airSlateFrame, 10)) {
            Logger.warning("airSlate component is not added to page");
            AppBuilder appBuilder = this.openAppBuilder();
            appBuilder.addComponent(appBuilder.airSlateComponent).save().activate();
            appBuilder.backToSalesForce();
            isOpened();
        }
        TimeMan.sleep(1);
        checkTrue(switchToFrame(airSlateFrame), "Can not switch to frame");
        TimeMan.sleep(1);
        return initExpectedPage(AirSlateLightningComponent.class);
    }

    @Step("Open edit page wizard")
    public AppBuilder openAppBuilder() {
        checkTrue(isElementPresentAndDisplayed(setupGear, 5), "Setup gear is not displayed");
        click(setupGear);
        checkTrue(isElementPresentAndDisplayed(setupGearMenu, 10), "Gear menu is not displayed");
        checkTrue(isElementPresentAndDisplayed(btnEditPage, 10), "Edit page button is not displayed");
        click(btnEditPage);

        AppBuilder appBuilder = new AppBuilder(driver);
        appBuilder.isOpened();
        return appBuilder;
    }

    @Step
    public CustomButtonS2SPage clickOrderFormButton(String buttonName) {
        By orderFormButton = By.xpath("//div[text()='" + buttonName + "']");
        checkTrue(isElementPresentAndDisplayed(orderFormButton, 10), "Order form button is not presented");
        click(orderFormButton);
        CustomButtonS2SPage orderFormPage = new CustomButtonS2SPage(driver);
        orderFormPage.isOpened();

        return orderFormPage;
    }

    @Step
    public NotesAndAttachmentsPreviewPage openFilePreview(String fileName) {
        By file = By.xpath("//*[contains(@class, 'previewMode ')]//a[@title = '" + fileName + "']/div");
        By notesAndAttachments = By.xpath("//*[@title='Notes & Attachments']");

        checkTrue(isElementPresentAndDisplayed(notesAndAttachments, 10), "Notes and attachments block" +
                " is not displayed");
        scrollTo(notesAndAttachments);
        checkTrue(isElementPresentAndDisplayed(file, 15), fileName + "file is not present");
        click(file);
        NotesAndAttachmentsPreviewPage attachmentsPreviewPage = new NotesAndAttachmentsPreviewPage(driver);
        attachmentsPreviewPage.isOpened();
        return attachmentsPreviewPage;
    }

    public boolean isCustomButtonAdded(String buttonLabel) {
        By button = By.xpath("//*[@title='" + buttonLabel + "']");

        checkIsElementDisplayed(dropdownOneActions, 6, "One action dropdown");
        click(dropdownOneActions);
        return isElementPresent(button);
    }

    @Step
    public void waitForCustomButton(String buttonLabel, int seconds) {
        try {
            new WebDriverWait(driver, seconds).until((WebDriver wd) -> {
                refreshPage();
                isOpened();
                return isCustomButtonAdded(buttonLabel);
            });
        } catch (Exception e) {
            checkFail("Custom button is not added to the page\n" + e.getMessage());
        }
    }

    @Step
    public void waitForDeleteCustomButton(String buttonLabel, int seconds) {
        try {
            await().ignoreExceptions().atMost(seconds, TimeUnit.SECONDS)
                    .until(() -> {
                        refreshPage();
                        isOpened();
                        return !isCustomButtonAdded(buttonLabel);
                    });
        } catch (Exception e) {
            checkFail("Custom button is not added to the page\n" + e.getMessage());
        }
    }

    @Step
    public <T extends SalesAppBasePage> T clickOnCustomButton(String buttonLabel, Class<T> expectedPage) {
        By button = By.xpath("//*[@title='" + buttonLabel + "']");

        if (!isElementDisplayed(button)) {
            checkIsElementDisplayed(dropdownOneActions, 6, "One action dropdown");
            click(dropdownOneActions);
        }
        checkIsElementDisplayed(button, 5, buttonLabel + " button ");
        clickJS(button);
        return initExpectedPage(expectedPage);
    }
}