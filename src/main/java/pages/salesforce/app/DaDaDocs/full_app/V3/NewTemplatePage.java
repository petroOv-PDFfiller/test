package pages.salesforce.app.DaDaDocs.full_app.V3;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import pages.salesforce.SalesforceBasePage;
import pages.salesforce.app.DaDaDocs.full_app.V3.tabs.TemplatesTab;
import pages.salesforce.enums.V3.NewTemplateV3Actions;
import utils.Logger;

import java.util.ArrayList;
import java.util.List;

import static core.check.Check.checkTrue;

public class NewTemplatePage extends TemplatesTab {

    public NewTemplatePage(WebDriver driver) {
        super(driver);
    }

    @Override
    public void isOpened() {
        super.isOpened();
        checkTrue(initBreadcrumbs().contains("New-Template"), "Breadcrumb is not added");
    }

    @Step
    public <T extends SalesforceBasePage> T selectAnAction(NewTemplateV3Actions action) {
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
    public NewTemplatePage selectDocument(String documentName) {
        By documentFile = By.xpath("//div[contains(@class, 'record__')]//*[contains(@class, 'title__') and text() ='" + documentName + "']");
        By documentActiveFile = By.xpath("//div[contains(@class, 'record--active__')]//*[contains(@class, 'title__') and text() ='" + documentName + "']");

        checkTrue(isElementPresent(documentFile), documentName + " document is not present");
        scrollTo(documentFile);
        click(documentFile);
        checkTrue(isElementPresent(documentActiveFile), documentName + " is not selected");
        return this;
    }

    @Step
    public boolean isActionsChanged() {
        List<String> actions = getAllActions();
        NewTemplateV3Actions[] expectedActions = NewTemplateV3Actions.values();
        if (actions.size() == expectedActions.length) {
            for (NewTemplateV3Actions expectedAction : expectedActions) {
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
        By actionLocator = By.xpath("//*[contains(@class, 'grid__sidebarNoSearch__')]//button[not(contains(@class, 'btn__icon__'))]");
        List<String> result = new ArrayList<>();

        Logger.info("Get all page actions");
        driver.findElements(actionLocator).forEach(element -> {
            result.add(element.getAttribute("textContent"));
        });
        return result;
    }
}
