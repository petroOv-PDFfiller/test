package pages.salesforce.app.DaDaDocs.editor;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import pages.pdffiller.editors.IDocument;
import pages.pdffiller.editors.newJSF.NewJSFiller;
import pages.pdffiller.editors.newJSF.constructor.ConstructorJSFiller;
import pages.pdffiller.editors.newJSF.constructor.enums.ConstructorTool;
import pages.salesforce.SalesforceBasePage;
import ru.yandex.qatools.ashot.Screenshot;
import utils.AShotMan;
import utils.Logger;
import utils.TimeMan;

import static core.check.Check.checkTrue;

public class DaDaDocsEditor extends SalesforceBasePage {

    public NewJSFiller newJSFiller = null;
    private IDocument<ConstructorJSFiller, ConstructorTool> document;
    private int pageWidth;
    private int pageHeight;

    public DaDaDocsEditor(WebDriver driver) {
        super(driver);
    }

    @Override
    public void isOpened() {
        skipGlobalLoader();
        driver.switchTo().defaultContent();
        checkTrue(waitUntilPageLoaded(), "Page isn`t loaded");
        if (isElementPresent(iframe)) {
            checkTrue(isElementPresentAndDisplayed(iframe, 30), "DaDaDocs editor was not loaded");
            checkTrue(switchToFrame(iframe), "cannot switch to vframe");
        }
        checkTrue(isElementDisappeared(vMappingLoader, 60), "Mapping loader is still present");
        TimeMan.sleep(1);
        checkTrue(isElementNotDisplayed(orangeLoader, 20), "Vframe loader isn`t disappeared");
        TimeMan.sleep(1);
        checkTrue(isElementPresentAndDisplayed(editorFrame, 30), "DaDaDocs editor was not loaded");
        checkTrue(switchToFrame(editorFrame), "cannot switch to editor frame");
        newJSFiller = new NewJSFiller(driver);
        newJSFiller.isOpened();
        newJSFiller.isReady();
        newJSFiller.closeModalsIfExists();
    }

    @Step
    public int getNumberOfFillableFields() {
        return newJSFiller.getFillableFieldsTotalSum();
    }

    @Step
    public void addFillableField() {
        document = newJSFiller.openConstructor();
        //newJSFiller.selectZoom("Fit Page");//doesn't work without zoom
        pageHeight = (int) (newJSFiller.getPageHeight(1));
        pageWidth = (int) (newJSFiller.getPageWidth(1));

        document.putContent(ConstructorTool.TEXT, pageHeight / 8, pageWidth / 18);

        click(By.xpath("//body"));
    }

    @Step
    public Screenshot takeScreenShotEditor() {
        By editor = By.xpath("//*[@class='pagePinch-PagePinch']");
        AShotMan aShot = new AShotMan(driver);
        checkTrue(isElementPresent(editor, 4), "Editor is not present");
        WebElement element = driver.findElement(editor);

        return aShot.takeScreenshotWithOutJquery(element);
    }

    @Step
    public void clickSaveButton() {
        By btnSave = By.xpath("//button[contains(.,'SAVE')]");
        checkTrue(isElementDisplayed(btnSave, 5), "Save button is not presented");
        click(btnSave);
    }

    public void clickDoneButton() {
        By doneButton = By.xpath("//*[@id='DoneButton']//span[contains(text(),'DONE')]");
        checkTrue(isElementPresent(doneButton, 8), "Done button is not present");
        clickJS(doneButton);
        Logger.info("Click on Done button");
        driver.switchTo().parentFrame();
        checkTrue(isElementDisappeared(orangeLoader, 15), "Orange loader is still present");
    }

    @Step
    public void fitPage() {
        By btnFit = By.xpath("//span[contains(text(),'Fit Width')]");
        By btnFitPage = By.xpath("//span[contains(text(),'Fit Page')]");
        checkTrue(isElementDisplayed(btnFit, 5), "Fit Button is not presented");
        hoverOverAndClick(btnFit);
        checkTrue(isElementDisplayed(btnFitPage, 10), "Fit Page Button is not presented");
        hoverOverAndClick(btnFitPage);
    }
}
