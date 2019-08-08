package pages.salesforce.app.DaDaDocs.full_app.V3;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import pages.salesforce.SalesforceBasePage;
import pages.salesforce.app.DaDaDocs.PreviewTab;
import pages.salesforce.app.DaDaDocs.full_app.V3.tabs.DocumentsTab;
import pages.salesforce.enums.V3.PrintDocumentV3Actions;
import utils.Logger;

import java.util.ArrayList;
import java.util.List;

import static core.check.Check.checkTrue;

public class PrintDocumentPage extends DocumentsTab {

    private By previewFrame = By.xpath("//iframe[contains(@class, 'iframe__')]");
    private PreviewTab previewTab;

    public PrintDocumentPage(WebDriver driver) {
        super(driver);
    }

    @Override
    public void isOpened() {
        skipLoader();
        checkTrue(isElementPresent(previewFrame, 15), "preview frame is not loaded");
    }

    @Step
    public <T extends SalesforceBasePage> T selectAnAction(PrintDocumentV3Actions action) {
        SalesforceBasePage page = null;
        By actionLocator = By.xpath("//*[contains(@class, 'grid__sidebarNoSearch__')]//button[.='" + action.getAction() + "']");

        Logger.info("Select action: " + action.getAction());
        checkTrue(isElementDisplayed(actionLocator), action.getAction() + " action is not presented");
        click(actionLocator);

        page = PageFactory.initElements(driver, action.getExpectedPage());
        skipLoader();
        page.isOpened();
        return (T) page;
    }

    @Step
    public boolean isActionsChanged() {
        List<String> actions = getAllActions();
        PrintDocumentV3Actions[] expectedActions = PrintDocumentV3Actions.values();
        if (actions.size() == expectedActions.length) {
            for (PrintDocumentV3Actions expectedAction : expectedActions) {
                if (!actions.contains(expectedAction.getAction())) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    @Step
    public List<String> getAllActions() {
        By actionLocator = By.xpath("//*[contains(@class, 'grid__sidebarNoSearch__')]//button");
        List<String> result = new ArrayList<>();

        Logger.info("Get all page actions");
        driver.findElements(actionLocator).forEach(element -> {
            result.add(element.getAttribute("textContent"));
        });
        return result;
    }

    @Step
    public DocumentsTab closePreview() {
        By btnClosePreview = By.xpath("//*[@data-test='closeButton']");

        Logger.info("Close preview");
        checkTrue(isElementPresentAndDisplayed(btnClosePreview, 2), "close preview button is not displayed");
        clickJS(btnClosePreview);
        checkTrue(isElementNotDisplayed(previewFrame, 5), "Preview frame is not disappeared");
        skipLoader();
        DocumentsTab documentsTab = new DocumentsTab(driver);
        documentsTab.isOpened();
        return documentsTab;
    }
}
