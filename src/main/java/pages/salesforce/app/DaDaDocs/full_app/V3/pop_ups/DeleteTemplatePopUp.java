package pages.salesforce.app.DaDaDocs.full_app.V3.pop_ups;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import pages.salesforce.app.DaDaDocs.full_app.V3.DaDaDocsV3BasePopUp;
import pages.salesforce.app.DaDaDocs.full_app.V3.tabs.TemplatesTab;

import java.util.ArrayList;
import java.util.List;

import static core.check.Check.checkEquals;
import static core.check.Check.checkTrue;

public class DeleteTemplatePopUp extends DaDaDocsV3BasePopUp {

    public DeleteTemplatePopUp(WebDriver driver) {
        super(driver);
    }

    @Override
    public void isOpened() {
        checkTrue(isElementPresentAndDisplayed(popUpBody, 4), "Document delete popup is not displayed");
        checkEquals(getText(popUpHeader), "Delete Template?", "Wrong header for template delete popup");
    }

    @Step
    public List<String> getNameOfTemplates() {
        By templatesName = By.xpath("//div[contains(@class, 'popup__container__')]//*[@data-test = 'templateName']");

        List<String> result = new ArrayList<>();
        List<WebElement> elements = driver.findElements(templatesName);
        for (WebElement element : elements) {
            result.add(element.getText().replace("- ", ""));
        }
        return result;
    }

    @Step
    public TemplatesTab deleteTemplate() {
        By btnDelete = By.xpath("//div[contains(@class, 'popup__container__')]//button[.='Delete']");

        checkTrue(isElementDisplayed(btnDelete, 2), "Delete button is not displayed");
        click(btnDelete);
        TemplatesTab templatesTab = new TemplatesTab(driver);
        templatesTab.isOpened();
        return templatesTab;
    }
}
