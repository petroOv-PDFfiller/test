package pages.salesforce.app.DaDaDocs.full_app.pop_ups;

import data.salesforce.SalesforceTestData;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import pages.salesforce.SalesforceBasePage;
import pages.salesforce.app.DaDaDocs.link_to_fill.LinkToFillCustomizeTab;
import pages.salesforce.app.DaDaDocs.send_to_sign.SendToSignPage;
import pages.salesforce.app.SalesAppBasePage;

import java.util.List;

import static core.check.Check.checkTrue;

public class DocumentPackagesPopUp extends SalesAppBasePage {

    private By newNameField = By.xpath("//input[contains(@class,'popup__input-name')]");

    public DocumentPackagesPopUp(WebDriver driver) {
        super(driver);
    }

    @Override
    public void isOpened() {
        By selectHeader = By.xpath("//h3[text()='Select PDF Documents For Merging']");
        checkTrue(isElementPresent(selectHeader, 60), "Select files tab wasn't load");
    }

    @Step
    public void selectFilesByName(List<String> fileNames) {
        for (String fileName : fileNames) {
            By checkbox = By.xpath("(//*[@data-name='" + fileName + "']//input)]");
            checkTrue(isElementPresent(checkbox, 5), "Needful file " + fileName + " is not presented");
            click(checkbox);
        }
    }

    @Step
    public void selectNFirstFiles(int numberOfFiles) {
        for (int i = 1; i <= numberOfFiles; i++) {
            By checkbox = By.xpath("(//*[@data-name][descendant::p[text()='File']]//input)[" + i + "]");
            checkTrue(isElementPresent(checkbox, 5), "Needful checkbox is not presented");
            click(checkbox);
        }
    }

    @Step
    public <T extends SalesforceBasePage> T selectAnAction(String actionName) {
        SalesforceBasePage page = null;
        By actionLocator = By.xpath("//button[contains(@class,'multiple')][contains(.,'" + actionName + "')]");

        checkTrue(isElementDisplayed(actionLocator, 5), actionName + " action is not presented");
        click(actionLocator);

        switch (actionName) {
            case SalesforceTestData.DaDaDocsSelectFilesActions.SEND_TO_SIGN: {
                page = new SendToSignPage(driver);
                skipGlobalLoader();
                break;
            }
            case SalesforceTestData.DaDaDocsSelectFilesActions.LINK_TO_FILL: {
                page = new LinkToFillCustomizeTab(driver);
                break;
            }
            case SalesforceTestData.DaDaDocsSelectFilesActions.MERGE_PDFS: {
                page = this;
                break;
            }
        }
        skipLoader();

        page.isOpened();
        return (T) page;
    }

    @Step
    public void clickOkButton() {
        By btnOK = By.xpath("//button[text()='Ok']");
        checkTrue(isElementDisplayed(btnOK, 5), "Ok button is not displaying");
        click(btnOK);
    }

    @Step
    public String getInputName() {
        By newNameField = By.xpath("//input[contains(@class, 'name')]");
        checkTrue(isElementDisplayed(newNameField, 5), "New name field is not displaying");
        return getAttribute(newNameField, "value");
    }

    @Step
    public void enterNewFilename(String text) {
        checkTrue(isElementDisplayed(newNameField, 5), "Rename field is not displaying");
        type(newNameField, text);
    }

    @Step
    public String getNameOfFileWithOrderNumber(int number) {
        By file = By.xpath("(//*[@data-name][descendant::p[text()='File']])[" + number + "]");
        checkTrue(isElementPresent(file, 5), number + " file is not present");
        return getAttribute(file, "data-name");
    }
}
