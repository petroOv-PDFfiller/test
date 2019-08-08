package pages.salesforce.app.DaDaDocs.full_app.pop_ups;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import pages.salesforce.app.SalesAppBasePage;
import utils.Logger;

import static core.check.Check.checkTrue;

public class CreateTemplatePopUp extends SalesAppBasePage {

    private By createTemplateHeader = By.xpath("//h3[text()='Tell us a little about your template']");
    private By giveTemplateANameInput = By.xpath("//h3[text()='Give your template a name:']/following-sibling::input");
    private By doneButton = By.xpath("//button[text()='Done']");

    public CreateTemplatePopUp(WebDriver driver) {
        super(driver);
    }

    @Override
    public void isOpened() {
        checkTrue(waitUntilPageLoaded(), "CreateTemplatePopUp isn`t loaded");
        checkTrue(isElementPresent(createTemplateHeader, 60), "Create template pop-up wasn't appeared");
    }

    @Step
    public CreateTemplatePopUp prefillTemplateAndInteractWithSalesforceObject(boolean isPrefill, boolean isNewOrUpdateObject) {
        By prefillCheckbox;
        if (isPrefill) {
            prefillCheckbox = By.id("prefillTemplate");
        } else {
            prefillCheckbox = By.id("noPrefillTemplate");
        }

        checkTrue(isElementPresent(prefillCheckbox), "Prefill checkbox is not displayed");
        click(prefillCheckbox);
        checkTrue(isElementEnabled(prefillCheckbox, 5), "Checkbox wasn't checked");

        if (isPrefill) {
            By objectInteractCheckbox;
            Logger.info("Updating existing salesforce object checkbox interaction");
            if (isNewOrUpdateObject) {
                objectInteractCheckbox = By.id("updateOrCreateRecord");
            } else {
                objectInteractCheckbox = By.id("noUpdateOrCreateRecord");
            }
            checkTrue(isElementPresent(objectInteractCheckbox), "Object checkbox is not displayed");

            click(objectInteractCheckbox);
        }

        return this;
    }

    @Step
    public CreateTemplatePopUp giveTemplateAName(String name) {
        checkTrue(isElementDisplayed(giveTemplateANameInput), "Input is not displayed");
        type(giveTemplateANameInput, name);

        return this;
    }

    @Step
    public void clickDoneButton() {
        checkTrue(isElementDisplayed(doneButton), "Done button is not displayed");
        click(doneButton);
    }
}
