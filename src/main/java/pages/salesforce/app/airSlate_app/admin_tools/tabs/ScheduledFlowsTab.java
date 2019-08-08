package pages.salesforce.app.airSlate_app.admin_tools.tabs;

import core.check.SoftCheck;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import pages.salesforce.app.airSlate_app.admin_tools.ASAppAdminToolsPage;
import pages.salesforce.app.airSlate_app.admin_tools.entities.ScheduleFlow;
import pages.salesforce.app.airSlate_app.admin_tools.popups.DeleteScheduleFlowPopUp;
import pages.salesforce.app.airSlate_app.admin_tools.wizards.scheduled_flow.ScheduleInfoWizardPage;
import utils.Logger;

import java.util.ArrayList;
import java.util.List;

import static core.check.Check.checkEquals;
import static core.check.Check.checkTrue;
import static pages.salesforce.enums.admin_tools.ASAppAdminToolTabs.SCHEDULED_FLOWS;

public class ScheduledFlowsTab extends ASAppAdminToolsPage {

    private By scheduleFlow = By.xpath("//*[@data-test = 'schedule']");

    public ScheduledFlowsTab(WebDriver driver) {
        super(driver);
    }

    @Override
    public void isOpened() {
        skipAdminToolLoader(20);
        checkEquals(getActiveTabName(), SCHEDULED_FLOWS.getName(), "Scheduled flows tab is not opened");
    }

    @Step
    public List<ScheduleFlow> getAllFlows() {
        List<WebElement> scheduleFlowElements = driver.findElements(scheduleFlow);
        List<ScheduleFlow> scheduleFlow = new ArrayList<>();
        scheduleFlowElements.forEach(element -> scheduleFlow.add(getScheduleFlow(element)));

        return scheduleFlow;
    }

    @Step
    public boolean isFlowPresent(String flowName) {
        By flow = By.xpath("//*[@data-test='schedule']//*[contains(@class, 'nameCol') and @title='" + flowName + "']");
        List<ScheduleFlow> flows = getAllFlows();
        for (int i = 0; i < 5; i++) {
            scrollToFlow(flows.get(flows.size() - 1).getName());
            if (flows.equals(getAllFlows())) {
                break;
            } else {
                flows = getAllFlows();
            }
        }
        return isElementPresent(flow);
    }

    @Step
    private void scrollToFlow(String flowName) {
        By flow = By.xpath("//*[@data-test='schedule']//*[contains(@class, 'nameCol') and @title='" + flowName + "']");

        checkTrue(isElementPresent(flow, 4), flowName + " flow ");
        scrollTo(flow);
        skipAdminToolLoader();
    }

    @Step
    public ScheduledFlowsTab sortBy(String field, String order) {
        By columnHeader = By.xpath("//*[contains(@class, 'tableHeader__')]//*[text()='" + field + "']");

        Logger.info("Sort schedule flows by " + field + " in " + order + " order");
        checkIsElementDisplayed(columnHeader, 2, field + " table header");
        click(columnHeader);
        if (!isElementContainsStringInClass(columnHeader, order)) {
            click(columnHeader);
        }
        skipAdminToolLoader();
        checkTrue(isElementContainsStringInClass(columnHeader, order), "Custom button sort attribute is not added");
        return this;
    }

    private ScheduleFlow getScheduleFlow(WebElement element) {
        By nameColumn = By.xpath(".//*[contains(@class, 'nameCol__')]");
        By flowColumn = By.xpath(".//*[contains(@class, 'flowCol__')]");
        By timeColumn = By.xpath(".//*[contains(@class, 'timeCol__')]");

        SoftCheck softCheck = new SoftCheck();
        softCheck.checkTrue(element.findElements(nameColumn).size() > 0, "name column is not present");
        softCheck.checkTrue(element.findElements(flowColumn).size() > 0, "flow column is not present");
        softCheck.checkTrue(element.findElements(timeColumn).size() > 0, "time column is not present");
        softCheck.checkAll();

        String name = element.findElement(nameColumn).getAttribute("title");
        String flow = element.findElement(flowColumn).getAttribute("title");
        String date = element.findElement(timeColumn).getAttribute("title");
        return new ScheduleFlow(name, flow, date);
    }

    @Step
    public ScheduleInfoWizardPage createSchedule() {
        By btnCreateSchedule = By.xpath("//button[.='Create Schedule']");

        checkIsElementDisplayed(btnCreateSchedule, 4, "Create schedule button");
        click(btnCreateSchedule);
        skipAdminToolLoader(60);
        return initExpectedPage(ScheduleInfoWizardPage.class);
    }

    @Step
    public DeleteScheduleFlowPopUp deleteFlow(String scheduleFlowName) {
        By btnAction = By.xpath("//*[@title = '" + scheduleFlowName + "']/ancestor::*[@data-test='schedule']//div[contains(@class, 'dropdown__')]");
        By btnDeleteSchedule = By.xpath("//button[.='Remove schedule']");

        checkTrue(isFlowPresent(scheduleFlowName), scheduleFlowName + " flow is not present");
        checkIsElementDisplayed(btnAction, 5, "Three dots button for " + scheduleFlowName);
        click(btnAction);
        checkIsElementDisplayed(btnDeleteSchedule, 3, "Remove schedule button");
        click(btnDeleteSchedule);
        return initExpectedPage(DeleteScheduleFlowPopUp.class);
    }

    @Step
    public ScheduleInfoWizardPage editFlow(String scheduleFlowName) {
        By btnAction = By.xpath("//*[@title = '" + scheduleFlowName + "']/ancestor::*[@data-test='schedule']//div[contains(@class, 'dropdown__')]");
        By btnEditSchedule = By.xpath("//button[.='Edit schedule']");

        checkTrue(isFlowPresent(scheduleFlowName), scheduleFlowName + " flow is not present");
        checkIsElementDisplayed(btnAction, 5, "Three dots button for " + scheduleFlowName);
        click(btnAction);
        checkIsElementDisplayed(btnEditSchedule, 3, "Remove schedule button");
        click(btnEditSchedule);
        return initExpectedPage(ScheduleInfoWizardPage.class);
    }
}
