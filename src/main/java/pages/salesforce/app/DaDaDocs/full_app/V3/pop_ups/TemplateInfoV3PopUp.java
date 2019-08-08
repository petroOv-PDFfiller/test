package pages.salesforce.app.DaDaDocs.full_app.V3.pop_ups;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import pages.salesforce.app.DaDaDocs.full_app.V3.tabs.TemplatesTab;
import pages.salesforce.app.DaDaDocs.full_app.pop_ups.TemplateInfoPopUp;
import pages.salesforce.app.SalesAppBasePage;

import static core.check.Check.checkEquals;
import static core.check.Check.checkTrue;

public class TemplateInfoV3PopUp extends TemplateInfoPopUp {

    public TemplateInfoV3PopUp(WebDriver driver) {
        super(driver);
        popUpContainer = By.xpath("//div[contains(@class, 'popup__container__')]");
        popUpHeader = By.xpath("//div[contains(@class, 'popup__header__')]//*[contains(@class, 'popup__title__')]");
        templateName = By.xpath("//span[@data-test = 'templateName']");
        templateDescription = By.xpath("//span[@data-test = 'templateDescription']");
        templateId = By.xpath("//span[@data-test = 'templateId']");
        templateOwnerNameEmail = By.xpath("//span[@data-test = 'templateOwnerNameEmail']");
        templateCreatedAt = By.xpath("//span[@data-test = 'templateCreatedAt']");
    }

    @Override
    public void isOpened() {
        checkTrue(isElementPresentAndDisplayed(popUpContainer, 5), "Pop up is not displayed");
        checkEquals(getText(popUpHeader), "Template info", "Wrong header for template info popup");
    }

    @Step
    @Override
    public <T extends SalesAppBasePage> T closePopUp() {
        By btnCloseInfo = By.xpath("//*[contains(@class, 'popup__header__')]//button[contains(@class, 'close__')]");
        checkTrue(isElementPresentAndDisplayed(btnCloseInfo, 5), "close button is not displayed");
        click(btnCloseInfo);
        TemplatesTab templatesTab = new TemplatesTab(driver);
        isOpened();
        return (T) templatesTab;
    }
}
