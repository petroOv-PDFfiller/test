package pages.salesforce.app.DaDaDocs.full_app.V3.tabs;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import pages.salesforce.SalesforceBasePage;
import pages.salesforce.app.DaDaDocs.full_app.V3.DaDaDocsFullAppV3;
import pages.salesforce.app.DaDaDocs.full_app.V3.entities.Template;
import pages.salesforce.enums.V3.TemplateTabV3Actions;
import utils.Logger;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static core.check.Check.*;
import static pages.salesforce.enums.V3.DaDaDocsV3Tabs.TEMPLATES_TAB;

public class TemplatesTab extends DaDaDocsFullAppV3 {

    public TemplatesTab(WebDriver driver) {
        super(driver);
    }

    @Override
    public void isOpened() {
        By firstTemplate = By.xpath("//*[contains(@class, 'tableBody__')]//div[contains(@class, 'wrap__')]");
        skipLoader();
        checkEquals(getActiveTabName(), TEMPLATES_TAB.getName(), "template tab page is not opened");
        if (!isElementDisappeared(firstTemplate, 6)) {
            Logger.info("No one template is present");
        }
    }

    @Step
    public <T extends SalesforceBasePage> T selectAnAction(TemplateTabV3Actions action) {
        By actionLocator = By.xpath("//button[.='" + action.getAction() + "']");

        checkTrue(isElementDisplayed(actionLocator), action.getAction() + " action is not presented");
        click(actionLocator);

        SalesforceBasePage page = PageFactory.initElements(driver, action.getExpectedPage());
        skipLoader();
        page.isOpened();
        return (T) page;
    }

    public boolean isTemplatePresent(String templateName) {
        By templateFile = By.xpath("//div[contains(@class, 'record__')]//*[contains(@class, 'title__') and text() ='" + templateName + "']");
        return isElementPresent(templateFile, 5);
    }

    public TemplatesTab selectTemplate(String templateName) {
        By templateFile = By.xpath("//div[contains(@class, 'record__')]//*[contains(@class, 'title__') and text() ='" + templateName + "']");
        By templateActiveFile = By.xpath("//div[contains(@class, 'record--active__')]//*[contains(@class, 'title__') and text() ='" + templateName + "']");

        checkTrue(isElementPresent(templateFile, 2), templateName + " template is not present");
        scrollTo(templateFile);
        click(templateFile);
        checkTrue(isElementPresent(templateActiveFile), templateName + " is not selected");
        return this;
    }

    public Template getSelectedTemplate() {
        By selectedTemplate = By.xpath("//div[contains(@class, 'record--active__')]");
        By selectedTemplateName = By.xpath("//div[contains(@class, 'record--active__')]//*[contains(@class, 'title__')]");
        By selectedTemplateDate = By.xpath("//div[contains(@class, 'record--active__')]//*[contains(@class, 'date__')]");
        By selectedTemplateTags = By.xpath("//div[contains(@class, 'record--active__')]//*[contains(@class, 'tag__')]");
        By selectedTemplateId = By.xpath("//div[contains(@class, 'record--active__')]//*[contains(@class, 'checkbox__input__')]");

        checkTrue(isElementPresent(selectedTemplate, 2), "No one template is selected");
        checkTrue(isElementPresent(selectedTemplateName, 2), "Selected template name is not present");
        checkTrue(isElementPresent(selectedTemplateDate, 2), "Selected template modified date is not present");
        checkTrue(isElementPresent(selectedTemplateId, 2), "Selected template id is not present");
        String name = getAttribute(selectedTemplateName, "textContent");
        String id = getAttribute(selectedTemplateId, "value");
        Date modifiedDate = null;
        try {
            modifiedDate = dateFormat.parse(getAttribute(selectedTemplateDate, "textContent"));
        } catch (ParseException e) {
            e.printStackTrace();
            checkFail("Wrong time format");
        }
        List<WebElement> tagElements = driver.findElements(selectedTemplateTags);
        List<String> tagsList = new ArrayList<>();
        tagElements.forEach(tag -> tagsList.add(tag.getText()));

        return new Template(name, tagsList, modifiedDate, id);
    }
}
