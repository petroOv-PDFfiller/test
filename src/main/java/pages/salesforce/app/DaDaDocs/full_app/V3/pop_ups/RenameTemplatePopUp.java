package pages.salesforce.app.DaDaDocs.full_app.V3.pop_ups;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import pages.salesforce.app.DaDaDocs.full_app.V3.DaDaDocsV3BasePopUp;
import pages.salesforce.app.DaDaDocs.full_app.V3.tabs.TemplatesTab;

import static core.check.Check.checkEquals;
import static core.check.Check.checkTrue;

public class RenameTemplatePopUp extends DaDaDocsV3BasePopUp {

    public RenameTemplatePopUp(WebDriver driver) {
        super(driver);
    }

    @Override
    public void isOpened() {
        checkTrue(isElementPresentAndDisplayed(popUpBody, 4), "Rename template popup is not displayed");
        checkEquals(getText(popUpHeader), "Template rename", "Wrong header for rename template popup");
    }

    @Step("Set template name:{0}")
    public RenameTemplatePopUp setNewTemplateName(String newTemplateName) {
        By inputTemplateName = By.xpath("//*[contains(@class, 'popup__body__')]//input[@name = 'name']");
        checkTrue(isElementDisplayed(inputTemplateName, 2), "Template name field is not displayed");
        type(inputTemplateName, newTemplateName);
        return this;
    }

    @Step
    public TemplatesTab renameTemplate() {
        By btnRename = By.xpath("//*[contains(@class, 'popup__body__')]//button[.='Rename']");

        checkTrue(isElementDisplayed(btnRename), "Rename button is not displayed");
        click(btnRename);
        TemplatesTab templatesTab = new TemplatesTab(driver);
        templatesTab.isOpened();
        return templatesTab;
    }
}
