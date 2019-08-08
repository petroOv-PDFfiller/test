package pages.salesforce.app.DaDaDocs.full_app.templates;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import pages.salesforce.app.SalesAppBasePage;
import pages.salesforce.enums.V3.TemplateCreateTabs;
import utils.Logger;

import static core.check.Check.checkEquals;
import static core.check.Check.checkTrue;

public class CreateTemplateWizardPage extends SalesAppBasePage {

    protected By heading = By.xpath("//*[contains(@class, 'heading__')]");
    protected String headingText = "";
    protected By description = By.xpath("//*[contains(@class, 'description__')]");
    protected String descriptionText = "";
    protected String pageName = "";
    private By header = By.xpath("//header[contains(@class, 'header__')]");

    public CreateTemplateWizardPage(WebDriver driver) {
        super(driver);
    }

    @Override
    public void isOpened() {
        switchToDefaultContent();
        if (isElementPresent(iframe)) {
            checkTrue(isElementPresentAndDisplayed(iframe, 5), "Create template wizard frame is not available");
            switchToFrame(iframe, 5);
        }
        skipMappingLoader();
        checkTrue(isElementPresentAndDisplayed(header, 20), "MappingObject header is not displayed");
    }

    @Step
    public <T extends CreateTemplateWizardPage> T openTab(TemplateCreateTabs tabName) {
        CreateTemplateWizardPage page = null;
        By tabLink = By.xpath("//a[. = '" + tabName.getName() + "' and contains(@class, 'navLink__')]");

        checkTrue(isElementDisplayed(tabLink), tabName.getName() + " tab is not presented");
        if (!isElementContainsStringInClass(tabLink, "active__"))
            click(tabLink);

        page = PageFactory.initElements(driver, tabName.getExpectedPage());
        page.isOpened();
        return (T) page;
    }

    @Step
    public void checkTexts() {
        checkEquals(getText(heading).trim(), headingText, "Wrong heading text for " + pageName + " page");
        checkEquals(getText(description).trim(), descriptionText, "Wrong heading text for " + pageName + " page");
    }

    public void skipMappingLoader() {
        checkTrue(isElementDisappeared(vMappingLoader, 90), "MappingObject loader is still present");
    }

    @Step("Check if notification contains message:({0})")
    public boolean notificationMessageContainsText(String message) {
        By notificationPopUp = By.xpath("//*[contains(@class,'toastrWrap__')]");

        checkTrue(isElementPresentAndDisplayed(notificationPopUp, 5), "Pop up is not displayed");
        String actualMessage = getAttribute(notificationPopUp, "innerText");
        Logger.info("Actual message is - " + actualMessage);
        return actualMessage.contains(message);
    }
}
