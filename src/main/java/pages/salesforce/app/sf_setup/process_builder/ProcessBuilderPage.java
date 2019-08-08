package pages.salesforce.app.sf_setup.process_builder;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import pages.salesforce.app.sf_setup.SetupSalesforcePage;
import pages.salesforce.app.sf_setup.process_builder.popups.DeleteProcessPopUp;
import pages.salesforce.app.sf_setup.process_builder.popups.NewProcessPopUp;
import utils.Logger;

import static core.check.Check.checkTrue;

public class ProcessBuilderPage extends SetupSalesforcePage {

    private By spinner = By.xpath("//div[contains(@class , 'processuicommonSpinner')]");
    private By title = By.xpath("//span[@class = 'text' and .='Process Builder']");

    public ProcessBuilderPage(WebDriver driver) {
        super(driver);
    }

    @Override
    public void isOpened() {
        switchToDefaultContent();
        checkTrue(waitUntilPageLoaded(), "Process builder page is not loaded");
        checkTrue(isElementPresentAndDisplayed(iframe, 90), "Vframe is not loaded");
        checkTrue(switchToFrame(iframe, 5), "Can not switch to Vframe");
        checkTrue(isElementPresentAndDisplayed(title, 60), "Title is not  displayed");
    }

    @Step
    public NewProcessPopUp addNewProcess() {
        By btnNew = By.xpath("//button[contains(@class, 'create')]");

        Logger.info("Add new process");
        checkTrue(isElementPresentAndDisplayed(btnNew, 5), "New process button is not displayed");
        checkTrue(isElementNotDisplayed(spinner, 20), "Spinner is still displayed");
        checkTrue(isElementNotDisplayed(spinner, 20), "Spinner is still displayed");
        checkTrue(isElementContainsStringInAttribute(spinner, "class", "hide", 15), "Spinner is not hide");
        click(btnNew);
        NewProcessPopUp newProcessPopUp = new NewProcessPopUp(driver);
        newProcessPopUp.isOpened();
        return newProcessPopUp;
    }

    @Step("Deactivate process {0}")
    public ProcessBuilderPage deactivateProcess(String processName) {
        By process = By.xpath("//td[@title='" + processName + "']//a[2]");
        By processStatus = By.xpath("//td[@title='" + processName + "']/parent::tr//td[@class='status']");

        Logger.info("Deactivate  process: " + processName);
        checkTrue(isElementPresentAndDisplayed(processStatus, 10), "Process status is not displayed");
        String processActiveStatus = getAttribute(processStatus, "title");
        if (processActiveStatus.equals("Active")) {
            checkTrue(isElementPresentAndDisplayed(process, 10), "process is not displayed");
            click(process);
            checkTrue(isElementNotDisplayed(process, 10), "Process is still displayed");
            ProcessBuilderEditorPage editorPage = new ProcessBuilderEditorPage(driver);
            editorPage.isOpened();
            editorPage.deactivateProcess();
            editorPage.backToProcessBuilder();
        }
        isOpened();
        Logger.info("Process " + processName + " is deactivated");
        return this;
    }

    @Step("Delete process {0}")
    public ProcessBuilderPage deleteProcess(String processName) {
        By process = By.xpath("//td[@title='" + processName + "']//a[1]");
        By btnDeleteProcess = By.xpath("//td[@title='" + processName + "']/parent::tr//a[.='Delete']");

        Logger.info("Delete process: " + processName);
        checkTrue(isElementPresentAndDisplayed(process, 10), "Process id not displayed");
        click(process);
        checkTrue(isElementPresentAndDisplayed(btnDeleteProcess, 10), "Delete process button is not displayed");
        click(btnDeleteProcess);
        DeleteProcessPopUp deleteProcessPopUp = new DeleteProcessPopUp(driver);
        return deleteProcessPopUp.confirmDelete();
    }
}
