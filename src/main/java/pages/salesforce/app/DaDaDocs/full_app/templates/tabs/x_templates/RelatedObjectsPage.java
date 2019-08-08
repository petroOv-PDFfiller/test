package pages.salesforce.app.DaDaDocs.full_app.templates.tabs.x_templates;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import pages.salesforce.app.DaDaDocs.full_app.templates.CreateTemplateWizardPage;
import pages.salesforce.app.DaDaDocs.full_app.templates.components.ObjectsTree;

import static core.check.Check.checkEquals;
import static core.check.Check.checkTrue;

public class RelatedObjectsPage extends CreateTemplateWizardPage implements ObjectsTree {

    public RelatedObjectsPage(WebDriver driver) {
        super(driver);
    }

    @Override
    @Step
    public ObjectsTree addNewRelatedObject() {
        By addedObject = By.xpath("//*[contains(@class, 'row__2_')]");

        int numberOfObjects = getNumberOfElements(addedObject);
        checkTrue(isElementDisplayed(btnAddRelatedObject), "Add related object is not displayed");
        click(btnAddRelatedObject);
        checkEquals(getNumberOfElements(addedObject), numberOfObjects + 1, "New related object " +
                "dropdown is not created");
        return this;
    }

    @Override
    @Step
    public ObjectsTree addRelatedObjectTo(int objectNumber) {
        By btnAddRecord = By.xpath("(//*[contains(@class, 'row__2_')])[" + objectNumber + "]//*[contains(@class, 'selectedLeave__')]/button");
        By addedObject = By.xpath("//*[contains(@class, 'row__2_')]");

        int numberOfObjects = getNumberOfElements(addedObject);
        checkTrue(isElementDisplayed(btnAddRecord), "Add related object button to record #" + objectNumber + "not displayed");
        click(btnAddRecord);
        checkEquals(getNumberOfElements(addedObject), numberOfObjects + 1, "New related object " +
                "dropdown is not created");
        return this;
    }


    @Override
    @Step
    public ObjectsTree setRelatedObjectTo(int objectNumber, String relatedObjectName) {
        By selectRelatedObject = By.xpath("(//*[@class = 'Select-control'])[" + objectNumber + "]");
        By selectMenuOption = By.xpath("//*[contains(@class,'Select-option') and .='" + relatedObjectName + "']");

        checkTrue(isElementDisplayed(selectRelatedObject), "Object select #" + objectNumber + " is not displayed");
        click(selectRelatedObject);
        checkTrue(isElementPresentAndDisplayed(selectMenuOption, 5), relatedObjectName + " select option is not present");
        scrollTo(selectMenuOption);
        click(selectMenuOption);
        return this;
    }
}
