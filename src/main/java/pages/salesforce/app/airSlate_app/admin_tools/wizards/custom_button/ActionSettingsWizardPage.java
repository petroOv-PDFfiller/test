package pages.salesforce.app.airSlate_app.admin_tools.wizards.custom_button;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import pages.salesforce.app.SalesAppBasePage;
import pages.salesforce.app.airSlate_app.admin_tools.wizards.WizardPage;

import java.util.List;
import java.util.stream.Collectors;

import static core.check.Check.checkTrue;

public class ActionSettingsWizardPage extends WizardPage {

    private By dropdownSelectFlow = By.xpath("(//*[contains(@class, 'controlWrapper__')])[1]");

    public ActionSettingsWizardPage(WebDriver driver) {
        super(driver);
        title = By.xpath("//*[contains(@class, 'title__') and text()='Action settings']");
    }

    @Override
    public void isOpened() {
        skipLoader();
        checkTrue(isElementPresentAndDisplayed(title, 20), "Action settings page title is not displayed");
    }

    public void skipLoader() {
        checkTrue(isElementDisappeared(loader, 90), "Loader is still present");
    }

    @Step
    public List<String> getAvailableFlows() {
        By flow = By.xpath("(//*[contains(@class, 'fieldWrapper')])[2]//*[@data-test='listItem']");
        List<WebElement> flowElements = driver.findElements(flow);
        return flowElements.stream().map(WebElement::getText).collect(Collectors.toList());
    }

    @Step
    public ActionSettingsWizardPage selectFlow(String flowName) {
        By flow = By.xpath("(//*[contains(@class, 'fieldWrapper')])[2]//*[@data-test='listItem']//*[.='" + flowName + "']");
        checkIsElementDisplayed(flow, 4, flowName);
        click(flow);
        return this;
    }

    public String getActiveFlow() {
        checkIsElementDisplayed(dropdownSelectFlow, 4, "Select flow dropdown");
        return getAttribute(dropdownSelectFlow, "innerText");
    }

    @Step
    public ActionSettingsWizardPage openSelectFlowDropdown() {
        checkIsElementDisplayed(dropdownSelectFlow, 2, "Select a flow dropdown");
        click(dropdownSelectFlow);
        return this;
    }

    @Override
    public <T extends SalesAppBasePage> T navigateToPreviousTab() {
        super.navigateToPreviousTab();
        return (T) initExpectedPage(CustomButtonInfoWizardPage.class);
    }

    @Override
    public <T extends SalesAppBasePage> T navigateToNextTab() {
        super.navigateToNextTab();
        return (T) initExpectedPage(SalesforceLayoutsAndListsWizardPage.class);
    }
}
