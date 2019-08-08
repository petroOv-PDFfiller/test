package pages.salesforce.app.airSlate_app.admin_tools.wizards.scheduled_flow;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import pages.salesforce.app.SalesAppBasePage;
import pages.salesforce.app.airSlate_app.admin_tools.wizards.WizardPage;

import java.util.List;
import java.util.stream.Collectors;

import static api.salesforce.util.TimeParamsUtil.*;
import static core.check.Check.checkTrue;

public class FrequencyWizardPage extends WizardPage {

    private By dropdownSelectFlow = By.xpath("(//*[@class = 'Select-control'])[1]");
    private By dropdownHours = By.xpath("(//*[contains(@class, 'select___')])[2]");
    private By dropdownMinutes = By.xpath("(//*[contains(@class, 'select___')])[3]");
    private By dropdownMeridiem = By.xpath("(//*[contains(@class, 'select___')])[4]");

    public FrequencyWizardPage(WebDriver driver) {
        super(driver);
        title = By.xpath("//*[contains(@class, 'title__') and text()='Frequency']");
    }

    @Override
    public void isOpened() {
        skipLoader();
        checkTrue(isElementPresentAndDisplayed(title, 10), "Action settings page title is not displayed");
    }

    @Step
    public List<String> getAvailableFrequencies() {
        By flow = By.xpath("(//*[contains(@class, 'fieldWrapper')])[1]//*[@aria-label]");
        List<WebElement> flowElements = driver.findElements(flow);
        return flowElements.stream().map((WebElement name) -> name.getAttribute("textContent")).collect(Collectors.toList());
    }

    @Step
    public FrequencyWizardPage selectFrequency(String frequencyName) {
        By flow = By.xpath("(//*[contains(@class, 'fieldWrapper')])[1]//*[@aria-label='" + frequencyName + "']");
        checkIsElementDisplayed(flow, 4, frequencyName);
        click(flow);
        return this;
    }

    public String getActiveFrequency() {
        checkIsElementDisplayed(dropdownSelectFlow, 4, "Select frequency dropdown");
        return getAttribute(dropdownSelectFlow, "innerText");
    }

    @Step
    public FrequencyWizardPage openSelectFrequencyDropdown() {
        checkIsElementDisplayed(dropdownSelectFlow, 2, "Select a frequency dropdown");
        click(dropdownSelectFlow);
        return this;
    }

    public int getHours() {
        checkIsElementDisplayed(dropdownHours, 2, "Select hours dropdown");
        return getCalendarHours(getAttribute(dropdownHours, "textContent"));
    }

    @Step
    public FrequencyWizardPage setHours(int hoursNumber) {
        String hours = getFormattedHours(hoursNumber);
        By hourOption = By.xpath("(//*[contains(@class, 'select___')])[2]//*[@aria-label='" + hours + "']");

        checkIsElementDisplayed(dropdownHours, 2, "Select hours dropdown");
        click(dropdownHours);
        checkIsElementDisplayed(hourOption, 5, hours + " hours option");
        click(hourOption);
        return this;
    }

    @Step
    public FrequencyWizardPage setDay(int dayNumber) {
        By day = By.xpath("(//*[contains(@class, 'day__')])[" + (dayNumber - 1) + "]//label");

        checkIsElementDisplayed(day, 5, dayNumber + " day Number option");
        click(day);
        return this;
    }

    public int getMinutes() {
        checkIsElementDisplayed(dropdownMinutes, 2, "Select minutes dropdown");
        return getCalendarMinutes(getAttribute(dropdownMinutes, "textContent"));
    }

    @Step
    public FrequencyWizardPage setMinutes(int minutesNumber) {
        String minutes = getFormattedMinutes(minutesNumber);
        By minutesOption = By.xpath("(//*[contains(@class, 'select___')])[3]//*[@aria-label='" + minutes + "']");

        checkIsElementDisplayed(dropdownMinutes, 2, "Select minutes dropdown");
        click(dropdownMinutes);
        checkIsElementDisplayed(minutesOption, 5, minutes + " minutes option");
        click(minutesOption);
        return this;
    }

    @Override
    public <T extends SalesAppBasePage> T navigateToPreviousTab() {
        super.navigateToPreviousTab();
        return (T) initExpectedPage(ScheduleInfoWizardPage.class);
    }

    @Override
    public <T extends SalesAppBasePage> T navigateToNextTab() {
        super.navigateToNextTab();
        return (T) initExpectedPage(BatchSettingsWizardPage.class);
    }

    public int getMeridiem() {
        checkIsElementDisplayed(dropdownMeridiem, 2, "Select meridiem dropdown");
        return getCalendarMeridiem(getAttribute(dropdownMeridiem, "textContent"));
    }

    @Step
    public FrequencyWizardPage setMeridiem(int meridiem) {
        String meridiemValue = getFormattedMeridiem(meridiem);
        By meridiemOption = By.xpath("(//*[contains(@class, 'select___')])[4]//*[@aria-label='" + meridiemValue + "']");


        checkIsElementDisplayed(dropdownMeridiem, 2, "Select meridiem dropdown");
        click(dropdownMeridiem);
        checkIsElementDisplayed(meridiemOption, 5, meridiemValue + " meridiam option");
        click(meridiemOption);
        return this;
    }
}
