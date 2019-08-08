package pages.salesforce.app.DaDaDocs.full_app.main_tabs;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import pages.salesforce.SalesforceBasePage;
import pages.salesforce.app.DaDaDocs.editor.DaDaDocsEditor;
import pages.salesforce.app.DaDaDocs.full_app.DaDaDocsFullApp;
import pages.salesforce.app.DaDaDocs.full_app.pop_ups.LinkToFillTemplatePopUp;
import pages.salesforce.app.DaDaDocs.full_app.pop_ups.TemplateInfoPopUp;
import pages.salesforce.app.DaDaDocs.link_to_fill.LinkToFillCustomizeTab;
import pages.salesforce.app.DaDaDocs.send_to_sign.SendToSignPage;
import pages.salesforce.enums.SaleforceMyDocsTab;

import static core.check.Check.checkTrue;
import static data.salesforce.SalesforceTestData.TemplatesActions.*;

public class TemplatesPage extends DaDaDocsFullApp {

    public TemplatesPage(WebDriver driver) {
        super(driver);
    }

    @Override
    public void isOpened() {
        By tab = By.xpath("//*[@id='" + SaleforceMyDocsTab.TEMPLATES.getName() + "']/ancestor-or-self::li");
        skipTabLoader();
        if (!isElementPresent(tab)) {
            driver.switchTo().defaultContent();
            switchToFrame(iframe, 4);
        }
        checkTrue(isElementPresentAndDisplayed(tab, 12), "Template tab is not present");
        skipTabLoader();
        checkTrue(isElementContainsStringInAttribute(tab, "class", "active", 5), "Template tab is not active");
    }

    @Step
    public <T extends SalesforceBasePage> T selectAnAction(String actionName) {
        SalesforceBasePage page = null;
        By actionLocator = By.xpath("//div[contains(@class, 'dadadocs_tab_active')]//button[contains(text(),'" + actionName + "') and not(contains(@class, 'isHidden'))]");

        checkTrue(isElementDisplayed(actionLocator), actionName + " action is not presented");
        hoverOverAndClick(actionLocator);

        switch (actionName) {
            case FILL_TEMPLATE: {
                page = new DaDaDocsEditor(driver);
                break;
            }
            case LINK_TO_FILL: {
                page = new LinkToFillCustomizeTab(driver);
                skipGlobalLoaderInMyDaDaDocs();
                break;
            }
            case GET_INFO: {
                page = new TemplateInfoPopUp(driver);
                break;
            }
            case DELETE_TEMPLATE: {
                page = this;
                break;
            }
            case SEND_TO_SIGN: {
                page = new SendToSignPage(driver);
                skipGlobalLoaderInMyDaDaDocs();
                break;
            }
        }
        skipLoader();
        page.isOpened();

        return (T) page;
    }

    @Step
    public String getTemplateID(String templateName) {
        By dadadadocsItem = By.cssSelector(".dadadocs_tab.dadadocs_tab_active div[data-name='" + templateName + "']");

        checkTrue(isDadaDocsItemPresent(templateName), "Item with name " + templateName + " is not presented");
        return getAttribute(dadadadocsItem, "data-id");
    }

    @Step
    public DaDaDocsEditor fillTemplate(String fileName) {
        selectDadadocsItemByFullName(fileName);
        return selectAnAction(FILL_TEMPLATE);
    }

    @Step
    public LinkToFillTemplatePopUp linkToFillTemplate(String fileName) {
        selectDadadocsItemByFullName(fileName);
        return selectAnAction(LINK_TO_FILL);
    }

    @Step
    public TemplateInfoPopUp getTemplateInfo(String fileName) {
        selectDadadocsItemByFullName(fileName);
        return selectAnAction(GET_INFO);
    }
}