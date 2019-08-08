package pages.salesforce.app.airSlate_app.admin_tools.wizards;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import pages.salesforce.app.DaDaDocs.full_app.templates.components.FooterNavigation;
import pages.salesforce.app.SalesAppBasePage;

import static core.check.Check.checkTrue;

public abstract class WizardPage extends SalesAppBasePage implements FooterNavigation {

    protected By loader = By.xpath("//*[contains(@class, 'loaderWrapper__')]");
    protected By title = By.xpath("//*[contains(@class, 'title__')]");
    private By btnPreviousTab = By.xpath("//*[contains(@class, 'footer')]//button[contains(@class, 'btn__secondary__')]");
    private By btnNextTab = By.xpath("//*[contains(@class, 'footer')]//button[contains(@class, 'btn__accent__')]");

    public WizardPage(WebDriver driver) {
        super(driver);
    }

    @Override
    public void isOpened() {
        skipLoader();
        checkTrue(isElementPresentAndDisplayed(title, 20), "MappingObject header is not displayed");
    }

    public void skipLoader() {
        checkTrue(isElementDisappeared(loader, 90), "Loader is still present");
    }

    @Override
    public <T extends SalesAppBasePage> T navigateToPreviousTab() {
        checkIsElementDisplayed(btnPreviousTab, 5, "Previous tab button");
        click(btnPreviousTab);
        skipLoader();
        return (T) this;
    }

    @Override
    public <T extends SalesAppBasePage> T navigateToNextTab() {
        checkIsElementDisplayed(btnNextTab, 5, "Next tab button");
        click(btnNextTab);
        skipLoader();
        return null;
    }

    @Step
    public <T extends LeaveWizardPopUp> T leaveWizard(Class<T> expectedPopup) {
        By btnLeaveWizard = By.xpath("//button[contains(@class, 'navbarBackButton__')]");

        checkIsElementDisplayed(btnLeaveWizard, 10, "Leave wizard button");
        click(btnLeaveWizard);
        return initExpectedPage(expectedPopup);
    }

    @Step
    public boolean isNextTabButtonEnable() {
        return isElementEnabled(btnNextTab, 3);
    }
}
